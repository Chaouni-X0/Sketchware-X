package pro.sketchware.activities.ai;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pro.sketchware.R;
import android.content.SharedPreferences;
import pro.sketchware.security.SecureKeyVault;

public class AIChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText messageInput;
    private Button sendButton;
    private LinearProgressIndicator progressBar;
    private AINetworkClient aiClient;
    private AIProjectEngine projectEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);

        // تعريب العنوان
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.ai_chat_title);
        }

        recyclerView = findViewById(R.id.chat_recycler_view);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);
        progressBar = findViewById(R.id.progress_bar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // استرداد إعدادات API المخصصة من المستخدم
        SharedPreferences prefs = getSharedPreferences("AISettings", MODE_PRIVATE);
        String apiKey = SecureKeyVault.get(this, "ai_api_key");
        String baseUrl = prefs.getString("base_url", "https://api.openai.com");
        
        aiClient = new AINetworkClient(apiKey, baseUrl);
        projectEngine = new AIProjectEngine(this);

        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString();
            if (apiKey.isEmpty()) {
                Toast.makeText(this, "يرجى إضافة مفتاح API في الإعدادات أولاً", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!message.isEmpty()) {
                sendMessage(message);
                messageInput.setText("");
            }
        });
    }

    private void sendMessage(String message) {
        progressBar.setVisibility(View.VISIBLE);
        sendButton.setEnabled(false);
        
        aiClient.sendPrompt(message, new AINetworkClient.AICallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    sendButton.setEnabled(true);
                    // معالجة الاستجابة لإنشاء المشروع إذا كان يحتوي على تعليمات برمجية
                    projectEngine.processAIResponse(response, "current_project_path");
                    Toast.makeText(AIChatActivity.this, "تمت معالجة طلبك بنجاح", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    sendButton.setEnabled(true);
                    Toast.makeText(AIChatActivity.this, "فشل الاتصال: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
