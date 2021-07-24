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
import com.example.myapplication.classi.Studente;
import com.example.myapplication.classi.Utente;
import com.example.myapplication.messaggi.MessaggiActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>  {

    private static final String TAG = "USER_ADAPTER";
    private Context mContext;
    private List<Utente> mUser;


    public UserAdapter(Context mContext, List<Utente> mUser) {
        this.mUser = mUser;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Utente user = mUser.get(position);
        Log.i(TAG,"USER é "+user.getEmail());
        holder.username.setText(user.getNome() + " " + user.getCognome());
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
                    if(user.getIdUtente().compareTo(stud.getIdUtente())==0){
                        flag = true;
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                        StorageReference profileRef = storageReference.child("Studenti/"+user.getIdUtente()+"/profile.jpg");
                        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.i(TAG,"URI "+uri);
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
                    StorageReference profileRef = storageReference.child("Proprietari/"+user.getIdUtente()+"/profile.jpg");
                    profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.i(TAG,"URI "+uri);
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, MessaggiActivity.class);
                Log.i(TAG,"USER che invio a messaggi activity "+user.getIdUtente());
                intent.putExtra("userId", user.getIdUtente());
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mUser.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public ImageView profile_image;

        public ViewHolder (View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
        }
    }
}

