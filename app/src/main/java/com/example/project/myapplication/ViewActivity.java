package com.example.project.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class ViewActivity extends AppCompatActivity implements View.OnClickListener{

    private Button delete;

    private Button edit;

    private TextView title;

    private TextView description;

    private Button map;

    private String titleText;

    private DBHelper dbHelper;

    private String latitude;

    private String longitude;

    private String address;

    private String base;

    private SQLiteDatabase db;

    private ImageView imageViewPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        delete = (Button) findViewById(R.id.buttonDelete);
        edit = (Button) findViewById(R.id.buttonEdit);
        map = (Button) findViewById(R.id.buttonMap);
        title = (TextView) findViewById(R.id.TextViewTitle);
        description = (TextView) findViewById(R.id.textViewDescription);
        imageViewPhoto = (ImageView) findViewById(R.id.imageViewPhoto);
        delete.setOnClickListener(this);
        edit.setOnClickListener(this);
        map.setOnClickListener(this);
        Intent intent = getIntent();
        titleText = intent.getStringExtra("title");
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        Cursor c = db.query(
                "items",
                new String[] {"*"},
                "name = ?",
                new String[] { titleText },
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
                    titleText = c.getString(nameColIndex);
                    title.setText(titleText);
                    description.setText(c.getString(descriptionColIndex));
                    longitude = c.getString(longitudeColIndex);
                    latitude = c.getString(latitudeColIndex);
                    address = c.getString(addressColIndex);
                    base = c.getString(baseColIndex);
                    Bitmap img = MyUtils.decodeBase64(base);
                    int dimensionInw = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, img.getWidth(), getResources().getDisplayMetrics());
                    int dimensionInh = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, img.getHeight(), getResources().getDisplayMetrics());
                    imageViewPhoto.getLayoutParams().height = dimensionInw;
                    imageViewPhoto.getLayoutParams().width = dimensionInh;
                    imageViewPhoto.setImageBitmap(img);
                    imageViewPhoto.requestLayout();
                } while (c.moveToNext());
            }
            c.close();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonMap:
                Intent intent = new Intent(ViewActivity.this, ViewMapsActivity.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("address", address);
                startActivity(intent);
                break;
            case R.id.buttonDelete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Удалить запись?");
                builder.setCancelable(true);
                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.delete("items","name=?",new String[]{titleText});
                        Toast.makeText(getApplicationContext(),
                                "Успешно удалено",
                                Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ViewActivity.this, MainActivity.class));
                    }
                });
                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() { // Кнопка ОК
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.buttonEdit:
                Intent intent1 = new Intent(ViewActivity.this, EditActivity.class);
                intent1.putExtra("name", titleText);
                startActivity(intent1);
        }
    }
}
