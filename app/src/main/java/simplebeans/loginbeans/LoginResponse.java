package simplebeans.loginbeans;

import com.fasterxml.jackson.annotation.JsonProperty;

import simplebeans.SimpleStatusBean;

/**
 * Created by Owner on 7/9/2016.
 */
public class LoginResponse {
    @JsonProperty("customerDetails")
    private LoginResponseBean loginResponseBean;
    @JsonProperty("status")
    private SimpleStatusBean simpleStatusBean;

    public LoginResponse() {
    }

    public LoginResponse(LoginResponseBean loginResponseBean, SimpleStatusBean simpleStatusBean) {

        this.setLoginResponseBean(loginResponseBean);
        this.setSimpleStatusBean(simpleStatusBean);
    }

    public LoginResponseBean getLoginResponseBean() {
        return loginResponseBean;
    }

    public void setLoginResponseBean(LoginResponseBean loginResponseBean) {
        this.loginResponseBean = loginResponseBean;
    }

    public SimpleStatusBean getSimpleStatusBean() {
        return simpleStatusBean;
    }

    public void setSimpleStatusBean(SimpleStatusBean simpleStatusBean) {
        this.simpleStatusBean = simpleStatusBean;
    }
}
