package client;

import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Created by Eng. ISHIMWE Aubain Consolateur email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: (250) 785 534 672 on 7/9/2016.
 * ClientData class is used in parsing object to other format
 */
public class ClientData {
    private String tag="AirTime: "+getClass().getSimpleName();
    private String jsonData;
    private ObjectMapper mapper;

    public ClientData() {
    }

    /**
     * This mapping method is used to parse object into JSON String
     * @param object Object to be parsed to JSON String
     * @return JSON String of the provided Object
     */
    public String mapping(Object object){
        Log.d(tag, "mapping object starts...");
        mapper= new ObjectMapper();

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            jsonData=mapper.writeValueAsString(object);
            Log.d(tag,"mapping result: "+jsonData);
            return jsonData;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(tag, "Erroneous mapping object: " + e.getMessage());
            return e.getMessage();
        }
    }
}
