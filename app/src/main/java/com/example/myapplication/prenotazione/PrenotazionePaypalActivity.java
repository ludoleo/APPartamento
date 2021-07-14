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

import java.util.HashMap;
import java.util.Map;

public class PrenotazionePaypalActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PAYMENT = 1;
    EditText etAmount;
    Button btnPay;
    //TODO DA MODIFICARE SE CAMBIO LA DIRECTORY
                                    //metto indirizzo col quale sono collegato
    final static String IP_ADDRESS = "";
    final static String get_token = "http://"+IP_ADDRESS+"/Android/Braintree/main.php";
    final static String send_payment_details = "" + IP_ADDRESS + "/Android/Braintree/checkout.php";

    String token;
    String amount = "";
    String currency = "EUR";
    Map<String,String> parameters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prenotazione_paypal);
        etAmount = findViewById(R.id.etAmount);
        btnPay = findViewById(R.id.b_paypal);
        new  HttpRequest().execute();

    }
    public void back(View view) {
        Intent intent = new Intent(this, LeMiePrenotazioni.class);
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
              //  Log.i(TAG, "Result: " + paymentMethodNonce+ " amount "+amount); //write to log
                parameters = new HashMap<>(); // to make transaction
                parameters.put("amount", amount);
                parameters.put("payment_method_nonce", paymentMethodNonce);
                sendPaymentDetails();
            }else if (resultCode == RESULT_CANCELED) {
            // the user canceled
                //Toast.makeText(MainActivity.this, "Payment cancelled by user",
                  //      Toast.LENGTH_LONG).show();
                //Log.d(TAG, "user canceled"); //to write log
            } else{
                Exception error = (Exception)
                        data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
             //   Toast.makeText(MainActivity.this, "Error!!! Message: " + error.toString(),
               //         Toast.LENGTH_LONG).show();
               // Log.d(TAG, "Error : " + error.toString()); //write to log
            }
        } else {
          //  Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
           // Log.d(TAG, "Error : "); //write to log
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
                           // Toast.makeText(MainActivity.this, "Transaction successful", Toast.LENGTH_LONG).show();
                    // Go to next activity
                            startActivity(new Intent(PrenotazionePaypalActivity.this, PaymentDetails.class)
                                    .putExtra("Amount", amount)
                                    .putExtra("Currency", currency)
                                    .putExtra("Response", response));
                        }
                        else{

                        }
                           // Toast.makeText(MainActivity.this, "Transaction failed : " + response.toString(),
                                   // Toast.LENGTH_LONG).show();
                       // Log.i(TAG, "Final Response: " + response.toString()); //write to log
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            //    Log.i(TAG, "Volley error : " + error.toString());
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