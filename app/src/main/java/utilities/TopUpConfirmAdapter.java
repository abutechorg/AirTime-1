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

import utilities.utilitiesbeans.TopUpNumber;

/**
 * Created by Owner on 7/20/2016.
 */
public class TopUpConfirmAdapter extends ArrayAdapter<TopUpNumber> {
    private String tag="AirTime: "+getClass().getSimpleName();
    private Typeface font;
    private Activity context;
    private List<TopUpNumber> numbers;
    public TopUpConfirmAdapter(Activity context, List<TopUpNumber> numbers) {
        super(context, R.layout.topupconfirm_style, numbers);
        this.context=context;
        this.numbers=numbers;
        font = Typeface.createFromAsset(context.getAssets(), "font/ubuntu.ttf");
    }

    public View getView(final int position,View view,ViewGroup parent) {
        View rowView = view;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.topupconfirm_style, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.telNumber = (TextView) rowView.findViewById(R.id.tel);

            viewHolder.fillAmount=(TextView) rowView.findViewById(R.id.amount);
            viewHolder.fillAmount.setTypeface(font);

            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        TopUpNumber topUpNumber=numbers.get(position);

        holder.telNumber.setTypeface(font);
        holder.telNumber.setText(topUpNumber.getMsisdn());

        holder.fillAmount.setTypeface(font);
        holder.fillAmount.setText(String.valueOf(topUpNumber.getAmount()));
        Log.d(tag,"Row Number: "+topUpNumber.getMsisdn());

//        TextView tv=(TextView) rowView.findViewById(R.id.tel);
        Log.d(tag, "UI Number: " + holder.telNumber.getText().toString());
        return rowView;
    }

    static class ViewHolder {
        public TextView telNumber;
        public TextView fillAmount;
    }
}
