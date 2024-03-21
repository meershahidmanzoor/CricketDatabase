package com.example.dbe1.owners;

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
import com.example.dbe1.players.PlayerDataBase;
import com.example.dbe1.players.PlayersViewActivity;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class OwnersViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owners_view);

        Intent getTeamID = getIntent();
        int teamID = getTeamID.getIntExtra("sendTeamID",0);

        Intent next = new Intent(new Intent(getApplicationContext(), GetOwnerValueByUser.class));
        next.putExtra("sendTeamID",teamID);
        FloatingActionButton fb = findViewById(R.id.float_add_new_owner);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(next);
            }
        });

        OwnerDataBase odb = new OwnerDataBase(this);

        ArrayList<OwnerAttributeClass> listOwner = odb.fetchAllOwner();

        ArrayList<String> ownerSequence = new ArrayList<>();
        ArrayList<Integer> particularOwner = new ArrayList<>();
        ArrayList<Integer> playerSequenceid = new ArrayList<>();
        ArrayList<Integer> imageSequence = new ArrayList<>();

        Log.d("ownerhere","ok 3 "+listOwner.size());
        Log.d("ownerhere","ok 4 "+teamID);
        for(int i=0;i<listOwner.size();i++)
        {
            if(teamID==listOwner.get(i).team_id)
            {
                particularOwner.add(listOwner.get(i).owner_id);
                ownerSequence.add(listOwner.get(i).owner_name);
                playerSequenceid.add(listOwner.get(i).owner_id);
                imageSequence.add(R.drawable.custom_owner_icon);
            }
            Log.d("checkerror","ok in"+listOwner.get(i).owner_name);
        }

        ListView listViewOwner = findViewById(R.id.listViewOwner);
//        ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ownerSequence);
        CustomViewForPerson adp = new CustomViewForPerson(getApplicationContext(),imageSequence,ownerSequence,playerSequenceid);
        listViewOwner.setAdapter(adp);
        listViewOwner.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OwnersViewActivity.this);
                builder.setMessage("Are you sure you want to delete this owner?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
//                        PlayerDataBase odb = new PlayerDataBase(OwnersViewActivity.this);
                        OwnerDataBase odb = new OwnerDataBase(OwnersViewActivity.this);
                        boolean isDeleted = odb.deleteOwner(particularOwner.get(i));
                        if (isDeleted) {
                            ArrayList<OwnerAttributeClass> listOwner = odb.fetchAllOwner();

                            ArrayList<String> ownerSequence = new ArrayList<>();
                            ArrayList<Integer> particularOwner = new ArrayList<>();
                            ArrayList<Integer> playerSequenceid = new ArrayList<>();
                            ArrayList<Integer> imageSequence = new ArrayList<>();

                            Log.d("ownerhere","ok 3 "+listOwner.size());
                            Log.d("ownerhere","ok 4 "+teamID);
                            for(int i=0;i<listOwner.size();i++)
                            {
                                if(teamID==listOwner.get(i).team_id)
                                {
                                    particularOwner.add(listOwner.get(i).owner_id);
                                    ownerSequence.add(listOwner.get(i).owner_name);
                                    playerSequenceid.add(listOwner.get(i).owner_id);
                                    imageSequence.add(R.drawable.custom_owner_icon);
                                }
                                Log.d("checkerror","ok in"+listOwner.get(i).owner_name);
                            }

                            ListView listViewOwner = findViewById(R.id.listViewOwner);
//        ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ownerSequence);
                            CustomViewForPerson adp = new CustomViewForPerson(getApplicationContext(),imageSequence,ownerSequence,playerSequenceid);
                            listViewOwner.setAdapter(adp);
                            adp.notifyDataSetChanged(); // Refresh the adapter
                            Toast.makeText(OwnersViewActivity.this, "Owner deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(OwnersViewActivity.this, "Failed to delete owner", Toast.LENGTH_SHORT).show();
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