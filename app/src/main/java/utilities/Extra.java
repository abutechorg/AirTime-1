package utilities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.oltranz.airtime.airtime.R;

import fragments.About;
import fragments.Notifications;
import fragments.RechargeWallet;
import fragments.SingleSell;
import utilities.utilitiesbeans.MySessionData;

public class Extra extends AppCompatActivity implements About.AboutListener, Notifications.NotificationInteraction {
    private final String tag="AirTime: "+getClass().getSimpleName();
    private TextView titleBar;
    private MySessionData mSession;
    private Typeface font;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.extralayout);
        font = Typeface.createFromAsset(this.getAssets(), "font/ubuntu.ttf");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        titleBar=(TextView) toolbar.findViewById(R.id.toolbar_title);
        titleBar.setTypeface(font);
        setSupportActionBar(toolbar);
        ActionBar actionBar;
        try{
            actionBar=getSupportActionBar();
            assert actionBar != null;
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

        }catch (Exception e){
            e.printStackTrace();
        }

        if (findViewById(R.id.extra) != null ) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            //initiate my session data;
            Bundle bundle=getIntent().getExtras();
            mSession=new MySessionData(bundle.getString("token"),bundle.getString("msisdn"),bundle.getString("userName"));
            if(bundle.getString("what").equals(About.class.getSimpleName())){
                //about
                titleBar.setText("About");
                aboutFrag();
            }else if(bundle.getString("what").equals(Notifications.class.getSimpleName())){
                //notifications
                titleBar.setText("Notifications");
                notifFrag();
            }
        }
    }

    //_________Fragments manager___________\\
    private void fragmentHandler(Object object, int fragId){
        FragmentManager fragmentManager=getSupportFragmentManager();
        Fragment fragment=(Fragment) object;
        String backStateName =  fragment.getClass().getSimpleName();
        String fragmentTag = backStateName;

        boolean fragmentPopped = fragmentManager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped && fragmentManager.findFragmentByTag(fragmentTag) == null){ //fragment not in back stack, create it.
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(fragId, fragment, fragmentTag);
            ft.addToBackStack(backStateName);
            ft.commit();
        }else{
            Log.d(tag, "Fragment already There");
        }
    }

    private void aboutFrag(){
        About about=new About();
        about.setArguments(setArgs());
        fragmentHandler(about, R.id.extra);
    }

    private void notifFrag(){
        Notifications notifications=new Notifications();
        notifications.setArguments(setArgs());
        fragmentHandler(notifications, R.id.extra);
    }

    private Bundle setArgs(){
        Bundle bundle=new Bundle();
        bundle.putString("token", mSession.getToken());
        bundle.putString("msisdn", mSession.getMsisdn());

        return bundle;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        //finishAndRemoveTask();
    }
    @Override
    public void onAboutInteraction(int statusCode, String message, Object object) {

    }
    @Override
    public void onNotificationInteraction(int statusCode, String message, Object object) {

    }
}
