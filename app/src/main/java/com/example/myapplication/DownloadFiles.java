package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class DownloadFiles extends AppCompatActivity {

    ListView listViewContratti;
    String[] contratti = {"STUDENTI","CEDOLARE"};
    //FIREBASE
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    StorageReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_files);
        listViewContratti = (ListView) findViewById(R.id.listViewContratti);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.row, R.id.textViewContratto,contratti);
        listViewContratti.setAdapter(arrayAdapter);
        //FACCIAMO UN LISTENER
        listViewContratti.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String contratto = (String) adapterView.getItemAtPosition(i);
                download(contratto);
            }
        });
    }

    private void download(String contratto) {

        storageReference =  firebaseStorage.getInstance().getReference();
        ref=storageReference.child("Contratti").child(contratto).child("contratto.pdf");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                downloadFiles(DownloadFiles.this, "contratto_"+contratto,".pdf",DIRECTORY_DOWNLOADS,url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void downloadFiles(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {

        DownloadManager downloadManager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);

        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName+fileExtension);
        downloadManager.enqueue(request);
    }
}