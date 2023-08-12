package com.example.aplicacionrsi.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aplicacionrsi.EditarPDFActivity;
import com.example.aplicacionrsi.PdfPreviewActivity;
import com.example.aplicacionrsi.R;
import com.example.aplicacionrsi.configuracion.DatabaseHelper;
import com.example.aplicacionrsi.modelo.Archivos;
import java.util.List;

public class ArchivosAdapter extends RecyclerView.Adapter<ArchivosAdapter.MyViewHolder> {

    private Context context;
    private List<Archivos> notesList;
    DatabaseHelper db;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView note;
        public TextView nombre;
        public TextView path;
        public CardView card;

        public MyViewHolder(View view) {
            super(view);
            note = view.findViewById(R.id.ivImage);
            nombre = view.findViewById(R.id.tvNombre);
            path = view.findViewById(R.id.tvPath);
            card = view.findViewById(R.id.cardview);
        }
    }


    public ArchivosAdapter(Context context, List<Archivos> notesList) {
        this.context = context;
        this.notesList = notesList;
        db = new DatabaseHelper(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_archivos_list_cards, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Archivos note = notesList.get(position);
      //  holder.note
        holder.nombre.setText(String.valueOf(note.getNombre()));
        holder.path.setText(String.valueOf(note.getPath()));
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(context);
                dialogo1.setTitle("Opciones");
                dialogo1.setMessage("Seleccione una opción.");
                dialogo1.setCancelable(false);
                dialogo1.setNeutralButton("Ver PDF", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        // aceptar();
                        Intent intent = new Intent(context, PdfPreviewActivity.class);
                        intent.putExtra("path", ""+note.getPath());
                        context.startActivity(intent);
                    }
                });
                dialogo1.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                       // aceptar();
                        Intent intent = new Intent(context, EditarPDFActivity.class);
                        intent.putExtra("id", ""+note.getIdArchiivo());
                        context.startActivity(intent);
                    }
                });
                dialogo1.setNegativeButton("Eliminar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        deleteNoteDialog(note.getIdArchiivo(), note.getNombre());
                        //cancelar();
                    }
                });
                dialogo1.show();
            }
        });
    }




    @Override
    public int getItemCount() {
        return notesList.size();
    }

    private void deleteNoteDialog(int i, String title) {
        db.deleteItem(i);
        Toast.makeText(context.getApplicationContext(), "Registro eliminado con exito" , Toast.LENGTH_SHORT).show();
    }
}
