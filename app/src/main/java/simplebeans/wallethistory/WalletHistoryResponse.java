package simplebeans.wallethistory;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import simplebeans.SimpleStatusBean;

/**
 * Created by Owner on 8/20/2016.
 */
public class WalletHistoryResponse {
    @JsonProperty("walletHistory")
    private
    List<WalletHistoryBean> history;
    @JsonProperty("status")
    private
    SimpleStatusBean status;

    public WalletHistoryResponse(List<WalletHistoryBean> history, SimpleStatusBean status) {
        this.setHistory(history);
        this.setStatus(status);
    }

    public WalletHistoryResponse() {

    }

    public List<WalletHistoryBean> getHistory() {
        return history;
    }

    public void setHistory(List<WalletHistoryBean> history) {
        this.history = history;
    }

    public SimpleStatusBean getStatus() {
        return status;
    }

    public void setStatus(SimpleStatusBean status) {
        this.status = status;
    }
}
