package simplebeans.loginbeans;

import com.fasterxml.jackson.annotation.JsonProperty;

import simplebeans.ResponseStatusSimpleBean;

/**
 * Created by Owner on 7/9/2016.
 */
public class LoginResponse {
    @JsonProperty("customerDetails")
    private LoginResponseBean loginResponseBean;
    @JsonProperty("status")
    private ResponseStatusSimpleBean responseStatusSimpleBean;

    public LoginResponse() {
    }

    public LoginResponse(LoginResponseBean loginResponseBean, ResponseStatusSimpleBean responseStatusSimpleBean) {

        this.setLoginResponseBean(loginResponseBean);
        this.setResponseStatusSimpleBean(responseStatusSimpleBean);
    }

    public LoginResponseBean getLoginResponseBean() {
        return loginResponseBean;
    }

    public void setLoginResponseBean(LoginResponseBean loginResponseBean) {
        this.loginResponseBean = loginResponseBean;
    }

    public ResponseStatusSimpleBean getResponseStatusSimpleBean() {
        return responseStatusSimpleBean;
    }

    public void setResponseStatusSimpleBean(ResponseStatusSimpleBean responseStatusSimpleBean) {
        this.responseStatusSimpleBean = responseStatusSimpleBean;
    }
}
