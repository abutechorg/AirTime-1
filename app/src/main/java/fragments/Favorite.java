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
import android.widget.TextView;

import com.oltranz.airtime.airtime.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Favorite.FavoriteInteraction} interface
 * to handle interaction events.
 * Use the {@link Favorite#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Favorite extends Fragment {
    private String tag="AirTime: "+getClass().getSimpleName();

    private FavoriteInteraction favoriteInteraction;
    private Typeface font;
    private String token;
    private String msisdn;

    public Favorite() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param token Parameter 1.
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
        return inflater.inflate(R.layout.favorite_layout, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(tag, "View are finally inflated");
        TextView test=(TextView) view.findViewById(R.id.test);
        test.setTypeface(font);
        test.setText("Test Message Ubuntu, and y");
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
