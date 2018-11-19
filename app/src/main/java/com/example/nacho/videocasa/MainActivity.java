package com.example.nacho.videocasa;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    static final int VENGO_DE_LA_CAMARA_CON_FICHERO = 2;
    static final int PEDI_PERMISOS_DE_ESCRITURA = 2;
    Button  btncapturar;
    VideoView camaraVid;
    MediaController mediaController;
    String rutaVid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        btncapturar = findViewById(R.id.capturarBtn);
        camaraVid = findViewById(R.id.videoView);
        btncapturar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirPermisoVideo();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if ((requestCode == VENGO_DE_LA_CAMARA_CON_FICHERO) && (resultCode == RESULT_OK)) {

            mediaController = new MediaController(this);
            mediaController.setAnchorView(camaraVid);
            camaraVid.setMediaController(mediaController);
           Uri uriV = Uri.parse(rutaVid);
            camaraVid.setVideoURI(uriV);
            camaraVid.start();
        }
    }

    public void CapVid() {
        File fichVid=null;
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        try {
            fichVid = crearFichdeVideo();
        } catch (IOException e) {
            e.printStackTrace();
        }
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(fichVid));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, VENGO_DE_LA_CAMARA_CON_FICHERO);
        } else {
            Toast.makeText(this, "No tengo cámara", Toast.LENGTH_SHORT).show();
        }
    }




    public void pedirPermisoVideo() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PEDI_PERMISOS_DE_ESCRITURA);
            }
        } else {
            CapVid();
        }
    }
    File crearFichdeVideo() throws IOException {
        String tiempo = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nomFich = "Video " + tiempo;
        File carpetaVid = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        File videoCap = File.createTempFile(nomFich, ".mp4", carpetaVid);
        rutaVid = videoCap.getAbsolutePath();
        Toast.makeText(this, rutaVid, Toast.LENGTH_SHORT).show();
        return videoCap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PEDI_PERMISOS_DE_ESCRITURA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.CapVid();
                } else {
                    Toast.makeText(this, "No has aceptado los permisos de escritura", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            default: break;

        }
    }
}