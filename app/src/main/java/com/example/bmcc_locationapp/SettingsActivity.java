package com.example.bmcc_locationapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bmcc_locationapp.Utils.Common;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;


public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private EditText message, primaryContact, secondaryContact;
    public static final String MyPreferences = "Preference";
    public static final String Messages = "MessageKey";
    public static final String pContacts = "pContactKey";

    public static final String sContacts = "sContactKey";

    SharedPreferences.Editor editor;

    SharedPreferences sharedPreferences;


    public static final String securityOnOffs = "securityKey";

    public SettingsActivity() {

    }

    public SettingsActivity(Context context) {
        sharedPreferences = context.getSharedPreferences(MyPreferences, 0);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    ImageButton menu_button;

    private AppBarConfiguration mAppBarConfiguration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_setting_activity);
        message = (EditText) findViewById(R.id.emergencyMessageTextField);
        primaryContact = (EditText) findViewById(R.id.primaryContactTextField);
        secondaryContact = (EditText) findViewById(R.id.secondaryContactTextField);
        Context context = getApplicationContext();

        menu_button = findViewById(R.id.menu_button);
        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                NavigationView navigationView = findViewById(R.id.nav_view);
                drawer.openDrawer(GravityCompat.START);
            }
        });

        sharedPreferences = context.getSharedPreferences(MyPreferences, 0);
        if (sharedPreferences.contains(Messages)) {
            message.setText(sharedPreferences.getString(Messages, ""));
        }
        if (sharedPreferences.contains(pContacts)) {
            primaryContact.setText(sharedPreferences.getString(pContacts, ""));
        }
        if (sharedPreferences.contains(sContacts)) {
            secondaryContact.setText(sharedPreferences.getString(sContacts, ""));
        }



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
         mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
//       // NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    public void settingDetails(View view) {
        Context context = getApplicationContext();
        sharedPreferences = context.getSharedPreferences(MyPreferences, 0);
        editor = sharedPreferences.edit();
        String emergencyMessage = message.getText().toString();
        String primaryCont = primaryContact.getText().toString();

        String sContact = secondaryContact.getText().toString();

        editor.putString(Messages, emergencyMessage);
        editor.putString(pContacts, primaryCont);

        editor.putString(sContacts, sContact);

        editor.apply();
        Toast.makeText(this, "Configuration details have been saved", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.nav_home) {
            Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
            startActivity(intent);
        }
        if (id == R.id.nav_find_people) {
            Intent intent = new Intent(SettingsActivity.this, AllPeopleActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_add_people) {

            startActivity(new Intent(SettingsActivity.this, FriendRequestActivity.class));

        } else if (id == R.id.setProPic) {
            startActivity(new Intent(this, ProfileImage.class));
        } else if (id == R.id.setUp) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_sign_out) {
            Toast.makeText(this, "signing out", Toast.LENGTH_LONG).show();

            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // User is now sign out
                            startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                            finish();
                        }
                    });
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
