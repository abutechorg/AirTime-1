package simplebeans.balancebeans;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Owner on 7/18/2016.
 */
public class BalanceRespopnse {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("transTime")
    private String transTime;
    @JsonProperty("amount")
    private double amount;
    @JsonProperty("srcrefId")
    private String  srcrefId;
    @JsonProperty("sourceType")
    private int sourceType;
    @JsonProperty("destRefId")
    private String  destRefId;
    @JsonProperty("destType")
    private int destType;
    @JsonProperty("prevBalance")
    private double prevBalance;
    @JsonProperty("newBalance")
    private double newBalance;
    @JsonProperty("transTypeId")
    private int transTypeId;
    @JsonProperty("token")
    private String token;

    public BalanceRespopnse(Long id, String transTime, double amount, String srcrefId, int sourceType, String destRefId, int destType, double prevBalance, double newBalance, int transTypeId, String token) {
        this.setId(id);
        this.setTransTime(transTime);
        this.setAmount(amount);
        this.setSrcrefId(srcrefId);
        this.setSourceType(sourceType);
        this.setDestRefId(destRefId);
        this.setDestType(destType);
        this.setPrevBalance(prevBalance);
        this.setNewBalance(newBalance);
        this.setTransTypeId(transTypeId);
        this.setToken(token);
    }

    public BalanceRespopnse() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransTime() {
        return transTime;
    }

    public void setTransTime(String transTime) {
        this.transTime = transTime;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getSrcrefId() {
        return srcrefId;
    }

    public void setSrcrefId(String srcrefId) {
        this.srcrefId = srcrefId;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public String getDestRefId() {
        return destRefId;
    }

    public void setDestRefId(String destRefId) {
        this.destRefId = destRefId;
    }

    public int getDestType() {
        return destType;
    }

    public void setDestType(int destType) {
        this.destType = destType;
    }

    public double getPrevBalance() {
        return prevBalance;
    }

    public void setPrevBalance(double prevBalance) {
        this.prevBalance = prevBalance;
    }

    public double getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(double newBalance) {
        this.newBalance = newBalance;
    }

    public int getTransTypeId() {
        return transTypeId;
    }

    public void setTransTypeId(int transTypeId) {
        this.transTypeId = transTypeId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
