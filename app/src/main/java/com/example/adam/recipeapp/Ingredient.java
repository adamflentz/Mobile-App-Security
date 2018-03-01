package com.example.adam.recipeapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by adam on 2/28/18.
 */

public class Ingredient extends ListActivity {

    ArrayList<String> instrList = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);

        Bundle bundle = getIntent().getExtras();
        final ArrayList<String> ingrList = (ArrayList<String>) bundle.getStringArrayList("Ingr_Data");

        Log.d("list", ingrList.toString());

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,instrList);

        Button addButton = (Button) findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                EditText instrText = (EditText) findViewById(R.id.instruct);


                instrList.add(instrText.getText().toString() );
                Log.d("list", instrList.toString());

                instrText.setText("");


                adapter.notifyDataSetChanged();


            }
        });

        setListAdapter(adapter);









        Button submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("Ingr_Data", ingrList);
                intent.putExtra("Instruct_Data", instrList);
                startActivity(intent);
            }
        });
    }

}
