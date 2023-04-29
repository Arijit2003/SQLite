package com.example.onlinedatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.onlinedatabase.data.MyDBHandler;
import com.example.onlinedatabase.model.CONTACT;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyDBHandler dbHandler = new MyDBHandler(MainActivity.this);
        CONTACT arijit=new CONTACT("ArijitModak","6295401465");
        dbHandler.addContact(arijit);

        CONTACT braj=new CONTACT("BrajKishorSharma","7895647026");
        dbHandler.addContact(braj);

        CONTACT devashish=new CONTACT("DevashishDhaulakhandi","6985478722");
        dbHandler.addContact(arijit);

        List<CONTACT> allContacts=dbHandler.getAllContacts();
        for(CONTACT contact:allContacts){
            Log.d("ALL_TYPE_CONTACT",contact.getId()+" "+contact.getName()+" "+contact.getContact());
        }

    }
}