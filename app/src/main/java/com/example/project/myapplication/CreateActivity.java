package com.example.project.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CreateActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonAddItem;

    private Button buttonAddImage;

    private Button buttonAddMap;

    private EditText editTextDescription;

    private EditText editTextTitle;

    private DBHelper dbHelper;

    private String loadImageBase64 = "";

    private String latitude;

    private String longitude;

    private String address = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        buttonAddItem = (Button) findViewById(R.id.buttonAddItem);
        editTextTitle = (EditText) findViewById(R.id.editTextTitleEdit);
        editTextDescription = (EditText) findViewById(R.id.editTextDescriptionEdit);
        buttonAddImage = (Button) findViewById(R.id.buttonAddImageEdit);
        buttonAddMap = (Button) findViewById(R.id.buttonAddMapEdit);

        dbHelper = new DBHelper(this);

        buttonAddMap.setOnClickListener(this);
        buttonAddItem.setOnClickListener(this);
        buttonAddImage.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonAddItem:
                String title = this.editTextTitle.getText().toString(),
                        description = this.editTextDescription.getText().toString(),
                        toastMessage;
                if(title.isEmpty()){
                    toastMessage = "Заполните 'Название'";
                }else if(description.isEmpty()) {
                    toastMessage = "Заполните 'Описание'";
                }else if(loadImageBase64.isEmpty()){
                    toastMessage = "Выберете 'Изображение'";
                }else if(address == null){
                    toastMessage = "Выберете 'Местоположение'";
                }else{
                    ContentValues cv = new ContentValues();
                    cv.put("name", title);
                    cv.put("description", description);
                    cv.put("base", loadImageBase64);
                    cv.put("address", address);
                    cv.put("latitude", latitude);
                    cv.put("longitude", longitude);
                    SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
                    sqLiteDatabase.insert("items", null, cv);
                    toastMessage = "Успешно добавлено";
                    startActivity(new Intent(CreateActivity.this, MainActivity.class));
                }
                Toast.makeText(getApplicationContext(),
                        toastMessage,
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonAddImageEdit:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
                break;
            case R.id.buttonAddMapEdit:
                Intent intent = new Intent(CreateActivity.this, MapsActivity.class);
                startActivityForResult(intent, 2);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap img = null;
            Uri selectedImage = data.getData();
            try {
                img = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                loadImageBase64 = MyUtils.encodeBase64(img);
                Toast.makeText(getApplicationContext(),
                        "Изображение выбрано",
                        Toast.LENGTH_SHORT).show();
                buttonAddImage.setText("ВЫБРАТЬ ДРУГОЕ ИЗОБРАЖЕНИЕ");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(requestCode == 2 && resultCode == RESULT_OK){
            address = data.getStringExtra("address");
            latitude = data.getStringExtra("latitude");
            longitude = data.getStringExtra("longitude");
            buttonAddMap.setText("ВЫБРАТЬ ДРУГОЕ МЕСТОПОЛОЖЕНИЕ");
        }
    }

}