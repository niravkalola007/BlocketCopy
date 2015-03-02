package com.nkdroid.bemcycle.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nkdroid.bemcycle.R;
import com.nkdroid.bemcycle.model.AppConstants;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class RemoveActivity extends ActionBarActivity {
    private Toolbar toolbar;
    private TextView txtSubmit;
    private EditText etEmail;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private String object_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.mipmap.ic_launcher);
            toolbar.setTitle("REMOVE OR RENEW");
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        Intent intent=getIntent();
        object_id=intent.getStringExtra("object_id");
        etEmail= (EditText) findViewById(R.id.etEmail);
        txtSubmit= (TextView) findViewById(R.id.txtSubmit);
        radioGroup= (RadioGroup) findViewById(R.id.radioGroup);
        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isEmailMatch(etEmail)==false){
                    Toast.makeText(RemoveActivity.this,"Please Enter Valid Email",Toast.LENGTH_SHORT).show();

                } else {
                    // get selected radio button from radioGroup
                    int selectedId = radioGroup.getCheckedRadioButtonId();

                    // find the radiobutton by returned id
                    radioButton = (RadioButton) findViewById(selectedId);



                        final ProgressDialog progressDialogValue=new ProgressDialog(RemoveActivity.this);
                        progressDialogValue.setMessage("Uploading Data...");
                        progressDialogValue.setCancelable(false);
                        progressDialogValue.show();

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("general");


                        query.getInBackground(object_id, new GetCallback<ParseObject>() {
                            public void done(ParseObject object, ParseException e) {
                                if (e == null) {
                                    // Now let's update it with some new data. In this case, only cheatMode and score
                                    // will get sent to the Parse Cloud. playerName hasn't changed.
                                    if(radioButton.getText().equals("remove")) {
                                        object.put("status", AppConstants.NOT_APPROVED);
                                    } else {
                                        object.put("status", AppConstants.APPROVED);
                                        object.put("DateString", new SimpleDateFormat("dd-MM-yyyy").format(new Date())+"");
                                    }
                                    object.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException ex) {
                                            if (ex == null) {
                                                progressDialogValue.dismiss();

                                            } else {

                                                Toast.makeText(RemoveActivity.this,"Error while uploading, try again!"+ex,Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });




                }


            }
        });
    }


    public boolean isEmailMatch(EditText param1) {
        // boolean isMatch = false;
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(param1.getText().toString()).matches();
    }
}
