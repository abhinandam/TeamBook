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
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ClassListView extends ListActivity {

	// LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
	ArrayList<String> listItems = new ArrayList<String>();
	ArrayList<ParseObject> listParseObjects = new ArrayList<ParseObject>();

	private static ParseObject selectedClass = null;

	// DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
	ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_class_list_view);
		
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, listItems);
		
		SimpleAdapter adapter2 = new SimpleAdapter(
				this,
				listItems,
				R.layout.custom_row_view,
				new String[] {"title","description"},
				new int[] {R.id.text1,R.id.text2}

				);

				setListAdapter(adapter2);
		//setListAdapter(adapter);
		getUserClasses();
	}

	public void getUserClasses() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Enrollment");
		query.whereEqualTo("User", LoginActivity.parseUser);
		Log.w("classes", "searching...");
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> classList, ParseException e) {
				Log.w("classes", "done...");
				if (e == null) {
					ArrayList<String> newListItems = new ArrayList<String>();
					ArrayList<ParseObject> newListParseObjects = new ArrayList<ParseObject>();
					for (ParseObject po : classList) {
						ParseObject cl = po.getParseObject("Classes");
						try {
							cl.fetchIfNeeded();
							newListItems.add(cl.getString("name"));
							newListParseObjects.add(cl);
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
					listItems.clear();
					listItems.addAll(newListItems);
					listParseObjects.clear();
					listParseObjects.addAll(newListParseObjects);
					adapter.notifyDataSetChanged();
				} else {
					Log.w("classes error", e.getMessage());
				}
			}
		});
	}

	public void displayLogIn() {
		Intent loginView = new Intent(this, LoginActivity.class);
		startActivity(loginView);
	}

	// METHOD WHICH WILL HANDLE DYNAMIC INSERTION
	public void addClass(View v) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("New Class");
		alert.setMessage("Please enter the class you wish to add:");

		// Set an EditText view to get user input
		final EditText classTitle = new EditText(this);
		classTitle.setHint("Enter class");
		// Set an EditText view to get user input
		final EditText classQuarter = new EditText(this);
		classQuarter.setHint("Enter quarter");
		// Set an EditText view to get user input
		final EditText classYear = new EditText(this);
		classYear.setHint("Enter year");

		// Set the layout for the alert dialog
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.addView(classTitle);
		ll.addView(classQuarter);
		ll.addView(classYear);
		alert.setView(ll);

		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@SuppressLint("NewApi")
			public void onClick(DialogInterface dialog, int whichButton) {
				final String title = classTitle.getText().toString();
				// For some reason not getting quarter
				final String quarter = classQuarter.getText().toString();
				final String year = classYear.getText().toString();
				Log.w("AddClass", "Trying to add class: " + title
						+ " quarter: " + quarter + " year: " + year);
				if (!title.isEmpty() && !quarter.isEmpty() && !year.isEmpty()) {
					ParseQuery<ParseObject> q = ParseQuery.getQuery("Classes");
					q.whereEqualTo("name", title);
					q.whereEqualTo("quarter", quarter);
					q.whereEqualTo("year", Integer.parseInt(year));

					q.findInBackground(new FindCallback<ParseObject>() {
						public void done(List<ParseObject> classList,
								ParseException e) {
							ParseObject theClass = null;
							if (classList.size() == 0) {
								theClass = new ParseObject("Classes");
								theClass.put("name", title);
								theClass.put("quarter", quarter);
								theClass.put("year", Integer.parseInt(year));
								try {
									theClass.save();
								} catch (ParseException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								Toast.makeText(ClassListView.this,
										"Class Added!", Toast.LENGTH_SHORT)
										.show();
							} else {
								theClass = classList.get(0);
								Toast.makeText(ClassListView.this,
										"Joined Class!", Toast.LENGTH_SHORT)
										.show();
							}
							enroll(theClass);
						}
					});

				}

				else {
					// Display a Toast telling the user to enter in a Place It
					Toast.makeText(ClassListView.this,
							"Please make sure to fill our all the info!",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Toast.makeText(ClassListView.this, "Nothing added!",
								Toast.LENGTH_SHORT).show();
					}
				});
		alert.show();

	}

	public void enroll(ParseObject theClass) {
		// Check if enroll exists otherwise enroll
		ParseQuery<ParseObject> q = ParseQuery.getQuery("Enrollment");
		q.whereEqualTo("User", LoginActivity.parseUser);
		q.whereEqualTo("Classes", theClass);
		try {
			if (q.count() == 0) {
				ParseObject enroll = new ParseObject("Enrollment");
				enroll.put("User", LoginActivity.parseUser);
				enroll.put("Classes", theClass);
				enroll.save();
				Toast.makeText(ClassListView.this, "Enrolled Class!",
						Toast.LENGTH_SHORT).show();

				listItems.add(theClass.getString("name"));
				listParseObjects.add(theClass);
				adapter.notifyDataSetChanged();
				Log.w("enroll", "Enrolled: " + theClass);
			} else {
				Toast.makeText(ClassListView.this, "You are already enrolled in that class!",
						Toast.LENGTH_SHORT).show();
				Log.w("error", "Already enrolled in class");
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onListItemClick(ListView l, View v, final int position, long id) {
		// TODO Auto-generated method stub
		// super.onListItemClick(l, v, position, id);
		ClassListView.setSelectedClass(this.listParseObjects.get(position));
		displayMainList();

	}

	public void displayMainList() {
		Intent mainListView = new Intent(this, MainList.class);
		startActivity(mainListView);
	}

	public static ParseObject getSelectedClass() {
		return selectedClass;
	}

	public static void setSelectedClass(ParseObject selectedClass) {
		ClassListView.selectedClass = selectedClass;
	}

}
