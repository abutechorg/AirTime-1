package utilities;

import android.app.Activity;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oltranz.mobilea.mobilea.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import simplebeans.wallethistory.WalletHistoryBean;

/**
 * Created by Owner on 8/19/2016.
 */
public class WalletHistoryAdapter extends ArrayAdapter<WalletHistoryBean> {

    private String tag="AirTime: "+getClass().getSimpleName();
    private Typeface font;
    private Activity context;
    private List<WalletHistoryBean> mHistory;
    private List<WalletHistoryBean> tempList;
    private boolean addedList=false;

    public WalletHistoryAdapter(Activity context, List<WalletHistoryBean> mHistory){
        super(context, R.layout.favorite_style, mHistory);

        this.context=context;
        this.mHistory = mHistory;
        this.tempList = new ArrayList<>();
        this.tempList.addAll(mHistory);
        font = Typeface.createFromAsset(context.getAssets(), "font/ubuntu.ttf");
    }

    public View getView(final int position, View view, ViewGroup parent) {
        View rowView = view;
        Log.d(tag, "Row Number Initiated: " + mHistory.get(position).getTransactionId());
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.wallethistory_style, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.parent=(RelativeLayout) rowView.findViewById(R.id.walletStyleParent);

            viewHolder.lblTrans=(TextView) rowView.findViewById(R.id.lblTransaction);
            viewHolder.lblTrans.setTypeface(font, Typeface.BOLD);

            viewHolder.trans=(TextView) rowView.findViewById(R.id.transactionId);
            viewHolder.trans.setTypeface(font, Typeface.BOLD);

            viewHolder.lblDate=(TextView) rowView.findViewById(R.id.lblDate);
            viewHolder.lblDate.setTypeface(font, Typeface.BOLD);

            viewHolder.date=(TextView) rowView.findViewById(R.id.date);
            viewHolder.date.setTypeface(font, Typeface.BOLD);

            viewHolder.lblPayType=(TextView) rowView.findViewById(R.id.lblPayType);
            viewHolder.lblPayType.setTypeface(font, Typeface.BOLD);

            viewHolder.pay=(TextView) rowView.findViewById(R.id.payType);
            viewHolder.pay.setTypeface(font, Typeface.BOLD);

            viewHolder.lblPayName=(TextView) rowView.findViewById(R.id.lblPayName);
            viewHolder.lblPayName.setTypeface(font, Typeface.BOLD);

            viewHolder.payName=(TextView) rowView.findViewById(R.id.payName);
            viewHolder.payName.setTypeface(font, Typeface.BOLD);

            viewHolder.status=(TextView) rowView.findViewById(R.id.lblStatus);
            viewHolder.status.setTypeface(font, Typeface.BOLD);

            viewHolder.lblAmount=(TextView) rowView.findViewById(R.id.lblAmount);
            viewHolder.lblAmount.setTypeface(font, Typeface.BOLD);

            viewHolder.amount=(TextView) rowView.findViewById(R.id.amount);
            viewHolder.amount.setTypeface(font, Typeface.BOLD);

            rowView.setTag(viewHolder);
        }

        final ViewHolder holder = (ViewHolder) rowView.getTag();
        WalletHistoryBean hist= mHistory.get(position);

        holder.trans.setText(String.valueOf(hist.getTransactionId()));
        holder.date.setText(hist.getDate());
        holder.pay.setText(hist.getPayType());
        holder.payName.setText(hist.getPayName());

        if(hist.getStatus().getStatusCode() != 400){
            holder.parent.setBackgroundResource(R.drawable.border_red);
            holder.status.setBackgroundResource(R.color.appRed);
            holder.status.setText("FAILED");
            holder.amount.setText("₦"+String.valueOf(hist.getAmount()));
            holder.amount.setTextColor(ContextCompat.getColor(context,R.color.appRed));
            holder.amount.setPaintFlags(holder.amount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else if(hist.getStatus().getStatusCode() == 400){
            holder.parent.setBackgroundResource(R.drawable.border_green);
            holder.status.setBackgroundResource(R.color.appGreen);
            holder.status.setText("SUCCEEDED");
            holder.amount.setText("₦"+String.valueOf(hist.getAmount()));
            holder.amount.setTextColor(ContextCompat.getColor(context,R.color.appGreen));
            holder.amount.setPaintFlags(holder.amount.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        return rowView;
    }

    // Filter Class
    public void filter(String charText) {
        if(! addedList){
            this.tempList = new ArrayList<>();
            this.tempList.addAll(mHistory);
            addedList=true;
        }

        charText = charText.toLowerCase(Locale.getDefault());
        mHistory.clear();
        if (charText.length() == 0) {
            mHistory.addAll(tempList);
            addedList=false;
        }
        else
        {
            for (WalletHistoryBean wHB : tempList)
            {
                if (wHB.getPayName().toLowerCase(Locale.getDefault()).contains(charText)){
                    mHistory.add(wHB);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder {
       public TextView lblTrans;
        public TextView trans;
        public TextView lblDate;
        public TextView date;
        public TextView lblPayType;
        public TextView pay;
        public TextView lblPayName;
        public TextView payName;

        public TextView status;
        public TextView lblAmount;
        public TextView amount;

        public RelativeLayout parent;
    }
}
