package com.openalliance_la.mdoctor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ManageDB extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 2;
    // Database Name
    private static final String DATABASE_NAME = "mdoctor_db";

    // Tabla Categoria
    public static final String TABLE_DOCTORS = "Doctor";
    // Columnas
    public static class ColumnsDoctor{
        public static final String DOCTOR_ID = "id";
        public static final String DOCTOR_NAME = "name";
        public static final String DOCTOR_PHONE = "phone";
        public static final String DOCTOR_MOBILE = "mobile";
        public static final String DOCTOR_ADDRESS = "address";
        public static final String DOCTOR_ADDRESS_WORK = "address_work";
        public static final String DOCTOR_FACEBOOK = "facebook";
        public static final String DOCTOR_DETAILS = "details";
        public static final String DOCTOR_SPECILTY = "specialty";
        public static final String DOCTOR_PHOTO = "photo";
    }

    public ManageDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear la tabla Categorias
        String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_DOCTORS + "("
                + ColumnsDoctor.DOCTOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ColumnsDoctor.DOCTOR_NAME + " VARCHAR(256) NOT NULL,"
                + ColumnsDoctor.DOCTOR_PHONE + " VARCHAR(10) NULL,"
                + ColumnsDoctor.DOCTOR_MOBILE + " VARCHAR(10) NULL,"
                + ColumnsDoctor.DOCTOR_ADDRESS + " VARCHAR(256) NULL,"
                + ColumnsDoctor.DOCTOR_ADDRESS_WORK + " VARCHAR(256) NULL,"
                + ColumnsDoctor.DOCTOR_FACEBOOK + " VARCHAR(256) NULL,"
                + ColumnsDoctor.DOCTOR_DETAILS + " VARCHAR(600) NULL,"
                + ColumnsDoctor.DOCTOR_SPECILTY + " VARCHAR(100) NULL,"
                + ColumnsDoctor.DOCTOR_PHOTO + " BLOB"
                + ")";
        db.execSQL(CREATE_CATEGORY_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eliminar las tablas Cuando se cambie la version de la base de datos
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCTORS);

        // Create tables again
        onCreate(db);
    }

    // Metodo para eliminar un registro de la base de datos
    public static void DeleteRecord(Context context, String TableName, int record_id) {
        SQLiteDatabase db = new ManageDB(context).getWritableDatabase();
        db.delete(TableName, "id" + " = " + record_id, null);
        db.close();
    }
}