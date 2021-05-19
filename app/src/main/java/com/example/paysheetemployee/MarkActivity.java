package com.example.paysheetemployee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.paysheetemployee.utils.Preferences;
import com.example.paysheetemployee.utils.UtilString;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MarkActivity extends AppCompatActivity {

    public static boolean DEBUG_MODE = false;
    public final static int RESP_TOMAR_FOTO = 1000;
    public static final int REQUEST_CODE_TAKE_PHOTO = 0 /*1*/;
    private static final int MY_PERMISSIONS_REQUEST_CODE = 123;
    private Preferences preferences;
    private String mCurrentPhotoPath, encodedImage, markMessage;
    private Uri photoURI;
    private Context context;
    private ImageView userPicture;
    ProgressBar indeterminateBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark);
        preferences = new Preferences(this);
        context = this;

        try {
            userPicture = (ImageView) findViewById(R.id.userPicture);
            encodedImage = "";
            markMessage = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            switch (requestCode) {
                case 100:
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //Dio Permiso Ubicacion
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        permisoCamara();
                    } else {
                        if (DEBUG_MODE) {
                            Toast.makeText(this, "No puede Marcar sin acceder a su ubicacion", Toast.LENGTH_SHORT).show();
                        }
                    }

                    break;
                case 200:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //Dio Permiso Camara
                    } else {
                        if (DEBUG_MODE) {
                            Toast.makeText(this, "Permiso Camara negado", Toast.LENGTH_SHORT).show();
                        }
                    }
                default:
                    break;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void permisoCamara(){
        int permissionCamaraCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        if (permissionCamaraCheck == PackageManager.PERMISSION_GRANTED){
            //hay permiso
        }else{
            //no hay permiso
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                if (DEBUG_MODE) {
                    Toast.makeText(MarkActivity.this, "Permitanos acceder a su camara para poder marcar", Toast.LENGTH_LONG).show();
                }
            }
            requestPermissions(new String[]{Manifest.permission.CAMERA},200);
        }
    }


    public void sacarFoto(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(
                this,Manifest.permission.READ_CONTACTS)
                + ContextCompat.checkSelfPermission(
                this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            // Do something, when permissions not granted
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    this,Manifest.permission.CAMERA)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this,Manifest.permission.READ_CONTACTS)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                // If we should give explanation of requested permissions

                // Show an alert dialog here with request explanation
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Camera, Read Contacts and Write External" +
                        " Storage permissions are required to do the task.");
                builder.setTitle("Please grant those permissions");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(
                                (Activity) context,
                                new String[]{
                                        Manifest.permission.CAMERA,
                                        Manifest.permission.READ_CONTACTS,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                },
                                MY_PERMISSIONS_REQUEST_CODE
                        );
                    }
                });
                builder.setNeutralButton("Cancel",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                // Directly request for required permissions, without explanation
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        MY_PERMISSIONS_REQUEST_CODE
                );
            }
        }else {
            // Do something, when permissions are already granted
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {

                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "MyPicture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "Photo taken on " + System.currentTimeMillis());
                    photoURI = getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    //Uri photoURI = FileProvider.getUriForFile(AddActivity.this, "com.example.android.fileprovider", photoFile);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PHOTO);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        File image = null;
        try {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
             image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = image.getAbsolutePath();
        } catch (Exception e){
            e.printStackTrace();
        }
        return image;
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == RESULT_OK) {

                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoURI);
                    userPicture.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Uri uri = photoURI;
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == RESULT_OK){
                in = getContentResolver().openInputStream(photoURI);

                // Decode image size
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(in, null, options);
                in.close();

                int scale = 1;
                while ((options.outWidth * options.outHeight) * (1 / Math.pow(scale, 2)) >
                        IMAGE_MAX_SIZE) {
                    scale++;
                }
                Log.d("IMAGE DATA: ", "scale = " + scale + ", orig-width: " + options.outWidth + ",orig-height: " + options.outHeight);

                Bitmap resultBitmap = null;
                in = getContentResolver().openInputStream(photoURI);
                if (scale > 1) {
                    scale--;
                    // scale to max possible inSampleSize that still yields an image
                    // larger than target
                    options = new BitmapFactory.Options();
                    options.inSampleSize = scale;
                    resultBitmap = BitmapFactory.decodeStream(in, null, options);
                    userPicture.setImageBitmap(resultBitmap);

                    // resize to desired dimensions
                    int height = resultBitmap.getHeight();
                    int width = resultBitmap.getWidth();
                    Log.d("IMAGE DATA: ", "1th scale operation dimenions - width: " + width + ",height: " + height);

                    double y = Math.sqrt(IMAGE_MAX_SIZE
                            / (((double) width) / height));
                    double x = (y / height) * width;

                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(resultBitmap, (int) x,
                            (int) y, true);
                    //resultBitmap.recycle();
                    resultBitmap = scaledBitmap;

                    System.gc();
                } else {
                    resultBitmap = BitmapFactory.decodeStream(in);
                }

                if (resultBitmap != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    resultBitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                    byte[] byteArray = stream.toByteArray();
                    encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    encodedImage = "data:image/jpeg;base64," + encodedImage;
                }
                in.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        /*try {
            if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == RESULT_OK){
                Bitmap imageBitmap = null;
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = false;
                if (mCurrentPhotoPath != "") {
                    File file = new File(mCurrentPhotoPath);
                    if(file.exists()){
                        Bitmap notEncodedImage  = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
                        userPicture.setImageBitmap(notEncodedImage);
                        int photoW = bmOptions.outWidth;
                        int photoH = bmOptions.outHeight;

                        // Determine how much to scale down the image
                        int scaleFactor = Math.min(photoW / 300, photoH / 400);

                        System.out.println("Source Width: " + photoW);
                        System.out.println("Source Height: " + photoH);

                        // Decode the image file into a Bitmap sized to fill the View
                        bmOptions.inJustDecodeBounds = false;
                        bmOptions.inSampleSize = scaleFactor;
                        bmOptions.inPurgeable = true;

                        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
                        System.out.println("Final Width: " + bmOptions.outWidth);
                        System.out.println("Final Height: " + bmOptions.outHeight);
                        imageBitmap = bitmap;
                    }
                }
                if (imageBitmap != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                    byte[] byteArray = stream.toByteArray();
                    encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }*/
    }


    //To HexString
    public static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        try{
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
        }catch (Exception e){
            System.out.println("Error Hex String: " + e);
        }
        return hexString.toString();
    }


    public void enviarMarcacion(View view) {
        try{
            Date today = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            System.out.println("Fecha: " + dateFormat.format(today));
            String todayStr = dateFormat.format(today);

            StringRequest postRequest = new StringRequest(Request.Method.POST, UtilString.MarkRegisterUrl,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject responseObj = new JSONObject(response);
                                String success = responseObj.get("success").toString();
                                if (success.compareTo("true") == 0){
                                    markMessage = responseObj.get("message").toString();
                                    AlertDialog.Builder builder;
                                    builder = new AlertDialog.Builder(context);
                                    builder.setTitle(R.string.app_name)
                                            .setMessage(markMessage)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // ok boton
                                                    Intent intent = new Intent(context, MapsActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            })
                                            .show();
                                }

                            } catch(JSONException e){
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "ERR-" + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();

                            } catch(Exception e){
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "ERR-" + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            AlertDialog.Builder builder;
                            builder = new AlertDialog.Builder(context);
                            builder.setTitle(context.getApplicationInfo().name)
                                    .setMessage(error.getMessage())
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                                    .show();
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + preferences.getPaysheetToken());
                    headers.put("Content-Type", "application/x-www-form-urlencoded");
                    return headers;
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String>  params = new HashMap<String, String>();
                    try{
                        params.put("mark_date", todayStr);
                        params.put("lat", "-2.19616");
                        params.put("lng", "-79.88621");
                        params.put("user_id", preferences.getPUsername());
                        params.put("mark_type_id", "1");
                        params.put("picture", encodedImage);
                    } catch(Exception e){
                        Toast.makeText(getApplicationContext(), e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(postRequest);
        } catch (Exception e){
                e.printStackTrace();
            }
    }
}