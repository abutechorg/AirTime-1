package fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
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

import com.oltranz.mobilea.mobilea.BuildConfig;
import com.oltranz.mobilea.mobilea.R;
import com.vistrav.ask.Ask;
import com.vistrav.ask.annotations.AskDenied;
import com.vistrav.ask.annotations.AskGranted;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import client.ClientData;
import client.ClientServices;
import client.ServerClient;
import config.DeviceIdentity;
import config.MPay;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import simplebeans.loginbeans.LoginRequest;
import simplebeans.loginbeans.LoginResponse;
import simplebeans.topupbeans.TopUpBean;
import simplebeans.topupbeans.TopUpRequest;
import simplebeans.topupbeans.TopUpResponse;
import utilities.CheckWalletBalance;
import utilities.DataFactory;
import utilities.IsGranted;
import utilities.RechargeWalletUtil;
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
public class SingleSell extends Fragment implements CheckWalletBalance.CheckWalletBalanceInteraction, RechargeWalletUtil.RechargeWalletUtilInteraction {
    static final int PICK_CONTACT_REQUEST = 1;
    private String tag = getClass().getSimpleName();
    private SingleSellInteractionListener onSingleSell;
    private String msisdn;
    private String token;
    private String appVersion;
    private CheckWalletBalance checkWalletBalance;
    private View view;
    private Dialog mDialog;
    private ProgressDialog progressDialog;

    private Typeface font;

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

    //validate Phone Field
    private final static boolean isValidMobile(String phone) {
        if (TextUtils.isEmpty(phone))
            return false;
        else
            return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Log.d(tag, "Fragment Created");
        if (getArguments() != null) {
            if (getArguments().getString("token") != null)
                token = getArguments().getString("token");
            if (getArguments().getString("msisdn") != null)
                msisdn = getArguments().getString("msisdn");
        }
        appVersion = "versionCode: " + BuildConfig.VERSION_CODE + " versionName: " + BuildConfig.VERSION_NAME;

        checkWalletBalance = new CheckWalletBalance(this, getContext(), token);
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
        progressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        this.view = view;

        final EditText tel = (EditText) view.findViewById(R.id.tel);
        tel.setTypeface(font, Typeface.BOLD);
        tel.setText("");
        tel.append(msisdn);//default owner number
        final EditText amount = (EditText) view.findViewById(R.id.amount);
        amount.setTypeface(font, Typeface.BOLD);
        final ImageView getNumber = (ImageView) view.findViewById(R.id.getNumber);
        final Button send = (Button) view.findViewById(R.id.send);
        send.setTypeface(font, Typeface.BOLD);

        getNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(new IsGranted(getContext()).checkReadContacts()) {
                    Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                    pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
                    startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
                }else{
                    Ask.on(getActivity())
                            .forPermissions(Manifest.permission.READ_CONTACTS)
                            .withRationales("In Order to make your life easy for contact pick up application needs Read Contact permission") //optional
                            .go();
                }
            }
        });

        //make a transaction
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(tel.getText().toString()) &&
                        (!TextUtils.isEmpty(amount.getText().toString())) &&
                        (TextUtils.isDigitsOnly(amount.getText().toString().trim())) &&
                        (isValidMobile(tel.getText().toString()))) {

                    if(Double.valueOf(amount.getText().toString())>= MPay.minTopUp){
                        try {
                            topUpProceed(tel.getText().toString().trim(), Double.parseDouble(amount.getText().toString().trim()));
                        } catch (Exception e) {
                            amount.setFocusable(true);
                            amount.requestFocus();
                            amount.setError("Revise the amount");
                        }
                    }else{
                        amount.setFocusable(true);
                        amount.requestFocus();
                        amount.setError("Revise the amount");
                    }


                } else {
                    if ((!TextUtils.isEmpty(amount.getText().toString())) &&
                            (new DataFactory().isValidAmount(amount.getText().toString()))) {
                        amount.setFocusable(true);
                        amount.requestFocus();
                        amount.setError("Revise the amount");
                    }
                    Toast.makeText(getContext(), "Failed, Revise your Entry", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //optional
    @AskGranted(Manifest.permission.READ_CONTACTS)
    public void readContactAllowed() {
        Log.i(tag, "READ CONTACTS GRANTED");
    }

    //optional
    @AskDenied(Manifest.permission.READ_CONTACTS)
    public void readContactDenied() {
        Log.i(tag, "READ CONTACTS DENIED");
        Toast.makeText(getContext(), "Sorry, Without READ_CONTACTS Permission the application workflow can be easily compromised", Toast.LENGTH_LONG).show();
    }
    private void checkBalance() {
        checkWalletBalance.getBalance();
    }

    private void topUpProceed(String tel, double amount) {

//        if (walletBalance <= amount) {
//            //redirect to the recharge of wallet
//            promptToRechargeWallet(tel, amount);
//        } else {
        final List<TopUpNumber> numbers = new ArrayList<TopUpNumber>();
        TopUpNumber topUpNumber = new TopUpNumber(tel, amount);
        numbers.add(topUpNumber);

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.confirmtopup);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

//        int dividerId = dialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
//        if (dividerId != 0) {
//            View divider = dialog.findViewById(dividerId);
//            if (divider != null)
//                divider.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.appOrange));
//        }
//        dialog.setTitle(Html.fromHtml("<font color='" + ContextCompat.getColor(getContext(), R.color.appOrange) + "'>Confirm...</font>"));
//        dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_info);

        TextView topLabel=(TextView) dialog.findViewById(R.id.topLabel);
        topLabel.setTypeface(font,Typeface.BOLD);
        TextView telLabel=(TextView) dialog.findViewById(R.id.lbltel);
        telLabel.setTypeface(font,Typeface.BOLD);
        TextView amountLabel=(TextView) dialog.findViewById(R.id.lblamount);
        amountLabel.setTypeface(font,Typeface.BOLD);
        final ListView telList = (ListView) dialog.findViewById(R.id.telList);
        final TextView tv = (TextView) dialog.findViewById(R.id.tv);
        tv.setTypeface(font);
        final EditText pin = (EditText) dialog.findViewById(R.id.pin);
        pin.setTypeface(font);
        final Button cancel = (Button) dialog.findViewById(R.id.cancel);
        cancel.setTypeface(font, Typeface.BOLD);
        final Button ok = (Button) dialog.findViewById(R.id.ok);
        ok.setTypeface(font, Typeface.BOLD);

        TopUpConfirmAdapter adapter = new TopUpConfirmAdapter(getActivity(), numbers);
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


                    progressDialog.setMessage("Validating...");
                    progressDialog.show();
                    //Client data to send to the Sever
                    Log.d(tag, "Data to push to the server:\n" + new ClientData().mapping(loginRequest));

                    //making a Login request
                    try {
                        ClientServices clientServices = ServerClient.getClient().create(ClientServices.class);
                        Call<LoginResponse> callService = clientServices.loginUser(appVersion, loginRequest);
                        callService.enqueue(new Callback<LoginResponse>() {
                            @Override
                            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                                //HTTP status code
                                int statusCode = response.code();
                                if (statusCode == 200) {
                                    if (response.body() != null) {
                                        LoginResponse loginResponse = response.body();

                                        //handle the response from the server
                                        Log.d(tag, "Server Result:\n" + new ClientData().mapping(loginResponse));
                                        if (loginResponse.getSimpleStatusBean().getStatusCode() == 400) {

                                            //Clear Amount list
                                            final EditText amount = (EditText) getView().findViewById(R.id.amount);
                                            amount.setText("");

                                            final EditText tel = (EditText) getView().findViewById(R.id.tel);
                                            tel.setText("");
                                            tel.append(msisdn);//default owner number
                                            dialog.dismiss();

                                            progressDialog.setMessage("Sending Airtime...");

                                            //making a transaction Request
                                            onTopUpRequest(numbers, loginResponse.getLoginResponseBean().getToken());

                                        } else {
                                            uiFeed(loginResponse.getSimpleStatusBean().getMessage());
                                            tv.setText(loginResponse.getSimpleStatusBean().getMessage());
                                            pin.setText("");
                                        }
                                    } else {
                                        uiFeed("Erroneous server response. Contact System Admin");
                                    }
                                } else {
                                    uiFeed(response.message() + "");
                                }
                            }

                            @Override
                            public void onFailure(Call<LoginResponse> call, Throwable t) {
                                // Log error here since request failed
                                Log.e(tag, t.toString());
                                uiFeed("Connectivity Error");
                                tv.setText("Connectivity Error");
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        uiFeed("" + e.getMessage());
                        tv.setText("Erroneous Data");
                    }
                } else {
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
//        }
    }

    private void promptToRechargeWallet(double currentBalance) {
        TextView close;
        TextView title;
        TextView contentMessage;
        ImageView visa, mc, verve;
        final EditText wAmount;

        mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setContentView(R.layout.promp_wallet_recharge);

        close = (TextView) mDialog.findViewById(R.id.close);
        close.setTypeface(font, Typeface.BOLD);
        title = (TextView) mDialog.findViewById(R.id.title);
        title.setTypeface(font, Typeface.BOLD);
        contentMessage = (TextView) mDialog.findViewById(R.id.boxMessage);
        contentMessage.setTypeface(font);

        title.setText("Fund wallet");
        title.setTextColor(ContextCompat.getColor(getContext(), R.color.appOrange));

        Spannable word = new SpannableString("Insufficient amount in wallet: \n");
        contentMessage.setText(word);
        contentMessage.setTextColor(ContextCompat.getColor(getContext(), R.color.appGray));

        Spannable amountToLoad = new SpannableString("₦ " + currentBalance + " \n\n");
        amountToLoad.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.appOrange)), 0, amountToLoad.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        contentMessage.append(amountToLoad);

        Spannable currentWallet = new SpannableString("Fund your wallet first");
        currentWallet.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.appGray)), 0, currentWallet.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        contentMessage.append(currentWallet);

//        Spannable amountOnWallet = new SpannableString("₦ " + walletBalance + " \n");
//        amountOnWallet.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.appOrange)), 0, amountOnWallet.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        contentMessage.append(amountOnWallet);
//
//        Spannable notifText = new SpannableString("Please Fund Your Wallet First\n");
//        notifText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.appGray)), 0, notifText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        contentMessage.append(notifText);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        wAmount = (EditText) mDialog.findViewById(R.id.amount);
        wAmount.setTypeface(font);
        wAmount.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                //WalletHistory.this.adapter.getFilter().filter(cs);
                try {
                    if (wAmount.getText().toString().length() > 0) {
                        if (Double.valueOf(wAmount.getText().toString().trim().toLowerCase(Locale.getDefault())) < 50) {
                            wAmount.setBackgroundResource(R.drawable.border_orange);
                            wAmount.setTextColor(ContextCompat.getColor(getContext(), R.color.appOrange));
                        } else {
                            wAmount.setBackgroundResource(R.drawable.border_gray);
                            wAmount.setTextColor(ContextCompat.getColor(getContext(), R.color.darkGray));
                        }
                    } else {
                        wAmount.setBackgroundResource(R.drawable.border_gray);
                        wAmount.setTextColor(ContextCompat.getColor(getContext(), R.color.darkGray));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    wAmount.requestFocus();
                    wAmount.setError("Revise the amount");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        final RechargeWalletUtil rechargeWalletUtil = new RechargeWalletUtil(this, getActivity(), msisdn, token);
        //handle buttons click
        visa = (ImageView) mDialog.findViewById(R.id.visaButton);
        mc = (ImageView) mDialog.findViewById(R.id.mcButton);
        verve = (ImageView) mDialog.findViewById(R.id.verveButton);

        visa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rechargeWalletUtil.startRecharge(view, wAmount.getText().toString());
            }
        });

        mc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rechargeWalletUtil.startRecharge(view, wAmount.getText().toString());
            }
        });

        verve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rechargeWalletUtil.startRecharge(view, wAmount.getText().toString());
            }
        });

        mDialog.show();
    }

    private void uiFeed(String message) {
        if (progressDialog != null)
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(message)
                    .setTitle(R.string.dialog_title);
            // Add the buttons
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();


            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void onTopUpRequest(List<TopUpNumber> numbers, String newToken) {
        if(newToken == null)
            newToken=token;
        //validation passed, initiation of topUp Object
        List<TopUpBean> destined = new ArrayList<TopUpBean>();
        for (TopUpNumber topUpNumber : numbers) {
            TopUpBean topUpBean = new TopUpBean(topUpNumber.getMsisdn(), topUpNumber.getAmount());
            destined.add(topUpBean);
        }

        //make a request of topUp Object
        TopUpRequest topUpRequest = new TopUpRequest(destined);

        Log.d(tag, "Request Body:\n" + new ClientData().mapping(topUpRequest));

        //making a Login request
        try {
            ClientServices clientServices = ServerClient.getClient().create(ClientServices.class);
            Call<TopUpResponse> callService = clientServices.topUp(newToken, topUpRequest);
            callService.enqueue(new Callback<TopUpResponse>() {
                @Override
                public void onResponse(Call<TopUpResponse> call, Response<TopUpResponse> response) {
                    //HTTP status code
                    int statusCode = response.code();
                    if (statusCode == 200) {
                        try {
                            TopUpResponse topUpResponse = response.body();

                            if (topUpResponse.getStatus().getStatusCode() == 401) {
                                //handle the response from the server
                                Log.d(tag, "Server Result:\n" + new ClientData().mapping(response.body()));
                                uiFeed(topUpResponse.getStatus().getMessage() + " Please fund your wallet");
                                onSingleSell.onSingleSell(1, topUpResponse.getStatus().getMessage() + " Please fund your wallet", topUpResponse);

                                checkBalance();
                            } else {
                                //handle the response from the server
                                progressDialog.setMessage(topUpResponse.getStatus().getMessage() + "");
                                Log.d(tag, "Server Result:\n" + new ClientData().mapping(response.body()));
                                uiFeed(topUpResponse.getStatus().getMessage());
                                onSingleSell.onSingleSell(1, topUpResponse.getStatus().getMessage(), topUpResponse);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            uiFeed(e.getMessage());
                            onSingleSell.onSingleSell(1, e.getMessage(), null);
                        }
                    } else {
                        if (progressDialog != null)
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        onSingleSell.onSingleSell(403, "Failure", null);
                    }

//                                loginListener.onLoginInteraction(loginResponse.getSimpleStatusBean().getStatusCode(),
//                                        loginResponse.getSimpleStatusBean().getMessage(),
//                                        msisdn,
//                                        loginResponse);
                }

                @Override
                public void onFailure(Call<TopUpResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(tag, t.toString());
                    uiFeed("Connectivity Error");
                    onSingleSell.onSingleSell(0, "Connectivity Error", null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            uiFeed(e.getMessage());
            onSingleSell.onSingleSell(0, e.getMessage(), null);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SingleSellInteractionListener) {
            onSingleSell = (SingleSellInteractionListener) context;
            font = Typeface.createFromAsset(context.getAssets(), "font/ubuntu.ttf");
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
                number = number.replaceAll("\\W", "");
                number = number.replace("[^a-zA-Z]", "");

                Log.d(tag, "Picked Number: " + number + " Of: " + name);
                // Do something with the phone number...
                EditText tel = (EditText) getView().findViewById(R.id.tel);
                tel.setText("");
                tel.append(number);
            }
        }
    }

    @Override
    public void onWalletBalanceCheck(String balance) {
        double walletBalance = 0;
        if (!TextUtils.isEmpty(balance))
            walletBalance = Double.valueOf(balance);

        promptToRechargeWallet(walletBalance);
    }

    @Override
    public void onRecargeWalletInteraction(int statusCode, String message) {
        try {
            mDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(message))
            onSingleSell.onSingleSell(100, message, null);
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
