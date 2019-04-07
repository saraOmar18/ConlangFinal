package com.example.alhanoufaldawood.conlang;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.example.alhanoufaldawood.conlang.Customer.Translator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Rate extends AppCompatActivity {



        Button send;
        String orderId;
        String transId;
        String totalRate;
        String numOfRating;
        String rating;
        double ratingStr;
        double r;
        double totald;
        double numd;
        double numOfRatingd;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_rate);
            final RatingBar ratingRatingBar = (RatingBar) findViewById(R.id.ratingBar);
            send = (Button) findViewById(R.id.button);
            final TextView ratingDisplayTextView = (TextView) findViewById(R.id.rating_display_text_View);

            final Intent intent = getIntent();
            orderId = intent.getStringExtra("orderId");

            final DatabaseReference mref = FirebaseDatabase.getInstance().getReference("Orders").child(orderId);
            mref.child("translatorID").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    transId = snapshot.getValue(String.class);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            //to get translator
            final DatabaseReference transRef = FirebaseDatabase.getInstance().getReference("Translators").child(transId);

           transRef.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                   Translator trans = dataSnapshot.getValue(Translator.class);
                   totalRate = trans.getAvrRate();
                   numOfRating = trans.getNumOfRate();
                   //Will be used in method calculateAvr.
               }



               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           } );
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    r=ratingRatingBar.getRating();
                    calculateAvr();

                    //Add to DB.
                    transRef.child("AvrRate").setValue(totalRate);
                    transRef.child("numOfRate").setValue(numOfRating);
                    transRef.child("rate").setValue(rating);

                    retingDialog();
                    //ratingDisplayTextView.setText("Your rating is: " + rating);
                }
            });



        }


        public void calculateAvr() {
            //convert string to double.
            totald = Double.parseDouble(totalRate);
            numd = Double.parseDouble(numOfRating);
            numOfRatingd = numd + 1;

            //calculate the avr.
            totald += r;
            ratingStr = (totald / numOfRatingd);

            //convert double to string.
            totalRate = String.valueOf(totald);
            numOfRating = String.valueOf(numd);
            rating = String.valueOf(ratingStr);


        }

        public void retingDialog(){
            AlertDialog.Builder dialog = new AlertDialog.Builder(Rate.this);
            dialog.setCancelable(false);
            dialog.setTitle("Thank you");
            dialog.setMessage("Your rate sent successfully.." );
            dialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //Action for "Delete".
                }
            });


            final AlertDialog alert = dialog.create();
            alert.show();

} }
