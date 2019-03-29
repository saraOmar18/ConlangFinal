package com.example.alhanoufaldawood.conlang.Customer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.alhanoufaldawood.conlang.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TranslatorsListFragment extends Fragment {

    //public DatabaseReference database;
    ListView listViewTranslator;
    DatabaseReference ref;
    public static  String translatorId="";


    List<Translator> translatorsList;

    Activity context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.translators_list_fragment, container, false);

        //database = FirebaseDatabase.getInstance().getReference("Hello");
        //database.setValue("Hello world");
        translatorsList = new ArrayList<>();
        listViewTranslator = (ListView) myFragmentView.findViewById(R.id.translator_listview);

        context = getActivity();

        ref = FirebaseDatabase.getInstance().getReference("Translators");
      //final Translator test = new Translator("Najla",5,"English","Medical","Najla is student in king saud university bla bla bla bla","Translation ","French","true");
      //ref.push().setValue(test);

       // Translator test1 = new Translator("Sara Aldawood",1,"English","IT");
       // ref.push().setValue(test1);

        listViewTranslator.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final Translator translator = translatorsList.get(position);

                ref.orderByChild("name").equalTo(translator.getName()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot childSnapShot : dataSnapshot.getChildren()) {
                            translatorId = childSnapShot.getKey();


                            Intent intent = new Intent(getActivity(), TranslatorProfilePage.class);
                            intent.putExtra("translatorId", translatorId);
                            startActivity(intent);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
            }
        });


        myFragmentView.setMinimumWidth(300);
        myFragmentView.setMinimumHeight(88);

        return myFragmentView;
    } // End onCreateView



    public void onResume(){
        super.onResume();
        // Set title bar
        ((CustomerHome) getActivity()).setActionBarTitle("Translators List");

    } // End onResume

    @Override
    public void onStart() {
        super.onStart();


        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                translatorsList.clear();

                for (DataSnapshot childSnapShot :dataSnapshot.getChildren()){

                    Translator translator=childSnapShot.getValue(Translator.class);

                    String edit = translator.getedit().toString();

                    if (edit.equals("true") ) {
                        translatorsList.add(translator);
                    }
                }

                TranslatorListAdapter adapter = new TranslatorListAdapter(context , translatorsList);

                listViewTranslator.setAdapter(adapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


} // End class
