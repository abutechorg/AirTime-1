package com.oltranz.airtime.airtime;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vistrav.ask.Ask;
import com.vistrav.ask.annotations.AskDenied;
import com.vistrav.ask.annotations.AskGranted;

import config.BaseUrl;
import fragments.About;
import fragments.CheckBalance;
import fragments.Favorite;
import fragments.MultipleSell;
import fragments.Notifications;
import fragments.RechargeWallet;
import fragments.SingleSell;
import fragments.TransactionHistory;
import fragments.WalletHistory;
import utilities.Extra;
import utilities.utilitiesbeans.MySessionData;

public class UserHome extends AppCompatActivity implements CheckBalance.CheckBalanceInteraction, SingleSell.SingleSellInteractionListener, MultipleSell.MultipleSellInteraction, RechargeWallet.RechargeWalletListener, WalletHistory.WalletHistoryInteraction, Favorite.FavoriteInteraction {
    private static final String sessionData = "sessionData";
    private String tag="AirTime: "+getClass().getSimpleName();
    private FragmentManager fragmentManager;
    private Typeface font;
    private TextView titleBar;
    private MySessionData mSession;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private TextView mainBadge, sideBadge;
    private LinearLayout mainTabHolder;
    private View accountTab;
    private View walletTab;
    private TextView welcomeUser;
    private TextView lblWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.userhome_layout);

        //request Permission
        rightManager();

        font = Typeface.createFromAsset(this.getAssets(), "font/ubuntu.ttf");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        titleBar=(TextView) toolbar.findViewById(R.id.toolbar_title);
        titleBar.setTypeface(font, Typeface.BOLD);
        setSupportActionBar(toolbar);

        //adding a menu
        menuHandle();

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.checkBalanceFrame) != null &&
                findViewById(R.id.mainButtonFrame) != null &&
                findViewById(R.id.mainTabFrame) != null &&
                findViewById(R.id.salesFrame) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {

                // Restore value of members from saved state
                Log.e(tag, "Activity Create Restoring session data");
                mSession = savedInstanceState.getParcelable(sessionData);
                //mCurrentLevel = savedInstanceState.getInt(STATE_LEVEL);
                return;
            }

            //initiate my session data;
            Bundle bundle=getIntent().getExtras();
            mSession=new MySessionData(bundle.getString("token"),bundle.getString("msisdn"),bundle.getString("userName"));
//            TextView wlcmLabel=(TextView) findViewById(R.id.welcomeLabel);
//            wlcmLabel.setTypeface(font);
//            Log.d(tag,"Logged user "+mSession.getUserName());
//            wlcmLabel.append(mSession.getUserName());

            //Activity tab holder initialisation
            mainTabHolder = (LinearLayout) findViewById(R.id.mainTabFrame);

            //Activity tabs initiation
            accountTab=View.inflate(this, R.layout.maintabs, null);

            walletTab=View.inflate(this, R.layout.wallettabs, null);

            fragmentManager = getSupportFragmentManager();

            /* *populating fragment holder with their Fragment**/

            //Adding Check balance Fragment
            checkBalanceFrag();
            //By default set Single topup
            singleTopUpFrag();
        }

    }

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
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.SEND_SMS)
                    .withRationales("In order to save useful session data, storage Permission is needed.",
                            "To properly identify your session and establish a secured connection Phone State right is needed.",
                            "In Order to make your life easy for contact pick up application needs Read Contact permission",
                            "For some sophisticated data validation in the app Receive SMS permission is needed",
                            "For some sophisticated data validation in the app Send SMS permission is needed") //optional
                    .go();
        } else {
            return;
        }
    }
    private void menuHandle(){
        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        Menu menu = null;
        MenuItem item = null;
        try {
            menu = navigationView.getMenu();
            item = menu.findItem(R.id.notifications);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MenuItemCompat.setActionView(item, R.layout.menucounter);
        RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);

        sideBadge = (TextView) notifCount.findViewById(R.id.actionbar_notifcation_textview);
        counter(sideBadge);

        //setting user name for the header
        View header = navigationView.getHeaderView(0);
        try {
            lblWelcome = (TextView) header.findViewById(R.id.lblWelcome);
            lblWelcome.setTypeface(font);

            welcomeUser = (TextView) header.findViewById(R.id.welcomeUser);
            welcomeUser.setTypeface(font);
            welcomeUser.setText(mSession.getUserName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                drawerLayout.closeDrawers();
                return menuSelected(menuItem.getItemId());
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.app_name, R.string.app_name){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

    }

    public void walletAddAmount(View v){
        walletTopUpFrag();
    }

    public void walletViewHistory(View v){
        walletHistoryFrag();
    }

    public void onAccountTabClick(View v){
        if(v.getId() == R.id.sendOne){
            singleTopUpFrag();
        }
        if(v.getId() == R.id.sendMany){
            multipleTopUpFrag();
        }
        if(v.getId() == R.id.favorites){
            favoriteFrag();
        }
    }

    public void onWalletTabClick(View v){
        if(v.getId() == R.id.addAirtime){
            walletTopUpFrag();
        }
        if(v.getId() == R.id.viewHistory){
            walletHistoryFrag();
        }
    }

    @Override
    public void onRechargeWalletInteraction(int statusCode, String message, String msisdn, Object object) {

    }

    @Override
    public void onSingleSell(int statusCode, String message, Object object) {
        Log.d(tag,"Single Selll");
        Fragment frg = null;
        frg = getSupportFragmentManager().findFragmentByTag(CheckBalance.class.getSimpleName());
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
//        if(statusCode != 0){
//            checkBalanceFrag();
//        }
//        if(message != null && statusCode != 2){
//            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
//        }
    }

    @Override
    public void onMultipleSellListener(int statusCode, String message, Object object) {

        Fragment frg = null;
        frg = getSupportFragmentManager().findFragmentByTag(CheckBalance.class.getSimpleName());
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
//        if(statusCode != 0){
//            checkBalanceFrag();
//        }
//        if(message != null && statusCode != 2){
//            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
//        }
    }

    @Override
    public void onCheckBalanceInteraction(int statusCode, String message,Object object) {

    }

    @Override
    public void onFavoriteInteraction(int statusCode, String message, Object object) {

    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(tag, "Resumed the app check");
        try {
            //Activity tab holder initialisation
            mainTabHolder = (LinearLayout) findViewById(R.id.mainTabFrame);

            //Activity tabs initiation
            accountTab = View.inflate(this, R.layout.maintabs, null);

            walletTab = View.inflate(this, R.layout.wallettabs, null);

            fragmentManager = getSupportFragmentManager();

            //Initializing NavigationView
            navigationView = (NavigationView) findViewById(R.id.navigation_view);
            //setting user name for the header
            View header = navigationView.getHeaderView(0);
            try {
                lblWelcome = (TextView) header.findViewById(R.id.lblWelcome);
                lblWelcome.setTypeface(font);

                welcomeUser = (TextView) header.findViewById(R.id.welcomeUser);
                welcomeUser.setTypeface(font);
                welcomeUser.setText(mSession.getUserName());
            } catch (Exception e) {
                e.printStackTrace();
            }

//            TextView wlcmLabel=(TextView) findViewById(R.id.welcomeLabel);
//            wlcmLabel.setTypeface(font);
//            Log.d(tag,"Logged user "+mSession.getUserName());
//            wlcmLabel.setText("Welcome ");
//            wlcmLabel.append(mSession.getUserName());

            Log.d(tag, "current tab ");
            currentFrag();

            //refreshBalance();

        } catch (Exception e) {
            Log.e(tag, Log.getStackTraceString(e));
            e.printStackTrace();
            onHomeActivity();
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
        Toast.makeText(this, "Sorry, Without READ_PHONE_STATE Permission the application security can easily be compromised", Toast.LENGTH_LONG).show();
        onHomeActivity();
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
        Toast.makeText(this, "Sorry, Without READ_PHONE_STATE Permission the application security can easily be compromised", Toast.LENGTH_LONG).show();
        onHomeActivity();
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
        Toast.makeText(this, "Sorry, Without READ_CONTACTS Permission the application workflow can be easily compromised", Toast.LENGTH_LONG).show();
        onHomeActivity();
    }

    //optional
    @AskGranted(Manifest.permission.RECEIVE_SMS)
    public void receiveMessageAllowed() {
        Log.i(tag, "RECEIVE SMS GRANTED");
    }

    //optional
    @AskDenied(Manifest.permission.RECEIVE_SMS)
    public void receiveMessageDenied() {
        Log.i(tag, "RECEIVE SMS DENIED");
        Toast.makeText(this, "Sorry, Without This Permission the application workflow can be easily compromised", Toast.LENGTH_LONG).show();
        //onHomeActivity();
    }

    //optional
    @AskGranted(Manifest.permission.SEND_SMS)
    public void sendMessageAllowed() {
        Log.i(tag, "SEND SMS GRANTED");
    }

    //optional
    @AskDenied(Manifest.permission.SEND_SMS)
    public void sendMessageDenied() {
        Log.i(tag, "SEND SMS DENIED");
        Toast.makeText(this, "Sorry, Without SEND_SMS Permission the application workflow can be easily compromised", Toast.LENGTH_LONG).show();
        //onHomeActivity();
    }

    @Override
    public void onBackPressed(){
        try{

            if (fragmentManager.getBackStackEntryCount() == 2){
                onHomeActivity();
            }
            else {
                super.onBackPressed();
            }

        }catch (Exception e){
            Log.e(tag, Log.getStackTraceString(e));
            onHomeActivity();
        }

        currentFrag();
    }

    private void currentFrag() {
        try{

            Fragment currentFrag=fragmentManager.findFragmentById(R.id.salesFrame);
            if(currentFrag.getClass().getSimpleName().equals(SingleSell.class.getSimpleName())){
                onTabChanged(R.id.mainTabGroup, SingleSell.class.getSimpleName());
            }

            if(currentFrag.getClass().getSimpleName().equals(MultipleSell.class.getSimpleName())){
                onTabChanged(R.id.mainTabGroup, MultipleSell.class.getSimpleName());
            }


            if(currentFrag.getClass().getSimpleName().equals(Favorite.class.getSimpleName())){
                onTabChanged(R.id.mainTabGroup, Favorite.class.getSimpleName());
            }

            if(currentFrag.getClass().getSimpleName().equals(RechargeWallet.class.getSimpleName())){
                onTabChanged(R.id.walletTabGroup, RechargeWallet.class.getSimpleName());
            }
            if (currentFrag.getClass().getSimpleName().equals(WalletHistory.class.getSimpleName())) {
                onTabChanged(R.id.walletTabGroup, WalletHistory.class.getSimpleName());
            }
        }catch (Exception e){
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

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            View drawerView = findViewById(R.id.navigation_view); // child drawer view
            if (!drawerLayout.isDrawerOpen(drawerView)) {
                drawerLayout.openDrawer(drawerView);
            } else if (drawerLayout.isDrawerOpen(drawerView)) {
                drawerLayout.closeDrawer(drawerView);
            }
            return true;
        }

        if(keyCode == KeyEvent.KEYCODE_BACK){
            View drawerView = findViewById(R.id.navigation_view); // child drawer view
            if (drawerLayout.isDrawerOpen(drawerView)) {
                drawerLayout.closeDrawer(drawerView);
                return false;
            }
        }

        return super.onKeyDown( keyCode, event );
    }

    private void onTabChanged(int tabLabel, String currentTab){
        if(tabLabel == R.id.mainTabGroup){
            //check the tab holder
            try{

                mainTabHolder.removeAllViews();
                mainTabHolder.addView(accountTab);

                TextView sendOne = (TextView) findViewById(R.id.sendOne);
                sendOne.setTypeface(font, Typeface.BOLD);
                TextView sendMany = (TextView) findViewById(R.id.sendMany);
                sendMany.setTypeface(font, Typeface.BOLD);
                TextView favorites = (TextView) findViewById(R.id.favorites);
                favorites.setTypeface(font, Typeface.BOLD);

                if (SingleSell.class.getSimpleName().equals(currentTab)) {

                    //Single sell is active
                    sendOne.setBackgroundResource(R.drawable.buttonorange);
                    sendOne.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.appWhite));

                    //mark others as no active
                    sendMany.setBackgroundResource(R.drawable.border_orange);
                    sendMany.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.appBlue));

                    favorites.setBackgroundResource(R.drawable.border_orange);
                    favorites.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.appBlue));
                } else if (MultipleSell.class.getSimpleName().equals(currentTab)) {

                    //Multiple sell is active
                    sendMany.setBackgroundResource(R.drawable.buttonorange);
                    sendMany.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.appWhite));

                    //mark others as no active
                    sendOne.setBackgroundResource(R.drawable.border_orange);
                    sendOne.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.appBlue));

                    favorites.setBackgroundResource(R.drawable.border_orange);
                    favorites.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.appBlue));
                } else if (Favorite.class.getSimpleName().equals(currentTab)) {

                    //Multiple sell is active
                    favorites.setBackgroundResource(R.drawable.buttonorange);
                    favorites.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.appWhite));

                    //mark others as no active
                    sendOne.setBackgroundResource(R.drawable.border_orange);
                    sendOne.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.appBlue));

                    sendMany.setBackgroundResource(R.drawable.border_orange);
                    sendMany.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.appBlue));
                }

//                if(mainTabHolder.getChildCount() > 0){
//                    if(mainTabHolder.getChildAt(0).getId()!= R.id.accountTabs){
//                        mainTabHolder.removeAllViews();
//                        mainTabHolder.addView(accountTab);
//                    }
//                }else{
//                    mainTabHolder.removeAllViews();
//                    mainTabHolder.addView(accountTab);
//                }
            } catch (Exception e) {
                Log.e(tag, " " + e.getMessage());
                onHomeActivity();
            }

        }else if(tabLabel == R.id.walletTabGroup){
            //check the tab holder
            try{
                mainTabHolder.removeAllViews();
                mainTabHolder.addView(walletTab);

                TextView addAirtime = (TextView) findViewById(R.id.addAirtime);
                addAirtime.setTypeface(font);
                TextView viewHistory = (TextView) findViewById(R.id.viewHistory);
                viewHistory.setTypeface(font);

                if (RechargeWallet.class.getSimpleName().equals(currentTab)) {

                    //Recharge wallet is active
                    addAirtime.setBackgroundResource(R.drawable.buttonorange);
                    addAirtime.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.appWhite));

                    //mark others as no active
                    viewHistory.setBackgroundResource(R.drawable.border_orange);
                    viewHistory.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.appBlue));
                } else if (WalletHistory.class.getSimpleName().equals(currentTab)) {

                    //Recharge wallet is active
                    viewHistory.setBackgroundResource(R.drawable.buttonorange);
                    viewHistory.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.appWhite));

                    //mark others as no active
                    addAirtime.setBackgroundResource(R.drawable.border_orange);
                    addAirtime.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.appBlue));
                }
//                if(mainTabHolder.getChildCount() > 0){
//                    if(mainTabHolder.getChildAt(0).getId()!= R.id.walletTabs){
//                        mainTabHolder.removeAllViews();
//                        mainTabHolder.addView(walletTab);
//                    }
//                }else{
//                    mainTabHolder.removeAllViews();
//                    mainTabHolder.addView(walletTab);
//                }
            } catch (Exception e) {
                Log.e(tag, " " + e.getMessage());
                onHomeActivity();
            }


        }
    }

    //_________Fragments manager___________\\
    private void fragmentHandler(Object object, int fragId){
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

    private void singleTopUpFrag(){
        onTabChanged(R.id.mainTabGroup, SingleSell.class.getSimpleName());
        SingleSell singleSell=new SingleSell();
        singleSell.setArguments(setArgs());
        fragmentHandler(singleSell, R.id.salesFrame);
    }

    private void multipleTopUpFrag(){
        onTabChanged(R.id.mainTabGroup, MultipleSell.class.getSimpleName());
        MultipleSell multipleSell=new MultipleSell();
        multipleSell.setArguments(setArgs());
        fragmentHandler(multipleSell, R.id.salesFrame);

    }

    private void favoriteFrag(){
        onTabChanged(R.id.mainTabGroup, Favorite.class.getSimpleName());
        Favorite favorite=new Favorite();
        favorite.setArguments(setArgs());
        fragmentHandler(favorite, R.id.salesFrame);
    }

    private void walletTopUpFrag(){
        onTabChanged(R.id.walletTabGroup, RechargeWallet.class.getSimpleName());
        RechargeWallet rechargeWallet=new RechargeWallet();
        rechargeWallet.setArguments(setArgs());
        fragmentHandler(rechargeWallet, R.id.salesFrame);
    }

    private void checkBalanceFrag(){
        CheckBalance checkBalance=new CheckBalance();
        checkBalance.setArguments(setArgs());
        fragmentHandler(checkBalance,R.id.checkBalanceFrame);
    }

    private void refreshBalance() {
        Fragment frg = null;
        frg = getSupportFragmentManager().findFragmentByTag(CheckBalance.class.getSimpleName());
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }

    private void walletHistoryFrag() {
        onTabChanged(R.id.walletTabGroup, WalletHistory.class.getSimpleName());
        WalletHistory walletHistory = new WalletHistory();
        walletHistory.setArguments(setArgs());
        fragmentHandler(walletHistory, R.id.salesFrame);
    }

    private void transactionHistoryFrag(){

    }

    private Bundle setArgs(){
        try {
            Bundle bundle = new Bundle();
            bundle.putString("token", mSession.getToken());
            bundle.putString("msisdn", mSession.getMsisdn());

            int i = 0;
            int b = 1 + i;
            return bundle;
        } catch (Exception e) {
            e.printStackTrace();
            onHomeActivity();
            return null;
        }
    }

    //__________Menu Item Click handling_____________\\
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        MenuItem item = menu.findItem(R.id.mainnotifications);
        MenuItemCompat.setActionView(item, R.layout.mainmenucounter);
        LinearLayout notifCount = (LinearLayout) MenuItemCompat.getActionView(item);

        mainBadge = (TextView) notifCount.findViewById(R.id.actionbar_notifcation_main);
        ImageView bell=(ImageView) notifCount.findViewById(R.id.notificationBell);
        bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuNotifications();
            }
        });
        counter(mainBadge);
        return true;
    }

    public boolean menuSelected(int menuId){
        //Check to see which item was being clicked and perform appropriate action
        switch (menuId){
            case R.id.account:
                menuAccount();
                return true;
            case R.id.transactionsHistory:
                menuTransaction();
                return true;
            case R.id.notifications:
                menuNotifications();
                return true;
            case R.id.help:
                menuHelp();
                return true;
            case R.id.invite:
                menuInvite();
                return true;
            case R.id.about:
                menuAbout();
                return true;
            case R.id.logout:
                menuLogout();
                return true;
            default:
                Toast.makeText(getApplicationContext(),"Something is Wrong", Toast.LENGTH_SHORT).show();
                return true;
        }
    }

    private void menuAccount(){
        singleTopUpFrag();
    }

    private void menuTransaction(){
        Intent intent = new Intent(this, Extra.class);
        intent.putExtras(extraBundle(new TransactionHistory()));
        startActivity(intent);
    }

    private void menuHelp(){
        showDialog("HELP", BaseUrl.helpUrl);
    }

    private void menuInvite(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.invitationTitle) + getResources().getString(R.string.invitationMessage) + getResources().getString(R.string.downloadUrl));
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.sendTo)));
        Toast.makeText(getApplicationContext(),"Inviting Friends",Toast.LENGTH_LONG).show();
    }

    private void menuAbout(){
        Intent intent=new Intent(this, Extra.class);
        intent.putExtras(extraBundle(new About()));
        startActivity(intent);
    }

    private void menuLogout(){
        mSession=null;
        if(getIntent().getExtras() != null){
            setIntent(null);
        }
        onHomeActivity();
    }

    private void menuNotifications(){
        mainBadge.setVisibility(View.INVISIBLE);
        sideBadge.setVisibility(View.INVISIBLE);

        Intent intent=new Intent(this, Extra.class);
        intent.putExtras(extraBundle(new Notifications()));
        startActivity(intent);
    }

    private Bundle extraBundle(Object object) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString("msisdn", mSession.getMsisdn());
            bundle.putString("token", mSession.getToken());
            bundle.putString("userName", mSession.getUserName());
            bundle.putString("what", object.getClass().getSimpleName());

            return bundle;
        } catch (Exception e) {
            e.printStackTrace();
            onHomeActivity();
            return null;
        }
    }
    private void counter(TextView tv){
        tv.setText("12");
    }

    private void onHomeActivity(){
        Intent intent=new Intent(this, Home.class);
        intent.setFlags(IntentCompat.FLAG_ACTIVITY_TASK_ON_HOME | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        this.finish();
        startActivity(intent);
    }

    private void showDialog(String mTitle, String url) {
        TextView close;
        TextView title;
        WebView mWeb;

        final Dialog dialog = new Dialog(UserHome.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.web_dialog);

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

        mWeb.getSettings().setJavaScriptEnabled(true);
        mWeb.loadUrl(url);

        dialog.show();
    }

    @Override
    public void onWalletHistoryInteraction(int statusCode, String message, Object object) {

    }
}
