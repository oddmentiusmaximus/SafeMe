package com.example.bmcc_locationapp;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.RemoteViews;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.widget.Toast;

import com.example.bmcc_locationapp.Model.GPSTracker;

import androidx.appcompat.app.AlertDialog;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class AlertWidget extends AppWidgetProvider {
    SharedPreferences sharedPreferences;
    SettingsActivity settingsActivity;

    HomeActivity homeActivity;


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            CharSequence widgetText = context.getString(R.string.appwidget_text);
            Intent intent = new Intent(context, WidgetConfigurator.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.alert_widget);
            views.setOnClickPendingIntent(R.id.imageView, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetIds[i], views);


        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Toast.makeText(context, "Widget Created", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.alert_widget);

    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Toast.makeText(context, "Widget Edited", Toast.LENGTH_SHORT).show();

        sharedPreferences = context.getSharedPreferences(settingsActivity.MyPreferences, 0);
        String requiredPermission = "android.permission.SEND_SMS";
        int checkVal = context.checkCallingOrSelfPermission(requiredPermission);
        if (checkVal == PackageManager.PERMISSION_GRANTED) {

            SmsManager sms = SmsManager.getDefault();
            SmsManager smsManager = SmsManager.getDefault();
            String message = sharedPreferences.getString(settingsActivity.Messages, null);
            String pContact = sharedPreferences.getString(settingsActivity.pContacts, null);
            String sContact = sharedPreferences.getString(settingsActivity.sContacts, null);
            sms.sendTextMessage(pContact, null, message, null, null);
            smsManager.sendTextMessage(sContact, null, message, null, null);
            Toast.makeText(context, "Message to Send message", Toast.LENGTH_SHORT).show();

        }
    }
}