package com.example.todolist;

// ListAdapter.java

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<ToDoList> toDoLists;
    private OnToDoListClickListener onToDoListClickListener;
    private Context context;
    private static final String SHARED_PREFS_KEY = "com.example.todolist.SHARED_PREFS_KEY";

    public ListAdapter(List<ToDoList> toDoLists, OnToDoListClickListener onToDoListClickListener) {
        this.toDoLists = toDoLists;
        this.onToDoListClickListener = onToDoListClickListener;
    }

    public void setToDoLists(List<ToDoList> toDoLists) {
        this.toDoLists = toDoLists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_main, parent, false);
        return new ViewHolder(view, onToDoListClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToDoList toDoList = toDoLists.get(position);
        holder.bind(toDoList);
    }

    @Override
    public int getItemCount() {
        return toDoLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView titleTextView;
        private ImageButton delListButton;
        private OnToDoListClickListener onToDoListClickListener;

        public ViewHolder(@NonNull View itemView, OnToDoListClickListener onToDoListClickListener) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.listTitleTextView);
            delListButton = itemView.findViewById(R.id.deleteItemImage);
            this.onToDoListClickListener = onToDoListClickListener;
            itemView.setOnClickListener(this);

            // Handle delete button click
            delListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && onToDoListClickListener != null) {
                        onToDoListClickListener.onDeleteButtonClick(position);
                    }
                }
            });
        }

        public void bind(ToDoList toDoList) {
            titleTextView.setText(toDoList.getTitle());
        }

        @Override
        public void onClick(View v) {
            if (onToDoListClickListener != null) {
                onToDoListClickListener.onToDoListClick(getAdapterPosition());
            }
        }
    }

    public interface OnToDoListClickListener {
        void onToDoListClick(int position);
        void onDeleteButtonClick(int position);
    }


}
