package com.example.myapplication.prenotazione;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.myapplication.R;

public class PaymentDetails extends AppCompatActivity {

    TextView tvAmount, tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        tvAmount = (TextView)findViewById(R.id.tvAmount);
        tvResult = (TextView)findViewById(R.id.tvResult);

        Intent intent = getIntent();

        String paymentAmount = new String(intent.getStringExtra("Amount"));
        String paymentCurrency = new String(intent.getStringExtra("Currency"));
        String response = new String(intent.getStringExtra("Response"));
        showDetails(paymentAmount, paymentCurrency, response);
    }

    // Show result
    private void showDetails(String paymentAmount, String paymentCurrency, String response){
        try {
            tvAmount.setText(paymentAmount + " "+paymentCurrency);
            tvResult.setText(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}