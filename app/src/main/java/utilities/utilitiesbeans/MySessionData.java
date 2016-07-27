package utilities.utilitiesbeans;

/**
 * Created by Owner on 7/14/2016.
 */
public class MySessionData {
    private String token;
    private String msisdn;
    private String userName;

    public MySessionData() {
    }

    public MySessionData(String token, String msisdn, String userName) {

        this.setToken(token);
        this.setMsisdn(msisdn);
        this.setUserName(userName);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
