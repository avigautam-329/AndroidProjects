package com.example.babyneeds.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.example.babyneeds.ListActivity;
import com.example.babyneeds.R;
import com.example.babyneeds.data.DatabaseHandler;
import com.example.babyneeds.model.Item;
import com.google.android.material.snackbar.Snackbar;

public class CreatePopUpMenu  {
    private Context context;

    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private Button saveButton;
    private EditText babyItem;
    private EditText itemQuantity;
    private EditText itemSize;
    private EditText itemColor;
    private DatabaseHandler databaseHandler;


    public CreatePopUpMenu(Context context) {
        this.context = context;
        databaseHandler = new DatabaseHandler(context);

    }

    public void CreateMenu(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        builder = new AlertDialog.Builder(context);
        View view = inflater.inflate(R.layout.popup,null);
        babyItem = view.findViewById(R.id.babyItem);
        itemQuantity = view.findViewById(R.id.itemQuantity);
        itemSize = view.findViewById(R.id.itemSize);
        itemColor = view.findViewById(R.id.itemColor);

        saveButton = view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!babyItem.getText().toString().isEmpty()
                        && !itemColor.getText().toString().isEmpty()
                        && !itemQuantity.getText().toString().isEmpty()
                        && !itemSize.getText().toString().isEmpty()){
                    saveItem(v);
                }
                else{
                    Snackbar.make(v,"Empty field not allowed",Snackbar.LENGTH_SHORT);
                }

            }
        });

        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    private void saveItem(View view) {
        Item item = new Item();
        final Intent intent = new Intent(context,ListActivity.class);


        String newItem = babyItem.getText().toString().trim();
        String newColor = itemColor.getText().toString().trim();
        int quantity = Integer.parseInt(itemQuantity.getText().toString().trim());
        int size = Integer.parseInt(itemSize.getText().toString().trim());

        item.setItemName(newItem);
        item.setItemColor(newColor);
        item.setItemQuantity(quantity);
        item.setItemSize(size);

        //date is already added at the SQL level when the entry is created.

        databaseHandler.addItem(item);
        Snackbar.make(view,"Item saved !",Snackbar.LENGTH_SHORT);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Put here the code to be run.
                dialog.dismiss();
                context.startActivity(intent);
                Activity activity = new Activity();
                activity.finish();

            }
        },500);

    }
}
