package fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TextView;
import android.widget.Toast;

import com.oltranz.airtime.airtime.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import client.ClientData;
import client.ClientServices;
import client.ServerClient;
import config.DeviceIdentity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import simplebeans.registerbeans.RegisterRequest;
import simplebeans.registerbeans.RegisterResponse;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Register#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Register extends Fragment {
    private String tag="AirTime: "+getClass().getSimpleName();
    private Typeface font;

    private RegisterInteractionListener registerListener;

    public Register() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Register.
     */
    public static Register newInstance(String param1, String param2) {
        Register fragment = new Register();
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
        Log.d(tag,"Fragment created");
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(tag,"Fragment view are being created");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.registerlayout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(tag,"View are created");
        final EditText fName=(EditText) view.findViewById(R.id.fname);
        final EditText lName=(EditText) view.findViewById(R.id.lname);
        final EditText tel=(EditText) view.findViewById(R.id.msisdn);
        final EditText mail=(EditText) view.findViewById(R.id.email);
        final EditText pin=(EditText) view.findViewById(R.id.pin);
        final EditText rePin=(EditText) view.findViewById(R.id.repin);
        final Button register=(Button) view.findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!TextUtils.isEmpty(pin.getText().toString()) &&
                        !TextUtils.isEmpty(tel.getText().toString()) &&
                        (pin.getText().toString().equals(rePin.getText().toString())) &&
                        !TextUtils.isEmpty(fName.getText().toString()) &&
                        !TextUtils.isEmpty(lName.getText().toString()) &&
                        !TextUtils.isEmpty(mail.getText().toString())) {
                    //show pop up to confirm Data
                    if (isValidEmail(mail.getText().toString().trim())) {
                        if(isValidMobile(tel.getText().toString())){

                            //validation completed
                            proceedRegistration(fName.getText().toString(),lName.getText().toString(),tel.getText().toString(), mail.getText().toString(), pin.getText().toString());
                        }else{
                            tel.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.appOrange));
                            tel.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    tel.setBackgroundResource(R.drawable.border_gray);
                                }
                            }, 2000);

                            Toast.makeText(getContext(), "Invalid Telephone", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        mail.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.appOrange));
                        mail.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mail.setBackgroundResource(R.drawable.border_gray);
                            }
                        }, 2000);
                        Toast.makeText(getContext(), "Invalid Mail", Toast.LENGTH_LONG).show();
                    }
                } else {
                    registerListener.onRegisterInteraction(201, "Invalid Data.", tel.getText().toString(), null);
                }
            }
        });
    }

//    public void onButtonPressed(Uri uri) {
//        if (registerListener != null) {
//            registerListener.onLoginInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(tag,"Fragment is attaching");
        if (context instanceof RegisterInteractionListener) {
            registerListener = (RegisterInteractionListener) context;
            font = Typeface.createFromAsset(context.getAssets(), "font/ubuntu.ttf");
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement RegisterInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(tag, "Fragment is Detaching");
        registerListener = null;
    }

    //validate Email Field
    private final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target))
            return false;
        else
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    //validate Phone Field
    private final static boolean isValidMobile(String phone)
    {
        if (TextUtils.isEmpty(phone))
            return false;
         else
            return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    private void proceedRegistration(final String fName, final String lName, final String tel, final String mail, final String pin){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        dialog.setContentView(R.layout.simplepopup);
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

        //ic_dialog_info
        dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_info);
        TextView content = (TextView) dialog.findViewById(R.id.dialogContent);
        content.setTypeface(font);
        content.setText("First Name: " + fName+ "\n");
        content.append("Last Name: " + lName + "\n");
        content.append("Telphone: " + tel+ "\n");
        content.append("Email: " + mail+ "\n");
        content.append("PIN: ####");

        Button ok = (Button) dialog.findViewById(R.id.ok);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get current Time
                DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                String currentTime = df.format(Calendar.getInstance().getTime());

                //get device Identity
                DeviceIdentity deviceIdentity = new DeviceIdentity(getContext());

                //get user Identity
                RegisterRequest registerRequest = new RegisterRequest(fName,
                        lName,
                        tel,
                        deviceIdentity.getImei(),
                        currentTime,
                        pin,
                        mail,
                        deviceIdentity.getSerialNumber(),
                        deviceIdentity.getVersion());

                //Client data to send to the Sever
                Log.d(tag, "Data to Push to the server:\n" + new ClientData().mapping(registerRequest));

                //making a Register request
                try {
                    ClientServices clientServices = ServerClient.getClient().create(ClientServices.class);
                    Call<RegisterResponse> callService = clientServices.registerUser(registerRequest);
                    callService.enqueue(new Callback<RegisterResponse>() {
                        @Override
                        public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {

                            //HTTP status code
                            int statusCode = response.code();
                            RegisterResponse registerResponse = response.body();

                            try{
                                //handle the response from the server
                                Log.d(tag, "Server Result:\n" + new ClientData().mapping(registerResponse));
                                registerListener.onRegisterInteraction(registerResponse.getResponseStatusSimpleBean().getStatusCode(),
                                        registerResponse.getResponseStatusSimpleBean().getMessage(),
                                        tel,
                                        registerResponse);
                            }catch (Exception e){
                                e.printStackTrace();
                                uiFeed(e.getMessage());
                                //registerListener.onRegisterInteraction(500, e.getMessage(), tel, null);
                            }
                            dialog.dismiss();
                        }

                        @Override
                        public void onFailure(Call<RegisterResponse> call, Throwable t) {
                            // Log error here since request failed
                            Log.e(tag, t.toString());
                            uiFeed("Server Error");
                            //registerListener.onRegisterInteraction(500, t.getMessage(), tel, null);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    uiFeed(e.getMessage());
                    //registerListener.onRegisterInteraction(500, e.getMessage(), tel, null);
                    dialog.dismiss();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //show the popUp
        dialog.show();
    }

    private void uiFeed(String feedBack){
        try{
            final TextView tv=(TextView) getView().findViewById(R.id.tv);
            if(!TextUtils.isEmpty(feedBack)){
                tv.setVisibility(View.VISIBLE);
                tv.setText(feedBack);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv.setVisibility(View.GONE);
                    }
                }, 4000);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
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
    public interface RegisterInteractionListener {
        void onRegisterInteraction(int statusCode, String message, String msisdn, RegisterResponse registerResponse);
    }
}
