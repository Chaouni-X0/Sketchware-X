package pro.sketchware.activities.ai;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import pro.sketchware.security.SecureKeyVault;

public final class AIProviderStore {
    private static final String PREFS = "ai_providers";
    private static final String ITEMS = "items";
    private static final String ACTIVE = "active";

    private AIProviderStore() {}

    public static List<Provider> getAll(Context context) {
        List<Provider> result = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(prefs(context).getString(ITEMS, "[]"));
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                result.add(new Provider(item.getString("id"), item.getString("name"),
                        item.getString("url"), item.getString("model")));
            }
        } catch (Exception ignored) {}
        return result;
    }

    public static Provider getActive(Context context) {
        String id = prefs(context).getString(ACTIVE, "");
        List<Provider> providers = getAll(context);
        for (Provider provider : providers) if (provider.id.equals(id)) return provider;
        return providers.isEmpty() ? null : providers.get(0);
    }

    public static String getKey(Context context, Provider provider) {
        return provider == null ? "" : SecureKeyVault.get(context, "ai_provider_" + provider.id);
    }

    public static Provider add(Context context, String name, String url, String model, String key) throws Exception {
        String cleanUrl = url.trim().replaceAll("/+$", "");
        if (!cleanUrl.startsWith("https://")) throw new IllegalArgumentException("API URL must use HTTPS");
        if (name.trim().isEmpty() || model.trim().isEmpty() || key.trim().isEmpty())
            throw new IllegalArgumentException("All provider fields are required");
        Provider provider = new Provider(UUID.randomUUID().toString(), name.trim(), cleanUrl, model.trim());
        List<Provider> providers = getAll(context);
        providers.add(provider);
        save(context, providers);
        SecureKeyVault.put(context, "ai_provider_" + provider.id, key.trim());
        setActive(context, provider.id);
        return provider;
    }

    public static void setActive(Context context, String id) {
        prefs(context).edit().putString(ACTIVE, id).apply();
    }

    private static void save(Context context, List<Provider> providers) throws Exception {
        JSONArray array = new JSONArray();
        for (Provider provider : providers) {
            JSONObject item = new JSONObject();
            item.put("id", provider.id);
            item.put("name", provider.name);
            item.put("url", provider.url);
            item.put("model", provider.model);
            array.put(item);
        }
        prefs(context).edit().putString(ITEMS, array.toString()).apply();
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static final class Provider {
        public final String id;
        public final String name;
        public final String url;
        public final String model;

        Provider(String id, String name, String url, String model) {
            this.id = id; this.name = name; this.url = url; this.model = model;
        }

        @Override public String toString() { return name + " · " + model; }
    }
}
