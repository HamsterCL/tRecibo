package cl.santotomas.trecibo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;

    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        TextView txtName = findViewById(R.id.txtWelcome);
        TextView txtEmail = findViewById(R.id.txtEmail);
        CircleImageView imgUser = findViewById(R.id.imgUser);

        ImageButton imgExit = (ImageButton) findViewById(R.id.btnExit);
        imgExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setMessage("¿Desea salir de su cuenta?")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                logout();

                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("Cierre de Sesión");
                alert.show();
            }
        });

        builder = new AlertDialog.Builder(this);

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        String token = task.getResult();
                    }
                });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.page_home);

        txtEmail.setText(user.getEmail());
        String[] names = user.getDisplayName().split(" ");
        txtName.setText("Bienvenido! "+ names[0]);
        Glide.with(getApplicationContext()).load(user.getPhotoUrl()).into(imgUser);

        FragmentPayment fragment=new FragmentPayment();
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentHome, fragment,"");
        fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.page_home:
                FragmentPayment frmPayment = new FragmentPayment();
                fragmentTransaction.replace(R.id.fragmentHome, frmPayment,"");
                fragmentTransaction.commit();
                return true;

            case R.id.page_about:
                FragmentAbout frmAbout = new FragmentAbout();
                fragmentTransaction.replace(R.id.fragmentHome, frmAbout,"");
                fragmentTransaction.commit();
                return true;

            case R.id.page_payment:

                FragmentQr frmQr = new FragmentQr();
                fragmentTransaction.replace(R.id.fragmentHome, frmQr,"");
                fragmentTransaction.commit();
                return true;

            case R.id.page_collect:
                FragmentCollect frmCollect = new FragmentCollect();
                fragmentTransaction.replace(R.id.fragmentHome, frmCollect,"");
                fragmentTransaction.commit();
                return true;
        }
        return false;
    }

    private void logout() {
        mAuth.signOut();
        finish();
    }

}