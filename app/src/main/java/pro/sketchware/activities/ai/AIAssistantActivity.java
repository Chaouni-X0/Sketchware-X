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

public class AIAssistantActivity extends AppCompatActivity {

    private EditText aiInput;
    private FloatingActionButton sendButton;
    private RecyclerView chatRecyclerView;
    private DrawerLayout drawerLayout;
    private ImageButton btnAttach;
    private ImageButton btnSelectProject;
    private a.a.a.DB db;
    private AIProjectEngine engine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        engine = new AIProjectEngine(this);
        db = new a.a.a.DB(this, "AI_CONFIG");
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
        
        // هنا سيتم استدعاء API الذكاء الاصطناعي (مثل OpenAI أو Gemini)
        // في الوقت الحالي، سنقوم بمحاكاة استجابة أكثر تعقيداً تعتمد على الـ prompt
        new android.os.Handler().postDelayed(() -> {
            String mockResponse = generateMockResponseBasedOnPrompt(prompt);
            // سيتم استبدال المسار بمسار المشروع الحالي
            engine.processAIResponse(mockResponse, "/sdcard/.sketchware/data/last_project");
            Toast.makeText(this, "تم إنشاء المشروع بنجاح! يمكنك الآن فتحه وتعديله.", Toast.LENGTH_LONG).show();
        }, 3000);
    }

    private String generateMockResponseBasedOnPrompt(String prompt) {
        // هذا مجرد محاكاة بسيطة. في التطبيق الفعلي، سيعود هذا من الـ API.
        if (prompt.toLowerCase().contains("تسجيل الدخول") || prompt.toLowerCase().contains("login")) {
            return "{\"views\": [{\"id\": \"et_email\", \"type\": \"EditText\", \"text\": \"البريد الإلكتروني\", \"attributes\": {\"layout_width\": \"match_parent\", \"margin_bottom\": \"16dp\"}}, {\"id\": \"et_password\", \"type\": \"EditText\", \"text\": \"كلمة المرور\", \"attributes\": {\"layout_width\": \"match_parent\", \"margin_bottom\": \"16dp\"}}, {\"id\": \"btn_login\", \"type\": \"Button\", \"text\": \"تسجيل الدخول\", \"attributes\": {\"layout_width\": \"match_parent\", \"background_color\": \"#FF6200EE\", \"text_color\": \"#FFFFFF\"}}], \"logic\": [{\"event\": \"onClick\", \"blocks\": [{\"type\": \"toast\", \"spec\": \"جاري تسجيل الدخول...\"}]}]}";
        } else {
            return "{\"views\": [{\"id\": \"tv_welcome\", \"type\": \"TextView\", \"text\": \"مرحباً بك في سكتشوير إكس\", \"attributes\": {\"layout_width\": \"wrap_content\", \"text_color\": \"#FF000000\"}}, {\"id\": \"btn_action\", \"type\": \"Button\", \"text\": \"اضغط هنا\", \"attributes\": {\"layout_width\": \"match_parent\", \"margin_top\": \"16dp\"}}], \"logic\": [{\"event\": \"onClick\", \"blocks\": [{\"type\": \"toast\", \"spec\": \"تم الضغط على الزر!\"}]}]}";
        }
    }
}
