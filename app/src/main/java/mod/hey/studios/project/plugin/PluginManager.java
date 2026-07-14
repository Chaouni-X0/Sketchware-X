package mod.hey.studios.project.plugin;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PluginManager {
    private static final String PLUGINS_DIR = "plugins";
    private final Context context;
    private final OkHttpClient client = new OkHttpClient();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public PluginManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public void installPluginFromGitHub(String repoUrl, InstallCallback callback) {
        executor.execute(() -> {
            File temp = null;
            try {
                String normalized = normalizeRepositoryUrl(repoUrl);
                String pluginName = normalized.substring(normalized.lastIndexOf('/') + 1);
                String archiveUrl = normalized + "/archive/refs/heads/main.zip";
                Request request = new Request.Builder().url(archiveUrl).get().build();
                temp = File.createTempFile("plugin-", ".zip", context.getCacheDir());

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful() || response.body() == null) {
                        throw new IOException("GitHub returned HTTP " + response.code());
                    }
                    try (InputStream input = response.body().byteStream();
                         FileOutputStream output = new FileOutputStream(temp)) {
                        byte[] buffer = new byte[8192];
                        int count;
                        while ((count = input.read(buffer)) != -1) output.write(buffer, 0, count);
                    }
                }

                File destination = new File(context.getFilesDir(), PLUGINS_DIR + "/" + pluginName);
                deleteRecursively(destination);
                if (!destination.mkdirs() && !destination.isDirectory()) {
                    throw new IOException("Cannot create plugin directory");
                }
                unzipSafely(temp, destination);
                callback.onSuccess(pluginName);
            } catch (Exception e) {
                callback.onFailure(e);
            } finally {
                if (temp != null) temp.delete();
            }
        });
    }

    public List<String> getInstalledPlugins() {
        List<String> plugins = new ArrayList<>();
        File pluginsDir = new File(context.getFilesDir(), PLUGINS_DIR);
        File[] files = pluginsDir.listFiles();
        if (files != null) {
            for (File file : files) if (file.isDirectory()) plugins.add(file.getName());
        }
        return plugins;
    }

    private static String normalizeRepositoryUrl(String value) {
        String url = value == null ? "" : value.trim().replaceAll("/+$", "");
        if (!url.matches("https://github\\.com/[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+")) {
            throw new IllegalArgumentException("Use a public HTTPS GitHub repository URL");
        }
        return url.endsWith(".git") ? url.substring(0, url.length() - 4) : url;
    }

    private static void unzipSafely(File archive, File destination) throws IOException {
        String root = destination.getCanonicalPath() + File.separator;
        try (ZipInputStream zip = new ZipInputStream(new java.io.FileInputStream(archive))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                String name = entry.getName();
                int slash = name.indexOf('/');
                if (slash >= 0) name = name.substring(slash + 1);
                if (name.isEmpty()) continue;
                File output = new File(destination, name);
                if (!output.getCanonicalPath().startsWith(root)) throw new IOException("Unsafe archive path");
                if (entry.isDirectory()) {
                    if (!output.mkdirs() && !output.isDirectory()) throw new IOException("Cannot create directory");
                } else {
                    File parent = output.getParentFile();
                    if (parent != null && !parent.mkdirs() && !parent.isDirectory()) throw new IOException("Cannot create directory");
                    try (FileOutputStream stream = new FileOutputStream(output)) {
                        byte[] buffer = new byte[8192];
                        int count;
                        while ((count = zip.read(buffer)) != -1) stream.write(buffer, 0, count);
                    }
                }
            }
        }
    }

    private static void deleteRecursively(File file) {
        if (!file.exists()) return;
        File[] children = file.listFiles();
        if (children != null) for (File child : children) deleteRecursively(child);
        file.delete();
    }

    public interface InstallCallback {
        void onSuccess(String pluginName);
        void onFailure(Exception error);
    }
}
