package com.example.aplicacionrsi;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static java.security.AccessController.getContext;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aplicacionrsi.adapter.ArchivosAdapter;
import com.example.aplicacionrsi.configuracion.DatabaseHelper;
import com.example.aplicacionrsi.modelo.Archivos;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper db;
    private TextView texto, texto2;
    private ImageView imagen;
    FloatingActionButton report;

    @Override
    public void onResume(){
        super.onResume();
        Cargar();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));

        texto = (TextView) findViewById(R.id.emptyCartText);
        texto2 = (TextView) findViewById(R.id.addItemText);
        imagen = (ImageView ) findViewById(R.id.emptyCartImg);

        Cargar();

        report = (FloatingActionButton) findViewById(R.id.fab);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddPDFActivity.class);
                startActivity(intent);
            }
        });
    }


      public void Cargar(){
          List<Archivos> notesList = new ArrayList<>();
          RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
          LinearLayoutManager layoutManager = new LinearLayoutManager(this);
          recyclerView.setLayoutManager(layoutManager);
          db = new DatabaseHelper(this);
          notesList.addAll(db.getTodosArchivos());
          ArchivosAdapter mAdapter = new ArchivosAdapter(this, notesList);
          RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
          recyclerView.setLayoutManager(mLayoutManager);
          recyclerView.setItemAnimator(new DefaultItemAnimator());
          // recyclerView.addItemDecoration(new MyDividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL, 16));
          recyclerView.setAdapter(mAdapter);
          mAdapter.notifyDataSetChanged();
          if (recyclerView.getAdapter() != null) {
              if (recyclerView.getAdapter().getItemCount() == 0) {
                  recyclerView.setVisibility(View.GONE);
                  texto.setVisibility(View.VISIBLE);
                  texto2.setVisibility(View.VISIBLE);
                  imagen.setVisibility(View.VISIBLE);
              } else {
                  recyclerView.setVisibility(View.VISIBLE);
                  texto.setVisibility(View.GONE);
                  texto2.setVisibility(View.GONE);
                  imagen.setVisibility(View.GONE);
              }
          }
      }
}
