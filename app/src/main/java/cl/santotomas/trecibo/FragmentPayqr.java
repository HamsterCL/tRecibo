package cl.santotomas.trecibo;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import cl.santotomas.trecibo.datamodels.ValidateQRModel;
import cl.santotomas.trecibo.watchers.MoneyTextWatcher;


public class FragmentPayqr extends DialogFragment {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy hh:mm a", new Locale("es", "CL"));
    private static final String URL_TRANSBANK_CREATE = "https://webpay3gint.transbank.cl/webpayserver/initTransaction";
    public ValidateQRModel validate;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payqr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText txtUserName = (TextInputEditText) getView().findViewById(R.id.editTextName);
        TextInputEditText txtDate = (TextInputEditText) getView().findViewById(R.id.editTextDate);
        TextInputEditText txtAmount = (TextInputEditText) getView().findViewById(R.id.editTextAmount);
        txtAmount.addTextChangedListener(new MoneyTextWatcher(txtAmount));
        TextInputEditText txtReason = (TextInputEditText) getView().findViewById(R.id.editTextReason);
        CircularProgressButton ciPay = (CircularProgressButton) getView().findViewById(R.id.cirQRPayButton);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String bValidate = bundle.getString("validate");
            validate = new Gson().fromJson(bValidate, ValidateQRModel.class);
            txtUserName.setText(validate.getInvoice());
            String dte = simpleDateFormat.format(new Date());
            txtDate.setText(dte);
            txtAmount.setText(validate.getAmount());
            txtReason.setText(validate.getReason());
        }

        ciPay.setOnClickListener(view1 -> {
            String uri = Uri.parse(URL_TRANSBANK_CREATE).buildUpon().appendQueryParameter("token_ws", validate.getCode_trx()).build().toString();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(browserIntent);
            dismiss();

            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(getContext());
            builder.setMessage("A continuación debe esperar mientras el pago se actualiza en nuestros sistemas, de todas formas al usuario a quien le hizo el pago se le notificará mediante mensajería. Muchas Gracias ;)").setCancelable(false).setPositiveButton("Aceptar", (dialog, id) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.setTitle("Atención!");
            alert.setIcon(R.drawable.ic_icon_success_alert);
            alert.show();
        });

    }
}