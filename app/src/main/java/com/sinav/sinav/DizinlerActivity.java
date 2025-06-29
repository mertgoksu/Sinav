package com.sinav.sinav;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class DizinlerActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> klasorListesi;
    TextView txtDizinYolu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dizinler);

        listView = findViewById(R.id.listViewDizin);
        txtDizinYolu = findViewById(R.id.txtDizinYolu);
        klasorListesi = new ArrayList<>();

        File anaKlasor = new File(getExternalFilesDir(null), "TeknolojiFakultesi");

        // 📍 TAM DİZİN YOLUNU TEXT'E YAZ
        txtDizinYolu.setText("Dizin yolu: " + anaKlasor.getAbsolutePath());

        if (anaKlasor.exists() && anaKlasor.isDirectory()) {
            File[] altKlasorler = anaKlasor.listFiles();
            if (altKlasorler != null) {
                for (File f : altKlasorler) {
                    if (f.isDirectory()) {
                        klasorListesi.add(f.getName());
                    }
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, klasorListesi);
        listView.setAdapter(adapter);
    }
}
