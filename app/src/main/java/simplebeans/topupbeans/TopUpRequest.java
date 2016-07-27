package simplebeans.topupbeans;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Owner on 7/19/2016.
 */
public class TopUpRequest {
    @JsonProperty("sourcemsisdn")
    private String sourceMsisdn;
    @JsonProperty("destinations")
    private List<TopUpBean> destinations;
    @JsonProperty("token")
    private String token;

    public TopUpRequest(String sourceMsisdn, List<TopUpBean> destinations, String token) {
        this.setSourceMsisdn(sourceMsisdn);
        this.setDestinations(destinations);
        this.setToken(token);
    }

    public TopUpRequest() {

    }

    public String getSourceMsisdn() {
        return sourceMsisdn;
    }

    public void setSourceMsisdn(String sourceMsisdn) {
        this.sourceMsisdn = sourceMsisdn;
    }

    public List<TopUpBean> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<TopUpBean> destinations) {
        this.destinations = destinations;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
