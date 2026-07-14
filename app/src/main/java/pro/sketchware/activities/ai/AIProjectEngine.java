package pro.sketchware.activities.ai;

import android.content.Context;
import com.google.gson.Gson;
import java.util.Map;
import a.a.a.yq;

public class AIProjectEngine {

    private Context context;
    private Gson gson;

    public AIProjectEngine(Context context) {
        this.context = context;
        this.gson = new Gson();
    }

    public void processAIResponse(String jsonResponse, String projectPath) {
        try {
            // Simplified for build stability
            yq fileUtil = new yq();
            fileUtil.a(projectPath + "/ai_response.json", jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
