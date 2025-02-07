package com.example.dbe1.teams;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

public class TeamDataBase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "CRICKET";
    private static final int DATABASE_ID = 1;

    private static final String TABLE_PLAYER = "players";
    private static final String TABLE_TEAM = "team";
    private static final String TABLE_COACH = "coaches";

    private static final String KEY_PLAYER_ID = "player_id";
    private static final String KEY_TEAM_ID = "team_id";
    private static final String KEY_JERSEY_NO = "jersey_no";
    private static final String KEY_SPECIFICATION = "player_specification";
    private static final String KEY_PLAYER_NAME = "player_name";
    private static final String KEY_HEIGHT = "player_height";
    private static final String KEY_WEIGHT = "player_weight";
    private static final String KEY_MATCHES = "player_matches";
    private static final String KEY_RUNS = "player_runs";
    private static final String KEY_WICKETS = "player_wickets";
    private static final String KEY_CATCHES = "player_catches";
    private static final String KEY_TEAM_NAME = "team_name";
    private static final String KEY_TEAM_SPONSOR = "team_sponsor";
    private static final String KEY_COACH_NAME = "coach_name";
    private static final String KEY_EXPERIENCE = "coach_experience";
    private static final String KEY_COACH_SPECIFICATION = "coach_specification";
    private static final String KEY_COACH_ID = "coach_id";

    public static final String TABLE_OWNER = "owner";
    public static final String KEY_OWNER_ID = "owner_id";
    public static final String KEY_OWNER_NAME = "owner_name";

    public TeamDataBase(@Nullable Context context) {
        super(context, DATABASE_NAME, null,DATABASE_ID);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("checkerror","ok 1");
        db.execSQL("CREATE TABLE " + TABLE_TEAM + " ( "+
                KEY_TEAM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_TEAM_NAME + " TEXT,"+
                KEY_TEAM_SPONSOR + " TEXT"+")");

        db.execSQL("CREATE TABLE " + TABLE_PLAYER+ " ( "+
                KEY_PLAYER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_TEAM_ID + " INTEGER NOT NULL," +
                KEY_JERSEY_NO + " INTEGER NOT NULL," +
                KEY_PLAYER_NAME + " TEXT NOT NULL," +
                KEY_SPECIFICATION + " TEXT NOT NULL," +
                KEY_WEIGHT + " REAL NOT NULL," +
                KEY_HEIGHT + " REAL NOT NULL,"+
                KEY_MATCHES + " INTEGER," +
                KEY_RUNS + " INTEGER," +
                KEY_WICKETS + " INTEGER," +
                KEY_CATCHES + " INTEGER" +")");

        db.execSQL("CREATE TABLE " +TABLE_COACH+" ( " +
                KEY_TEAM_ID+" INTEGER," +
                KEY_COACH_NAME+" TEXT," +
                KEY_EXPERIENCE+" INTEGER," +
                KEY_COACH_SPECIFICATION+" TEXT,"+
                KEY_COACH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT"+" )");

        db.execSQL("CREATE TABLE "+ TABLE_OWNER+"(" +
                KEY_OWNER_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                KEY_TEAM_ID +" INTEGER, "+
                KEY_OWNER_NAME +" TEXT "+ ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1)
    {
//        Log.d("ContactInfo ","Check2");
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_TEAM);
        onCreate(db);
    }
    //        ArrayList<TeamAttributeClass> list = new ArrayList<>();

    public void addTeam(String teamName, String teamSponsorName) {
        SQLiteDatabase db = getWritableDatabase();

        // Get the maximum team ID from the database
        Cursor cursor = db.rawQuery("SELECT MAX(" + KEY_TEAM_ID + ") FROM " + TABLE_TEAM, null);
        int maxId = 0;
        if (cursor != null && cursor.moveToFirst()) {
            maxId = cursor.getInt(0);
            cursor.close();
        }

        ContentValues values = new ContentValues();
        // Increment the maximum ID by 1 for the new team
        values.put(KEY_TEAM_ID, maxId + 1);
        values.put(KEY_TEAM_NAME, teamName);
        values.put(KEY_TEAM_SPONSOR, teamSponsorName);

        long result = db.insert(TABLE_TEAM, null, values);

        if (result != -1) {
            Log.d("TeamDataBase", "Team added successfully");
        } else {
            Log.e("TeamDataBase", "Failed to add team");
        }
    }

    public ArrayList<TeamAttributeClass> fetchAllTeams()
    {
        ArrayList<TeamAttributeClass> getTeamData = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_TEAM,null);
        while(cursor.moveToNext())
        {
            TeamAttributeClass model = new TeamAttributeClass();
            model.teamId = cursor.getInt(0);
            model.teamName = cursor.getString(1);
            model.teamSposor = cursor.getString(2);
            getTeamData.add(model);
        }
        return getTeamData;
    }
    public void updateData(int team_id,String team_name,String team_sponsor)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String str = "UPDATE "+TABLE_TEAM+
                " SET "+KEY_TEAM_NAME+" = '"+team_name+"' , "+
                KEY_TEAM_SPONSOR+" = '"+team_sponsor+"' "+
                " WHERE "+KEY_TEAM_ID+" = "+team_id;
        db.execSQL(str);
    }
    public boolean deleteTeam(int teamId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete players of the team
        db.delete(TABLE_PLAYER, KEY_TEAM_ID + "=?", new String[]{String.valueOf(teamId)});
        // Delete coach of the team
        db.delete(TABLE_COACH, KEY_TEAM_ID + "=?", new String[]{String.valueOf(teamId)});
        // Delete owner of the team
        db.delete(TABLE_OWNER, KEY_TEAM_ID + "=?", new String[]{String.valueOf(teamId)});
        // Delete the team itself
        db.delete(TABLE_TEAM, KEY_TEAM_ID + "=?", new String[]{String.valueOf(teamId)});

        // After deletion, update team IDs to start from 1
        reassignTeamIds(db);

        return true; // Assuming the deletion always succeeds
    }

    private void reassignTeamIds(SQLiteDatabase db) {
        // Retrieve all teams after deletion
        ArrayList<TeamAttributeClass> teamsList = fetchAllTeams();

        int newTeamId = 1; // Start with ID 1
        for (int i = 0; i < teamsList.size(); i++) {
            int oldTeamId = teamsList.get(i).teamId; // Old team ID from the list
            if (newTeamId != oldTeamId) {
                ContentValues values = new ContentValues();
                values.put(KEY_TEAM_ID, newTeamId);
                db.update(TABLE_TEAM, values, KEY_TEAM_ID + "=?", new String[]{String.valueOf(oldTeamId)});
            }
            newTeamId++; // Increment ID for the next team
        }
    }





}
