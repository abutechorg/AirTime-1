package utilities;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import client.ClientData;
import client.ClientServices;
import client.PaymentInterface;
import client.PaymentServerClient;
import client.ServerClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import simplebeans.StatusUsage;
import simplebeans.datebeans.ServerDate;
import simplebeans.loginbeans.LoginResponse;

/**
 * Created by Hp on 10/13/2016.
 */
public class GetCurrentDate {
    Context context;
    String date="";

    public GetCurrentDate(Context context) {
        this.context = context;
        final DateFormat df = new SimpleDateFormat("dd/MM/yyy HH:mm", Locale.ENGLISH);

        try {
            PaymentInterface paymentInterface = PaymentServerClient.getClient().create(PaymentInterface.class);
            Call<ServerDate> callService = paymentInterface.serverDate();
            callService.enqueue(new Callback<ServerDate>() {
                @Override
                public void onResponse(Call<ServerDate> call, Response<ServerDate> response) {

                    //HTTP status code
                    int statusCode = response.code();
                    if(statusCode == 200){
                        ServerDate serverDate = response.body();
                        if(serverDate != null)
                            date = df.format(serverDate.getDate());
                    }
                }

                @Override
                public void onFailure(Call<ServerDate> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("Url Error Date: ", t.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getServerDate(){
        final DateFormat df = new SimpleDateFormat("dd/MM/yyy HH:mm", Locale.ENGLISH);

        if(date.equals("") || date == null){
            date = df.format(Calendar.getInstance().getTime());
        }

        return date;
    }
}
