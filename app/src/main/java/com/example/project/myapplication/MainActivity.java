package com.example.project.myapplication;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonCreate;

    private DBHelper dbHelper;

    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.buttonCreate = (Button) findViewById(R.id.button_create);
        list = (ListView) findViewById(R.id.list);
        this.buttonCreate.setOnClickListener(this);
        dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("items", new String[]{"name"}, null, null, null, null, "id DESC");
        ArrayList<String> arr = new ArrayList<String>();
        if (c != null) {
            if (c.moveToFirst()) {
                int nameColIndex = c.getColumnIndex("name");

                do {
                    arr.add(c.getString(nameColIndex));
                } while (c.moveToNext());
            }
            c.close();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arr);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, ViewActivity.class);
                intent.putExtra("title", list.getItemAtPosition(i).toString());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_create:
                startActivity(new Intent(this, CreateActivity.class));
                break;
        }
    }
}
