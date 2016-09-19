package utilities.utilitiesbeans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Owner on 7/14/2016.
 */
public class MySessionData implements Parcelable {
    public static final Parcelable.Creator<MySessionData> CREATOR = new Parcelable.Creator<MySessionData>() {

        public MySessionData createFromParcel(Parcel in) {
            return new MySessionData(in);
        }

        public MySessionData[] newArray(int size) {
            return new MySessionData[size];
        }
    };
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

    /**
     * recreate object from parcel
     */
    private MySessionData(Parcel in) {
        token = in.readString();
        msisdn = in.readString();
        userName = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(token);
        dest.writeString(msisdn);
        dest.writeString(userName);
    }
}
