package config;

/**
 * Created by Owner on 7/9/2016.
 */
public class BaseUrl {
    public static final String baseUrl="http://41.74.172.132:8080/AirtimeRechargeSystem/";//must end with(/)
    public static final String mPay="http://41.74.172.132:8080/Mpay/";
    public static final String registerUrl="customer/register";
    public static final String loginUrl="customer/login";
    public static final String checkBalanceUrl="wallettransactions/balance";
    public static final String topUpUrl="purchase/airtime";
    public static final String paymentModes="topupmodes/alltypes";
    public static final String initiateWalletRecharge = "interswitch/initiation";
    public static final String confirmWalletRecharge = "interswitch/newpayment";
    public static final String helpUrl = "http://41.74.172.132:8080/AirtimeWebPortal/faces/how.xhtml";
    public static final String termsUrl = "http://41.74.172.132:8080/AirtimeWebPortal/faces/term.xhtml";
}