package com.example.alhanoufaldawood.conlang.Translator;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alhanoufaldawood.conlang.Customer.Offer;
import com.example.alhanoufaldawood.conlang.Customer.RequestService;
import com.example.alhanoufaldawood.conlang.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Objects;

public class sendCustomOffer extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "sendCustomOffer";

    EditText translationPrice;
    EditText translatorComment;
    Button send;
    FirebaseAuth mAuth;
    Offer offerInfo;
    static String requestID;
    String price;
    String offerComments;
    String deadline;
    String name;
    String to;
    private TextView mDisplayDate;
    private TextView mDisplayDate1;

    ImageView error;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    DatabaseReference mref;
    Activity context;
    int year;
    int month;
    int day;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_custom_offer);
        getSupportActionBar().setTitle("Send offer");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        send = findViewById(R.id.customSendToCustomer);

        translationPrice = findViewById(R.id.customPrice);
        translatorComment = findViewById(R.id.customOfferComment);

        mDisplayDate = (TextView) findViewById(R.id.customOfferData);
        mDisplayDate1 = (TextView) findViewById(R.id.customOfferData1);

        error = (ImageView) findViewById(R.id.imageView12);





        Objects.requireNonNull(getSupportActionBar()).setDefaultDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(Html.fromHtml("<font color='#707070'>"+"Back"+" </font>"));
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.signin_actionbar, null));



        mref = FirebaseDatabase.getInstance().getReference("Orders");
        //  offerRef = FirebaseDatabase.getInstance().getReference("Orders").child("Offers");

        // ref=FirebaseDatabase.getInstance().getReference("CustomRequests");

        context= this;
        findViewById(R.id.customSendToCustomer).setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();

        Intent i = getIntent();
        requestID = i.getStringExtra("key");

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        sendCustomOffer.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);

                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                deadline = month + "/" + day + "/" + year;
                mDisplayDate1.setText(deadline);
            }
        };


        mref.child(requestID).child("translatorName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                name = snapshot.getValue(String.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void sendingOffer() {

        price = translationPrice.getText().toString().trim();
        offerComments = translatorComment.getText().toString().trim();
        // ref.child(requestID).setValue(offerInfo);

      //  offerInfo = new Offer(deadline, price, offerComments,"","");

    }


    public void openDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(sendCustomOffer.this);
        dialog.setCancelable(false);
        dialog.setTitle("Dear "+name+" ..");
        dialog.setMessage(" Are you sure about sending this offer?" );


        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


               String offerkey = mref.child(requestID).child("Offers").push().getKey();
               // mref.child(requestID).child("Offers").child(offerkey).setValue(offerInfo);
                mref.child(requestID).child("Offers").child(offerkey).child("key").setValue(offerkey);
                mref.child(requestID).child("Offers").child(offerkey).child("translatorName").setValue(name);
                mref.child(requestID).child("Offers").child(offerkey).child("deadline").setValue(deadline);
                mref.child(requestID).child("Offers").child(offerkey).child("comment").setValue(offerComments);
                mref.child(requestID).child("Offers").child(offerkey).child("price").setValue(price);





                mref.child(requestID).child("translatorStatus").setValue("Offer sent to the customer");
                mref.child(requestID).child("status").setValue("New offer !");



                finish();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        final AlertDialog alert = dialog.create();
        alert.show();
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.customSendToCustomer:
                sendingOffer();
                if (price.isEmpty()){
                    error.setVisibility(View.VISIBLE);
                   // translationPrice.setError("Please you have to specify a price");
                    //translationPrice.requestFocus();
                    return;
                }

                if (month==0 && year==0 && day==0){
                    error.setVisibility(View.VISIBLE);
                   // mDisplayDate.setError("Please you have to specify a deadline");
                    ///mDisplayDate.requestFocus();
                    return;}

                openDialog();
                break;
            //ToDo.
            // Intent intent = new Intent(context, TranslatorHome.class);
            //startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


        translationPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!TextUtils.isEmpty(translationPrice.getText().toString()) && !mDisplayDate1.getText().toString().equals("No date selected")){
                    error.setVisibility(View.GONE);
                    send.setBackground(ContextCompat.getDrawable(sendCustomOffer.this, R.drawable.select_issue_btn_onclick));// set here your backgournd to button

                }else{
                    send.setBackground(ContextCompat.getDrawable(sendCustomOffer.this, R.drawable.select_issue_btn));// set here your backgournd to button

                }



            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDisplayDate1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!TextUtils.isEmpty(translationPrice.getText().toString()) && !mDisplayDate1.getText().toString().equals("No date selected")){
                    error.setVisibility(View.GONE);
                    send.setBackground(ContextCompat.getDrawable(sendCustomOffer.this, R.drawable.select_issue_btn_onclick));// set here your backgournd to button

                }else{
                    send.setBackground(ContextCompat.getDrawable(sendCustomOffer.this, R.drawable.select_issue_btn));// set here your backgournd to button

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }



}
