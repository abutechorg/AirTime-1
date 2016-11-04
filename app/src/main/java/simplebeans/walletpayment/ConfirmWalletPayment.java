package simplebeans.walletpayment;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Hp on 9/10/2016.
 */
public class ConfirmWalletPayment {
    @JsonProperty("initUid")
    private String initUid;
    @JsonProperty("sPPaymentRef")
    private String sPPaymentRef;
    @JsonProperty("paymentSPId")
    private long paymentSPId;
    @JsonProperty("paymentTypeId")
    private long paymentTypeId;
    @JsonProperty("amount")
    private String amount;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("transactionRef")
    private String transactionRef;
    @JsonProperty("cardType")
    private String cardType;

    public ConfirmWalletPayment(String initUid, String sPPaymentRef, long paymentSPId, long paymentTypeId, String amount, String msisdn, String transactionRef, String cardType) {
        this.setInitUid(initUid);
        this.setsPPaymentRef(sPPaymentRef);
        this.setPaymentSPId(paymentSPId);
        this.setPaymentTypeId(paymentTypeId);
        this.setAmount(amount);
        this.setMsisdn(msisdn);
        this.setTransactionRef(transactionRef);
        this.setCardType(cardType);
    }

    public ConfirmWalletPayment() {

    }

    public String getInitUid() {
        return initUid;
    }

    public void setInitUid(String initUid) {
        this.initUid = initUid;
    }

    public String getsPPaymentRef() {
        return sPPaymentRef;
    }

    public void setsPPaymentRef(String sPPaymentRef) {
        this.sPPaymentRef = sPPaymentRef;
    }

    public long getPaymentSPId() {
        return paymentSPId;
    }

    public void setPaymentSPId(long paymentSPId) {
        this.paymentSPId = paymentSPId;
    }

    public long getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(long paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
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

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
}
