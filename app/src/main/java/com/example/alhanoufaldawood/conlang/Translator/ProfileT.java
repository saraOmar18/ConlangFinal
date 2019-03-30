package com.example.alhanoufaldawood.conlang.Translator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alhanoufaldawood.conlang.Customer.Profile;
import com.example.alhanoufaldawood.conlang.Customer.UserProfile;
import com.example.alhanoufaldawood.conlang.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileT extends AppCompatActivity {
    private EditText profileName, profileEmail,profileField,language,providedLanguage,bio;
    private Button btnEdit;
    FirebaseAuth firebaseAuth;
    String Id;
    DatabaseReference databaseref;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_t);
        context = this;
        Intent intent;


        firebaseAuth = FirebaseAuth.getInstance();
        databaseref = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = user.getUid();//easy to refer


        databaseref.child("Translators").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                String fname = dataSnapshot.child("name").getValue(String.class);
                String cemail = dataSnapshot.child("email").getValue(String.class);
                String field = dataSnapshot.child("field").getValue(String.class);
                String lang = dataSnapshot.child("language").getValue(String.class);
                String plang = dataSnapshot.child("providedLanguage").getValue(String.class);
                String profilebio = dataSnapshot.child("bio").getValue(String.class);

                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);

                profileName = (EditText) findViewById(R.id.NameT);
                profileName.setText(fname);

                profileEmail = (EditText) findViewById(R.id.Email);
                profileEmail.setText(cemail);

                profileField = (EditText) findViewById(R.id.field);
                profileField.setText(field);

                language= (EditText)findViewById(R.id.language);
                language.setText(lang);

                providedLanguage= (EditText)findViewById(R.id.planguage);
                providedLanguage.setText(plang);


                bio= (EditText)findViewById(R.id.bio);
                bio.setText(profilebio);


                btnEdit = (Button) findViewById(R.id.Edit);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileT.this, "Please make sure you are connected to Internet", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void chickInfo(View view) {


        final String fnameP = profileName.getText().toString().trim();
        final String emaill = profileEmail.getText().toString().trim();
        final String ff = profileField.getText().toString().trim();
        final String lan = language.getText().toString().trim();
        final String plan = providedLanguage.getText().toString().trim();
        final String bb = bio.getText().toString().trim();





        FirebaseUser customer = FirebaseAuth.getInstance().getCurrentUser();
        String idTrans = customer.getUid();
        if (!TextUtils.isEmpty(ff) && !TextUtils.isEmpty(fnameP) && !TextUtils.isEmpty(emaill) && !TextUtils.isEmpty(lan) && !TextUtils.isEmpty(plan) && !TextUtils.isEmpty(bb)) {
            DatabaseReference owner = FirebaseDatabase.getInstance().getReference("Translators").child(idTrans);


            owner.child("name").setValue(fnameP);
            owner.child("field").setValue(ff);
            owner.child("email").setValue(emaill);
            owner.child("language").setValue(lan);
            owner.child("providedLanguage").setValue(plan);
            owner.child("bio").setValue(bb);



            Toast.makeText(ProfileT.this, "Saved Changes", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(ProfileT.this, "Please Fill All The Required Field", Toast.LENGTH_SHORT).show();
    };


}
