package com.adapei.navhelper.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.adapei.navhelper.Destination;

import java.util.ArrayList;

import timber.log.Timber;

public class DestinationData {
    private final String nomBaseSQL = "destinationData.sqlite";
    private SQLiteHelper access;
    private SQLiteDatabase bdd;
    private final String nomBase = "destinations";
    private Context context;

    public DestinationData(Context context) {
        this.access = new SQLiteHelper(context, nomBaseSQL);
        this.context = context;
    }

    /**
     * add a destination to the base
     * @param destination the destination to add
     */
    public void addDestination(Destination destination) {
        if(!isIn(destination)) {
            this.bdd = this.access.getWritableDatabase();
            String req = "INSERT INTO " + nomBase + "(name, nickname, icone, latitude, longitude, prononciation, foyer) VALUES ";
            req += "(\"" + destination.getDisplayName() + "\",";
            req += "\"" + destination.getNickname() + "\",";
            req += "\"" + destination.getRepresentation() + "\",";
            req += +destination.getLatitude() + ",";
            req += +destination.getLongitude() + ",";
            req += "\"" + destination.getPronunciation() + "\",";
            req += "\"" + destination.getFoyer() + "\");";

            try {
                this.bdd.execSQL(req);
            } catch (SQLiteConstraintException e) {
                Timber.e("addDestination : Constraint failed");
            }
        } else {
            Toast.makeText(this.context, "Destination déjà entrée", Toast.LENGTH_SHORT).show();
            System.out.println("addDestination() : Destination déjà présente");
        }
    }

    /**
     * check if the given destination is already in the database
     * @param destination, the destination to check
     * @return true if it is
     */
    public boolean isIn(Destination destination) {
        boolean ret = false;
        this.bdd = this.access.getReadableDatabase();
        String req = "SELECT * FROM " + nomBase + " WHERE "
                + "latitude = " + destination.getLatitude() + " AND "
                + "longitude = " + destination.getLongitude()  + " AND "
                + "foyer = \"" + destination.getFoyer() + "\"";
        Cursor curseur = this.bdd.rawQuery(req, null);
        curseur.moveToFirst();
        if(!curseur.isAfterLast()) {
            ret = true;
        }
        curseur.close();
        return ret;
    }

    /**
     * get all of the destination on the base
     * @return an arraylist of phonenumber
     */
    public ArrayList<Destination> getAllDestination() {
        ArrayList<Destination> ret = new ArrayList<>();
        this.bdd = this.access.getReadableDatabase();
        String req = "SELECT * FROM " + nomBase;
        Cursor curseur = this.bdd.rawQuery(req, null);
        curseur.moveToFirst();
        for(int i = 0; i < curseur.getCount(); i++) {
            ret.add(new Destination(
                    curseur.getString(1),
                    curseur.getString(2),
                    curseur.getString(3),
                    curseur.getDouble(4),
                    curseur.getDouble(5),
                    curseur.getString(6),
                    curseur.getString(7)));
            curseur.moveToNext();
        }
        curseur.close();
        return ret;
    }

    /**
     * give all the destination link to the given cluster
     * @param foyer, the cluster
     * @return all the destination
     */
    public ArrayList<Destination> getAllDestinationFoyer(String foyer) {
        ArrayList<Destination> ret = new ArrayList<>();
        this.bdd = this.access.getReadableDatabase();
        String req = "SELECT * FROM " + nomBase + " WHERE foyer = \"" + foyer + "\"";
        Cursor curseur = this.bdd.rawQuery(req, null);
        curseur.moveToFirst();
        for(int i = 0; i < curseur.getCount(); i++) {
            ret.add(new Destination(
                    curseur.getString(1),
                    curseur.getString(2),
                    curseur.getString(3),
                    curseur.getDouble(4),
                    curseur.getDouble(5),
                    curseur.getString(6),
                    curseur.getString(7)));
            curseur.moveToNext();
        }
        curseur.close();
        return ret;
    }

    /**
     * give the amount of destination in the database
     * @return an int
     */
    public int getNbDestination() {
        this.bdd = this.access.getReadableDatabase();
        String req = "SELECT * FROM " + nomBase;
        Cursor curseur = this.bdd.rawQuery(req, null);
        int ret = curseur.getCount();
        curseur.close();
        return  ret;
    }

    /**
     * give the id of the last destination enterred
     * @return the id
     */
    public int getLastId(){
        this.bdd = this.access.getReadableDatabase();
        String req = "SELECT id FROM " + nomBase + " ORDER BY id DESC;";
        Cursor curseur = this.bdd.rawQuery(req, null);
        int ret = curseur.getInt(0);
        curseur.close();
        return  ret;
    }

    /**
     * delete the given destination
     * @param destinationSelected, the destination to delete
     */
    public void deleteDestination(Destination destinationSelected) {
        this.bdd = this.access.getWritableDatabase();
        String req = "DELETE FROM " + nomBase + " WHERE "
                + "name = \"" + destinationSelected.getDisplayName() + "\" AND "
                + "latitude = " + destinationSelected.getLatitude() + " AND "
                + "longitude = " + destinationSelected.getLongitude()  + " AND "
                + "nickname = \"" + destinationSelected.getNickname() + "\" AND "
                + "foyer = \"" + destinationSelected.getFoyer() + "\"";
        try {
            this.bdd.execSQL(req);
        } catch (SQLiteConstraintException e) {
            System.out.println("deleteDestination : Delete failed");
        }
    }

    /**
     * update the nickname of the given destination
     * @param destinationSelected, the destination to update
     * @param nickname, the new nickname
     */
    public void updateSurname(Destination destinationSelected, String nickname) {
        this.bdd = this.access.getWritableDatabase();
        String req = "UPDATE " + nomBase + " SET "
                + "nickname = \"" + nickname + "\" "
                + "WHERE name =\"" + destinationSelected.getDisplayName() + "\" AND "
                + "latitude = " + destinationSelected.getLatitude() + " AND "
                + "longitude = " + destinationSelected.getLongitude() + " AND "
                + "foyer = \"" + destinationSelected.getFoyer() + "\"";
        try {
            this.bdd.execSQL(req);
        } catch (SQLiteConstraintException e) {
            System.out.println("updateSurname : Update failed");
        }
    }

    /**
     * update the pronounciation of the given destination
     * @param destinationSelected, the destination to update
     * @param pronunciation, the new pronounciation
     */
    public void updatePronunciation(Destination destinationSelected, String pronunciation) {
        this.bdd = this.access.getWritableDatabase();
        String req = "UPDATE " + nomBase + " SET "
                + "prononciation = \"" + pronunciation + "\" "
                + "WHERE name =\"" + destinationSelected.getDisplayName() + "\" AND "
                + "latitude = " + destinationSelected.getLatitude() + " AND "
                + "longitude = " + destinationSelected.getLongitude() + " AND "
                + "foyer = \"" + destinationSelected.getFoyer() + "\"";
        try {
            this.bdd.execSQL(req);
        } catch (SQLiteConstraintException e) {
            System.out.println("updateSurname : Update failed");
        }
    }
}
