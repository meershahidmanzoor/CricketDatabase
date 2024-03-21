package com.example.dbe1.teams;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dbe1.R;
import com.example.dbe1.customview.CustomViewForPerson;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;

public class TeamsViewActivity extends AppCompatActivity {

    ListView listViewTeam;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams_view);
        Log.d("errorcheck","ok 1");

        FloatingActionButton fb = findViewById(R.id.float_add_new_team);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),GetTeamValueByUser.class));
            }
        });
        FloatingActionsMenu fab = findViewById(R.id.fabMenuTeam);
//        fab.collapse();
//        findViewById(R.id.listViewTeam).setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                Log.d("errorcheck","ok 4");
//                fab.collapse();
//                return true;
//            }
//        });
        Log.d("errorcheck","ok 5");

        Log.d("errorcheck","ok 2");
        TeamDataBase db = new TeamDataBase(this);
        final ArrayList<TeamAttributeClass>[] listTeams = new ArrayList[]{db.fetchAllTeams()};
        ArrayList<Integer> playerSequenceid = new ArrayList<>();
        ArrayList<Integer> imageSequence = new ArrayList<>();

        ArrayList<String> teamSequence = new ArrayList<>();
        for(int i = 0; i< listTeams[0].size(); i++)
        {
            teamSequence.add(listTeams[0].get(i).teamName);
            playerSequenceid.add(listTeams[0].get(i).teamId);
            imageSequence.add(R.drawable.custom_team_icon);
            Log.d("checkerror","ok in"+ listTeams[0].get(i).teamName);
        }
        listViewTeam = findViewById(R.id.listViewTeam);
//        ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, teamSequence);
        CustomViewForPerson adp = new CustomViewForPerson(getApplicationContext(),imageSequence,teamSequence,playerSequenceid);
        listViewTeam.setAdapter(adp);
        listViewTeam.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("checkerror","yes 1");
                fab.collapse();
                Intent intent = new Intent(getApplicationContext(), TeamDisplay.class);
                intent.putExtra("sendTeamID", listTeams[0].get(i).teamId);
                Log.d("checkerror","yes 2 "+ listTeams[0].get(i).teamId);
                startActivity(intent);
            }
        });
        listViewTeam.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TeamsViewActivity.this);
                builder.setMessage("Are you sure you want to delete this team?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        TeamDataBase db = new TeamDataBase(TeamsViewActivity.this);
                        boolean isDeleted = db.deleteTeam(listTeams[0].get(i).teamId);
                        if (isDeleted) {
                            // Remove the team from the list
                            listTeams[0].remove(i);
                            // Fetch updated list of teams from the database
                            listTeams[0] = db.fetchAllTeams();
                            // Create new sequences for the adapter
                            ArrayList<Integer> playerSequenceid = new ArrayList<>();
                            ArrayList<Integer> imageSequence = new ArrayList<>();
                            ArrayList<String> teamSequence = new ArrayList<>();
                            for(int j = 0; j< listTeams[0].size(); j++) {
                                teamSequence.add(listTeams[0].get(j).teamName);
                                playerSequenceid.add(listTeams[0].get(j).teamId);
                                imageSequence.add(R.drawable.custom_team_icon);
                            }
                            // Create and set the adapter to the ListView
                            CustomViewForPerson adp = new CustomViewForPerson(getApplicationContext(),imageSequence,teamSequence,playerSequenceid);
                            listViewTeam.setAdapter(adp);

                            Toast.makeText(TeamsViewActivity.this, "Team deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TeamsViewActivity.this, "Failed to delete team", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true; // Consume the long click
            }
        });



        //long press to delete the team from the team database table and update the UI accordingly



    }
}