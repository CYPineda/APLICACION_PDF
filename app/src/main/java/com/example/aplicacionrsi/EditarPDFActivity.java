package com.example.aplicacionrsi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.aplicacionrsi.modelo.Archivos;
import java.util.UUID;

public class EditarPDFActivity extends AppCompatActivity {

    private Button buttonChoose;
    private Button buttonUpload;
    private EditText editTextArchivo, editTextPath, editTextId,  editTextBase;
    private int PICK_PDF_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private Uri filePath;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_archivo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DatabaseHelper(this);
        //Solicitar permiso de almacenamiento
        requestStoragePermission();
        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        editTextArchivo = (EditText) findViewById(R.id.editTextArchivo);
        editTextPath = (EditText) findViewById(R.id.editTextPath);
        editTextId = (EditText) findViewById(R.id.editTextId);
      //  editTextBase = (EditText) findViewById(R.id.editTextBase);

        Bundle param = this.getIntent().getExtras();
        if(param != null){
           String  bid = param.getString("id");
           Archivos arch =  db.getIdArchivos(bid);
           editTextArchivo.setText(""+arch.getNombre());
           editTextPath.setText(""+arch.getPath());
           editTextId.setText(""+arch.getIdArchiivo());
        }

        buttonChoose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
              showFileChooser();
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
             long i = db.updateArchivos(editTextId.getText().toString(),editTextArchivo.getText().toString(), editTextPath.getText().toString(), getApplicationContext());
             Toast.makeText(getApplicationContext(), "Modificado con exito! "+i,  Toast.LENGTH_LONG).show();
            }
        });

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
    }


     //Método para mostrar el selector de archivos
     private void showFileChooser() {
         Intent intent = new Intent();
         intent.setType("application/pdf");
         intent.setAction(Intent.ACTION_GET_CONTENT);
         startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PICK_PDF_REQUEST);
     }

     //manejo del resultado de la actividad del selector de ima
     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
             filePath = data.getData();
             editTextPath.setText(""+filePath.getPath());
             //Toast.makeText(this, "Path : "+filePath, Toast.LENGTH_LONG).show();
         }
     }


     //Solicitando permiso
     private void requestStoragePermission() {
         if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
             return;

         if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
           //Si el usuario ha denegado el permiso previamente su código llegará a este bloque
           //Aquí puedes explicar por qué necesitas este permiso
         }
         //Y finalmente pedir el permiso
         ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
     }



     @Override
      public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
         //Comprobando el código de solicitud de nuestra solicitud
         super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         if (requestCode == STORAGE_PERMISSION_CODE) {
             //Si se concede el permiso
             if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                 //Mostramos el toast (Mensaje)
                 Toast.makeText(this, "Permiso concedido ahora puedes leer el almacenamiento", Toast.LENGTH_LONG).show();
             } else {
                 // Mostrar otro toast (Mensaje) si no se otorga el permiso
                 Toast.makeText(this, "Vaya, acabas de denegar el permiso", Toast.LENGTH_LONG).show();
             }
         }
     }

}
