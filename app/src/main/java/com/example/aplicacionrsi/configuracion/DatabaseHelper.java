package com.example.aplicacionrsi.configuracion;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import com.example.aplicacionrsi.modelo.Archivos;
import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {

    //    invoiceAndEstimate.exdb
    public static final String DATABASE_NAME = "BDArchivos.exdb";
    public static final int DATABASE_VERSION = 1;

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {

        sQLiteDatabase.execSQL("CREATE TABLE " + Archivos.TABLE_NAME + "("
                + Archivos.COLUMN_IDARCHIVO + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Archivos.COLUMN_NOMBRE + " TEXT,"
                + Archivos.COLUMN_PATH + " TEXT"
                + ")");
    }

    public void deleteItem(int item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Archivos.TABLE_NAME, Archivos.COLUMN_IDARCHIVO + " = ?",
                new String[]{String.valueOf(item)});
        db.close();
    }

    public long insertArchivo(String nombre ,String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Archivos.COLUMN_NOMBRE, nombre);
        values.put(Archivos.COLUMN_PATH, path);
        long id = db.insert(Archivos.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public int updateArchivos(String id, String nombre, String path , Context context) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Archivos.COLUMN_NOMBRE, nombre);
        values.put(Archivos.COLUMN_PATH, path);
        return db.update(Archivos.TABLE_NAME, values, Archivos.COLUMN_IDARCHIVO + " = ?",
                new String[]{String.valueOf(id)});
    }


    @SuppressLint("Range")
    public List<Archivos> getTodosArchivos() {
        List<Archivos> imgs = new ArrayList<>();
        String selectQuery = "SELECT * FROM "+Archivos.TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Archivos img = new Archivos();
                img.setIdArchivo(cursor.getInt(cursor.getColumnIndex(Archivos.COLUMN_IDARCHIVO)));
                img.setNombre(cursor.getString(cursor.getColumnIndex(Archivos.COLUMN_NOMBRE)));
                img.setPath(cursor.getString(cursor.getColumnIndex(Archivos.COLUMN_PATH)));
                imgs.add(img);
            } while (cursor.moveToNext());
        }
        db.close();
        return imgs;
    }




    @SuppressLint("Range")
    public Archivos getIdArchivos(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + Archivos.TABLE_NAME+" WHERE "+Archivos.COLUMN_IDARCHIVO+" = '"+id+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        Archivos img;
        if(cursor.getCount()==0){
            img = null;
        }else{
            cursor.moveToFirst();
            img = new Archivos();
            img.setIdArchivo(cursor.getInt(cursor.getColumnIndex(Archivos.COLUMN_IDARCHIVO)));
            img.setNombre(cursor.getString(cursor.getColumnIndex(Archivos.COLUMN_NOMBRE)));
            img.setPath(cursor.getString(cursor.getColumnIndex(Archivos.COLUMN_PATH)));
        }
        db.close();
        return img;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onConfigure(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.setForeignKeyConstraintsEnabled(true);
    }
}
