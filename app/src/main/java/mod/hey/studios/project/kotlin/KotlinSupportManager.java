package mod.hey.studios.project.kotlin;

import java.io.File;
import a.a.a.yq;

public class KotlinSupportManager {
    
    public static void enableKotlinSupport(String projectPath) {
        // إضافة إعدادات Kotlin للمشروع
        String configPath = projectPath + "/project.kotlin";
        new yq().a(configPath, "enabled=true\nversion=2.1.21");
    }
    
    public static void enableComposeSupport(String projectPath) {
        // إضافة إعدادات Jetpack Compose للمشروع
        String configPath = projectPath + "/project.compose";
        new yq().a(configPath, "enabled=true\ncompose_version=1.7.0");
    }
}
