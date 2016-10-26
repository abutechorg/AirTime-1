package config;

/**
 * Created by Owner on 7/9/2016.
 */
public class BaseUrl {
//    public static final String baseUrl="http://41.74.172.132:8080/AirtimeRechargeSystem/";//must end with(/)
    public static final String baseUrl = "http://197.210.2.229/AirtimeRechargeSystemCore/";
    public static final String paymentBaseUrl = "http://197.210.2.229/AndroidPayment/";
    public static final String initPay = "Payment/init";
    public static final String confPay = "Payment/confirmPayment";
    public static final String failedPay = "Payment/failed";
    public static final String registerUrl = "customer/register";
    public static final String loginUrl = "customer/login";
    public static final String referrerurl = "customer/refer";
    public static final String checkBalanceUrl = "wallets/CustomerWalletBalance";
    public static final String topUpUrl = "airtimetopup/topup";
    public static final String paymentModes = "topupmodes/alltypes";
    public static final String initiateWalletRecharge = "interswitch/initiation";
    public static final String confirmWalletRecharge = "interswitch/newpayment";
    public static final String offerUrl = "http://mobilea.ng/faces/android/moffers.xhtml";
    public static final String contactUrl = "http://mobilea.ng/faces/android/mcontactus.xhtml";
    public static final String helpUrl = "http://mobilea.ng/faces/mhelp.xhtml";
    public static final String termsUrl = "http://mobilea.ng/faces/mterm.xhtml";
    public static final String headerToken = "Token";
    public static final String appVersion = "Version";
    public static final String serverDateUrl = "Payment/serverDate";
    public static final String transactionHistory = "airtimetopup/getlatest/";
    public static final String getFavorites = "airtimetopup/getfavorites/";
}