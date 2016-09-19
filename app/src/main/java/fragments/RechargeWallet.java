package fragments;

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
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.interswitchng.sdk.auth.Passport;
import com.interswitchng.sdk.model.RequestOptions;
import com.interswitchng.sdk.payment.IswCallback;
import com.interswitchng.sdk.payment.Payment;
import com.interswitchng.sdk.payment.android.inapp.PayWithCard;
import com.interswitchng.sdk.payment.android.util.Util;
import com.interswitchng.sdk.payment.model.PurchaseRequest;
import com.interswitchng.sdk.payment.model.PurchaseResponse;
import com.oltranz.airtime.airtime.R;

import java.util.Calendar;
import java.util.List;

import client.ClientData;
import client.ClientServices;
import client.ServerClient;
import config.Interswitching;
import config.MPay;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import simplebeans.ResponseStatusSimpleBean;
import simplebeans.StatusUsage;
import simplebeans.payments.PaymentModeBean;
import simplebeans.walletpayment.ConfirmWalletPayment;
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

        amount = (EditText) view.findViewById(R.id.amount);
        amount.setTypeface(font);

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

    public void payWithButton(View view) {
        Log.d(tag, "Pay with Card");
        if (!Util.isNetworkAvailable(getContext())) {
            Toast.makeText(getContext(), "There is no Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }
        EditText amount;
        if (getView().findViewById(R.id.amount) != null) {
            amount = (EditText) getView().findViewById(R.id.amount);
            if (!TextUtils.isEmpty(amount.getText().toString()) && isNumeric(amount.getText().toString())) {
                try {
                    int checkAmount = Integer.parseInt(amount.getText().toString());
                    if (checkAmount > 0) {
                        //initiate wallet recharge
                        initiateRecharge(checkAmount);
//                        processPayment(String.valueOf(checkAmount));
                    } else {
                        amount.setError("Revise the Amount");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    amount.setError("Revise the Amount");
                }
            } else {
                amount.setError("Revise the Amount");
            }
        } else {
            Toast.makeText(getContext(), "Error Processing the Recharge Payment", Toast.LENGTH_SHORT).show();
        }
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

    private void processPayment(String amount, String currency, final InitiateWalletRecharge initiator) {

        Payment.overrideApiBase(Payment.QA_API_BASE); // used to override the payment api base url.
        Passport.overrideApiBase(Passport.QA_API_BASE); //used to override the payment api base url.

        options = RequestOptions.builder().setClientId(Interswitching.CLIENT_ID)
                .setClientSecret(Interswitching.CLIENT_SECRET)
                .build();


        final PayWithCard payWithCard = new PayWithCard(this.getActivity(), msisdn, "Recharge AirTime Wallet", amount, currency, options, new IswCallback<PurchaseResponse>() {

            @Override
            public void onError(Exception error) {
                Util.notify(context, "Error", error.getMessage(), "Close", false);
                try {
                    confirmRecharge(null, initiator, new ResponseStatusSimpleBean(error.getMessage(), 503));
                } catch (Exception e) {
                    e.printStackTrace();
                    uiFeed(e.getMessage());
                }
            }

            @Override
            public void onSuccess(final PurchaseResponse response) {
                final PurchaseRequest request = new PurchaseRequest();
                transactionIdentifier = response.getTransactionIdentifier();
//                if (StringUtils.hasText(response.getResponseCode())) {
//                    if (PaymentSDK.SAFE_TOKEN_RESPONSE_CODE.equals(response.getResponseCode())) {
//                        paymentId = response.getPaymentId();
//                        authData = request.getAuthData();
//                        Util.prompt(getActivity(), "OTP", response.getMessage(), "Close", "Continue", true, 1L);
//                    }
//                    if (PaymentSDK.CARDINAL_RESPONSE_CODE.equals(response.getResponseCode())) {
//                        final Dialog cardinalDialog = new Dialog(context) {
//                            @Override
//                            public void onBackPressed() {
//                                super.onBackPressed();
//                            }
//                        };
//                        webView = new AuthorizeWebView(context, response) {
//                            @Override
//                            public void onPageDone() {
//                                Util.showProgressDialog(context, "Processing...");
//                                AuthorizePurchaseRequest cardinalRequest = new AuthorizePurchaseRequest();
//                                cardinalRequest.setAuthData(request.getAuthData());
//                                cardinalRequest.setPaymentId(response.getPaymentId());
//                                cardinalRequest.setTransactionId(response.getTransactionId());
//                                cardinalRequest.setEciFlag(response.getEciFlag());
//                                new PaymentSDK(context, options).authorizePurchase(cardinalRequest, new IswCallback<AuthorizePurchaseResponse>() {
//                                    @Override
//                                    public void onError(Exception error) {
//                                        Util.hideProgressDialog();
//                                        cardinalDialog.dismiss();
//                                        Util.notify(context, "Error", error.getMessage(), "Close", false);
//                                    }
//
//                                    @Override
//                                    public void onSuccess(AuthorizePurchaseResponse response) {
//                                        Util.hideProgressDialog();
//                                        cardinalDialog.dismiss();
//                                        Util.notify(context, "Success", response.getMessage(), "Close", false);
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onPageError(Exception error) {
//                                Util.notify(context, "Error", error.getMessage(), "Close", false);
//                            }
//                        };
//                        cardinalDialog.setContentView(webView);
//                        cardinalDialog.show();
//                        cardinalDialog.setCancelable(true);
//                        webView.requestFocus(View.FOCUS_DOWN);
//                        webView.getSettings().setJavaScriptEnabled(true);
//                        webView.setVerticalScrollBarEnabled(true);
//                        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//                        Display display = wm.getDefaultDisplay();
//                        Point size = new Point();
//                        //display.getSize(size);
//                        DisplayMetrics dsp=getContext().getResources().getDisplayMetrics();
//                        int width = dsp.widthPixels;//size.x;
//                        int height =dsp.heightPixels; //size.y;
//                        Window window = cardinalDialog.getWindow();
//                        window.setLayout(width, height);
//                    }
//
//                } else {
                Util.notify(context, "Success", "Ref: " + transactionIdentifier, "Close", false);
                try {
                    confirmRecharge(response, initiator, new ResponseStatusSimpleBean("Successfull Transaction", 400));
                } catch (Exception e) {
                    e.printStackTrace();
                    uiFeed(e.getMessage());
                }
//                }
            }
        });
        payWithCard.start();
    }

//    public void executePay() {
//        Payment.overrideApiBase(Payment.QA_API_BASE); // used to override the payment api base url.
//        Passport.overrideApiBase(Passport.QA_API_BASE); //used to override the payment api base url.
//        List<EditText> fields = new ArrayList<>();
//        fields.clear();
//
//        fields.add(customerID);
//        fields.add(amount);
//        fields.add(pan);
//        fields.add(pin);
//        fields.add(expiry);
//        fields.add(cvv2);
//        if (Validation.isValidEditboxes(fields)) {
//            if (Util.isNetworkAvailable(getContext())) {
//                final PurchaseRequest request = new PurchaseRequest();
//                request.setCustomerId(customerID.getText().toString());
//                request.setAmount("200");
//                request.setPan(pan.getText().toString());
//                request.setPinData(pin.getText().toString());
//                request.setExpiryDate(expiry.getText().toString());
//                request.setRequestorId("11179920172");
//                request.setCurrency("NGN");
//                request.setTransactionRef(RandomString.numeric(12));
//                request.setCvv2(cvv2.getText().toString());
//                Util.hide_keyboard(getActivity());
//                Util.showProgressDialog(context, "Sending Payment");
//                new PaymentSDK(context, options).purchase(request, new IswCallback<PurchaseResponse>() {
//                    @Override
//                    public void onError(Exception error) {
//                        Util.hideProgressDialog();
//                        Util.notify(context, "Error", error.getLocalizedMessage(), "Close", false);
//                    }
//
//                    @Override
//                    public void onSuccess(final PurchaseResponse response) {
//                        Util.hideProgressDialog();
//                        transactionIdentifier = response.getTransactionIdentifier();
//                        if (StringUtils.hasText(response.getResponseCode())) {
//                            if (PaymentSDK.SAFE_TOKEN_RESPONSE_CODE.equals(response.getResponseCode())) {
//                                paymentId = response.getPaymentId();
//                                authData = request.getAuthData();
//                                Util.prompt(getActivity(), "OTP", response.getMessage(), "Close", "Continue", true, 1L);
//                            }
//                            if (PaymentSDK.CARDINAL_RESPONSE_CODE.equals(response.getResponseCode())) {
//                                final Dialog cardinalDialog = new Dialog(context) {
//                                    @Override
//                                    public void onBackPressed() {
//                                        super.onBackPressed();
//                                    }
//                                };
//                                webView = new AuthorizeWebView(context, response) {
//                                    @Override
//                                    public void onPageDone() {
//                                        Util.showProgressDialog(context, "Processing...");
//                                        AuthorizePurchaseRequest cardinalRequest = new AuthorizePurchaseRequest();
//                                        cardinalRequest.setAuthData(request.getAuthData());
//                                        cardinalRequest.setPaymentId(response.getPaymentId());
//                                        cardinalRequest.setTransactionId(response.getTransactionId());
//                                        cardinalRequest.setEciFlag(response.getEciFlag());
//                                        new PaymentSDK(context, options).authorizePurchase(cardinalRequest, new IswCallback<AuthorizePurchaseResponse>() {
//                                            @Override
//                                            public void onError(Exception error) {
//                                                Util.hideProgressDialog();
//                                                cardinalDialog.dismiss();
//                                                Util.notify(context, "Error", error.getMessage(), "Close", false);
//                                            }
//
//                                            @Override
//                                            public void onSuccess(AuthorizePurchaseResponse response) {
//                                                Util.hideProgressDialog();
//                                                cardinalDialog.dismiss();
//                                                Util.notify(context, "Success", response.getMessage(), "Close", false);
//                                            }
//                                        });
//                                    }
//
//                                    @Override
//                                    public void onPageError(Exception error) {
//                                        Util.notify(context, "Error", error.getMessage(), "Close", false);
//                                    }
//                                };
//                                cardinalDialog.setContentView(webView);
//                                cardinalDialog.show();
//                                cardinalDialog.setCancelable(true);
//                                webView.requestFocus(View.FOCUS_DOWN);
//                                webView.getSettings().setJavaScriptEnabled(true);
//                                webView.setVerticalScrollBarEnabled(true);
//                                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//                                Display display = wm.getDefaultDisplay();
//                                Point size = new Point();
//                                //display.getSize(size);
//                                DisplayMetrics dsp=getContext().getResources().getDisplayMetrics();
//                                int width = dsp.widthPixels;//size.x;
//                                int height =dsp.heightPixels; //size.y;
//                                Window window = cardinalDialog.getWindow();
//                                window.setLayout(width, height);
//                            }
//
//                        } else {
//                            Util.notify(context, "Success", "Ref: " + transactionIdentifier, "Close", false);
//                        }
//                    }
//                });
//            } else {
//                Util.notifyNoNetwork(getContext());
//            }
//        }
//    }

//    @Override
//    public void promptResponse(String response, long requestId) {
//        if (requestId == 1 && StringUtils.hasText(response)) {
//            AuthorizePurchaseRequest request = new AuthorizePurchaseRequest();
//            request.setPaymentId(paymentId);
//            request.setOtp(response);
//            request.setAuthData(authData);
//            Util.hide_keyboard(getActivity());
//            Util.showProgressDialog(context, "Verifying OTP");
//            new PaymentSDK(context, options).authorizePurchase(request, new IswCallback<AuthorizePurchaseResponse>() {
//                @Override
//                public void onError(Exception error) {
//                    Util.hideProgressDialog();
//                    Util.notify(context, "Error", error.getLocalizedMessage(), "Close", false);
//                }
//
//                @Override
//                public void onSuccess(AuthorizePurchaseResponse otpResponse) {
//                    Util.hideProgressDialog();
//                    Util.notify(context, "Success", "Ref: " + transactionIdentifier, "Close", false);
//                }
//            });
//        }
//
//    }

    private void initiateRecharge(final long amount) {

        final InitiateWalletRecharge initiateWalletRecharge = new InitiateWalletRecharge(refId(), msisdn, amount, MPay.currency);
        //initiate request
        try {
            Log.d(tag, "Data to Post on Server:\n" + new ClientData().mapping(initiateWalletRecharge));
            ClientServices clientServices = ServerClient.getClient().create(ClientServices.class);
            Call<StatusUsage> callService = clientServices.initWalletRecharge(initiateWalletRecharge);
            callService.enqueue(new Callback<StatusUsage>() {
                @Override
                public void onResponse(Call<StatusUsage> call, Response<StatusUsage> response) {

                    //HTTP status code
                    int statusCode = response.code();
                    try {
                        final ResponseStatusSimpleBean status = response.body().getStatus();
                        //handle the response from the server
                        Log.d(tag, "Server Result:\n" + new ClientData().mapping(response.body()));
                        if (status.getStatusCode() == 400) {
                            //proceed to interswitch
                            try {
                                processPayment(String.valueOf(amount), MPay.currency, initiateWalletRecharge);
                            } catch (Exception e) {
                                e.printStackTrace();
                                uiFeed(e.getMessage());
                            }

                        } else {
                            uiFeed(status.getMessage());
                        }
                    } catch (final Exception e) {
                        uiFeed(e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<StatusUsage> call, Throwable t) {
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

    private void confirmRecharge(PurchaseResponse purchaseResponse, InitiateWalletRecharge initiator, ResponseStatusSimpleBean status) {
        ConfirmWalletPayment confirmWalletPayment = new ConfirmWalletPayment(purchaseResponse, initiator, status);
        //initiate request

        try {
            Log.d(tag, "Data to Post on Server:\n" + new ClientData().mapping(confirmWalletPayment));
            ClientServices clientServices = ServerClient.getClient().create(ClientServices.class);
            Call<StatusUsage> callService = clientServices.confWalletRecharge(confirmWalletPayment);
            callService.enqueue(new Callback<StatusUsage>() {
                @Override
                public void onResponse(Call<StatusUsage> call, Response<StatusUsage> response) {

                    //HTTP status code
                    int statusCode = response.code();
                    try {
                        final ResponseStatusSimpleBean status = response.body().getStatus();
                        //handle the response from the server
                        Log.d(tag, "Server Result:\n" + new ClientData().mapping(status));
                        if (status.getStatusCode() == 400) {
                            //handle the airtime core response
                            try {
                                uiFeed(status.getMessage());
                            } catch (Exception e) {
                                e.printStackTrace();
                                uiFeed(e.getMessage());
                            }

                        } else {
                            uiFeed(status.getMessage());
                        }
                    } catch (final Exception e) {
                        uiFeed(e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<StatusUsage> call, Throwable t) {
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

    private String refId() {
        Calendar dateNow = Calendar.getInstance();
        // offset to add since we're not UTC
        long offset = dateNow.get(Calendar.ZONE_OFFSET) + dateNow.get(Calendar.DST_OFFSET);
        long sinceMidnight = (dateNow.getTimeInMillis() + offset) % (24 * 60 * 60 * 1000);
        String referenceId = sinceMidnight + msisdn != null ? sinceMidnight + msisdn : "xyz";

        Log.d(tag, sinceMidnight + " milliseconds since midnight");
        return referenceId;
    }

    private void uiFeed(String message) {
        try {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onClick(View v) {
        Log.d(tag, "Pay Button is triggered");
        payWithButton(v);
    }

//    private void paymentList(PaymentModesResponse paymentModesResponse){
//        final EditText amountField=(EditText) getView().findViewById(R.id.amount);
//        final GridView paymentsGrid=(GridView) getView().findViewById(R.id.paymentGrid);
//        paymentModeBeanList=paymentModesResponse.getPaymentModeBeanList();
//        adapter=new PaymentModeAdapter(getContext(),R.layout.payment_style,paymentModeBeanList);
//        paymentsGrid.setAdapter(adapter);
//        paymentsGrid.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
//
//        paymentsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                paymentsGrid.requestFocusFromTouch();
//                paymentsGrid.setSelection(position);
//                TextView paymentName=(TextView) view.findViewById(R.id.paymentName);
//                TextView paymentId=(TextView) view.findViewById(R.id.paymentId);
//                Log.d(tag, "Payment=>\n amount to add: " + amountField.getText().toString() + " payemnt mode: " + paymentName.getText().toString() + "\n and Payment Id: " +paymentId.getText().toString());
//
//                try{
//                    double amount=Double.valueOf(amountField.getText().toString());
//                    proceed(amount, paymentName.getText().toString(), Long.parseLong(paymentId.getText().toString()));
//                    amountField.setText("");
//                }catch (Exception e){
//                    e.printStackTrace();
//                    Toast.makeText(getContext(),"Invalid Amount",Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//    }
//
//    private void proceed(final double amount, final String paymentModeName, final long paymentModeId){
//
//        final Dialog dialog = new Dialog(getContext());
//        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
//        dialog.setContentView(R.layout.confirmrechargewallet);
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
//
//        int dividerId = dialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
//        if (dividerId != 0) {
//            View divider = dialog.findViewById(dividerId);
//            if(divider != null)
//                divider.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.appOrange));
//        }
//        dialog.setTitle(Html.fromHtml("<font color='" + ContextCompat.getColor(getContext(), R.color.appOrange) + "'>Confirm...</font>"));
//        dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_info);
//        final TextView amountLabel=(TextView) dialog.findViewById(R.id.amount);
//        final TextView accountLable=(TextView) dialog.findViewById(R.id.accountlbl);
//        final TextView tv=(TextView) dialog.findViewById(R.id.tv);
//        final EditText pin=(EditText) dialog.findViewById(R.id.pin);
//        final Button cancel=(Button) dialog.findViewById(R.id.cancel);
//        final Button ok=(Button) dialog.findViewById(R.id.ok);
//
//        amountLabel.setText(amount+"");
//        accountLable.setText(paymentModeName);
//
//        ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!TextUtils.isEmpty(pin.getText().toString())) {
//
//                    //get current Time
//                    DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
//                    String currentTime = df.format(Calendar.getInstance().getTime());
//
//                    //get device Identity
//                    DeviceIdentity deviceIdentity = new DeviceIdentity(getContext());
//                    //get user Identity
//                    LoginRequest loginRequest = new LoginRequest(pin.getText().toString(),
//                            msisdn,
//                            currentTime,
//                            deviceIdentity.getSerialNumber(),
//                            deviceIdentity.getImei(),
//                            deviceIdentity.getVersion());
//
//
//                    //Client data to send to the Sever
//                    Log.d(tag, "Data to push to the server:\n" + new ClientData().mapping(loginRequest));
//
//                    //making a Login request
//                    try {
//                        ClientServices clientServices = ServerClient.getClient().create(ClientServices.class);
//                        Call<LoginResponse> callService = clientServices.loginUser(loginRequest);
//                        callService.enqueue(new Callback<LoginResponse>() {
//                            @Override
//                            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
//
//                                //HTTP status code
//                                int statusCode = response.code();
//                                LoginResponse loginResponse = response.body();
//
//                                //handle the response from the server
//                                Log.d(tag, "Server Result:\n" + new ClientData().mapping(loginResponse));
//                                if(loginResponse.getResponseStatusSimpleBean().getStatusCode() == 400){
//
//                                    //making a transaction Request
//                                    onWalletRechargeRequest(amount, paymentModeName, paymentModeId);
//
//                                    dialog.dismiss();
//                                }else{
//                                    tv.setText(loginResponse.getResponseStatusSimpleBean().getMessage());
//                                    pin.setText("");
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<LoginResponse> call, Throwable t) {
//                                // Log error here since request failed
//                                Log.e(tag, t.toString());
//                                tv.setText(t.toString());
//                            }
//                        });
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        tv.setText(e.getMessage());
//                    }
//                }else{
//                    //Empty Pin
//                    tv.setText("Fill your PIN");
//                }
//            }
//        });
//
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show();
//    }
//
//    private void onWalletRechargeRequest(final double amount, final String paymentModeName, final long paymentModeId){
//        Toast.makeText(getContext(),"Recharge Wallet Under Construction",Toast.LENGTH_LONG).show();
//    }

//    //__________Browsing class______________\\
//    private class MyBrowser extends WebViewClient {
//        private Context context;
//
//        public MyBrowser(Context context) {
//            this.context = context;
//        }
//
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            if(isValidURL(url)){
//                view.loadUrl(url);
//            }
//
//            return true;
//        }
//
//        @Override
//        public void onPageFinished(WebView view, String url) {
//            //When rendering a page just finished
//            //Toast.makeText(getContext(), "Loading Success! " + url, Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//            Toast.makeText(getContext(), "Error! " + description, Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                Toast.makeText(getContext(), "Error! " + error.getDescription().toString(), Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        @Override
//        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            super.onPageStarted(view, url, favicon);
//        }
//
//
//        @Override
//        public void onLoadResource(WebView view, String url) {
//            super.onLoadResource(view, url);
//        }
//    }
//
//    private boolean isValidURL(String url) {
//
//        URL u = null;
//
//        try {
//            u = new URL(url);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            return false;
//        }
//
//        try {
//            u.toURI();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//            return false;
//        }catch (Exception e){
//            e.printStackTrace();
//            return false;
//        }
//
//        return true;
//    }

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
