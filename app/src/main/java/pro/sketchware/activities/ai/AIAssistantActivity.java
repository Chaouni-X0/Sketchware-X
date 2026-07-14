package pro.sketchware.activities.ai;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import a.a.a.lC;
import a.a.a.yB;
import pro.sketchware.R;
import com.besome.sketch.projects.MyProjectSettingActivity;

public class AIAssistantActivity extends AppCompatActivity {
    private EditText input;
    private FloatingActionButton send;
    private TextView transcript;
    private Chip providerChip;
    private Chip projectChip;
    private Chip createAppChip;
    private AIProviderStore.Provider provider;
    private HashMap<String, Object> project;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.ai_assistant_activity);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> drawer.open());

        input = findViewById(R.id.ai_input);
        send = findViewById(R.id.send_button);
        transcript = findViewById(R.id.chat_transcript);
        providerChip = findViewById(R.id.provider_chip);
        projectChip = findViewById(R.id.project_chip);
        createAppChip = findViewById(R.id.create_app_chip);
        ImageButton selectProject = findViewById(R.id.btn_select_project);
        ImageButton attach = findViewById(R.id.btn_attach);

        providerChip.setOnClickListener(v -> chooseProvider());
        projectChip.setOnClickListener(v -> chooseProject());
        selectProject.setOnClickListener(v -> chooseProject());
        attach.setOnClickListener(v -> Toast.makeText(this,
                "Project context is attached using the project button", Toast.LENGTH_SHORT).show());
        send.setOnClickListener(v -> sendMessage());
        refreshProvider();
    }

    @Override protected void onResume() {
        super.onResume();
        refreshProvider();
    }

    private void refreshProvider() {
        provider = AIProviderStore.getActive(this);
        providerChip.setText(provider == null ? "Add API" : provider.toString());
    }

    private void chooseProvider() {
        List<AIProviderStore.Provider> providers = AIProviderStore.getAll(this);
        List<String> labels = new ArrayList<>();
        for (AIProviderStore.Provider item : providers) labels.add(item.toString());
        labels.add("＋ Add provider");
        new MaterialAlertDialogBuilder(this).setTitle("AI provider")
                .setItems(labels.toArray(new String[0]), (d, which) -> {
                    if (which == providers.size()) {
                        startActivity(new Intent(this, AIProvidersActivity.class));
                    } else {
                        AIProviderStore.setActive(this, providers.get(which).id);
                        refreshProvider();
                    }
                }).show();
    }

    private void chooseProject() {
        List<HashMap<String, Object>> projects = lC.a();
        if (projects.isEmpty()) {
            Toast.makeText(this, "No Sketchware X projects found", Toast.LENGTH_LONG).show();
            return;
        }
        String[] names = new String[projects.size()];
        for (int i = 0; i < projects.size(); i++) {
            String name = yB.c(projects.get(i), "my_app_name");
            names[i] = name.isEmpty() ? yB.c(projects.get(i), "my_ws_name") : name;
        }
        new MaterialAlertDialogBuilder(this).setTitle("Choose project")
                .setItems(names, (d, which) -> {
                    project = projects.get(which);
                    projectChip.setText(names[which]);
                }).show();
    }

    private void sendMessage() {
        String message = input.getText().toString().trim();
        if (message.isEmpty()) return;
        if (provider == null) {
            chooseProvider();
            return;
        }
        String key = AIProviderStore.getKey(this, provider);
        if (key.isEmpty()) {
            Toast.makeText(this, "The selected API key is unavailable", Toast.LENGTH_LONG).show();
            return;
        }

        String prompt = buildPrompt(message);
        transcript.append("\n\nYou\n" + message + "\n\nSketchware X AI\nThinking…");
        input.setText("");
        send.setEnabled(false);
        new AINetworkClient(key, provider.url, provider.model).sendPrompt(prompt,
                new AINetworkClient.AICallback() {
                    @Override public void onSuccess(String response) {
                        runOnUiThread(() -> {
                            if (createAppChip.isChecked()) handleBlueprint(response);
                            else replaceThinking(response);
                            send.setEnabled(true);
                        });
                    }
                    @Override public void onFailure(Exception error) {
                        runOnUiThread(() -> {
                            replaceThinking("Error: " + error.getMessage());
                            send.setEnabled(true);
                        });
                    }
                });
    }

    private String buildPrompt(String request) {
        StringBuilder prompt = new StringBuilder("You are the Sketchware X coding assistant. ")
                .append("Give practical Android/Java/Kotlin guidance. Never claim files were changed unless a change plan is explicitly approved.\n");
        if (createAppChip.isChecked()) {
            prompt.append("CREATE APP MODE. Return only one JSON object. If details are missing use: ")
                    .append("{\"status\":\"needs_input\",\"questions\":[\"question\"]}. ")
                    .append("If complete use: {\"status\":\"ready\",\"name\":\"App\",\"package\":\"com.example.app\",")
                    .append("\"language\":\"java|kotlin\",\"screens\":[],\"components\":[],\"dependencies\":[],\"build_options\":[]}. ")
                    .append("Never request or include passwords, private keys, tokens, or API key values. Ask only which service is needed.\n");
        }
        if (project != null) {
            prompt.append("Selected project metadata: sc_id=").append(yB.c(project, "sc_id"))
                    .append(", app=").append(yB.c(project, "my_app_name"))
                    .append(", package=").append(yB.c(project, "my_sc_pkg_name"))
                    .append(", version=").append(yB.c(project, "sc_ver_name")).append(".\n")
                    .append("When changes are requested, explain the files and blocks to change before applying anything.\n");
        }
        return prompt.append("User request: ").append(request).toString();
    }

    private void handleBlueprint(String response) {
        try {
            AIAppBlueprint blueprint = AIAppBlueprint.parse(response);
            if (!blueprint.needsInput()) {
                AIAppBlueprintStore.save(this, blueprint);
                showBlueprintApproval(blueprint);
            }
            replaceThinking(blueprint.displayText());
        } catch (Exception error) {
            replaceThinking("Invalid app blueprint: " + error.getMessage());
        }
    }

    private void showBlueprintApproval(AIAppBlueprint blueprint) {
        String summary = "Name: " + blueprint.name() + "\nPackage: " + blueprint.packageName()
                + "\nLanguage: " + blueprint.language()
                + "\n\nSketchware X will open its project creator with these values. You can review them before saving.";
        new MaterialAlertDialogBuilder(this)
                .setTitle("Create this app?")
                .setMessage(summary)
                .setNegativeButton("Not now", null)
                .setPositiveButton("Review & create", (dialog, which) -> openProjectCreator(blueprint))
                .show();
    }

    private void openProjectCreator(AIAppBlueprint blueprint) {
        Intent intent = new Intent(this, MyProjectSettingActivity.class);
        intent.putExtra("my_app_name", blueprint.name());
        intent.putExtra("my_ws_name", blueprint.name().replaceAll("[^A-Za-z0-9_]", ""));
        intent.putExtra("my_sc_pkg_name", blueprint.packageName());
        intent.putExtra("sc_ver_code", "1");
        intent.putExtra("sc_ver_name", "1.0");
        intent.putExtra("ai_blueprint", blueprint.data.toString());
        intent.putExtra("preferred_language", blueprint.language());
        startActivity(intent);
    }

    private void replaceThinking(String response) {
        String value = transcript.getText().toString();
        int marker = value.lastIndexOf("Thinking…");
        transcript.setText(marker < 0 ? value + "\n" + response : value.substring(0, marker) + response);
    }
}
