package cl.santotomas.trecibo;

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

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;


public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout textInputName, textInputEmail, textInputMobile, textInputPassword;
    private TextInputEditText editTextName, editTextEmail, editTextMobile, editTextPassword;
    private CircularProgressButton btnRegister;

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
                                    showMessage("Favor comunicarse con \"Soporte\" al correo eletrónico soporte@trecibo.cl.");
                                    break;

                                case "ERROR_CUSTOM_TOKEN_MISMATCH":
                                    showMessage("Favor comunicarse con \"Soporte\" al correo eletrónico soporte@trecibo.cl.");
                                    break;

                                case "ERROR_INVALID_CREDENTIAL":
                                    showMessage("Favor comunicarse con \"Soporte\" al correo eletrónico soporte@trecibo.cl.");
                                    break;

                                case "ERROR_INVALID_EMAIL":
                                    showMessage( "Campo \"Correo Electrónico\" erróneo.");
                                    break;

                                case "ERROR_WRONG_PASSWORD":
                                    showMessage("La contraseña no es válida o el usuario no tiene contraseña.");
                                    break;

                                case "ERROR_USER_MISMATCH":
                                    showMessage("Las credenciales proporcionadas no corresponden al usuario que inició sesión anteriormente.");
                                    break;

                                case "ERROR_REQUIRES_RECENT_LOGIN":
                                    showMessage("Esta operación es confidencial y requiere autenticación reciente. Vuelva a iniciar sesión antes de volver a intentar esta solicitud.");
                                    break;

                                case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                                    showMessage("Ya existe una cuenta con la misma dirección de correo electrónico pero con diferentes credenciales de inicio de sesión. Inicie sesión con un proveedor asociado con esta dirección de correo electrónico.");
                                    break;

                                case "ERROR_EMAIL_ALREADY_IN_USE":
                                    showMessage("La dirección de correo electrónico ya está en uso por otra cuenta.");
                                    break;

                                case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                                    showMessage("Esta credencial ya está asociada con una cuenta de usuario diferente.");
                                    break;

                                case "ERROR_USER_DISABLED":
                                    showMessage("La cuenta de usuario ha sido deshabilitada.");
                                    break;

                                case "ERROR_USER_TOKEN_EXPIRED":
                                    showMessage("La credencial del usuario ya no es válida. El usuario debe iniciar sesión de nuevo.");
                                    break;

                                case "ERROR_USER_NOT_FOUND":
                                    showMessage("El correo eletrónico o contraseña proporcionada no es válido.");
                                    break;

                                case "ERROR_INVALID_USER_TOKEN":
                                    showMessage("La credencial del usuario ya no es válida. El usuario debe iniciar sesión de nuevo.");
                                    break;

                                case "ERROR_OPERATION_NOT_ALLOWED":
                                    showMessage("Favor comunicarse con \"Soporte\" al correo eletrónico soporte@trecibo.cl.");
                                    break;

                                case "ERROR_WEAK_PASSWORD":
                                    showMessage("La contraseña proporcionada no es válida.");
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
                        if (task.isSuccessful()) {
                            startActivity(HomeActivity);
                        } else {
                            Log.d("UpdateUserRegister", task.getException().getMessage());
                        }
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

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }



}
