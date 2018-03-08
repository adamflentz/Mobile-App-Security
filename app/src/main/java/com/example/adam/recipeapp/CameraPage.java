package com.example.adam.recipeapp;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by adam on 2/28/18.
 */

public class CameraPage extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    String mCurrentPhotoPath;
    ImageView mImageView;

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

            }
            else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 123);
            }
        }



        Button cButton = (Button) findViewById(R.id.takephoto);
        cButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });


        mImageView = (ImageView) findViewById(R.id.camera);

        Button submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click

                EditText nameText = (EditText) findViewById(R.id.name);

                String filename = nameText.getText().toString();
                JSONObject recipeJSON = new JSONObject();
                JSONObject individualRecipe = new JSONObject();
                JSONObject ingredient = new JSONObject();
                List<String> IngredientList = new ArrayList<String>();

                for(int i = 0; i < ingrList.size(); i++){
                    IngredientList.add(ingrList.get(i));
                }



                List<String> Directions = new ArrayList<String>();

                for(int i = 0; i < instrList.size(); i++){
                    Directions.add(instrList.get(i));
                }

                try {
                    individualRecipe.put("name", nameText.getText().toString());
                    individualRecipe.put("ingredients", IngredientList);
                    individualRecipe.put("directions", Directions);
                    individualRecipe.put("picture", mCurrentPhotoPath);
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


    //Camera functions



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
/*        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
        }*/
    }

    //JSON functions
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
}