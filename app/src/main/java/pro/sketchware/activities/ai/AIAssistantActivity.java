package pro.sketchware.activities.ai;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import pro.sketchware.R;

import androidx.drawerlayout.widget.DrawerLayout;
import android.widget.ImageButton;
import pro.sketchware.activities.ai.AINetworkClient;
import pro.sketchware.activities.ai.AINetworkClient.AICallback;
import android.os.Handler;
import android.os.Looper;

public class AIAssistantActivity extends AppCompatActivity {

    private EditText aiInput;
    private FloatingActionButton sendButton;
    private RecyclerView chatRecyclerView;
    private DrawerLayout drawerLayout;
    private ImageButton btnAttach;
    private ImageButton btnSelectProject;
    private a.a.a.DB db;
    private AIProjectEngine engine;
    private AINetworkClient aiNetworkClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        engine = new AIProjectEngine(this);
        db = new a.a.a.DB(this, "AI_CONFIG");
        String apiKey = db.a("api_key", "");
        String baseUrl = db.a("api_base_url", "https://api.openai.com"); // Default to OpenAI
        aiNetworkClient = new AINetworkClient(apiKey, baseUrl);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ai_assistant_activity);

        drawerLayout = findViewById(R.id.drawer_layout);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> drawerLayout.open());

        aiInput = findViewById(R.id.ai_input);
        sendButton = findViewById(R.id.send_button);
        btnAttach = findViewById(R.id.btn_attach);
        btnSelectProject = findViewById(R.id.btn_select_project);
        chatRecyclerView = findViewById(R.id.chat_recycler_view);

        sendButton.setOnClickListener(v -> {
            String prompt = aiInput.getText().toString().trim();
            if (!prompt.isEmpty()) {
                handleUserPrompt(prompt);
                aiInput.setText("");
            } else {
                Toast.makeText(this, "يرجى كتابة وصف التطبيق أولاً", Toast.LENGTH_SHORT).show();
            }
        });

        btnAttach.setOnClickListener(v -> {
            // منطق اختيار الملفات
            Toast.makeText(this, "ميزة رفع الملفات قيد التطوير", Toast.LENGTH_SHORT).show();
        });

        btnSelectProject.setOnClickListener(v -> {
            // منطق اختيار المشروع الحالي
            Toast.makeText(this, "جاري جلب قائمة مشاريعك...", Toast.LENGTH_SHORT).show();
        });
    }

    private void handleUserPrompt(String prompt) {
        String apiKey = db.a("api_key", "");
        if (apiKey.isEmpty()) {
            Toast.makeText(this, "يرجى إعداد مفتاح الـ API أولاً من الإعدادات", Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(this, "جاري التواصل مع الذكاء الاصطناعي لإنشاء مشروعك...", Toast.LENGTH_LONG).show();
        
        aiNetworkClient.sendPrompt(prompt, new AICallback() {
            @Override
            public void onSuccess(String response) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    // سيتم استبدال المسار بمسار المشروع الحالي
                    engine.processAIResponse(response, "/sdcard/.sketchware/data/last_project");
                    Toast.makeText(AIAssistantActivity.this, "تم إنشاء المشروع بنجاح! يمكنك الآن فتحه وتعديله.", Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onFailure(Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(AIAssistantActivity.this, "فشل التواصل مع الذكاء الاصطناعي: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                });
            }
        });
    }
}
