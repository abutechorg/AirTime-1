package com.oltranz.airtime.airtime;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import fragments.Login;
import fragments.Register;
import simplebeans.loginbeans.LoginResponse;
import simplebeans.registerbeans.RegisterResponse;

public class Home extends AppCompatActivity implements Login.LoginInteractionListener, Register.RegisterInteractionListener {
    private String tag="AirTime: "+getClass().getSimpleName();
    private TextView titleBar;
    private ImageView register;
    private ImageView help;
    private TextView tv;
    private Typeface font;
    private Login loginFrag;
    private Register registerFrag;
    private FragmentManager fragmentManager;
    private boolean isRegisterClicked=false;

    //experimenting
    static final int PICK_CONTACT_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.home_layout);
        font = Typeface.createFromAsset(this.getAssets(), "font/ubuntu.ttf");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        titleBar=(TextView) toolbar.findViewById(R.id.toolbar_title);
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

            fragmentManager=getSupportFragmentManager();
//            tv=(TextView) findViewById(R.id.tv);
//            tv.setTypeface(font);
//            tv.setVisibility(View.GONE);
            // Create the new Fragment to be placed in the activity layout
            loginFrag =new Login();
            registerFrag=new Register();
            // Add the fragment to the 'fragment_container' FrameLayout
            fragmentHandler(loginFrag);
            //fragmentManager.beginTransaction().add(R.id.fragment_container, loginFrag).addToBackStack(null).commit();
            register=(ImageView) findViewById(R.id.register);
            help=(ImageView) findViewById(R.id.help);

            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!isRegisterClicked){
                        fragmentHandler(registerFrag);
                        isRegisterClicked=true;
                        register.setImageResource(R.drawable.icon_login);
                        try{
                            TableRow headerLogo=(TableRow) findViewById(R.id.headerLogo);
                            headerLogo.setVisibility(View.GONE);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        fragmentHandler(loginFrag);
                        isRegisterClicked=false;
                        register.setImageResource(R.drawable.icon_register);
                        try{
                            TableRow headerLogo=(TableRow) findViewById(R.id.headerLogo);
                            headerLogo.setVisibility(View.VISIBLE);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            });

            help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }else{
            //popup window

        }
    }

    @Override
    public void onLoginInteraction(int statusCode, String message, String msisdn, LoginResponse loginResponse) {
        if(statusCode != 400) {
            //uiFeed(message);
        }else{

            try{
                Intent intent=new Intent(this, UserHome.class);
                Bundle bundle=new Bundle();
                String temp1=loginResponse.getLoginResponseBean().getFirstName();
                String temp2=loginResponse.getLoginResponseBean().getLastName();
                String temp3=temp1.length()>temp2.length()?temp2:temp1;

                bundle.putString("userName", temp3);
                bundle.putString("token",loginResponse.getLoginResponseBean().getToken());
                bundle.putString("msisdn", msisdn);
                intent.putExtras(bundle);

                intent.setFlags(IntentCompat.FLAG_ACTIVITY_TASK_ON_HOME | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                endActivity();
                startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
                //uiFeed(e.getMessage());
            }
        }
    }

    @Override
    public void onRegisterInteraction(int statusCode, String message, String msisdn, RegisterResponse registerResponse) {
        //fragmentHandler(loginFrag);
       // fragmentManager.beginTransaction().add(R.id.fragment_container, loginFrag).addToBackStack(null).commit();
        if(statusCode !=400){
            //uiFeed(message);
        }else{
            try {
                Intent intent=new Intent(this, UserHome.class);
                Bundle bundle=new Bundle();
                String temp1=registerResponse.getRegisterResponseBean().getFirstName();
                String temp2=registerResponse.getRegisterResponseBean().getLastName();
                String temp3=temp1.length()>temp2.length()?temp2:temp1;

                bundle.putString("userName", temp3);
                bundle.putString("token",registerResponse.getRegisterResponseBean().getToken());
                bundle.putString("msisdn", msisdn);
                intent.putExtras(bundle);

                intent.setFlags(IntentCompat.FLAG_ACTIVITY_TASK_ON_HOME | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                endActivity();
                startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
                //uiFeed(e.getMessage());
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        fragmentManager=getSupportFragmentManager();
    }

    @Override
    public void onBackPressed(){
        if (fragmentManager.getBackStackEntryCount() == 1){
            finish();
        }
        else {
            super.onBackPressed();
        }
        titleBar.setText(fragmentManager.findFragmentById(R.id.fragment_container).getClass().getSimpleName());

        try{

            Fragment currentFrag=fragmentManager.findFragmentById(R.id.fragment_container);
            if(currentFrag.getClass().getSimpleName().equals(Login.class.getSimpleName())){
                isRegisterClicked=false;
                register.setImageResource(R.drawable.icon_register);
                try{
                    TableRow headerLogo=(TableRow) findViewById(R.id.headerLogo);
                    headerLogo.setVisibility(View.VISIBLE);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if(currentFrag.getClass().getSimpleName().equals(Register.class.getSimpleName())){
                isRegisterClicked=true;
                register.setImageResource(R.drawable.icon_login);
                try{
                    TableRow headerLogo=(TableRow) findViewById(R.id.headerLogo);
                    headerLogo.setVisibility(View.GONE);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void fragmentHandler(Object object){
        Fragment fragment=(Fragment) object;
        String backStateName =  fragment.getClass().getSimpleName();
        String fragmentTag = backStateName;

        boolean fragmentPopped = fragmentManager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped && fragmentManager.findFragmentByTag(fragmentTag) == null){ //fragment not in back stack, create it.
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.fragment_container, fragment, fragmentTag);
            ft.addToBackStack(backStateName);
            ft.commit();
        }else{
            Log.d(tag, "Fragment already There");
        }
        titleBar.setText(fragmentTag);
    }

    private void endActivity(){
        finish();
    }
}
