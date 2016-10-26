package fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.oltranz.mobilea.mobilea.R;

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
import utilities.CheckWalletBalance;
import utilities.UserPrimaryMail;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Register#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Register extends Fragment {
    private String tag = "AirTime: " + getClass().getSimpleName();
    private Typeface font;
    private AlertDialog dialog;
    private ProgressDialog progressDialog;

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

    //validate Email Field
    private final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target))
            return false;
        else
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    //validate Phone Field
    private final static boolean isValidMobile(String phone) {
        if(!TextUtils.isEmpty(phone)){
            if(phone.length()>9){
                    return android.util.Patterns.PHONE.matcher(phone).matches();
            } else
                return false;
        }else
            return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Log.d(tag, "Fragment created");
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

//    public void onButtonPressed(Uri uri) {
//        if (registerListener != null) {
//            registerListener.onLoginInteraction(uri);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(tag, "Fragment view are being created");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.registerlayout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(tag, "View are created");
        final EditText fName = (EditText) view.findViewById(R.id.fname);
        fName.setTypeface(font);
        final EditText lName = (EditText) view.findViewById(R.id.lname);
        lName.setTypeface(font);
        final EditText tel = (EditText) view.findViewById(R.id.histMsisdn);
        tel.setTypeface(font);
        final EditText mail = (EditText) view.findViewById(R.id.email);
        mail.setTypeface(font);
        String primaryMail = new UserPrimaryMail().getEmail(getContext());
        if (primaryMail != null)
            if (isValidEmail(primaryMail)){
                mail.setText(primaryMail);
            }

        final EditText pin = (EditText) view.findViewById(R.id.pin);
        pin.setTypeface(font);
        final EditText rePin = (EditText) view.findViewById(R.id.repin);
        rePin.setTypeface(font);
        final Button register = (Button) view.findViewById(R.id.register);
        register.setTypeface(font, Typeface.BOLD);
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!pin.getText().toString().equals(rePin.getText().toString())) {
                    pin.setError("Invalid Password");
                    Toast.makeText(getContext(), "Invalid Password", Toast.LENGTH_LONG).show();
                }
                if (!TextUtils.isEmpty(pin.getText().toString()) &&
                        !TextUtils.isEmpty(tel.getText().toString()) &&
                        (pin.getText().toString().equals(rePin.getText().toString())) &&
                        !TextUtils.isEmpty(fName.getText().toString()) &&
                        !TextUtils.isEmpty(lName.getText().toString()) &&
                        !TextUtils.isEmpty(mail.getText().toString())) {
                    //show pop up to confirm Data
                    if (isValidEmail(mail.getText().toString().trim())) {
                        if (isValidMobile(tel.getText().toString().trim())) {

                            //validation completed
                            proceedRegistration(fName.getText().toString(), lName.getText().toString(), tel.getText().toString().trim(), mail.getText().toString().trim(), pin.getText().toString());
                        } else {
                            tel.setError("Invalid Telephone");

                            Toast.makeText(getContext(), "Invalid Telephone", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        mail.setError("Invalid Mail");
                        Toast.makeText(getContext(), "Invalid Mail", Toast.LENGTH_LONG).show();
                    }
                } else {
                    uiFeed("Invalid Data");
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(tag, "Fragment is attaching");
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

    private void proceedRegistration(final String fName, final String lName, final String tel, final String mail, final String pin) {
//        final Dialog dialog = new Dialog(getContext());
//        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
//        dialog.setContentView(R.layout.simplepopup);
//        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
//
//        int dividerId = dialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
//        if (dividerId != 0) {
//            View divider = dialog.findViewById(dividerId);
//            if(divider != null)
//                divider.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.appOrange));
//
//        }
//        dialog.setTitle(Html.fromHtml("<font color='" + ContextCompat.getColor(getContext(), R.color.appOrange) + "'>Confirm...</font>"));

        String contentMessage = "First Name: " + fName + "\n" +
                "Last Name: " + lName + "\n" +
                "Telphone: " + tel + "\n" +
                "Email: " + mail + "\n" +
                "Password: ####\n";
        progressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(contentMessage)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(R.string.confirm_title);
            // Add the buttons
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(final DialogInterface dialog, int id) {
                    progressDialog.setMessage("Sending...");
                    progressDialog.show();
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
                                if(progressDialog != null)
                                    if(progressDialog.isShowing())
                                        progressDialog.dismiss();
                                //HTTP status code
                                int statusCode = response.code();
                                RegisterResponse registerResponse = response.body();

                                try {
                                    //handle the response from the server
                                    Log.d(tag, "Server Result:\n" + new ClientData().mapping(registerResponse));
                                    registerListener.onRegisterInteraction(registerResponse.getSimpleStatusBean().getStatusCode(),
                                            registerResponse.getSimpleStatusBean().getMessage(),
                                            tel,
                                            registerResponse);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    uiFeed(e.getMessage());
                                    dialog.dismiss();
                                    //registerListener.onRegisterInteraction(500, e.getMessage(), tel, null);
                                }
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                                if(progressDialog != null)
                                    if(progressDialog.isShowing())
                                        progressDialog.dismiss();
                                // Log error here since request failed
                                Log.e(tag, t.toString());
                                uiFeed("Server Error");
                                //registerListener.onRegisterInteraction(500, t.getMessage(), tel, null);
                            }
                        });
                    } catch (Exception e) {
                        if(progressDialog != null)
                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
                        e.printStackTrace();
                        uiFeed(e.getMessage());
                        //registerListener.onRegisterInteraction(500, e.getMessage(), tel, null);
                        dialog.dismiss();
                    }
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        } catch (Exception e) {
            if(progressDialog != null)
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

//        //ic_dialog_info
//        dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_info);
//        TextView content = (TextView) dialog.findViewById(R.id.dialogContent);
//        content.setTypeface(font);
//        content.setText("First Name: " + fName+ "\n");
//        content.append("Last Name: " + lName + "\n");
//        content.append("Telphone: " + tel+ "\n");
//        content.append("Email: " + mail+ "\n");
//        content.append("PIN: ####");
//
//        Button ok = (Button) dialog.findViewById(R.id.ok);
//        Button cancel = (Button) dialog.findViewById(R.id.cancel);
//
//        ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //get current Time
//                DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
//                String currentTime = df.format(Calendar.getInstance().getTime());
//
//                //get device Identity
//                DeviceIdentity deviceIdentity = new DeviceIdentity(getContext());
//
//                //get user Identity
//                RegisterRequest registerRequest = new RegisterRequest(fName,
//                        lName,
//                        tel,
//                        deviceIdentity.getImei(),
//                        currentTime,
//                        pin,
//                        mail,
//                        deviceIdentity.getSerialNumber(),
//                        deviceIdentity.getVersion());
//
//                //Client data to send to the Sever
//                Log.d(tag, "Data to Push to the server:\n" + new ClientData().mapping(registerRequest));
//
//                //making a Register request
//                try {
//                    ClientServices clientServices = ServerClient.getClient().create(ClientServices.class);
//                    Call<RegisterResponse> callService = clientServices.registerUser(registerRequest);
//                    callService.enqueue(new Callback<RegisterResponse>() {
//                        @Override
//                        public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
//
//                            //HTTP status code
//                            int statusCode = response.code();
//                            RegisterResponse registerResponse = response.body();
//
//                            try{
//                                //handle the response from the server
//                                Log.d(tag, "Server Result:\n" + new ClientData().mapping(registerResponse));
//                                registerListener.onRegisterInteraction(registerResponse.getSimpleStatusBean().getStatusCode(),
//                                        registerResponse.getSimpleStatusBean().getMessage(),
//                                        tel,
//                                        registerResponse);
//                            }catch (Exception e){
//                                e.printStackTrace();
//                                uiFeed(e.getMessage());
//                                //registerListener.onRegisterInteraction(500, e.getMessage(), tel, null);
//                            }
//                            dialog.dismiss();
//                        }
//
//                        @Override
//                        public void onFailure(Call<RegisterResponse> call, Throwable t) {
//                            // Log error here since request failed
//                            Log.e(tag, t.toString());
//                            uiFeed("Server Error");
//                            //registerListener.onRegisterInteraction(500, t.getMessage(), tel, null);
//                        }
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    uiFeed(e.getMessage());
//                    //registerListener.onRegisterInteraction(500, e.getMessage(), tel, null);
//                    dialog.dismiss();
//                }
//            }
//        });
//
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//
//        //show the popUp
//        dialog.show();
    }

    private void uiFeed(String feedBack) {
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(feedBack)
                    .setTitle(R.string.dialog_title);
            // Add the buttons
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();


            Toast.makeText(getContext(), feedBack, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
