package com.sinav.sinav;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = openOrCreateDatabase("sinav.db", MODE_PRIVATE, null);

        db.execSQL("CREATE TABLE IF NOT EXISTS sinav (id INTEGER PRIMARY KEY AUTOINCREMENT, isim TEXT)");
        db.execSQL("INSERT OR IGNORE INTO sinav (id, isim) VALUES (1, 'Mobil Programlama SQLite ile eklendi')");
        db.execSQL("UPDATE sinav SET isim = 'Mobil Programlama SQLite ile eklendi' WHERE id = 1");

        db.execSQL("CREATE TABLE IF NOT EXISTS konumlar (id INTEGER PRIMARY KEY AUTOINCREMENT, latitude REAL, longitude REAL)");

        TextView txtSinavBasligi = findViewById(R.id.txtSinavBasligi);

        Button btnToast = findViewById(R.id.btnToast);
        Button btnDizin = findViewById(R.id.btnDizin);
        Button btnCamera = findViewById(R.id.btnCamera);
        Button btnWhatsapp = findViewById(R.id.btnWhatsapp);
        Button btnKonum = findViewById(R.id.btnKonum);

        LinearLayout layoutBolumler = findViewById(R.id.layoutBolumler);
        LinearLayout layoutSinav = findViewById(R.id.layoutSinav);
        Button btnAgac = findViewById(R.id.btnAgac);
        Button btnBilisim = findViewById(R.id.btnBilisim);
        Button btnEnerji = findViewById(R.id.btnEnerji);

        Button btnKonumlariGor = findViewById(R.id.btnKonumlariGor);

        Button btnDizinleriGor = findViewById(R.id.btnDizinleriGor);

        TextView txtTitle = findViewById(R.id.title);


        Cursor c = db.rawQuery("SELECT isim FROM sinav WHERE id = 1", null);
        if (c.moveToFirst()) {
            String sinavAdi = c.getString(0);
            txtSinavBasligi.setText("Sınav Adı: " + sinavAdi);
        }
        c.close();

        layoutSinav.setOnClickListener(v -> {
            db.execSQL("UPDATE sinav SET isim = 'Mobil Programlama - Güncellendi' WHERE id = 1");

            Cursor cursor = db.rawQuery("SELECT isim FROM sinav WHERE id = 1", null);
            if (cursor.moveToFirst()) {
                String guncelSinavAdi = cursor.getString(0);
                txtSinavBasligi.setText("Sınav Adı: " + guncelSinavAdi);
            }
            cursor.close();
        });


        btnToast.setOnClickListener(v -> {
            Toast.makeText(this, "Bölümünüzü Seçiniz", Toast.LENGTH_SHORT).show();
            txtTitle.setText("Teknoloji Fakültesi");
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

        btnAgac.setOnClickListener(v -> {
            Toast.makeText(this, "Ağaç İşleri Endüstri seçildi", Toast.LENGTH_SHORT).show();
            txtTitle.setText("Teknoloji Fakültesi Ağaç İşleri Endüstri");
        });

        btnBilisim.setOnClickListener(v -> {
            Toast.makeText(this, "Bilişim Sistemleri seçildi", Toast.LENGTH_SHORT).show();
            txtTitle.setText("Teknoloji Fakültesi Bilişim Sistemleri");
        });

        btnEnerji.setOnClickListener(v -> {
            Toast.makeText(this, "Enerji Sistemleri seçildi", Toast.LENGTH_SHORT).show();
            txtTitle.setText("Teknoloji Fakültesi Enerji Sistemleri");
        });



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

                    db.execSQL("INSERT INTO konumlar (latitude, longitude) VALUES (" + latitude + ", " + longitude + ")");

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
