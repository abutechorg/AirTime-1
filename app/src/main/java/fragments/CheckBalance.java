package fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.oltranz.airtime.airtime.R;

import java.util.Date;
import java.util.logging.SimpleFormatter;
import java.text.SimpleDateFormat;

import client.ClientData;
import client.ClientServices;
import client.ServerClient;
import config.BaseUrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import simplebeans.balancebeans.BalanceRespopnse;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CheckBalanceInteraction} interface
 * to handle interaction events.
 * Use the {@link CheckBalance#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckBalance extends Fragment {
    private String tag="AirTime: "+getClass().getSimpleName();

    private CheckBalanceInteraction balanceInteraction;
    private Typeface font;
    private String token;
    private String msisdn;
    private String accountBalance;

    public CheckBalance() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param token Parameter 1.
     * @param msisdn Parameter 2.
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
            if(getArguments().getString("token") != null)
                token = getArguments().getString("token");
            if(getArguments().getString("msisdn") != null)
                msisdn = getArguments().getString("msisdn");
            if(getArguments().getString("accountBalance") != null)
                accountBalance =getArguments().getString("accountBalance");
        }
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
        final TextView nSign=(TextView) view.findViewById(R.id.nairaSign);
        nSign.setTypeface(font);
        nSign.setTypeface(nSign.getTypeface(), Typeface.BOLD);
        final TextView lastHistory=(TextView) view.findViewById(R.id.lastHistory);
        lastHistory.setTypeface(font);
        final TextView balance=(TextView) view.findViewById(R.id.balance);
        balance.setTypeface(font);
        balance.setTypeface(balance.getTypeface(), Typeface.BOLD);

        if(accountBalance != null)
            balance.setText(accountBalance);

        Log.d(tag, "Data to push to the server:\n" + BaseUrl.checkBalanceUrl+"/"+msisdn);

        //Dummy data
        // loginListener.onLoginInteraction(200, "Success", null);

        //making a Balance request
        try {
            ClientServices clientServices = ServerClient.getClient().create(ClientServices.class);
            Call<BalanceRespopnse> callService = clientServices.getWalletBalance(msisdn);
            callService.enqueue(new Callback<BalanceRespopnse>() {
                @Override
                public void onResponse(Call<BalanceRespopnse> call, Response<BalanceRespopnse> response) {

                    //HTTP status code
                    int statusCode = response.code();
                    BalanceRespopnse balanceRespopnse = response.body();
                    try{
                        //handle the response from the server
                        balance.setText(String.valueOf(balanceRespopnse.getNewBalance()));
                        String mDate=balanceRespopnse.getTransTime().toString();
                        try{

                            SimpleDateFormat sdf=new SimpleDateFormat("E, dd-MM-yyyy, HH:mm:ss");
                            mDate=sdf.format(balanceRespopnse.getTransTime());

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        lastHistory.append(mDate);
                        Log.d(tag, "Data from the server:\n" + new ClientData().mapping(balanceRespopnse));
                    }catch (Exception e){
                        e.printStackTrace();
                        balanceInteraction.onCheckBalanceInteraction(500,e.getMessage(),null);
                    }
                }

                @Override
                public void onFailure(Call<BalanceRespopnse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(tag, t.toString());
                    balanceInteraction.onCheckBalanceInteraction(500,t.getMessage(),null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            balanceInteraction.onCheckBalanceInteraction(500, e.getMessage(), null);
        }
    }

//    public void onButtonPressed(Uri uri) {
//        if (balanceInteraction != null) {
//            balanceInteraction.onFavoriteInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CheckBalanceInteraction) {
            balanceInteraction = (CheckBalanceInteraction) context;
            font = Typeface.createFromAsset(context.getAssets(), "font/ubuntu.ttf");
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FavoriteInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        balanceInteraction = null;
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
