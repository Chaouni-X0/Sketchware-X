package mod.hey.studios.project.dependency;

import java.util.ArrayList;
import java.util.List;
import pro.sketchware.utility.FileUtil;
import com.google.gson.Gson;

public class DependencyManager {
    
    private String projectPath;
    private Gson gson;

    public DependencyManager(String projectPath) {
        this.projectPath = projectPath;
        this.gson = new Gson();
    }

    public void addDependency(String dependency) {
        // إضافة مكتبة إلى ملف project.dependencies
        String path = projectPath + "/project.dependencies";
        List<String> dependencies = getDependencies();
        if (!dependencies.contains(dependency)) {
            dependencies.add(dependency);
            FileUtil.writeFile(path, gson.toJson(dependencies));
        }
    }

    public List<String> getDependencies() {
        String path = projectPath + "/project.dependencies";
                if (FileUtil.isExistFile(path)) {
            return gson.fromJson(FileUtil.readFile(path), ArrayList.class);
        }
        return new ArrayList<>();
    }

    public void addMavenRepository(String url) {
        String path = projectPath + "/project.repositories";
        List<String> repositories = getRepositories();
        if (!repositories.contains(url)) {
            repositories.add(url);
            FileUtil.writeFile(path, gson.toJson(repositories));
        }
    }

    public List<String> getRepositories() {
        String path = projectPath + "/project.repositories";
                if (FileUtil.isExistFile(path)) {
            return gson.fromJson(FileUtil.readFile(path), ArrayList.class);
        }
        return new ArrayList<>();
    }
}
