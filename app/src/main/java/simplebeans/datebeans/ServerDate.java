package simplebeans.datebeans;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by Hp on 10/13/2016.
 */
public class ServerDate {
    @JsonProperty("value")
    private
    Date date;

    public ServerDate(Date date) {
        this.setDate(date);
    }

    public ServerDate() {

    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
