package entity;

import com.orm.SugarRecord;

/**
 * Created by Hp on 10/19/2016.
 */
public class Payments extends SugarRecord {
    private String initUid;
    private String sPPaymentRef;
    private long paymentSPId;
    private long paymentTypeId;
    private String amount;
    private String msisdn;
    private String transactionRef;
    private String cardType;

    public Payments(String initUid, String sPPaymentRef, long paymentSPId, long paymentTypeId, String amount, String msisdn, String transactionRef, String cardType) {
        this.setInitUid(initUid);
        this.setsPPaymentRef(sPPaymentRef);
        this.setPaymentSPId(paymentSPId);
        this.setPaymentTypeId(paymentTypeId);
        this.setAmount(amount);
        this.setMsisdn(msisdn);
        this.setTransactionRef(transactionRef);
        this.setCardType(cardType);
    }

    public Payments() {

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
