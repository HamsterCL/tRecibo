package cl.santotomas.vergara.trecibo;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import cl.santotomas.vergara.trecibo.datamodels.ValidateQRModel;
import cl.santotomas.vergara.trecibo.interfaces.APITRecibo;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentQr extends Fragment {

    private FirebaseAuth mAuth;
    private QRCodeReaderView qrCodeReaderView;
    private static final String URL_VALIDATE_QR = "https://secure.tooltips.cl";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        FragmentPayqr dialogFragment = new FragmentPayqr();

        qrCodeReaderView = (QRCodeReaderView) getView().findViewById(R.id.camera_view);
        qrCodeReaderView.setOnQRCodeReadListener((text, points) -> {
            qrCodeReaderView.stopCamera();
            mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Call call = postValidateQR(text, task.getResult().getToken());
                    call.enqueue(new Callback<ValidateQRModel>() {
                        @Override
                        public void onResponse(Call<ValidateQRModel> call, Response<ValidateQRModel> response) {
                            if (response.body() != null) {
                                if (response.body().getId() != null) {
                                    ValidateQRModel validate;
                                    validate = response.body();
                                    Bundle bViewQR = new Bundle();
                                    bViewQR.putString("validate", new Gson().toJson(validate));
                                    dialogFragment.setArguments(bViewQR);
                                    dialogFragment.show(getChildFragmentManager(), "");
                                }
                            } else {
                                messageDialog("El código QR que ha presentado es inváido, favor volver a generar el código QR y realizar el pago. Muchas Gracias!", "Ups!");
                            }
                        }

                        @Override
                        public void onFailure(Call<ValidateQRModel> call, Throwable t) {
                            messageDialog("Tenemos problemas en la plataforma, por favor vuelva a intentar el cobro en otro momento. Gracias ;)", "Ups!");
                            Log.d("PostValidateQR", "PostValidateQR Exception -> " + ((t != null && t.getMessage() != null) ? t.getMessage() : "---"));
                        }
                    });
                }
            });
        });

        qrCodeReaderView.setQRDecodingEnabled(true);
        qrCodeReaderView.setAutofocusInterval(2000L);
        qrCodeReaderView.setTorchEnabled(true);
        qrCodeReaderView.setBackCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        return inflater.inflate(R.layout.fragment_qr, container, false);
    }

    private Call postValidateQR(String code, String token) {

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request newRequest = chain.request().newBuilder().addHeader("Content-Type", "application/json").addHeader("Authorization", "Bearer " + token).header("Authorization", "Bearer " + token).build();
            return chain.proceed(newRequest);
        }).build();

        Retrofit retrofit = new Retrofit.Builder().client(client).baseUrl(URL_VALIDATE_QR).addConverterFactory(GsonConverterFactory.create()).build();

        APITRecibo apitRecibo = retrofit.create(APITRecibo.class);

        ValidateQRModel dataModel = new ValidateQRModel(code);
        Call<ValidateQRModel> call = apitRecibo.postValidateCreateQR("Bearer {token}", dataModel);

        return call;
    }

    private void messageDialog(String text, String title) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getContext());
        builder.setMessage(text).setCancelable(false).setPositiveButton("Aceptar", (dialog, id) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.setTitle(title);
        alert.setIcon(R.drawable.ic_icon_warning_alert);
        alert.show();
    }
}