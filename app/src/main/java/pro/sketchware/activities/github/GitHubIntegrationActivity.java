package pro.sketchware.activities.github;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import pro.sketchware.R;

public class GitHubIntegrationActivity extends AppCompatActivity {

    private EditText repoUrlInput;
    private Button btnPush, btnPull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.github_integration_activity);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        repoUrlInput = findViewById(R.id.repo_url_input);
        btnPush = findViewById(R.id.btn_push);
        btnPull = findViewById(R.id.btn_pull);

        btnPush.setOnClickListener(v -> {
            String url = repoUrlInput.getText().toString();
            if (!url.isEmpty()) {
                pushToGitHub(url);
            }
        });

        btnPull.setOnClickListener(v -> {
            String url = repoUrlInput.getText().toString();
            if (!url.isEmpty()) {
                pullFromGitHub(url);
            }
        });
    }

    private void pushToGitHub(String url) {
        // منطق الرفع إلى GitHub
        Toast.makeText(this, "جاري الرفع إلى GitHub...", Toast.LENGTH_SHORT).show();
    }

    private void pullFromGitHub(String url) {
        // منطق الاستيراد من GitHub
        Toast.makeText(this, "جاري الاستيراد من GitHub...", Toast.LENGTH_SHORT).show();
    }
}
