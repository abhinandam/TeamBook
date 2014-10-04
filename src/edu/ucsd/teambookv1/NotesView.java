package edu.ucsd.teambookv1;


import java.util.ArrayList;

import com.parse.ParseException;
import com.parse.ParseObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class NotesView extends Activity {

	private EditText editNote;
	private ParseObject note;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notes_view);
		editNote = (EditText) findViewById(R.id.notes);
		note = NotesActivity.getSelectedNote();
		try {
			note.fetchIfNeeded();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		editNote.setText(note.getString("text"));
		findViewById(R.id.save_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptSave();
					}
				});

		findViewById(R.id.share_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptShare();
					}
				});

	}

	public void attemptShare(){
		final CharSequence[] items = {"Team1 ","Team2 ","Team3 ","Team4 "};
		// arraylist to keep the selected items
		final ArrayList seletedItems=new ArrayList();

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Share with following teams");
		builder.setMultiChoiceItems(items, null,
				new DialogInterface.OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int indexSelected,
					boolean isChecked) {
				if (isChecked) {
					// If the user checked the item, add it to the selected items
					seletedItems.add(indexSelected);
				} else if (seletedItems.contains(indexSelected)) {
					// Else, if the item is already in the array, remove it
					seletedItems.remove(Integer.valueOf(indexSelected));
				}
			}
		})
		// Set the action buttons
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				//  Your code when user clicked on OK
				//  You can write the code  to save the selected item here
				Toast.makeText(NotesView.this,
						"Note was shared with selected Teams!", Toast.LENGTH_SHORT)
						.show();

			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				//  Your code when user clicked on Cancel
				Toast.makeText(NotesView.this,
						"Note was not shared!", Toast.LENGTH_SHORT)
						.show();
			}
		});

		AlertDialog dialog = builder.create();//AlertDialog dialog; create like this outside onClick
		dialog.show();
	}

	public void attemptSave(){
		note.put("text", editNote.getText().toString());
		note.saveEventually();
	}
}
