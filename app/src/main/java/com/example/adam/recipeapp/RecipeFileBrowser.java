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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

public class RecipeFileBrowser extends AppCompatActivity {


    List<JSONObject> JSONRecipes = new ArrayList<JSONObject>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_file_browser);

        //on start of the file listing, we need to get permission to read external storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            }
        }

        //Get files in directory
        File[] recipeFilesList = getRecipeFiles();
        if(recipeFilesList == null || recipeFilesList.length == 0){
            //print error about no existing files, return to main activity
            Toast.makeText(this, "Couldn't find recipe directory, or directory was empty", Toast.LENGTH_SHORT).show();
            this.onBackPressed();
        }
        try {
            Toast.makeText(this, "Number of files:" + recipeFilesList.length, Toast.LENGTH_SHORT).show();
            wait(2000);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        convertToJSON(recipeFilesList);
        Toast.makeText(this, "Number of JSON Files:" + JSONRecipes.size(), Toast.LENGTH_SHORT).show();

        //now we need interpret the JSON with our custom adapter and try to populate our custom view
        populateListView();
        //register our ListView items to call the appropriate intent and pass it the appropriate JSONString
        registerClickCallback();
    }

    //Just parse our directory for all the files
    protected File[] getRecipeFiles(){
        try {
            File recipeDirectory = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                    "recipejson"
            );
            return recipeDirectory.listFiles();
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //We're gonna generate a cursor/loader to populate our ListView
    protected void convertToJSON(File[] recipes) {
        for(int i = 0; i < recipes.length; i++) {

            //Naive approach: interpret all files in our private directory in external memory
            //as strings, convert strings to JSONObjects

            String jsonString = null;
            FileInputStream stream;
            FileChannel channel;
            try {
                //use an input stream to read every character of the file into jsonString
                stream = new FileInputStream(recipes[i]);
                channel = stream.getChannel();
                MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY,
                        0, channel.size());
                jsonString = Charset.defaultCharset().decode(buffer).toString();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (jsonString.startsWith("[")){
                jsonString.replaceFirst("\\[]", "");
                jsonString = jsonString.substring(0, jsonString.length() - 1);
            }
            Log.d("JSONFile", jsonString);
            //Now turn your string into an JSONObject and add it to our JSON ArrayList
            if(jsonString != null) {
                try {
                    JSONObject recipe = new JSONObject(jsonString);
                    JSONRecipes.add(recipe);
                } catch (JSONException e) {
                    Log.e("JSON", "Could not generate JSON object, skip recipe");
                }
            }
        }
    }

    protected void populateListView(){
        ArrayAdapter<JSONObject> JSONadapter = new MyAdapter();
        ListView list = (ListView) findViewById(R.id.FileList);
        list.setAdapter(JSONadapter);
    }

    private class MyAdapter extends ArrayAdapter<JSONObject> {
        public MyAdapter(){
            super(RecipeFileBrowser.this, R.layout.recipe_layout, JSONRecipes);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View recipeView = convertView;
            if (recipeView == null){
                recipeView = getLayoutInflater()
                        .inflate(R.layout.recipe_layout, parent, false);
            }

            JSONObject recipe = JSONRecipes.get(position);
            ImageView thumbnail =  (ImageView)recipeView.findViewById(R.id.item_recipeThumbnail);
            TextView nameText = (TextView) recipeView.findViewById(R.id.item_recipeTitle);
            try{
                File image = new File (recipe.getJSONObject("recipe").getString("picture"));
                Bitmap thumbnailBitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
                thumbnail.setImageBitmap(thumbnailBitmap);
            }
            catch(JSONException e){
                e.printStackTrace();
            }

            try{
                nameText.setText(recipe.getJSONObject("recipe").getString("name"));
            }
            catch(JSONException e){
                nameText.setText("Recipe Title Not Found");
            }
            return recipeView;
        }
    }

    //Creates a onItemClick listener, onClick starts the ViewRecipe activity.
    // The onClick converts the JSONRecipes object that matches the ListView item position,
    //converts that JSONObject to a string, and adds that as an intent extra
    private void registerClickCallback(){
        ListView list = (ListView) findViewById(R.id.FileList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> paret, View viewClicked, int position, long id){
                RelativeLayout clicked = (RelativeLayout) viewClicked;
                TextView textView = (TextView) clicked.getChildAt(1);
                String message = "You Clicked " + textView.getText().toString();
                Log.e("Message",message);
                Intent intent = new Intent(getApplicationContext(), RecipeViewer.class);
                intent.putExtra("JSONrecipe",JSONRecipes.get(position).toString());
                Toast.makeText(RecipeFileBrowser.this, message, Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
    }
}
