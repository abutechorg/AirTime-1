package simplebeans.changepin;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Hp on 9/23/2016.
 */
public class ChangePinBean {
    @JsonProperty("oldPassword")
    private String oldPassword;
    @JsonProperty("newTypedPassword")
    private String newTypedPassword;
    @JsonProperty("confirmNewPassword")
    private String confirmNewPassword;

    public ChangePinBean(String oldPassword, String newTypedPassword, String confirmNewPassword) {
        this.setOldPassword(oldPassword);
        this.setNewTypedPassword(newTypedPassword);
        this.setConfirmNewPassword(confirmNewPassword);
    }

    public ChangePinBean() {

    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewTypedPassword() {
        return newTypedPassword;
    }

    public void setNewTypedPassword(String newTypedPassword) {
        this.newTypedPassword = newTypedPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }
}
