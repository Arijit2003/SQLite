package com.example.onlinedatabase;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Switch;

import com.example.onlinedatabase.data.MyAdapter;
import com.example.onlinedatabase.data.MyDBHandler;
import com.example.onlinedatabase.model.CONTACT;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    public static RecyclerView recyclerView;
    public static  ArrayList<CONTACT> contactArrayList;
    private FloatingActionButton addNewContactFAB;
    private MyDBHandler dbHandler;
    private Boolean permission=false;
    private MyAdapter myAdapter;
    private CircleImageView contactImage;
    private Bitmap bitmap;

    private byte[] byteArrayOut=null;

    private ActivityResultLauncher<String> phoneCallResultLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if(result){
                        permission=true;
                    }else{
                        showAlertDialog("Phone Call permission denied","Not able to call anyone");
                        permission=false;
                    }
                }
            });

    private ActivityResultLauncher<String> galleryResultLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if(result!=null){
                        contactImage.setImageURI(result);
                        byteArrayOut=encodeImageUri(result);
                    }
                }
            });




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phoneCallResultLauncher.launch(Manifest.permission.CALL_PHONE);
        recyclerView=findViewById(R.id.recyclerView);
        addNewContactFAB=findViewById(R.id.addNewContactFAB);

        dbHandler = new MyDBHandler(MainActivity.this);

        addNewContactFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddContactDialog();
            }
        });



        contactArrayList = new ArrayList<>(dbHandler.getAllContacts());
        myAdapter = new MyAdapter(MainActivity.this,contactArrayList);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                switch(direction){
                    case ItemTouchHelper.RIGHT:{
                        if(permission){
                            //viewHolder.setBackgroundColor(Color.parseColor("#000000"));
                            String phoneNo=dbHandler.getAllContacts().get(viewHolder.getAdapterPosition()).getContact();
                            Uri phone=Uri.parse("tel:"+phoneNo.trim());
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_CALL);
                            intent.setData(phone);
                            if(intent.resolveActivity(getPackageManager())!=null){
                                startActivity(intent);
                            }

                        }else{
                            phoneCallResultLauncher.launch(Manifest.permission.CALL_PHONE);
                        }
                        break;
                    }
                    case ItemTouchHelper.LEFT:{
                        String phoneNo=dbHandler.getAllContacts().get(viewHolder.getAdapterPosition()).getContact();
                        Uri uriMessage=Uri.parse("sms:"+phoneNo.trim());
                        String message="";
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SENDTO);
                        intent.setData(uriMessage);
                        intent.putExtra(Intent.EXTRA_TEXT,message);
                        if(intent.resolveActivity(getPackageManager())!=null){
                            startActivity(intent);
                        }
                    }
                }
                contactArrayList=new ArrayList<>(dbHandler.getAllContacts());
                myAdapter = new MyAdapter(MainActivity.this,contactArrayList);
                recyclerView.setAdapter(myAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);







    }
    private void openAddContactDialog(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_contact);
        dialog.setCancelable(false);

        TextInputEditText contactNameADDCON=dialog.findViewById(R.id.contactNameADDCON);
        TextInputEditText contactNumberADDCON=dialog.findViewById(R.id.contactNumberADDCON);
        Button cancelButtonDialog=dialog.findViewById(R.id.cancelButtonDialog);
        Button okButtonDialog=dialog.findViewById(R.id.okButtonDialog);
        contactImage = dialog.findViewById(R.id.contactImage);

        contactImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryResultLauncher.launch("image/*");
            }
        });



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
                if(byteArrayOut==null) {
                    Drawable drawable = ResourcesCompat.getDrawable(getResources(),R.drawable.person3,null);
                    BitmapDrawable bitmapDrawable =  (BitmapDrawable) drawable;

                    assert bitmapDrawable != null;
                    bitmap=bitmapDrawable.getBitmap();

                    ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                    Bitmap resized;
                    if(bitmap.getWidth()>300 && bitmap.getHeight()>300) {
                        resized = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
                    }
                    else{
                        resized=bitmap;
                    }
                    resized.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);

                    byteArrayOut=byteArrayOutputStream.toByteArray();
                    newContact.setByteArrayBlob(byteArrayOut);

                }else{
                    newContact.setByteArrayBlob(byteArrayOut);
                }

                dbHandler.addContact(newContact);
                newContact.setId(dbHandler.getAllContacts().get(dbHandler.getAllContacts().size()-1).getId());
                contactArrayList.add(newContact);
                dialog.dismiss();
                recyclerView.scrollToPosition(contactArrayList.size()-1);
                byteArrayOut=null;

            }
        });

        dialog.create();
        dialog.show();
    }

    private void showAlertDialog(String title,String message){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(message);
        alertDialog.setTitle(title);
        alertDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.create();
        alertDialog.show();
    }

    byte[] encodeImageUri(Uri result){

        InputStream inputStream = null;
        byte[] bytesOfImage;
        try {
            inputStream = getContentResolver().openInputStream(result);
            bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
            Bitmap resized;
            if(bitmap.getWidth()>300 && bitmap.getHeight()>300) {
                resized = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
            }
            else{
                resized=bitmap;
            }
            resized.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);

            bytesOfImage=byteArrayOutputStream.toByteArray();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return bytesOfImage;
    }
}