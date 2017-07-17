package client;

import config.BaseUrl;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import simplebeans.SimpleStatusBean;
import simplebeans.StatusUsage;
import simplebeans.balancebeans.BalanceResponse;
import simplebeans.loginbeans.LoginRequest;
import simplebeans.loginbeans.LoginResponse;
import simplebeans.payments.PaymentModesResponse;
import simplebeans.registerbeans.RegisterRequest;
import simplebeans.registerbeans.RegisterResponse;
import simplebeans.topupbeans.TopUpRequest;
import simplebeans.topupbeans.TopUpResponse;
import simplebeans.transactionhistory.TransactionHistoryResponse;
import simplebeans.walletpayment.ConfirmWalletPayment;
import simplebeans.walletpayment.InitiateWalletRecharge;

/**
 * Created by Eng. ISHIMWE Aubain Consolateur email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: (250) 785 534 672 on 7/9/2016.
 * Client service is an interface for general user activities service generation
 */
public interface ClientServices {

    /**
     * API login service call
     * @param version application version
     * @param loginRequest login credential object
     * @return LoginResponse
     */
    //login client service
    @POST(BaseUrl.loginUrl)
    Call<LoginResponse> loginUser(@Header(BaseUrl.appVersion) String version, @Body LoginRequest loginRequest);

    /**
     * API Referrer service
     * @param version application version
     * @param token session token
     * @param number referrer msisdn
     * @return SimpleStatusBean
     */
    //referrer client service
    @POST(BaseUrl.referrerurl+"/{number}")
    Call<SimpleStatusBean> referrer(@Header(BaseUrl.appVersion) String version, @Header(BaseUrl.headerToken) String token, @Path("number") String number);

    /**
     * API Sign up or User registration
     * @param registerRequest registration data
     * @return registerResponse
     */
    //register client service
    @POST(BaseUrl.registerUrl)
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    /**
     * API user wallet balance
     * @param tokenHeader session token
     * @return BalanceResponse
     */
    //get Wallet balance
    @GET(BaseUrl.checkBalanceUrl)
    Call<BalanceResponse> getWalletBalance(@Header(BaseUrl.headerToken) String tokenHeader);

    /**
     * API airtime topup
     * @param token session token
     * @param topUpRequest topup data
     * @return topupResponse
     */
    //topUp Airtime
    @POST(BaseUrl.topUpUrl)
    Call<TopUpResponse> topUp(@Header(BaseUrl.headerToken) String token, @Body TopUpRequest topUpRequest);

    /**
     * API payment methods
     * @return PaymentModesResponse
     */
    //getPayment mode list
    @GET(BaseUrl.paymentModes)
    Call<PaymentModesResponse> getPaymentModes();

    /**
     * API get user's transaction
     * @param token session token
     * @param number number of record needed
     * @return TransactionHistoryResponse
     */
    //getTopup history
    @GET(BaseUrl.transactionHistory+"{number}")
    Call<TransactionHistoryResponse> getTransactionHistory(@Header(BaseUrl.headerToken) String token, @Path("number") int number);

    /**
     * API service for initiation of payment
     * @param initiateWalletRecharge wallet recharge initiation data
     * @return StatusUsage
     */
    //initiate wallet recharge
    @POST(BaseUrl.initiateWalletRecharge)
    Call<StatusUsage> initWalletRecharge(@Body InitiateWalletRecharge initiateWalletRecharge);

    /**
     * API for confirmation of wallet recharge
     * @param confirmWalletPayment wallet recharge confirmation
     * @return StatusUsage
     */
    //confirm wallet recharge
    @POST(BaseUrl.confirmWalletRecharge)
    Call<StatusUsage> confWalletRecharge(@Body ConfirmWalletPayment confirmWalletPayment);

    /**
     * API for user getting their top up favorites
     * @param token session token
     * @param number the number of favorite records that are needed
     * @return TransactionHistoryResponse
     */
    //getTopup favorites
    @GET(BaseUrl.transactionHistory+"{number}")
    Call<TransactionHistoryResponse> getFavorites(@Header(BaseUrl.headerToken) String token, @Path("number") int number);

}
