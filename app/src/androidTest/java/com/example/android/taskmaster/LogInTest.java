package com.example.android.taskmaster;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.taskmaster.view.LogInActivity;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LogInTest
{
  private static final String email = "john.smith@example.com";

  private static final String password = "123456";

  @Rule
  public ActivityTestRule<LogInActivity> activityTestRule = new ActivityTestRule<>(LogInActivity.class);

  @Test
  public void testLogInButtonDisabledWithAllFieldsEmpty()
  {
    Espresso.onView(ViewMatchers.withId(R.id.et_log_in_activity_email_address))
            .check(ViewAssertions.matches(ViewMatchers.withText("")));

    Espresso.onView(ViewMatchers.withId(R.id.et_log_in_activity_password))
            .check(ViewAssertions.matches(ViewMatchers.withText("")));

    Espresso.onView(ViewMatchers.withId(R.id.button_log_in_activity_log_in))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isEnabled())));
  }

  @Test
  public void testLogInButtonDisabledWithEmailFieldNonEmpty()
  {
    Espresso.onView(ViewMatchers.withId(R.id.et_log_in_activity_email_address))
            .perform(ViewActions.typeText(LogInTest.email));

    Espresso.onView(ViewMatchers.withId(R.id.et_log_in_activity_email_address))
            .check(ViewAssertions.matches(ViewMatchers.withText(LogInTest.email)));

    Espresso.onView(ViewMatchers.withId(R.id.et_log_in_activity_password))
            .check(ViewAssertions.matches(ViewMatchers.withText("")));

    Espresso.onView(ViewMatchers.withId(R.id.button_log_in_activity_log_in))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isEnabled())));
  }

  @Test
  public void testLogInButtonDisabledWithPasswordFieldNonEmpty()
  {
    Espresso.onView(ViewMatchers.withId(R.id.et_log_in_activity_email_address))
            .check(ViewAssertions.matches(ViewMatchers.withText("")));

    Espresso.onView(ViewMatchers.withId(R.id.et_log_in_activity_password))
            .perform(ViewActions.typeText(LogInTest.password));

    Espresso.onView(ViewMatchers.withId(R.id.et_log_in_activity_password))
            .check(ViewAssertions.matches(ViewMatchers.withText(LogInTest.password)));

    Espresso.onView(ViewMatchers.withId(R.id.button_log_in_activity_log_in))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isEnabled())));
  }

  @Test
  public void testLogInButtonEnabledWithAllFieldsNonEmpty()
  {
    Espresso.onView(ViewMatchers.withId(R.id.et_log_in_activity_email_address))
            .perform(ViewActions.typeText(LogInTest.email));

    Espresso.onView(ViewMatchers.withId(R.id.et_log_in_activity_email_address))
            .check(ViewAssertions.matches(ViewMatchers.withText(LogInTest.email)));

    Espresso.onView(ViewMatchers.withId(R.id.et_log_in_activity_password))
            .perform(ViewActions.typeText(LogInTest.password));

    Espresso.onView(ViewMatchers.withId(R.id.et_log_in_activity_password))
            .check(ViewAssertions.matches(ViewMatchers.withText(LogInTest.password)));

    Espresso.onView(ViewMatchers.withId(R.id.button_log_in_activity_log_in))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()));
  }
}
