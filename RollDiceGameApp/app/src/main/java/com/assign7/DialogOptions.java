package com.assign7;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

// DialogOptions.java

public class DialogOptions extends DialogFragment {

    private CheckBox doubleCheckBox;
    private CheckBox tripleCheckBox;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_options, null);

        doubleCheckBox = view.findViewById(R.id.doubleCheckBox);
        tripleCheckBox = view.findViewById(R.id.tripleCheckBox);

        // Set the initial checked status of checkboxes
        doubleCheckBox.setChecked(((MainActivity) requireActivity()).isDoubleChecked);
        tripleCheckBox.setChecked(((MainActivity) requireActivity()).isTripleChecked);

        Button saveButton = view.findViewById(R.id.saveBtn);
        Button cancelButton = view.findViewById(R.id.cancelBtn);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass data back to MainActivity
                boolean doubleChecked = doubleCheckBox.isChecked();
                boolean tripleChecked = tripleCheckBox.isChecked();
                ((MainActivity) requireActivity()).updateDiceSettings(doubleChecked, tripleChecked);
                dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog without applying changes
                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }

}
