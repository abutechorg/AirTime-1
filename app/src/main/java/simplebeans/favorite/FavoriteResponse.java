package simplebeans.favorite;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import simplebeans.SimpleStatusBean;

/**
 * Created by Owner on 8/19/2016.
 */
public class FavoriteResponse {
    @JsonProperty("favorites")
    private List<FavoriteResponseBean> mFavorite;
    @JsonProperty("status")
    private SimpleStatusBean statusSimpleBean;

    public FavoriteResponse(List<FavoriteResponseBean> mFavorite, SimpleStatusBean statusSimpleBean) {
        this.setmFavorite(mFavorite);
        this.setStatusSimpleBean(statusSimpleBean);
    }

    public FavoriteResponse() {

    }

    public List<FavoriteResponseBean> getmFavorite() {
        return mFavorite;
    }

    public void setmFavorite(List<FavoriteResponseBean> mFavorite) {
        this.mFavorite = mFavorite;
    }

    public SimpleStatusBean getStatusSimpleBean() {
        return statusSimpleBean;
    }

    public void setStatusSimpleBean(SimpleStatusBean statusSimpleBean) {
        this.statusSimpleBean = statusSimpleBean;
    }
}
