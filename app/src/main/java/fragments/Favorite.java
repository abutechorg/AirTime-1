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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.oltranz.mobilea.mobilea.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import client.ClientServices;
import client.ServerClient;
import entity.NotificationTable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import simplebeans.favorite.FavoriteResponseBean;
import simplebeans.transactionhistory.TransactionHistoryBean;
import simplebeans.transactionhistory.TransactionHistoryResponse;
import utilities.FavoriteAdapter;
import utilities.GetCurrentDate;
import utilities.ResendAirtime;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Favorite.FavoriteInteraction} interface
 * to handle interaction events.
 * Use the {@link Favorite#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Favorite extends Fragment implements FavoriteAdapter.FavoritesAdapterInteraction, ResendAirtime.ResendAirtimeInteraction {
    private String tag = "AirTime: " + getClass().getSimpleName();

    private FavoriteInteraction favoriteInteraction;
    private Typeface font;
    private String token;
    private String msisdn;
    private FavoriteAdapter adapter;
    private ProgressDialog progressDialog;

    public Favorite() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param token  Parameter 1.
     * @param msisdn Parameter 2.
     * @return A new instance of fragment CheckBalance.
     */
    public static Favorite newInstance(String token, String msisdn) {
        Log.d("AirTime: Login", "New Instance is creating");
        Favorite favorite = new Favorite();
        Bundle args = new Bundle();
        args.putString("token", token);
        args.putString("msisdn", msisdn);
        favorite.setArguments(args);
        return favorite;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (getArguments() != null) {
            if (getArguments().getString("token") != null)
                token = getArguments().getString("token");
            if (getArguments().getString("msisdn") != null)
                msisdn = getArguments().getString("msisdn");
        }
        Log.d(tag, "The fragment is created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(tag, "The fragment view are being created");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.favorite_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextView label=(TextView) view.findViewById(R.id.label);
        label.setTypeface(font, Typeface.BOLD);
        //__________Initialize List______________\\
        final List<FavoriteResponseBean> mFavorite = new ArrayList<>();
        adapter = new FavoriteAdapter(this, getActivity(), mFavorite);

        ListView mList = (ListView) view.findViewById(R.id.favList);
        mList.setAdapter(adapter);


        progressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        try {
            ClientServices clientServices = ServerClient.getClient().create(ClientServices.class);
            Call<TransactionHistoryResponse> callService = clientServices.getFavorites(token, 5);
            callService.enqueue(new Callback<TransactionHistoryResponse>() {
                @Override
                public void onResponse(Call<TransactionHistoryResponse> call, Response<TransactionHistoryResponse> response) {

                    //HTTP status code
                    int statusCode = response.code();
                    if (statusCode == 200) {
                        TransactionHistoryResponse transactionHistoryResponse = response.body();
                        try {
                            if (transactionHistoryResponse.getStatus().getStatusCode() != 400) {
                                progressDialog.setMessage(transactionHistoryResponse.getStatus().getMessage());
                                label.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();
                            } else {
                                List<TransactionHistoryBean> transactionHistoryBeanList = transactionHistoryResponse.getHistory();
                                if(!transactionHistoryBeanList.isEmpty()){
                                    for (TransactionHistoryBean historyBean : transactionHistoryBeanList) {
                                        TransactionHistoryBean wHistoryBean = new TransactionHistoryBean(historyBean.getDate(), historyBean.getAmount(), historyBean.getMsisdn(), historyBean.getStatus(), "" + historyBean.getStatus().getMessage() + historyBean.getDate() + historyBean.getAmount() + historyBean.getMsisdn());
                                        FavoriteResponseBean favorite = new FavoriteResponseBean(wHistoryBean.getMsisdn(), wHistoryBean.getAmount());
                                        mFavorite.add(favorite);
                                        adapter.notifyDataSetChanged();
                                    }
                                    progressDialog.dismiss();
                                }else{
                                    label.setVisibility(View.VISIBLE);
                                    progressDialog.dismiss();
                                }
                            }
                        } catch (Exception e) {
                            progressDialog.setMessage(e.getMessage());
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }

                    } else {
                        progressDialog.dismiss();
                        favoriteInteraction.onFavoriteInteraction(403, "Failure", null);
                    }
                }

                @Override
                public void onFailure(Call<TransactionHistoryResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e(tag, t.toString());
                    progressDialog.setMessage("Connectivity Error");
                    progressDialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.setMessage(e.getMessage());
            progressDialog.dismiss();
        }

        Log.d(tag, "View are finally inflated");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FavoriteInteraction) {
            favoriteInteraction = (FavoriteInteraction) context;
            font = Typeface.createFromAsset(context.getAssets(), "font/ubuntu.ttf");
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FavoriteInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        favoriteInteraction = null;
    }

    @Override
    public void onResendAirtimeAdapter(String tel, String amount) {
        ResendAirtime resendAirtime = new ResendAirtime(this, getActivity(), token, msisdn);
        resendAirtime.startResend(tel, Double.valueOf(amount));
    }

    @Override
    public void onResendAirtimeModule(int statusCode, String message) {
        String date = new GetCurrentDate(getContext()).getServerDate();
        if (!TextUtils.isEmpty(message)) {
            NotificationTable notificationTable = new NotificationTable(date, "AirTime TopUp " + message, msisdn, "0");
            notificationTable.save();
        }
        uiFeed("" + message);
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

        favoriteInteraction.onFavoriteInteraction(0, "refresh", null);
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
    public interface FavoriteInteraction {
        void onFavoriteInteraction(int statusCode, String message, Object object);
    }
}
