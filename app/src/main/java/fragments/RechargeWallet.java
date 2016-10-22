package fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.interswitchng.sdk.model.RequestOptions;
import com.interswitchng.sdk.payment.IswCallback;
import com.interswitchng.sdk.payment.android.inapp.PayWithCard;
import com.interswitchng.sdk.payment.android.util.Util;
import com.interswitchng.sdk.payment.model.PurchaseResponse;
import com.oltranz.mobilea.mobilea.R;

import java.util.List;
import java.util.Locale;

import client.ClientData;
import client.PaymentInterface;
import client.PaymentServerClient;
import config.Interswitching;
import config.MPay;
import config.StatusConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import simplebeans.StatusUsage;
import simplebeans.payments.PaymentModeBean;
import simplebeans.walletpayment.ConfirmWalletPayment;
import simplebeans.walletpayment.FailedPayment;
import simplebeans.walletpayment.InitiateResponse;
import simplebeans.walletpayment.InitiateWalletRecharge;
import utilities.PaymentModeAdapter;

//__________________________Inter switch


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RechargeWalletListener} interface
 * to handle interaction events.
 * Use the {@link RechargeWallet#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RechargeWallet extends Fragment implements View.OnClickListener {
    private static final String tokenParam = "token";
    private static final String msisdnParam = "msisdn";
    private String tag = "AirTime: " + getClass().getSimpleName();
    private String token;
    private String msisdn;
    private Typeface font;
    private RechargeWalletListener onRechargeWallet;
    private PaymentModeAdapter adapter;
    private List<PaymentModeBean> paymentModeBeanList;
    private ProgressDialog progressDialog;

    //____________________InterSwitch________________\\

    private ImageView visa;
    private ImageView mc;
    private ImageView verve;
    private ImageView other;

    private EditText customerID;
    private EditText amount;
    private EditText pan;
    private EditText pin;
    private EditText expiry;
    private EditText cvv2;
    private Context context;

    private Button payNow;
    private String paymentId;
    private String transactionIdentifier;
    private String authData;
    private WebView webView;

    private RequestOptions options;

    public RechargeWallet() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param token  Parameter 1.
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
            if (getArguments().getString(tokenParam) != null)
                token = getArguments().getString(tokenParam);
            if (getArguments().getString(msisdnParam) != null)
                msisdn = getArguments().getString(msisdnParam);


//            Passport.overrideApiBase(Passport.QA_API_BASE);//used to override the payment api base url.
//            Payment.overrideApiBase(Payment.QA_API_BASE);// used to override the payment api base url.
//        Payment.overrideApiBase(Payment.QA_API_BASE);
//        Passport.overrideApiBase(Passport.QA_API_BASE);


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

        progressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        amount = (EditText) view.findViewById(R.id.amount);
        amount.setTypeface(font);
        amount.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                //WalletHistory.this.adapter.getFilter().filter(cs);
                try {
                    if (amount.getText().toString().length() > 0) {
                        if (Double.valueOf(amount.getText().toString().trim().toLowerCase(Locale.getDefault())) < 50) {
                            amount.setBackgroundResource(R.drawable.border_orange);
                            amount.setTextColor(ContextCompat.getColor(getContext(),R.color.appOrange));
                        } else {
                            amount.setBackgroundResource(R.drawable.border_gray);
                            amount.setTextColor(ContextCompat.getColor(getContext(),R.color.darkGray));
                        }
                    }else{
                        amount.setBackgroundResource(R.drawable.border_gray);
                        amount.setTextColor(ContextCompat.getColor(getContext(),R.color.darkGray));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    amount.requestFocus();
                    amount.setError("Revise the amount");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });


        TextView label = (TextView) view.findViewById(R.id.label);
        label.setTypeface(font, Typeface.BOLD);

        visa = (ImageView) view.findViewById(R.id.visaButton);
        mc = (ImageView) view.findViewById(R.id.mcButton);
        verve = (ImageView) view.findViewById(R.id.verveButton);
        other = (ImageView) view.findViewById(R.id.otherButton);

        visa.setOnClickListener(this);
        mc.setOnClickListener(this);
        verve.setOnClickListener(this);
        other.setOnClickListener(this);
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

    private boolean isNumeric(String value) {

        try {
            String clean = "-?\\d+(\\.\\d+)?";
            if (value.matches(clean)) {
                Log.i(tag, "entered Amount is numeric");
                return true;
            } else {
                Log.i(tag, "entered Amount is not numeric");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    private void processPayment(final String uId, final long paymentType, final String amount, String currency) {

        options = RequestOptions.builder().setClientId(Interswitching.CLIENT_ID)
                .setClientSecret(Interswitching.CLIENT_SECRET)
                .build();

        final PayWithCard payWithCard = new PayWithCard(getActivity(), msisdn, "Recharge AirTime Wallet", amount, currency, uId, options, new IswCallback<PurchaseResponse>() {

            @Override
            public void onError(Exception error) {
                Util.notify(getContext(), "Error", error.getMessage(), "Close", false);
                onRechargeWallet.onRechargeWalletInteraction(0, error.toString(), msisdn, null);
                FailedPayment failedPayment = new FailedPayment(uId, StatusConfig.failedTransaction, error.getMessage());

                if (!progressDialog.isShowing()) {
                    progressDialog.setMessage("Reporting Failure...");
                    progressDialog.show();
                } else {
                    progressDialog.setMessage("Reporting Failure...");
                }
                failedPaymentMethod(failedPayment);
            }

            @Override
            public void onSuccess(final PurchaseResponse response) {

                transactionIdentifier = response.getTransactionIdentifier();
                onRechargeWallet.onRechargeWalletInteraction(1, "Successfully Money Debited " + response.getAmount() + " Identifier: " + transactionIdentifier + " Reference: " + response.getTransactionRef(), msisdn, null);
                ConfirmWalletPayment confirmWalletPayment = new ConfirmWalletPayment(uId,
                        transactionIdentifier,
                        Long.valueOf(MPay.InterswitchSDK),
                        paymentType,
                        response.getAmount(),
                        msisdn,
                        response.getTransactionRef(),
                        response.getCardType());

                if (!progressDialog.isShowing()) {
                    progressDialog.setMessage("Recharging Wallet...");
                    progressDialog.show();
                } else {
                    progressDialog.setMessage("Recharging Wallet...");
                }
                confirmRecharge(confirmWalletPayment);
                Util.notify(getContext(), "Success", "Ref: " + transactionIdentifier, "Close", false);
            }
        });
        payWithCard.start();
    }

    private void failedPaymentMethod(final FailedPayment failedPayment) {

        if (failedPayment == null)
            return;

        try {
            Log.d(tag, "Data to Post on Server:\n" + new ClientData().mapping(failedPayment));
            PaymentInterface paymentInterface = PaymentServerClient.getClient().create(PaymentInterface.class);
            Call<StatusUsage> callService = paymentInterface.failedPayment(failedPayment);
            callService.enqueue(new Callback<StatusUsage>() {
                @Override
                public void onResponse(Call<StatusUsage> call, Response<StatusUsage> response) {
                    //HTTP status code
                    int httpStatus = response.code();
                    try {

                        //handle the response from the server
                        if (httpStatus == 200) {
                            final StatusUsage status = response.body();
                            if (status != null)
                                if (status.getStatus().getStatusCode() != 0) {
                                    onRechargeWallet.onRechargeWalletInteraction(0, status.getStatus().getMessage(), msisdn, null);
                                    uiFeed(status.getStatus().getMessage());
                                } else {
                                    onRechargeWallet.onRechargeWalletInteraction(0, status.getStatus().getMessage(), msisdn, null);
                                    uiFeed(status.getStatus().getMessage());
                                }

                        } else {
                            onRechargeWallet.onRechargeWalletInteraction(0, response.message(), msisdn, null);
                            uiFeed(response.message());
                        }
                    } catch (final Exception e) {
                        onRechargeWallet.onRechargeWalletInteraction(0, e.getMessage(), msisdn, null);
                        uiFeed(e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<StatusUsage> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(tag, t.toString());
                    onRechargeWallet.onRechargeWalletInteraction(0, "Connectivity Error to report Failed Transaction", msisdn, null);
                    uiFeed("Connectivity Error");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            onRechargeWallet.onRechargeWalletInteraction(0, e.getMessage(), msisdn, null);
            uiFeed(e.getMessage());
        }
    }

    private void initiateRecharge(final InitiateWalletRecharge initiateWalletRecharge) {

        if (initiateWalletRecharge == null)
            return;

        try {
            Log.d(tag, "Data to Post on Server:\n" + new ClientData().mapping(initiateWalletRecharge));
            PaymentInterface paymentInterface = PaymentServerClient.getClient().create(PaymentInterface.class);
            Call<InitiateResponse> callService = paymentInterface.initPayment(initiateWalletRecharge);
            callService.enqueue(new Callback<InitiateResponse>() {
                @Override
                public void onResponse(Call<InitiateResponse> call, Response<InitiateResponse> response) {
                    //HTTP status code
                    int httpStatus = response.code();
                    try {

                        //handle the response from the server
                        if (httpStatus == 200) {
                            final InitiateResponse initResponse = response.body();
                            if (initResponse != null && initResponse.getStatus() != null)
                                if (initResponse.getStatus().getStatusCode() == 0 && initResponse.getInitUid() != null) {
                                    //proceed to interswitch
                                    try {
                                        progressDialog.dismiss();
                                        processPayment(initResponse.getInitUid(),
                                                Long.valueOf(initiateWalletRecharge.getPaymentType()),
                                                initiateWalletRecharge.getAmount(),
                                                MPay.currency);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        uiFeed(e.getMessage());
                                    }
                                } else {
                                    uiFeed(initResponse.getStatus().getMessage());
                                }

                        } else {
                            uiFeed(response.message());
                        }
                    } catch (final Exception e) {
                        uiFeed(e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<InitiateResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(tag, t.toString());
                    uiFeed("Connectivity Error");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            uiFeed(e.getMessage());
        }
    }


    private void confirmRecharge(final ConfirmWalletPayment confirmWalletPayment) {

        if (confirmWalletPayment == null)
            return;

        try {
            Log.d(tag, "Data to Post on Server:\n" + new ClientData().mapping(confirmWalletPayment));
            PaymentInterface paymentInterface = PaymentServerClient.getClient().create(PaymentInterface.class);
            Call<StatusUsage> callService = paymentInterface.confirmPayment(token, confirmWalletPayment);
            callService.enqueue(new Callback<StatusUsage>() {
                @Override
                public void onResponse(Call<StatusUsage> call, Response<StatusUsage> response) {
                    progressDialog.dismiss();

                    //HTTP status code
                    int httpStatus = response.code();
                    try {

                        //handle the response from the server
                        if (httpStatus == 200) {
                            final StatusUsage status = response.body();
                            if (status != null)
                                if (status.getStatus().getStatusCode() == 0) {
                                    //handle the successful and refresh the wallet amount
                                    uiFeed(status.getStatus().getMessage());
                                    onRechargeWallet.onRechargeWalletInteraction(100, "Wallet Credited " + confirmWalletPayment.getAmount(), msisdn, null);

                                } else {
                                    uiFeed(status.getStatus().getMessage());
                                    onRechargeWallet.onRechargeWalletInteraction(0, "Faillure: " + status.getStatus().getMessage(), msisdn, null);
                                }
                        } else {
                            onRechargeWallet.onRechargeWalletInteraction(0, "Faillure: " + response.message(), msisdn, null);
                            uiFeed(response.message());
                        }
                    } catch (final Exception e) {
                        onRechargeWallet.onRechargeWalletInteraction(0, "Faillure: " + response.message(), msisdn, null);
                        uiFeed(e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<StatusUsage> call, Throwable t) {
                    // Log error here since request failed
                    onRechargeWallet.onRechargeWalletInteraction(0, "Recharging wallet Failed due to connectivity Error, If your account was debited Please contact System Admin. See Menu/About Us", msisdn, null);
                    Log.e(tag, t.toString());
                    uiFeed("Recharging wallet Failed due to connectivity Error, If your account was debited Please contact System Admin. See Menu/About Us");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            onRechargeWallet.onRechargeWalletInteraction(0, e.getMessage(), msisdn, null);
            uiFeed(e.getMessage());
        }

    }


    private void uiFeed(String message) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
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


    @Override
    public void onClick(View v) {
        Log.d(tag, "Pay Button is triggered");

        if (!Util.isNetworkAvailable(getContext())) {
            Toast.makeText(getContext(), "There is no Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }

        String paymentType = null;

        switch (v.getId()) {
            case R.id.visaButton:
                paymentType = MPay.paymentVisa;
                break;
            case R.id.mcButton:
                paymentType = MPay.paymentMc;
                break;
            case R.id.verveButton:
                paymentType = MPay.paymentVerve;
                break;
            case R.id.otherButton:
                paymentType = MPay.paymentOther;
                break;
            default:
                paymentType = MPay.paymentOther;
                Log.d(tag, "Element is not a payment button");
        }

        final EditText amount;
        if (getView().findViewById(R.id.amount) != null) {
            amount = (EditText) getView().findViewById(R.id.amount);

            if (!TextUtils.isEmpty(amount.getText().toString()) && isNumeric(amount.getText().toString())) {
                try {
                    amount.requestFocus();
                    int checkAmount = 0;
                    try {
                        checkAmount = Integer.parseInt(amount.getText().toString().trim());
                    } catch (Exception e) {
                        uiFeed("Invalid Amount");
                        amount.setError("Revise the Amount");
                    }

                    if (checkAmount >= MPay.minAmount) {
                        //initiate wallet recharge
                        InitiateWalletRecharge initiateWalletRecharge = new InitiateWalletRecharge(MPay.InterswitchSDK,
                                String.valueOf(checkAmount),
                                msisdn,
                                paymentType,
                                StatusConfig.pendingTransaction,
                                StatusConfig.pendingTransactionDesc);

                        progressDialog.setMessage("Initiating...");
                        progressDialog.show();
                        initiateRecharge(initiateWalletRecharge);
                    } else {
                        uiFeed("Amount should not be lower than 1");
                        amount.setError("Revise the Amount");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    amount.setError("Revise the Amount");
                }
            } else {
                uiFeed("Amount should not be lower than 1 and should be numeric only");
                amount.setError("Revise the Amount");
            }
        } else {
            Toast.makeText(getContext(), "Error Processing the Recharge Payment", Toast.LENGTH_SHORT).show();
        }
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
    public interface RechargeWalletListener {
        void onRechargeWalletInteraction(int statusCode, String message, String msisdn, Object object);
    }
}
