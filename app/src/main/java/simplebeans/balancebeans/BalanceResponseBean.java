package simplebeans.balancebeans;

/**
 * Created by Owner on 7/19/2016.
 */
public class BalanceResponseBean {
    private String balance;
    private String lastAcountActivity;

    public BalanceResponseBean(String balance, String lastAcountActivity) {
        this.setBalance(balance);
        this.setLastAcountActivity(lastAcountActivity);
    }

    public BalanceResponseBean() {

    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getLastAcountActivity() {
        return lastAcountActivity;
    }

    public void setLastAcountActivity(String lastAcountActivity) {
        this.lastAcountActivity = lastAcountActivity;
    }
}
