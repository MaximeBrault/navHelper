package com.adapei.navhelper.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.adapei.navhelper.PhoneNumber;

import java.util.ArrayList;

import timber.log.Timber;

public class PhoneData {
    private final String nomBaseSQL = "phoneData.sqlite";
    private SQLiteHelper access;
    private SQLiteDatabase bdd;
    private final String nomBase = "phoneNumbers";

    public PhoneData(Context context) {
        this.access = new SQLiteHelper(context, nomBaseSQL);
    }

    /**
     * add a phone to the base
     * @param phoneNumber the phone number
     */
    public void addPhone(PhoneNumber phoneNumber) {
        this.bdd = this.access.getWritableDatabase();
        String req = "INSERT INTO " + nomBase + "(phoneNumber, nameNumber) VALUES ";
        req += "(\"" + phoneNumber.getNumber() + "\",";
        req += "\"" + phoneNumber.getName() + "\")";

        try {
            this.bdd.execSQL(req);
        } catch(SQLiteConstraintException e) {
            Timber.e("addPhone : Constraint failed");
        }
    }

    /**
     * check if a phonenumber is already in the base
     * @param phoneNumber the phonenumber to check
     * @return true if it's in, false otherwise
     */
    public boolean isIn(PhoneNumber phoneNumber) {
        boolean ret = false;
        this.bdd = this.access.getReadableDatabase();
        String req = "SELECT * FROM " + nomBase;
        req += " WHERE phoneNumber = \"" + phoneNumber.getNumber() + "\"";
        Cursor curseur = this.bdd.rawQuery(req, null);
        curseur.moveToFirst();
        if(!curseur.isAfterLast()) {
            ret = true;
        }
        curseur.close();
        return ret;
    }

    /**
     * get all of the phonenumbers on the base
     * @return an arraylist of phonenumber
     */
    public ArrayList<PhoneNumber> getAllPhoneNumbers() {
        ArrayList<PhoneNumber> ret = new ArrayList<>();
        this.bdd = this.access.getReadableDatabase();
        String req = "SELECT * FROM " + nomBase;
        Cursor curseur = this.bdd.rawQuery(req, null);
        curseur.moveToFirst();
        for(int i = 0; i < curseur.getCount(); i++) {
            ret.add(new PhoneNumber(curseur.getString(0), curseur.getString(1)));
            curseur.moveToNext();
        }
        curseur.close();
        return ret;
    }

    /**
     * get a phone from it's number
     * @param  phone the phone number
     * @return the object phone number
     */
    public PhoneNumber getPhoneFromNumber(String phone) {
        PhoneNumber ret;
        this.bdd = this.access.getReadableDatabase();
        String req = "SELECT * FROM " + nomBase + " WHERE phoneNumber = \"" + phone + "\"";
        Cursor curseur = this.bdd.rawQuery(req, null);
        curseur.moveToFirst();
        ret = new PhoneNumber(curseur.getString(0), curseur.getString(1));
        curseur.close();
        return ret;
    }

    /**
     * remove on specific phonenumber
     * @param phoneNumber the phonenumber to remove
     */
    public void removePhone(PhoneNumber phoneNumber) {
        this.bdd = this.access.getWritableDatabase();
        String req = "DELETE FROM " + nomBase + " WHERE phoneNumber = \"" + phoneNumber.getNumber() + "\"";
        try {
            this.bdd.execSQL(req);
        } catch (SQLiteConstraintException e) {
            Timber.e(e);
        }
    }

    /**
     * check if the database is empty
     * @return true if it is
     */
    public boolean noPhoneNumber () {
        boolean ret = false;
        this.bdd = this.access.getReadableDatabase();
        String req = "SELECT * FROM " + nomBase;
        Cursor curseur = this.bdd.rawQuery(req, null);
        curseur.moveToFirst();
        if(!curseur.isAfterLast()) {
            ret = true;
        }
        curseur.close();
        return ret;
    }
}
