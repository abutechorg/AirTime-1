package client;

import config.BaseUrl;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import simplebeans.StatusUsage;
import simplebeans.balancebeans.BalanceRespopnse;
import simplebeans.loginbeans.LoginRequest;
import simplebeans.loginbeans.LoginResponse;
import simplebeans.payments.PaymentModesResponse;
import simplebeans.registerbeans.RegisterRequest;
import simplebeans.registerbeans.RegisterResponse;
import simplebeans.topupbeans.TopUpRequest;
import simplebeans.topupbeans.TopUpResponse;
import simplebeans.walletpayment.ConfirmWalletPayment;
import simplebeans.walletpayment.InitiateWalletRecharge;

/**
 * Created by Owner on 7/9/2016.
 */
public interface ClientServices {
    //login client service
    @POST(BaseUrl.loginUrl)
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    //register client service
    @POST(BaseUrl.registerUrl)
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    //get Wallet balance
    @GET(BaseUrl.checkBalanceUrl+"/{msisdn}")
    Call<BalanceRespopnse> getWalletBalance(@Path("msisdn") String msisdn);

    //topUp Airtime
    @POST(BaseUrl.topUpUrl)
    Call<TopUpResponse> topUp(@Body TopUpRequest topUpRequest);

    //getPayment mode list
    @GET(BaseUrl.paymentModes)
    Call<PaymentModesResponse> getPaymentModes();

    //initiate wallet recharge
    @POST(BaseUrl.initiateWalletRecharge)
    Call<StatusUsage> initWalletRecharge(@Body InitiateWalletRecharge initiateWalletRecharge);

    //confirm wallet recharge
    @POST(BaseUrl.confirmWalletRecharge)
    Call<StatusUsage> confWalletRecharge(@Body ConfirmWalletPayment confirmWalletPayment);
}
