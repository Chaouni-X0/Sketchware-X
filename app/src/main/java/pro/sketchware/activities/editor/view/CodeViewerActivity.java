package pro.sketchware.activities.editor.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.besome.sketch.lib.base.BaseAppCompatActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import a.a.a.Lx;
import a.a.a.wq;
import mod.hey.studios.util.Helper;
import pro.sketchware.R;
import pro.sketchware.databinding.ActivityCodeViewerBinding;
import pro.sketchware.utility.EditorUtils;
import pro.sketchware.utility.SketchwareUtil;
import pro.sketchware.utility.UI;

public class CodeViewerActivity extends BaseAppCompatActivity {
    public static final String SCHEME_XML = "xml";
    public static final String SCHEME_JAVA = "java";
    public static final String EXTRA_FILE_PATH = "file_path";

    private ActivityCodeViewerBinding binding;
    private File editableFile;
    private String originalContent;
    private boolean editable;

    @Override public void onCreate(@Nullable Bundle state) {
        enableEdgeToEdgeNoContrast();
        super.onCreate(state);
        binding = ActivityCodeViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String code = getIntent().getStringExtra("code");
        String scheme = getIntent().getStringExtra("scheme");
        String scId = getIntent().getStringExtra("sc_id");
        editableFile = resolveProjectFile(scId, getIntent().getStringExtra(EXTRA_FILE_PATH));
        editable = editableFile != null;
        originalContent = editable ? read(editableFile) : Lx.j(code == null ? "" : code, false);

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> confirmExit());
        binding.toolbar.setSubtitle(editable ? editableFile.getName() : scId);
        binding.toolbar.setTitle(editable ? "Source Editor" : "Code Viewer");

        binding.editor.setTypefaceText(EditorUtils.getTypeface(this));
        binding.editor.setTextSize(14);
        binding.editor.setText(originalContent);
        binding.editor.setEditable(editable);
        binding.editor.setWordwrap(false);
        loadColorScheme(scheme == null ? SCHEME_JAVA : scheme);
        UI.addSystemWindowInsetToPadding(binding.appBarLayout, true, true, true, false);
        UI.addSystemWindowInsetToMargin(binding.editor, true, false, true, true);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        if (!editable) return super.onCreateOptionsMenu(menu);
        menu.add("Undo").setIcon(AppCompatResources.getDrawable(this, R.drawable.ic_mtrl_undo))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("Redo").setIcon(AppCompatResources.getDrawable(this, R.drawable.ic_mtrl_redo))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("Save").setIcon(AppCompatResources.getDrawable(this, R.drawable.ic_mtrl_save))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String title = item.getTitle().toString();
        if (title.equals("Undo")) binding.editor.undo();
        else if (title.equals("Redo")) binding.editor.redo();
        else if (title.equals("Save")) save();
        else return super.onOptionsItemSelected(item);
        return true;
    }

    private void save() {
        try {
            String content = binding.editor.getText().toString();
            if (content.equals(originalContent)) {
                SketchwareUtil.toast("No changes to save");
                return;
            }
            File backupDir = new File(getFilesDir(), "source_backups");
            if (!backupDir.mkdirs() && !backupDir.isDirectory()) throw new IllegalStateException("Cannot create backup folder");
            File backup = new File(backupDir, editableFile.getName() + "." + System.currentTimeMillis() + ".bak");
            Files.copy(editableFile.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.write(editableFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
            originalContent = content;
            SketchwareUtil.toast("Saved · backup created");
        } catch (Exception error) {
            SketchwareUtil.toastError("Save failed: " + error.getMessage());
        }
    }

    private void confirmExit() {
        if (!editable || originalContent.equals(binding.editor.getText().toString())) {
            finish();
            return;
        }
        new MaterialAlertDialogBuilder(this).setTitle("Unsaved changes")
                .setMessage("Save your source changes before closing?")
                .setNegativeButton("Discard", (d, w) -> finish())
                .setNeutralButton("Cancel", null)
                .setPositiveButton("Save", (d, w) -> { save(); finish(); })
                .show();
    }

    private File resolveProjectFile(String scId, String path) {
        if (scId == null || path == null || path.trim().isEmpty()) return null;
        try {
            File candidate = new File(path).getCanonicalFile();
            File data = new File(wq.b(scId)).getCanonicalFile();
            File source = new File(wq.c(scId)).getCanonicalFile();
            boolean inside = isInside(candidate, data) || isInside(candidate, source);
            return inside && candidate.isFile() ? candidate : null;
        } catch (Exception ignored) { return null; }
    }

    private boolean isInside(File file, File root) {
        return file.getPath().equals(root.getPath()) || file.getPath().startsWith(root.getPath() + File.separator);
    }

    private String read(File file) {
        try { return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8); }
        catch (Exception error) { return ""; }
    }

    private void loadColorScheme(String scheme) {
        if (SCHEME_XML.equals(scheme)) EditorUtils.loadXmlConfig(binding.editor);
        else EditorUtils.loadJavaConfig(binding.editor);
    }
}
