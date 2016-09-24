package com.openalliance_la.mdoctor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.openalliance_la.mdoctor.ManageDB.*;

import java.util.ArrayList;
import java.util.List;

public class clsDoctor {
    protected Context Context;
    protected int _id;
    protected String _name;
    protected String _phone;
    protected String _mobile;
    protected String _address;
    protected String _address_work;
    protected String _facebook;
    protected String _details;
    protected String _specialty;
    protected byte[] _photo = null;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_phone() {
        return _phone;
    }

    public void set_phone(String _phone) {
        this._phone = _phone;
    }

    public String get_mobile() {
        return _mobile;
    }

    public void set_mobile(String _mobile) {
        this._mobile = _mobile;
    }

    public String get_address() {
        return _address;
    }

    public void set_address(String _address) {
        this._address = _address;
    }

    public String get_address_work() {
        return _address_work;
    }

    public void set_address_work(String _address_work) {
        this._address_work = _address_work;
    }

    public String get_facebook() {
        return _facebook;
    }

    public void set_facebook(String _facebook) {
        this._facebook = _facebook;
    }

    public String get_details() {
        return _specialty;
    }

    public void set_specialty(String _specialty) {
        this._specialty = _specialty;
    }

    public String get_specialty() {
        return _specialty;
    }

    public void set_details(String _details) {
        this._details = _details;
    }

    public byte[] get_photo() {
        return _photo;
    }

    public void set_photo(byte[] _photo) {
        this._photo = _photo;
    }

    public void set_photo(Bitmap bmp) {
        this.set_photo(gl.BitmaptoByteArray(bmp));
    }

    public clsDoctor() { }

    public clsDoctor(Context Context) {
        this.Context = Context;
    }

    public clsDoctor(int _id, String _name, String _phone, String _mobile, String _address, String _address_work, String _facebook, String _details, String _specialty, byte[] _photo) {
        this._id = _id;
        this._name = _name;
        this._phone = _phone;
        this._mobile = _mobile;
        this._address = _address;
        this._address_work = _address_work;
        this._facebook = _facebook;
        this._details = _details;
        this._specialty = _specialty;
        this._photo = _photo;
    }

    public clsDoctor(Context Context, int DOCTOR_ID) {
        clsDoctor res_doctor = new clsDoctor(Context).getById(DOCTOR_ID);
        this._id = res_doctor.get_id();
        this._name = res_doctor.get_name();
        this._phone = res_doctor.get_phone();
        this._mobile = res_doctor.get_mobile();
        this._address = res_doctor.get_address();
        this._address_work = res_doctor.get_address_work();
        this._facebook = res_doctor.get_facebook();
        this._details = res_doctor.get_details();
        this._specialty = res_doctor.get_specialty();
        this._photo = res_doctor.get_photo();
    }

    public void AddRecord(clsDoctor NewRecord) {
        SQLiteDatabase db = new ManageDB(Context).getWritableDatabase();
        // Armar el Insert
        ContentValues values = new ContentValues();
        values.put(ColumnsDoctor.DOCTOR_NAME, NewRecord.get_name()); // Nombre
        values.put(ColumnsDoctor.DOCTOR_PHONE, NewRecord.get_phone()); // Telefono
        values.put(ColumnsDoctor.DOCTOR_MOBILE, NewRecord.get_mobile()); // Celular
        values.put(ColumnsDoctor.DOCTOR_ADDRESS, NewRecord.get_address()); // Address
        values.put(ColumnsDoctor.DOCTOR_ADDRESS_WORK, NewRecord.get_address_work()); // Address Work
        values.put(ColumnsDoctor.DOCTOR_FACEBOOK, NewRecord.get_facebook()); // Facebook
        values.put(ColumnsDoctor.DOCTOR_DETAILS, NewRecord.get_details()); // Detalles
        values.put(ColumnsDoctor.DOCTOR_SPECILTY, NewRecord.get_specialty()); // Specialidad Medica
        values.put(ColumnsDoctor.DOCTOR_PHOTO, NewRecord.get_photo()); // Detalles

        // Insertar registro
        db.insert(ManageDB.TABLE_DOCTORS, null, values);
        db.close();
    }
    public int AddRecord(String Name, String Phone, String Mobile, String Address, String Address_work, String Facebook, String Details, String Specialty, byte[] Photo, int intImg) {
        clsDoctor record = getByName(Name);
        Bitmap bmp = BitmapFactory.decodeResource(Context.getResources(), intImg);
        bmp = gl.scaleDown(bmp, 230, true);
        byte[] Img = gl.BitmaptoByteArray(bmp);

        if (record == null) {
            clsDoctor NewRecord = new clsDoctor();
            NewRecord.set_name(Name);
            NewRecord.set_phone(Phone);
            NewRecord.set_mobile(Mobile);
            NewRecord.set_address(Address);
            NewRecord.set_address_work(Address_work);
            NewRecord.set_facebook(Facebook);
            NewRecord.set_details(Details);
            NewRecord.set_specialty(Specialty);
            NewRecord.set_photo(Photo);
            AddRecord(NewRecord);
            record = getByName(Name);
        } else {
            ContentValues vals = new ContentValues();
            vals.put(ColumnsDoctor.DOCTOR_PHOTO, Img);
            Update(record.get_id(), vals);
        }
        return record.get_id();
    }

    // Obtener un Doctor
    public clsDoctor getById(int DOCTOR_ID) {
        clsDoctor result = null;
        List<clsDoctor> Categories = getRecords(DOCTOR_ID);
        if (Categories.size() > 0)
            result = Categories.get(0);
        return result;
    }
    public clsDoctor getByName(String Name) {
        clsDoctor result = null;
        List<Object[]> args = new ArrayList<Object[]>();
        args.add(new Object[]{ColumnsDoctor.DOCTOR_NAME, "=", Name});
        List<clsDoctor> res =  getRecords(args);
        if ( res.size() > 0){
            result = res.get(0);
        }
        return result;
    }

    // Obtener todas las categorias
    public List<clsDoctor> getAll() {
        return getRecords(new ArrayList<Object[]>());
    }

    public List<clsDoctor> getRecords(int DOCTOR_ID) {
        List<Object[]> args = new ArrayList<Object[]>();
        args.add(new Object[] {ColumnsDoctor.DOCTOR_ID, "=", DOCTOR_ID});
        return getRecords(args);
    }
    public List<clsDoctor> getRecords(Object[] arg) {
        List<Object[]> args = new ArrayList<Object[]>();
        args.add(new Object[] {arg[0], arg[1], arg[2]});
        return getRecords(args);
    }

    public List<clsDoctor> getRecords(List<Object[]> args) {
        List<clsDoctor> RecordList = new ArrayList<clsDoctor>();
        SQLiteDatabase db = new ManageDB(Context).getWritableDatabase();
        // Select All Query
        String selectQuery = "SELECT "
                + ColumnsDoctor.DOCTOR_ID + ","
                + ColumnsDoctor.DOCTOR_NAME + ","
                + ColumnsDoctor.DOCTOR_PHONE + ","
                + ColumnsDoctor.DOCTOR_MOBILE + ","
                + ColumnsDoctor.DOCTOR_ADDRESS + ","
                + ColumnsDoctor.DOCTOR_ADDRESS_WORK + ","
                + ColumnsDoctor.DOCTOR_FACEBOOK + ","
                + ColumnsDoctor.DOCTOR_DETAILS + ","
                + ColumnsDoctor.DOCTOR_SPECILTY + ","
                + ColumnsDoctor.DOCTOR_PHOTO
                + " FROM " + ManageDB.TABLE_DOCTORS;

        String WhereQuery = "";
        String _and = " and ";
        if (args.size() > 0)
            WhereQuery = " WHERE ";
        for (Object[] arg: args) {
            String field = arg[0].toString();
            String operator = arg[1].toString();
            Object value = arg[2];
            if (field != null && value != null){
                if (value instanceof String){
                    value = "'" + value + "'";
                }
                WhereQuery = WhereQuery + field + " " + operator + " " + value + _and;
            }
        }
        if (WhereQuery.length() > _and.length() && WhereQuery.substring(WhereQuery.length() - _and.length(), WhereQuery.length()).equals(_and)){
            WhereQuery = WhereQuery.substring(0, WhereQuery.length() - _and.length());
        }
        selectQuery = selectQuery + WhereQuery + " ORDER BY " + ColumnsDoctor.DOCTOR_NAME;

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                clsDoctor Record = new clsDoctor(
                        Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getBlob(9)
                );
                // Add Record
                RecordList.add(Record);
            } while (cursor.moveToNext());
        }
        db.close();
        return RecordList;
    }

    // Update
    public int Update(int record_id, ContentValues values) {
        SQLiteDatabase db = new ManageDB(Context).getWritableDatabase();
        return db.update(ManageDB.TABLE_DOCTORS, values, ColumnsDoctor.DOCTOR_ID + " = " + record_id, null);
    }

    // Delete
    public void Delete(int record_id) {
        ManageDB.DeleteRecord(Context, ManageDB.TABLE_DOCTORS, record_id);
    }

    // Count Records
    public int CountRecords() {
        return CountRecords(new ArrayList<Object[]>());
    }
    public int CountRecords(Object[] arg) {
        List<Object[]> args = new ArrayList<Object[]>();
        args.add(new Object[]{arg[0], arg[1], arg[2]});
        return CountRecords(args);
    }
    public int CountRecords(List<Object[]> args) {
        SQLiteDatabase db = new ManageDB(Context).getWritableDatabase();

        String selectQuery = "SELECT " + ColumnsDoctor.DOCTOR_ID + " FROM " + ManageDB.TABLE_DOCTORS;
        String WhereQuery = "";
        String _and = " and ";
        if (args.size() > 0)
            WhereQuery = " WHERE ";
        for (Object[] arg: args) {
            String field = arg[0].toString();
            String operator = arg[1].toString();
            Object value = arg[2];
            if (field != null && value != null){
                if (value instanceof String){
                    value = "'" + value + "'";
                }
                WhereQuery = WhereQuery + field + " " + operator + " " + value + _and;
            }
        }
        if (WhereQuery.length() > _and.length() && WhereQuery.substring(WhereQuery.length() - _and.length(), WhereQuery.length()).equals(_and)){
            WhereQuery = WhereQuery.substring(0, WhereQuery.length() - _and.length());
        }
        selectQuery = selectQuery + WhereQuery;

        Cursor cursor = db.rawQuery(selectQuery, null);
        int result = cursor.getCount();
        db.close();
        return result;
    }
}