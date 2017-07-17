package com.oltranz.mobilea.mobilea;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.vistrav.ask.Ask;
import com.vistrav.ask.annotations.AskDenied;
import com.vistrav.ask.annotations.AskGranted;

import client.ClientData;
import client.ClientServices;
import client.ServerClient;
import config.BaseUrl;
import fragments.Login;
import fragments.Register;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import simplebeans.SimpleStatusBean;
import simplebeans.StatusUsage;
import simplebeans.loginbeans.LoginResponse;
import simplebeans.registerbeans.RegisterResponse;
import utilities.IsGranted;
/**
 * Created by Eng. ISHIMWE Aubain Consolateur email: iaubain@yahoo.fr / aubain.c.ishimwe@oltranz.com Tel: (250) 785 534 672.
 * application Home activity or main activity
 */
public class Home extends AppCompatActivity implements Login.LoginInteractionListener, Register.RegisterInteractionListener {
    static final int PICK_CONTACT_REQUEST = 1;
    private String tag = "AirTime: " + getClass().getSimpleName();
    private TextView titleBar;
    private ImageView register;
    private ImageView help;
    private TextView tv;
    private Typeface font;
    private Login loginFrag;
    private Register registerFrag;
    private FragmentManager fragmentManager;
    private boolean isRegisterClicked = false;
    private Toolbar toolbar;

    private AlertDialog refDialog;
    private EditText refNumber;
    private TextView lbl1, lbl2;
    private ImageView browseTel;
    private View layout;
    private String appVersion;
    private WebView webView;
    private ProgressBar progress;

    /**
     *
     * @param savedInstanceState application saved instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.home_layout);
        //request Permission
        rightManager();

        font = Typeface.createFromAsset(this.getAssets(), "font/ubuntu.ttf");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle("");
        titleBar = (TextView) toolbar.findViewById(R.id.toolbar_title);
        titleBar.setTypeface(font);
        setSupportActionBar(toolbar);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            try {
                getSupportActionBar().hide();
            } catch (Exception e) {
                e.printStackTrace();
            }

            fragmentManager = getSupportFragmentManager();
//            tv=(TextView) findViewById(R.id.tv);
//            tv.setTypeface(font);
//            tv.setVisibility(View.GONE);
            // Create the new Fragment to be placed in the activity layout
            loginFrag = new Login();
            registerFrag = new Register();
            // Add the fragment to the 'fragment_container' FrameLayout
            fragmentHandler(loginFrag);
            //fragmentManager.beginTransaction().add(R.id.fragment_container, loginFrag).addToBackStack(null).commit();
            register = (ImageView) findViewById(R.id.register);
            help = (ImageView) findViewById(R.id.help);

            final Context cont = this;
            help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(tag, "Help triggered");
                    showDialog("HELP", BaseUrl.helpUrl);

                }
            });

            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isRegisterClicked) {
                        fragmentHandler(registerFrag);
                        isRegisterClicked = true;
                        register.setImageResource(R.drawable.login_selector);
                        try {
                            getSupportActionBar().show();
                            TableRow headerLogo = (TableRow) findViewById(R.id.headerLogo);
                            headerLogo.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        fragmentHandler(loginFrag);
                        isRegisterClicked = false;
                        register.setImageResource(R.drawable.register_selector);
                        try {
                            getSupportActionBar().hide();
                            TableRow headerLogo = (TableRow) findViewById(R.id.headerLogo);
                            headerLogo.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            //popup window

        }
    }

    /**
     * Show popup dialog
     * @param mTitle popup title
     * @param url requesting url for webview
     */
    private void showDialog(String mTitle, String url) {
        TextView close;
        TextView title;
        WebView mWeb;

        final Dialog dialog = new Dialog(Home.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.web_dialog);

        progress = (ProgressBar) dialog.findViewById(R.id.progressBar);
        progress.setVisibility(View.GONE);
        close = (TextView) dialog.findViewById(R.id.close);
        close.setTypeface(font, Typeface.BOLD);
        title = (TextView) dialog.findViewById(R.id.title);
        title.setTypeface(font, Typeface.BOLD);
        mWeb = (WebView) dialog.findViewById(R.id.webView);

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

    /**
     * Permission access manager for different android version
     */
    private void rightManager() {
        int currentVersion = 0;
        try {
            currentVersion = android.os.Build.VERSION.SDK_INT;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (currentVersion > 0 && currentVersion >= 23) {
            Ask.on(this)
                    .forPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.GET_ACCOUNTS)
                    .withRationales("In order to save useful session data, storage Permission is needed.",
                            "To properly identify your session and establish a secured connection Phone State permission is needed.",
                            "For some sophisticated data validation in the app Receive SMS permission is needed",
                            "Help this app get secured with your primary email address.") //optional
                    .go();
        } else {
            return;
        }
    }

    /**
     * LoginInteraction callback
     * @param statusCode link status code
     * @param message notification message
     * @param msisdn user msisdn
     * @param balance current wallet balance
     * @param lDate last account activity date
     * @param loginResponse loginResponse data
     */
    @Override
    public void onLoginInteraction(int statusCode, String message, String msisdn, String balance, String lDate, LoginResponse loginResponse) {
        if (statusCode != 400) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Error: " + message)
                    .setTitle(R.string.dialog_title);
            // Add the buttons

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {

            try {
                Intent intent = new Intent(this, UserHome.class);
                Bundle bundle = new Bundle();
                String temp1 = loginResponse.getLoginResponseBean().getFirstName();
                String temp2 = loginResponse.getLoginResponseBean().getLastName();
                String temp3 = temp1.length() > temp2.length() ? temp2 : temp1;

                bundle.putString("userName", temp3);
                bundle.putString("token", loginResponse.getLoginResponseBean().getToken());
                bundle.putString("msisdn", msisdn);
                bundle.putString("balance", balance);
                bundle.putString("walletDate", lDate);
                intent.putExtras(bundle);

                intent.setFlags(IntentCompat.FLAG_ACTIVITY_TASK_ON_HOME | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                //uiFeed(e.getMessage());
            }
        }
    }

    /**
     * RegistrationListener callback
     * @param statusCode link status code
     * @param message notification message
     * @param msisdn user msisdn
     * @param registerResponse register response
     */
    @Override
    public void onRegisterInteraction(int statusCode, String message, String msisdn, RegisterResponse registerResponse) {
        //fragmentHandler(loginFrag);
        // fragmentManager.beginTransaction().add(R.id.fragment_container, loginFrag).addToBackStack(null).commit();
        if (statusCode != 400) {
            if (!TextUtils.isEmpty(message))
                uiFeed(message);
            else
                uiFeed(registerResponse.getSimpleStatusBean().getMessage());
        } else {
            try {
                Intent intent = new Intent(this, UserHome.class);
                Bundle bundle = new Bundle();
                String temp1 = registerResponse.getRegisterResponseBean().getFirstName();
                String temp2 = registerResponse.getRegisterResponseBean().getLastName();
                String temp3 = temp1.length() > temp2.length() ? temp2 : temp1;

                bundle.putString("userName", temp3);
                bundle.putString("token", registerResponse.getRegisterResponseBean().getToken());
                bundle.putString("msisdn", msisdn);
                bundle.putString("balance", "0");
                bundle.putString("walletDate", "000-00-00 00:00");
                intent.putExtras(bundle);
                intent.setFlags(IntentCompat.FLAG_ACTIVITY_TASK_ON_HOME | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);

                setRefferreer(intent, registerResponse.getRegisterResponseBean().getToken());
            } catch (Exception e) {
                e.printStackTrace();
                uiFeed(e.getMessage());
            }
        }
    }

    /**
     * Setting the refferer msisdn
     * @param intent intent instacne that hold the next activity
     * @param token session token
     */
    private void setRefferreer(final Intent intent, final String token) {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);

        font = Typeface.createFromAsset(getAssets(), "font/ubuntu.ttf");
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.referrer_layout, (ViewGroup) findViewById(R.id.root));
        lbl1 = (TextView) layout.findViewById(R.id.label);
        lbl1.setTypeface(font);

        lbl2 = (TextView) layout.findViewById(R.id.lblHint);
        lbl2.setTypeface(font, Typeface.BOLD);

        refNumber = (EditText) layout.findViewById(R.id.tel);
        refNumber.setTypeface(font);

        browseTel = (ImageView) layout.findViewById(R.id.getNumber);
        browseTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new IsGranted(Home.this).checkReadContacts()) {
                    Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                    pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
                    startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
                } else {
                    Ask.on(Home.this)
                            .forPermissions(Manifest.permission.READ_CONTACTS)
                            .withRationales("In Order to make your life easy for contact pick up application needs Read Contact permission") //optional
                            .go();
                }
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(R.string.notification);

        builder.setNegativeButton(R.string.skip, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                endActivity();
                startActivity(intent);
            }
        });

        builder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int which) {
                appVersion = "versionCode: " + BuildConfig.VERSION_CODE + " versionName: " + BuildConfig.VERSION_NAME;
                String refMsisdn=refNumber.getText().toString();
                if(TextUtils.isEmpty(refMsisdn))
                    refMsisdn="0780000000";
                try {
                    progressDialog.setMessage("Sending...");
                    progressDialog.show();

                    ClientServices clientServices = ServerClient.getClient().create(ClientServices.class);
                    Call<SimpleStatusBean> callService = clientServices.referrer(appVersion, token, refMsisdn);
                    callService.enqueue(new Callback<SimpleStatusBean>() {
                        @Override
                        public void onResponse(Call<SimpleStatusBean> call, Response<SimpleStatusBean> response) {
                            if(progressDialog != null)
                                if(progressDialog.isShowing())
                                    progressDialog.dismiss();
                            //HTTP status code
                            int statusCode = response.code();

                            if (statusCode == 200) {
                                if(refDialog != null)
                                    if(!refDialog.isShowing())
                                        refDialog.show();
                                try {
                                    final SimpleStatusBean status = response.body();
                                    //handle the response from the server
                                    Log.d(tag, "Server Result:\n" + new ClientData().mapping(status));
                                    try {
                                        if (status.getStatusCode() != 400) {
                                            lbl2.setText(status.getMessage() + "");
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    lbl2.setText("Referrer phone number");
                                                }
                                            }, 2000);
                                        } else {
                                            lbl2.setText(status.getMessage() + "");
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    dialog.dismiss();
                                                    endActivity();
                                                    startActivity(intent);
                                                }
                                            }, 3000);
                                        }
                                    } catch (Exception e) {
                                        lbl2.setText(e.getMessage());
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                lbl2.setText("Referrer phone number");
                                            }
                                        }, 2000);
                                    }

                                } catch (final Exception e) {
                                    lbl2.setText(e.getMessage());
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            lbl2.setText("Referrer phone number");
                                        }
                                    }, 2000);
                                }
                            } else {
                                if(refDialog != null)
                                    if(!refDialog.isShowing())
                                        refDialog.show();
                                lbl2.setText(response.message());
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        lbl2.setText("Referrer phone number");
                                        fragmentHandler(loginFrag);
                                    }
                                }, 2000);
                            }
                        }

                        @Override
                        public void onFailure(Call<SimpleStatusBean> call, Throwable t) {
                            // Log error here since request failed
                            if(refDialog != null)
                                if(!refDialog.isShowing())
                                    refDialog.show();

                            if(progressDialog != null)
                                if(progressDialog.isShowing())
                                    progressDialog.dismiss();
                            Log.e(tag, t.toString());
                            uiFeed("Connectivity Error");
                            //loginListener.onLoginInteraction(500, t.getMessage(), msisdn, null);
                        }
                    });
                } catch (Exception e) {
                    if(progressDialog != null)
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
                    e.printStackTrace();
                    uiFeed(e.getMessage());
                    //loginListener.onLoginInteraction(500, e.getMessage(), msisdn, null);
                }
            }
        });

        refDialog = builder.create();
        refDialog.setCancelable(false);
        refDialog.setCanceledOnTouchOutside(false);
        refDialog.show();
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
        Toast.makeText(Home.this, "Sorry, Without READ_CONTACTS Permission the application workflow can be easily compromised", Toast.LENGTH_LONG).show();
    }

    /**
     * UI popup feedback
     * @param message message to sho on popup
     */
    private void uiFeed(String message) {
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
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


            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //optional
    @AskGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void fileAccessGranted() {
        Log.i(tag, "FILE  GRANTED");
    }

    //optional
    @AskDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void fileAccessDenied() {
        Log.i(tag, "FILE  DENiED");
        finish();
    }

    //optional
    @AskGranted(Manifest.permission.READ_PHONE_STATE)
    public void phoneStateAllowed() {
        Log.i(tag, "PHONE SATE GRANTED");
    }

    //optional
    @AskDenied(Manifest.permission.READ_PHONE_STATE)
    public void phoneStateDenied() {
        Log.i(tag, "PHONE SATE DENIED");
        Toast.makeText(this, "Sorry, Without This Permission the application security can easily be compromised", Toast.LENGTH_LONG).show();
        finish();
    }

    //optional
    @AskGranted(Manifest.permission.GET_ACCOUNTS)
    public void getAccountAllowed() {
        Log.i(tag, "PHONE SATE GRANTED");
    }

    //optional
    @AskDenied(Manifest.permission.GET_ACCOUNTS)
    public void getAccountDenied() {
        Log.i(tag, "PHONE SATE DENIED");
        Toast.makeText(this, "Sorry, Without This Permission GET_ACCOUNTS the application security can easily be compromised", Toast.LENGTH_LONG).show();
        finish();
    }

    //optional
    @AskGranted(Manifest.permission.RECEIVE_SMS)
    public void receiveSMSAllowed() {
        Log.i(tag, "PHONE SATE GRANTED");
    }

    //optional
    @AskDenied(Manifest.permission.RECEIVE_SMS)
    public void receiveSMSDenied() {
        Log.i(tag, "PHONE SATE DENIED");
        Toast.makeText(this, "Sorry, Without This Permission RECEIVE_SMS the application security can easily be compromised", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentManager = getSupportFragmentManager();
    }

    /**
     * Handle soft back key processes
     */
    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
        titleBar.setText(fragmentManager.findFragmentById(R.id.fragment_container).getClass().getSimpleName());

        try {

            Fragment currentFrag = fragmentManager.findFragmentById(R.id.fragment_container);
            if (currentFrag.getClass().getSimpleName().equals(Login.class.getSimpleName())) {
                isRegisterClicked = false;
                register.setImageResource(R.drawable.register_selector);
                try {
                    TableRow headerLogo = (TableRow) findViewById(R.id.headerLogo);
                    headerLogo.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (currentFrag.getClass().getSimpleName().equals(Register.class.getSimpleName())) {
                isRegisterClicked = true;
                register.setImageResource(R.drawable.login_selector);
                try {
                    getSupportActionBar().show();
                    toolbar.setVisibility(View.VISIBLE);
                    TableRow headerLogo = (TableRow) findViewById(R.id.headerLogo);
                    headerLogo.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    getSupportActionBar().hide();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Activity fragment manager
     * @param object an instance of fragment
     */
    private void fragmentHandler(Object object) {
        Fragment fragment = (Fragment) object;
        String backStateName = fragment.getClass().getSimpleName();
        String fragmentTag = backStateName;

        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped && fragmentManager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.fragment_container, fragment, fragmentTag);
            ft.addToBackStack(backStateName);
            ft.commit();
        } else {
            Log.d(tag, "Fragment already There");
        }
        titleBar.setText(fragmentTag);
    }

    /**
     * Getting result from other activities such as getting the msisdn from phone book
     * @param requestCode internal activity request code
     * @param resultCode external activity result codes
     * @param data imported data
     */
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
                Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String number = cursor.getString(column).trim();
                number = number.replaceAll("\\W", "");
                number = number.replace("[^a-zA-Z]", "");

                Log.d(tag, "Picked Number: " + number + " Of: " + name);
                // Do something with the phone number...
                try {
                    EditText tel = (EditText) layout.findViewById(R.id.tel);
                    tel.setText("");
                    tel.append(number);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Common application end
     */
    private void endActivity() {
        finish();
    }

    /**
     * Web client for handling web pages
     */
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
            Home.this.progress.setProgress(100);
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progress.setVisibility(View.VISIBLE);
            Home.this.progress.setProgress(0);
            super.onPageStarted(view, url, favicon);
        }
    }



    public void setValue(int progress) {
        this.progress.setProgress(progress);
    }
}
