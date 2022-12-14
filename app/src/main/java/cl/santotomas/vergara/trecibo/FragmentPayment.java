package cl.santotomas.vergara.trecibo;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import cl.santotomas.vergara.trecibo.adapter.ClickAdapter;
import cl.santotomas.vergara.trecibo.adapter.ImageGalleryAdapter;
import cl.santotomas.vergara.trecibo.datamodels.PaymentModel;
import cl.santotomas.vergara.trecibo.interfaces.APITRecibo;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentPayment extends Fragment {

    private FirebaseAuth mAuth;
    public ImageGalleryAdapter adapter;
    public RecyclerView recyclerView;
    public ClickAdapter listener;
    private static final String URL_LIST_PAYMENTS = "https://secure.tooltips.cl";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);

        mAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("AUTH", task.getResult().getToken());
                Call call = listPayments(task.getResult().getToken());
                call.enqueue(new Callback<List<PaymentModel>>() {
                    @Override
                    public void onResponse(Call<List<PaymentModel>> call, Response<List<PaymentModel>> response) {
                        if (response.body() != null) {
                            adapter = new ImageGalleryAdapter(response.body(), getContext(), listener);
                            recyclerView.setAdapter(adapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        } else {
                            messageDialog("Nuestros sistemas presenta problemas. Favor de volver a intentar en unos instantes. Disculpe las molestias!", "Ups!");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<PaymentModel>> call, Throwable t) {
                        messageDialog("Tenemos problemas en la plataforma, por favor vuelva a intentar en otro momento. Gracias ;)", "Ups!");
                        Log.d("ListPayments", "ListPayments Exception -> " + ((t != null && t.getMessage() != null) ? t.getMessage() : "---"));
                    }
                });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment, container, false);

    }

    private Call listPayments(String token) {

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request newRequest = chain.request().newBuilder().addHeader("Content-Type", "application/json").addHeader("Authorization", "Bearer " + token).header("Authorization", "Bearer " + token).build();
            return chain.proceed(newRequest);
        }).build();

        Retrofit retrofit = new Retrofit.Builder().client(client).baseUrl(URL_LIST_PAYMENTS).addConverterFactory(GsonConverterFactory.create()).build();

        APITRecibo apitRecibo = retrofit.create(APITRecibo.class);
        Call<List<PaymentModel>> call = apitRecibo.getPaymentsMonth("Bearer {token}");

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