package simplebeans.transactionhistory;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import simplebeans.SimpleStatusBean;

/**
 * Created by Owner on 8/20/2016.
 */
public class TransactionHistoryResponse {
    @JsonProperty("destinations")
    private
    List<TransactionHistoryBean> history;
    @JsonProperty("status")
    private
    SimpleStatusBean status;

    public TransactionHistoryResponse(List<TransactionHistoryBean> history, SimpleStatusBean status) {
        this.setHistory(history);
        this.setStatus(status);
    }

    public TransactionHistoryResponse() {

    }

    public List<TransactionHistoryBean> getHistory() {
        return history;
    }

    public void setHistory(List<TransactionHistoryBean> history) {
        this.history = history;
    }

    public SimpleStatusBean getStatus() {
        return status;
    }

    public void setStatus(SimpleStatusBean status) {
        this.status = status;
    }
}
