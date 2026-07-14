package pro.sketchware.activities.ai;

import android.util.Log;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AINetworkClient {

    private static final String TAG = "AINetworkClient";
    private OkHttpClient client;
    private String apiKey;
    private String baseUrl;

    public AINetworkClient(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public void sendPrompt(String prompt, AICallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "gpt-3.5-turbo"); // يمكن تغيير النموذج حسب الحاجة
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);
            jsonBody.put("messages", new org.json.JSONArray().put(message));
            jsonBody.put("max_tokens", 1000);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON body", e);
            callback.onFailure(e);
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(baseUrl + "/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "API call failed", e);
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "API response: " + responseBody);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String content = jsonResponse.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        callback.onSuccess(content);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing API response", e);
                        callback.onFailure(e);
                    }
                } else {
                    String errorBody = response.body().string();
                    Log.e(TAG, "API call unsuccessful: " + response.code() + " - " + errorBody);
                    callback.onFailure(new IOException("Unsuccessful response: " + response.code() + " - " + errorBody));
                }
            }
        });
    }

    public interface AICallback {
        void onSuccess(String response);
        void onFailure(Exception e);
    }
}
