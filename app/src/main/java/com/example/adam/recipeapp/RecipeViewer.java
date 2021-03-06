package com.example.adam.recipeapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class RecipeViewer extends AppCompatActivity {
    List<String> finalIngredients = new ArrayList<String>();
    List<String> finalDirections = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_viewer);
        try{
            String inputJSONString = bundle.getString("JSONrecipe");
            JSONObject JSONRecipe = new JSONObject(inputJSONString);
            JSONObject inputJSON = JSONRecipe.getJSONObject("recipe");
            final String Name = inputJSON.getString("name");
            final String Picture = inputJSON.getString("picture");
            JSONArray Ingredients = new JSONArray(inputJSON.getString("ingredients"));
            JSONArray RecipeDirections = new JSONArray(inputJSON.getString("directions"));
            ListView listViewDirections = (ListView) findViewById(R.id.directions);
            for(int n = 0; n < RecipeDirections.length(); n++){
                String Direction = RecipeDirections.getString(n);
                finalDirections.add(Direction);
            }
            TextView name = (TextView) findViewById(R.id.name);
            name.setText(Name);
            ListView ingredients = (ListView) findViewById(R.id.ingredients);
            for(int n = 0; n < Ingredients.length(); n++){
                String IndividualIngredient = Ingredients.getString(n);
                finalIngredients.add(IndividualIngredient);
            }
            Button submitButton = (Button) findViewById(R.id.email);
            submitButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    composeEmail(Name, finalIngredients, finalDirections, Picture);
                }
            });
            ArrayAdapter<String> ingredientAdapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_list_item_1, finalIngredients
            );
            ArrayAdapter<String> directionAdapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_list_item_1, finalDirections
            );
            ingredients.setAdapter(ingredientAdapter);
            listViewDirections.setAdapter(directionAdapter);

            }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public File saveJSONToStorageDir(String recipeName, JSONObject recipe) {
        File filepath = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "recipejson");
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


    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public void composeEmail(String subject, List<String> Ingredients, List<String> Directions, String Picture) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("application/image");
        StringBuilder finalString = new StringBuilder();
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        String picturePath = "file://" + Picture;
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(picturePath));
        finalString.append("Ingredients:");
        finalString.append('\n');
        for(int n = 0; n < Ingredients.size(); n++){
            finalString.append(Ingredients.get(n));
            if(n != Ingredients.size() - 1) {
                finalString.append('\n');
            }
        }
        finalString.append('\n');
        finalString.append("Directions:");
        finalString.append('\n');
        for(int n = 0; n < Directions.size(); n++){
            finalString.append(Directions.get(n));
            if(n != Directions.size() - 1) {
                finalString.append('\n');
            }
        }
        intent.putExtra(Intent.EXTRA_TEXT, finalString.toString());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}
