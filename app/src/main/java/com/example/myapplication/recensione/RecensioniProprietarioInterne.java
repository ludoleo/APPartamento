package com.example.myapplication.recensione;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.myapplication.R;

public class RecensioniProprietarioInterne extends AppCompatActivity {
    TextView rateCount, showRating;
    EditText review;
    Button submit;
    RatingBar ratingbar;
    float rateValue; String temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recensioni_proprietario_interne);
        rateCount = findViewById(R.id.ratecount);
        review = findViewById(R.id.Review);
        submit = findViewById(R.id.Submit);
        ratingbar = findViewById(R.id.RatingBar);
        showRating = findViewById(R.id.showRating);

        ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rateValue = ratingbar.getRating();
                if(rateValue <= 1  && rateValue > 0)
                    rateCount.setText("Pessimo"+rateValue + "/5");

                else if (rateValue <= 2  && rateValue > 1)
                    rateCount.setText("Discreto"+rateValue + "/5");

                else if (rateValue <= 3  && rateValue > 2)
                    rateCount.setText("Buono"+rateValue + "/5");

                else if (rateValue <= 4  && rateValue > 3)
                    rateCount.setText("Ottimo"+rateValue + "/5");

                else if (rateValue <= 5  && rateValue > 4)
                    rateCount.setText("Eccelente"+rateValue + "/5");

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp = rateCount.getText().toString();
                showRating.setText("Your Rating \n" + temp +"\n" + review.getText());
                review.setText("");
                ratingbar.setRating(0);
                rateCount.setText("");
            }
        });
    }
}



