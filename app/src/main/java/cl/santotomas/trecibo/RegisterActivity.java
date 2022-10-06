package cl.santotomas.trecibo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.IOException;
import java.util.Objects;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import cl.santotomas.trecibo.datamodels.AccountModel;
import cl.santotomas.trecibo.datamodels.CreateQRModel;
import cl.santotomas.trecibo.interfaces.APITRecibo;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RegisterActivity extends AppCompatActivity {

    private static final String URL_CREATE_ACCOUNT = "https://secure.tooltips.cl";

    private TextInputLayout textInputName, textInputEmail, textInputMobile, textInputPassword;
    private TextInputEditText editTextName, editTextEmail, editTextMobile, editTextPassword;
    private CircularProgressButton btnRegister;

    private AlertDialog.Builder builder;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Intent HomeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        changeStatusBarColor();

        mAuth = FirebaseAuth.getInstance();

        textInputName = findViewById(R.id.textInputName);
        textInputEmail = findViewById(R.id.textInputEmail);
        textInputMobile = findViewById(R.id.textInputMobile);
        textInputPassword = findViewById(R.id.textInputPassword);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextMobile = findViewById(R.id.editTextMobile);
        editTextPassword = findViewById(R.id.editTextPassword);

        onCleanAllTextInputLayout();

        btnRegister = findViewById(R.id.cirRegisterButton);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateCredentials()) {
                    onRegisterAccount(editTextEmail.getText().toString().trim(), editTextPassword.getText().toString().trim(), String.valueOf(editTextName.getText()));
                }

            }
        });

        HomeActivity = new Intent(this,cl.santotomas.trecibo.HomeActivity.class);
    }

    private void onRegisterAccount(String email, String password, String name) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {

                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                            switch (errorCode) {

                                case "ERROR_INVALID_CUSTOM_TOKEN":
                                    messageDialog("Favor comunicarse con \"Soporte\" al correo eletrónico soporte@trecibo.cl.", "Validación", R.drawable.ic_icon_warning_alert);
                                    break;

                                case "ERROR_CUSTOM_TOKEN_MISMATCH":
                                    messageDialog("Favor comunicarse con \"Soporte\" al correo eletrónico soporte@trecibo.cl.", "Validación", R.drawable.ic_icon_warning_alert);
                                    break;

                                case "ERROR_INVALID_CREDENTIAL":
                                    messageDialog("Favor comunicarse con \"Soporte\" al correo eletrónico soporte@trecibo.cl.", "Validación", R.drawable.ic_icon_warning_alert);
                                    break;

                                case "ERROR_INVALID_EMAIL":
                                    messageDialog( "Campo \"Correo Electrónico\" erróneo.", "Validación", R.drawable.ic_icon_warning_alert);
                                    break;

                                case "ERROR_WRONG_PASSWORD":
                                    messageDialog("La contraseña no es válida o el usuario no tiene contraseña.", "Validación", R.drawable.ic_icon_warning_alert);
                                    break;

                                case "ERROR_USER_MISMATCH":
                                    messageDialog("Las credenciales proporcionadas no corresponden al usuario que inició sesión anteriormente.", "Validación", R.drawable.ic_icon_warning_alert);
                                    break;

                                case "ERROR_REQUIRES_RECENT_LOGIN":
                                    messageDialog("Esta operación es confidencial y requiere autenticación reciente. Vuelva a iniciar sesión antes de volver a intentar esta solicitud.", "Validación", R.drawable.ic_icon_warning_alert);
                                    break;

                                case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                                    messageDialog("Ya existe una cuenta con la misma dirección de correo electrónico pero con diferentes credenciales de inicio de sesión. Inicie sesión con un proveedor asociado con esta dirección de correo electrónico.", "Validación", R.drawable.ic_icon_warning_alert);
                                    break;

                                case "ERROR_EMAIL_ALREADY_IN_USE":
                                    messageDialog("La dirección de correo electrónico ya está en uso por otra cuenta.", "Validación", R.drawable.ic_icon_warning_alert);
                                    break;

                                case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                                    messageDialog("Esta credencial ya está asociada con una cuenta de usuario diferente.", "Validación", R.drawable.ic_icon_warning_alert);
                                    break;

                                case "ERROR_USER_DISABLED":
                                    messageDialog("La cuenta de usuario ha sido deshabilitada.", "Validación", R.drawable.ic_icon_warning_alert);
                                    break;

                                case "ERROR_USER_TOKEN_EXPIRED":
                                    messageDialog("La credencial del usuario ya no es válida. El usuario debe iniciar sesión de nuevo.", "Validación", R.drawable.ic_icon_warning_alert);
                                    break;

                                case "ERROR_USER_NOT_FOUND":
                                    messageDialog("El correo eletrónico o contraseña proporcionada no es válido.", "Validación", R.drawable.ic_icon_warning_alert);
                                    break;

                                case "ERROR_INVALID_USER_TOKEN":
                                    messageDialog("La credencial del usuario ya no es válida. El usuario debe iniciar sesión de nuevo.", "Validación", R.drawable.ic_icon_warning_alert);
                                    break;

                                case "ERROR_OPERATION_NOT_ALLOWED":
                                    messageDialog("Favor comunicarse con \"Soporte\" al correo eletrónico soporte@trecibo.cl.", "Validación", R.drawable.ic_icon_warning_alert);
                                    break;

                                case "ERROR_WEAK_PASSWORD":
                                    messageDialog("La contraseña proporcionada no es válida.", "Validación", R.drawable.ic_icon_warning_alert);
                                    break;
                            }
                        }
                    }
                });
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }

    public void onLoginClick(View view){
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);

    }

    private void updateUI(FirebaseUser user) {


        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(editTextName.getText().toString())
                .setPhotoUri(Uri.parse("https://firebasestorage.googleapis.com/v0/b/trecibo-1f834.appspot.com/o/logo_trecibo.png?alt=media&token=971e9882-9b8c-4dc2-9e96-2774ea5d9255"))
                .build();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Log.d("UpdateUserRegister", task.getException().getMessage());
                        }
                    }
                });

        Log.d("UID", user.getUid());
        Call call =  postCreateAccount(editTextName.getText().toString(), editTextMobile.getText().toString(), editTextEmail.getText().toString(), user.getUid());
        call.enqueue(new Callback<AccountModel>() {
            @Override
            public void onResponse(Call<AccountModel> call, Response<AccountModel> response) {
                if (response.body() != null) {
                    messageDialogWelcome("Le damos la bienvenida a nuestra APP tRecibo y le damos las gracias por registrarse. Ya puede utilizar las prestaciones de tRecibo.", "Registro", R.drawable.ic_icon_success_alert);
                }
                else {
                    messageDialog("Nuestros sistemas presenta problemas. Favor de volver a intentar en unos instantes. Disculpe las molestias!", "Ups!", R.drawable.ic_icon_error_alert);
                }
            }

            @Override
            public void onFailure(Call<AccountModel> call, Throwable t) {
                messageDialog("Tenemos problemas en la plataforma, por favor vuelva a intentar en otro momento. Gracias ;)", "Ups!", R.drawable.ic_icon_warning_alert);
                Log.d("PostCreateAccount", "PostCreateAccount Exception -> " + ((t != null && t.getMessage() != null) ? t.getMessage() : "---"));
            }
        });

    }

    private boolean validateCredentials() {

        final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        if (editTextName.length() == 0) {
            toggleTextInputLayoutError(textInputName, "Este campo es Requerido.");
            return false;
        }

        if (editTextEmail.length() == 0) {
            toggleTextInputLayoutError(textInputEmail, "Este campo es Requerido.");
            return false;
        }

        if (editTextMobile.length() == 0) {
            toggleTextInputLayoutError(textInputMobile, "Este campo es Requerido.");
            return false;
        }

        if (editTextPassword.length() == 0) {
            toggleTextInputLayoutError(textInputPassword, "Este campo es Requerido.");
            return false;
        }

        if (!editTextEmail.getText().toString().trim().matches(EMAIL_PATTERN)) {
            toggleTextInputLayoutError(textInputEmail, "Formato erroneo del Correo Electrónico.");
            return false;
        }

        return true;
    }

    private static void toggleTextInputLayoutError(@NonNull TextInputLayout textInputLayout, String msg) {
        textInputLayout.setError(msg);
        textInputLayout.setErrorEnabled(msg != null);
    }

    private void onCleanAllTextInputLayout() {
        editTextName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    textInputName.setError(null);
                    textInputName.clearFocus();
                }
            }
        });

        editTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    textInputEmail.setError(null);
                    textInputEmail.clearFocus();
                }
            }
        });

        editTextMobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    textInputMobile.setError(null);
                    textInputMobile.clearFocus();
                }
            }
        });

        editTextPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    textInputPassword.setError(null);
                    textInputPassword.clearFocus();
                }
            }
        });
    }

    private Call postCreateAccount(String name, String phone, String email, String uID) {

        String firstName = "", lastName = "";

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest  = chain.request().newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(URL_CREATE_ACCOUNT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APITRecibo apitRecibo = retrofit.create(APITRecibo.class);

        String[] names = name.trim().split(" ");
        switch (names.length) {
            case 4:
                firstName = names[0] + " " + names[1];
                lastName = names[2] + " " + names[3];
                break;
            case 3:
                firstName = names[0] + " " + names[1];
                lastName = names[2];
                break;
            case 2:
                firstName = names[0];
                lastName = names[1];
                break;
            default:
                firstName = names[0];
        }

        AccountModel dataModel = new AccountModel(firstName, lastName, email, Integer.parseInt(phone), uID);
        Call<AccountModel> call = apitRecibo.postCreateAccount(dataModel);

        return call;
    }

    private void messageDialog(String text, String title, int icon) {
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
        alert.setIcon(icon);
        alert.show();
    }

    private void messageDialogWelcome(String text, String title, int icon) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setMessage(text)
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        startActivity(HomeActivity);
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle(title);
        alert.setIcon(icon);
        alert.show();
    }
}
