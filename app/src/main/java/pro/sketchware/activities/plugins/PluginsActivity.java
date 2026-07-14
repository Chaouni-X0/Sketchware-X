package pro.sketchware.activities.plugins;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.besome.sketch.lib.base.BaseAppCompatActivity;
import mod.hey.studios.project.plugin.PluginManager;
import pro.sketchware.R;

public class PluginsActivity extends BaseAppCompatActivity {
    private PluginManager pluginManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pluginManager = new PluginManager(this);
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        EditText etRepoUrl = new EditText(this);
        etRepoUrl.setHint("رابط مستودع GitHub للإضافة");
        layout.addView(etRepoUrl);

        MaterialButton btnInstall = new MaterialButton(this);
        btnInstall.setText("تثبيت إضافة جديدة");
        btnInstall.setOnClickListener(v -> {
            String url = etRepoUrl.getText().toString();
            if (!url.isEmpty()) {
                pluginManager.installPluginFromGitHub(url);
                Toast.makeText(this, "بدء تحميل الإضافة...", Toast.LENGTH_SHORT).show();
            }
        });
        layout.addView(btnInstall);

        setContentView(layout);
    }
}
