package cl.santotomas.trecibo.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cl.santotomas.trecibo.R;
import cl.santotomas.trecibo.datamodels.PaymentModel;
import cl.santotomas.trecibo.datamodels.ValidateQRModel;
import cl.santotomas.trecibo.holder.PaymentsViewHolder;

public class ImageGalleryAdapter extends RecyclerView.Adapter<PaymentsViewHolder> {

    private static final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("es", "CL"));
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy hh:mm a", new Locale("es", "CL"));

    public List<PaymentModel> list = Collections.emptyList();


    public Context context;
    public ClickAdapter listener;

    public ImageGalleryAdapter(List<PaymentModel> list, Context context)
    {
        this.list = list;
        this.context = context;
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
    onBindViewHolder(final PaymentsViewHolder viewHolder, final int position) {

        int index = viewHolder.getAdapterPosition();
        viewHolder.paymentAmount.setText(numberFormat.format(Double.parseDouble(list.get(position).getAmount())));
        viewHolder.paymentReason.setText(list.get(position).getReason());
        viewHolder.paymentDate.setText(simpleDateFormat.format(getDateFromString(list.get(position).getDate())));
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.click(index);
            }
        });
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