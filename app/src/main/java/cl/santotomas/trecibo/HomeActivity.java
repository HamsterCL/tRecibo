package cl.santotomas.trecibo;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

    private Button btnLogout;

    private String name;
    private String email;
    private Uri photoURL;
    private String uid;

    private int mMenuId;

    private Intent QRActivity;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        QRActivity = new Intent(this, cl.santotomas.trecibo.QrActivity.class);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        TextView txtName = findViewById(R.id.txtWelcome);
        TextView txtEmail = findViewById(R.id.txtEmail);
        CircleImageView imgUser = findViewById(R.id.imgUser);

        AlertDialog.Builder alert = new AlertDialog.Builder(getApplicationContext());
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


    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.page_home:

                return true;

            case R.id.page_exit:
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
                return true;

            case R.id.page_payment:

                startActivity(QRActivity);


                return true;

            case R.id.page_collect:

                return true;
        }
        return false;
    }

    private void logout() {
        mAuth.signOut();
        finish();
    }

}