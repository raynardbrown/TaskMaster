package com.example.android.taskmaster.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.ActivityLogInBinding;
import com.example.android.taskmaster.utils.InputValidator;
import com.example.android.taskmaster.view.dialog.ForgotPasswordDialogFragment;
import com.example.android.taskmaster.view.dialog.IForgotPasswordDialogListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LogInActivity extends AppCompatActivity implements IForgotPasswordDialogListener
{
  private static final String TAG = LogInActivity.class.getSimpleName();

  private ActivityLogInBinding binding;

  /**
   * Check if the email address field is empty per the text change listener.
   */
  private boolean emailFieldEmpty;

  /**
   * Check if the password field is empty per the text change listener
   */
  private boolean passwordFieldEmpty;

  public static Intent getStartIntent(Context context)
  {
    return new Intent(context, LogInActivity.class);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_log_in);

    emailFieldEmpty = true;
    passwordFieldEmpty = true;

    disableLogInButton();

    // set up the toolbar
    setSupportActionBar(binding.tbLogInActivity);

    ActionBar actionBar = getSupportActionBar();
    if(actionBar != null)
    {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setTitle(getString(R.string.log_in_activity_title));
    }

    registerTextChangeHandlers();
    registerClickHandlers();
  }

  private void registerTextChangeHandlers()
  {
    binding.etLogInActivityEmailAddress.addTextChangedListener(new EmailAddressFieldWatcher());
    binding.etLogInActivityPassword.addTextChangedListener(new PasswordFieldWatcher());
  }

  private void registerClickHandlers()
  {
    binding.buttonLogInActivityLogIn.setOnClickListener(new LogInClickHandler());
    binding.tvLogInActivityForgotPassword.setOnClickListener(new ForgotYourPasswordClickHandler());
  }

  private void disableLogInButton()
  {
    binding.buttonLogInActivityLogIn.setEnabled(false);
  }

  /**
   * Enables the Log In button if the input in both the email address and password fields are
   * non-empty.
   */
  private void enableLogInButton()
  {
    if(!emailFieldEmpty && !passwordFieldEmpty)
    {
      binding.buttonLogInActivityLogIn.setEnabled(true);
    }
  }

  private void showPasswordRetrievalDialog()
  {
    DialogFragment dialogFragment = new ForgotPasswordDialogFragment();

    dialogFragment.show(getSupportFragmentManager(),
            getString(R.string.log_in_activity_dialog_send_password_tag_string));
  }

  @Override
  public void onSendPasswordClick(final String emailAddress)
  {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    // show progress bar
    binding.progressBarLogInActivity.setVisibility(View.VISIBLE);

    firebaseAuth.sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener(new OnCompleteListener<Void>()
            {
              @Override
              public void onComplete(@NonNull Task<Void> task)
              {
                if(task.isSuccessful())
                {
                  // hide progress bar
                  binding.progressBarLogInActivity.setVisibility(View.GONE);

                  Toast.makeText(LogInActivity.this,
                          String.format(getString(R.string.firebase_password_sent_success), emailAddress),
                          Toast.LENGTH_LONG).show();
                }
                else
                {
                  // password reset failed

                  // hide progress bar
                  binding.progressBarLogInActivity.setVisibility(View.GONE);

                  Exception exception = task.getException();

                  if(exception != null)
                  {
                    try
                    {
                      throw exception;
                    }
                    catch(FirebaseAuthInvalidUserException e)
                    {
                      binding.tilLogInActivityEmailAddress.setError(getString(R.string.error_firebase_reset_password_failed_email_invalid_string));
                      binding.tilLogInActivityEmailAddress.requestFocus();
                    }
                    catch(Exception e)
                    {
                      Toast.makeText(LogInActivity.this,
                              getString(R.string.error_firebase_reset_password_failed),
                              Toast.LENGTH_LONG).show();
                    }
                  }
                }
              }
            });
  }

  class EmailAddressFieldWatcher implements TextWatcher
  {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
      if(s.length() > 0)
      {
        LogInActivity.this.emailFieldEmpty = false;

        enableLogInButton();
      }
      else
      {
        LogInActivity.this.emailFieldEmpty = true;

        disableLogInButton();
      }
    }

    @Override
    public void afterTextChanged(Editable s)
    {

    }
  }

  class PasswordFieldWatcher implements TextWatcher
  {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
      if(s.length() > 0)
      {
        LogInActivity.this.passwordFieldEmpty = false;

        enableLogInButton();
      }
      else
      {
        LogInActivity.this.passwordFieldEmpty = true;

        disableLogInButton();
      }
    }

    @Override
    public void afterTextChanged(Editable s)
    {

    }
  }

  class LogInClickHandler implements View.OnClickListener
  {
    @Override
    public void onClick(View v)
    {
      // Validate the email
      String emailString = binding.etLogInActivityEmailAddress.getText().toString();
      boolean emailFieldIsValid = false;

      emailString = InputValidator.sanitizeInput(emailString);

      if(InputValidator.isValidInput(emailString))
      {
        if(InputValidator.isEmailValid(emailString))
        {
          emailFieldIsValid = true;

          // clear any errors
          binding.tilLogInActivityEmailAddress.setError(null);
        }
        else
        {
          // email is invalid give a hint to the user
          binding.tilLogInActivityEmailAddress.setError(getString(R.string.error_log_in_activity_email_address_string));
        }
      }
      else
      {
        // error empty give a hint to the user
        binding.tilLogInActivityEmailAddress.setError(getString(R.string.error_log_in_activity_email_address_string));
      }

      // Validate the password field.
      String passwordString = binding.etLogInActivityPassword.getText().toString();
      boolean passwordFieldIsValid = false;

      passwordString = InputValidator.sanitizeInput(passwordString);

      if(InputValidator.isValidInput(passwordString))
      {
        passwordFieldIsValid = true;

        // clear any errors
        binding.tilLogInActivityPassword.setError(null);
      }
      else
      {
        // error must contain a password
        binding.tilLogInActivityPassword.setError(getString(R.string.error_log_in_activity_password_string));
      }

      if(emailFieldIsValid && passwordFieldIsValid)
      {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        // show progress bar
        binding.progressBarLogInActivity.setVisibility(View.VISIBLE);

        firebaseAuth.signInWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task)
                  {
                    // hide progress bar
                    binding.progressBarLogInActivity.setVisibility(View.GONE);

                    if(task.isSuccessful())
                    {
                      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LogInActivity.this);
                      SharedPreferences.Editor editor = sharedPreferences.edit();

                      editor.putBoolean(getString(R.string.shared_pref_user_logged_in_key), true);
                      editor.apply();

                      Intent intent = MainActivity.getStartIntent(LogInActivity.this);

                      startActivity(intent);

                      // We are logged in we no logger need to go back to this activity.
                      finish();
                    }
                    else
                    {
                      Exception exception = task.getException();

                      if(exception != null)
                      {
                        Log.w(LogInActivity.TAG, "signInWithEmailAndPassword: failed: ", exception);

                        try
                        {
                          throw exception;
                        }
                        catch(FirebaseAuthInvalidUserException e)
                        {
                          binding.tilLogInActivityEmailAddress.setError(getString(R.string.error_firebase_log_in_failed_email_invalid_string));
                          binding.tilLogInActivityEmailAddress.requestFocus();
                        }
                        catch(FirebaseAuthInvalidCredentialsException e)
                        {
                          binding.tilLogInActivityPassword.setError(getString(R.string.error_firebase_log_in_failed_password_invalid_string));
                          binding.tilLogInActivityPassword.requestFocus();
                        }
                        catch(Exception e)
                        {
                          Toast.makeText(LogInActivity.this,
                                  getString(R.string.error_firebase_log_in_failed),
                                  Toast.LENGTH_LONG).show();
                        }
                      }
                    }
                  }
                });
      }
    }
  } // end class LogInClickHandler

  class ForgotYourPasswordClickHandler implements View.OnClickListener
  {
    @Override
    public void onClick(View v)
    {
      showPasswordRetrievalDialog();
    }
  } // end class ForgotYourPasswordClickHandler
}
