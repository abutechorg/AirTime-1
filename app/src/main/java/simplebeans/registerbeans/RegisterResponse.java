package simplebeans.registerbeans;

import com.fasterxml.jackson.annotation.JsonProperty;

import simplebeans.ResponseStatusSimpleBean;

/**
 * Created by Owner on 7/9/2016.
 */
public class RegisterResponse {
    @JsonProperty("customerDetails")
    private RegisterResponseBean registerResponseBean;
    @JsonProperty("status")
    private ResponseStatusSimpleBean responseStatusSimpleBean;

    public RegisterResponse() {
    }

    public RegisterResponse(RegisterResponseBean registerResponseBean, ResponseStatusSimpleBean responseStatusSimpleBean) {

        this.setRegisterResponseBean(registerResponseBean);
        this.setResponseStatusSimpleBean(responseStatusSimpleBean);
    }

    public RegisterResponseBean getRegisterResponseBean() {
        return registerResponseBean;
    }

    public void setRegisterResponseBean(RegisterResponseBean registerResponseBean) {
        this.registerResponseBean = registerResponseBean;
    }

    public ResponseStatusSimpleBean getResponseStatusSimpleBean() {
        return responseStatusSimpleBean;
    }

    public void setResponseStatusSimpleBean(ResponseStatusSimpleBean responseStatusSimpleBean) {
        this.responseStatusSimpleBean = responseStatusSimpleBean;
    }
}
