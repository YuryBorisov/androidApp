package com.example.project.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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

public class EditActivity extends AppCompatActivity implements View.OnClickListener{


    private Button buttonAddImage;

    private Button buttonAddMap;

    private Button buttonSave;

    private EditText editTextDescription;

    private EditText editTextTitle;

    private DBHelper dbHelper;

    private String loadImageBase64 = "";

    private String latitude;

    private String longitude;

    private String address = null;

    private SQLiteDatabase db;

    private Button backEdit;

    private String titleMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        editTextTitle = (EditText) findViewById(R.id.editTextTitleEdit);
        editTextDescription = (EditText) findViewById(R.id.editTextDescriptionEdit);
        buttonAddImage = (Button) findViewById(R.id.buttonAddImageEdit);
        buttonAddMap = (Button) findViewById(R.id.buttonAddMapEdit);
        buttonSave = (Button) findViewById(R.id.buttonSave);
        backEdit = (Button) findViewById(R.id.buttonBackEdit);
        dbHelper = new DBHelper(this);
        buttonAddMap.setOnClickListener(this);
        buttonAddImage.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
        dbHelper = new DBHelper(this);
        backEdit.setOnClickListener(this);
        db = dbHelper.getWritableDatabase();
        Cursor c = db.query(
                "items",
                new String[] {"*"},
                "name = ?",
                new String[] { getIntent().getStringExtra("name")},
                null,
                null,
                null
        );
        if (c != null) {
            if (c.moveToFirst()) {
                int nameColIndex = c.getColumnIndex("name");
                int descriptionColIndex = c.getColumnIndex("description");
                int baseColIndex = c.getColumnIndex("base");
                int addressColIndex = c.getColumnIndex("address");
                int latitudeColIndex = c.getColumnIndex("latitude");
                int longitudeColIndex = c.getColumnIndex("longitude");
                do {
                    titleMain = c.getString(nameColIndex);
                    editTextTitle.setText(titleMain);
                    editTextDescription.setText(c.getString(descriptionColIndex));
                    loadImageBase64 = c.getString(baseColIndex);
                    longitude = c.getString(longitudeColIndex);
                    latitude = c.getString(latitudeColIndex);
                    address = c.getString(addressColIndex);
                } while (c.moveToNext());
            }
            c.close();
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonAddImageEdit:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
                break;
            case R.id.buttonAddMapEdit:
                Intent intent = new Intent(EditActivity.this, MapsActivity.class);
                startActivityForResult(intent, 2);
                break;
            case R.id.buttonBackEdit:
                Intent intent1 = new Intent(EditActivity.this, ViewActivity.class);
                intent1.putExtra("title", titleMain);
                startActivity(intent1);
                break;
            case R.id.buttonSave:
                String title = editTextTitle.getText().toString(),
                        description = editTextDescription.getText().toString(),
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
                    sqLiteDatabase.update("items", cv, "name=" + titleMain, null);
                    toastMessage = "Успешно отредактировано";
                    Intent intent2 = new Intent(EditActivity.this, ViewActivity.class);
                    intent2.putExtra("title", title);
                    startActivity(intent2);
                }
                Toast.makeText(getApplicationContext(),
                        toastMessage,
                        Toast.LENGTH_SHORT).show();
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
