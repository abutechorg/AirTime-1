package fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.oltranz.mobilea.mobilea.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import client.ClientServices;
import client.ServerClient;
import entity.NotificationTable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import simplebeans.transactionhistory.TransactionHistoryBean;
import simplebeans.transactionhistory.TransactionHistoryResponse;
import utilities.GetCurrentDate;
import utilities.ResendAirtime;
import utilities.TransactionHistoryAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TransactionHistoryInteraction} interface
 * to handle interaction events.
 * Use the {@link TransactionHistory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionHistory extends Fragment implements TransactionHistoryAdapter.TransactionHistoryAdapterInteraction, ResendAirtime.ResendAirtimeInteraction {
    private ProgressDialog progressDialog;
    private static final String msisdn_param = "msisdn";
    private static final String token_param = "token";
    private String tag = "AirTime: " + getClass().getSimpleName();
    private String msisdn;
    private String token;
    private Typeface font;
    private ListView mHistoryList;
    private EditText searchText;
    private ImageView searchButton;
    private TransactionHistoryAdapter adapter;

    private TransactionHistoryInteraction onTransactionHistory;

    public TransactionHistory() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param msisdn Parameter 1.
     * @param token  Parameter 2.
     * @return A new instance of fragment About.
     */
    public static TransactionHistory newInstance(String msisdn, String token) {
        Log.d("AirTime: TransHistory", "New Instance is creating");
        TransactionHistory fragment = new TransactionHistory();
        Bundle args = new Bundle();
        args.putString(msisdn_param, msisdn);
        args.putString(token_param, token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (getArguments() != null) {
            if (getArguments().getString(token_param) != null)
                token = getArguments().getString(token_param);
            if (getArguments().getString(msisdn_param) != null)
                msisdn = getArguments().getString(msisdn_param);
        }
        Log.d(tag, "The fragment is created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(tag, "The fragment view are being created");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.transaction_history_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(tag, "View are finally inflated");
        mHistoryList = (ListView) view.findViewById(R.id.mHistory);
        searchText = (EditText) view.findViewById(R.id.searchTerm);
        searchButton = (ImageView) view.findViewById(R.id.searchIcon);
        final List<TransactionHistoryBean> mHistory = new ArrayList<>();

        adapter = new TransactionHistoryAdapter(this, getActivity(), mHistory);
        mHistoryList.setAdapter(adapter);

        progressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading History...");
        progressDialog.show();

        try {
            ClientServices clientServices = ServerClient.getClient().create(ClientServices.class);
            Call<TransactionHistoryResponse> callService = clientServices.getTransactionHistory(token, 10);
            callService.enqueue(new Callback<TransactionHistoryResponse>() {
                @Override
                public void onResponse(Call<TransactionHistoryResponse> call, Response<TransactionHistoryResponse> response) {

                    //HTTP status code
                    int statusCode = response.code();
                    if(statusCode==200){
                        TransactionHistoryResponse transactionHistoryResponse=response.body();
                        try{
                            if(transactionHistoryResponse.getStatus().getStatusCode() != 400)
                                progressDialog.setMessage(transactionHistoryResponse.getStatus().getMessage());
                            else{
                                List<TransactionHistoryBean> transactionHistoryBeanList=transactionHistoryResponse.getHistory();
                                for(TransactionHistoryBean historyBean: transactionHistoryBeanList){
                                    TransactionHistoryBean wHistoryBean = new TransactionHistoryBean(historyBean.getDate(), historyBean.getAmount(), historyBean.getMsisdn(), historyBean.getStatus(),""+historyBean.getStatus().getMessage()+historyBean.getDate()+ historyBean.getAmount()+ historyBean.getMsisdn());
                                    mHistory.add(wHistoryBean);
                                    adapter.notifyDataSetChanged();
                                }

                                progressDialog.dismiss();
                            }
                        }catch (Exception e){
                            progressDialog.setMessage(e.getMessage());
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }

                    }else{
                        progressDialog.setMessage(response.message());
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<TransactionHistoryResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(tag, t.toString());
                    progressDialog.setMessage("Connectivity Error");
                    progressDialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.setMessage(e.getMessage());
            progressDialog.dismiss();
        }

        searchText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                //WalletHistory.this.adapter.getFilter().filter(cs);

                String text = searchText.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TransactionHistoryInteraction) {
            onTransactionHistory = (TransactionHistoryInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TransactionHistoryInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onTransactionHistory = null;
    }

    @Override
    public void onResendAirtimeAdapter(String tel, String amount) {
        ResendAirtime resendAirtime=new ResendAirtime(this,getActivity(),token,msisdn);
        resendAirtime.startResend(tel,Double.valueOf(amount));
    }

    @Override
    public void onResendAirtimeModule(int statusCode, String message) {
        String date = new GetCurrentDate(getContext()).getServerDate();
        if (!TextUtils.isEmpty(message)) {
            NotificationTable notificationTable = new NotificationTable(date, "AirTime TopUp " + message, msisdn, "0");
            notificationTable.save();
        }
        uiFeed(""+message);
    }

    private void uiFeed(String message) {
        if(progressDialog != null)
            if(progressDialog.isShowing())
                progressDialog.dismiss();
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(message)
                    .setTitle(R.string.dialog_title);
            // Add the buttons
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();


            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface TransactionHistoryInteraction {

        void onTransactionHistoryInteraction(int statusCode, String message, Object object);
    }
}
