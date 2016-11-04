package fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.oltranz.mobilea.mobilea.R;

import client.ClientData;
import client.ClientServices;
import client.ServerClient;
import config.BaseUrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import simplebeans.SimpleStatusBean;
import simplebeans.balancebeans.BalanceResponse;
import utilities.CheckWalletBalance;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CheckBalanceInteraction} interface
 * to handle interaction events.
 * Use the {@link CheckBalance#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckBalance extends Fragment implements CheckWalletBalance.CheckWalletBalanceInteraction {
    private String tag = "AirTime: " + getClass().getSimpleName();

    private CheckBalanceInteraction balanceInteraction;
    private Typeface font;
    private String token;
    private String msisdn;
    private String accountBalance;
    private CheckWalletBalance checkWalletBalance;
    private String loginBalance, loginWalletDate;
//    private ProgressDialog progressDialog;

    public CheckBalance() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param token   Parameter 1.
     * @param msisdn  Parameter 2.
     * @param balance Parameter 3.
     * @return A new instance of fragment CheckBalance.
     */
    public static CheckBalance newInstance(String token, String msisdn, String balance) {
        Log.d("AirTime: Login", "New Instance is creating");
        CheckBalance fragment = new CheckBalance();
        Bundle args = new Bundle();
        args.putString("token", token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (getArguments() != null) {
            if (getArguments().getString("token") != null)
                token = getArguments().getString("token");
            if (getArguments().getString("msisdn") != null)
                msisdn = getArguments().getString("msisdn");
            if (getArguments().getString("walletBalance") != null)
                loginBalance = getArguments().getString("walletBalance");
            if (getArguments().getString("walletBalanceDate") != null)
                loginWalletDate = getArguments().getString("walletBalanceDate");
        }
//        progressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
//        progressDialog.setIndeterminate(false);
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.setCancelable(true);
        checkWalletBalance = new CheckWalletBalance(this, getContext(), token);
        Log.d(tag, "The fragment is created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(tag, "The fragment view are being created");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.checkbalancelayout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(tag, "View are finally inflated");
        final TextView nSign = (TextView) view.findViewById(R.id.nairaSign);
        nSign.setTypeface(font);
        final TextView lastHistory = (TextView) view.findViewById(R.id.lastHistory);
        lastHistory.setTypeface(font);
        lastHistory.setText("Latest Account Balance: "+loginWalletDate);
        final TextView balance = (TextView) view.findViewById(R.id.balance);
        balance.setTypeface(font, Typeface.BOLD);
        balance.setText(loginBalance);
        final TextView label = (TextView) view.findViewById(R.id.label);
        label.setTypeface(font, Typeface.BOLD);

        Log.d(tag, "Data to push to the server:\n" + BaseUrl.checkBalanceUrl + "/" + msisdn);

        //Dummy data
        // loginListener.onLoginInteraction(200, "Success", null);
//
//        //making a Balance request
//        try {
//            progressDialog.setMessage("Checking wallet balance...");
//            progressDialog.show();
//
//            ClientServices clientServices = ServerClient.getClient().create(ClientServices.class);
//            Call<BalanceResponse> callService = clientServices.getWalletBalance(token);
//            callService.enqueue(new Callback<BalanceResponse>() {
//                @Override
//                public void onResponse(Call<BalanceResponse> call, Response<BalanceResponse> response) {
//                    if (progressDialog != null)
//                        if (progressDialog.isShowing())
//                            progressDialog.dismiss();
//                    //HTTP status code
//                    int statusCode = response.code();
//                    if (statusCode == 200) {
//                        Log.d(tag, "Data from the server:\n" + new ClientData().mapping(response.body()));
//                        try {
//                            BalanceResponse balanceResponse = response.body();
//                            //handle the response from the server
//                            if (response.body() != null) {
//                                String mDate="N/A";
//                                balance.setText(balanceResponse.getBalance());
//                                balanceInteraction.onCheckBalanceInteraction(200, balanceResponse.getStatus().getMessage(), balanceResponse);
//                                if (balanceResponse.getLastTxTime() != null)
//                                    mDate = balanceResponse.getLastTxTime();
//                                lastHistory.setText("Latest Account Balance: " + mDate);
//                            } else {
//                                checkBalanceComponent();
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            checkBalanceComponent();
//                        }
//                    } else {
//                        balanceInteraction.onCheckBalanceInteraction(403, "Faillure", null);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<BalanceResponse> call, Throwable t) {
//                    if (progressDialog != null)
//                        if (progressDialog.isShowing())
//                            progressDialog.dismiss();
//                    Log.e(tag, t.toString());
//                    checkBalanceComponent();
//                }
//            });
//        } catch (Exception e) {
//            if (progressDialog != null)
//                if (progressDialog.isShowing())
//                    progressDialog.dismiss();
//            e.printStackTrace();
//            checkBalanceComponent();
//        }

//        try{
//            checkBalanceComponent();
//        }catch (Exception e){
//            e.printStackTrace();
//            balanceInteraction.onCheckBalanceInteraction(403, "Faillure", null);
//        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CheckBalanceInteraction) {
            balanceInteraction = (CheckBalanceInteraction) context;
            font = Typeface.createFromAsset(context.getAssets(), "font/ubuntu.ttf");
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CheckBalanceInteraction");
        }
//        if (progressDialog != null)
//            if (progressDialog.isShowing())
//                progressDialog.dismiss();
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        if (progressDialog != null)
//            if (progressDialog.isShowing())
//                progressDialog.dismiss();
        balanceInteraction = null;
    }

    public void checkBalanceComponent() {
//        if (progressDialog != null)
//            if (progressDialog.isShowing())
//                progressDialog.dismiss();
        checkWalletBalance.getBalance();
    }

    @Override
    public void onWalletBalanceCheck(String balance, String date) {
//        if (progressDialog != null)
//            if (progressDialog.isShowing())
//                progressDialog.dismiss();

        if(balanceInteraction == null)
            return;

        String mDate="00/00/000 00:00";
        if(date != null && (!TextUtils.isEmpty(date)))
            mDate=date;

        if (!TextUtils.isEmpty(balance))
            if (Double.valueOf(balance) > 0) {
                BalanceResponse balResponse = new BalanceResponse(balance, mDate, new SimpleStatusBean("Success", 400));
                balanceInteraction.onCheckBalanceInteraction(200, balResponse.getStatus().getMessage(), balResponse);
                try {
                    final TextView lastHistory = (TextView) getView().findViewById(R.id.lastHistory);
                    lastHistory.setTypeface(font);
                    lastHistory.setText("Latest Account Balance: "+mDate);

                    final TextView balanceView = (TextView) getView().findViewById(R.id.balance);
                    balanceView.setTypeface(font, Typeface.BOLD);
                    balanceView.setText(balance);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else
                balanceInteraction.onCheckBalanceInteraction(500, "Failed to load wallet Balance", null);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface CheckBalanceInteraction {
        void onCheckBalanceInteraction(int statusCode, String message, Object object);
    }
}
