package com.example.music_player_lite;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;

    ActionBar actionBar; // Defining the actionbar to change its background color
    ColorDrawable colorDrawable; // ColorDrawable object where i am going to store my color value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listView = findViewById(R.id.listView);

        colorDrawable = new ColorDrawable(Color.parseColor("#FF000000")); // defining color value for action bar
        actionBar = getSupportActionBar(); // getting the action bar to apply custom color
        actionBar.setBackgroundDrawable(colorDrawable); // setting the back ground color for the action bar hello





        Dexter.withContext(this)                    //Import Dexter Library into Gradle Script --> build.gradle(Module:Music_Player_lite)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {

                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                        Log.d("ExternalStorage","Permission Given To Access External Storage");
                        ArrayList<File> songs = getSongs(Environment.getExternalStorageDirectory());            // This is how you fetch files from the SD Card
                        String[] items = new String[songs.size()];                                              // Getting a array full of the song name(String) without .mp3 at the end
                        for (int i = 0; i < items.length; i++) {
                            items[i] = songs.get(i).getName().replace(".mp3", "");
                        }
                        ArrayAdapter<String> Adp = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, items); // Making Array Adapter for ListView
                        listView.setAdapter(Adp);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {               // When we select each song displayed in the listView this happens
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(MainActivity.this, Player.class);     // Making a explicit intent to take us to the page where the song will play
                                String CurrentSongString = listView.getItemAtPosition(position).toString();
                                intent.putExtra("SongList", songs); // Returns all the songs as a object of File class
                                intent.putExtra("CurrentSongString", CurrentSongString);
                                intent.putExtra("Position", position);
                                startActivity(intent);

                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Log.d("ExternalStorage","Permission denied To Access External Storage");
                        Toast.makeText(MainActivity.this, "Permission Denied ,You Cannot use this App", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest(); // Asks for permission from the user in the Runtime
                    }
                })
                .check();


    }

    public ArrayList<File> getSongs(File file) {    // Fetching song files from a folder or directory
        ArrayList<File> arrayList = new ArrayList<>();
        File[] songs = file.listFiles(); //
        if (songs != null) {
            for (File A : songs) {
                if (!A.isHidden() && A.isDirectory()) {
                    arrayList.addAll(getSongs(A));
                } else {
                    if (A.getName().endsWith(".mp3") && !A.getName().startsWith(".")) {            // Including .endsWith(".mp3") to get mp3 files and
                        arrayList.add(A);                                                          // Including .startsWith(".") to not clash with other android .mp3 file
                    }
                }
            }

        }
        return arrayList;
    }

}