package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.model.UploadSong;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    AppCompatEditText editTextTitle;
    TextView fileNameText;
    ProgressBar progressBar;
    Uri uriAudio;
    StorageReference mStorageRef;
    StorageTask storageTask;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTitle = (AppCompatEditText) findViewById(R.id.songTitle);
        fileNameText = (TextView) findViewById(R.id.fileSelectedId);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("songs");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("songs");




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
        if(requestCode == 101 && resultCode == RESULT_OK && data.getData() != null){
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

    public void uploadFileToFirebase(View view){

        if(fileNameText.getText().toString().equals("No file selected")){
            Toast.makeText(getApplicationContext(),"Please select an File", Toast.LENGTH_LONG).show();
        }else{
            uploadFile();
        }
    }

    private void uploadFile(){
        if(uriAudio != null){
            String duration;
            Toast.makeText(getApplicationContext(),"Uploading Please wait...",Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.VISIBLE);
            StorageReference storageReference = mStorageRef.child(System.currentTimeMillis()+"."+getFileExtention(uriAudio));
            int durationMill = findSongDuration(uriAudio);

            if(durationMill == 0){
                duration = "NA";

            }
            duration = getDurationFromMill(durationMill);
            final String finalDuration = duration;

            storageTask = storageReference.putFile(uriAudio)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            UploadSong uploadSong = new UploadSong(editTextTitle.getText().toString(),finalDuration,uriAudio.toString());

                            String songId = databaseReference.push().getKey();
                            databaseReference.child(songId).setValue(uploadSong);

                        }
                    })

            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int)progress);

                }
            });

        }else{
            Toast.makeText(getApplicationContext(),"No file selected to Upload!",Toast.LENGTH_LONG).show();
        }

    }

    private String getDurationFromMill(int durationMill) {
        Date date = new Date(durationMill);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
        String myTime = simpleDateFormat.format(date);
        return myTime;
    }

    private int findSongDuration(Uri uriAudio) {
        int timeInMill = 0;

        try{
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this,uriAudio);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            timeInMill = Integer.parseInt(time);

            retriever.release();
            return timeInMill;
        }catch (Exception e){
            e.printStackTrace();
            return 0;

        }

    }

    private String  getFileExtention(Uri uriAudio) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uriAudio));
    }
}
