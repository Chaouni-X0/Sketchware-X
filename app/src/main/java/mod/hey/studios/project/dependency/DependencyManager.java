package mod.hey.studios.project.dependency;

import java.util.ArrayList;
import java.util.List;
import a.a.a.yq;
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
            new yq().a(path, gson.toJson(dependencies));
        }
    }

    public List<String> getDependencies() {
        String path = projectPath + "/project.dependencies";
        if (new yq().e(path)) {
            return gson.fromJson(new yq().f(path), ArrayList.class);
        }
        return new ArrayList<>();
    }

    public void addMavenRepository(String url) {
        String path = projectPath + "/project.repositories";
        List<String> repositories = getRepositories();
        if (!repositories.contains(url)) {
            repositories.add(url);
            new yq().a(path, gson.toJson(repositories));
        }
    }

    public List<String> getRepositories() {
        String path = projectPath + "/project.repositories";
        if (new yq().e(path)) {
            return gson.fromJson(new yq().f(path), ArrayList.class);
        }
        return new ArrayList<>();
    }
}
