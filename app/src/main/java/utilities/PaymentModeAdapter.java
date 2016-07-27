package utilities;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.oltranz.airtime.airtime.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import simplebeans.payments.PaymentModeBean;

/**
 * Created by Owner on 7/25/2016.
 */
public class PaymentModeAdapter extends ArrayAdapter<PaymentModeBean> {
    private String tag="AirTime: "+getClass().getSimpleName();
    private Context context;
    private int layoutResourceId;
    private List<PaymentModeBean> paymentGrid = new ArrayList<PaymentModeBean>();

    public PaymentModeAdapter(Context context, int layoutResourceId, List<PaymentModeBean> paymentGrid) {
        super(context, layoutResourceId, paymentGrid);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.paymentGrid = paymentGrid;
        Log.d(tag,"Adapter is initiated");
    }


    /**
     * Updates grid data and refresh grid items.
     * @param paymentGrid
     */
    public void setGridData(List<PaymentModeBean> paymentGrid) {
        this.paymentGrid = paymentGrid;
        notifyDataSetChanged();
        Log.d(tag, "Adapter state changed");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(tag,"Adapter Row view is initiating");
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.paymentNameHolder = (TextView) row.findViewById(R.id.paymentName);
            holder.paymentIconHolder = (ImageView) row.findViewById(R.id.paymentIcon);
            holder.paymentIdHolder=(TextView) row.findViewById(R.id.paymentId);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        PaymentModeBean paymentModeBean = paymentGrid.get(position);
        holder.paymentNameHolder.setText(paymentModeBean.getTransactionSource());
        holder.paymentIdHolder.setText(String.valueOf(paymentModeBean.getId()));
        holder.paymentIconHolder.setImageResource(R.drawable.ic_action_ic_icon_main_naira);

        if(paymentModeBean.getIconUrl() != null && (!TextUtils.isEmpty(paymentModeBean.getIconUrl()))){
            Log.d(tag, "Adapter downloading payment icon");
            try{
                Picasso.with(context).load(paymentModeBean.getIconUrl()).into(holder.paymentIconHolder);
            }catch (Exception e){
                e.printStackTrace();
                holder.paymentIconHolder.setImageResource(R.drawable.ic_action_ic_icon_main_naira);
                Log.e(tag, "Adapter downloading payment icon failed");
            }
        }
        return row;
    }

    static class ViewHolder {
        TextView paymentNameHolder;
        TextView paymentIdHolder;
        ImageView paymentIconHolder;
    }
}
