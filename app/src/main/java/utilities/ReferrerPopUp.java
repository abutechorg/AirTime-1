package utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.oltranz.mobilea.mobilea.R;
import com.vistrav.ask.Ask;
import com.vistrav.ask.annotations.AskDenied;
import com.vistrav.ask.annotations.AskGranted;

/**
 * Created by Hp on 10/24/2016.
 */
public class ReferrerPopUp {
    private String tag = getClass().getSimpleName();
    private Activity context;
    private AlertDialog dialog;
    private Typeface font;
    static final int PICK_CONTACT_REQUEST = 1;
    private EditText refNumber;
    private TextView lbl1,lbl2;
    private ImageView browseTel;
    private View layout;

    public ReferrerPopUp(Activity context) {
        this.context = context;
    }

    public void setReferrer(){
        font = Typeface.createFromAsset(context.getAssets(), "font/ubuntu.ttf");
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.referrer_layout, (ViewGroup) context.findViewById(R.id.root));
        lbl1=(TextView) layout.findViewById(R.id.label);
        lbl1.setTypeface(font);

        lbl2=(TextView) layout.findViewById(R.id.lblHint);
        lbl2.setTypeface(font, Typeface.BOLD);

        refNumber=(EditText) layout.findViewById(R.id.tel);
        refNumber.setTypeface(font);

        browseTel=(ImageView) layout.findViewById(R.id.getNumber);
        browseTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(new IsGranted(context).checkReadContacts()) {
                    Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                    pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
                    context.startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
                }else{
                    Ask.on(context)
                            .forPermissions(Manifest.permission.READ_CONTACTS)
                            .withRationales("In Order to make your life easy for contact pick up application needs Read Contact permission") //optional
                            .go();
                }
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(R.string.notification);

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        dialog = builder.create();
        dialog.show();
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
        Toast.makeText(context, "Sorry, Without READ_CONTACTS Permission the application workflow can be easily compromised", Toast.LENGTH_LONG).show();
    }
}
