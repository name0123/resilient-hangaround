package com.everis.lucmihai.hangaround;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;
import android.widget.EditText;

import com.facebook.login.widget.LoginButton;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static android.support.test.InstrumentationRegistry.getInstrumentation;


/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApplicationTest {

	private String TAG = "Testating";
	private static final String STRING_TO_BE_TYPED = "UiAutomator";
	private UiDevice mDevice;

	@Rule
	public ActivityTestRule<LoginActivity> activityTestRule =
			new ActivityTestRule<>(LoginActivity.class);

	private Solo solo;


	@Before
	public void setUp() throws Exception {
			//setUp() is run before a test case is started.
			//This is where the solo object is created.
			solo = new Solo(getInstrumentation(),
			activityTestRule.getActivity());
			}

	@After
	public void tearDown() throws Exception {
			//tearDown() is run after a test case has finished.
			//finishOpenedActivities() will finish all the activities that have been opened during the test execution.
			solo.finishOpenedActivities();
			}

	@Test
	public void facebookLoginTest() throws Exception {
		//Unlock the lock screen
		solo.unlockScreen();

		//Assert that NoteEditor activity is opened
		solo.assertCurrentActivity("Expected NoteEditor Activity", LoginActivity.class);
		if(solo.waitForActivity(LoginActivity.class)) {
			LoginButton loginButton = (LoginButton) solo.getView(R.id.login_button);
			String text = String.valueOf(loginButton.getText());
			Log.d(TAG, "This is text: " + text);
			//Click on button

			if (!text.equals("Log out")) {
				Log.d(TAG, "Clicking log in");
				solo.clickOnButton(0);
				solo.sleep(80000);
				// permission - bad stuff!
				//solo.clickOnButton("Allow");
				//solo.sleep(2000);

			}
			//solo.clickOnView(solo.getView(R.id.login_button));
			else {
				Log.d(TAG, "Clicking continue");
				solo.clickOnButton(1);
			}
		}
		if(solo.waitForActivity(MapsActivity.class)) {
			Log.d(TAG, "MapsActivity loaded");
			solo.enterText((EditText) solo.getView(R.id.txtsearch), "Sabadell");
			solo.clickOnButton(1);
			Context contex = solo.getCurrentActivity();
			Log.d(TAG, "wait 5 seconds");
			solo.sleep(5000);
			Log.d(TAG, "done 5 seconds");
			UiObject marker = mDevice.findObject(new UiSelector().descriptionContains("McDonald's"));
			Log.d(TAG, "marker values"+marker.isCheckable());
			marker.click();

			// marker clicked?
			solo.sleep(40000);

		}
	}

	@Test
	public void searchSabadellTest(){
		//

	}
}
