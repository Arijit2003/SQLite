package com.example.onlinedatabase.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.onlinedatabase.model.CONTACT;
import com.example.onlinedatabase.params.Params;

import java.util.ArrayList;
import java.util.List;

public class MyDBHandler extends SQLiteOpenHelper {
    public MyDBHandler(Context context) {
        super(context, Params.DB_NAME, null, Params.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE="CREATE TABLE "+Params.DB_TABLE+ "("+
                Params.ID+" INTEGER PRIMARY KEY,"+
                Params.NAME+" TEXT,"+
                Params.PHONE+" TEXT," +
                Params.IMAGE+" BLOB" +")";
        Log.d("OPEN_HELPER","SQL QUERY: "+CREATE_TABLE);
        sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public void addContact(CONTACT contact){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Params.NAME.trim(),contact.getName());
        values.put(Params.PHONE.trim(),contact.getContact());
        values.put(Params.IMAGE.trim(),contact.getByteArrayBlob());

        db.insert(Params.DB_TABLE,null,values);

        Log.d("DataAdded","Successfully Inserted");
        db.close();

    }
    public List<CONTACT> getAllContacts(){
        SQLiteDatabase db=this.getReadableDatabase();
        String QUERY="SELECT * FROM "+Params.DB_TABLE;
        Cursor cursor=db.rawQuery(QUERY,null);
        List<CONTACT> allContacts=new ArrayList<>();
        if(cursor.moveToFirst()){
            do {
                CONTACT contact = new CONTACT();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setContact(cursor.getString(2));
                contact.setByteArrayBlob(cursor.getBlob(3));
                allContacts.add(contact);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return allContacts;
    }

    public int updateContact(CONTACT contact){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Params.NAME,contact.getName());
        values.put(Params.PHONE,contact.getContact());
        values.put(Params.IMAGE,contact.getByteArrayBlob());
        return db.update(Params.DB_TABLE,values,Params.ID + "=?",
                new String[]{String.valueOf(contact.getId())});
    }
    public int deleteContact(int id){
        SQLiteDatabase db= this.getWritableDatabase();
        int deletedRows= db.delete(Params.DB_TABLE,Params.ID+"=?",new String[]{String.valueOf(id)});
        db.close();
        return deletedRows;
    }
}
