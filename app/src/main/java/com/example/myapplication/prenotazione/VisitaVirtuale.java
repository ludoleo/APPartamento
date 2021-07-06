package com.example.myapplication.prenotazione;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.example.myapplication.classi.Utente;
import com.google.firebase.auth.FirebaseAuth;
import com.sendbird.calls.AcceptParams;
import com.sendbird.calls.AudioDevice;
import com.sendbird.calls.AuthenticateParams;
import com.sendbird.calls.CallOptions;
import com.sendbird.calls.DialParams;
import com.sendbird.calls.DirectCall;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.calls.SendBirdException;
import com.sendbird.calls.handler.DialHandler;
import com.sendbird.calls.handler.DirectCallListener;
import com.sendbird.calls.handler.SendBirdCallListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class VisitaVirtuale extends AppCompatActivity {


    private static final int MY_PERMISSIONS_RECORD_AUDIO = 1 ;
    private static final String APP_ID = "67EEFB30-29D0-4E6A-8F5B-BDD9171E120D" ;
    private static final String UNIQUE_HANDLER_ID = "" ;
    private static final String CALLEE_ID = "";


    //Map<String, String> customItemsToAdd = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestAudioPermissions();

        // Initialize SendBirdCall instance to use APIs in your app.
        SendBirdCall.init(getApplicationContext(), APP_ID);

        setContentView(R.layout.activity_visita_virtuale);
        // GoogleMeet = (TextView) findViewById(R.id.GoogleMeet);


        DialParams params = new DialParams(CALLEE_ID);
        params.setVideoCall(true);
        params.setCallOptions(new CallOptions());

        DirectCall call = SendBirdCall.dial(params, new DialHandler() {
            @Override
            public void onResult(DirectCall call, SendBirdException e) {
                if (e == null) {
                    // The call has been created successfully.
                }
            }
        });

        SendBirdCall.addListener(UNIQUE_HANDLER_ID, new SendBirdCallListener() {
            @Override
            public void onRinging(DirectCall call) {
                call.setListener(new DirectCallListener() {
                    @Override
                    public void onEstablished(DirectCall call) {
                    }

                    @Override
                    public void onConnected(DirectCall call) {
                    }

                    @Override
                    public void onEnded(DirectCall call) {
                        // End a call
                        call.end();
                    }

                    @Override
                    public void onRemoteAudioSettingsChanged(DirectCall call) {

                        if (call.isRemoteAudioEnabled()) {
                            // The remote user has been unmuted.
                            // Unmute my microphone
                            call.unmuteMicrophone();
                            // Display an unmuted icon.
                        } else {
                            // The remote user has been muted.
                            // Mute my microphone
                            call.muteMicrophone();
                            // Display and toggles a muted icon.
                        }
                    }

                    @Override
                    public void onRemoteVideoSettingsChanged(DirectCall call) {
                        if (call.isRemoteVideoEnabled()) {
                            // The remote user has started video.
                            // Start to show video
                            call.startVideo();
                        } else {
                            // The remote user has stopped video.
                            // Stop showing video
                            call.stopVideo();
                        }
                    }
                });

                call.accept(new AcceptParams());
            }
        });

        /*
        customItemsToAdd.put("key1", "value1");
        customItemsToAdd.put("key2", "value2");
        call.updateCustomItems(customItemsToAdd, (addedCustomItems, affectedKeys, e) -> {
            // Handle added custom items.
        });


         */

        call.setListener(new DirectCallListener() {
            @Override
            public void onEstablished(DirectCall call) {
            }

            @Override
            public void onConnected(DirectCall call) {
            }

            @Override
            public void onEnded(DirectCall call) {
            }

            @Override
            public void onRemoteAudioSettingsChanged(DirectCall call) {
            }


            @Override
            public void onReconnecting(DirectCall call) {
            }

            @Override
            public void onReconnected(DirectCall call) {
            }

            @Override
            public void onAudioDeviceChanged(DirectCall call, AudioDevice currentAudioDevice, Set<AudioDevice> availableAudioDevices) {
            }

            @Override
            public void onRemoteRecordingStatusChanged(DirectCall call) {
            }
        });
    }








    // SendBirdUser.java
    class SendBirdUser {
        private Utente chatUser;
        private com.sendbird.calls.User callUser;

        public SendBirdUser(Utente chatUser, com.sendbird.calls.User callUser) {
            this.chatUser = chatUser;
            this.callUser = callUser;
        }

        public Utente getChatUser() {
            return chatUser;
        }

        public com.sendbird.calls.User getCallUser() {
            return callUser;
        }
    }




    private void initUI() {
        // link a google
        // Linkify.addLinks(GoogleMeet,Linkify.WEB_URLS);

    }
    //Create placeholder for user's consent to record_audio permission.
//This will be used in handling callback

    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {

            //Go ahead with recording audio now
        }
    }



    //Handling callback
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permissions Denied to record audio", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }



}