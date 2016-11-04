package utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.oltranz.mobilea.mobilea.R;

import java.util.List;

import utilities.utilitiesbeans.TopUpNumber;


/**
 * Created by Hp on 9/13/2016.
 */
public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder> {

    private Context mContext;
    private List<TopUpNumber> mData;
    private static RecycleClickListener clickListener;

    public RecycleViewAdapter(Context mContext, List<TopUpNumber> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.multiplesellstyle, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TopUpNumber topUpNumber=mData.get(position);

        holder.tel.setText(topUpNumber.getMsisdn());

        if(topUpNumber.getAmount()>0)
            holder.amount.setText(""+topUpNumber.getAmount());
        else
            holder.amount.setText("");
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView reset;
        public ImageButton getNumber;
        public EditText tel,amount;

        public MyViewHolder(View view) {
            super(view);
            reset=(TextView) view.findViewById(R.id.reset);
            getNumber=(ImageButton) view.findViewById(R.id.getNumber);
            tel=(EditText) view.findViewById(R.id.tel);
            amount=(EditText) view.findViewById(R.id.amount);

            //setting click listener
            reset.setOnClickListener(this);
            getNumber.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onRecycleItemClick(getAdapterPosition(), v);
        }
    }

    public void addItem(TopUpNumber dataObj, int index) {
        mData.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mData.remove(index);
        notifyItemRemoved(index);
    }


    public void setOnItemClickListener(RecycleClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface RecycleClickListener {
        public void onRecycleItemClick(int position, View v);
    }
}
