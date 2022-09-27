package cl.santotomas.trecibo;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

import java.util.Random;

public class SplashActivity extends AppCompatActivity {

    private final int progressStatus = 0;
    private ProgressBar progressBar;
    private final Handler handler = new Handler();
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseApp.initializeApp(this);

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