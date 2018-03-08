package com.example.adam.recipeapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button createButton = (Button) findViewById(R.id.createBtn);
        Button viewButton = (Button) findViewById(R.id.viewBtn);

        createButton.setOnClickListener(this);
        viewButton.setOnClickListener(this);
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
                Intent viewIntent = new Intent(getApplicationContext(), RecipeFileBrowser.class);
                startActivity(viewIntent);
                break;
        }
    }
}
