package com.example.project_library;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView1;

    @SuppressLint({"ClickableViewAccessibility", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView1 = findViewById(R.id.imageView1);

        imageView1.setOnTouchListener((v, event) -> {
            int action = event.getAction();

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // This is where you can increase the size of your ImageView
                    imageView1.setScaleX(1.1f); // Increase width by 20%
                    imageView1.setScaleY(1.1f); // Increase height by 20%
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    break;
                case MotionEvent.ACTION_UP:
                    // This is where you can restore the original size of your ImageView
                    imageView1.setScaleX(1.0f); // Restore original width
                    imageView1.setScaleY(1.0f); // Restore original height
                    break;
            }
            return true;
        });
    }
}