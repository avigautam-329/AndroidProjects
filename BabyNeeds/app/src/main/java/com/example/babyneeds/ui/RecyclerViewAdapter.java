package com.example.babyneeds.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babyneeds.R;
import com.example.babyneeds.data.DatabaseHandler;
import com.example.babyneeds.model.Item;
import com.google.android.material.snackbar.Snackbar;

import java.text.MessageFormat;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Item> itemList;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private Button yes_button;
    private Button no_button;

    public RecyclerViewAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row,parent,false);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        Item item = itemList.get(position);

        holder.itemName.setText(MessageFormat.format("Item Name: {0}", item.getItemName()));
        holder.itemQuantity.setText(MessageFormat.format("Quantity: {0}", String.valueOf(item.getItemQuantity())));
        holder.itemColor.setText(MessageFormat.format("Color: {0}", item.getItemColor()));
        holder.itemSize.setText(MessageFormat.format("Size: {0}", String.valueOf(item.getItemSize())));
        holder.itemAddedDate.setText("Added on: " + item.getDateItemAdded());
}

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView itemName;
        public TextView itemColor;
        public TextView itemQuantity;
        public TextView itemSize;
        public TextView itemAddedDate;
        public Button editButton;
        public Button deleteButton;

        public ViewHolder(@NonNull View itemView,Context ctx) {
            super(itemView);
            context = ctx;

            itemName = itemView.findViewById(R.id.item_name);
            itemColor = itemView.findViewById(R.id.item_color);
            itemQuantity = itemView.findViewById(R.id.item_quantity);
            itemSize = itemView.findViewById(R.id.item_size);
            itemAddedDate = itemView.findViewById(R.id.item_date);

            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            final int item_id;
            item_id = getAdapterPosition();
            final Item item = itemList.get(getAdapterPosition());
            switch(v.getId()){
                case R.id.edit_button:
                    editItem(item);
                    break;
                case R.id.delete_button:

                    LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    builder = new AlertDialog.Builder(context);

                    View view = layoutInflater.inflate(R.layout.deleteconfirmationpopoup,null);
                    builder.setView(view);
                    dialog = builder.create();
                    dialog.show();

                    yes_button = view.findViewById(R.id.yes_btn);
                    no_button = view.findViewById(R.id.no_btn);

                    yes_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Item item = itemList.get(item_id);
                            deleteItem(item.getId());
                            dialog.dismiss();
                        }
                    });
                    no_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    break;
                default:
                    break;
            }
        }

        private void editItem(final Item newItem) {
            final EditText babyItem;
            TextView title;
            Button saveButton;
            final EditText itemQuantity;
            final EditText itemSize;
            final EditText itemColor;


            builder = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate(R.layout.popup,null);

            babyItem = view.findViewById(R.id.babyItem);
            itemQuantity = view.findViewById(R.id.itemQuantity);
            itemSize = view.findViewById(R.id.itemSize);
            itemColor = view.findViewById(R.id.itemColor);
            title = view.findViewById(R.id.title_card);

            saveButton = view.findViewById(R.id.save_button);
            title.setText(R.string.edit_item_text);
            babyItem.setText(newItem.getItemName());
            itemQuantity.setText(String.valueOf(newItem.getItemQuantity()));
            itemColor.setText(newItem.getItemColor());
            itemSize.setText(String.valueOf(newItem.getItemSize()));
            saveButton.setText(R.string.update_text);

            builder.setView(view);
            dialog = builder.create();
            dialog.show();

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseHandler db = new DatabaseHandler(context);
                    //update item information.
                    newItem.setItemName(babyItem.getText().toString().trim());
                    newItem.setItemColor(itemColor.getText().toString().trim());
                    newItem.setItemSize(Integer.parseInt(itemSize.getText().toString()));
                    newItem.setItemQuantity(Integer.parseInt(itemQuantity.getText().toString()));

                    if(!babyItem.getText().toString().isEmpty()
                            && !itemColor.getText().toString().isEmpty()
                            && !itemQuantity.getText().toString().isEmpty()
                            && !itemSize.getText().toString().isEmpty()){
                        db.updateItem(newItem);
                        notifyItemChanged(getAdapterPosition(),newItem);
                        dialog.dismiss();
                    }else{
                        Snackbar.make(v,"There are empty fields.",Snackbar.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }


                }
            });
        }

        private void deleteItem(int id) {
            DatabaseHandler db = new DatabaseHandler(context);
            db.deleteItem(id);
            itemList.remove(getAdapterPosition());
            notifyItemRemoved(getAdapterPosition());
        }
    }


}
