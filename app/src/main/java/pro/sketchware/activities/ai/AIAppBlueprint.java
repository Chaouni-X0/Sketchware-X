package pro.sketchware.activities.ai;

import org.json.JSONArray;
import org.json.JSONObject;

public final class AIAppBlueprint {
    public final JSONObject data;

    private AIAppBlueprint(JSONObject data) { this.data = data; }

    public static AIAppBlueprint parse(String response) throws Exception {
        int start = response.indexOf('{');
        int end = response.lastIndexOf('}');
        if (start < 0 || end <= start) throw new IllegalArgumentException("AI did not return an app blueprint");
        JSONObject data = new JSONObject(response.substring(start, end + 1));
        if (!data.has("status")) throw new IllegalArgumentException("Blueprint status is missing");
        String status = data.getString("status");
        if (!status.equals("needs_input") && !status.equals("ready"))
            throw new IllegalArgumentException("Unsupported blueprint status");
        if (status.equals("needs_input") && !data.has("questions")) data.put("questions", new JSONArray());
        if (status.equals("ready")) {
            requireArray(data, "screens");
            requireArray(data, "components");
            requireArray(data, "dependencies");
            requireArray(data, "build_options");
        }
        rejectSecrets(data.toString().toLowerCase());
        return new AIAppBlueprint(data);
    }

    public boolean needsInput() { return "needs_input".equals(data.optString("status")); }
    public String name() { return data.optString("name", "AI App"); }
    public String packageName() { return data.optString("package", "com.my.aiapp"); }
    public String language() { return data.optString("language", "java"); }

    public String displayText() {
        if (needsInput()) {
            StringBuilder text = new StringBuilder("I need a few details:\n");
            JSONArray questions = data.optJSONArray("questions");
            for (int i = 0; questions != null && i < questions.length(); i++)
                text.append("• ").append(questions.optString(i)).append('\n');
            return text.toString();
        }
        try {
            return "App blueprint ready\n\n" + data.toString(2);
        } catch (org.json.JSONException e) {
            return "App blueprint ready\n\n" + data.toString();
        }
    }

    private static void requireArray(JSONObject object, String name) {
        if (object.optJSONArray(name) == null) throw new IllegalArgumentException(name + " must be an array");
    }

    private static void rejectSecrets(String value) {
        if (value.contains("password\"") || value.contains("api_key\"") || value.contains("private_key\""))
            throw new IllegalArgumentException("Blueprint must not contain secret values");
    }
}
