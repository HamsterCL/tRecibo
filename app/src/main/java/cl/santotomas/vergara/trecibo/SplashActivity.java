package cl.santotomas.vergara.trecibo;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;

public class SplashActivity extends AppCompatActivity {

    private final Handler handler = new Handler();
    private TextView edTxtVersion;
    private String versionName = BuildConfig.VERSION_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        FirebaseApp.initializeApp(this);

        edTxtVersion = findViewById(R.id.txtVersion);
        edTxtVersion.setText(versionName);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }, 5000);
    }
}