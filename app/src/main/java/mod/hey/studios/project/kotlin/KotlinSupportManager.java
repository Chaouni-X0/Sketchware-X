package mod.hey.studios.project.kotlin;

import java.io.File;
import pro.sketchware.utility.FileUtil;

public class KotlinSupportManager {
    
    public static void enableKotlinSupport(String projectPath) {
        // إضافة إعدادات Kotlin للمشروع
        String configPath = projectPath + "/project.kotlin";
        FileUtil.writeFile(configPath, "enabled=true\nversion=2.1.21");
    }
    
    public static void enableComposeSupport(String projectPath) {
        // إضافة إعدادات Jetpack Compose للمشروع
        String configPath = projectPath + "/project.compose";
        FileUtil.writeFile(configPath, "enabled=true\ncompose_version=1.7.0");
    }
}
