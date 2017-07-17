package utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.interswitchng.sdk.model.RequestOptions;
import com.interswitchng.sdk.payment.IswCallback;
import com.interswitchng.sdk.payment.android.inapp.PayWithCard;
import com.interswitchng.sdk.payment.android.util.Util;
import com.interswitchng.sdk.payment.model.PurchaseResponse;
import com.oltranz.mobilea.mobilea.R;

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
import simplebeans.walletpayment.ConfirmWalletPayment;
import simplebeans.walletpayment.FailedPayment;
import simplebeans.walletpayment.InitiateResponse;
import simplebeans.walletpayment.InitiateWalletRecharge;

/**
 * Created by Hp on 10/17/2016.
 */
public class RechargeWalletUtil {
    private final String tag=getClass().getSimpleName();
    Activity context;
    RechargeWalletUtilInteraction rechargeWalletUtilInteraction;
    View view;
    String token;
    ProgressDialog progressDialog;
    private RequestOptions options;
    private String transactionIdentifier;
    String msisdn;
    public RechargeWalletUtil(RechargeWalletUtilInteraction rechargeWalletUtilInteraction, Activity context, String msisdn, String token) {
        this.context = context;
        this.rechargeWalletUtilInteraction = rechargeWalletUtilInteraction;
        this.msisdn=msisdn;
        this.token=token;
        progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
    }

    public void startRecharge(View v, String amount){
        String paymentType = null;
        Log.d(tag, "Pay Button is triggered");

        if (!Util.isNetworkAvailable(context)) {
            Toast.makeText(context, "There is no Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }

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

            if (!TextUtils.isEmpty(amount) && isNumeric(amount)) {
                try {
                    int checkAmount = 0;
                    try {
                        checkAmount = Integer.parseInt(amount.trim());
                    } catch (Exception e) {
                        uiFeed("Invalid Amount");
                    }

                    if (checkAmount >= MPay.minTopUp) {
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
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                uiFeed("Amount should not be lower than 1 and should be numeric only");
            }
    }

    private void initiateRecharge(final InitiateWalletRecharge initiateWalletRecharge) {

        if (initiateWalletRecharge == null)
            return;

        try {
            Log.d(tag, "Data to Post on Server:\n" + new ClientData().mapping(initiateWalletRecharge));
            PaymentInterface paymentInterface = PaymentServerClient.getClient().create(PaymentInterface.class);
            Call<InitiateResponse> callService = paymentInterface.initPayment(token, initiateWalletRecharge);
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

    /**
     * The call of of interswitch payment gateway
     * @param uId Server provided transaction ID
     * @param paymentType interswitch supported payment Type
     * @param amount amount to be debited to customer
     * @param currency transaction allowed currency
     */
    private void processPayment(final String uId, final long paymentType, final String amount, String currency) {

        options = RequestOptions.builder().setClientId(Interswitching.CLIENT_ID)
                .setClientSecret(Interswitching.CLIENT_SECRET)
                .build();

        final PayWithCard payWithCard = new PayWithCard(context, msisdn, "Recharge AirTime Wallet", amount, currency, uId, options, new IswCallback<PurchaseResponse>() {

            @Override
            public void onError(Exception error) {
                Util.notify(context, "Error", error.getMessage(), "Close", false);
                rechargeWalletUtilInteraction.onRecargeWalletInteraction(101, error.toString());
                FailedPayment failedPayment = new FailedPayment(uId, StatusConfig.failedTransaction, error.getMessage());

                if (!progressDialog.isShowing()) {
                    progressDialog.setMessage("Reporting Failure...");
                    progressDialog.show();
                }else{
                    progressDialog.setMessage("Reporting Failure...");
                }
                failedPaymentMethod(failedPayment);
            }

            @Override
            public void onSuccess(final PurchaseResponse response) {

                transactionIdentifier = response.getTransactionIdentifier();
                rechargeWalletUtilInteraction.onRecargeWalletInteraction(100, "Successfully Money Debited " + response.getAmount() + " Identifier: " + transactionIdentifier + " Reference: " + response.getTransactionRef());
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
                }else{
                    progressDialog.setMessage("Recharging Wallet...");
                }
                confirmRecharge(confirmWalletPayment);
                Util.notify(context, "Success", "Ref: " + transactionIdentifier, "Close", false);
            }
        });
        payWithCard.start();
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
                                    rechargeWalletUtilInteraction.onRecargeWalletInteraction(1, "Wallet Credited " + confirmWalletPayment.getAmount());
                                } else {
                                    uiFeed(status.getStatus().getMessage());
                                    rechargeWalletUtilInteraction.onRecargeWalletInteraction(0, "Faillure: " + status.getStatus().getMessage());

                                }
                        } else {
                            rechargeWalletUtilInteraction.onRecargeWalletInteraction(0, "Faillure: " + response.message());
                            uiFeed(response.message());
                        }
                    } catch (final Exception e) {
                        rechargeWalletUtilInteraction.onRecargeWalletInteraction(0, "Faillure: " + e.getMessage());
                        uiFeed(e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<StatusUsage> call, Throwable t) {
                    // Log error here since request failed

                    rechargeWalletUtilInteraction.onRecargeWalletInteraction(0, "Recharging wallet Failed due to connectivity Error, If your account was debited Please contact System Admin. See Menu/About Us");
                    Log.e(tag, t.toString());
                    uiFeed("Recharging wallet Failed due to connectivity Error, If your account was debited Please contact System Admin. See Menu/About Us");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            rechargeWalletUtilInteraction.onRecargeWalletInteraction(0, e.getMessage());
            uiFeed(e.getMessage());
        }

    }
    private void failedPaymentMethod(final FailedPayment failedPayment) {

        if (failedPayment == null)
            return;

        try {
            Log.d(tag, "Data to Post on Server:\n" + new ClientData().mapping(failedPayment));
            PaymentInterface paymentInterface = PaymentServerClient.getClient().create(PaymentInterface.class);
            Call<StatusUsage> callService = paymentInterface.failedPayment(token, failedPayment);
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
                                    rechargeWalletUtilInteraction.onRecargeWalletInteraction(0, status.getStatus().getMessage());
                                    uiFeed(status.getStatus().getMessage());
                                } else {
                                    rechargeWalletUtilInteraction.onRecargeWalletInteraction(0, status.getStatus().getMessage());
                                    uiFeed(status.getStatus().getMessage());
                                }

                        } else {
                            rechargeWalletUtilInteraction.onRecargeWalletInteraction(1, response.message());
                            uiFeed(response.message());
                        }
                    } catch (final Exception e) {
                        rechargeWalletUtilInteraction.onRecargeWalletInteraction(1, e.getMessage());
                        uiFeed(e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<StatusUsage> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(tag, t.toString());
                    rechargeWalletUtilInteraction.onRecargeWalletInteraction(1, "Connectivity Error to report Failed Transaction");
                    uiFeed("Connectivity Error");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            rechargeWalletUtilInteraction.onRecargeWalletInteraction(1, e.getMessage());
            uiFeed(e.getMessage());
        }
    }

    private void uiFeed(String message) {
        if(progressDialog != null)
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
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

    public interface RechargeWalletUtilInteraction {
        void onRecargeWalletInteraction(int statusCode, String message);
    }
}
