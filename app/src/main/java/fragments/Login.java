package fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import simplebeans.loginbeans.LoginRequest;
import simplebeans.loginbeans.LoginResponse;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Login extends Fragment {
    private String tag="AirTime: "+getClass().getSimpleName();
    private String msisdn="";

    private LoginInteractionListener loginListener;
    private Typeface font;
    private ProgressDialog progressDialog;

    public Login() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginBean.
     */
    public static Login newInstance(String param1, String param2) {
        Log.d("AirTime: Login", "New Instance is creating");
        Login fragment = new Login();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Log.d(tag, "The fragment is created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(tag, "The fragment view are being created");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.loginlayout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(tag, "View are finally inflated");
        final TextView recoverPin = (TextView) view.findViewById(R.id.recoverPin);
        recoverPin.setTypeface(font);
        recoverPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinRecover();
            }
        });

        final EditText tel = (EditText) view.findViewById(R.id.welcomeUser);
        tel.setTypeface(font);
        final EditText pin=(EditText) view.findViewById(R.id.pin);
        pin.setTypeface(font);
        Button btnLogin=(Button) view.findViewById(R.id.submit);
        btnLogin.setTypeface(font, Typeface.BOLD);

        progressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Authenticating...");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(tag, "Login Button touched");

                if (!TextUtils.isEmpty(pin.getText().toString()) && !TextUtils.isEmpty(tel.getText().toString())) {

                    progressDialog.show();
                    //get current Time
                    DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                    String currentTime = df.format(Calendar.getInstance().getTime());

                    //get device Identity
                    DeviceIdentity deviceIdentity = new DeviceIdentity(getContext());
                    //get user Identity
                    LoginRequest loginRequest = new LoginRequest(pin.getText().toString(),
                            tel.getText().toString(),
                            currentTime,
                            deviceIdentity.getSerialNumber(),
                            deviceIdentity.getImei(),
                            deviceIdentity.getVersion());

                    //keep the msisdn for future use
                    msisdn=loginRequest.getMsisdn();

                    //Client data to send to the Sever
                    Log.d(tag, "Data to push to the server:\n" + new ClientData().mapping(loginRequest));

                    //Dummy data
                   // loginListener.onLoginInteraction(400, "Success", null);

                    //making a Login request
                    try {
                        ClientServices clientServices = ServerClient.getClient().create(ClientServices.class);
                        Call<LoginResponse> callService = clientServices.loginUser(loginRequest);
                        callService.enqueue(new Callback<LoginResponse>() {
                            @Override
                            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                                //HTTP status code
                                int statusCode = response.code();
                                try{
                                    final LoginResponse loginResponse = response.body();

                                    progressDialogHide("Contacting Server", false);
                                    //handle the response from the server
                                    Log.d(tag, "Server Result:\n" + new ClientData().mapping(loginResponse));

                                    try {
                                        progressDialogHide(" " + loginResponse.getResponseStatusSimpleBean().getMessage(), true);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                loginListener.onLoginInteraction(loginResponse.getResponseStatusSimpleBean().getStatusCode(),
                                                        loginResponse.getResponseStatusSimpleBean().getMessage(),
                                                        msisdn,
                                                        loginResponse);
                                            }
                                        }, 3000);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } catch (final Exception e) {
                                    progressDialogHide(e.getMessage(), true);
                                    uiFeed(e.getMessage());
                                    //loginListener.onLoginInteraction(500, e.getMessage(), msisdn, null);
                                }
                            }

                            @Override
                            public void onFailure(Call<LoginResponse> call, Throwable t) {
                                // Log error here since request failed
                                Log.e(tag, t.toString());
                                progressDialogHide("Connectivity Error", true);
                                uiFeed("Connectivity Error");
                                //loginListener.onLoginInteraction(500, t.getMessage(), msisdn, null);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressDialogHide(e.getMessage(), true);
                        uiFeed(e.getMessage());
                        //loginListener.onLoginInteraction(500, e.getMessage(), msisdn, null);
                    }
                } else {
                    try {

                        Drawable drawableInfo = ContextCompat.getDrawable(getActivity(), android.R.drawable.ic_dialog_info);
                        tel.setError("Revise Your Tel", drawableInfo);
                        pin.setError("Revise Your Pin", drawableInfo);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tel.setError(null);
                                pin.setError(null);
                            }
                        }, 4000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    uiFeed("Invalid Credential.");
                    //loginListener.onLoginInteraction(500, "Invalid Credential.", msisdn, null);
                }
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginInteractionListener) {
            loginListener = (LoginInteractionListener) context;
            font = Typeface.createFromAsset(context.getAssets(), "font/ubuntu.ttf");
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LoginInteractionListener");
        }
        Log.d(tag, "Fragment Attached");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        loginListener = null;
    }

    private void progressDialogHide(final String message, final boolean hide) {
        if (progressDialog.isShowing()) {

            try {
                progressDialog.setMessage(message);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (hide)
                            progressDialog.dismiss();
                    }
                }, 3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void pinRecover() {
        Log.d(tag, "Recover PIN is triggered");
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
                        tv.setVisibility(View.INVISIBLE);
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
     */
    public interface LoginInteractionListener {
        // passing the Message and Staus code to mother activity
        void onLoginInteraction(int statusCode, String message, String msisdn, LoginResponse loginResponse);
    }
}
