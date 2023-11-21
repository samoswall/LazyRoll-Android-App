package com.example.lazyrolls;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Date;
import java.util.HexFormat;

import android.content.ContentResolver;
import android.content.Context;



public class Crop_Image extends AppCompatActivity {

    int img_pos;

    //   ActivityMainBinding binding;
    ActivityResultLauncher<Intent> getImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null && data.getData() != null) {
                Uri imageUri = data.getData();
                launchImageCropper(imageUri);
            }
        }
    });

    private final ActivityResultLauncher<String> requestPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            getImageFile();
        } else {
            permissionDenied();
        }
    });

    ActivityResultLauncher<Intent> android11StoragePermission = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (isPermitted()) {
            getImageFile();
        } else {
            permissionDenied();
        }
    });

    ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(new CropImageContract(), result -> {
        if (result.isSuccessful()) {
            Bitmap cropped = BitmapFactory.decodeFile(result.getUriFilePath(getApplicationContext(), true));
            saveCroppedImage(cropped,img_pos);
   //         Room_image_load(1);
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Получаем номер комнаты от MainActivity
        Intent intent = getIntent();
        img_pos = intent.getIntExtra("img_position", -1);

        if (isPermitted()) {
            getImageFile();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                requestAndroid11StoragePermission();
            } else {
                requestPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    private void getImageFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getImage.launch(intent);
    }

    @TargetApi(Build.VERSION_CODES.R)
    private void requestAndroid11StoragePermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
        android11StoragePermission.launch(intent);
    }

    private boolean isPermitted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            return ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void launchImageCropper(Uri uri) {
        CropImageOptions cropImageOptions = new CropImageOptions();
        cropImageOptions.imageSourceIncludeGallery = true;
        cropImageOptions.imageSourceIncludeCamera = true;
        cropImageOptions.aspectRatioX = 165;
        cropImageOptions.aspectRatioY = 102;
        cropImageOptions.fixAspectRatio = true;
        cropImageOptions.activityMenuIconColor = Color.BLACK;
        CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(uri, cropImageOptions);
        cropImage.launch(cropImageContractOptions);
    }

    private void permissionDenied() {
        Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_LONG).show();
    }

    private void showFailureMessage() {
        Toast.makeText(getApplicationContext(), "Cropped image not saved something went wrong", Toast.LENGTH_LONG).show();
    }


    private void saveCroppedImage(Bitmap bitmap, int pos) {
        File myDir =  new File(Crop_Image.this.getFilesDir(), "RoomsImg");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        String imageName = "Image_room_" + String.valueOf(pos) + ".jpg";
        File file = new File(myDir, imageName);
        if (file.exists()) file.delete();
        try {
            // Save the Bitmap to the file
            OutputStream outputStream;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                outputStream = Files.newOutputStream(file.toPath());
            } else {
                outputStream = new FileOutputStream(file);
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
Log.i("---save---", myDir.toString()+"   "+file.toString());
            Intent intent = new Intent();
            intent.putExtra("image_file_name", img_pos);
            setResult(RESULT_OK, intent);
            finish();
        } catch (Exception e) {
            showFailureMessage();
        }
    }










}