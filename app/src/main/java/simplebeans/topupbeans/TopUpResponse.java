package simplebeans.topupbeans;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import simplebeans.SimpleStatusBean;

/**
 * Created by Owner on 7/19/2016.
 */

public class TopUpResponse {
    @JsonProperty("destinations")
    private
    List<TopUpResponseBean> topUpResponseBean;
    @JsonProperty("status")
    private
    SimpleStatusBean status;

    public TopUpResponse() {
    }

    public TopUpResponse(List<TopUpResponseBean> topUpResponseBean, SimpleStatusBean status) {
        this.setTopUpResponseBean(topUpResponseBean);
        this.setStatus(status);
    }

    public List<TopUpResponseBean> getTopUpResponseBean() {
        return topUpResponseBean;
    }

    public void setTopUpResponseBean(List<TopUpResponseBean> topUpResponseBean) {
        this.topUpResponseBean = topUpResponseBean;
    }

    public SimpleStatusBean getStatus() {
        return status;
    }

    public void setStatus(SimpleStatusBean status) {
        this.status = status;
    }
}
