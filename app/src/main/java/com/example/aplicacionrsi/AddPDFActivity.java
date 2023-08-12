package com.example.aplicacionrsi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.aplicacionrsi.configuracion.DatabaseHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

public class AddPDFActivity extends AppCompatActivity {

    private Button buttonChoose;
    private Button buttonUpload;
    private EditText editTextArchivo, editTextPath;
    private int PICK_PDF_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private Uri filePath;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_archivo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new DatabaseHelper(this);

        // Solicitar permiso de almacenamiento
        requestStoragePermission();
        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        editTextArchivo = (EditText) findViewById(R.id.editTextArchivo);
        editTextPath = (EditText) findViewById(R.id.editTextPath);

        buttonChoose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
              showFileChooser();
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
             long i = db.insertArchivo(editTextArchivo.getText().toString(),  editTextPath.getText().toString());
             Toast.makeText(getApplicationContext(), "Registro insertado con éxito "+i,  Toast.LENGTH_LONG).show();
            }
        });

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }


     //método para mostrar el selector de archivos
     private void showFileChooser() {
         Intent intent = new Intent();
         intent.setType("application/pdf");
         intent.setAction(Intent.ACTION_GET_CONTENT);
         startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PICK_PDF_REQUEST);
     }

     //manejo del resultado de la actividad del selector
     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
             filePath = data.getData();
             String path = FilePath.getPath(this, filePath);
             Toast.makeText(this, "Path "+path, Toast.LENGTH_LONG).show();
             if (path == null) {
                 Toast.makeText(this, "Mueva su archivo .pdf al almacenamiento interno y vuelva a intentarlo", Toast.LENGTH_LONG).show();
             } else {
               //  editTextBase.setText(""+convertPDFToBase64(path));
                 editTextPath.setText(path);
             }
         }
     }

     // metodo que recibe la ruta del archivo en formato string y convierte el archivo de extension .pdf a base64 y lo retorna como una cadena
     public String convertPDFToBase64(String filePath) {
         try {
             FileInputStream fileInputStream = new FileInputStream(new File(filePath));
             byte[] bytes = new byte[fileInputStream.available()];
             fileInputStream.read(bytes);
             fileInputStream.close();
             return Base64.encodeToString(bytes, Base64.DEFAULT);
         } catch (IOException e) {
             e.printStackTrace();
             Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_LONG).show();
         }
           return null;
     }




     // Solicitando permiso
     private void requestStoragePermission() {
         if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
             return;
         if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
         }
         ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
     }

     @Override
      public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
         // Comprobando el código de solicitud de nuestra solicitud
         super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         if (requestCode == STORAGE_PERMISSION_CODE) {
             // Si se concede el permiso
             if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                 // Mostrando el toast (Mensaje) de permiso otorgado con exito
                 Toast.makeText(this, "Permiso concedido ahora puedes leer el almacenamiento", Toast.LENGTH_LONG).show();
             } else {
                 // Mostrar otro toast (Mensaje) si no se otorga el permiso
                 Toast.makeText(this, "Vaya, acabas de denegar el permiso.", Toast.LENGTH_LONG).show();
             }
         }
     }
}
