package com.example.todolist;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<Item> items;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private OnItemDeleteClickListener deleteClickListener;

    public ItemAdapter(List<Item> items) {
        this.items = items;
    }

    public ItemAdapter() {
        this.items = new ArrayList<>();
    }

    public void setItems(List<Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        sharedPreferences = parent.getContext().getSharedPreferences("checkbox_states", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Item item = items.get(position);
        holder.bind(item);

        // Restore the checkbox state from SharedPreferences
        boolean isChecked = sharedPreferences.getBoolean("checkbox_" + position, false);
        holder.checkbox.setChecked(isChecked);

        // Save the checkbox state to SharedPreferences when it changes
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setChecked(isChecked);
                editor.putBoolean("checkbox_" + position, isChecked);
                editor.apply();
            }
        });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Item item) {
        // Set the checkbox state of the new item to false
        item.setChecked(false);
        items.add(item);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void clearItems() {
        items.clear();
        notifyDataSetChanged();
    }

    public Item getItem(int position) {
        return items.get(position);
    }

    public List<Item> getItems() {
        return items;
    }

    public interface OnItemDeleteClickListener {
        void onItemDeleteClick(int position);
    }

    public void setOnItemDeleteClickListener(OnItemDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private ImageView deleteItemImage;
        private CheckBox checkbox;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.ItemTextView);
            deleteItemImage = itemView.findViewById(R.id.deleteItemImage);
            checkbox = itemView.findViewById(R.id.itemCheckBox);

            deleteItemImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (deleteClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            deleteClickListener.onItemDeleteClick(position);
                        }
                    }
                }
            });
        }

        public void bind(Item item) {
            textView.setText(item.getItemText());
        }
    }
}
