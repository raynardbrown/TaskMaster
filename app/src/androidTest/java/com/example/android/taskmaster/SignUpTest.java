package com.example.android.taskmaster;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.taskmaster.view.SignUpActivity;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SignUpTest
{
  private static final String firstName = "John";

  private static final String lastName = "Smith";

  private static final String email = "john.smith@example.com";

  private static final String password = "123456";

  @Rule
  public ActivityTestRule<SignUpActivity> activityTestRule = new ActivityTestRule<>(SignUpActivity.class);

  @Test
  public void testCreateAccountButtonDisabledWithAllFieldsEmpty()
  {
    Espresso.onView(ViewMatchers.withId(R.id.et_sign_up_activity_first_name))
            .check(ViewAssertions.matches(ViewMatchers.withText("")));

    Espresso.onView(ViewMatchers.withId(R.id.et_sign_up_activity_last_name))
            .check(ViewAssertions.matches(ViewMatchers.withText("")));

    Espresso.onView(ViewMatchers.withId(R.id.et_sign_up_activity_email_address))
            .check(ViewAssertions.matches(ViewMatchers.withText("")));

    Espresso.onView(ViewMatchers.withId(R.id.et_sign_up_activity_password))
            .check(ViewAssertions.matches(ViewMatchers.withText("")));

    Espresso.onView(ViewMatchers.withId(R.id.button_sign_up_activity_create_account))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isEnabled())));
  }

  public void testCreateAccountButtonDisabledWithFirstAndLastNameNonEmpty()
  {
    Espresso.onView(ViewMatchers.withId(R.id.et_sign_up_activity_first_name))
            .perform(ViewActions.typeText(SignUpTest.firstName));

    Espresso.onView(ViewMatchers.withId(R.id.et_sign_up_activity_first_name))
            .check(ViewAssertions.matches(ViewMatchers.withText(SignUpTest.firstName)));

    Espresso.onView(ViewMatchers.withId(R.id.et_sign_up_activity_last_name))
            .perform(ViewActions.typeText(SignUpTest.lastName));

    Espresso.onView(ViewMatchers.withId(R.id.et_sign_up_activity_last_name))
            .check(ViewAssertions.matches(ViewMatchers.withText(SignUpTest.lastName)));

    Espresso.onView(ViewMatchers.withId(R.id.et_sign_up_activity_email_address))
            .check(ViewAssertions.matches(ViewMatchers.withText("")));

    Espresso.onView(ViewMatchers.withId(R.id.et_sign_up_activity_password))
            .check(ViewAssertions.matches(ViewMatchers.withText("")));

    Espresso.onView(ViewMatchers.withId(R.id.button_sign_up_activity_create_account))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isEnabled())));
  }

  public void testCreateAccountButtonDisabledWithEmailAndPasswordNonEmpty()
  {
    Espresso.onView(ViewMatchers.withId(R.id.et_sign_up_activity_first_name))
            .check(ViewAssertions.matches(ViewMatchers.withText("")));

    Espresso.onView(ViewMatchers.withId(R.id.et_sign_up_activity_last_name))
            .check(ViewAssertions.matches(ViewMatchers.withText("")));

    Espresso.onView(ViewMatchers.withId(R.id.et_sign_up_activity_email_address))
            .perform(ViewActions.typeText(SignUpTest.email));

    Espresso.onView(ViewMatchers.withId(R.id.et_sign_up_activity_email_address))
            .check(ViewAssertions.matches(ViewMatchers.withText(SignUpTest.email)));

    Espresso.onView(ViewMatchers.withId(R.id.et_sign_up_activity_password))
            .perform(ViewActions.typeText(SignUpTest.password));

    Espresso.onView(ViewMatchers.withId(R.id.et_sign_up_activity_password))
            .check(ViewAssertions.matches(ViewMatchers.withText(SignUpTest.password)));

    Espresso.onView(ViewMatchers.withId(R.id.button_sign_up_activity_create_account))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isEnabled())));
  }

  public void testCreateAccountButtonEnabledWithAllFieldsNonEmpty()
  {
    Espresso.onView(ViewMatchers.withId(R.id.et_sign_up_activity_first_name))
            .perform(ViewActions.typeText(SignUpTest.firstName));

    Espresso.onView(ViewMatchers.withId(R.id.et_sign_up_activity_first_name))
            .check(ViewAssertions.matches(ViewMatchers.withText(SignUpTest.firstName)));

    Espresso.onView(ViewMatchers.withId(R.id.et_sign_up_activity_last_name))
            .perform(ViewActions.typeText(SignUpTest.lastName));

    Espresso.onView(ViewMatchers.withId(R.id.et_sign_up_activity_last_name))
            .check(ViewAssertions.matches(ViewMatchers.withText(SignUpTest.lastName)));

    Espresso.onView(ViewMatchers.withId(R.id.et_sign_up_activity_email_address))
            .perform(ViewActions.typeText(email));

    Espresso.onView(ViewMatchers.withId(R.id.et_sign_up_activity_email_address))
            .check(ViewAssertions.matches(ViewMatchers.withText(email)));

    Espresso.onView(ViewMatchers.withId(R.id.et_sign_up_activity_password))
            .perform(ViewActions.typeText(password));

    Espresso.onView(ViewMatchers.withId(R.id.et_sign_up_activity_password))
            .check(ViewAssertions.matches(ViewMatchers.withText(password)));

    Espresso.onView(ViewMatchers.withId(R.id.button_sign_up_activity_create_account))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()));
  }
}
