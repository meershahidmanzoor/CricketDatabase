package com.example.dbe1.coaches;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

public class CoachDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CRICKET";
    private static final int DATABASE_ID = 1;

    private static final String TABLE_COACH = "coaches";
    private static final String KEY_TEAM_ID = "team_id";
    private static final String KEY_COACH_NAME = "coach_name";
    private static final String KEY_EXPERIENCE = "coach_experience";
    private static final String KEY_SPECIFICATION = "coach_specification";
    private static final String KEY_COACH_ID = "coach_id";

    public CoachDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_ID);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE " +TABLE_COACH+" (" +
//                KEY_TEAM_ID+" INTEGER" +
//                KEY_COACH_NAME+"TEXT" +
//                KEY_EXPERIENCE+"INTEGER" +
//                KEY_SPECIFICATION+"TEXT"+
//                KEY_COACH_ID+ "INTEGER PRIMARY KEY AUTOINCREMENT"
//                +" )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_COACH);
        onCreate(db);
    }

    public void addCoach(int teamID, String coachName, int experience, String specification) {
        SQLiteDatabase db = getWritableDatabase();

        // Get the maximum coach ID from the database
        Cursor cursor = db.rawQuery("SELECT MAX(" + KEY_COACH_ID + ") FROM " + TABLE_COACH, null);
        int maxId = 0;
        if (cursor != null && cursor.moveToFirst()) {
            maxId = cursor.getInt(0);
            cursor.close();
        }

        ContentValues values = new ContentValues();
        // Increment the maximum ID by 1 for the new coach
        values.put(KEY_COACH_ID, maxId + 1);
        values.put(KEY_TEAM_ID, teamID);
        values.put(KEY_COACH_NAME, coachName);
        values.put(KEY_EXPERIENCE, experience);
        values.put(KEY_SPECIFICATION, specification);

        long result = db.insert(TABLE_COACH, null, values);

        if (result != -1) {
            Log.d("CoachDatabase", "Coach added successfully");
        } else {
            Log.e("CoachDatabase", "Failed to add coach");
        }
    }


    public ArrayList<CoachAttributeCLass> fetchAllCoach()
    {
        ArrayList<CoachAttributeCLass> getCoachData = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_COACH,null);
        while(cursor.moveToNext())
        {
            CoachAttributeCLass model = new CoachAttributeCLass();
            model.team_id = cursor.getInt(0);
            model.coach_name = cursor.getString(1);
            model.experience = cursor.getInt(2);
            model.specification = cursor.getString(3);
            model.coach_id = cursor.getInt(4);
            getCoachData.add(model);
        }
        return getCoachData;
    }
    public void updateData(int id,String name,String experience,String specification)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String str = "UPDATE "+TABLE_COACH+ " SET "+
                KEY_COACH_NAME+" = '"+name+"' , "+
                KEY_EXPERIENCE+" = '"+experience+"' ,"+
                KEY_SPECIFICATION+" = '"+specification+"' "+
                " WHERE "+KEY_COACH_ID+" = "+id;
        db.execSQL(str);
    }
    public boolean deleteCoach(int coachId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_COACH, KEY_COACH_ID + "=?", new String[]{String.valueOf(coachId)});
        if (rowsAffected > 0) {
            Log.d("CoachDatabase", "Coach deleted successfully");
        } else {
            Log.e("CoachDatabase", "Failed to delete coach");
        }
        reassignCoachIds(db);
        return true; // Assuming the deletion always succeeds
    }

    private void reassignCoachIds(SQLiteDatabase db) {
        // Retrieve all coaches after deletion
        ArrayList<CoachAttributeCLass> coachesList = fetchAllCoach();

        int newCoachId = 1; // Start with ID 1
        for (int i = 0; i < coachesList.size(); i++) {
            int oldCoachId = coachesList.get(i).coach_id; // Old coach ID from the list
            if (newCoachId != oldCoachId) {
                ContentValues values = new ContentValues();
                values.put(KEY_COACH_ID, newCoachId);
                db.update(TABLE_COACH, values, KEY_COACH_ID + "=?", new String[]{String.valueOf(oldCoachId)});
            }
            newCoachId++; // Increment ID for the next coach
        }
    }

}
