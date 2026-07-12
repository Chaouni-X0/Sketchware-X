package pro.sketchware.activities.ai;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import a.a.a.DB;
import pro.sketchware.R;

public class AISettingsActivity extends AppCompatActivity {

    private EditText apiKeyInput;
    private EditText agentCodeInput;
    private MaterialButton saveButton;
    private DB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ai_settings_activity);

        db = new DB(this, "AI_CONFIG");

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        apiKeyInput = findViewById(R.id.api_key_input);
        agentCodeInput = findViewById(R.id.agent_code_input);
        saveButton = findViewById(R.id.save_button);

        // تحميل الإعدادات المحفوظة
        apiKeyInput.setText(db.a("api_key", ""));
        agentCodeInput.setText(db.a("agent_code", ""));

        saveButton.setOnClickListener(v -> {
            String apiKey = apiKeyInput.getText().toString().trim();
            String agentCode = agentCodeInput.getText().toString().trim();

            db.a("api_key", apiKey);
            db.a("agent_code", agentCode);

            Toast.makeText(this, "تم حفظ الإعدادات بنجاح", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
