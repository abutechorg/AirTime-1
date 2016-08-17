package fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.oltranz.airtime.airtime.R;

import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import client.ClientData;
import client.ClientServices;
import client.ServerClient;
import config.BaseUrl;
import config.DeviceIdentity;
import config.MPay;
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

//__________________________Inter switch

import android.graphics.Point;
import android.view.Display;

import com.interswitchng.sdk.auth.Passport;
import com.interswitchng.sdk.model.RequestOptions;
import com.interswitchng.sdk.payment.IswCallback;
import com.interswitchng.sdk.payment.Payment;
import com.interswitchng.sdk.payment.android.AuthorizeWebView;
import com.interswitchng.sdk.payment.android.PaymentSDK;
import com.interswitchng.sdk.payment.android.util.Util;
import com.interswitchng.sdk.payment.android.util.Validation;
import com.interswitchng.sdk.payment.model.AuthorizePurchaseRequest;
import com.interswitchng.sdk.payment.model.AuthorizePurchaseResponse;
import com.interswitchng.sdk.payment.model.PurchaseRequest;
import com.interswitchng.sdk.payment.model.PurchaseResponse;
import com.interswitchng.sdk.util.RandomString;
import com.interswitchng.sdk.util.StringUtils;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RechargeWalletListener} interface
 * to handle interaction events.
 * Use the {@link RechargeWallet#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RechargeWallet extends Fragment implements Util.PromptResponseHandler {
    private String tag="AirTime: "+getClass().getSimpleName();
    private static final String tokenParam = "token";
    private static final String msisdnParam = "msisdn";

    private String token;
    private String msisdn;
    private Typeface font;
    private RechargeWalletListener onRechargeWallet;
    private PaymentModeAdapter adapter;
    private List<PaymentModeBean> paymentModeBeanList;

    //____________________InterSwitch________________\\
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
        options = RequestOptions.builder().setClientId("IKIA57B80F5CA5AD61B512C451E8655B39B5A4D5249C").setClientSecret("d4We4cOD33O9QzTZf0sp8WjmNxC9l+qT/3OiLUlaXyo=").build();
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

        context = getContext();

        payNow = (Button) view.findViewById(R.id.payButton);

        customerID = (EditText) view.findViewById(R.id.customerid);
        amount = (EditText) view.findViewById(R.id.amount);
        pan = (EditText) view.findViewById(R.id.cardpan);
        pin = (EditText) view.findViewById(R.id.cardpin);
        expiry = (EditText) view.findViewById(R.id.expirydate);
        cvv2 = (EditText) view.findViewById(R.id.cardCvv2);
        payNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executePay();
            }
        });



//        wv=(WebView) view.findViewById(R.id.mWeb);
//        wv.getSettings().setBuiltInZoomControls(true);
//        wv.getSettings().setSupportZoom(true);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            wv.getSettings().setDisplayZoomControls(true);
//        }
//        wv.setWebViewClient(new MyBrowser(getContext()));
//        wv.setWebChromeClient(new WebChromeClient() {
//            public void onProgressChanged(WebView view, int progress) {
//                // Activities and WebViews measure progress with different scales.
////                                activity.setProgress(progress * 1000);
////                                progress(progress);
////                                webTitle.setText(""+progress);
//            }
//        });
//        wv.getSettings().setLoadsImagesAutomatically(true);
//        wv.getSettings().setJavaScriptEnabled(true);
//        wv.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
//        if (Build.VERSION.SDK_INT >= 19) {
//            // chromium, enable hardware acceleration
//            wv.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        } else {
//            // older android version, disable hardware acceleration
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                wv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//            }
//        }
//
//        wv.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
//        wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//        //wv.loadData(MPay.mImbed, "text/html", null); //"http://tunein.com/embed/player/s270168/"
//        wv.loadUrl(BaseUrl.mPay);

//
//        try {
//            ClientServices clientServices = ServerClient.getClient().create(ClientServices.class);
//            Call<PaymentModesResponse> callService = clientServices.getPaymentModes();
//            callService.enqueue(new Callback<PaymentModesResponse>() {
//                @Override
//                public void onResponse(Call<PaymentModesResponse> call, Response<PaymentModesResponse> response) {
//
//                    //HTTP status code
//                    int statusCode = response.code();
//                    try{
//                        //handle the response from the server
//                        PaymentModesResponse paymentModesResponse = response.body();
//                        Log.d(tag, "Data from the server:\n" + new ClientData().mapping(paymentModesResponse));
//                        if(paymentModesResponse.getResponseStatusSimpleBean().getStatusCode()== 400)
//                            paymentList(paymentModesResponse);
//                        else{
//                            TextView tv=(TextView) getView().findViewById(R.id.tv);
//                            tv.setVisibility(View.VISIBLE);
//                            tv.setText(paymentModesResponse.getResponseStatusSimpleBean().getMessage());
//                        }
//
//                    }catch (Exception e){
//                        e.printStackTrace();
//                        onRechargeWallet.onRechargeWalletInteraction(500, e.getMessage(), msisdn, null);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<PaymentModesResponse> call, Throwable t) {
//                    // Log error here since request failed
//                    Log.e(tag, t.toString());
//                    onRechargeWallet.onRechargeWalletInteraction(500, t.getMessage(), msisdn, null);
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//            onRechargeWallet.onRechargeWalletInteraction(500, e.getMessage(), msisdn, null);
//        }
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

    public void executePay() {
        Payment.overrideApiBase(Payment.QA_API_BASE); // used to override the payment api base url.
        Passport.overrideApiBase(Passport.QA_API_BASE); //used to override the payment api base url.
        List<EditText> fields = new ArrayList<>();
        fields.clear();

        fields.add(customerID);
        fields.add(amount);
        fields.add(pan);
        fields.add(pin);
        fields.add(expiry);
        fields.add(cvv2);
        if (Validation.isValidEditboxes(fields)) {
            if (Util.isNetworkAvailable(getContext())) {
                final PurchaseRequest request = new PurchaseRequest();
                request.setCustomerId(customerID.getText().toString());
                request.setAmount("200");
                request.setPan(pan.getText().toString());
                request.setPinData(pin.getText().toString());
                request.setExpiryDate(expiry.getText().toString());
                request.setRequestorId("11179920172");
                request.setCurrency("NGN");
                request.setTransactionRef(RandomString.numeric(12));
                request.setCvv2(cvv2.getText().toString());
                Util.hide_keyboard(getActivity());
                Util.showProgressDialog(context, "Sending Payment");
                new PaymentSDK(context, options).purchase(request, new IswCallback<PurchaseResponse>() {
                    @Override
                    public void onError(Exception error) {
                        Util.hideProgressDialog();
                        Util.notify(context, "Error", error.getLocalizedMessage(), "Close", false);
                    }

                    @Override
                    public void onSuccess(final PurchaseResponse response) {
                        Util.hideProgressDialog();
                        transactionIdentifier = response.getTransactionIdentifier();
                        if (StringUtils.hasText(response.getResponseCode())) {
                            if (PaymentSDK.SAFE_TOKEN_RESPONSE_CODE.equals(response.getResponseCode())) {
                                paymentId = response.getPaymentId();
                                authData = request.getAuthData();
                                Util.prompt(getActivity(), "OTP", response.getMessage(), "Close", "Continue", true, 1L);
                            }
                            if (PaymentSDK.CARDINAL_RESPONSE_CODE.equals(response.getResponseCode())) {
                                final Dialog cardinalDialog = new Dialog(context) {
                                    @Override
                                    public void onBackPressed() {
                                        super.onBackPressed();
                                    }
                                };
                                webView = new AuthorizeWebView(context, response) {
                                    @Override
                                    public void onPageDone() {
                                        Util.showProgressDialog(context, "Processing...");
                                        AuthorizePurchaseRequest cardinalRequest = new AuthorizePurchaseRequest();
                                        cardinalRequest.setAuthData(request.getAuthData());
                                        cardinalRequest.setPaymentId(response.getPaymentId());
                                        cardinalRequest.setTransactionId(response.getTransactionId());
                                        cardinalRequest.setEciFlag(response.getEciFlag());
                                        new PaymentSDK(context, options).authorizePurchase(cardinalRequest, new IswCallback<AuthorizePurchaseResponse>() {
                                            @Override
                                            public void onError(Exception error) {
                                                Util.hideProgressDialog();
                                                cardinalDialog.dismiss();
                                                Util.notify(context, "Error", error.getMessage(), "Close", false);
                                            }

                                            @Override
                                            public void onSuccess(AuthorizePurchaseResponse response) {
                                                Util.hideProgressDialog();
                                                cardinalDialog.dismiss();
                                                Util.notify(context, "Success", response.getMessage(), "Close", false);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onPageError(Exception error) {
                                        Util.notify(context, "Error", error.getMessage(), "Close", false);
                                    }
                                };
                                cardinalDialog.setContentView(webView);
                                cardinalDialog.show();
                                cardinalDialog.setCancelable(true);
                                webView.requestFocus(View.FOCUS_DOWN);
                                webView.getSettings().setJavaScriptEnabled(true);
                                webView.setVerticalScrollBarEnabled(true);
                                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                                Display display = wm.getDefaultDisplay();
                                Point size = new Point();
                                //display.getSize(size);
                                DisplayMetrics dsp=getContext().getResources().getDisplayMetrics();
                                int width = dsp.widthPixels;//size.x;
                                int height =dsp.heightPixels; //size.y;
                                Window window = cardinalDialog.getWindow();
                                window.setLayout(width, height);
                            }

                        } else {
                            Util.notify(context, "Success", "Ref: " + transactionIdentifier, "Close", false);
                        }
                    }
                });
            } else {
                Util.notifyNoNetwork(getContext());
            }
        }
    }

    @Override
    public void promptResponse(String response, long requestId) {
        if (requestId == 1 && StringUtils.hasText(response)) {
            AuthorizePurchaseRequest request = new AuthorizePurchaseRequest();
            request.setPaymentId(paymentId);
            request.setOtp(response);
            request.setAuthData(authData);
            Util.hide_keyboard(getActivity());
            Util.showProgressDialog(context, "Verifying OTP");
            new PaymentSDK(context, options).authorizePurchase(request, new IswCallback<AuthorizePurchaseResponse>() {
                @Override
                public void onError(Exception error) {
                    Util.hideProgressDialog();
                    Util.notify(context, "Error", error.getLocalizedMessage(), "Close", false);
                }

                @Override
                public void onSuccess(AuthorizePurchaseResponse otpResponse) {
                    Util.hideProgressDialog();
                    Util.notify(context, "Success", "Ref: " + transactionIdentifier, "Close", false);
                }
            });
        }

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
