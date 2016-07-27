package fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.oltranz.airtime.airtime.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
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
import utilities.Contact;
import utilities.ContactsPickerActivity;
import utilities.MultipleSellAdapter;
import utilities.TopUpConfirmAdapter;
import utilities.utilitiesbeans.TopUpNumber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MultipleSellInteraction} interface
 * to handle interaction events.
 * Use the {@link MultipleSell#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MultipleSell extends Fragment {
    private String tag="AirTime: "+getClass().getSimpleName();

    private Typeface font;
    private MultipleSellInteraction onMultipleSell;
    private MultipleSellAdapter adapter;
    private TableLayout buyList;
    private List<TopUpNumber> numbers;

    private String msisdn;
    private String token;

    public MultipleSell() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MultipleSell.
     */
    public static MultipleSell newInstance(String param1, String param2) {
        MultipleSell fragment = new MultipleSell();
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
        return inflater.inflate(R.layout.multiplesell_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(tag, "View are finally inflated");
        Button send=(Button) view.findViewById(R.id.send);
        send.setTypeface(font);
        ImageButton addNumbers=(ImageButton) view.findViewById(R.id.addnumber);
        buyList=(TableLayout) view.findViewById(R.id.buyTable);
//        setListViewHeightBasedOnChildren(buyList);
//        buyList.setOnTouchListener(new View.OnTouchListener() {
//            // Setting on Touch Listener for handling the touch inside ScrollView
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // Disallow the touch request for parent scroll on touch of child view
//                v.getParent().requestDisallowInterceptTouchEvent(true);
//                return false;
//            }
//        });
        numbers=new ArrayList<TopUpNumber>();
        addNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pick the numbers
                if (buyList.getChildCount() == 3) {
                    buyList.removeAllViews();
//                    TextView tv=(TextView) getView().findViewById(R.id.tv);
//                    tv.setVisibility(View.VISIBLE);
                }
                Intent intentContactPick = new Intent(getContext(), ContactsPickerActivity.class);
                startActivityForResult(intentContactPick, 1000);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final List<TopUpNumber> topUpNumbers = new ArrayList<TopUpNumber>();
                for (int i = 0; i < buyList.getChildCount(); i++) {
                    View rowView = buyList.getChildAt(i);
                    if (rowView.findViewById(R.id.tel) != null) {
                        TextView tel = (TextView) rowView.findViewById(R.id.tel);
                        EditText amount = (EditText) rowView.findViewById(R.id.amount);
                        Log.d(tag, tel.getText().toString() + " : " + amount.getText().toString());
                        String formattedAmount = "0";
                        try {
                            if (!TextUtils.isEmpty(amount.getText().toString()))
                                formattedAmount = new DecimalFormat("#").format(Double.valueOf(amount.getText().toString()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Invalid Amount Entry", Toast.LENGTH_SHORT).show();
                        }

                        TopUpNumber topUpNumber = new TopUpNumber(tel.getText().toString(), Double.valueOf(formattedAmount));

                        if (!TextUtils.isEmpty(topUpNumber.getMsisdn()) &&
                                (topUpNumber.getAmount() > 0) &&
                                (isValidMobile(topUpNumber.getMsisdn()))) {

                            // TopUpNumber topUpNumber = new TopUpNumber(tel.getText().toString(), Double.valueOf(amount.getText().toString()));
                            topUpNumbers.add(topUpNumber);
                        }
                    }
                }

                if (topUpNumbers.size() <= 0) {
                    Toast.makeText(getContext(), "Invalid Amount Entry", Toast.LENGTH_SHORT).show();
                    return;
                }

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

                final ListView telList = (ListView) dialog.findViewById(R.id.telList);
                final TextView tv = (TextView) dialog.findViewById(R.id.tv);
                final EditText pin = (EditText) dialog.findViewById(R.id.pin);
                final Button cancel = (Button) dialog.findViewById(R.id.cancel);
                final Button ok = (Button) dialog.findViewById(R.id.ok);

                final TopUpConfirmAdapter adapter = new TopUpConfirmAdapter(getActivity(), topUpNumbers);
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
                                        if (loginResponse.getResponseStatusSimpleBean().getStatusCode() == 200) {

                                            //making a transaction Request
                                            onTopUpRequest(topUpNumbers);

                                            //Clear List data
                                            adapter.clear();
                                            adapter.notifyDataSetChanged();
                                            TextView tv = (TextView) getView().findViewById(R.id.tv);
                                            tv.setVisibility(View.VISIBLE);
                                            dialog.dismiss();

                                        } else {
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
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MultipleSellInteraction) {
            onMultipleSell = (MultipleSellInteraction) context;
            font = Typeface.createFromAsset(context.getAssets(), "font/ubuntu.ttf");
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MultipleSellInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onMultipleSell = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1000 && resultCode == Activity.RESULT_OK){

            ArrayList<Contact> selectedContacts = data.getParcelableArrayListExtra("SelectedContacts");
            List<TopUpNumber> numbers=new ArrayList<>();
            for(Contact contact: selectedContacts){

                String number = contact.phone.trim();
                number=number.replaceAll("\\W", "");
                number=number.replace("[^a-zA-Z]", "");

                TopUpNumber topUpNumber=new TopUpNumber(number,0);
                numbers.add(topUpNumber);
                Log.d(tag,"Selected Number: "+number);
            }

            int sumItems=buyList.getChildCount()+numbers.size();
            if(sumItems>3){
                Toast.makeText(getContext(),"Maximum Telephone Numbers Exceeded pick only (3)",Toast.LENGTH_LONG).show();
            }else{
                buyListHandle(numbers);
            }
        }else if(resultCode==500){
            Toast.makeText(getContext(),"Maximum Telephone Numbers Exceeded pick only (3)",Toast.LENGTH_LONG).show();
        }
    }

    private void buyListHandle(List<TopUpNumber> mList){
        Log.d(tag, "Adding Numbers");
        if (mList != null) {

            for (int rowCounter = 0; rowCounter < mList.size(); rowCounter++) {

                final TableRow row = new TableRow(getContext());
                row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT));

                final LinearLayout col = (LinearLayout) LayoutInflater.from(
                        getContext()).inflate(
                        R.layout.multiplesellstyle, null);

                row.addView(col);
                TopUpNumber item= mList.get(rowCounter);

                TextView telNumber = (TextView) row.findViewById(R.id.tel);
                telNumber.setTypeface(font);

                EditText fillAmount=(EditText) row.findViewById(R.id.amount);
                fillAmount.setTypeface(font);

                telNumber.setText(item.getMsisdn());

                ImageView remove = (ImageView) row.findViewById(R.id.remove);

                final int finalRowCounter = rowCounter;
                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buyList.removeView(row);
                    }
                });

//                // inner for loop
//                for (int j = 1; j <= 1; j++) {
//                    final LinearLayout col = (LinearLayout) LayoutInflater.from(
//                            getContext()).inflate(
//                            R.layout.multiplesellstyle, null);
//
//                    row.addView(col);
//                    TopUpNumber item= mList.get(rowCounter);
//
//                    TextView telNumber = (TextView) row.findViewById(R.id.tel);
//                    telNumber.setTypeface(font);
//
//                    EditText fillAmount=(EditText) row.findViewById(R.id.amount);
//                    fillAmount.setTypeface(font);
//
//                    telNumber.setText(item.getMsisdn());
//
//                    final int finalRowCounter = rowCounter;
//
//
////                    row.setClickable(true);
////                    col.setClickable(true);
////                    col.setOnClickListener(new View.OnClickListener() {
////                        @Override
////                        public void onClick(View v) {
////                            int col_id = row.indexOfChild(col);
////                            int row_id = buyList.indexOfChild(row);
////                            Toast.makeText(getContext(), " "+col_id + " "+row_id, Toast.LENGTH_SHORT).show();
////
////                        }
////                    });
//
//                }
                buyList.addView(row);
            }

//            for(int i=0; i<buyList.getChildCount();i++){
//                View row=buyList.getChildAt(i);
//                ImageView remove = (ImageView) row.findViewById(R.id.remove);
//                remove.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        buyList.removeViewAt(i);
//                    }
//                });
//            }

//            if(buyList.getChildCount()>0) {
//                TextView tv = (TextView) getView().findViewById(R.id.tv);
//                tv.setVisibility(View.INVISIBLE);
//            }
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
                        onMultipleSell.onMultipleSellListener(1, "success", null);
                        Log.d(tag, "Server Result:\n" + new ClientData().mapping(topUpResponse));
                    }catch (Exception e){
                        e.printStackTrace();
                        onMultipleSell.onMultipleSellListener(1, e.getMessage(), null);
                    }
                }

                @Override
                public void onFailure(Call<TopUpResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(tag, t.toString());
                    onMultipleSell.onMultipleSellListener(0, null, null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            onMultipleSell.onMultipleSellListener(0, null, null);
        }

    }

//    public static void setListViewHeightBasedOnChildren(ListView listView) {
//        ListAdapter listAdapter = listView.getAdapter();
//        if (listAdapter == null) {
//            return;
//        }
//
//        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
//        for (int i = 0; i < listAdapter.getCount(); i++) {
//            View listItem = listAdapter.getView(i, null, listView);
//            if (listItem instanceof ViewGroup)
//                listItem.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT));
//            listItem.measure(0, 0);
//            totalHeight += listItem.getMeasuredHeight();
//        }
//
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
//        listView.setLayoutParams(params);
//    }

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
    public interface MultipleSellInteraction {

        void onMultipleSellListener(int statusCode, String message, Object object);
    }
}
