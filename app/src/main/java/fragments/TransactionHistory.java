package fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.oltranz.airtime.airtime.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import simplebeans.ResponseStatusSimpleBean;
import simplebeans.transactionhistory.TransactionHistoryBean;
import utilities.TransactionHistoryAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TransactionHistoryInteraction} interface
 * to handle interaction events.
 * Use the {@link TransactionHistory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionHistory extends Fragment {
    private static final String msisdn_param = "msisdn";
    private static final String token_param = "token";
    private String tag = "AirTime: " + getClass().getSimpleName();
    private String msisdn;
    private String token;
    private Typeface font;
    private ListView mHistoryList;
    private EditText searchText;
    private ImageView searchButton;
    private TransactionHistoryAdapter adapter;

    private TransactionHistoryInteraction onTransactionHistory;

    public TransactionHistory() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param msisdn Parameter 1.
     * @param token  Parameter 2.
     * @return A new instance of fragment About.
     */
    public static TransactionHistory newInstance(String msisdn, String token) {
        Log.d("AirTime: TransHistory", "New Instance is creating");
        TransactionHistory fragment = new TransactionHistory();
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
            if (getArguments().getString(token_param) != null)
                token = getArguments().getString(token_param);
            if (getArguments().getString(msisdn_param) != null)
                msisdn = getArguments().getString(msisdn_param);
        }
        Log.d(tag, "The fragment is created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(tag, "The fragment view are being created");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.transaction_history_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(tag, "View are finally inflated");
        mHistoryList = (ListView) view.findViewById(R.id.mHistory);
        searchText = (EditText) view.findViewById(R.id.searchTerm);
        searchButton = (ImageView) view.findViewById(R.id.searchIcon);

        //______________DUMMY DATA________________\\
        List<TransactionHistoryBean> mHistory = new ArrayList<>();

        ResponseStatusSimpleBean status1 = new ResponseStatusSimpleBean("failed", 401);
        ResponseStatusSimpleBean status2 = new ResponseStatusSimpleBean("success", 400);
        TransactionHistoryBean wHistoryBean = new TransactionHistoryBean(1111, "10/08/2016 10:00", "Credit Card", "VISA", 500, "0785534672", status2);
        mHistory.add(wHistoryBean);
        wHistoryBean = new TransactionHistoryBean(2222, "10/09/2016 10:00", "Credit Card", "MC", 800, "0788838634", status1);
        mHistory.add(wHistoryBean);
        wHistoryBean = new TransactionHistoryBean(3333, "10/10/2016 10:00", "Credit Card", "VISA", 200, "0786077903", status1);
        mHistory.add(wHistoryBean);
        wHistoryBean = new TransactionHistoryBean(4444, "10/11/2016 10:00", "Credit Card", "VERVE", 1000, "0786077903", status2);
        mHistory.add(wHistoryBean);
        wHistoryBean = new TransactionHistoryBean(5555, "10/12/2016 10:00", "Credit Card", "VISA", 200, "0785534672", status1);
        mHistory.add(wHistoryBean);

        adapter = new TransactionHistoryAdapter(getActivity(), mHistory);
        mHistoryList.setAdapter(adapter);

        searchText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                //WalletHistory.this.adapter.getFilter().filter(cs);

                String text = searchText.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TransactionHistoryInteraction) {
            onTransactionHistory = (TransactionHistoryInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TransactionHistoryInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onTransactionHistory = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface TransactionHistoryInteraction {

        void onTransactionHistoryInteraction(int statusCode, String message, Object object);
    }
}
