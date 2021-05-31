package com.example.myapplication.recensione;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.myapplication.R;

public class RecensioniStudentInterne extends AppCompatActivity {
    TextView rateCount1, showRating1;
    EditText review1;
    Button submit1;
    RatingBar ratingbar1;
    float rateValue1; String temp1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recensioni_student_interne);
        rateCount1 = findViewById(R.id.ratecount);
        review1 = findViewById(R.id.Review);
        submit1 = findViewById(R.id.Submit1);
        ratingbar1 = findViewById(R.id.RatingBar);
        showRating1 = findViewById(R.id.showRating);

        ratingbar1.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rateValue1 = ratingbar1.getRating();
                if(rateValue1 <= 1  && rateValue1 > 0)
                    rateCount1.setText("Pessimo"+rateValue1 + "/5");

                else if (rateValue1 <= 2  && rateValue1 > 1)
                    rateCount1.setText("Discreto"+rateValue1 + "/5");

                else if (rateValue1 <= 3  && rateValue1 > 2)
                    rateCount1.setText("Buono"+rateValue1 + "/5");

                else if (rateValue1 <= 4  && rateValue1 > 3)
                    rateCount1.setText("Ottimo"+rateValue1 + "/5");

                else if (rateValue1 <= 5  && rateValue1 > 4)
                    rateCount1.setText("Eccelente"+rateValue1 + "/5");

            }
        });
        submit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp1 = rateCount1.getText().toString();
                showRating1.setText("Your Rating \n" + temp1 +"\n" + review1.getText());
                review1.setText("");
                ratingbar1.setRating(0);
                rateCount1.setText("");
            }
        });
    }
}



