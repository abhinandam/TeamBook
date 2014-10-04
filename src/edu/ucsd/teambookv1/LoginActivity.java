package edu.ucsd.teambookv1;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {

	// LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
	ArrayList<String> listItems = new ArrayList<String>();

	// DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
	ArrayAdapter<String> adapter;

	public static final String REGISTER_URI = "http://toolbook.herokuapp.com/toolbook/api/user/register";
	public static final String SIGN_IN_URI = "http://toolbook.herokuapp.com/toolbook/api/user/login";

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";
	public static Boolean parseInit = false;


	// Values for email and password at the time of the login attempt.
	private String mUserID;
	private String mPassword;

	public static ParseUser parseUser = null;
	// UI references.
	private EditText mUserIDView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if ( !LoginActivity.parseInit ) {
			Parse.initialize(this, "PMI4orvtldZuBmLvDbxlGTPsl8rQhpPVCIc9gCZx",
				"Jr2oBtP8y7DGk7IcNwVCEXcX3wSo7TvgfcPt6m9n");
			LoginActivity.parseInit = true;
		}

		setContentView(R.layout.activity_login);
		setupActionBar();

		// Set up the login form.
		mUserID = getIntent().getStringExtra(EXTRA_EMAIL);
		mUserIDView = (EditText) findViewById(R.id.email);
		mUserIDView.setText(mUserID);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.register_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptRegister();
					}
				});
		
		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});


	}

	public void attemptRegister() {
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Welcome New User");
		alert.setMessage("Please enter the the following information:");

		// Set an EditText view to get user input
		final EditText firstName = new EditText(this);
		firstName.setHint("First Name");
		// Set an EditText view to get user input
		final EditText lastName = new EditText(this);
		lastName.setHint("Last Name");
		// Set an EditText view to get user input
		final EditText email = new EditText(this);
		email.setHint("Email");
		final EditText password = new EditText(this);
		password.setHint("Password");
		final EditText repPass = new EditText(this);
		repPass.setHint("Repeat Password");

		// Set the layout for the alert dialog
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.addView(firstName);
		ll.addView(lastName);
		ll.addView(email);
		ll.addView(password);
		ll.addView(repPass);
		alert.setView(ll);

		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@SuppressLint("NewApi")
			public void onClick(DialogInterface dialog, int whichButton) {
				String title = firstName.getText().toString();
				if (!title.isEmpty()) {
					ParseUser user = new ParseUser();
					user.setUsername(email.getText().toString());
					user.setPassword(password.getText().toString());
					user.setEmail(email.getText().toString());

					user.signUpInBackground(new SignUpCallback() {

						@Override
						public void done(com.parse.ParseException arg0) {
							// TODO Auto-generated method stub
							if (arg0 == null) {
								// Hooray! Let them use the app now.
							} else {
								// Sign up didn't succeed. Look at the
								// ParseException
								// to figure out what went wrong
							}
						}

					});
					
					Toast.makeText(LoginActivity.this, "User Registered!",
							Toast.LENGTH_SHORT).show();
				}

				else {
					// Display appropriate login
					Toast.makeText(LoginActivity.this,
							"Please enter appropriate login info",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Toast.makeText(LoginActivity.this,
								"You Were Not Registered!", Toast.LENGTH_SHORT)
								.show();
					}
				});
		alert.show();

		boolean cancel = false;
		View focusView = null;

		// Reset errors.
		mUserIDView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mUserID = mUserIDView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}
		// Check for a valid email address.
		if (TextUtils.isEmpty(mUserID)) {
			mUserIDView.setError(getString(R.string.error_field_required));
			focusView = mUserIDView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			// mAuthTask = new UserLoginTask();
			// mAuthTask.execute((Void) null);
		}

	}

	public void displayClassList() {
		Intent classListView = new Intent(this, ClassListView.class);
		startActivity(classListView);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// TODO: If Settings has multiple levels, Up should navigate up
			// that hierarchy.
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		// Reset errors.
		mUserIDView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mUserID = mUserIDView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}
		// Check for a valid email address.
		if (TextUtils.isEmpty(mUserID)) {
			mUserIDView.setError(getString(R.string.error_field_required));
			focusView = mUserIDView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			ParseUser.logInInBackground(mUserID, mPassword, new LogInCallback() {
				  public void done(ParseUser user, com.parse.ParseException arg1) {
					showProgress(false);
				    if (user != null) {
				    	Log.w("login","login successful");
				    	// Hooray! The user is logged in
						LoginActivity.parseUser = user;
						displayClassList();
				    } else {
				    	Log.w("error","login failed: " + arg1.getMessage());
				      // Signup failed. Look at the ParseException to see what happened.
				    }
				  }
				});
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
}