package com.sinav.sinav;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private SQLiteDatabase db; // EKLENDİ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SQLite Veri Tabanı Oluştur (EKLENDİ)
        db = openOrCreateDatabase("bolumler.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS bolumler (id INTEGER PRIMARY KEY, isim TEXT)");
        db.execSQL("INSERT OR IGNORE INTO bolumler (id, isim) VALUES (1, 'Bilişim Sistemleri')");
        db.execSQL("UPDATE bolumler SET isim = 'Bilişim Sistemleri Mühendisliği' WHERE id = 1");
        db.execSQL("CREATE TABLE IF NOT EXISTS konumlar (id INTEGER PRIMARY KEY AUTOINCREMENT, latitude REAL, longitude REAL)");

        Button btnToast = findViewById(R.id.btnToast);
        Button btnDizin = findViewById(R.id.btnDizin);
        Button btnCamera = findViewById(R.id.btnCamera);
        Button btnWhatsapp = findViewById(R.id.btnWhatsapp);
        Button btnKonum = findViewById(R.id.btnKonum);

        LinearLayout layoutBolumler = findViewById(R.id.layoutBolumler);
        Button btnAgac = findViewById(R.id.btnAgac);
        Button btnBilisim = findViewById(R.id.btnBilisim);
        Button btnEnerji = findViewById(R.id.btnEnerji);

        Button btnKonumlariGor = findViewById(R.id.btnKonumlariGor);

        Button btnDizinleriGor = findViewById(R.id.btnDizinleriGor);

        ImageView icWhatsapp = findViewById(R.id.ic_wp);


        // Bölüm Seç butonu davranışı
        btnToast.setOnClickListener(v -> {
            if (layoutBolumler.getVisibility() == View.GONE) {
                layoutBolumler.setVisibility(View.VISIBLE);
            } else {
                layoutBolumler.setVisibility(View.GONE);
            }
        });

        btnDizinleriGor.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DizinlerActivity.class);
            startActivity(intent);
        });

        btnKonumlariGor.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, KonumlarActivity.class);
            startActivity(intent);
        });

        // Alt butonlar (opsiyonel toast)
        btnAgac.setOnClickListener(v ->
                Toast.makeText(this, "Ağaç İşleri seçildi", Toast.LENGTH_SHORT).show());

        btnBilisim.setOnClickListener(v ->
                Toast.makeText(this, "Bilişim seçildi", Toast.LENGTH_SHORT).show());

        btnEnerji.setOnClickListener(v ->
                Toast.makeText(this, "Enerji seçildi", Toast.LENGTH_SHORT).show());


        btnDizin.setOnClickListener(v -> {
            File anaKlasor = new File(getExternalFilesDir(null), "TeknolojiFakultesi");
            anaKlasor.mkdirs();
            new File(anaKlasor, "AgacIsleriEndustri").mkdirs();
            new File(anaKlasor, "BilisimSistemleri").mkdirs();
            new File(anaKlasor, "EnerjiSistemleri").mkdirs();
            Toast.makeText(this, "Dizinler oluşturuldu", Toast.LENGTH_SHORT).show();
        });

        btnCamera.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        });

        btnWhatsapp.setOnClickListener(v -> {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Merhaba!");
            sendIntent.setPackage("com.whatsapp");
            try {
                startActivity(sendIntent);
            } catch (Exception e) {
                Toast.makeText(this, "WhatsApp yüklü değil!", Toast.LENGTH_SHORT).show();
            }
        });

        icWhatsapp.setOnClickListener(v-> {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Merhaba!");
            sendIntent.setPackage("com.whatsapp");
            try {
                startActivity(sendIntent);
            } catch (Exception e) {
                Toast.makeText(this, "WhatsApp yüklü değil!", Toast.LENGTH_SHORT).show();
            }
        });

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        btnKonum.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }

            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    // ✅ SQLite'a kaydet
                    db.execSQL("INSERT INTO konumlar (latitude, longitude) VALUES (" + latitude + ", " + longitude + ")");

                    // ✅ Haritada göster
                    Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(Konumum)");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(this, "Konum alınamadı. Lütfen GPS’i kontrol et.", Toast.LENGTH_SHORT).show();
                }
            });
        });


        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
    }
}
