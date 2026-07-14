package pro.sketchware.activities.ai;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mod.hilal.saif.activities.tools.ConfigActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pro.sketchware.R;

public class AIChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText messageInput;
    private Button sendButton;
    private ChatAdapter adapter;
    private List<ChatMessage> messages = new ArrayList<>();
    private OkHttpClient client = new OkHttpClient();
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);

        recyclerView = findViewById(R.id.chat_recycler_view);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);

        adapter = new ChatAdapter(messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        sendButton.setOnClickListener(v -> {
            String text = messageInput.getText().toString().trim();
            if (!text.isEmpty()) {
                sendMessage(text);
            }
        });
        
        addBotMessage("مرحباً بك في Sketchware X AI! أنا هنا لمساعدتك في إنشاء تطبيقاتك. أخبرني ماذا تريد أن أبني لك؟");
    }

    private void sendMessage(String text) {
        messages.add(new ChatMessage(text, true));
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
        messageInput.setText("");

        String apiKey = ConfigActivity.getStringSettingValueOrSetAndGet(ConfigActivity.SETTING_AI_API_KEY, "");
        String apiBase = ConfigActivity.getStringSettingValueOrSetAndGet(ConfigActivity.SETTING_AI_API_BASE, "https://api.openai.com/v1");
        String model = ConfigActivity.getStringSettingValueOrSetAndGet(ConfigActivity.SETTING_AI_MODEL, "gpt-4-turbo");

        if (apiKey.isEmpty()) {
            addBotMessage("يرجى إعداد مفتاح الـ API في إعدادات التطبيق أولاً لتتمكن من استخدام الذكاء الاصطناعي.");
            return;
        }

        // توجيه الذكاء الاصطناعي لإنشاء كود متوافق مع سكتشوير
        String systemPrompt = "You are a Sketchware Pro expert. When a user asks to create an app, provide logic, components, and XML structures that can be used in Sketchware. If possible, provide the response in a structured way.";
        
        AIRequest aiRequest = new AIRequest(model, systemPrompt, text);
        String json = gson.toJson(aiRequest);

        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(apiBase + (apiBase.endsWith("/") ? "" : "/") + "chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> addBotMessage("حدث خطأ في الاتصال: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    try {
                        AIResponse aiResponse = gson.fromJson(responseData, AIResponse.class);
                        if (aiResponse != null && aiResponse.choices != null && !aiResponse.choices.isEmpty()) {
                            String botReply = aiResponse.choices.get(0).message.content;
                            runOnUiThread(() -> {
                                addBotMessage(botReply);
                                // هنا يمكن إضافة منطق لتحليل الرد وتحويله لمشروع سكتشوير فعلي
                                if (botReply.contains("<sketchware_project>")) {
                                    // منطق مستقبلي لإنشاء المشروع تلقائياً
                                }
                            });
                        }
                    } catch (Exception e) {
                        runOnUiThread(() -> addBotMessage("خطأ في تحليل الرد: " + e.getMessage()));
                    }
                } else {
                    runOnUiThread(() -> addBotMessage("خطأ من الخادم (رمز الحالة: " + response.code() + ")"));
                }
            }
        });
    }

    private void addBotMessage(String text) {
        messages.add(new ChatMessage(text, false));
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
    }

    public static class ChatMessage {
        String text;
        boolean isUser;
        ChatMessage(String text, boolean isUser) { this.text = text; this.isUser = isUser; }
    }

    public static class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
        List<ChatMessage> messages;
        ChatAdapter(List<ChatMessage> messages) { this.messages = messages; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ChatMessage msg = messages.get(position);
            holder.textView.setText((msg.isUser ? "أنت: " : "Sketchware X AI: ") + msg.text);
            holder.textView.setTextAlignment(msg.isUser ? View.TEXT_ALIGNMENT_VIEW_END : View.TEXT_ALIGNMENT_VIEW_START);
        }

        @Override
        public int getItemCount() { return messages.size(); }
        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            ViewHolder(View v) { super(v); textView = v.findViewById(android.R.id.text1); }
        }
    }

    static class AIRequest {
        String model;
        List<Message> messages = new ArrayList<>();
        AIRequest(String model, String system, String user) {
            this.model = model;
            this.messages.add(new Message("system", system));
            this.messages.add(new Message("user", user));
        }
        static class Message {
            String role, content;
            Message(String r, String c) { role = r; content = c; }
        }
    }

    static class AIResponse {
        List<Choice> choices;
        static class Choice { Message message; }
        static class Message { String content; }
    }
}
