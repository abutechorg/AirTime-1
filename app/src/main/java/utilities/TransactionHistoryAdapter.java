package utilities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oltranz.mobilea.mobilea.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import simplebeans.transactionhistory.TransactionHistoryBean;

/**
 * Created by Owner on 8/19/2016.
 */
public class TransactionHistoryAdapter extends ArrayAdapter<TransactionHistoryBean> {

    private String tag = "AirTime: " + getClass().getSimpleName();
    private Typeface font;
    private Activity context;
    private List<TransactionHistoryBean> mHistory;
    private List<TransactionHistoryBean> tempList;
    private boolean addedList=false;
    private TransactionHistoryAdapterInteraction onResendClick;

    public TransactionHistoryAdapter(TransactionHistoryAdapterInteraction onResendClick, Activity context, List<TransactionHistoryBean> mHistory) {
        super(context, R.layout.transactionhistory_style, mHistory);
        this.onResendClick=onResendClick;
        this.context = context;
        this.mHistory = mHistory;
        this.tempList = new ArrayList<>();
        this.tempList.addAll(mHistory);
        font = Typeface.createFromAsset(context.getAssets(), "font/ubuntu.ttf");
    }

    @TargetApi(Build.VERSION_CODES.M)
    public View getView(final int position, View view, ViewGroup parent) {
        View rowView = view;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.transactionhistory_style, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.parent = (RelativeLayout) rowView.findViewById(R.id.transactionStyleParent);

            viewHolder.rightHolder = (LinearLayout) rowView.findViewById(R.id.rightHolder);

            viewHolder.lblTrans = (TextView) rowView.findViewById(R.id.lblTransaction);
            viewHolder.lblTrans.setTypeface(font, Typeface.BOLD);

            viewHolder.trans = (TextView) rowView.findViewById(R.id.transactionId);
            viewHolder.trans.setTypeface(font, Typeface.BOLD);

            viewHolder.lblDate = (TextView) rowView.findViewById(R.id.lblDate);
            viewHolder.lblDate.setTypeface(font, Typeface.BOLD);

            viewHolder.date = (TextView) rowView.findViewById(R.id.date);
            viewHolder.date.setTypeface(font, Typeface.BOLD);

            viewHolder.lblMsisdn = (TextView) rowView.findViewById(R.id.lblMsisdn);
            viewHolder.lblMsisdn.setTypeface(font, Typeface.BOLD);

            viewHolder.msisdn = (TextView) rowView.findViewById(R.id.histMsisdn);
            viewHolder.msisdn.setTypeface(font, Typeface.BOLD);

//            viewHolder.actionIcon=(ImageView) rowView.findViewById(R.id.actionIcon);

            viewHolder.lblAction = (TextView) rowView.findViewById(R.id.lblAction);
            viewHolder.lblAction.setTypeface(font, Typeface.BOLD);

            viewHolder.status = (TextView) rowView.findViewById(R.id.lblStatus);
            viewHolder.status.setTypeface(font, Typeface.BOLD);

            viewHolder.lblAmount = (TextView) rowView.findViewById(R.id.lblAmount);
            viewHolder.lblAmount.setTypeface(font, Typeface.BOLD);

            viewHolder.amount = (TextView) rowView.findViewById(R.id.amount);
            viewHolder.amount.setTypeface(font, Typeface.BOLD);

            rowView.setTag(viewHolder);
        }

        final ViewHolder holder = (ViewHolder) rowView.getTag();
        final TransactionHistoryBean hist = mHistory.get(position);

        holder.date.setText(hist.getDate());
        holder.msisdn.setText(hist.getMsisdn());
        holder.amount.setText("₦" + String.valueOf(hist.getAmount()));

        if (hist.getStatus().getStatusCode() != 400) {
            holder.parent.setBackgroundResource(R.drawable.border_red);
            holder.rightHolder.setBackgroundResource(R.drawable.border_red);

            //holder.actionIcon.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_transactpage_failed));
            holder.lblAction.setTextColor(ContextCompat.getColorStateList(context, R.drawable.text_button_red));
            holder.lblAction.setBackground(ContextCompat.getDrawable(context, R.drawable.buttonstrokeoutred));
            holder.lblAction.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_transactpage_failed, 0, 0);
            holder.lblAction.setText("RETRY");

            holder.status.setBackgroundResource(R.color.appRed);
            holder.status.setText("FAILED");
        } else if (hist.getStatus().getStatusCode() == 400) {
            holder.parent.setBackgroundResource(R.drawable.border_green);
            holder.rightHolder.setBackgroundResource(R.drawable.border_green);

            //holder.actionIcon.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.icon_transactpage_successed));
            holder.lblAction.setTextColor(ContextCompat.getColorStateList(context, R.drawable.text_button_green));
            holder.lblAction.setBackground(ContextCompat.getDrawable(context, R.drawable.buttonstrokeoutgreen));
            holder.lblAction.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_transactpage_successed, 0, 0);
            if (versionManager() >= 23)
                holder.lblAction.setCompoundDrawableTintList(null);
            holder.lblAction.setText("RESEND");

            holder.status.setBackgroundResource(R.color.appGreen);
            holder.status.setText("SUCCEEDED");
        }
        holder.lblAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onResendClick.onResendAirtimeAdapter(hist.getMsisdn(),String.valueOf(hist.getAmount()));
            }
        });

        return rowView;
    }

    private int versionManager() {
        int currentVersion = 0;
        try {
            currentVersion = android.os.Build.VERSION.SDK_INT;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentVersion;
    }

    // Filter Class
    public void filter(String charText) {
        if(! addedList){
            this.tempList = new ArrayList<>();
            this.tempList.addAll(mHistory);
            addedList=true;
        }

        charText = charText.toLowerCase();
        mHistory.clear();
        if (charText.length() == 0) {
            mHistory.addAll(tempList);
            addedList=false;
        }
        else
        {
            for (TransactionHistoryBean transactionBean : tempList) {
                if (transactionBean.getSearchChain().toLowerCase(Locale.getDefault()).contains(charText)) {
                    mHistory.add(transactionBean);
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
        public TextView lblMsisdn;
        public TextView msisdn;
        //        public ImageView actionIcon;
        public TextView lblAction;

        public TextView status;
        public TextView lblAmount;
        public TextView amount;

        public LinearLayout rightHolder;
        public RelativeLayout parent;
    }

    public interface TransactionHistoryAdapterInteraction {
        void onResendAirtimeAdapter(String tel, String amount);
    }
}
