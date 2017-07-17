package client;

import config.BaseUrl;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Eng. ISHIMWE Aubain Consolateur email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: (250) 785 534 672 on 7/9/2016.
 */
public class PaymentServerClient {
    private static Retrofit retrofit = null;

    /**
     * Get service interface link to open external dependencies
     * @return Retrofit
     */
    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BaseUrl.paymentBaseUrl)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
