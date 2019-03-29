/*
 * done by Asmaa Mostafa
 * res https://www.youtube.com/watch?v=h62bcMwahTU
 * */
package com.example.alhanoufaldawood.conlang.Translator;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alhanoufaldawood.conlang.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class EditInformation extends AppCompatActivity implements MultiSpinner.MultiSpinnerListener {

    private Button upload, edit;
    private EditText bio ;
    private MultiSpinner from , to , field ;
    private ImageView image;
    private ProgressBar mProgressBar;

    private Uri filepath;
    private final int PICK_IMAGE_REQUEST=1;
    private String fromLang ,toLang , fieldLang , bioTran;
    private String uid;
    // firebase

    FirebaseStorage storage ;
    StorageReference mStorageRef;
    DatabaseReference mDatabaseRefTranslator;
    DatabaseReference mDatabaseRefProvided;
    StorageTask mUploadTask;
    FirebaseDatabase firebaseDatabase;
    List<String> listLanguage = new ArrayList<String>();
    List<String> listField = new ArrayList<String>();
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_information_activity);
        initElement();
    }

    private void initElement() {
        // firebase
        storage =FirebaseStorage.getInstance();
        FirebaseUser currentUser  = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference("image");
        mDatabaseRefTranslator = FirebaseDatabase.getInstance().getReference("Translators");
        // button
        upload=(Button)findViewById(R.id.upload);
        edit=(Button)findViewById(R.id.edit);
        // text
        bio=findViewById(R.id.bio);
        //image
        image=(ImageView)findViewById(R.id.imag);
        //spinner
        from=(MultiSpinner)findViewById(R.id.from);
        to=(MultiSpinner)findViewById(R.id.to);
        field=(MultiSpinner)findViewById(R.id.field);
        mProgressBar = findViewById(R.id.progress_bar);
        //
        listLanguage.add("ARABIC");
        listLanguage.add("ENGLISH");
        listLanguage.add("CHINESE");
        listLanguage.add("SPANISH");
        listLanguage.add("HINDI");
        listLanguage.add("PORTUGUESE");
        listLanguage.add("BENGALI");
        listLanguage.add("RUSSIAN");

        //
        listField.add("Political");
        listField.add("Commercial");
        listField.add("Legal");
        listField.add("Religious");
        listField.add("Medical");
        listField.add("Technology");
        listField.add("Literature");
        listField.add("History");
        listField.add("Academic");



        // fill out the adapter
        spinnerAdapter();
        //choose from the adapter
        from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromLang=from.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)  {
                toLang=to.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        field.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fieldLang=field.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bioTran=bio.getText().toString();
        // for upload click listener
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        // for edit click listener
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(EditInformation.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImage(filepath);
                }
            }}
        );

    }

    private boolean isValidFrom(String fromLang) {
        if(fromLang.equalsIgnoreCase("From Language*")||fromLang.trim().equals("")){
            ((TextView)from.getSelectedView()).setError("Please choose provided language");
            return false ;
        }
        return true;
    }
    private boolean isValidFilePath(Uri filepath) {
        if(filepath==null){
            upload.setError("You must upload a picture");
            return false;
        }
        return true;
    }
    private boolean isValidfield(String fieldLang) {
        if(fieldLang.trim().equals("")||fieldLang.equalsIgnoreCase("Field*")){
            ((TextView)field.getSelectedView()).setError("Please choose your field");
            return false;}
        return true;
    }
    private boolean isValidTo(String toa){
        if(fromLang.trim().equals("")||toa.equalsIgnoreCase("To Language*")){
            ((TextView)to.getSelectedView()).setError("Please choose provided language");
            return false ;}
        return true;
    }
    private void makeToast(String no_condition) {
        Toast.makeText(this,no_condition,Toast.LENGTH_LONG);
    }

    private void spinnerAdapter() {
        field.setItems(listField,"Field*",this);
        from.setItems(listLanguage ,"From Language*",this);
        to.setItems(listLanguage ,"To Language*",this);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filepath = data.getData();

            Picasso.get().load(filepath).into(image);
        }
    }
    private  String getFileExtention(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(filepath));
    }
    private void uploadImage(Uri uri){
        if(isValidfield(fieldLang) && isValidFrom(fromLang)&& isValidTo(toLang) && isValidFilePath(filepath)){
            StorageReference fileReference = mStorageRef.child(uid);
            mUploadTask = fileReference.putFile(filepath)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            String fileUri = task.getResult().getUploadSessionUri().toString();
                            mDatabaseRefTranslator.child(uid).child("bio").setValue(bioTran);
                            mDatabaseRefTranslator.child(uid).child("edit").setValue("true");
                            mDatabaseRefTranslator.child(uid).child("field").setValue(fieldLang);
                            mDatabaseRefTranslator.child(uid).child("language").setValue(fromLang);
                            mDatabaseRefTranslator.child(uid).child("providedLanguage").setValue(toLang);
                            mDatabaseRefTranslator.child(uid).child("image").setValue(fileUri);
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //String fileUri = taskSnapshot.getResult().getUploadSessionUri().toString();
                            //

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(EditInformation.this, "Upload successful", Toast.LENGTH_LONG).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditInformation.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        }
        else{
            makeToast("no condition");
        }

    }



    @Override
    public void onItemsSelected(boolean[] selected) {

    }
}

