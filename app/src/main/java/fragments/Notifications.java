package fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;

import com.oltranz.airtime.airtime.R;

import java.util.ArrayList;
import java.util.List;

import simplebeans.ResponseStatusSimpleBean;
import simplebeans.notifications.NotificationBean;
import utilities.NotificationAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotificationInteraction} interface
 * to handle interaction events.
 * Use the {@link Notifications#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Notifications extends Fragment {
    private static final String msisdn_param = "msisdn";
    private static final String token_param = "token";
    private String tag = "AirTime: " + getClass().getSimpleName();
    private String msisdn;
    private String token;
    private Typeface font;

    private ListView nList;
    private NotificationAdapter adapter;

    private NotificationInteraction onNotification;

    public Notifications() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param msisdn Parameter 1.
     * @param token Parameter 2.
     * @return A new instance of fragment About.
     */
    public static Notifications newInstance(String msisdn, String token) {
        Log.d("AirTime: About", "New Instance is creating");
        Notifications fragment = new Notifications();
        Bundle args = new Bundle();
        args.putString(msisdn_param, msisdn);
        args.putString(token_param, token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (getArguments() != null) {
            if(getArguments().getString(token_param) != null)
                token = getArguments().getString(token_param);
            if(getArguments().getString(msisdn_param) != null)
                msisdn = getArguments().getString(msisdn_param);
        }
        Log.d(tag, "The fragment is created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(tag, "The fragment view are being created");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.notification_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(tag, "View are finally inflated");

        nList = (ListView) view.findViewById(R.id.notificationList);

        //____________Dummy Datas______________\\
        List<NotificationBean> mNotification = new ArrayList<>();

        ResponseStatusSimpleBean status = new ResponseStatusSimpleBean("success", 400);
        NotificationBean nBean = new NotificationBean("10/09/2016 10:00", "Your AirTime TopUp Transaction Is Successfully Passed. Thank You!", status);
        mNotification.add(nBean);

        nBean = new NotificationBean("10/10/2016 10:00", "Wallet Credit Transaction Failed, Cause: Not Sufficient Funds On Main Account, Please Try Again. Thank You!", status);
        mNotification.add(nBean);

        adapter = new NotificationAdapter(getActivity(), mNotification);

        nList.setAdapter(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NotificationInteraction) {
            onNotification = (NotificationInteraction) context;
            font = Typeface.createFromAsset(context.getAssets(), "font/ubuntu.ttf");
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AboutListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onNotification = null;
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
    public interface NotificationInteraction {
        void onNotificationInteraction(int statusCode, String message, Object object);
    }
}
