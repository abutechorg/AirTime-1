package simplebeans.payments;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Owner on 7/25/2016.
 */
public class PaymentModeBean {
    @JsonProperty("id")
    private long id;
    @JsonProperty("imageurl")
    private String iconUrl;
    @JsonProperty("transactionSourceTypeName")
    private String transactionSource;

    public PaymentModeBean(long id, String iconUrl, String transactionSource) {
        this.setId(id);
        this.setIconUrl(iconUrl);
        this.setTransactionSource(transactionSource);
    }

    public PaymentModeBean() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getTransactionSource() {
        return transactionSource;
    }

    public void setTransactionSource(String transactionSource) {
        this.transactionSource = transactionSource;
    }
}
