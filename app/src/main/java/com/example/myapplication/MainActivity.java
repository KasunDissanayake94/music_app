package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    AppCompatEditText editText;
    TextView songName;
    ProgressBar progressBar;
    Uri uriAudio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (AppCompatEditText) findViewById(R.id.songTitle);
        songName = (TextView) findViewById(R.id.fileSelectedId);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);




    }
    public void openAudioFile(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent , 101);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 101 && resultCode == RESULT_OK && data.getData() != null){
            uriAudio = data.getData();
            String fileName  = getFileName(uriAudio);
        }
    }

    private String getFileName(Uri uriAudio) {
        String result = null;
        if(uriAudio.getScheme().equals("content")){
            Cursor cursor = getContentResolver().query(uriAudio,null,null,null,null);

        }
    }
}
