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
import android.widget.ListView;

import java.util.ArrayList;


/**
 * Created by adam on 2/28/18.
 */

public class Editor extends ListActivity {

    ArrayList<String> ingrList = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,ingrList);

        Button addButton = (Button) findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                EditText amountText = (EditText) findViewById(R.id.amount);
                EditText unitText = (EditText) findViewById(R.id.units);
                EditText ingrText = (EditText) findViewById(R.id.ingr);

                ingrList.add("x" + amountText.getText().toString() + " " + unitText.getText().toString() + " " + ingrText.getText().toString() );
                Log.d("list", ingrList.toString());

                amountText.setText("");
                unitText.setText("");
                ingrText.setText("");

                adapter.notifyDataSetChanged();


            }
        });

        setListAdapter(adapter);



        Button submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                Intent intent = new Intent(getApplicationContext(), Ingredient.class);
                intent.putExtra("Ingr_Data", ingrList);
                startActivity(intent);
            }
        });



    }

}
