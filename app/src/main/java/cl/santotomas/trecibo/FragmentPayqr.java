package cl.santotomas.trecibo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import cl.santotomas.trecibo.datamodels.ValidateQRModel;
import cl.santotomas.trecibo.watchers.MoneyTextWatcher;


public class FragmentPayqr extends DialogFragment {

    ValidateQRModel validate;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payqr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText txtUserName = (TextInputEditText) getView().findViewById(R.id.editTextName);
        TextInputEditText txtDate = (TextInputEditText) getView().findViewById(R.id.editTextDate);
        TextInputEditText txtAmount = (TextInputEditText) getView().findViewById(R.id.editTextAmount);
        txtAmount.addTextChangedListener(new MoneyTextWatcher(txtAmount));
        TextInputEditText txtReason= (TextInputEditText) getView().findViewById(R.id.editTextReason);
        CircularProgressButton ciPay = (CircularProgressButton) getView().findViewById(R.id.cirQRPayButton);


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String bValidate = bundle.getString("validate");
            validate = new Gson().fromJson(bValidate, ValidateQRModel.class);
            txtUserName.setText(validate.getInvoice());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy hh:mm a", new Locale("es", "CL"));
            String dte =  simpleDateFormat.format(new Date());
            txtDate.setText(dte);
            txtAmount.setText(validate.getAmount());
            txtReason.setText(validate.getReason());
        }

        ciPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = Uri.parse("https://webpay3gint.transbank.cl/webpayserver/initTransaction")
                        .buildUpon()
                        .appendQueryParameter("token_ws", validate.getCode_trx())
                        .build().toString();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(browserIntent);
                dismiss();

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(getContext());
                builder.setMessage("A continuación debe esperar mientras el pago se actualiza en nuestros sistemas, de todas formas al usuario a quien le hizo el pago se le notificará mediante mensajería. Muchas Gracias ;)")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("Atención!");
                alert.setIcon(R.drawable.ic_icon_success_alert);
                alert.show();
            }
        });

    }
}