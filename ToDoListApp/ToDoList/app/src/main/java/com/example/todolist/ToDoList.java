package com.example.todolist;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ToDoList implements Parcelable {
    private String title;
    private List<Item> items;

    public ToDoList(String title, List<Item> items) {
        this.title = title;
        this.items = items;
    }

    public ToDoList() {
        title = "New List";
        items = new ArrayList<>();
    }

    // Getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    // Method to add a new item to the list
    public void addItem(Item item) {
        items.add(item);
    }

    // Parcelable implementation
    protected ToDoList(Parcel in) {
        title = in.readString();
        items = new ArrayList<Item>();
        in.readList(items, Item.class.getClassLoader());
    }

    public static final Creator<ToDoList> CREATOR = new Creator<ToDoList>() {
        @Override
        public ToDoList createFromParcel(Parcel in) {
            return new ToDoList(in);
        }

        @Override
        public ToDoList[] newArray(int size) {
            return new ToDoList[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeList(items);
    }
    public void removeItem(int position) {
        if (items != null && position >= 0 && position < items.size()) {
            items.remove(position);
        }
    }
}
