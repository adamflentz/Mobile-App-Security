package com.example.adam.recipeapp;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adam on 2/28/18.
 */

public class CameraPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_page);

        Bundle bundle = getIntent().getExtras();
        final ArrayList<String> ingrList = (ArrayList<String>) bundle.getStringArrayList("Ingr_Data");
        final ArrayList<String> instrList = (ArrayList<String>) bundle.getStringArrayList("Instruct_Data");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            }
            else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            }
            else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
            }
        }


        Button submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click

                EditText nameText = (EditText) findViewById(R.id.name);

                String filename = nameText.toString();
                JSONObject recipeJSON = new JSONObject();
                JSONObject individualRecipe = new JSONObject();
                JSONObject ingredient = new JSONObject();
                try{
                    ingredient.put("name", "Kosher Salt");
                    ingredient.put("value", "3");
                    ingredient.put("measurement", "pinch");
                }
                catch (JSONException e){
                    Log.e("JSON", "JSON not created");}
                List<JSONObject> IngredientList = new ArrayList<JSONObject>();
                IngredientList.add(ingredient);
                List<String> Directions = new ArrayList<String>();
                Directions.add("Eat 3 pinches of Kosher Salt");
                try {
                    individualRecipe.put("name", "Salt Pie (gluten free)");
                    individualRecipe.put("ingredients", IngredientList);
                    individualRecipe.put("directions", Directions);
                    individualRecipe.put("picture", "salt.jpeg");
                    recipeJSON.put("recipe", individualRecipe);
                }
                catch (JSONException e){Log.e("JSON", "JSON not created");}
                if(isExternalStorageWritable() == true && isExternalStorageReadable() == true){
                    File OutputFile = saveJSONToStorageDir(filename, recipeJSON);
                    try {
                        FileInputStream recipeReader = new FileInputStream(OutputFile);
                        byte[] recipeData = new byte[(int) OutputFile.length()];
                        recipeReader.read(recipeData);
                        recipeReader.close();
                        String recipeString = new String(recipeData, "UTF-8");
                        JSONObject inputJSON = new JSONObject(recipeString);
                        Log.e("JSON", inputJSON.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });





    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public File saveJSONToStorageDir(String recipeName, JSONObject recipe) {
        File filepath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), recipeName);
        File file = new File(filepath, recipeName + ".json");
        try{
            filepath.mkdirs();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        try{

            file.createNewFile();
            FileOutputStream recipeOut = new FileOutputStream(file);
            OutputStreamWriter writeRecipe = new OutputStreamWriter(recipeOut);
            writeRecipe.append(recipe.toString());
            writeRecipe.close();
            recipeOut.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return file;
    }
}