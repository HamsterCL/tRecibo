package cl.santotomas.trecibo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import cl.santotomas.trecibo.adapter.ImageGalleryAdapter;
import cl.santotomas.trecibo.datamodels.CreateQRModel;
import cl.santotomas.trecibo.datamodels.DeviceModel;
import cl.santotomas.trecibo.datamodels.PaymentModel;
import cl.santotomas.trecibo.interfaces.APITRecibo;
import cl.santotomas.trecibo.message.services.FirebaseMessageService;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String URL_NOTIFICATION_QR = "https://secure.tooltips.cl";

    private FirebaseAuth mAuth;
    private FirebaseMessageService firebaseMessageService;

    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        firebaseMessageService = new FirebaseMessageService();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> taskTokenDevice) {
                        if (!taskTokenDevice.isSuccessful()) {
                            Log.d("FCMToken", "Fetching FCM registration token failed", taskTokenDevice.getException());
                            return;
                        }
                        mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                            @Override
                            public void onComplete(@NonNull Task<GetTokenResult> taskTokenUid) {
                                if (taskTokenUid.isSuccessful()) {
                                    Call call = putDeviceToken(taskTokenDevice.getResult(), taskTokenUid.getResult().getToken());
                                    call.enqueue(new Callback<DeviceModel>() {
                                        @Override
                                        public void onResponse(Call<DeviceModel> call, Response<DeviceModel> response) {
                                            if (response.body() == null) {
                                                messageDialog("Nuestros sistemas presenta problemas. Favor de volver a intentar en unos instantes. Disculpe las molestias!", "Ups!");
                                            }
                                        }
                                        @Override
                                        public void onFailure(Call<DeviceModel> call, Throwable t) {
                                            messageDialog("Tenemos problemas en la plataforma, por favor vuelva a intentar en otro momento. Gracias ;)", "Ups!");
                                            Log.d("DeviceToken", "DeviceToken Exception -> " + ((t != null && t.getMessage() != null) ? t.getMessage() : "---"));
                                        }
                                    });
                                }
                            }
                        });
                    }
                });

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

    private Call putDeviceToken(String key, String token) {
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest  = chain.request().newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "Bearer " + token)
                        .header("Authorization", "Bearer " + token)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(URL_NOTIFICATION_QR)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APITRecibo apitRecibo = retrofit.create(APITRecibo.class);

        DeviceModel dataModel = new DeviceModel(key);
        Call<DeviceModel> call = apitRecibo.putDeviceToken("Bearer {token}", dataModel);

        return call;
    }

    private void messageDialog(String text, String title) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setMessage(text)
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle(title);
        alert.setIcon(R.drawable.ic_icon_warning_alert);
        alert.show();
    }

}