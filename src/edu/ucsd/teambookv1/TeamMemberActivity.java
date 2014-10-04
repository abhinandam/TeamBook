package edu.ucsd.teambookv1;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class TeamMemberActivity extends ListActivity {

	// LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
	ArrayList<String> listItems = new ArrayList<String>();
	ArrayList<ParseUser> listUsers = new ArrayList<ParseUser>();
	
	// DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
	ArrayAdapter<String> adapter;

	private EditText edittext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_team_member);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, listItems);
		setListAdapter(adapter);
		edittext = (EditText) findViewById(R.id.editText);
		edittext.setHint("Member's gmail address");
		findViewById(R.id.invite_members).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptInvite();
					}
				});
		getCurrentTeamMembers();
	}

	public void getCurrentTeamMembers() {
		ArrayList<String> listItems = new ArrayList<String>();
		ArrayList<ParseUser> listUsers = new ArrayList<ParseUser>();
		ParseQuery<ParseObject> members = ParseQuery.getQuery("TeamMembers");
		members.whereEqualTo("team", TeamActivity.getSelectedTeam());
		try {
			List<ParseObject> memberList = members.find();
			for (ParseObject memberObject : memberList) {
				ParseUser member = memberObject.getParseUser("user");
				member.fetch();
				listItems.add(member.getUsername());
				listUsers.add(member);
			}
			this.listItems.clear();
			this.listItems.addAll(listItems);
			adapter.notifyDataSetChanged();
			this.listUsers.addAll(listUsers);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void attemptInvite() {

		// get edittext component
		edittext = (EditText) findViewById(R.id.editText);

		// use this to invite member
		String memberToInvite = edittext.getText().toString().trim();
		Log.w("invite", "trying to invite: " + memberToInvite);
		ParseQuery<ParseUser> checkUser = ParseUser.getQuery();
		checkUser.whereEqualTo("username", memberToInvite);
		try {
			// Check if user exists otherwise give an error
			if ( checkUser.count() == 0 ) {
				Toast.makeText(TeamMemberActivity.this, "Member does not exist",
						Toast.LENGTH_LONG).show();
				return;
			} else {
				ParseUser user = checkUser.find().get(0);
				ParseQuery<ParseObject> checkTeam = ParseQuery.getQuery("TeamMembers");
				checkTeam.whereEqualTo("user", user);
				checkTeam.whereEqualTo("team", TeamActivity.getSelectedTeam());
				// Check if they are already in the team...
				if ( checkTeam.count() > 0 ) {
					Toast.makeText(TeamMemberActivity.this, "Member already exists in this team.",
							Toast.LENGTH_LONG).show();
					return;
				} else {
					// Try and send them an invite
					ParseQuery<ParseObject> checkInvites = ParseQuery.getQuery("TeamInvites");
					checkInvites.whereEqualTo("to", user);
					checkInvites.whereEqualTo("team", TeamActivity.getSelectedTeam());
					// Check if an invite has already been sent
					if ( checkInvites.count() > 0 ) {
						Toast.makeText(TeamMemberActivity.this, "A member of your team already sent an invite.",
								Toast.LENGTH_LONG).show();
						return;
					} else {
						// Now we can finally send the invite
						ParseObject invite = new ParseObject("TeamInvites");
						invite.put("by", LoginActivity.parseUser);
						invite.put("to", user);
						invite.put("team", TeamActivity.getSelectedTeam());
						invite.save();
						Toast.makeText(TeamMemberActivity.this, user.getUsername() + " has been invited",
								Toast.LENGTH_LONG).show();
					}
				}
				
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
}
