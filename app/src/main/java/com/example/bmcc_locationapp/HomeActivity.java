package com.example.bmcc_locationapp;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.example.bmcc_locationapp.Interface.AccelerometerListener;
import com.example.bmcc_locationapp.Interface.IFirebaseLoadDone;
import com.example.bmcc_locationapp.Interface.IRecyclerItemClickListener;
import com.example.bmcc_locationapp.Model.AccelerometerManager;
import com.example.bmcc_locationapp.Model.User;
import com.example.bmcc_locationapp.Service.MyLocationReceiver;
import com.example.bmcc_locationapp.Utils.Common;
import com.example.bmcc_locationapp.ViewHolder.UserViewHolder;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.provider.Settings;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IFirebaseLoadDone, AccelerometerListener {
    FirebaseRecyclerAdapter<User, UserViewHolder> adapter, searchAdapter;
    RecyclerView recycler_friend_list;
    IFirebaseLoadDone firebaseLoadDone;
    MaterialSearchBar searchBar;
    List<String> suggestList = new ArrayList<>();

    SharedPreferences sharedPreferences;
    SettingsActivity settingsActivity;

    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    private AppBarConfiguration mAppBarConfiguration;

    FloatingActionButton button_alert;
    FloatingActionButton call_button;

    ImageButton menu_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        menu_button = findViewById(R.id.menu_button);
        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                NavigationView navigationView = findViewById(R.id.nav_view);
                drawer.openDrawer(GravityCompat.START);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, AllPeopleActivity.class));
            }
        });
        button_alert = findViewById(R.id.alert);
        button_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = getApplicationContext().getSharedPreferences(settingsActivity.MyPreferences, 0);

                String message = sharedPreferences.getString(settingsActivity.Messages, "");
                String pContact = sharedPreferences.getString(settingsActivity.pContacts, "");
                String sContact = sharedPreferences.getString(settingsActivity.sContacts, "");

                if (message.equals("") || pContact.equals("") || sContact.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please fill all the fields and then continue", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                    startActivity(intent);

                } else {
                    String requiredPermission = "android.permission.SEND_SMS";
                    int checkVal = getApplicationContext().checkCallingOrSelfPermission(requiredPermission);
                    if (checkVal == PackageManager.PERMISSION_GRANTED) {

                        sendMessage(view);
                        shareWithWhatsApp(view);
                    } else {
                        showDialogRequest();
                    }

                }

            }
        });
        call_button = findViewById(R.id.medical_emergency);
        call_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callHospital(view);
            }
        });

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

        View headerView = navigationView.getHeaderView(0);
        TextView txt_user_logged = headerView.findViewById(R.id.txt_logged_email);
        Log.e("TAG", "onCreate: " + Common.loggedUser.getEmail());
        txt_user_logged.setText(Common.loggedUser.getEmail());

        //View mazha
        searchBar = findViewById(R.id.material_serach_bar);
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<String> suggest = new ArrayList<>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<String> suggest = new ArrayList<>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                List<String> suggest = new ArrayList<>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled) {
                    if (adapter != null) {
                        //if search is closed it restores to default
                        recycler_friend_list.setAdapter(adapter);
                    }

                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString());
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });


        recycler_friend_list = findViewById(R.id.recycler_friend_list);
        recycler_friend_list.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_friend_list.setLayoutManager(layoutManager);
        recycler_friend_list.addItemDecoration(new DividerItemDecoration(this, ((LinearLayoutManager) layoutManager).getOrientation()));

        //Update Location

        updateLocation();

        firebaseLoadDone = this;

        loadFriendList();
        loadSearchData();

    }


    private void loadSearchData() {
        final List<String> lstUserEmail = new ArrayList<>();

        DatabaseReference userList = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.ACCEPT_LIST);
        userList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {

                    User user = userSnapshot.getValue(User.class);
                    lstUserEmail.add(user.getEmail());

                }
                firebaseLoadDone.onFirebaseLoadUserNameDone(lstUserEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                firebaseLoadDone.onFirebaseLoadFailed(databaseError.getMessage());
            }
        });


    }

    private void loadFriendList() {
        Query query = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.ACCEPT_LIST);

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int i, @NonNull final User model) {

                holder.txt_user_email.setText(new StringBuilder(model.getEmail()));


                holder.setiRecyclerItemClickListener(new IRecyclerItemClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        //show tracking
                        Common.trackingUser = model;
                        startActivity(new Intent(HomeActivity.this, TrackingActivity.class));

                    }
                });
            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_user, viewGroup, false);


                return new UserViewHolder(itemView);
            }
        };
        adapter.startListening();
        ;
        recycler_friend_list.setAdapter(adapter);


    }

    @Override
    protected void onStop() {
        if (adapter != null) {
            adapter.stopListening();
        }
        if (searchAdapter != null) {
            searchAdapter.stopListening();
        }
        //Check device supported Accelerometer sensor or not
        if (AccelerometerManager.isListening()) {

//Start Accelerometer Listening
            AccelerometerManager.stopListening();

            Toast.makeText(getBaseContext(), "onStop Accelerometer Stopped",
                    Toast.LENGTH_SHORT).show();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.startListening();
        }
        if (searchAdapter != null) {
            searchAdapter.startListening();
        }
        Toast.makeText(getBaseContext(), "onResume Accelerometer Started",
                Toast.LENGTH_SHORT).show();

//Check device supported Accelerometer senssor or not
        if (AccelerometerManager.isSupported(this)) {

//Start Accelerometer Listening
            AccelerometerManager.startListening(this);
        }
    }

    private void updateLocation() {
        buildLocationRequest();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent());

    }

    private PendingIntent getPendingIntent() {

        Intent intent = new Intent(HomeActivity.this, MyLocationReceiver.class);
        intent.setAction(MyLocationReceiver.ACTION);


        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setSmallestDisplacement(10f);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startSearch(String search_value) {
        Query query = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.ACCEPT_LIST)
                .orderByChild("name")
                .startAt(search_value);

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int i, @NonNull final User model) {

                holder.txt_user_email.setText(new StringBuilder(model.getEmail()));


                holder.setiRecyclerItemClickListener(new IRecyclerItemClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        //show tracking
                        Common.trackingUser = model;
                        startActivity(new Intent(HomeActivity.this, TrackingActivity.class));

                    }
                });
            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_user, viewGroup, false);


                return new UserViewHolder(itemView);
            }
        };
        searchAdapter.startListening();
        recycler_friend_list.setAdapter(adapter);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
            startActivity(intent);
        }
        if (id == R.id.nav_find_people) {
            Intent intent = new Intent(HomeActivity.this, AllPeopleActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_add_people) {

            startActivity(new Intent(HomeActivity.this, FriendRequestActivity.class));

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
                            startActivity(new Intent(HomeActivity.this, MainActivity.class));
                            finish();
                        }
                    });
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFirebaseLoadUserNameDone(List<String> lstEmail) {
        searchBar.setLastSuggestions(lstEmail);
    }

    @Override
    public void onFirebaseLoadFailed(String message) {

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showDialogRequest() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.MyRquestDialog);

        alertDialog.setTitle("Permissions Request");
        alertDialog.setMessage("Enable All the Permissions First Please");
        alertDialog.setIcon(R.mipmap.boy_girl_round);
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

            }
        });
        alertDialog.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Add to Accept List
                Intent intent1 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent1.setData(uri);
                startActivity(intent1);


            }
        });

        alertDialog.show();//
    }

    private void callHospital(View view) {

        Intent intent = new Intent(Intent.ACTION_CALL);

        intent.setData(Uri.parse("tel:112"));
        if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            Toast.makeText(getApplicationContext(), "Please allow us", Toast.LENGTH_LONG).show();

        }
        String requiredPermission = "android.permission.CALL_PHONE";
        int checkVal = getApplicationContext().checkCallingOrSelfPermission(requiredPermission);
        if (checkVal == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent);
        } else {
            showDialogRequest();
        }
    }

    public void sendMessage(View view) {
        Context context = getApplicationContext();
        sharedPreferences = context.getSharedPreferences(settingsActivity.MyPreferences, 0);
        String requiredPermission = "android.permission.SEND_SMS";
        int checkVal = getApplicationContext().checkCallingOrSelfPermission(requiredPermission);
        if (checkVal == PackageManager.PERMISSION_GRANTED) {

            SmsManager sms = SmsManager.getDefault();
            SmsManager smsManager = SmsManager.getDefault();
            String message = sharedPreferences.getString(settingsActivity.Messages, null);
            String pContact = sharedPreferences.getString(settingsActivity.pContacts, null);
            String sContact = sharedPreferences.getString(settingsActivity.sContacts, null);
            sms.sendTextMessage(pContact, null, message, null, hello());
            smsManager.sendTextMessage(sContact, null, message, null, null);
        } else {
            showDialogRequest();
        }
    }

    private PendingIntent hello() {
        int i = 0;
        Toast.makeText(this, "Message sent successfully " + ++i, Toast.LENGTH_LONG).show();
        return null;
    }

    public void shareWithWhatsApp(View view) {
        Context context = getApplicationContext();
        TrackingActivity trackingActivity = new TrackingActivity();
//        Log.e("TAG", "shareWithWhatsApp: "+a );
        //Boolean checkInternet = checkInternetConnection(context);
        sharedPreferences = context.getSharedPreferences(settingsActivity.MyPreferences,
                0);
        SmsManager smsManager = SmsManager.getDefault();
        String message = sharedPreferences.getString(settingsActivity.Messages, null);
        String pContact = sharedPreferences.getString(settingsActivity.pContacts, null);
        String sContact = sharedPreferences.getString(settingsActivity.sContacts, null);
        try {
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            //sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, message);
            sendIntent.putExtra("id", pContact + "@s.whatsapp.net"); //phone number without "+" prefix
            sendIntent.putExtra("id", sContact + "@s.whatsapp.net");
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Error/n" + e.toString(), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onAccelerationChanged(float x, float y, float z) {

    }

    @Override
    public void onShake(float force) {
        Toast.makeText(getBaseContext(), "Motion detected  --- Shake harder and faster ---" + force, Toast.LENGTH_SHORT).show();

        for (force++; force > 30; ) {

            Toast.makeText(getBaseContext(), "Motion detected  --- Shake harder to activate emergency protocol", Toast.LENGTH_SHORT).show();

            if (force > 100) {
                sendMessage(getCurrentFocus());
                callHospital(getCurrentFocus());
                //shareWithWhatsApp(getCurrentFocus());
                break;
            } else {
                Toast.makeText(getBaseContext(), "Motion detected  --- Shake harder and faster ---" + force, Toast.LENGTH_SHORT).show();
                break;

            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Sensor", "Service destroy");

//Check device supported Accelerometer sensor or not
        if (AccelerometerManager.isListening()) {

//Start Accelerometer Listening
            AccelerometerManager.stopListening();

            Toast.makeText(getBaseContext(), "onDestroy Accelerometer Stopped",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_add_people:
                Intent intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
