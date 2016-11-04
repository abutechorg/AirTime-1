package utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.oltranz.mobilea.mobilea.R;

import client.ClientData;
import client.ClientServices;
import client.ServerClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import simplebeans.balancebeans.BalanceResponse;

/**
 * Created by Hp on 10/17/2016.
 */
public class CheckWalletBalance {
    private String tag = "AirTime: " + getClass().getSimpleName();
    Context context;
    String token;
//    private ProgressDialog progressDialog;

    CheckWalletBalanceInteraction checkBalance;

    public CheckWalletBalance(CheckWalletBalanceInteraction checkBalance, Context context, String token) {
        this.checkBalance = checkBalance;
        this.context = context;
        this.token = token;

//        progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setCanceledOnTouchOutside(true);
//        progressDialog.setCancelable(true);
    }

    public void getBalance() {
//        if(progressDialog!= null)
//            if(!progressDialog.isShowing()){
//        progressDialog.setMessage("Checking wallet balance...");
//        progressDialog.show();
//            }
        try {
            ClientServices clientServices = ServerClient.getClient().create(ClientServices.class);
            Call<BalanceResponse> callService = clientServices.getWalletBalance(token);
            callService.enqueue(new Callback<BalanceResponse>() {
                @Override
                public void onResponse(Call<BalanceResponse> call, Response<BalanceResponse> response) {
//                    if (progressDialog != null)
//                        if (progressDialog.isShowing())
//                            progressDialog.dismiss();
                    //HTTP status code
                    int statusCode = response.code();
                    if (statusCode == 200) {
                        try {
                            Log.d(tag, "Data from the server:\n" + new ClientData().mapping(response.body()));
                            BalanceResponse balanceResponse = response.body();
                            //handle the response from the server
                            if (balanceResponse.getBalance() != null) {
                                checkBalance.onWalletBalanceCheck(balanceResponse.getBalance(), balanceResponse.getLastTxTime());
                            } else {
                                checkBalance.onWalletBalanceCheck("0","000/00/00 00:00");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            checkBalance.onWalletBalanceCheck("0","000/00/00 00:00");
                        }
                    } else {
                        checkBalance.onWalletBalanceCheck("0","000/00/00 00:00");
                    }
                }

                @Override
                public void onFailure(Call<BalanceResponse> call, Throwable t) {
                    // Log error here since request failed
//                    if (progressDialog != null)
//                        if (progressDialog.isShowing())
//                            progressDialog.dismiss();
                    checkBalance.onWalletBalanceCheck("0", "000/00/00 00:00");
                }
            });
        } catch (Exception e) {
//            if (progressDialog != null)
//                if (progressDialog.isShowing())
//                    progressDialog.dismiss();
            checkBalance.onWalletBalanceCheck("0","000/00/00 00:00");
        }
    }

    public interface CheckWalletBalanceInteraction {
        void onWalletBalanceCheck(String balance, String date);
    }
}
