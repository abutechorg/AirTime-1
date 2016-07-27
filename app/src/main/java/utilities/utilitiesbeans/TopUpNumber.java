package utilities.utilitiesbeans;

/**
 * Created by Owner on 7/20/2016.
 */
public class TopUpNumber {
    private String msisdn;
    private double amount;

    public TopUpNumber(String msisdn, double amount) {
        this.setMsisdn(msisdn);
        this.setAmount(amount);
    }

    public TopUpNumber() {

    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
