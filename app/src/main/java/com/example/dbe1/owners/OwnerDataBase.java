package com.example.dbe1.owners;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

public class OwnerDataBase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "CRICKET";
    public static final int DATABASE_ID = 1;

    public static final String TABLE_OWNER = "owner";
    public static final String KEY_OWNER_ID = "owner_id";
    public static final String KEY_TEAM_ID = "team_id";
    public static final String KEY_OWNER_NAME = "owner_name";



    public OwnerDataBase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_ID);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE "+ TABLE_OWNER+"(" +
//                KEY_OWNER_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
//                KEY_TEAM_ID +" INTEGER, "+
//                KEY_OWNER_NAME +" TEXT "+
//                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addOwner(int teamID, String ownerName) {
        SQLiteDatabase db = getWritableDatabase();

        // Get the maximum owner ID from the database
        Cursor cursor = db.rawQuery("SELECT MAX(" + KEY_OWNER_ID + ") FROM " + TABLE_OWNER, null);
        int maxId = 0;
        if (cursor != null && cursor.moveToFirst()) {
            maxId = cursor.getInt(0);
            cursor.close();
        }

        ContentValues values = new ContentValues();
        // Increment the maximum ID by 1 for the new owner
        values.put(KEY_OWNER_ID, maxId + 1);
        values.put(KEY_TEAM_ID, teamID);
        values.put(KEY_OWNER_NAME, ownerName);

        long result = db.insert(TABLE_OWNER, null, values);

        if (result != -1) {
            Log.d("OwnerDataBase", "Owner added successfully");
        } else {
            Log.e("OwnerDataBase", "Failed to add owner");
        }
    }


    public ArrayList<OwnerAttributeClass> fetchAllOwner()
    {
        ArrayList<OwnerAttributeClass> getOwnerData = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_OWNER,null);
        while(cursor.moveToNext())
        {
            OwnerAttributeClass model = new OwnerAttributeClass();
            model.owner_id = cursor.getInt(0);
            model.team_id = cursor.getInt(1);
            model.owner_name = cursor.getString(2);

            getOwnerData.add(model);
        }
        return getOwnerData;
    }
    public boolean deleteOwner(int ownerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_OWNER, KEY_OWNER_ID + "=?", new String[]{String.valueOf(ownerId)});
        if (rowsAffected > 0) {
            Log.d("OwnerDataBase", "Owner deleted successfully");
        } else {
            Log.e("OwnerDataBase", "Failed to delete owner");
        }
        reassignOwnerIds(db); // Call reassignOwnerIds after deletion
        return true; // Assuming deletion always succeeds
    }

    private void reassignOwnerIds(SQLiteDatabase db) {
        // Retrieve all owners after deletion
        ArrayList<OwnerAttributeClass> ownersList = fetchAllOwner();

        int newOwnerId = 1; // Start with ID 1
        for (int i = 0; i < ownersList.size(); i++) {
            int oldOwnerId = ownersList.get(i).owner_id; // Old owner ID from the list
            if (newOwnerId != oldOwnerId) {
                ContentValues values = new ContentValues();
                values.put(KEY_OWNER_ID, newOwnerId);
                db.update(TABLE_OWNER, values, KEY_OWNER_ID + "=?", new String[]{String.valueOf(oldOwnerId)});
            }
            newOwnerId++; // Increment ID for the next owner
        }
    }

}
