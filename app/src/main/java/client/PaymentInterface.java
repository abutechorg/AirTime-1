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
 * Created by Eng. ISHIMWE Aubain Consolateur email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: (250) 785 534 672 on 10/7/2016.
 * PaymentInterface is an interface which serve the purpose of payment API services
 */
public interface PaymentInterface {
    /**
     * API payment initiation
     * @param token session token
     * @param initiateWalletRecharge initiating session data
     * @return InitiateResponse
     */
    //Init Payment
    @POST(BaseUrl.initPay)
    Call<InitiateResponse> initPayment(@Header(BaseUrl.headerToken) String token, @Body InitiateWalletRecharge initiateWalletRecharge);

    /**
     * API confirm payment which has been initiated
     * @param token session token
     * @param confirmWalletPayment confirmation of initiated wallet recharge
     * @return StatusUsage
     */
    //Confirm Payment
    @POST(BaseUrl.confPay)
    Call<StatusUsage> confirmPayment(@Header(BaseUrl.headerToken) String token, @Body ConfirmWalletPayment confirmWalletPayment);

    /**
     * API for cancelling or notifying a failed transaction
     * @param token session token
     * @param failedPayment failed transaction data
     * @return StatusUsage
     */
    //Failed Payment
    @POST(BaseUrl.failedPay)
    Call<StatusUsage> failedPayment(@Header(BaseUrl.headerToken) String token, @Body FailedPayment failedPayment);

    /**
     * API for syncing the server time with device time
     * @return ServerDate
     */
    //ServeerTime
    @GET(BaseUrl.serverDateUrl)
    Call<ServerDate> serverDate();
}
