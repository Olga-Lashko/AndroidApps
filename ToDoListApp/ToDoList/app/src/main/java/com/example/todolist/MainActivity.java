/**
 * Final Project "To Do List": Program allows create list and add items in list.
 * Items can be marked as done using checkboxes.
 * User can delete any To Do List.
 * User can delete any items from any To Do List.
 * User can create multiple lists and see list of To Do Lists in recycler view in MainActivity.
 *
 * Class: CITC 2376, Spring 2024
 *
 * @author  Olga Lashko
 * @version May 3, 2024
 */
package com.example.todolist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ListAdapter.OnToDoListClickListener {

    private Button addNewListButton;
    private RecyclerView listsRecyclerView;
    private ListAdapter listAdapter;
    private List<ToDoList> toDoLists;

    private static final String SHARED_PREFS_KEY = "com.example.todolist.SHARED_PREFS_KEY";
    private static final int REQUEST_CODE_LIST_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addNewListButton = findViewById(R.id.addListButton);
        listsRecyclerView = findViewById(R.id.mainListRecView);

        // Load ToDoLists from SharedPreferences
        toDoLists = loadToDoListsFromSharedPreferences();

        // Initialize RecyclerView and set layout manager
        listsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize ListAdapter with loaded ToDoLists and set it to RecyclerView
        listAdapter = new ListAdapter(toDoLists, this);
        listsRecyclerView.setAdapter(listAdapter);

        addNewListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new empty list
                ToDoList newToDoList = new ToDoList();

                // Animate the addNewListButton
                animateAddNewListButton();

                // Start ListActivity
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                intent.putExtra("currentToDoList", newToDoList); // Pass the newToDoList object
                intent.putExtra("isNewList", true); // Add a boolean extra to indicate that the ToDoList is new
                startActivity(intent);
            }
        });
    }

    // Method to animate the addNewListButton
    private void animateAddNewListButton() {
        // Get the height of the screen
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;

        // Calculate the distance the button needs to move
        int buttonHeight = addNewListButton.getHeight();
        int distanceToMove = screenHeight - buttonHeight;

        // Animate the addNewListButton
        Animation animation = new TranslateAnimation(0, 0, 0, -distanceToMove);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Do nothing
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Gradually decrease the opacity of the button
                AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
                fadeOut.setDuration(500);
                fadeOut.setFillAfter(true);
                addNewListButton.startAnimation(fadeOut);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Do nothing
            }
        });
        addNewListButton.startAnimation(animation);
    }



    @Override
    protected void onResume() {
        super.onResume();
        // Reload saved ToDoLists from SharedPreferences
        toDoLists = loadToDoListsFromSharedPreferences();

        // Update the adapter with the new ToDoLists
        listAdapter.setToDoLists(toDoLists);

        // Reset the position of the add new list button
        addNewListButton.clearAnimation();
    }

    // Method to get ToDoLists from SharedPreferences
    private List<ToDoList> loadToDoListsFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("toDoLists", null);
        Type type = new TypeToken<List<ToDoList>>() {}.getType();
        return gson.fromJson(json, type);
    }

    // Method to save ToDoLists to SharedPreferences
    private void saveToDoListsToSharedPreferences(List<ToDoList> toDoLists) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(toDoLists);
        editor.putString("toDoLists", json);
        editor.apply();
    }

    private void startListActivity(ToDoList toDoList) {
        Intent intent = new Intent(MainActivity.this, ListActivity.class);
        intent.putExtra("currentToDoList", toDoList);
        startActivity(intent);
    }


    @Override
    public void onToDoListClick(int position) {
        ToDoList selectedToDoList = toDoLists.get(position);
        startListActivity(selectedToDoList);
    }

    @Override
    public void onDeleteButtonClick(int position) {
        toDoLists.remove(position);
        saveToDoListsToSharedPreferences(toDoLists);
        listAdapter.setToDoLists(toDoLists); // Update the RecyclerView
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LIST_ACTIVITY && resultCode == RESULT_OK && data != null) {
            ToDoList currentToDoList = data.getParcelableExtra("currentToDoList");
            String listTitle = data.getStringExtra("listTitle");
            // Update the UI with the received data
            if (currentToDoList != null && !TextUtils.isEmpty(listTitle)) {
                setTitle(listTitle);
                // Do whatever you need with the received currentToDoList
            }
        }
    }

    //methods to create option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Check the ID of the clicked item and perform the appropriate action
        if (item.getItemId() == R.id.about) {
            openAboutDialog();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void openAboutDialog() {
        AboutDialog aboutDialog = new AboutDialog();
        aboutDialog.show(getSupportFragmentManager(), "about_dialog");
    }

}
