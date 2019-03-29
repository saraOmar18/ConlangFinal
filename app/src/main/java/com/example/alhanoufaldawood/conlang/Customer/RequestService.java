package com.example.alhanoufaldawood.conlang.Customer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RequestService extends AppCompatActivity implements Serializable {

    DatabaseReference ref;
    DatabaseReference ref2;


    public Spinner from;
    public Spinner to;
    public Spinner areaSpinner;
    public EditText comments;
    TextView selectedFile;
    public Button upload;
    public Button scan;
    public Button send;
    public static List<String> feild;
    public static List<String> language;
    public static List<String> providedLanguage;
    public ProgressDialog progressDialog;

    public String currentUser;

    public FirebaseUser user;

    static String  name ;

    static String translatorImage;



    public String translatorId;
    public String field;

    public Uri fileURL;

    public StorageReference storage;
    public DatabaseReference ServiceRequests;

    public String key;

    ImageView selec_file_error_message;


/////////// Scan Qr ////////////
private DatabaseReference db;
    private ProgressDialog progressDialog1;
    private FirebaseAuth firebaseAuth;
    private String ticketCode;




    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_service);

        /////////scan qr ///////////
        ticketCode=null;
        scan = (Button) findViewById(R.id.scan);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
        final Activity activity = this;
        progressDialog = new ProgressDialog(this);
        //////////// scan qr //////////////////


        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(Html.fromHtml("<font color='#707070'>"+"Back"+" </font>"));
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.signin_actionbar, null));

        selec_file_error_message = (ImageView) findViewById(R.id.imageView12);


        from = (Spinner) findViewById(R.id.spinnerFrom);
        to = (Spinner) findViewById(R.id.spinnerTo);
        comments = (EditText) findViewById(R.id.comment);
        upload = (Button) findViewById(R.id.upload);
        send = (Button) findViewById(R.id.request);
        selectedFile = (TextView) findViewById(R.id.fileselected);

        user = FirebaseAuth.getInstance().getCurrentUser();
        currentUser =user.getUid();

        Intent intent = getIntent();
        translatorId = intent.getStringExtra("translatorId");

        storage = FirebaseStorage.getInstance().getReference();
        ServiceRequests = FirebaseDatabase.getInstance().getReference("Orders");

        key = ServiceRequests.push().getKey();
        ref2 = FirebaseDatabase.getInstance().getReference("Customers").child(currentUser);



        ref = FirebaseDatabase.getInstance().getReference("Translators").child(translatorId);

        ref.child("field").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                  feild = new ArrayList<String>();
                  feild.add(dataSnapshot.getValue(String.class));



                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {

                    String areaName = dataSnapshot.getValue(String.class);
                    feild.add(areaName);
                }

                areaSpinner = (Spinner) findViewById(R.id.spinnerField);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(RequestService.this, android.R.layout.simple_list_item_1, feild);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                areaSpinner.setAdapter(areasAdapter);
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ref.child("language").addValueEventListener(new ValueEventListener() {

            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                language = new ArrayList<String>();
                language.add(dataSnapshot.getValue(String.class));



                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {

                    String areaName = dataSnapshot.getValue(String.class);
                    language.add(areaName);
                }

                from = (Spinner) findViewById(R.id.spinnerFrom);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(RequestService.this, android.R.layout.simple_list_item_1, language);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                from.setAdapter(areasAdapter);
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

        ref.child("providedLanguage").addValueEventListener(new ValueEventListener() {

            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                providedLanguage = new ArrayList<String>();
                providedLanguage.add(dataSnapshot.getValue(String.class));



                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {

                    String areaName = dataSnapshot.getValue(String.class);
                    providedLanguage.add(areaName);
                }

                to = (Spinner) findViewById(R.id.spinnerTo);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(RequestService.this, android.R.layout.simple_list_item_1, providedLanguage);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                to.setAdapter(areasAdapter);
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });



        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(RequestService.this , android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                    selectFile();
                }else {

                    ActivityCompat.requestPermissions(RequestService.this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},9);
                }
            }
        });


        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator= new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Go to Conlang.com and scan the QR code");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);// in orginall code false :)
                integrator.initiateScan();

            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (!(selectedFile.getText().equals("No file selected"))) {
                    uploadFile();
                }
                else{

                   selec_file_error_message.setVisibility(View.VISIBLE);

                   // Toast.makeText(RequestService.this,"Please select a file ", Toast.LENGTH_LONG).show();
                    //AlertDialog.Builder dialog = new AlertDialog.Builder(RequestService.this);
                    //dialog.setCancelable(false);
                    //dialog.setMessage("Please select a file!" );
                    //dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        //@Override
                       // public void onClick(DialogInterface dialog, int id) {
                            //Action for "Delete".
                        //}
                    //});


                    //final AlertDialog alert = dialog.create();
                    //alert.show();

                }
            }
        });

    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            selectFile();
        }else{
            Toast.makeText(RequestService.this,"Please provide us a permission",Toast.LENGTH_LONG);
        }
    }



    private void selectFile() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        //intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent ,"Select file"),86);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode ==86 && resultCode == RESULT_OK && data!= null && data.getData() != null){

            fileURL = data.getData();
            //selectedFile.setText(data.getData().getPath());
            Uri uri2 = data.getData();
            String uriString = uri2.toString();
            File myFile = new File(uriString);
            String path = myFile.getAbsolutePath();
            String displayName = null;

            if (uriString.startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = this.getContentResolver().query(uri2, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    cursor.close();
                }
            } else if (uriString.startsWith("file://")) {
                displayName = myFile.getName();
            }

            selectedFile.setText(displayName);

        }else {
            Toast.makeText(RequestService.this,"Please select file",Toast.LENGTH_LONG);
        }
    }


    private void uploadFile() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Sending Request...");
        progressDialog.setProgress(0);
        progressDialog.show();

        String filePath = System.currentTimeMillis()+"";

        final StorageReference riversRef = storage.child("NeedTranslationDocuments").child("Hello.."+filePath);

        riversRef.putFile(fileURL) .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String URL = uri.toString();
                        sendRequest(URL);

                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RequestService.this ,"File not successfully uploaded", Toast.LENGTH_LONG);


                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);
            }
        });

    }

    private void scanQR() {

        Intent i = new Intent(RequestService.this , ScanQR.class);
        startActivity(i);
    }

    private void sendRequest(final String url) {

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                translatorImage = dataSnapshot.child("image").getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String field = areaSpinner.getSelectedItem().toString().trim();
                String from1 = from.getSelectedItem().toString().trim();
                String to1 = to.getSelectedItem().toString().trim();
                String comment = comments.getText().toString().trim();
                int orderNo1 = (int) (Math.random() * 50000 + 1); // random number between 1-50
                String orderNo = Integer.toString(orderNo1);
                String date = "date";

                String name = dataSnapshot.child("name").getValue(String.class);





                ServiceRequest serviceRequest = new ServiceRequest(field, from1,to1, comment, url, translatorId, currentUser, "Sent...", orderNo, name, "","Custom",date,"New request","",translatorImage);
                ServiceRequests.child(key).setValue(serviceRequest);
                ServiceRequests.child(key).child("translatorStatus").setValue("New request");

                String orderDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                ServiceRequests.child(key).child("orderDate").setValue(orderDate);




                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String translatorName = dataSnapshot.child("name").getValue(String.class);
                        ServiceRequests.child(key).child("translatorName").setValue(translatorName);


                       Intent i = new Intent(RequestService.this, CustomerHome.class);
                       startActivity(i);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        }

    @Override
    protected void onStart() {
        super.onStart();

        if (!(selectedFile.getText().equals("No File Selected"))) {
            selec_file_error_message.setVisibility(View.GONE);
        }

        selectedFile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!(selectedFile.getText().equals("No file selected"))) {
                    send.setBackground(ContextCompat.getDrawable(RequestService.this, R.drawable.send_request_onclick));// set here your backgournd to button
                }else {
                    send.setBackground(ContextCompat.getDrawable(RequestService.this, R.drawable.send_request));// set here your backgournd to button
                }
            }
        });
    }
}

