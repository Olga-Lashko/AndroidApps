package com.example.todolist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Editable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ListActivity extends AppCompatActivity implements ItemAdapter.OnItemDeleteClickListener{

    private EditText titleEditText;
    private Button addNewItemButton;
    private Button cancelItemButton;
    private Button saveItemButton;
    private List<Item> itemList;
    private RecyclerView itemRecyclerView;
    private RelativeLayout addNewItemLayout;
    private ItemAdapter itemAdapter;
    private EditText newItemTextEdit;
    private TextView titleLabelTextView;

    private List<ToDoList> toDoLists;
    private ToDoList currentToDoList;

    private static final String SHARED_PREFS_KEY = "com.example.todolist.SHARED_PREFS_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Enable the back button in the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Load ToDoLists from SharedPreferences
        toDoLists = loadToDoListsFromSharedPreferences();

        // Initialize RecyclerView and LayoutManager
        itemRecyclerView = findViewById(R.id.itemRecyclerView);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        titleLabelTextView = findViewById(R.id.titleLabelTextView);
        newItemTextEdit = findViewById(R.id.newItemEditText);
        titleEditText = findViewById(R.id.titleEditText);
        addNewItemButton = findViewById(R.id.addItemButton);
        cancelItemButton = findViewById(R.id.cancelItemButton);
        saveItemButton = findViewById(R.id.saveItemButton);
        addNewItemLayout = findViewById(R.id.addNewItemLayout);

        // Load the current ToDoList from the Intent extras
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("currentToDoList")) {
            currentToDoList = intent.getParcelableExtra("currentToDoList");
            boolean isNewList = intent.getBooleanExtra("isNewList", false); // Get the boolean extra
            if (!isNewList) {
                // Hide the titleEditText if the ToDoList is new
                titleEditText.setVisibility(View.GONE);
                titleLabelTextView.setVisibility(View.GONE);
            }
            if (currentToDoList != null) {
                // Update the ActionBar title with the current ToDoList's title
                setTitle(currentToDoList.getTitle());
                // Set the title in the titleEditText field
                titleEditText.setText(currentToDoList.getTitle());

                // Set up TextWatcher to monitor changes in titleEditText
                titleEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (currentToDoList != null) {
                            currentToDoList.setTitle(s.toString());
                            // Update the title in the title bar
                            setTitle(currentToDoList.getTitle());
                            // Save the updated ToDoList to SharedPreferences
                            saveToDoListToSharedPreferences(currentToDoList);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });

                itemAdapter = new ItemAdapter(currentToDoList.getItems());
                itemRecyclerView.setAdapter(itemAdapter);

                // Set item delete click listener
                itemAdapter.setOnItemDeleteClickListener(this);
            } else {
                // Inform the user that the ToDoList is empty
                Toast.makeText(this, "ToDoList is empty. You can add new items and a title.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // If no ToDoList is passed via intent, inform the user
            Toast.makeText(this, "No ToDoList found.", Toast.LENGTH_SHORT).show();
        }

        addNewItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString().trim();

                if (currentToDoList != null) {
                    if (title.isEmpty()) {
                        showToast("Please enter a title");
                    } else {
                        // set title
                        currentToDoList.setTitle(title);
                        saveToDoListsToSharedPreferences(toDoLists);
                        showAddNewItem();
                    }
                } else {
                    if (title.isEmpty()) {
                        showToast("Please enter a title");
                    } else {
                        currentToDoList.setTitle(title);
                        toDoLists.add(currentToDoList);
                        saveToDoListsToSharedPreferences(toDoLists);
                        // Restart the method
                        recreate();
                    }
                }
            }
        });


        saveItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newItemText = newItemTextEdit.getText().toString().trim();
                if (!newItemText.isEmpty()) {
                    // Create a new Item object with the item text
                    Item newItem = new Item(newItemText);
                    currentToDoList.addItem(newItem);
                    itemAdapter.notifyDataSetChanged();
                    saveToDoListToSharedPreferences(currentToDoList); // Save ToDoList to SharedPreferences
                    newItemTextEdit.setText("");

                    // Show all elements that were hidden
                    hideAddNewItem();
                } else {
                    showToast("Enter the item text");
                }
            }
        });

        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (currentToDoList != null) {
                    currentToDoList.setTitle(s.toString());
                    saveToDoListsToSharedPreferences(toDoLists);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        cancelItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show all elements that were hidden
                hideAddNewItem();
            }
        });

        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (currentToDoList != null) {
                    currentToDoList.setTitle(s.toString());
                    setTitle(currentToDoList.getTitle());
                    saveToDoListToSharedPreferences(currentToDoList); // Save the updated title to SharedPreferences
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }

    // Handle back button press in the action bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Respond to the back arrow click
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Handle item deletion
    @Override
    public void onItemDeleteClick(int position) {
        if (currentToDoList != null) {
            currentToDoList.removeItem(position);
            itemAdapter.notifyDataSetChanged();
            saveToDoListToSharedPreferences(currentToDoList); // Save ToDoList to SharedPreferences
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (currentToDoList != null) {
            saveToDoListToSharedPreferences(currentToDoList); // Save ToDoList to SharedPreferences when the activity stops
        }
    }


    private void updateUI() {
        if (currentToDoList != null) {
            //    titleEditText.setText(currentToDoList.getTitle());
            itemList = currentToDoList.getItems();
            if (itemAdapter == null) {
                itemAdapter = new ItemAdapter(itemList);
                itemRecyclerView.setAdapter(itemAdapter); // Set the adapter to the RecyclerView
            } else {
                itemAdapter.setItems(itemList); // Update the adapter with new list of items
                itemAdapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
            }
        }
    }

    private void startListActivity(ToDoList currentToDoList) {
        Intent intent = new Intent(ListActivity.this, MainActivity.class);
        intent.putExtra("currentToDoList", currentToDoList);
        intent.putExtra("listTitle", currentToDoList.getTitle()); // Pass the title
        startActivity(intent);
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void hideAddNewItem() {
        addNewItemLayout.setVisibility(View.GONE);
        addNewItemButton.setVisibility(View.VISIBLE);
        itemRecyclerView.setVisibility(View.VISIBLE);
       // titleLabelTextView.setVisibility(View.VISIBLE);
        //titleEditText.setVisibility(View.VISIBLE);
        updateUI();
    }

    private void showAddNewItem() {
        addNewItemLayout.setVisibility(View.VISIBLE);
        addNewItemButton.setVisibility(View.GONE);
        itemRecyclerView.setVisibility(View.GONE);
        titleLabelTextView.setVisibility(View.GONE);
        titleEditText.setVisibility(View.GONE);
    }

    private int getToDoListIndex(ToDoList currentToDoList, List<ToDoList> toDoLists) {
        for (int i = 0; i < toDoLists.size(); i++) {
            if (toDoLists.get(i).getTitle().equals(currentToDoList.getTitle())) {
                return i;
            }
        }
        return -1;
    }

    public void saveToDoListsToSharedPreferences(List<ToDoList> toDoLists) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(toDoLists);
        editor.putString("toDoLists", json);
        editor.apply();
    }

    private List<ToDoList> loadToDoListsFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("toDoLists", null);
        Type type = new TypeToken<List<ToDoList>>() {}.getType();
        return gson.fromJson(json, type);
    }

    // Method to save ToDoList to SharedPreferences
    private void saveToDoListToSharedPreferences(ToDoList currentToDoList) {
        // Get the list of ToDoLists from SharedPreferences
        List<ToDoList> toDoLists = loadToDoListsFromSharedPreferences();
        // Update the ToDoList or add it if it's a new ToDoList
        int index = getToDoListIndex(currentToDoList, toDoLists);
        if (index != -1) {
            toDoLists.set(index, currentToDoList); // Update existing ToDoList
        } else {
            toDoLists.add(currentToDoList); // Add new ToDoList
        }
        // Save the updated list of ToDoLists to SharedPreferences
        saveToDoListsToSharedPreferences(toDoLists);
    }
    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("currentToDoList", currentToDoList);
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }


}