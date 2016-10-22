package utilities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.oltranz.mobilea.mobilea.R;

public class ContactsPickerActivity extends AppCompatActivity {

    private Typeface font;
    private ListView contactsChooser;
    private Button btnDone;
    private EditText txtFilter;
    private TextView txtLoadInfo;
    private ContactsListAdapter contactsListAdapter;
    private ContactsLoader contactsLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.activity_contacts_picker);

        font = Typeface.createFromAsset(this.getAssets(), "font/ubuntu.ttf");

        contactsChooser = (ListView) findViewById(R.id.lst_contacts_chooser);
        btnDone = (Button) findViewById(R.id.btn_done);
        btnDone.setTypeface(font, Typeface.BOLD);
        txtFilter = (EditText) findViewById(R.id.txt_filter);
        txtFilter.setTypeface(font);
        txtLoadInfo = (TextView) findViewById(R.id.txt_load_progress);
        txtLoadInfo.setTypeface(font);


        contactsListAdapter = new ContactsListAdapter(this,new ContactsList());

        contactsChooser.setAdapter(contactsListAdapter);


        loadContacts("");



        txtFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                contactsListAdapter.filter(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(contactsListAdapter.selectedContactsList.contactArrayList.isEmpty()){
                    setResult(RESULT_CANCELED);
                }else if(contactsListAdapter.selectedContactsList.contactArrayList.size()>3){
                    setResult(500);
                } else{

                    Intent resultIntent = new Intent();

                    resultIntent.putParcelableArrayListExtra("SelectedContacts", contactsListAdapter.selectedContactsList.contactArrayList);
                    setResult(RESULT_OK,resultIntent);

                }
                finish();

            }
        });
    }

    private void loadContacts(String filter){

        if(contactsLoader!=null && contactsLoader.getStatus()!= AsyncTask.Status.FINISHED){
            try{
                contactsLoader.cancel(true);
            }catch (Exception e){

            }
        }
        if(filter==null) filter="";

        try{
            //Running AsyncLoader with adapter and  filter
            contactsLoader = new ContactsLoader(this,contactsListAdapter);
            contactsLoader.txtProgress = txtLoadInfo;
            contactsLoader.execute(filter);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
