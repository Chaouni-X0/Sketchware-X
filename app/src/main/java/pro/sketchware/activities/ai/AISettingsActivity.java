package pro.sketchware.activities.ai;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import pro.sketchware.R;
import pro.sketchware.security.SecureKeyVault;

public class AISettingsActivity extends AppCompatActivity {

    private EditText apiKeyInput;
    private EditText agentCodeInput;
    private MaterialButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ai_settings_activity);

        // استخدام SharedPreferences لسهولة الوصول من AIChatActivity
        android.content.SharedPreferences prefs = getSharedPreferences("AISettings", MODE_PRIVATE);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.ai_settings_title);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        apiKeyInput = findViewById(R.id.api_key_input);
        agentCodeInput = findViewById(R.id.agent_code_input); // سيتم استخدامه كـ Base URL
        saveButton = findViewById(R.id.save_button);

        // Keep endpoints in preferences, but credentials only in Android Keystore.
        apiKeyInput.setText(SecureKeyVault.get(this, "ai_api_key"));
        agentCodeInput.setText(prefs.getString("base_url", "https://api.openai.com"));

        saveButton.setOnClickListener(v -> {
            String apiKey = apiKeyInput.getText().toString().trim();
            String baseUrl = agentCodeInput.getText().toString().trim();

            if (baseUrl.isEmpty()) baseUrl = "https://api.openai.com";

            try {
                if (apiKey.isEmpty()) SecureKeyVault.remove(this, "ai_api_key");
                else SecureKeyVault.put(this, "ai_api_key", apiKey);
                prefs.edit().remove("api_key").putString("base_url", baseUrl).apply();
                Toast.makeText(this, R.string.ai_save_config, Toast.LENGTH_SHORT).show();
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "تعذر تشفير مفتاح API", Toast.LENGTH_LONG).show();
            }
        });
    }
}
