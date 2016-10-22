package utilities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.oltranz.mobilea.mobilea.R;
import com.oltranz.mobilea.mobilea.UserHome;

import fragments.About;
import fragments.Notifications;
import fragments.TransactionHistory;
import utilities.utilitiesbeans.MySessionData;

public class Extra extends AppCompatActivity implements About.AboutListener, Notifications.NotificationInteraction, TransactionHistory.TransactionHistoryInteraction {
    private static final String sessionData = "sessionData";
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
                // Restore value of members from saved state
                Log.e(tag, "Activity Create Restoring session data");
                mSession = savedInstanceState.getParcelable(sessionData);

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
            } else if (bundle.getString("what").equals(TransactionHistory.class.getSimpleName())) {
                //TransactionHistory
                titleBar.setText("Transactions History");
                transactionHistoryFrag();
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

    private void transactionHistoryFrag() {
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setArguments(setArgs());
        fragmentHandler(transactionHistory, R.id.extra);
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
    public void onResume() {
        super.onResume();
        Log.d(tag, "Resumed the activity");
        try {
            currentFrag();

            titleBar = (TextView) toolbar.findViewById(R.id.toolbar_title);
            titleBar.setTypeface(font);
            Log.d(tag, "Logged user " + mSession.getUserName());
        } catch (Exception e) {
            Log.e(tag, Log.getStackTraceString(e));
            e.printStackTrace();
            onHomeActivity();
        }
    }

    private void setFragment() {
        //initiate my session data;
        Bundle bundle = getIntent().getExtras();
        mSession = new MySessionData(bundle.getString("token"), bundle.getString("msisdn"), bundle.getString("userName"));
        if (bundle.getString("what").equals(About.class.getSimpleName())) {
            //about
            titleBar.setText("About");
            aboutFrag();
        } else if (bundle.getString("what").equals(Notifications.class.getSimpleName())) {
            //notifications
            titleBar.setText("Notifications");
            notifFrag();
        } else if (bundle.getString("what").equals(TransactionHistory.class.getSimpleName())) {
            //TransactionHistory
            titleBar.setText("Transactions History");
            transactionHistoryFrag();
        }
    }

    private void currentFrag() {
        try {

            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.extra);
            if (currentFragment == null)
                setFragment();

            if (currentFragment.getClass().getSimpleName().equals(About.class.getSimpleName())) {
                //about
                titleBar.setText("About");
            }

            if (currentFragment.getClass().getSimpleName().equals(TransactionHistory.class.getSimpleName())) {
                //TransactionHistory
                titleBar.setText("Transactions History");
            }


            if (currentFragment.getClass().getSimpleName().equals(Notifications.class.getSimpleName())) {
                //notifications
                titleBar.setText("Notifications");
            }

            Log.d(tag, "Current Fragment " + currentFragment.getClass().getSimpleName());
        } catch (Exception e) {
            Log.e(tag, Log.getStackTraceString(e));
            onHomeActivity();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d(tag, "Activity Storing session data");
        // Save the user's current game state
        savedInstanceState.putParcelable(sessionData, mSession);

        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.d(tag, "Activity Recreate Restoring session data");
        // Restore state members from saved instance
        mSession = savedInstanceState.getParcelable(sessionData);
    }

    private void onHomeActivity() {
        try {
            Intent intent = new Intent(this, UserHome.class);
            Bundle bundle = new Bundle();

            bundle.putString("userName", mSession.getUserName());
            bundle.putString("token", mSession.getToken());
            bundle.putString("msisdn", mSession.getMsisdn());
            intent.putExtras(bundle);

            intent.setFlags(IntentCompat.FLAG_ACTIVITY_TASK_ON_HOME | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            //uiFeed(e.getMessage());
            this.finish();
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

    @Override
    public void onTransactionHistoryInteraction(int statusCode, String message, Object object) {

    }
}
