package simplebeans.walletpayment;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Hp on 9/10/2016.
 */
public class InitiateWalletRecharge {
    @JsonProperty("paymentSpId")
    private String paymentSpId;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("msisdn")
    private String msisdn;

    @JsonProperty("paymentType")
    private String paymentType;

    @JsonProperty("status")
    private long status;

    @JsonProperty("message")
    private String message;

    public InitiateWalletRecharge(String paymentSpId, String amount, String msisdn, String paymentType, long status, String message) {
        this.setPaymentSpId(paymentSpId);
        this.setAmount(amount);
        this.setMsisdn(msisdn);
        this.setPaymentType(paymentType);
        this.setStatus(status);
        this.setMessage(message);
    }

    public InitiateWalletRecharge() {

    }

    public String getPaymentSpId() {
        return paymentSpId;
    }

    public void setPaymentSpId(String paymentSpId) {
        this.paymentSpId = paymentSpId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
