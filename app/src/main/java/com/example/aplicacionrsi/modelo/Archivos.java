package com.example.aplicacionrsi.modelo;


public class Archivos {
    public static final String TABLE_NAME = "Archivos";

    public static final String COLUMN_IDARCHIVO = "idarchivo";
    public static final String COLUMN_NOMBRE = "nombre";
   // public static final String COLUMN_BASE = "base";
    public static final String COLUMN_PATH = "path";

    private int idArchivo;
    private String nombre;
 //   private String base;
    private String path;


    public Archivos() {
    }

    public Archivos(int idArchivo, String nombre, String base ,String path) {
        this.idArchivo = idArchivo;
        this.nombre = nombre;
    //    this.base = base;
        this.path = path;
    }

    public void setIdArchivo(int idArchivo) {
    this.idArchivo = idArchivo;
    }
    public int getIdArchiivo() {
    return idArchivo;
    }

    public void setNombre(String nombre) {
    this.nombre = nombre;
    }
    public String getNombre() {
    return nombre;
    }


   /* public void setBase(String base) {
    this.base = base;
    }
    public String getBase() {
    return base;
    }*/

    public void setPath(String path) {
    this.path = path;
    }
    public String getPath() {
    return path;
    }

}
