package simplebeans.payments;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import simplebeans.ResponseStatusSimpleBean;

/**
 * Created by Owner on 7/25/2016.
 */
public class PaymentModesResponse {
    @JsonProperty("status")
    private ResponseStatusSimpleBean responseStatusSimpleBean;
    @JsonProperty("topupmodes")
    private List<PaymentModeBean> paymentModeBeanList;

    public PaymentModesResponse(ResponseStatusSimpleBean responseStatusSimpleBean, List<PaymentModeBean> paymentModeBeanList) {
        this.setResponseStatusSimpleBean(responseStatusSimpleBean);
        this.setPaymentModeBeanList(paymentModeBeanList);
    }

    public PaymentModesResponse() {

    }

    public ResponseStatusSimpleBean getResponseStatusSimpleBean() {
        return responseStatusSimpleBean;
    }

    public void setResponseStatusSimpleBean(ResponseStatusSimpleBean responseStatusSimpleBean) {
        this.responseStatusSimpleBean = responseStatusSimpleBean;
    }

    public List<PaymentModeBean> getPaymentModeBeanList() {
        return paymentModeBeanList;
    }

    public void setPaymentModeBeanList(List<PaymentModeBean> paymentModeBeanList) {
        this.paymentModeBeanList = paymentModeBeanList;
    }
}
