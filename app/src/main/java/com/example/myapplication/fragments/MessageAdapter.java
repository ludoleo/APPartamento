package com.example.myapplication.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.classi.Chat;
import com.example.myapplication.classi.Studente;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;


    private Context mContext;
    private List<Chat> mChat;
    private String imgURL;

    FirebaseUser user;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imgURL) {
        this.mChat = mChat;
        this.mContext = mContext;
        this.imgURL = imgURL;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MSG_TYPE_RIGHT) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_destra, parent,false);
            return new MessageAdapter.ViewHolder(view);
        } else {

            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_sinistra, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

       Chat chat = mChat.get(position);

       holder.mostra_messaggio.setText(chat.getText_message());
        //SCORRO IL DB
        DatabaseReference myRef = FirebaseDatabase
                .getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference();
        myRef.child("Utenti").child("Studenti").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean flag = false;
                for (DataSnapshot studentiSnapshot : dataSnapshot.getChildren()) {
                    Studente stud = studentiSnapshot.getValue(Studente.class);
                    if(chat.getSender().compareTo(stud.getIdUtente())==0){
                        flag = true;
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                        StorageReference profileRef = storageReference.child("Studenti/"+chat.getSender()+"/profile.jpg");
                        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Log.i(TAG,"URI "+uri);
                                Picasso.get().load(uri).into(holder.profile_image);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                holder.profile_image.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
                            }
                        });
                    }
                }
                if(!flag){
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                    StorageReference profileRef = storageReference.child("Proprietari/"+chat.getSender()+"/profile.jpg");
                    profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //Log.i(TAG,"URI "+uri);
                            Picasso.get().load(uri).into(holder.profile_image);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                            holder.profile_image.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    @Override
    public int getItemCount() {
        return mChat.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mostra_messaggio;
        public ImageView profile_image;

        public ViewHolder (View itemView) {
            super(itemView);

            mostra_messaggio = itemView.findViewById(R.id.mostra_messaggio);
            profile_image = itemView.findViewById(R.id.profile_image);


        }
    }

    @Override
    public int getItemViewType(int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(user.getUid())) {
            return MSG_TYPE_RIGHT;
        }
        else
            return MSG_TYPE_LEFT;
    }
}
