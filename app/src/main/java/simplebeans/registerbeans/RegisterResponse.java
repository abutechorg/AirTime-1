package simplebeans.registerbeans;

import com.fasterxml.jackson.annotation.JsonProperty;

import simplebeans.SimpleStatusBean;

/**
 * Created by Owner on 7/9/2016.
 */
public class RegisterResponse {
    @JsonProperty("customerDetails")
    private RegisterResponseBean registerResponseBean;
    @JsonProperty("status")
    private SimpleStatusBean simpleStatusBean;

    public RegisterResponse() {
    }

    public RegisterResponse(RegisterResponseBean registerResponseBean, SimpleStatusBean simpleStatusBean) {

        this.setRegisterResponseBean(registerResponseBean);
        this.setSimpleStatusBean(simpleStatusBean);
    }

    public RegisterResponseBean getRegisterResponseBean() {
        return registerResponseBean;
    }

    public void setRegisterResponseBean(RegisterResponseBean registerResponseBean) {
        this.registerResponseBean = registerResponseBean;
    }

    public SimpleStatusBean getSimpleStatusBean() {
        return simpleStatusBean;
    }

    public void setSimpleStatusBean(SimpleStatusBean simpleStatusBean) {
        this.simpleStatusBean = simpleStatusBean;
    }
}
