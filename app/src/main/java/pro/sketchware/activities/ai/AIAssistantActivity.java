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

public class AIAssistantActivity extends AppCompatActivity {

    private EditText aiInput;
    private FloatingActionButton sendButton;
    private RecyclerView chatRecyclerView;
    private a.a.a.DB db;
    private AIProjectEngine engine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        engine = new AIProjectEngine(this);
        db = new a.a.a.DB(this, "AI_CONFIG");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ai_assistant_activity);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        aiInput = findViewById(R.id.ai_input);
        sendButton = findViewById(R.id.send_button);
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
    }

    private void handleUserPrompt(String prompt) {
        String apiKey = db.a("api_key", "");
        if (apiKey.isEmpty()) {
            Toast.makeText(this, "يرجى إعداد مفتاح الـ API أولاً من الإعدادات", Toast.LENGTH_LONG).show();
            return;
        }

        // سيتم دمج الـ API الفعلي هنا لإرسال الـ prompt واستلام هيكل المشروع
        Toast.makeText(this, "جاري التواصل مع الذكاء الاصطناعي لإنشاء مشروعك...", Toast.LENGTH_LONG).show();
        
        // محاكاة لعملية الإنشاء الفعلية باستخدام المحرك
        new android.os.Handler().postDelayed(() -> {
            String mockResponse = "{\"views\": [{\"id\": \"btn_hello\", \"type\": \"Button\", \"text\": \"مرحباً بك في سكتشوير إكس\"}], \"logic\": [{\"event\": \"onClick\", \"blocks\": [{\"type\": \"toast\", \"spec\": \"مرحباً محمد الشاوني\"}]}]}";
            // سيتم استبدال المسار بمسار المشروع الحالي
            engine.processAIResponse(mockResponse, "/sdcard/.sketchware/data/last_project");
            Toast.makeText(this, "تم إنشاء المشروع بنجاح! يمكنك الآن فتحه وتعديله.", Toast.LENGTH_LONG).show();
        }, 3000);
    }
}
