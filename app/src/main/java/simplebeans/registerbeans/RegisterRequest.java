package simplebeans.registerbeans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Created by Owner on 7/9/2016.
 */
@JsonRootName("registerRequest")
public class RegisterRequest {
    @JsonProperty("fName")
    private String fName;
    @JsonProperty("otherNames")
    private String otherNames;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("imei")
    private String imei;
    @JsonProperty("currentTime")
    private String currentTime;
    @JsonProperty("pin")
    private String pin;
    @JsonProperty("email")
    private String email;
    @JsonProperty("phoneSerialNumber")
    private String phoneSerialNumber;
    @JsonProperty("OSversion")
    private String OSversion;

    public RegisterRequest() {
    }

    public RegisterRequest(String fName, String otherNames, String msisdn, String imei, String currentTime, String pin, String email, String phoneSerialNumber, String OSversion) {
        this.fName = fName;
        this.otherNames = otherNames;
        this.msisdn = msisdn;
        this.imei = imei;
        this.currentTime = currentTime;
        this.pin = pin;
        this.email = email;
        this.phoneSerialNumber = phoneSerialNumber;
        this.OSversion = OSversion;
    }

    /**
     * @return the fName
     */
    public String getfName() {
        return fName;
    }

    /**
     * @param fName the fName to set
     */
    public void setfName(String fName) {
        this.fName = fName;
    }

    /**
     * @return the OtherNames
     */
    public String getOtherNames() {
        return otherNames;
    }

    /**
     * @param otherNames the OtherNames to set
     */
    public void setOtherNames(String otherNames) {
        this.otherNames = otherNames;
    }

    /**
     * @return the msisdn
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * @param msisdn the msisdn to set
     */
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    /**
     * @return the pin
     */
    public String getPin() {
        return pin;
    }

    /**
     * @param pin the pin to set
     */
    public void setPin(String pin) {
        this.pin = pin;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the phoneSerialNumber
     */
    public String getPhoneSerialNumber() {
        return phoneSerialNumber;
    }

    /**
     * @param phoneSerialNumber the phoneSerialNumber to set
     */
    public void setPhoneSerialNumber(String phoneSerialNumber) {
        this.phoneSerialNumber = phoneSerialNumber;
    }

    /**
     * @return the OSversion
     */
    public String getOSversion() {
        return OSversion;
    }

    /**
     * @param OSversion the OSversion to set
     */
    public void setOSversion(String OSversion) {
        this.OSversion = OSversion;
    }

    /**
     * @return the imei
     */
    public String getImei() {
        return imei;
    }

    /**
     * @param imei the imei to set
     */
    public void setImei(String imei) {
        this.imei = imei;
    }

    /**
     * @return the currentTime
     */
    public String getCurrentTime() {
        return currentTime;
    }

    /**
     * @param currentTime the currentTime to set
     */
    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }
}
