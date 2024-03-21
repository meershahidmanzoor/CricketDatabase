package com.example.dbe1.coaches;

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

import java.util.ArrayList;

public class CoachesViewActivity extends AppCompatActivity {

    ListView listViewCoach;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coaches_view);

        Intent getTeamId = getIntent();
        Intent next = new Intent(new Intent(getApplicationContext(), GetCoachValueByUser.class));
        int id = getTeamId.getIntExtra("sendTeamID",0);
//        int id = 1;
        next.putExtra("sendTeamID",id);

        FloatingActionButton fb = findViewById(R.id.float_add_new_coach);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(next);
            }
        });

        CoachDatabase db = new CoachDatabase(this);
        final ArrayList<CoachAttributeCLass>[] listCoach = new ArrayList[]{db.fetchAllCoach()};
        ArrayList<String> coachSequence = new ArrayList<>();
        ArrayList<Integer> playerSequenceid = new ArrayList<>();
        ArrayList<Integer> imageSequence = new ArrayList<>();
        Log.d("idcheck 3",id+"");
        ArrayList<Integer> particularTeamCoach = new ArrayList<>();
        for(int i = 0; i< listCoach[0].size(); i++)
        {
            if(id== listCoach[0].get(i).team_id)
            {
                particularTeamCoach.add(listCoach[0].get(i).coach_id);
                coachSequence.add(""+ listCoach[0].get(i).coach_name);
                playerSequenceid.add(listCoach[0].get(i).coach_id);
                imageSequence.add(R.drawable.custom_coach_icon);
            }
            Log.d("checkerror","ok in"+ listCoach[0].get(i).coach_name);
        }
        listViewCoach = findViewById(R.id.listViewCoach);
//        ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, coachSequence);
        CustomViewForPerson adp = new CustomViewForPerson(getApplicationContext(),imageSequence,coachSequence,playerSequenceid);
        listViewCoach.setAdapter(adp);
//
        listViewCoach.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("checkerror","yes 1");
                Intent intent = new Intent(getApplicationContext(), CoachDisplay.class);
                intent.putExtra("sendCoachID",particularTeamCoach.get(i));
                Log.d("checkerror","yes 2 "+particularTeamCoach.get(i));
                startActivity(intent);
            }
        });
        listViewCoach.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CoachesViewActivity.this);
                builder.setMessage("Are you sure you want to delete this coach?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        CoachDatabase db = new CoachDatabase(CoachesViewActivity.this);
                        boolean isDeleted = db.deleteCoach(particularTeamCoach.get(i)); // Assuming particularTeamCoach is the list of coach IDs
                        if (isDeleted) {
                            // Remove the coach from the list
                            particularTeamCoach.remove(i);
                            // Fetch updated list of coaches from the database
                            listCoach[0] = db.fetchAllCoach();
                            // Create new sequences for the adapter
                            ArrayList<Integer> playerSequenceid = new ArrayList<>();
                            ArrayList<Integer> imageSequence = new ArrayList<>();
                            ArrayList<String> coachSequence = new ArrayList<>();
                            for (int j = 0; j < listCoach[0].size(); j++) {
                                coachSequence.add(listCoach[0].get(j).coach_name);
                                playerSequenceid.add(listCoach[0].get(j).coach_id);
                                imageSequence.add(R.drawable.custom_coach_icon);
                            }
                            // Create and set the adapter to the ListView
                            CustomViewForPerson adp = new CustomViewForPerson(getApplicationContext(), imageSequence, coachSequence, playerSequenceid);
                            listViewCoach.setAdapter(adp);

                            Toast.makeText(CoachesViewActivity.this, "Coach deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CoachesViewActivity.this, "Failed to delete coach", Toast.LENGTH_SHORT).show();
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

    }
}