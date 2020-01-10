package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    AppCompatEditText editText;
    TextView fileNameText;
    ProgressBar progressBar;
    Uri uriAudio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (AppCompatEditText) findViewById(R.id.songTitle);
        fileNameText = (TextView) findViewById(R.id.fileSelectedId);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);




    }
    //Open Audio files in Phone
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
            fileNameText.setText(fileName);
        }
    }

    private String getFileName(Uri uriAudio) {
        String result = null;
        if(uriAudio.getScheme().equals("content")){
            Cursor cursor = getContentResolver().query(uriAudio,null,null,null,null);
            try{
                if(cursor!=null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }finally {
                cursor.close();
            }
        }
        if(result == null){
            result = uriAudio.getPath();
            int cut = result.lastIndexOf('/');
            if(cut!= -1){
                result = result.substring(cut + 1);

            }
        }
        return result;
    }
}
