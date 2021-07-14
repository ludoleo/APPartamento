package com.example.myapplication.prenotazione;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.interfaces.HttpResponseCallback;
import com.braintreepayments.api.internal.HttpClient;
import com.braintreepayments.api.models.PayPalRequest;
import com.example.myapplication.R;
import com.example.myapplication.home.Home;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class PrenotazionePaypalActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final String TAG = "Pagamen";
    EditText etAmount;
    Button btnPay;
    //TODO DA MODIFICARE SE CAMBIO LA DIRECTORY
                                    //metto indirizzo col quale sono collegato
    //final static String IP_ADDRESS = "192.168.47.175"; //CREA
    final static String IP_ADDRESS = "10.14.4.151";   //CASA
    final static String get_token = "http://"+IP_ADDRESS+"/Android/braintree_php/main.php";
    final static String send_payment_details = "http://" + IP_ADDRESS + "/Android/braintree_php/checkout.php";

    String token;
    String amount = "";
    Map<String,String> parameters;

    String id="";

    //Database
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prenotazione_paypal);
        etAmount = findViewById(R.id.etAmount);
        btnPay = findViewById(R.id.b_paypal);

        id = getIntent().getExtras().getString("id");
        database = FirebaseDatabase.getInstance("https://appartamento-81c2d-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();

        new  HttpRequest().execute();

    }
    public void back(View view) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    public void pagamentoTicket(View view) {
        sendPayment();
    }


    private class HttpRequest extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            HttpClient client = new HttpClient();
            client.get(get_token, new HttpResponseCallback() {
                @Override
                public void success(String responseBody) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(MainActivity.this, "Successfully got token", Toast.LENGTH_SHORT).show();
                        }
                    });
                    token = responseBody;
                }
                @Override
                public void failure(Exception exception) {
                    final Exception ex = exception;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(MainActivity.this, "Failed to get token: " + ex.toString(),
                              //      Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
            return null;
        }
    }

    public void sendPayment() {
        amount = etAmount.getText().toString();
        //Log.i(TAG, amount);
        PayPalRequest paypalRequest = new PayPalRequest(amount)
                .currencyCode("EUR")
                .intent(PayPalRequest.INTENT_SALE);
        DropInRequest dropInRequest = new DropInRequest()
                .paypalRequest(paypalRequest)
                .collectDeviceData(true)
                .clientToken(token);
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE_PAYMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == RESULT_OK) {
                DropInResult result =
                        data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                String paymentMethodNonce = result.getPaymentMethodNonce().getNonce();
               Log.i(TAG, "Result: " + paymentMethodNonce+ " amount "+amount); //write to log
                parameters = new HashMap<>(); // to make transaction
                parameters.put("amount", amount);
                parameters.put("payment_method_nonce", paymentMethodNonce);
                sendPaymentDetails();
            }else if (resultCode == RESULT_CANCELED) {
            // the user canceled
                Log.d(TAG, "user canceled"); //to write log
            } else{
                Exception error = (Exception)
                        data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                 Log.d(TAG, "Error : " + error.toString()); //write to log
            }
        } else {
              Log.d(TAG, "Error : "); //write to log
        }
    }

    // Finalize transaction
    private void sendPaymentDetails() {
        RequestQueue queue = Volley.newRequestQueue(PrenotazionePaypalActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, send_payment_details,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("Successful")){
                            //cambia la prenotazione in pagata
                            myRef.child("Prenotazioni").child(id).child("pagata").setValue(true);
                            Toast.makeText(PrenotazionePaypalActivity.this, "Transaction successful", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(PrenotazionePaypalActivity.this, Home.class);
                            startActivity(i);
                        }
                        else{
                            Log.i(TAG, "Final Response: " + response.toString());
                        }
                           // Toast.makeText(MainActivity.this, "Transaction failed : " + response.toString(),
                                   // Toast.LENGTH_LONG).show();
                       //write to log
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
             Log.i(TAG, "Volley error : " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                if (parameters == null)
                    return null;
                Map<String, String> params = new HashMap<>();
                for (String key : parameters.keySet()) {
                    params.put(key, parameters.get(key));
                }
                return params;
            }@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(stringRequest);
    }
}