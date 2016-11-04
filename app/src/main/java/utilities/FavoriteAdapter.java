package utilities;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.oltranz.mobilea.mobilea.R;

import java.util.List;

import simplebeans.favorite.FavoriteResponseBean;

/**
 * Created by Owner on 8/19/2016.
 */
public class FavoriteAdapter extends ArrayAdapter<FavoriteResponseBean> {

    private String tag="AirTime: "+getClass().getSimpleName();
    private Typeface font;
    private Activity context;
    private List<FavoriteResponseBean> mFavorite;
    private FavoritesAdapterInteraction onResend;

    public FavoriteAdapter(FavoritesAdapterInteraction onResend, Activity context, List<FavoriteResponseBean> mFavorite){
        super(context, R.layout.favorite_style, mFavorite);
        this.onResend=onResend;
        this.context=context;
        this.mFavorite=mFavorite;
        font = Typeface.createFromAsset(context.getAssets(), "font/ubuntu.ttf");
    }

    public View getView(final int position, View view, ViewGroup parent) {
        View rowView = view;
        // reuse views
        if (rowView == null) {
            Log.d(tag, "Row Number Initiated: " + mFavorite.get(position).getMsisdn());
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.favorite_style, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.telNumber = (TextView) rowView.findViewById(R.id.favId);
            viewHolder.telNumber.setTypeface(font);

            viewHolder.fillAmount=(TextView) rowView.findViewById(R.id.amount);
            viewHolder.fillAmount.setTypeface(font);

            viewHolder.resend=(TextView) rowView.findViewById(R.id.resend);
            viewHolder.resend.setTypeface(font, Typeface.BOLD);

            rowView.setTag(viewHolder);
        }

        final ViewHolder holder = (ViewHolder) rowView.getTag();
        FavoriteResponseBean fav=mFavorite.get(position);

        holder.telNumber.setTypeface(font);
        holder.telNumber.setText(fav.getMsisdn());

        holder.fillAmount.setTypeface(font);
        holder.fillAmount.setText(String.valueOf(fav.getAmount()));

        holder.resend.setTypeface(font, Typeface.BOLD);
        holder.resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResend.onResendAirtimeAdapter(holder.telNumber.getText().toString(), holder.fillAmount.getText().toString());
            }
        });

        Log.d(tag,"UI Number: "+holder.telNumber.getText().toString());
        return rowView;
    }

    static class ViewHolder {
        public TextView telNumber;
        public TextView fillAmount;
        public TextView resend;
    }

    public interface FavoritesAdapterInteraction {
        void onResendAirtimeAdapter(String tel, String amount);
    }
}
