package cl.santotomas.trecibo.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import cl.santotomas.trecibo.R;

public class PaymentsViewHolder extends RecyclerView.ViewHolder {
    public TextView paymentDate, paymentAmount, paymentReason;
    public ImageView paymentStatus;
    public View view;

    public PaymentsViewHolder(View itemView)
    {
        super(itemView);

        paymentAmount = (TextView) itemView.findViewById(R.id.paymentAmount);
        paymentDate = (TextView) itemView.findViewById(R.id.paymentDate);
        paymentReason = (TextView) itemView.findViewById(R.id.paymentReason);
        paymentStatus = (ImageView) itemView.findViewById(R.id.paymentStatus);
        view  = itemView;
    }

}
