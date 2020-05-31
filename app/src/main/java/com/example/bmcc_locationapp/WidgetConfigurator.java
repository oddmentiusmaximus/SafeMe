package com.example.bmcc_locationapp;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WidgetConfigurator extends AppCompatActivity
{
    SettingsActivity settingsActivity;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageButton imageButton;
        imageButton=findViewById(R.id.imageView);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = getApplicationContext().getSharedPreferences(settingsActivity.MyPreferences, 0);
                String requiredPermission = "android.permission.SEND_SMS";
                int checkVal = getApplicationContext().checkCallingOrSelfPermission(requiredPermission);
                if (checkVal == PackageManager.PERMISSION_GRANTED) {

                    SmsManager sms = SmsManager.getDefault();
                    SmsManager smsManager = SmsManager.getDefault();
                    String message = sharedPreferences.getString(settingsActivity.Messages, null);
                    String pContact = sharedPreferences.getString(settingsActivity.pContacts, null);
                    String sContact = sharedPreferences.getString(settingsActivity.sContacts, null);
                    sms.sendTextMessage(pContact, null, message, null, null);
                    smsManager.sendTextMessage(sContact, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "Message to Send message", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Unable to Send message", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
