package com.sinav.sinav;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class KonumlarActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private ListView listView;
    private ArrayList<String> konumListesi;
    private TextView txtVeritabaniYolu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konumlar);

        listView = findViewById(R.id.listViewKonum);
        txtVeritabaniYolu = findViewById(R.id.txtVeritabaniYolu);
        konumListesi = new ArrayList<>();

        File veritabaniDosyasi = getDatabasePath("sinav.db");
        txtVeritabaniYolu.setText("Veritabanı yolu: " + veritabaniDosyasi.getAbsolutePath());

        db = openOrCreateDatabase("sinav.db", MODE_PRIVATE, null);

        Cursor cursor = db.rawQuery("SELECT * FROM konumlar", null);
        while (cursor.moveToNext()) {
            double lat = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
            double lng = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));
            konumListesi.add("Lat: " + lat + " | Lon: " + lng);
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, konumListesi);
        listView.setAdapter(adapter);
    }
}
