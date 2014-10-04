package edu.ucsd.teambookv1;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class TeamActivity extends ListActivity {

	// LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
	ArrayList<String> listItems = new ArrayList<String>();
	ArrayList<ParseObject> listTeams = new ArrayList<ParseObject>();

	// DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
	ArrayAdapter<String> adapter;

	// Selected team
	private static ParseObject selectedTeam = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_team);

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, listItems);
		setListAdapter(adapter);
		getUserTeams();
	}

	public void getUserTeams() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("TeamMembers");
		query.whereEqualTo("user", LoginActivity.parseUser);
		Log.w("teams", "searching...");
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> teamList, ParseException e) {
				Log.w("teams", "done...");
				if (e == null) {
					ArrayList<String> newListItems = new ArrayList<String>();
					ArrayList<ParseObject> newTeamItems = new ArrayList<ParseObject>();
					for (ParseObject po : teamList) {
						Log.w("team", "found team");
						ParseObject team = po.getParseObject("team");
						try {
							team.fetchIfNeeded();
							if (team.getParseObject("class")
									.getObjectId()
									.equals(ClassListView.getSelectedClass()
											.getObjectId())) {
								newListItems.add(team.getString("name"));
								newTeamItems.add(team);
							}
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
					listItems.clear();
					listItems.addAll(newListItems);
					listTeams.clear();
					listTeams.addAll(newTeamItems);
					adapter.notifyDataSetChanged();
				} else {
					Log.w("teams error", e.getMessage());
				}
			}
		});
	}

	// METHOD WHICH WILL HANDLE DYNAMIC INSERTION
	public void createTeam(View v) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("New Team");
		alert.setMessage("Please enter the name of the team you wish to create:");

		// Set an EditText view to get user input
		final EditText teamTitle = new EditText(this);
		teamTitle.setHint("Team Name");

		// Set the layout for the alert dialog
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.addView(teamTitle);
		alert.setView(ll);

		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@SuppressLint("NewApi")
			public void onClick(DialogInterface dialog, int whichButton) {
				String title = teamTitle.getText().toString();

				if (!title.isEmpty()) {
					ParseObject team = createTeam(LoginActivity.parseUser,
							ClassListView.getSelectedClass(), title);
					joinTeam(LoginActivity.parseUser, team);
				}

				else {
					// Display a Toast telling the user to enter in a Place It
					Toast.makeText(TeamActivity.this,
							"Please enter a name for your team",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Toast.makeText(TeamActivity.this, "Nothing added!",
								Toast.LENGTH_SHORT).show();
					}
				});
		alert.show();
	}

	//
	public ParseObject createTeam(ParseUser user, ParseObject cl,
			String teamName) {
		ParseObject team = new ParseObject("Team");
		team.put("name", teamName);
		team.put("class", cl);
		try {
			team.save();
			Toast.makeText(TeamActivity.this, "Team Added!", Toast.LENGTH_SHORT)
					.show();
			listItems.add(teamName);
			listTeams.add(team);
			adapter.notifyDataSetChanged();
			return team;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// return TeamMembers object
	public static ParseObject joinTeam(ParseUser user, ParseObject team) {
		ParseObject teamMember = new ParseObject("TeamMembers");
		teamMember.put("user", user);
		teamMember.put("team", team);
		try {
			teamMember.save();
			return teamMember;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public void onListItemClick(ListView l, View v, final int position, long id) {

		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Invite New Members");
		// Set the layout for the alert dialog
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		alert.setView(ll);
		alert.setPositiveButton("Invite",
				new DialogInterface.OnClickListener() {
					@SuppressLint("NewApi")
					public void onClick(DialogInterface dialog, int whichButton) {
						setSelectedTeam(listTeams.get(position));
						displayTeamMemberView();

					}
				});

		alert.setNegativeButton("Remove",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Toast.makeText(TeamActivity.this,
								"Team has been removed", Toast.LENGTH_SHORT)
								.show();
					}
				});
		alert.show();
	}

	private void displayTeamMemberView() {
		Intent teamMemberView = new Intent(this, TeamMemberActivity.class);
		startActivity(teamMemberView);
	}

	public static ParseObject getSelectedTeam() {
		return selectedTeam;
	}

	public static void setSelectedTeam(ParseObject selectedTeam) {
		TeamActivity.selectedTeam = selectedTeam;
	}
}
