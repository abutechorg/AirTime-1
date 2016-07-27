package simplebeans.topupbeans;

import com.fasterxml.jackson.annotation.JsonProperty;

import simplebeans.ResponseStatusSimpleBean;

/**
 * Created by Owner on 7/19/2016.
 */

public class TopUpResponse {
    @JsonProperty("status")
    private ResponseStatusSimpleBean responseStatusSimpleBean;

    public TopUpResponse(ResponseStatusSimpleBean responseStatusSimpleBean) {
        this.setResponseStatusSimpleBean(responseStatusSimpleBean);
    }

    public TopUpResponse() {

    }

    public ResponseStatusSimpleBean getResponseStatusSimpleBean() {
        return responseStatusSimpleBean;
    }

    public void setResponseStatusSimpleBean(ResponseStatusSimpleBean responseStatusSimpleBean) {
        this.responseStatusSimpleBean = responseStatusSimpleBean;
    }
}
