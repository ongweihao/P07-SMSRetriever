package com.example.a15039840.p07_smsretriever;


import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSecond extends Fragment {
    TextView tvDisplay;
    Button btnRetrieve;
    EditText et;

    public FragmentSecond() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        tvDisplay = (TextView) view.findViewById(R.id.textViewDisplayFrag2);
        btnRetrieve = (Button) view.findViewById(R.id.btnRetrieveFrag2);
        et = (EditText) view.findViewById(R.id.editTextNumberFrag2);
        btnRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] args = et.getText().toString().split(" ");
                int permissionCheck = PermissionChecker.checkSelfPermission
                        (getContext(), Manifest.permission.READ_SMS);

                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_SMS}, 0);
                    // stops the action from proceeding further as permission not
                    //  granted yet
                    return;
                }

                // Create all messages URI
                Uri uri = Uri.parse("content://sms");

                // The columns we want
                //  date is when the message took place
                //  address is the number of the other party
                //  body is the message content
                //  type 1 is received, type 2 sent
                String[] reqCols = new String[]{"date", "address", "body", "type"};

                String conditions = "BODY LIKE ? ";
                for(int i = 0; i < args.length;i++) {
                    args[i] = "%"+args[i]+"%";
                    if(i!=0) {
                        conditions += ("OR BODY LIKE ? ");
                    }
                }

//                // The filter String
//                String filter="body LIKE ?";
//                // The matches for the ?
//                String[] filterArgs = {"%"+ et.getText().toString() +"%"};
//
//                // Get Content Resolver object from which to
                //  query the content provider
                ContentResolver cr = getActivity().getContentResolver();
                // Fetch SMS Message from Built-in Content Provider
                Cursor cursor = cr.query(uri, reqCols, conditions, args, null);
                String smsBody = "";
                if (cursor.moveToFirst()) {
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat
                                .format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox:";
                        } else {
                            type = "Sent:";
                        }
                        smsBody += type + " " + address + "\n at " + date
                                + "\n\"" + body + "\"\n\n";
                    } while (cursor.moveToNext());
                }
                tvDisplay.setText(smsBody);
            }
        });
        return view;
    }

}
