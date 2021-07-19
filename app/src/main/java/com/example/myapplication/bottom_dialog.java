package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class bottom_dialog extends BottomSheetDialogFragment {
    private TextView Title,link, btn_visit;
    private ImageView close;
    private String fetchurl;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.bottom_dialog_qr,container,false);

    Title= view.findViewById(R.id.Text_qr_title);
    link=view.findViewById(R.id.txt_link);
     btn_visit= view.findViewById(R.id.visit_qr_link);
     close = view.findViewById(R.id.idclose);
     Title.setText(fetchurl);

     btn_visit.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent intent = new Intent("android.intent.action.VIEW");
             intent.setData(Uri.parse(fetchurl));
             startActivity(intent);


         }
     });
     close.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             dismiss();
         }
     });
     return view;
    }
    public void fetchurl(String url) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                fetchurl=url;

            }
        });

    }

}
