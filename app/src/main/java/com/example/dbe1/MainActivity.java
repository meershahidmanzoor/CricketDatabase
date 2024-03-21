package com.example.dbe1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.dbe1.teams.TeamAttributeClass;
import com.example.dbe1.teams.TeamDataBase;
import com.example.dbe1.teams.TeamsViewActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TeamDataBase db=new TeamDataBase(this);
        ArrayList<TeamAttributeClass> listTeams=db.fetchAllTeams();
        if(listTeams.size()==0){
            Toast.makeText(this, "use Team Dashboard for adding teams ", Toast.LENGTH_SHORT).show();
        }
        Intent change = new Intent(getApplicationContext(), PlayerCoachView.class);
        Button team = findViewById(R.id.mainteam);
        team.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), TeamsViewActivity.class));
            }
        });
        Button player = findViewById(R.id.mainplayer);
        player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change.putExtra("change","player");
                startActivity(change);
            }
        });
        Button coach = findViewById(R.id.maincoach);
        coach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change.putExtra("change","coach");
                startActivity(change);
            }
        });

        Button info = findViewById(R.id.maininfo);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(),Information.class));

            }
        });

    }
}