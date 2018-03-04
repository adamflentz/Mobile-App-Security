package com.example.adam.recipeapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button createButton = (Button) findViewById(R.id.createBtn);
        Button viewButton = (Button) findViewById(R.id.viewBtn);

        createButton.setOnClickListener(this);
        viewButton.setOnClickListener(this);


//        createButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // Do something in response to button click
//                Intent intent = new Intent(getApplicationContext(), Editor.class);
//                startActivity(intent);
//            }
//        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.createBtn:
                Toast.makeText(this, "Create Recipe button clicked", Toast.LENGTH_SHORT).show();
                Intent createIntent = new Intent(getApplicationContext(), Editor.class);
                startActivity(createIntent);
                break;
            case R.id.viewBtn:
                Toast.makeText(this, "View Recipes clicked", Toast.LENGTH_SHORT).show();
                Intent viewIntent = new Intent(getApplicationContext(), RecipeViewer.class);
                startActivity(viewIntent);
                break;
        }
    }
}
