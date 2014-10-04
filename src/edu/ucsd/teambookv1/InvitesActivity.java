package edu.ucsd.teambookv1;


import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class InvitesActivity extends ListActivity {

	// LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
	ArrayList<String> listItems = new ArrayList<String>();
	ArrayList<ParseObject> listParseObjects = new ArrayList<ParseObject>();


	// DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
	ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invites);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, listItems);
		setListAdapter(adapter);
		getCurrentInvites();
	}

	public void getCurrentInvites() {
		ArrayList<String> listItems = new ArrayList<String>();
		ArrayList<ParseObject> listParseObjects = new ArrayList<ParseObject>();
		ParseQuery<ParseObject> invitesQuery = ParseQuery.getQuery("TeamInvites");
		invitesQuery.whereEqualTo("to", LoginActivity.parseUser);
		try {
			List<ParseObject> invites = invitesQuery.find();
			if (invites.size() == 0) {
				Log.w("invites", "no invites found");
			}
			for ( ParseObject invite : invites ) {
				Log.w("invite", "found invite...");
				// Get ParseUser who invited you
				ParseUser by = invite.getParseUser("by");
				by.fetch();
				
				// Get info for team you were invited to
				ParseObject team = invite.getParseObject("team");
				team.fetch();
				
				if ( team.getParseObject("class").getObjectId().equals(ClassListView.getSelectedClass().getObjectId())) {
					Log.w("invite", "found invite to: " + team.getString("name"));
					listItems.add(team.getString("name") + " - " + ClassListView.getSelectedClass().getString("name") + " from " + by.getUsername());
					listParseObjects.add(invite);
				}
				this.listItems.clear();
				this.listItems.addAll(listItems);
				adapter.notifyDataSetChanged();
				this.listParseObjects.clear();
				this.listParseObjects.addAll(listParseObjects);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void onListItemClick(ListView l, View v, final int position, long id) {
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Invitation to team");

		// Set the layout for the alert dialog
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		alert.setView(ll);


		alert.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
			@SuppressLint("NewApi")
			public void onClick(DialogInterface dialog, int whichButton) {
				ParseObject invite = listParseObjects.get(position);
				try {
					ParseObject team = invite.getParseObject("team");
					team.fetch();
					TeamActivity.joinTeam(LoginActivity.parseUser, team);
					invite.delete();
					Toast.makeText(InvitesActivity.this, "Invite has been accepted",
							Toast.LENGTH_SHORT).show();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				getCurrentInvites();
			}});

		alert.setNegativeButton("Reject",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				ParseObject invite = listParseObjects.get(position);
				try {
				
					invite.delete();
					Toast.makeText(InvitesActivity.this, "Invite has been rejected",
							Toast.LENGTH_SHORT).show();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				getCurrentInvites();

			}
		});
		alert.show();
	}



}
