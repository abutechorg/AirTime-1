package simplebeans.payments;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import simplebeans.SimpleStatusBean;

/**
 * Created by Owner on 7/25/2016.
 */
public class PaymentModesResponse {
    @JsonProperty("status")
    private SimpleStatusBean simpleStatusBean;
    @JsonProperty("topupmodes")
    private List<PaymentModeBean> paymentModeBeanList;

    public PaymentModesResponse(SimpleStatusBean simpleStatusBean, List<PaymentModeBean> paymentModeBeanList) {
        this.setSimpleStatusBean(simpleStatusBean);
        this.setPaymentModeBeanList(paymentModeBeanList);
    }

    public PaymentModesResponse() {

    }

    public SimpleStatusBean getSimpleStatusBean() {
        return simpleStatusBean;
    }

    public void setSimpleStatusBean(SimpleStatusBean simpleStatusBean) {
        this.simpleStatusBean = simpleStatusBean;
    }

    public List<PaymentModeBean> getPaymentModeBeanList() {
        return paymentModeBeanList;
    }

    public void setPaymentModeBeanList(List<PaymentModeBean> paymentModeBeanList) {
        this.paymentModeBeanList = paymentModeBeanList;
    }
}
