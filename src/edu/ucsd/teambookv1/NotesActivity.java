package edu.ucsd.teambookv1;


import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class NotesActivity extends ListActivity {
	// LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
	ArrayList<String> listItems = new ArrayList<String>();
	ArrayList<ParseObject> listParseObjects = new ArrayList<ParseObject>();

	// Selected note for next page
	private static ParseObject selectedNote = null;
	
	// DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
	ArrayAdapter<String> adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notes);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, listItems);
		setListAdapter(adapter);
		getUserNotes();
		findViewById(R.id.createButton).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						createNote(view);
					}
				});

	}
	
	public void getUserNotes() {
		ParseQuery<ParseObject> noteQuery = ParseQuery.getQuery("Notes");
		noteQuery.whereEqualTo("owner", LoginActivity.parseUser);
		noteQuery.whereEqualTo("class", ClassListView.getSelectedClass());
		try {
			List<ParseObject> notes = noteQuery.find();
			ArrayList<String> listItems = new ArrayList<String>();
			ArrayList<ParseObject> listParseObjects = new ArrayList<ParseObject>();
			for (ParseObject note : notes) {
				listItems.add(note.getString("name"));
				listParseObjects.add(note);
			}
			this.listItems.clear();
			this.listItems.addAll(listItems);
			adapter.notifyDataSetChanged();
			this.listParseObjects.clear();
			this.listParseObjects.addAll(listParseObjects);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//METHOD WHICH WILL create a new note
	public void createNote(View v) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("New Note");
		alert.setMessage("Please enter the title of the note you wish to create:");

		// Set an EditText view to get user input
		final EditText noteTitle = new EditText(this);
		noteTitle.setHint("Note Title");


		// Set the layout for the alert dialog
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.addView(noteTitle);
		alert.setView(ll);


		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@SuppressLint("NewApi")
			public void onClick(DialogInterface dialog, int whichButton) {
				String title = noteTitle.getText().toString();

				if(!title.isEmpty()){
					ParseObject newNote = new ParseObject("Notes");
					newNote.put("owner", LoginActivity.parseUser);
					newNote.put("class", ClassListView.getSelectedClass());
					newNote.put("name", noteTitle.getText().toString());
					newNote.saveEventually();
					listItems.add(noteTitle.getText().toString());
					adapter.notifyDataSetChanged();
					listParseObjects.add(newNote);
					getUserNotes();
				}

				else{
					// Display a Toast telling the user to enter in name for the note
					Toast.makeText(NotesActivity.this,
							"Please enter a name for your note",
							Toast.LENGTH_LONG).show();
				}
			}});


		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Toast.makeText(NotesActivity.this, "Nothing added!",
						Toast.LENGTH_SHORT).show();
			}
		});
		alert.show();
	}
	
	@Override
	public void onListItemClick(ListView l, View v, final int position, long id) {
		setSelectedNote(listParseObjects.get(position));
		Intent notesView = new Intent(this, NotesView.class);
		startActivity(notesView);
	}

	public static ParseObject getSelectedNote() {
		return selectedNote;
	}

	public static void setSelectedNote(ParseObject selectedNote) {
		NotesActivity.selectedNote = selectedNote;
	}
	
}
