package utilities;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.oltranz.airtime.airtime.R;

import java.util.List;

import utilities.utilitiesbeans.TopUpNumber;

/**
 * Created by Owner on 7/13/2016.
 */
public class MultipleSellAdapter extends ArrayAdapter<TopUpNumber>  {
    private String tag="AirTime: "+getClass().getSimpleName();
    private Typeface font;
    private Activity context;
    private List<TopUpNumber> numbers;
    public MultipleSellAdapter(Activity context, List<TopUpNumber> numbers) {
        super(context, R.layout.multiplesellstyle, numbers);
        this.context=context;
        this.numbers=numbers;
        font = Typeface.createFromAsset(context.getAssets(), "font/ubuntu.ttf");
    }

    public View getView(final int position,View view,ViewGroup parent) {
        View rowView = view;
        TopUpNumber topUpNumber=numbers.get(position);
        // reuse views
        if (rowView == null) {
            Log.d(tag,"Row Number Initiated: "+topUpNumber.getMsisdn());
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.multiplesellstyle, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.telNumber = (TextView) rowView.findViewById(R.id.tel);

            viewHolder.fillAmount=(EditText) rowView.findViewById(R.id.amount);
            viewHolder.fillAmount.setTypeface(font);

//            viewHolder.remove = (ImageView) rowView.findViewById(R.id.remove);
//            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.telNumber.setTypeface(font);
        holder.telNumber.setText(topUpNumber.getMsisdn());

        holder.fillAmount.setTypeface(font);
        holder.fillAmount.setText("");
//        holder.fillAmount.append(String.valueOf(topUpNumber.getAmount()));

        Log.d(tag,"Row Number: "+topUpNumber.getMsisdn());
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numbers.remove(position);
                MultipleSellAdapter.this.notifyDataSetChanged();
            }
        });
//        LayoutInflater inflater=context.getLayoutInflater();
//        View rowView=inflater.inflate(R.layout.multiplesellstyle, null, true);
//
//        TextView tel = (TextView) rowView.findViewById(R.id.tel);
//        tel.setTypeface(font);
//        EditText amount=(EditText) rowView.findViewById(R.id.amount);
//        amount.setTypeface(font);
//        ImageView remove=(ImageView) rowView.findViewById(R.id.remove);
//
//        tel.setText(numbers.get(position));
//
//        remove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                numbers.remove(position);
//                MultipleSellAdapter.this.notifyDataSetChanged();
//            }
//        });

        TextView tv=(TextView) rowView.findViewById(R.id.tel);
        return rowView;
    }

    @Override
    public TopUpNumber getItem(int position) {
        return numbers.get(position);
    }

    static class ViewHolder {
        public TextView telNumber;
        public EditText fillAmount;
        public ImageView remove;
    }
}
