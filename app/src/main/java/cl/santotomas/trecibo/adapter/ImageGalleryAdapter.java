package cl.santotomas.trecibo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cl.santotomas.trecibo.FragmentPayqr;
import cl.santotomas.trecibo.R;
import cl.santotomas.trecibo.datamodels.PaymentModel;
import cl.santotomas.trecibo.datamodels.ValidateQRModel;
import cl.santotomas.trecibo.holder.PaymentsViewHolder;

public class ImageGalleryAdapter extends RecyclerView.Adapter<PaymentsViewHolder> {

    private static final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("es", "CL"));
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy hh:mm a", new Locale("es", "CL"));
    private static final String successful = "SUCCESSFUL";
    private static final String created = "CREATED";
    private FragmentPayqr dialogFragment = new FragmentPayqr();
    private List<PaymentModel> list = Collections.emptyList();
    private Context context;
    private ClickAdapter listener;

    public ImageGalleryAdapter(List<PaymentModel> list, Context context, ClickAdapter listener)
    {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public PaymentsViewHolder
    onCreateViewHolder(ViewGroup parent, int viewType)
    {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater .inflate(R.layout.payment_card, parent, false);
        PaymentsViewHolder viewHolder = new PaymentsViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void
    onBindViewHolder(final PaymentsViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {

        int index = viewHolder.getAdapterPosition();
        viewHolder.paymentAmount.setText(numberFormat.format(Double.parseDouble(list.get(position).getAmount())));
        viewHolder.paymentReason.setText(list.get(position).getReason());
        viewHolder.paymentDate.setText(simpleDateFormat.format(getDateFromString(list.get(position).getDate())));
        switch (list.get(position).getStatus().trim()) {
            case successful:
                viewHolder.paymentStatus.setImageResource(R.drawable.ic_icon_success_alert);
                break;
            case created:
                viewHolder.paymentStatus.setImageResource(R.drawable.ic_icon_warning_alert);
                break;
            default:
                viewHolder.paymentStatus.setImageResource(R.drawable.ic_icon_error_alert);
        };
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(
            RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static Date getDateFromString(String string)
    {
        Instant timestamp = null;
        Date date = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            timestamp = Instant.parse(string);
            date = Date.from(timestamp);
        }
        return date;
    }


}