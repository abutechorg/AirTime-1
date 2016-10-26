package fragments;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.oltranz.mobilea.mobilea.R;

import config.BaseUrl;
import config.WebContent;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OffersInteraction} interface
 * to handle interaction events.
 * Use the {@link Offers#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Offers extends Fragment {
    private static final String msisdn_param = "msisdn";
    private static final String token_param = "token";
    private String tag = "AirTime: " + getClass().getSimpleName();
    private String msisdn;
    private String token;
    private Typeface font;
    private WebView mWebView;
    private ProgressBar progress;


    private OffersInteraction offersInteraction;

    public Offers() {
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
    public static Offers newInstance(String msisdn, String token) {
        Log.d("AirTime: About", "New Instance is creating");
        Offers fragment = new Offers();
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
        return inflater.inflate(R.layout.offers_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mWebView = (WebView) view.findViewById(R.id.webview);
        progress = (ProgressBar) view.findViewById(R.id.progressBar);
        progress.setVisibility(View.GONE);

        //mWebView.loadData(WebContent.offers, "text/html", "utf-8");
       //mWebView.loadUrl("file:///android_asset/localwebpage/offers.html");
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.loadUrl(BaseUrl.offerUrl);


        Log.d(tag, "View are finally inflated");
    }

    private void showDialog(String mTitle, String url) {
        TextView close;
        TextView title;
        WebView mWeb;

        final Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.web_dialog);

        close = (TextView) dialog.findViewById(R.id.close);
        close.setTypeface(font, Typeface.BOLD);
        title = (TextView) dialog.findViewById(R.id.title);
        title.setTypeface(font, Typeface.BOLD);
        mWeb = (WebView) dialog.findViewById(R.id.webView);
        progress = (ProgressBar) dialog.findViewById(R.id.progressBar);
        progress.setVisibility(View.GONE);

        title.setText(mTitle);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        mWeb.setWebViewClient(new MyWebViewClient());
        mWeb.loadUrl(url);

        dialog.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OffersInteraction) {
            offersInteraction = (OffersInteraction) context;
            font = Typeface.createFromAsset(context.getAssets(), "font/ubuntu.ttf");
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AboutListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        offersInteraction = null;
    }

    private class MyWebViewClient extends WebViewClient {

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(String.valueOf(request.getUrl()));
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progress.setVisibility(View.GONE);
            progress.setProgress(100);
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progress.setVisibility(View.VISIBLE);
            progress.setProgress(0);
            super.onPageStarted(view, url, favicon);
        }
    }



    public void setValue(int progress) {
        this.progress.setProgress(progress);
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
    public interface OffersInteraction {
        void onOffersInteraction(int statusCode, String message, Object object);
    }
}
