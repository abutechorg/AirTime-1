package simplebeans.topupbeans;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Owner on 7/19/2016.
 */
public class TopUpRequest {
    @JsonProperty("destinations")
    private List<TopUpBean> destinations;

    public TopUpRequest(List<TopUpBean> destinations) {
        this.setDestinations(destinations);
    }

    public TopUpRequest() {

    }

    public List<TopUpBean> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<TopUpBean> destinations) {
        this.destinations = destinations;
    }
}
