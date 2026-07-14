package pro.sketchware.activities.ai;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public final class AIAppBlueprintStore {
    private AIAppBlueprintStore() {}

    public static File save(Context context, AIAppBlueprint blueprint) throws Exception {
        File directory = new File(context.getFilesDir(), "ai_blueprints");
        if (!directory.mkdirs() && !directory.isDirectory()) throw new IllegalStateException("Cannot create blueprint folder");
        File file = new File(directory, "blueprint-" + System.currentTimeMillis() + ".json");
        try (FileOutputStream output = new FileOutputStream(file)) {
            output.write(blueprint.data.toString(2).getBytes(StandardCharsets.UTF_8));
        }
        return file;
    }
}
