package com.github.carlhmitchell.contactablespicker.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.carlhmitchell.contactablespicker.ContactsList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button showPickerButton = findViewById(R.id.show_picker_button);
        showPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
                    public void onClick(View v) {
                doLaunchContactPicker();
            }
        });
    }

    private void doLaunchContactPicker() {
        Intent contactPickerIntent = new Intent(this, ContactsList.class);
        startActivity(contactPickerIntent);
    }
}
