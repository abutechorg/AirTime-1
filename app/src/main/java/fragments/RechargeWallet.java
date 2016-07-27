package fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.oltranz.airtime.airtime.R;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import client.ClientData;
import client.ClientServices;
import client.ServerClient;
import config.DeviceIdentity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import simplebeans.balancebeans.BalanceRespopnse;
import simplebeans.loginbeans.LoginRequest;
import simplebeans.loginbeans.LoginResponse;
import simplebeans.payments.PaymentModeBean;
import simplebeans.payments.PaymentModesResponse;
import simplebeans.registerbeans.RegisterRequest;
import simplebeans.registerbeans.RegisterResponse;
import utilities.PaymentModeAdapter;
import utilities.TopUpConfirmAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RechargeWalletListener} interface
 * to handle interaction events.
 * Use the {@link RechargeWallet#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RechargeWallet extends Fragment {
    private String tag="AirTime: "+getClass().getSimpleName();
    private static final String tokenParam = "token";
    private static final String msisdnParam = "msisdn";

    private String token;
    private String msisdn;
    private Typeface font;
    private RechargeWalletListener onRechargeWallet;
    private PaymentModeAdapter adapter;
    private List<PaymentModeBean> paymentModeBeanList;

    public RechargeWallet() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param token Parameter 1.
     * @param msisdn Parameter 2.
     * @return A new instance of fragment RechargeWallet.
     */
    public static RechargeWallet newInstance(String token, String msisdn) {
        RechargeWallet fragment = new RechargeWallet();
        Bundle args = new Bundle();
        args.putString(tokenParam, token);
        args.putString(msisdnParam, msisdn);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (getArguments() != null) {
            if(getArguments().getString(tokenParam) != null)
                token = getArguments().getString(tokenParam);
            if(getArguments().getString(msisdnParam) != null)
                msisdn = getArguments().getString(msisdnParam);
        }
        Log.d(tag, "The fragment is created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(tag, "The fragment view are being created");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.rechargewallet_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            ClientServices clientServices = ServerClient.getClient().create(ClientServices.class);
            Call<PaymentModesResponse> callService = clientServices.getPaymentModes();
            callService.enqueue(new Callback<PaymentModesResponse>() {
                @Override
                public void onResponse(Call<PaymentModesResponse> call, Response<PaymentModesResponse> response) {

                    //HTTP status code
                    int statusCode = response.code();
                    try{
                        //handle the response from the server
                        PaymentModesResponse paymentModesResponse = response.body();
                        Log.d(tag, "Data from the server:\n" + new ClientData().mapping(paymentModesResponse));
                        if(paymentModesResponse.getResponseStatusSimpleBean().getStatusCode()== 400)
                            paymentList(paymentModesResponse);
                        else{
                            TextView tv=(TextView) getView().findViewById(R.id.tv);
                            tv.setVisibility(View.VISIBLE);
                            tv.setText(paymentModesResponse.getResponseStatusSimpleBean().getMessage());
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        onRechargeWallet.onRechargeWalletInteraction(500, e.getMessage(), msisdn, null);
                    }
                }

                @Override
                public void onFailure(Call<PaymentModesResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(tag, t.toString());
                    onRechargeWallet.onRechargeWalletInteraction(500, t.getMessage(), msisdn, null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            onRechargeWallet.onRechargeWalletInteraction(500, e.getMessage(), msisdn, null);
        }
        Log.d(tag, "View are finally inflated");
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RechargeWalletListener) {
            onRechargeWallet = (RechargeWalletListener) context;
            font = Typeface.createFromAsset(context.getAssets(), "font/ubuntu.ttf");
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement RechargeWalletListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onRechargeWallet = null;
    }

    private void paymentList(PaymentModesResponse paymentModesResponse){
        final EditText amountField=(EditText) getView().findViewById(R.id.amount);
        final GridView paymentsGrid=(GridView) getView().findViewById(R.id.paymentGrid);
        paymentModeBeanList=paymentModesResponse.getPaymentModeBeanList();
        adapter=new PaymentModeAdapter(getContext(),R.layout.payment_style,paymentModeBeanList);
        paymentsGrid.setAdapter(adapter);
        paymentsGrid.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

        paymentsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                paymentsGrid.requestFocusFromTouch();
                paymentsGrid.setSelection(position);
                TextView paymentName=(TextView) view.findViewById(R.id.paymentName);
                TextView paymentId=(TextView) view.findViewById(R.id.paymentId);
                Log.d(tag, "Payment=>\n amount to add: " + amountField.getText().toString() + " payemnt mode: " + paymentName.getText().toString() + "\n and Payment Id: " +paymentId.getText().toString());

                try{
                    double amount=Double.valueOf(amountField.getText().toString());
                    proceed(amount, paymentName.getText().toString(), Long.parseLong(paymentId.getText().toString()));
                    amountField.setText("");
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getContext(),"Invalid Amount",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void proceed(final double amount, final String paymentModeName, final long paymentModeId){

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        dialog.setContentView(R.layout.confirmrechargewallet);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        int dividerId = dialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
        if (dividerId != 0) {
            View divider = dialog.findViewById(dividerId);
            if(divider != null)
                divider.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.appOrange));
        }
        dialog.setTitle(Html.fromHtml("<font color='" + ContextCompat.getColor(getContext(), R.color.appOrange) + "'>Confirm...</font>"));
        dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_info);
        final TextView amountLabel=(TextView) dialog.findViewById(R.id.amount);
        final TextView accountLable=(TextView) dialog.findViewById(R.id.accountlbl);
        final TextView tv=(TextView) dialog.findViewById(R.id.tv);
        final EditText pin=(EditText) dialog.findViewById(R.id.pin);
        final Button cancel=(Button) dialog.findViewById(R.id.cancel);
        final Button ok=(Button) dialog.findViewById(R.id.ok);

        amountLabel.setText(amount+"");
        accountLable.setText(paymentModeName);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(pin.getText().toString())) {

                    //get current Time
                    DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                    String currentTime = df.format(Calendar.getInstance().getTime());

                    //get device Identity
                    DeviceIdentity deviceIdentity = new DeviceIdentity(getContext());
                    //get user Identity
                    LoginRequest loginRequest = new LoginRequest(pin.getText().toString(),
                            msisdn,
                            currentTime,
                            deviceIdentity.getSerialNumber(),
                            deviceIdentity.getImei(),
                            deviceIdentity.getVersion());


                    //Client data to send to the Sever
                    Log.d(tag, "Data to push to the server:\n" + new ClientData().mapping(loginRequest));

                    //making a Login request
                    try {
                        ClientServices clientServices = ServerClient.getClient().create(ClientServices.class);
                        Call<LoginResponse> callService = clientServices.loginUser(loginRequest);
                        callService.enqueue(new Callback<LoginResponse>() {
                            @Override
                            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                                //HTTP status code
                                int statusCode = response.code();
                                LoginResponse loginResponse = response.body();

                                //handle the response from the server
                                Log.d(tag, "Server Result:\n" + new ClientData().mapping(loginResponse));
                                if(loginResponse.getResponseStatusSimpleBean().getStatusCode() == 200){

                                    //making a transaction Request
                                    onWalletRechargeRequest(amount, paymentModeName, paymentModeId);

                                    dialog.dismiss();
                                }else{
                                    tv.setText(loginResponse.getResponseStatusSimpleBean().getMessage());
                                    pin.setText("");
                                }
                            }

                            @Override
                            public void onFailure(Call<LoginResponse> call, Throwable t) {
                                // Log error here since request failed
                                Log.e(tag, t.toString());
                                tv.setText(t.toString());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        tv.setText(e.getMessage());
                    }
                }else{
                    //Empty Pin
                    tv.setText("Fill your PIN");
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void onWalletRechargeRequest(final double amount, final String paymentModeName, final long paymentModeId){
        Toast.makeText(getContext(),"Recharge Wallet Under Construction",Toast.LENGTH_LONG).show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface RechargeWalletListener {
        void onRechargeWalletInteraction(int statusCode, String message, String msisdn, Object object);
    }
}
