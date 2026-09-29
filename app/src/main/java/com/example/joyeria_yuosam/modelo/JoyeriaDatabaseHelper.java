package com.example.joyeria_yuosam.modelo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Persistencia Local con SQLite utilizando SQLiteOpenHelper.
 * Base de Datos: joyeria_yousam.db
 * Tabla: joyas
 * 
 * Autor: TDA Joyería YouSam
 */
public class JoyeriaDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "joyeria_yousam.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_JOYAS = "joyas";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_CATEGORIA = "categoria";
    public static final String COLUMN_MATERIAL = "material";
    public static final String COLUMN_PRECIO = "precio";
    public static final String COLUMN_STOCK = "stock";

    private static final String CREATE_TABLE_JOYAS =
            "CREATE TABLE " + TABLE_JOYAS + " (" +
                    COLUMN_ID + " TEXT PRIMARY KEY, " +
                    COLUMN_NOMBRE + " TEXT NOT NULL, " +
                    COLUMN_CATEGORIA + " TEXT NOT NULL, " +
                    COLUMN_MATERIAL + " TEXT NOT NULL, " +
                    COLUMN_PRECIO + " REAL NOT NULL, " +
                    COLUMN_STOCK + " INTEGER NOT NULL" +
                    ");";

    public JoyeriaDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_JOYAS);
        insertarDatosSemilla(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOYAS);
        onCreate(db);
    }

    /**
     * Inserta datos iniciales de ejemplo si la base de datos es recién creada.
     */
    private void insertarDatosSemilla(SQLiteDatabase db) {
        List<ProductoJoya> semilla = new ArrayList<>();
        semilla.add(new ProductoJoya("JOY-001", "Anillo Solitario Imperial", "Anillo", "Oro 24K", 3500.00, 8));
        semilla.add(new ProductoJoya("JOY-002", "Collar Corazón Esmeralda", "Collar", "Platinium & Esmeralda", 4800.00, 5));
        semilla.add(new ProductoJoya("JOY-003", "Pulsera Tenis Diamantes", "Pulsera", "Plata .925 & Diamante", 2200.00, 2));
        semilla.add(new ProductoJoya("JOY-004", "Aretes Perla Del Mar", "Aretes", "Oro Rosa & Perla", 1250.00, 10));
        semilla.add(new ProductoJoya("JOY-005", "Reloj Chrono Executive", "Reloj", "Platino", 8900.00, 3));
        semilla.add(new ProductoJoya("JOY-006", "Dije Cruz Estelar", "Dije", "Oro 24K", 980.00, 1));

        for (ProductoJoya p : semilla) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, p.getId());
            values.put(COLUMN_NOMBRE, p.getNombre());
            values.put(COLUMN_CATEGORIA, p.getCategoria());
            values.put(COLUMN_MATERIAL, p.getMaterial());
            values.put(COLUMN_PRECIO, p.getPrecio());
            values.put(COLUMN_STOCK, p.getStock());
            db.insert(TABLE_JOYAS, null, values);
        }
    }

    /**
     * Carga todos los registros de la tabla 'joyas' en el contenedor TDA Joyeria.
     */
    public void cargarEnJoyeria(Joyeria joyeria) {
        if (joyeria == null) return;
        joyeria.limpiar();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_JOYAS, null, null, null, null, null, COLUMN_ID + " ASC");

        if (cursor != null) {
            try {
                int idxId = cursor.getColumnIndexOrThrow(COLUMN_ID);
                int idxNombre = cursor.getColumnIndexOrThrow(COLUMN_NOMBRE);
                int idxCat = cursor.getColumnIndexOrThrow(COLUMN_CATEGORIA);
                int idxMat = cursor.getColumnIndexOrThrow(COLUMN_MATERIAL);
                int idxPrecio = cursor.getColumnIndexOrThrow(COLUMN_PRECIO);
                int idxStock = cursor.getColumnIndexOrThrow(COLUMN_STOCK);

                while (cursor.moveToNext()) {
                    String id = cursor.getString(idxId);
                    String nombre = cursor.getString(idxNombre);
                    String categoria = cursor.getString(idxCat);
                    String material = cursor.getString(idxMat);
                    double precio = cursor.getDouble(idxPrecio);
                    int stock = cursor.getInt(idxStock);

                    ProductoJoya joya = new ProductoJoya(id, nombre, categoria, material, precio, stock);
                    joyeria.agregarJoya(joya);
                }
            } finally {
                cursor.close();
            }
        }
    }

    /**
     * Inserta un nuevo objeto ProductoJoya en la base de datos SQLite.
     */
    public boolean insertarJoya(ProductoJoya p) {
        if (p == null) return false;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, p.getId());
        values.put(COLUMN_NOMBRE, p.getNombre());
        values.put(COLUMN_CATEGORIA, p.getCategoria());
        values.put(COLUMN_MATERIAL, p.getMaterial());
        values.put(COLUMN_PRECIO, p.getPrecio());
        values.put(COLUMN_STOCK, p.getStock());

        long resultado = db.insert(TABLE_JOYAS, null, values);
        return resultado != -1;
    }

    /**
     * Actualiza todos los campos modificables de una joya existente.
     */
    public boolean actualizarJoya(ProductoJoya p) {
        if (p == null) return false;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NOMBRE, p.getNombre());
        values.put(COLUMN_CATEGORIA, p.getCategoria());
        values.put(COLUMN_MATERIAL, p.getMaterial());
        values.put(COLUMN_PRECIO, p.getPrecio());
        values.put(COLUMN_STOCK, p.getStock());

        int filasAfectadas = db.update(TABLE_JOYAS, values, COLUMN_ID + " = ?", new String[]{p.getId()});
        return filasAfectadas > 0;
    }

    /**
     * Actualiza únicamente el valor del stock para un producto en SQLite.
     */
    public boolean actualizarStockJoya(String id, int nuevoStock) {
        if (id == null) return false;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_STOCK, nuevoStock);

        int filasAfectadas = db.update(TABLE_JOYAS, values, COLUMN_ID + " = ?", new String[]{id});
        return filasAfectadas > 0;
    }

    /**
     * Elimina un registro de la tabla 'joyas' por su ID.
     */
    public boolean eliminarJoya(String id) {
        if (id == null) return false;
        SQLiteDatabase db = this.getWritableDatabase();
        int filasAfectadas = db.delete(TABLE_JOYAS, COLUMN_ID + " = ?", new String[]{id});
        return filasAfectadas > 0;
    }
}
