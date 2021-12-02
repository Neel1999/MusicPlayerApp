package com.example.music_player_lite;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;


public class Player extends AppCompatActivity {
    @Override
    protected void onDestroy() { // Stopping the Activity, media player and "thread which tracks the song with the seekbar" when the user clicks on the back button
        super.onDestroy();
        mediaPlayer.stop();
        threadSeeking.interrupt();
    }

    TextView songNameTextView ;
    ImageView song_previous, song_playPause, song_next;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;

    ActionBar actionBar; // Defining the actionbar to change its background color
    ColorDrawable colorDrawable; // ColorDrawable object where i am going to store my color value


    ArrayList<File> songs_Array;
    String song_name;
    int position;
    Thread threadSeeking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        colorDrawable = new ColorDrawable(Color.parseColor("#FF000000")); // defining color value for action bar
        actionBar = getSupportActionBar(); // getting the action bar to apply custom color
        actionBar.setBackgroundDrawable(colorDrawable); // setting the back ground color for the action bar

        // Declarations of Objects Created
        setContentView(R.layout.activity_player);
        songNameTextView = findViewById(R.id.songNameTextView);
        song_previous = findViewById(R.id.song_previous);
        song_next = findViewById(R.id.song_next);
        song_playPause = findViewById(R.id.song_playPause);
        seekBar = findViewById(R.id.seekBar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs_Array = (ArrayList) bundle.getParcelableArrayList("SongList"); // ArrayList of All song file objects Extracted using bundle Class
        song_name = intent.getStringExtra("CurrentSongString"); // Getting the name of the current Song
        songNameTextView.setText(song_name);                          // Setting the song name in the text view
        position = intent.getIntExtra("Position",0); // Getting position of current song
        Uri uri = Uri.parse(songs_Array.get(position).toString());   // Getting the file location of the current song using position and Uri class in the ArrayList<File> of songs
        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);    // Creating a media player using the song location we just got
        mediaPlayer.start();                                         // Playing the Song


        // Setting on click listeners on the image view which we can do

        // Pause play imageview
        song_playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mediaPlayer.isPlaying()){
                    song_playPause.setImageResource(R.drawable.song_play);
                    mediaPlayer.pause();
                }else{
                    song_playPause.setImageResource(R.drawable.song_pause);
                    mediaPlayer.start();
                }
            }
        });

        //Changing song to next song by clicking the imageview
        song_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();                                 // Stop and Release the media player (if you dont
                mediaPlayer.release();                              // do this then the songs will start playing on top of each other)
                if(position != songs_Array.size()-1){
                    position ++;
                }else{
                    position = 0;    //// the song after the last element of the array will be the first element of the array (circular list)
                }
                Uri uri = Uri.parse(songs_Array.get(position).toString());  // Getting uri of the new song , to the corresponding position
                song_name = songs_Array.get(position).getName();            // Getting name of the new song , to the corresponding position
                songNameTextView.setText(song_name);                        // Setting new song name in the textview
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);   // Creating a new media player to play the new song , putting the new uri variable assigned before
                mediaPlayer.start();                                        // Starting the new Song
            }
        });

        //Changing song to previous song by clicking the imageview
        song_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();                         // Stop and Release the media player (if you dont
                mediaPlayer.release();                      // do this then the songs will start playing on top of each other)
                if(position != 0){
                    position --;
                }else{
                    position = songs_Array.size() -1;   // the song before the first element of the array will be the last element of the array (circular list)
                }
                Uri uri = Uri.parse(songs_Array.get(position).toString());  // Getting uri of the new song , to the corresponding position
                song_name = songs_Array.get(position).getName();            // Getting name of the new song , to the corresponding position
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);   // Creating a new media player to play the new song , putting the new uri variable assigned before
                mediaPlayer.start();                                        // Starting the new Song
                songNameTextView.setText(song_name);                        // Setting new song name in the textview
            }
        });


        seekBar.setMax(mediaPlayer.getDuration());                                      // Setting maximum of the seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        threadSeeking = new Thread(){        // Making Thread that will track the progress of the song and set that progress to the seekbar
            @Override
            public void run() {
                int currentPosition = 0;
                while(currentPosition < mediaPlayer.getDuration()){
                    currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    try{
                        sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        threadSeeking.start(); // Starting the Thread 
        //hello this is me in the future commiting something

    }
}