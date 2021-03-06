package com.example.alhanoufaldawood.conlang.Customer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alhanoufaldawood.conlang.R;
import com.google.android.gms.common.util.NumberUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.common.StringUtils;


public class Profile extends AppCompatActivity {

    private EditText profileName,profilePhone;
    private Button btnEdit;
    FirebaseAuth firebaseAuth;
    String Id;
    DatabaseReference databaseref;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        context = this;
        Intent intent;


        firebaseAuth = FirebaseAuth.getInstance();
        databaseref = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = user.getUid();//easy to refer


        databaseref.child("Customers").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                String fname = dataSnapshot.child("name").getValue(String.class);
                String cphone = dataSnapshot.child("phone").getValue(String.class);

                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);

                profileName = (EditText) findViewById(R.id.Name);
                profileName.setText(fname);



                profilePhone = (EditText) findViewById(R.id.Phone);
                profilePhone.setText(cphone);

                btnEdit = (Button) findViewById(R.id.Edit);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Profile.this, "Please make sure you are connected to Internet", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void chickInfo(View view) {


        final String phoneN =""+ profilePhone.getText().toString().trim();

        final String fnameP = profileName.getText().toString().trim();
         char mychar;
         String theString="";
         for(int i=0;i<phoneN.length();i++){
          mychar=phoneN.charAt(i);
          if(!(NumberUtils.isNumeric(mychar+""))){
             theString=phoneN.substring(0,i-1);
          }
         }


        FirebaseUser customer = FirebaseAuth.getInstance().getCurrentUser();
        String idCustomer = customer.getUid();



       if (!TextUtils.isEmpty(theString) && !TextUtils.isEmpty(fnameP) ) {

           if (theString.length() > 10 || theString.length() < 10) {
               profilePhone.setError(" Incorrect Phone Number ");
               profilePhone.requestFocus();
               return;}

            DatabaseReference owner = FirebaseDatabase.getInstance().getReference("Customers").child(idCustomer);

            owner.child("name").setValue(fnameP);
            owner.child("phone").setValue(phoneN);


            Toast.makeText(Profile.this, "Saved Changes", Toast.LENGTH_SHORT).show();
        }




            else
               Toast.makeText(Profile.this, "Please Fill All The Required Field", Toast.LENGTH_SHORT).show();




    };



    }






