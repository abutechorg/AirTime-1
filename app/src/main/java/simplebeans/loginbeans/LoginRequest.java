package simplebeans.loginbeans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Created by Owner on 7/9/2016.
 */
@JsonRootName("loginRequest")
public class LoginRequest {
    @JsonProperty("pin")
    private String pin;
    @JsonProperty("msisdn")
    private String msisdn;
    @JsonProperty("currentTime")
    private String currentTime;
    @JsonProperty("serialNumber")
    private String serialNumber;
    @JsonProperty("imei")
    private String imei;
    @JsonProperty("osVersion")
    private String osVersion;

    public LoginRequest(String pin, String msisdn, String currentTime, String serialNumber, String imei, String osVersion) {
        this.pin = pin;
        this.msisdn = msisdn;
        this.currentTime = currentTime;
        this.serialNumber = serialNumber;
        this.imei = imei;
        this.osVersion = osVersion;
    }

    public LoginRequest() {
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
     * @return the serialNumber
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * @param serialNumber the serialNumber to set
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
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
     * @return the osVersion
     */
    public String getOsVersion() {
        return osVersion;
    }

    /**
     * @param osVersion the osVersion to set
     */
    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
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
