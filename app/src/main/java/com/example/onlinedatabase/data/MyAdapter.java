package com.example.onlinedatabase.data;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinedatabase.MainActivity;
import com.example.onlinedatabase.R;
import com.example.onlinedatabase.model.CONTACT;
import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;
    ArrayList<CONTACT> contactList;
    MyDBHandler myDBHandler;

    public MyAdapter(Context context, ArrayList<CONTACT> contactList){
        this.context=context;
        this.contactList=contactList;
        myDBHandler = new MyDBHandler(context);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.list_item,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.contactNameTV.setText(contactList.get(position).getName());
        holder.contactNumberTV.setText(contactList.get(position).getContact());
        byte[] byteArray = contactList.get(position).getByteArrayBlob();
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
        holder.contactImageItem.setImageBitmap(bitmap);
        holder.optionsIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view,holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }
    public void showPopupMenu(View view,int position){
        PopupMenu popupMenu = new PopupMenu(context,view);
        popupMenu.inflate(R.menu.edit_delete_menus);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.edit:{
                        showDialogBox(position);
                        popupMenu.dismiss();
                        break;
                    }
                    case R.id.delete: {
                        deleteItem(position);
                        popupMenu.dismiss();

                        break;
                    }
                }
                return true;
            }
        });
        popupMenu.show();

        //Below try catch block is used to force menu icon to appear with the text.
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void showDialogBox(int position){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.add_contact);
        dialog.setCancelable(false);

        TextInputEditText contactNameADDCON=dialog.findViewById(R.id.contactNameADDCON);
        TextInputEditText contactNumberADDCON=dialog.findViewById(R.id.contactNumberADDCON);
        Button cancelButtonDialog=dialog.findViewById(R.id.cancelButtonDialog);
        Button okButtonDialog=dialog.findViewById(R.id.okButtonDialog);
        CircleImageView contactImage=dialog.findViewById(R.id.contactImage);

        contactNameADDCON.setText(contactList.get(position).getName());
        contactNumberADDCON.setText(contactList.get(position).getContact());
        byte[] byteArray = contactList.get(position).getByteArrayBlob();
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
        contactImage.setImageBitmap(bitmap);



//        contactImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        cancelButtonDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        okButtonDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CONTACT updatedContact=new CONTACT();
                updatedContact.setId(contactList.get(position).getId());
                updatedContact.setName(contactNameADDCON.getText().toString().trim());
                updatedContact.setContact(contactNumberADDCON.getText().toString().toString());
                updatedContact.setByteArrayBlob(byteArray);

                myDBHandler.updateContact(updatedContact);
                MainActivity.contactArrayList = new ArrayList<>(myDBHandler.getAllContacts());
                MyAdapter myAdapter=new MyAdapter(context,MainActivity.contactArrayList);
                MainActivity.recyclerView.setAdapter(myAdapter);
                MainActivity.recyclerView.setLayoutManager(new LinearLayoutManager(context));
                MainActivity.recyclerView.scrollToPosition(position);
                dialog.dismiss();
            }
        });

        dialog.create();
        dialog.show();

    }

    @SuppressLint("NotifyDataSetChanged")
    public void deleteItem(int position){
        myDBHandler.deleteContact(contactList.get(position).getId());
        MainActivity.contactArrayList.remove(position);
        notifyDataSetChanged();
        MainActivity.recyclerView.scrollToPosition(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView contactNameTV;
        TextView contactNumberTV;
        ImageView optionsIV;
        CircleImageView contactImageItem;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            contactNameTV=itemView.findViewById(R.id.contactNameTV);
            contactNumberTV=itemView.findViewById(R.id.contactNumberTV);
            optionsIV=itemView.findViewById(R.id.optionsIV);
            contactImageItem=itemView.findViewById(R.id.contactImageItem);
        }
    }
}
