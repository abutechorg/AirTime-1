package utilities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.oltranz.mobilea.mobilea.BuildConfig;
import com.oltranz.mobilea.mobilea.R;

import java.text.DateFormat;
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
import simplebeans.loginbeans.LoginRequest;
import simplebeans.loginbeans.LoginResponse;
import simplebeans.topupbeans.TopUpBean;
import simplebeans.topupbeans.TopUpRequest;
import simplebeans.topupbeans.TopUpResponse;
import utilities.utilitiesbeans.TopUpNumber;

/**
 * Created by Hp on 10/21/2016.
 */
public class ResendAirtime implements CheckWalletBalance.CheckWalletBalanceInteraction, RechargeWalletUtil.RechargeWalletUtilInteraction {
    private Activity context;
    private ResendAirtimeInteraction resendAirtimeInteraction;
    private String token,msisdn,appVersion;
    private String tag = getClass().getSimpleName();
    private ProgressDialog progressDialog;
    private CheckWalletBalance checkWalletBalance;
    private Typeface font;

    public ResendAirtime(ResendAirtimeInteraction resendAirtimeInteraction, Activity context, String token, String msisdn) {
        this.context = context;
        this.resendAirtimeInteraction = resendAirtimeInteraction;
        this.token = token;
        this.msisdn = msisdn;
    }

    public void startResend(String tel, double amount){
        progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        font = Typeface.createFromAsset(context.getAssets(), "font/ubuntu.ttf");

        checkWalletBalance = new CheckWalletBalance(this, context, token);
        appVersion = "versionCode: " + BuildConfig.VERSION_CODE + " versionName: " + BuildConfig.VERSION_NAME;

        topUpProceed(tel, amount);
    }

    private void topUpProceed(String tel, double amount) {

//        if (walletBalance <= amount) {
//            //redirect to the recharge of wallet
//            promptToRechargeWallet(tel, amount);
//        } else {
        final List<TopUpNumber> numbers = new ArrayList<TopUpNumber>();
        TopUpNumber topUpNumber = new TopUpNumber(tel, amount);
        numbers.add(topUpNumber);

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        dialog.setContentView(R.layout.confirmtopup);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        int dividerId = dialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
        if (dividerId != 0) {
            View divider = dialog.findViewById(dividerId);
            if (divider != null)
                divider.setBackgroundColor(ContextCompat.getColor(context, R.color.appOrange));
        }
        dialog.setTitle(Html.fromHtml("<font color='" + ContextCompat.getColor(context, R.color.appOrange) + "'>Confirm...</font>"));
        dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_info);

        TextView topLabel=(TextView) dialog.findViewById(R.id.topLabel);
        topLabel.setTypeface(font, Typeface.BOLD);
        TextView telLabel=(TextView) dialog.findViewById(R.id.lbltel);
        telLabel.setTypeface(font,Typeface.BOLD);
        TextView amountLabel=(TextView) dialog.findViewById(R.id.lblamount);
        amountLabel.setTypeface(font,Typeface.BOLD);
        final ListView telList = (ListView) dialog.findViewById(R.id.telList);
        final TextView tv = (TextView) dialog.findViewById(R.id.tv);
        tv.setTypeface(font);
        final EditText pin = (EditText) dialog.findViewById(R.id.pin);
        pin.setTypeface(font);
        final Button cancel = (Button) dialog.findViewById(R.id.cancel);
        cancel.setTypeface(font, Typeface.BOLD);
        final Button ok = (Button) dialog.findViewById(R.id.ok);
        ok.setTypeface(font, Typeface.BOLD);

        TopUpConfirmAdapter adapter = new TopUpConfirmAdapter(context, numbers);
        telList.setAdapter(adapter);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(pin.getText().toString())) {

                    //get current Time
                    DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                    String currentTime = df.format(Calendar.getInstance().getTime());

                    //get device Identity
                    DeviceIdentity deviceIdentity = new DeviceIdentity(context);
                    //get user Identity
                    LoginRequest loginRequest = new LoginRequest(pin.getText().toString(),
                            msisdn,
                            currentTime,
                            deviceIdentity.getSerialNumber(),
                            deviceIdentity.getImei(),
                            deviceIdentity.getVersion());


                    progressDialog.setMessage("Validating...");
                    progressDialog.show();
                    //Client data to send to the Sever
                    Log.d(tag, "Data to push to the server:\n" + new ClientData().mapping(loginRequest));

                    //making a Login request
                    try {
                        ClientServices clientServices = ServerClient.getClient().create(ClientServices.class);
                        Call<LoginResponse> callService = clientServices.loginUser(appVersion, loginRequest);
                        callService.enqueue(new Callback<LoginResponse>() {
                            @Override
                            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                                //HTTP status code
                                int statusCode = response.code();
                                if(statusCode == 200){
                                    if(response.body() != null){
                                        LoginResponse loginResponse = response.body();

                                        //handle the response from the server
                                        Log.d(tag, "Server Result:\n" + new ClientData().mapping(loginResponse));
                                        if (loginResponse.getSimpleStatusBean().getStatusCode() == 400) {

                                            dialog.dismiss();
                                            progressDialog.setMessage("Sending Airtime...");
                                            //making a transaction Request
                                            onTopUpRequest(numbers);
                                        } else {
                                            uiFeed(loginResponse.getSimpleStatusBean().getMessage());
                                            tv.setText(loginResponse.getSimpleStatusBean().getMessage());
                                            pin.setText("");
                                        }
                                    }else{
                                        uiFeed("Erroneous server response. Contact System Admin");
                                    }
                                }else{
                                    uiFeed(response.message()+"");
                                }
                            }

                            @Override
                            public void onFailure(Call<LoginResponse> call, Throwable t) {
                                // Log error here since request failed
                                Log.e(tag, t.toString());
                                uiFeed("Connectivity Error");
                                tv.setText("Connectivity Error");
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        uiFeed(""+e.getMessage());
                        tv.setText("Erroneous Data");
                    }
                } else {
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
//        }
    }

    private void onTopUpRequest(List<TopUpNumber> numbers) {
        //validation passed, initiation of topUp Object
        List<TopUpBean> destined = new ArrayList<TopUpBean>();
        for (TopUpNumber topUpNumber : numbers) {
            TopUpBean topUpBean = new TopUpBean(topUpNumber.getMsisdn(), topUpNumber.getAmount());
            destined.add(topUpBean);
        }

        //make a request of topUp Object
        TopUpRequest topUpRequest = new TopUpRequest(destined);

        Log.d(tag, "Request Body:\n" + new ClientData().mapping(topUpRequest));

        //making a Login request
        try {
            ClientServices clientServices = ServerClient.getClient().create(ClientServices.class);
            Call<TopUpResponse> callService = clientServices.topUp(token, topUpRequest);
            callService.enqueue(new Callback<TopUpResponse>() {
                @Override
                public void onResponse(Call<TopUpResponse> call, Response<TopUpResponse> response) {

                    //HTTP status code
                    int statusCode = response.code();
                    if (statusCode == 200) {
                        try {
                            TopUpResponse topUpResponse = response.body();

                            if(topUpResponse.getStatus().getStatusCode() == 401){
                                //handle the response from the server
                                Log.d(tag, "Server Result:\n" + new ClientData().mapping(response.body()));
                                uiFeed(topUpResponse.getStatus().getMessage()+" Please fund your wallet");
                                resendAirtimeInteraction.onResendAirtimeModule(1, topUpResponse.getStatus().getMessage()+" Please fund your wallet");//onSingleSell(1, topUpResponse.getStatus().getMessage()+" Please fund your wallet", topUpResponse);

                                checkBalance();
                            }else{
                                //handle the response from the server
                                progressDialog.setMessage(topUpResponse.getStatus().getMessage()+"");
                                Log.d(tag, "Server Result:\n" + new ClientData().mapping(response.body()));
                                uiFeed(topUpResponse.getStatus().getMessage());
                                resendAirtimeInteraction.onResendAirtimeModule(1, topUpResponse.getStatus().getMessage());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            uiFeed(e.getMessage());

                            resendAirtimeInteraction.onResendAirtimeModule(1, e.getMessage());
                        }
                    } else {
                        uiFeed(response.message());
                    }

//                                loginListener.onLoginInteraction(loginResponse.getSimpleStatusBean().getStatusCode(),
//                                        loginResponse.getSimpleStatusBean().getMessage(),
//                                        msisdn,
//                                        loginResponse);
                }

                @Override
                public void onFailure(Call<TopUpResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(tag, t.toString());
                    uiFeed("Connectivity Error");
                    resendAirtimeInteraction.onResendAirtimeModule(0, "Connectivity Error");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            uiFeed(e.getMessage());
            resendAirtimeInteraction.onResendAirtimeModule(0, e.getMessage());
        }

    }

    private void uiFeed(String message) {
        if(progressDialog != null)
            if(progressDialog.isShowing())
                progressDialog.dismiss();
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
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


            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkBalance(){
        checkWalletBalance.getBalance();
    }

    @Override
    public void onWalletBalanceCheck(String balance) {

    }

    @Override
    public void onRecargeWalletInteraction(int statusCode, String message) {

    }

    public interface ResendAirtimeInteraction {
        void onResendAirtimeModule(int statusCode, String message);
    }
}
