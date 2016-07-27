package fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.oltranz.airtime.airtime.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import client.ClientData;
import client.ClientServices;
import client.ServerClient;
import config.DeviceIdentity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import simplebeans.loginbeans.LoginRequest;
import simplebeans.loginbeans.LoginResponse;
import simplebeans.topupbeans.TopUpBean;
import simplebeans.topupbeans.TopUpRequest;
import simplebeans.topupbeans.TopUpResponse;
import utilities.TopUpConfirmAdapter;
import utilities.utilitiesbeans.TopUpNumber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SingleSellInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SingleSell#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleSell extends Fragment {
    private String tag=getClass().getSimpleName();
    static final int PICK_CONTACT_REQUEST = 1;
    private SingleSellInteractionListener onSingleSell;
    private String msisdn;
    private String token;

    public SingleSell() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SingleSell.
     */
    public static SingleSell newInstance(String param1, String param2) {
        Log.d("AirTime: SingleSell", "New Instance is creating");
        SingleSell fragment = new SingleSell();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Log.d(tag,"Fragment Created");
        if (getArguments() != null) {
            if(getArguments().getString("token") != null)
                token = getArguments().getString("token");
            if(getArguments().getString("msisdn") != null)
                msisdn = getArguments().getString("msisdn");
        }
        Log.d(tag, "The fragment is created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(tag, "The fragment view are being created");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.singlesell_layout, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(tag, "View are finally inflated");
        final EditText tel=(EditText) view.findViewById(R.id.tel);
        tel.setText("");
        tel.append(msisdn);//default owner number
        final EditText amount=(EditText) view.findViewById(R.id.amount);
        final ImageView getNumber=(ImageView) view.findViewById(R.id.getNumber);
        final Button send=(Button) view.findViewById(R.id.send);
        getNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
                startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
            }
        });

        //make a transaction
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(tel.getText().toString()) &&
                        (!TextUtils.isEmpty(amount.getText().toString())) &&
                        (TextUtils.isDigitsOnly(amount.getText().toString())) &&
                        (isValidMobile(tel.getText().toString()))){

                    final List<TopUpNumber> numbers=new ArrayList<TopUpNumber>();
                    TopUpNumber topUpNumber=new TopUpNumber(tel.getText().toString(),Double.valueOf(amount.getText().toString()));
                    numbers.add(topUpNumber);

                    final Dialog dialog = new Dialog(getContext());
                    dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
                    dialog.setContentView(R.layout.confirmtopup);
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);

                    int dividerId = dialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
                    if (dividerId != 0) {
                        View divider = dialog.findViewById(dividerId);
                        if(divider != null)
                            divider.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.appOrange));
                    }
                    dialog.setTitle(Html.fromHtml("<font color='" + ContextCompat.getColor(getContext(), R.color.appOrange) + "'>Confirm...</font>"));
                    dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_info);

                    final ListView telList=(ListView) dialog.findViewById(R.id.telList);
                    final TextView tv=(TextView) dialog.findViewById(R.id.tv);
                    final EditText pin=(EditText) dialog.findViewById(R.id.pin);
                    final Button cancel=(Button) dialog.findViewById(R.id.cancel);
                    final Button ok=(Button) dialog.findViewById(R.id.ok);

                    TopUpConfirmAdapter adapter=new TopUpConfirmAdapter(getActivity(),numbers);
                    telList.setAdapter(adapter);

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!TextUtils.isEmpty(pin.getText().toString())) {

                                //get current Time
                                DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                                String currentTime = df.format(Calendar.getInstance().getTime());

                                //get device Identity
                                DeviceIdentity deviceIdentity = new DeviceIdentity(getContext());
                                //get user Identity
                                LoginRequest loginRequest = new LoginRequest(pin.getText().toString(),
                                        msisdn,
                                        currentTime,
                                        deviceIdentity.getSerialNumber(),
                                        deviceIdentity.getImei(),
                                        deviceIdentity.getVersion());


                                //Client data to send to the Sever
                                Log.d(tag, "Data to push to the server:\n" + new ClientData().mapping(loginRequest));

                                //making a Login request
                                try {
                                    ClientServices clientServices = ServerClient.getClient().create(ClientServices.class);
                                    Call<LoginResponse> callService = clientServices.loginUser(loginRequest);
                                    callService.enqueue(new Callback<LoginResponse>() {
                                        @Override
                                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                                            //HTTP status code
                                            int statusCode = response.code();
                                            LoginResponse loginResponse = response.body();

                                            //handle the response from the server
                                            Log.d(tag, "Server Result:\n" + new ClientData().mapping(loginResponse));
                                            if(loginResponse.getResponseStatusSimpleBean().getStatusCode() == 200){

                                                //making a transaction Request
                                                onTopUpRequest(numbers);

                                                //Clear Amount list
                                                final EditText amount=(EditText) getView().findViewById(R.id.amount);
                                                amount.setText("");

                                                final EditText tel=(EditText) getView().findViewById(R.id.tel);
                                                tel.setText("");
                                                tel.append(msisdn);//default owner number

                                                dialog.dismiss();
                                            }else{
                                                tv.setText(loginResponse.getResponseStatusSimpleBean().getMessage());
                                                pin.setText("");
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                                            // Log error here since request failed
                                            Log.e(tag, t.toString());
                                            tv.setText(t.toString());
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    tv.setText(e.getMessage());
                                }
                            }else{
                                //Empty Pin
                                tv.setText("Fill your PIN");
                            }
                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }else{
                    Toast.makeText(getContext(),"Failed, Revise your Entry",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void onTopUpRequest(List<TopUpNumber> numbers){
        //validation passed, initiation of topUp Object
        List<TopUpBean> to=new ArrayList<TopUpBean>();
        for(TopUpNumber topUpNumber: numbers){
            TopUpBean topUpBean=new TopUpBean(topUpNumber.getMsisdn(),topUpNumber.getAmount());
            to.add(topUpBean);
        }

        //make a request of topUp Object
        TopUpRequest topUpRequest=new TopUpRequest(msisdn,to,token);

        Log.d(tag, "Server Result:\n" + new ClientData().mapping(topUpRequest));

        //making a Login request
        try {
            ClientServices clientServices = ServerClient.getClient().create(ClientServices.class);
            Call<TopUpResponse> callService = clientServices.topUp(topUpRequest);
            callService.enqueue(new Callback<TopUpResponse>() {
                @Override
                public void onResponse(Call<TopUpResponse> call, Response<TopUpResponse> response) {

                    //HTTP status code
                    int statusCode = response.code();
                    try{
                        TopUpResponse topUpResponse = response.body();

                        //handle the response from the server
                        Log.d(tag, "Server Result:\n" + new ClientData().mapping(response.body()));
                        onSingleSell.onSingleSell(1, "success", null);
                    }catch (Exception e){
                        e.printStackTrace();
                        onSingleSell.onSingleSell(1, e.getMessage(), null);
                    }

//                                loginListener.onLoginInteraction(loginResponse.getResponseStatusSimpleBean().getStatusCode(),
//                                        loginResponse.getResponseStatusSimpleBean().getMessage(),
//                                        msisdn,
//                                        loginResponse);
                }

                @Override
                public void onFailure(Call<TopUpResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(tag, t.toString());
                    onSingleSell.onSingleSell(0, null, null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            onSingleSell.onSingleSell(0,null,null);
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SingleSellInteractionListener) {
            onSingleSell = (SingleSellInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SingleSellInteractionListener");
        }
        Log.d(tag, "Fragment Attached");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onSingleSell = null;
        Log.d(tag, "Fragment Detached");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request it is that we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();

                //experimenting

                // We only need the NUMBER column, because there will be only one row in the result
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

                // Perform the query on the contact to get the NUMBER column
                // We don't need a selection or sort order (there's only one result for the given URI)
                // CAUTION: The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                // Consider using CursorLoader to perform the query.
                Cursor cursor = getContext().getContentResolver().query(contactUri, null, null, null, null);
                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String number = cursor.getString(column).trim();
                number=number.replaceAll("\\W", "");
                number=number.replace("[^a-zA-Z]", "");

                Log.d(tag,"Picked Number: "+number+" Of: "+name);
                // Do something with the phone number...
                EditText tel=(EditText) getView().findViewById(R.id.tel);
                tel.setText("");
                tel.append(number);
            }
        }
    }

    //validate Phone Field
    private final static boolean isValidMobile(String phone)
    {
        if (TextUtils.isEmpty(phone))
            return false;
        else
            return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface SingleSellInteractionListener {
        void onSingleSell(int statusCode, String message, Object object);
    }
}
