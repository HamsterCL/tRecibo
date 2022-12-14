package cl.santotomas.vergara.trecibo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private Intent HomeActivity;
    private CircularProgressButton btnLogin;
    private TextInputEditText edTextEmail, edTextPassword;
    private TextInputLayout txtIEmail, txtIPassword;
    private AlertDialog.Builder builder;
    private RelativeLayout rlLogin;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_login);
        rlLogin = findViewById(R.id.relativePayments);
        mAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                startActivity(HomeActivity);
            }
        };

        edTextEmail = findViewById(R.id.txtEmail);
        edTextPassword = findViewById(R.id.txtPassword);

        txtIEmail = findViewById(R.id.textInputEmail);
        txtIPassword = findViewById(R.id.textInputPassword);

        onCleanAllTextInputLayout();


        btnLogin = findViewById(R.id.cirLoginButton);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateCredentials()) {
                    onEmailAndPassword(edTextEmail.getText().toString(), edTextPassword.getText().toString());
                }
            }
        });

        HomeActivity = new Intent(this, HomeActivity.class);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            updateUI(user);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    updateUI(null);
                }
            }
        });
    }

    public void onLoginClick(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
    }

    public void onEmailAndPassword(String mail, String password) {
        mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    BiometricManager biometricManager = androidx.biometric.BiometricManager.from(getApplicationContext());
                    switch (biometricManager.canAuthenticate()) {
                        case BiometricManager.BIOMETRIC_SUCCESS:
                            updateUI(null);
                            break;
                        case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                            startActivity(HomeActivity);
                            finish();
                            break;

                        case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                            startActivity(HomeActivity);
                            finish();
                            break;

                        case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                            startActivity(HomeActivity);
                            finish();
                            break;
                    }


                } else {
                    String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                    switch (errorCode) {

                        case "ERROR_INVALID_CUSTOM_TOKEN":
                            showMessage("Favor comunicarse con \"Soporte\" al correo eletr??nico soporte@trecibo.cl.");
                            break;

                        case "ERROR_CUSTOM_TOKEN_MISMATCH":
                            showMessage("Favor comunicarse con \"Soporte\" al correo eletr??nico soporte@trecibo.cl.");
                            break;

                        case "ERROR_INVALID_CREDENTIAL":
                            showMessage("Favor comunicarse con \"Soporte\" al correo eletr??nico soporte@trecibo.cl.");
                            break;

                        case "ERROR_INVALID_EMAIL":
                            showMessage("Campo \"Correo Electr??nico\" err??neo.");
                            break;

                        case "ERROR_WRONG_PASSWORD":
                            showMessage("La contrase??a no es v??lida o el usuario no tiene contrase??a.");
                            break;

                        case "ERROR_USER_MISMATCH":
                            showMessage("Las credenciales proporcionadas no corresponden al usuario que inici?? sesi??n anteriormente.");
                            break;

                        case "ERROR_REQUIRES_RECENT_LOGIN":
                            showMessage("Esta operaci??n es confidencial y requiere autenticaci??n reciente. Vuelva a iniciar sesi??n antes de volver a intentar esta solicitud.");
                            break;

                        case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                            showMessage("Ya existe una cuenta con la misma direcci??n de correo electr??nico pero con diferentes credenciales de inicio de sesi??n. Inicie sesi??n con un proveedor asociado con esta direcci??n de correo electr??nico.");
                            break;

                        case "ERROR_EMAIL_ALREADY_IN_USE":
                            showMessage("La direcci??n de correo electr??nico ya est?? en uso por otra cuenta.");
                            break;

                        case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                            showMessage("Esta credencial ya est?? asociada con una cuenta de usuario diferente.");
                            break;

                        case "ERROR_USER_DISABLED":
                            showMessage("La cuenta de usuario ha sido deshabilitada.");
                            break;

                        case "ERROR_USER_TOKEN_EXPIRED":
                            showMessage("La credencial del usuario ya no es v??lida. El usuario debe iniciar sesi??n de nuevo.");
                            break;

                        case "ERROR_USER_NOT_FOUND":
                            showMessage("El correo eletr??nico o contrase??a proporcionada no es v??lido.");
                            break;

                        case "ERROR_INVALID_USER_TOKEN":
                            showMessage("La credencial del usuario ya no es v??lida. El usuario debe iniciar sesi??n de nuevo.");
                            break;

                        case "ERROR_OPERATION_NOT_ALLOWED":
                            showMessage("Favor comunicarse con \"Soporte\" al correo eletr??nico soporte@trecibo.cl.");
                            break;

                        case "ERROR_WEAK_PASSWORD":
                            showMessage("La contrase??a proporcionada no es v??lida.");
                            break;
                    }
                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {

        BiometricPrompt biometricPrompt = onBiometricCreate();
        final BiometricPrompt.PromptInfo promptInfo =
                new BiometricPrompt.PromptInfo.Builder().
                        setTitle("tRecibo").
                        setDescription("Autenticaci??n Biom??trica").
                        setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK).
                        setNegativeButtonText("Cancelar").build();
        biometricPrompt.authenticate(promptInfo);

    }

    private boolean validateCredentials() {

        final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        if (edTextEmail.length() == 0) {
            toggleTextInputLayoutError(txtIEmail, "Este campo es Requerido.");
            return false;
        }

        if (edTextEmail.getText().toString().isEmpty()) {
            toggleTextInputLayoutError(txtIEmail, "Este campo es Requerido.");
            return false;
        }

        if (edTextPassword.length() == 0) {
            toggleTextInputLayoutError(txtIPassword, "Este campo es Requerido.");
            return false;
        }

        if (!edTextEmail.getText().toString().trim().matches(EMAIL_PATTERN)) {
            toggleTextInputLayoutError(txtIEmail, "Formato erroneo del Correo Electr??nico.");
            return false;
        }

        txtIEmail.clearFocus();
        txtIPassword.clearFocus();

        return true;
    }

    private static void toggleTextInputLayoutError(@NonNull TextInputLayout textInputLayout, String msg) {
        textInputLayout.setError(msg);
        textInputLayout.setErrorEnabled(msg != null);
    }

    private void showMessage(String text) {
        builder = new AlertDialog.Builder(this);

        builder.setMessage(text).setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setIcon(R.drawable.ic_icon_error_alert);
        alertDialog.setTitle("Validaci??n");
        alertDialog.show();

    }

    private void onCleanAllTextInputLayout() {
        edTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    txtIEmail.setError(null);
                    txtIEmail.clearFocus();
                }
            }
        });

        edTextPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    txtIPassword.setError(null);
                    txtIPassword.clearFocus();
                }
            }
        });
    }

    private BiometricPrompt onBiometricCreate() {

        Executor executor = ContextCompat.getMainExecutor(getApplicationContext());

        final BiometricPrompt biometricPrompt = new BiometricPrompt(LoginActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                startActivity(HomeActivity);
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        return biometricPrompt;
    }

}
