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
    List<JSONObject> JSONRecipes = new ArrayList<JSONObject>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button createButton = (Button) findViewById(R.id.createBtn);
        Button viewButton = (Button) findViewById(R.id.viewBtn);

        createButton.setOnClickListener(this);
        viewButton.setOnClickListener(this);

        //on start of the file listing, we need to get permission to read external storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            }
        }
        //Check for directory
        File recipeDirectory = getRecipeDirectory();
        if(recipeDirectory == null){
            //return to main activity with error message regarding nonexistant directory, we might be better served by registering this as a return from the stack
            Toast.makeText(this, "Could not find recipe directory",Toast.LENGTH_SHORT).show();
            Intent returnIntent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(returnIntent);
        }
        //Get files in directory
        File[] recipeFilesList = getRecipeFiles(recipeDirectory);
        if(recipeFilesList.length == 0){
            //print error about no existing files, return to main activity
            Toast.makeText(this, "No files within Recipe Directory", Toast.LENGTH_SHORT).show();
            Intent returnIntent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(returnIntent);
        }
        convertToJSON(recipeFilesList);

        //now we need interpret the JSON with our custom adapter and try to populate our custom view
        populateListView();
        //register our ListView items to call the appropriate intent and pass it the appropriate JSONString
        registerClickCallback();


    }

    //Method to get/check our Recipe directory
    protected File getRecipeDirectory(){
        try{
            File recipeDirectory = new File(Environment.getExternalStorageDirectory(), "recipeFolder");
            return recipeDirectory;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //Just parse our directory for all the files
    protected File[] getRecipeFiles(File directory){
        File[] recipes = directory.listFiles();
        return recipes;
    }

    //We're gonna generate a cursor/loader to populate our ListView
    protected void convertToJSON(File[] recipes) {
        for(int i = 0; i < recipes.length; i++){

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
            }
            catch(IOException e) {
                e.printStackTrace();
            }

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
        JSONObject myJSON;
        String[] content = {"0","1"};
        String jString = "{'name1':'smoked salmon','name2':'mashed potatoes'}";
        try {
            myJSON = new JSONObject(jString);
            content[0] = myJSON.getString("name1");
            content[1] = myJSON.getString("name2");
        }
        catch(JSONException e){
            Toast.makeText(this, "Your JSON Failed",Toast.LENGTH_SHORT).show();
            Log.e("JSON","JSON FAILED");
            e.printStackTrace();
        }
        /* Array adapter implementation that assumes items in the JSONObject have already been parsed.
           Implementation kept until the custom adapter functionality is confirmed

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.recipe_layout,
                content);
        */

        ArrayAdapter<JSONObject> JSONadapter = new MyAdapter();
        ListView list = (ListView) findViewById(R.id.FileList);
        list.setAdapter(JSONadapter);
    }

    private class MyAdapter extends ArrayAdapter<JSONObject>{
        public MyAdapter(){
            super(MainActivity.this, R.layout.recipe_layout, JSONRecipes);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View recipeView = convertView;
            if (recipeView == null){
                recipeView = getLayoutInflater()
                        .inflate(R.layout.recipe_layout, parent, false);
            }
            //Get the JSONObject, set the attributes of recipeView by the image and name
            //referenced by the JSONObject
            JSONObject recipe = JSONRecipes.get(position);
            ImageView thumbnail =  (ImageView)recipeView.findViewById(R.id.item_recipeThumbnail);
            TextView nameText = (TextView) recipeView.findViewById(R.id.item_recipeTitle);
            try{
                File image = new File (recipe.getString("picture"));
                Bitmap thumbnailBitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
                thumbnail.setImageBitmap(thumbnailBitmap);
            }
            catch(JSONException e){
                //-----------------------------image = some default drawable, WE SHOULD ADD THIS DEFAULT RESOURCE------------------------------------------------------
            }

            try{
                nameText.setText(recipe.getString("name"));
            }
            catch(JSONException e){
                nameText.setText("Recipe Title Not Found");
            }
            return recipeView;
        }
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

    //Creates a onItemClick listener, onClick starts the ViewRecipe activity.
    // The onClick converts the JSONRecipes object that matches the ListView item position,
    //converts that JSONObject to a string, and adds that as an intent extra
    private void registerClickCallback(){
        ListView list = (ListView) findViewById(R.id.FileList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> paret, View viewClicked, int position, long id){
                TextView textView = (TextView) viewClicked;
                String message = "You Clicked " + textView.getText().toString();
                Intent intent = new Intent(getApplicationContext(), RecipeViewer.class);  //THIS NEEDS TO BE THE VIEWER ACTIVITY, MAKE SURE TO PLUG THAT VALUE HERE
                intent.putExtra("JSONrecipe",JSONRecipes.get(position).toString());
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
