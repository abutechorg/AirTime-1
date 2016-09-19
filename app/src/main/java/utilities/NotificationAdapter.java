package utilities;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oltranz.airtime.airtime.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import simplebeans.notifications.NotificationBean;

/**
 * Created by Owner on 8/19/2016.
 */
public class NotificationAdapter extends ArrayAdapter<NotificationBean> {

    private String tag = "AirTime: " + getClass().getSimpleName();
    private Typeface font;
    private Activity context;
    private List<NotificationBean> mNotifications;
    private List<NotificationBean> tempList;

    public NotificationAdapter(Activity context, List<NotificationBean> mNotifications) {
        super(context, R.layout.notification_style, mNotifications);

        this.context = context;
        this.mNotifications = mNotifications;
        this.tempList = new ArrayList<>();
        this.tempList.addAll(mNotifications);
        font = Typeface.createFromAsset(context.getAssets(), "font/ubuntu.ttf");
    }

    public View getView(final int position, View view, ViewGroup parent) {
        View rowView = view;
        Log.d(tag, "Row Number Initiated: " + mNotifications.get(position).getDate());
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.notification_style, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.parent = (RelativeLayout) rowView.findViewById(R.id.parentContainer);

            viewHolder.lblDate = (TextView) rowView.findViewById(R.id.lblDate);
            viewHolder.lblDate.setTypeface(font);

            viewHolder.date = (TextView) rowView.findViewById(R.id.date);
            viewHolder.date.setTypeface(font);

            viewHolder.lblMessage = (TextView) rowView.findViewById(R.id.lblMessage);
            viewHolder.lblMessage.setTypeface(font);

            viewHolder.message = (TextView) rowView.findViewById(R.id.message);
            viewHolder.message.setTypeface(font);

            rowView.setTag(viewHolder);
        }

        final ViewHolder holder = (ViewHolder) rowView.getTag();
        NotificationBean notif = mNotifications.get(position);

        holder.date.setText(notif.getDate());
        holder.message.setText(notif.getMessage());

        return rowView;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mNotifications.clear();
        if (charText.length() == 0) {
            mNotifications.addAll(tempList);
        } else {
            for (NotificationBean nb : tempList) {
                if (nb.getDate().toLowerCase(Locale.getDefault()).contains(charText)) {
                    mNotifications.add(nb);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder {
        public TextView lblDate;
        public TextView date;
        public TextView lblMessage;
        public TextView message;

        public RelativeLayout parent;
    }
}
