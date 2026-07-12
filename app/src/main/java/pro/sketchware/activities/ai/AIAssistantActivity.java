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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                Toast.show(this, "يرجى كتابة وصف التطبيق أولاً", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleUserPrompt(String prompt) {
        // سيتم دمج الـ API الخاص بالذكاء الاصطناعي هنا لاحقاً
        // في الوقت الحالي سنعرض رسالة تؤكد استلام الطلب
        Toast.makeText(this, "جاري تحليل طلبك وإنشاء المشروع...", Toast.LENGTH_LONG).show();
    }
}
