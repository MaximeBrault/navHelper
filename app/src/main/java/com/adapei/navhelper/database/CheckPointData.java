package com.adapei.navhelper.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.adapei.navhelper.Checkpoint;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import timber.log.Timber;

public class CheckPointData {

    private final String nomBaseSQL = "checkpointData.sqlite";
    private SQLiteHelper access;
    private SQLiteDatabase bdd;
    private final String nomBase = "checkpoints";

    public CheckPointData(Context context) { this.access = new SQLiteHelper(context, nomBaseSQL); }

    /**
     * add a checkpoint to the base
     * @param cp the checkpoint to add
     */
    public void addCheckpoint(Checkpoint cp) {

        this.bdd = this.access.getWritableDatabase();
        String req = "INSERT INTO " + nomBase + "(latitude, longitude, name, prononciation, icone) VALUES ";
        req += "(" + cp.getLatitude() + ",";
        req += + cp.getLongitude() + ",";
        req += "\"" + cp.getDisplayName() + "\",";
        req += "\"" + cp.getPronunciation() + "\",";
        req += "\"" + cp.getPictureUri().toString() + "\");";

        try {
            this.bdd.execSQL(req);
        } catch(SQLiteConstraintException e) {
            Timber.e("addCheckpoint : Constraint failed");
        }
    }

    /**
     * get all of the checkpoints on the base
     * @return an arraylist of checkpoints
     */
    public ArrayList<Checkpoint> getAllCheckpoints() {
        ArrayList<Checkpoint> ret = new ArrayList<>();
        this.bdd = this.access.getReadableDatabase();
        String req = "SELECT * FROM " + nomBase;
        Cursor curseur = this.bdd.rawQuery(req, null);
        curseur.moveToFirst();
        for(int i = 0; i < curseur.getCount(); i++) {
            try {
                ret.add(new Checkpoint(
                        curseur.getDouble(0),
                        curseur.getDouble(1),
                        curseur.getString(2),
                        curseur.getString(3),
                        new URI(curseur.getString(4))));
            } catch(URISyntaxException e){
                Timber.e("getAllCheckpoints : adding to list failed");
            }
            curseur.moveToNext();
        }
        curseur.close();
        return ret;
    }

    /**
     * give the amount of checkpoints
     * @return an int
     */
    public int getNbCheckpoint() {
        this.bdd = this.access.getReadableDatabase();
        String req = "SELECT * FROM " + nomBase;
        Cursor curseur = this.bdd.rawQuery(req, null);
        int ret = curseur.getCount();
        curseur.close();
        return  ret;
    }

    /**
     * delete the give checkpoint
     * @param cp, checkpoint to remove
     */
    public void deleteCheckpoint(Checkpoint cp) {
        this.bdd = this.access.getWritableDatabase();
        String req = "DELETE FROM " + nomBase + " WHERE "
                + "name = '" + cp.getDisplayName() + "' AND "
                + "latitude = " + cp.getLatitude() + " AND "
                + "longitude = " + cp.getLongitude() + ";";
        System.out.println(req);
        try {
            this.bdd.execSQL(req);
        } catch (SQLiteConstraintException e) {
            System.out.println("deleteCheckpoint : Delete failed");
        }
    }

    /**
     * change the name of the given checkpoint
     * @param cp, the checkpoint to update
     * @param name, the new name
     */

    public void updateName(Checkpoint cp, String name) {
        this.bdd = this.access.getWritableDatabase();
        String req = "UPDATE " + nomBase + " SET "
                + "name = \"" + name + "\" "
                + " WHERE " + "icone = '" + cp.getPictureUri().toString() + "' AND "
                + "latitude = " + cp.getLatitude() + " AND "
                + "longitude = " + cp.getLongitude() + ";";
        try {
            this.bdd.execSQL(req);
        } catch (SQLiteConstraintException e) {
            System.out.println("updateName : Update failed");
        }
    }

    /**
     * change the pronunciation of the give checkpoint
     * @param cp, the checkpoint to update
     * @param pronunciation, the new pronounciation
     */

    public void updatePronunciation(Checkpoint cp, String pronunciation) {
        this.bdd = this.access.getWritableDatabase();
        String req = "UPDATE " + nomBase + " SET "
                + "prononciation = \"" + pronunciation + "\" "
                + " WHERE " + "name = '" + cp.getDisplayName() + "' AND "
                + "latitude = " + cp.getLatitude() + " AND "
                + "longitude = " + cp.getLongitude() + ";";
        try {
            this.bdd.execSQL(req);
        } catch (SQLiteConstraintException e) {
            System.out.println("updatePronounciation : Update failed");
        }
    }
}
