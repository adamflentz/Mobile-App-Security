package com.example.adam.recipeapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class RecipeViewer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        String filename = "recipejson";
        JSONObject recipeJSON = new JSONObject();
        JSONObject individualRecipe = new JSONObject();
        JSONObject ingredient = new JSONObject();
        try{
            ingredient.put("name", "Kosher Salt");
            ingredient.put("value", "3");
            ingredient.put("measurement", "pinch");
        }
        catch (JSONException e){Log.e("JSON", "JSON not created");}
        List<JSONObject> IngredientList = new ArrayList<JSONObject>();
        IngredientList.add(ingredient);
        JSONArray directionsArray = new JSONArray();
        directionsArray.put("Eat 3 pinches of Kosher Salt");
        directionsArray.put("That's so gross why did you do that");
        try {
            individualRecipe.put("name", "Salt Pie (gluten free)");
            individualRecipe.put("ingredients", IngredientList);
            individualRecipe.put("directions", directionsArray);
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
                inputJSON = inputJSON.getJSONObject("recipe");
                Log.e("JSON", inputJSON.toString());
                String Name = inputJSON.getString("name");
                String Picture = inputJSON.getString("picture");
                JSONArray Ingredients = new JSONArray(inputJSON.getString("ingredients"));
                JSONArray RecipeDirections = new JSONArray(inputJSON.getString("directions"));
                List<String> finalDirections = new ArrayList<String>();
                ListView listViewDirections = (ListView) findViewById(R.id.directions);
                for(int n = 0; n < RecipeDirections.length(); n++){
                    String Direction = RecipeDirections.getString(n);
                    finalDirections.add(Direction);
                }
                TextView name = (TextView) findViewById(R.id.name);
                name.setText(Name);
                List<String> finalIngredients = new ArrayList<String>();
                ListView ingredients = (ListView) findViewById(R.id.ingredients);
                for(int n = 0; n < Ingredients.length(); n++){
                    JSONObject IndividualIngredient = Ingredients.getJSONObject(n);
                    String listViewIngredient = IndividualIngredient.getString("value") + " " +
                            IndividualIngredient.getString("measurement") + " " +
                            IndividualIngredient.getString("name");
                    finalIngredients.add(listViewIngredient);
                }
                ArrayAdapter<String> ingredientAdapter = new ArrayAdapter<String>(
                        this, android.R.layout.simple_list_item_1, finalIngredients
                );
                ArrayAdapter<String> directionAdapter = new ArrayAdapter<String>(
                        this, android.R.layout.simple_list_item_1, finalDirections
                );
                ingredients.setAdapter(ingredientAdapter);
                listViewDirections.setAdapter(directionAdapter);


            } catch (Exception e) {
                e.printStackTrace();
            }
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


    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

}
