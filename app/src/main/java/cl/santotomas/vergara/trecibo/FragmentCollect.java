package cl.santotomas.vergara.trecibo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.sql.Date;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import cl.santotomas.vergara.trecibo.datamodels.CreateQRModel;
import cl.santotomas.vergara.trecibo.datamodels.History;
import cl.santotomas.vergara.trecibo.interfaces.APITRecibo;
import cl.santotomas.vergara.trecibo.watchers.MoneyTextWatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentCollect extends Fragment {

    private FirebaseAuth mAuth;
    private TextInputEditText edTextAmount, edTextReason;
    private TextInputLayout txtIAmount, txtIReason;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private static final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("es", "CL"));

    private static final String URL_CREATE_QR = "https://secure.tooltips.cl";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        edTextAmount = (TextInputEditText) view.findViewById(R.id.txtAmount);
        edTextAmount.addTextChangedListener(new MoneyTextWatcher(edTextAmount));

        edTextReason = (TextInputEditText) view.findViewById(R.id.txtReason);

        txtIAmount = (TextInputLayout) view.findViewById(R.id.textInputAmount);
        txtIReason = (TextInputLayout) view.findViewById(R.id.textInputReason);

        CircularProgressButton cirButton = (CircularProgressButton) view.findViewById(R.id.cirQRButton);
        cirButton.setOnClickListener(view1 -> mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (validateCollect()) {
                    FragmentViewqr dialogFragment = new FragmentViewqr();
                    Call call = postCreateQR(edTextAmount.getText().toString(), edTextReason.getText().toString(), task.getResult().getToken());
                    call.enqueue(new Callback<CreateQRModel>() {
                        @Override
                        public void onResponse(Call<CreateQRModel> call, Response<CreateQRModel> response) {
                            if (response.body() != null) {
                                if (response.body().getData() != null) {
                                    Bundle bViewQR = new Bundle();
                                    bViewQR.putString("qr", response.body().getData());
                                    dialogFragment.setArguments(bViewQR);
                                    dialogFragment.show(getChildFragmentManager(), "");
                                }
                            } else {
                                messageDialog("Nuestros sistemas presenta problemas. Favor de volver a intentar en unos instantes. Disculpe las molestias!", "Ups!");
                            }
                        }

                        @Override
                        public void onFailure(Call<CreateQRModel> call, Throwable t) {
                            messageDialog("Tenemos problemas en la plataforma, por favor vuelva a intentar el cobro en otro momento. Gracias ;)", "Ups!");
                            Log.d("PostCreateQR", "PostCreateQR Exception -> " + ((t != null && t.getMessage() != null) ? t.getMessage() : "---"));
                        }
                    });
                }
            }
        }));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collect, container, false);
    }

    private Call postCreateQR(String amount, String reason, String token) {
        String replaceRegex = String.format("[$,.\\s]", Objects.requireNonNull(numberFormat.getCurrency().getSymbol()));
        String finalAmount = amount.replaceAll(replaceRegex, "");

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder().addHeader("Content-Type", "application/json").addHeader("Authorization", "Bearer " + token).header("Authorization", "Bearer " + token).build();
                return chain.proceed(newRequest);
            }
        }).build();

        Retrofit retrofit = new Retrofit.Builder().client(client).baseUrl(URL_CREATE_QR).addConverterFactory(GsonConverterFactory.create()).build();

        APITRecibo apitRecibo = retrofit.create(APITRecibo.class);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("History"+System.currentTimeMillis());
        History history = new History();
        history.setAmount(Double.parseDouble(finalAmount));
        history.setReason(reason);
        history.setToken(token);
        history.setDate(System.currentTimeMillis());
        history.setFunctionality("Collect");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.setValue(history);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DB Firebase", "Error Database Firebase");
            }
        });

        CreateQRModel dataModel = new CreateQRModel(finalAmount, reason);
        Call<CreateQRModel> call = apitRecibo.postCreateQR("Bearer {token}", dataModel);

        return call;
    }

    private boolean validateCollect() {

        String replaceRegex = String.format("[$,.\\s]", Objects.requireNonNull(numberFormat.getCurrency().getSymbol()));
        String finalAmount = edTextAmount.getText().toString().replaceAll(replaceRegex, "");

        if (edTextAmount.length() == 0) {
            toggleTextInputLayoutError(txtIAmount, "Este campo es Requerido.");
            return false;
        }

        if (edTextReason.length() == 0) {
            toggleTextInputLayoutError(txtIReason, "Este campo es Requerido.");
            return false;
        }

        if (Integer.parseInt(finalAmount) < 1000 || Integer.parseInt(finalAmount) > 250000) {
            toggleTextInputLayoutError(txtIAmount, "El Monto debe ser mayor que $1.000 o menor que $250.000");
            return false;
        }

        txtIAmount.clearFocus();
        txtIReason.clearFocus();

        return true;
    }

    private static void toggleTextInputLayoutError(@NonNull TextInputLayout textInputLayout, String msg) {
        textInputLayout.setError(msg);
        textInputLayout.setErrorEnabled(msg != null);
    }

    private void messageDialog(String text, String title) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getContext());
        builder.setMessage(text).setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
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