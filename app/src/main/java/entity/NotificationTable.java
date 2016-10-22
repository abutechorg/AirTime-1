package entity;

import com.orm.SugarRecord;

/**
 * Created by Hp on 10/14/2016.
 */
public class NotificationTable extends SugarRecord {
    private String date;
    private String message;
    private String msisdn;
    private String seen;

    public NotificationTable(String date, String message, String msisdn, String seen) {
        this.setDate(date);
        this.setMessage(message);
        this.setMsisdn(msisdn);
        this.setSeen(seen);
    }

    public NotificationTable() {

    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }
}

