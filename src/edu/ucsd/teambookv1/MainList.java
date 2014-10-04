package edu.ucsd.teambookv1;


import java.util.ArrayList;
import java.util.HashMap;

import com.parse.ParseException;
import com.parse.ParseObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.os.Build;

public class MainList extends ListActivity {
	private static final int NOTES_POSITION = 0;
	private static final int PICTURES_POSITION = 1;
	private static final int CLASS_WEBSITE_POSITION = 2;
	private static final int TEAM_POSITION = 3;
	private static final int CONTACT_POSITION = 4;
	private static final int PIAZZA_POSITION = 5;
	private static final int DEADLINES_POSITION = 6;
	private static final int INVITES_POSITION = 7; 

	//LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
	ArrayList<String> listItems=new ArrayList<String>();

	//DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
	ArrayAdapter<String> adapter;

	String piazzaSite = "http://www.piazza.com";
	String classSite = null;

	//RECORDING HOW MANY TIMES THE BUTTON HAS BEEN CLICKED
	// int clickCounter=0;
	static final ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_main_list);/*
		listItems.add("Notes");
		listItems.add("Pictures");
		listItems.add("Class Website");
		listItems.add("Manage Teams");
		listItems.add("Contacts");
		listItems.add("Piazza");
		listItems.add("Calendar");
		adapter=new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				listItems);
		adapter.notifyDataSetChanged();
		setListAdapter(adapter);*/
		
		SimpleAdapter adapter = new SimpleAdapter(
				this,
				list,
				R.layout.custom_row_view,
				new String[] {"title","description"},
				new int[] {R.id.text1,R.id.text2}

				);
				populateList();
				setListAdapter(adapter);

	}

	private void populateList() {
		classSite = ClassListView.getSelectedClass().getString("website");
		list.clear();
		HashMap<String,String> notes = new HashMap<String,String>();
		notes.put("title","Notes");
		notes.put("description", "Access notes created by your teammates");
		list.add(notes);
		HashMap<String,String> pics = new HashMap<String,String>();
		pics.put("title","Pictures");
		pics.put("description", "Access pictures taken by your teammates");
		list.add(pics);
		HashMap<String,String> classUrl = new HashMap<String,String>();
		classUrl.put("title","Class Website");
		classUrl.put("description", classSite);
		list.add(classUrl);
		HashMap<String,String> teams = new HashMap<String,String>();
		teams.put("title","Manage Teams");
		teams.put("description", "Create a team or join an existing one");
		list.add(teams);
		/*
		HashMap<String,String> contacts = new HashMap<String,String>();
		contacts.put("title","Contacts");
		contacts.put("description", "Find classmates registered for this class");
		list.add(contacts);*/
		HashMap<String,String> piazza = new HashMap<String,String>();
		piazza.put("title","Piazza");
		piazza.put("description", "Access your piazza for this class");
		list.add(piazza);
		HashMap<String,String> calendar = new HashMap<String,String>();
		calendar.put("title","Calendar");
		calendar.put("description", "Add deadlines to your calendar");
		list.add(calendar);
		HashMap<String,String> invites = new HashMap<String,String>();
		invites.put("title","Invites");
		invites.put("description", "Check which groups you have been invited to");
		list.add(invites);
		}
	
	
	@Override
	public void onListItemClick(ListView l, View v, final int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

		if(position == PICTURES_POSITION){
			Intent picturesView = new Intent(this, PicturesActivity.class);
			startActivity(picturesView);

		}
		final ParseObject selectedClass = ClassListView.getSelectedClass();
		if(position == CLASS_WEBSITE_POSITION){
			if(selectedClass.getString("website") == null){
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setTitle("Class Website");
				alert.setMessage("Please enter your class website");

				// Set an EditText view to get user input
				final EditText classWebsite = new EditText(this);
				classWebsite.setText("https://");

				// Set the layout for the alert dialog
				LinearLayout ll = new LinearLayout(this);
				ll.setOrientation(LinearLayout.VERTICAL);
				ll.addView(classWebsite);
				alert.setView(ll);


				alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {
						selectedClass.put("website", classWebsite.getText().toString());
						try {
							selectedClass.save();
							Toast.makeText(MainList.this, "Class Website Saved!", Toast.LENGTH_SHORT).show();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}});

				alert.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Toast.makeText(MainList.this, "Class Website has not been saved!",
								Toast.LENGTH_SHORT).show();
					}
				});
				alert.show();
			}

			else{
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(selectedClass.getString("website")));
				startActivity(browserIntent);
			}

		}

		if(position == TEAM_POSITION){
			Intent teamView = new Intent(this, TeamActivity.class);
			startActivity(teamView);
		}

		if(position == PIAZZA_POSITION){
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(piazzaSite));
			startActivity(browserIntent);
		}

		if(position == DEADLINES_POSITION){

			Intent intent = new Intent(Intent.ACTION_EDIT);  
			intent.setType("vnd.android.cursor.item/event");
			intent.putExtra("title", "Deadline for ");
			intent.putExtra("description", "Some description");
			intent.putExtra("beginTime", DateUtils.HOUR_IN_MILLIS);
			intent.putExtra("endTime", DateUtils.HOUR_IN_MILLIS);
			startActivity(intent);
		}
		
		if(position == INVITES_POSITION){
			Intent invitesView = new Intent(this, InvitesActivity.class);
			startActivity(invitesView);
		}

		if(position == NOTES_POSITION){
			Intent notesActivity = new Intent(this, NotesActivity.class);
			startActivity(notesActivity);
		}

	}

}
