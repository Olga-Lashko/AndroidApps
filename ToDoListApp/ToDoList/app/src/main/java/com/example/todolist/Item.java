package com.example.todolist;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {
    private String itemText;
    private boolean isChecked;

    public Item(String text, boolean isChecked) {
        this.itemText = text;
        this.isChecked = isChecked;
    }

    public Item(String text) {
        this.itemText = text;
    }


    public String getItemText() {
        return itemText;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    // Parcelable implementation
    protected Item(Parcel in) {
        itemText = in.readString();
        isChecked = in.readByte() != 0;
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemText);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }
}
