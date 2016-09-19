package simplebeans.transactionhistory;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import simplebeans.ResponseStatusSimpleBean;

/**
 * Created by Owner on 8/20/2016.
 */
public class TransactionHistoryResponse {
    @JsonProperty("walletHistory")
    private
    List<TransactionHistoryBean> history;
    @JsonProperty("status")
    private
    ResponseStatusSimpleBean status;

    public TransactionHistoryResponse(List<TransactionHistoryBean> history, ResponseStatusSimpleBean status) {
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

    public ResponseStatusSimpleBean getStatus() {
        return status;
    }

    public void setStatus(ResponseStatusSimpleBean status) {
        this.status = status;
    }
}
