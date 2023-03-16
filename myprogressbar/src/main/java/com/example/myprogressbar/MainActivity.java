package com.example.myprogressbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private ProgressBar progressBar2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar=findViewById(R.id.pb);
        progressBar2=findViewById(R.id.pb2);
        Button button=findViewById(R.id.bu);
        TextView textView=findViewById(R.id.dragDown);
        textView.setVisibility(View.GONE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            int progress=progressBar2.getProgress();
            progress+=10;
            progressBar2.setProgress(progress);
            if(progress==progressBar2.getMax())
            {
                Log.i("你好","522");
            progressBar2.setIndeterminate(true);}
            }
        });
    }

    public void leoClick(View view) {
        if(progressBar.getVisibility()==View.GONE){
            progressBar.setVisibility(View.VISIBLE);
        }
        else{
            progressBar.setVisibility(View.GONE);
        }
    }
}