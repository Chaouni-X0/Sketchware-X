package pro.sketchware.activities.ai;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import pro.sketchware.R;

public class AIProvidersActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        int p = (int) (16 * getResources().getDisplayMetrics().density);
        root.setPadding(p, p, p, p);

        MaterialToolbar toolbar = new MaterialToolbar(this);
        toolbar.setTitle("AI API Providers");
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(v -> finish());
        root.addView(toolbar);

        EditText name = field("Provider name", false);
        EditText url = field("Base URL, e.g. https://api.openai.com", false);
        EditText model = field("Model, e.g. gpt-4o-mini", false);
        EditText key = field("API key", true);
        root.addView(name); root.addView(url); root.addView(model); root.addView(key);

        MaterialButton save = new MaterialButton(this);
        save.setText("Add and use provider");
        save.setOnClickListener(v -> {
            try {
                AIProviderStore.add(this, name.getText().toString(), url.getText().toString(),
                        model.getText().toString(), key.getText().toString());
                Toast.makeText(this, "Provider encrypted and saved", Toast.LENGTH_SHORT).show();
                finish();
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        root.addView(save);
        setContentView(root);
    }

    private EditText field(String hint, boolean secret) {
        EditText field = new EditText(this);
        field.setHint(hint);
        field.setSingleLine(true);
        if (secret) field.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        return field;
    }
}
