package client;

import config.BaseUrl;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import simplebeans.StatusUsage;
import simplebeans.datebeans.ServerDate;
import simplebeans.loginbeans.LoginRequest;
import simplebeans.loginbeans.LoginResponse;
import simplebeans.walletpayment.ConfirmWalletPayment;
import simplebeans.walletpayment.FailedPayment;
import simplebeans.walletpayment.InitiateResponse;
import simplebeans.walletpayment.InitiateWalletRecharge;

/**
 * Created by Hp on 10/7/2016.
 */
public interface PaymentInterface {
    //Init Payment
    @POST(BaseUrl.initPay)
    Call<InitiateResponse> initPayment(@Body InitiateWalletRecharge initiateWalletRecharge);

    //Confirm Payment
    @POST(BaseUrl.confPay)
    Call<StatusUsage> confirmPayment(@Header(BaseUrl.headerToken) String token, @Body ConfirmWalletPayment confirmWalletPayment);

    //Failed Payment
    @POST(BaseUrl.failedPay)
    Call<StatusUsage> failedPayment(@Body FailedPayment failedPayment);

    //ServeerTime
    @GET(BaseUrl.serverDateUrl)
    Call<ServerDate> serverDate();
}
