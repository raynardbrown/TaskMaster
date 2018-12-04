package com.example.android.taskmaster.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.ActivityLogInBinding;
import com.example.android.taskmaster.utils.InputValidator;
import com.example.android.taskmaster.view.dialog.ForgotPasswordDialogFragment;
import com.example.android.taskmaster.view.dialog.IForgotPasswordDialogListener;

public class LogInActivity extends AppCompatActivity implements IForgotPasswordDialogListener
{
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
  public void onSendPasswordClick(String emailAddress)
  {
    // TODO: Actually send the password request
    Toast.makeText(this, emailAddress, Toast.LENGTH_LONG).show();
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
        // TODO: trigger log in and show progress
        // TODO: for now fake the login and start the main activity for testing
        Intent intent = new Intent(LogInActivity.this, MainActivity.class);

        // Also clear the back stack
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK  | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);

        // TODO: We are logged in we no logger need to go back to this activity. Move this to the
        //       part of the code that handles log in success.
        finish();
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
