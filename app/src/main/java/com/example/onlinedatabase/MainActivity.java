package com.example.onlinedatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.example.onlinedatabase.data.MyAdapter;
import com.example.onlinedatabase.data.MyDBHandler;
import com.example.onlinedatabase.model.CONTACT;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static RecyclerView recyclerView;
    public static  ArrayList<CONTACT> contactArrayList;
    private FloatingActionButton addNewContactFAB;
    private MyDBHandler dbHandler;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView=findViewById(R.id.recyclerView);
        addNewContactFAB=findViewById(R.id.addNewContactFAB);

        dbHandler = new MyDBHandler(MainActivity.this);
//        CONTACT arijit=new CONTACT("ArijitModak","6295401465");
//        dbHandler.addContact(arijit);
//
//        CONTACT braj=new CONTACT("BrajKishorSharma","7895647026");
//        dbHandler.addContact(braj);
//
//        CONTACT devashish=new CONTACT("DevashishDhaulakhandi","6985478722");
//        dbHandler.addContact(devashish);
//
//        devashish.setId(15);
//        devashish.setName("Deva Dhalu");
//        devashish.setContact("2456186206");
//        int affectedRows=dbHandler.updateContact(devashish);
//        Log.d("Affected Rows",""+affectedRows);
//
//
//
//        List<CONTACT> allContacts=dbHandler.getAllContacts();
//        Log.d("SIZE_OF_CONTACTS",""+allContacts.size());
//        for(CONTACT contact:allContacts){
//            Log.d("ALL_TYPE_CONTACT",contact.getId()+" "+contact.getName()+" "+contact.getContact());
//        }
//
//        int dr=dbHandler.deleteContact(16);
//        Log.d("Deleted Rows",""+dr);
        addNewContactFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddContactDialog();
            }
        });



        contactArrayList = new ArrayList<>(dbHandler.getAllContacts());
        MyAdapter myAdapter = new MyAdapter(MainActivity.this,contactArrayList);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));






    }
    private void openAddContactDialog(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_contact);
        dialog.setCancelable(false);

        TextInputEditText contactNameADDCON=dialog.findViewById(R.id.contactNameADDCON);
        TextInputEditText contactNumberADDCON=dialog.findViewById(R.id.contactNumberADDCON);
        Button cancelButtonDialog=dialog.findViewById(R.id.cancelButtonDialog);
        Button okButtonDialog=dialog.findViewById(R.id.okButtonDialog);



        cancelButtonDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        okButtonDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CONTACT newContact=new CONTACT();

                newContact.setName(contactNameADDCON.getText().toString().trim());
                newContact.setContact(contactNumberADDCON.getText().toString().toString());

                dbHandler.addContact(newContact);
                newContact.setId(dbHandler.getAllContacts().get(dbHandler.getAllContacts().size()-1).getId());
                contactArrayList.add(newContact);
                dialog.dismiss();
                recyclerView.scrollToPosition(contactArrayList.size()-1);

            }
        });

        dialog.create();
        dialog.show();
    }
}