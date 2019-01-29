package com.example.android.taskmaster.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.ActivitySignUpBinding;
import com.example.android.taskmaster.utils.InputValidator;
import com.example.android.taskmaster.utils.TaskMasterUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class SignUpActivity extends AppCompatActivity
{
  private static final String TAG = SignUpActivity.class.getSimpleName();

  private ActivitySignUpBinding binding;

  /**
   * Check if the first name field is empty per the text change listener.
   */
  private boolean firstNameFieldEmpty;

  /**
   * Check if the last name field is empty per the text change listener.
   */
  private boolean lastNameFieldEmpty;

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
    return new Intent(context, SignUpActivity.class);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);

    firstNameFieldEmpty = true;
    lastNameFieldEmpty = true;
    emailFieldEmpty = true;
    passwordFieldEmpty = true;

    disableCreateAccountButton();

    // set up the toolbar
    setSupportActionBar(binding.tbSignUpActivity);

    ActionBar actionBar = getSupportActionBar();
    if(actionBar != null)
    {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setTitle(getString(R.string.sign_up_activity_title));
    }

    registerTextChangeHandlers();
    registerClickHandler();
  }

  private void registerTextChangeHandlers()
  {
    binding.etSignUpActivityFirstName.addTextChangedListener(new FirstNameFieldWatcher());
    binding.etSignUpActivityLastName.addTextChangedListener(new LastNameFieldWatcher());
    binding.etSignUpActivityEmailAddress.addTextChangedListener(new EmailAddressFieldWatcher());
    binding.etSignUpActivityPassword.addTextChangedListener(new PasswordFieldWatcher());
  }

  private void registerClickHandler()
  {
    binding.buttonSignUpActivityCreateAccount.setOnClickListener(new CreateAccountClickHandler());
  }

  private void disableCreateAccountButton()
  {
    binding.buttonSignUpActivityCreateAccount.setEnabled(false);
  }

  private void enableCreateAccountButton()
  {
    if(!firstNameFieldEmpty && !lastNameFieldEmpty && !emailFieldEmpty && !passwordFieldEmpty)
    {
      binding.buttonSignUpActivityCreateAccount.setEnabled(true);
    }
  }

  class FirstNameFieldWatcher implements TextWatcher
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
        SignUpActivity.this.firstNameFieldEmpty = false;

        enableCreateAccountButton();
      }
      else
      {
        SignUpActivity.this.firstNameFieldEmpty = true;

        disableCreateAccountButton();
      }
    }

    @Override
    public void afterTextChanged(Editable s)
    {

    }
  } // end class FirstNameFieldWatcher

  class LastNameFieldWatcher implements TextWatcher
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
        SignUpActivity.this.lastNameFieldEmpty = false;

        enableCreateAccountButton();
      }
      else
      {
        SignUpActivity.this.lastNameFieldEmpty = true;

        disableCreateAccountButton();
      }
    }

    @Override
    public void afterTextChanged(Editable s)
    {

    }
  } // end class LastNameFieldWatcher

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
        SignUpActivity.this.emailFieldEmpty = false;

        enableCreateAccountButton();
      }
      else
      {
        SignUpActivity.this.emailFieldEmpty = true;

        disableCreateAccountButton();
      }
    }

    @Override
    public void afterTextChanged(Editable s)
    {

    }
  } // end class EmailAddressFieldWatcher

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
        SignUpActivity.this.passwordFieldEmpty = false;

        enableCreateAccountButton();
      }
      else
      {
        SignUpActivity.this.passwordFieldEmpty = true;

        disableCreateAccountButton();
      }
    }

    @Override
    public void afterTextChanged(Editable s)
    {

    }
  } // end class PasswordFieldWatcher

  class CreateAccountClickHandler implements View.OnClickListener
  {
    @Override
    public void onClick(View v)
    {
      // Validate the first name field
      String firstNameString = binding.etSignUpActivityFirstName.getText().toString();
      boolean firstNameIsValid = false;

      firstNameString = InputValidator.sanitizeInput(firstNameString);

      if(InputValidator.isValidInput(firstNameString))
      {
        firstNameIsValid = true;

        // clear errors
        binding.tilSignUpActivityFirstName.setError(null);
      }
      else
      {
        binding.tilSignUpActivityFirstName.setError(getString(R.string.error_sign_up_activity_first_name_string));
      }

      // Validate the last name field
      String lastNameString = binding.etSignUpActivityLastName.getText().toString();
      boolean lastNameIsValid = false;

      lastNameString = InputValidator.sanitizeInput(lastNameString);

      if(InputValidator.isValidInput(lastNameString))
      {
        lastNameIsValid = true;

        // clear errors
        binding.tilSignUpActivityLastName.setError(null);
      }
      else
      {
        binding.tilSignUpActivityLastName.setError(getString(R.string.error_sign_up_activity_last_name_string));
      }

      // Validate the email
      String emailString = binding.etSignUpActivityEmailAddress.getText().toString();
      boolean emailFieldIsValid = false;

      emailString = InputValidator.sanitizeInput(emailString);

      if(InputValidator.isValidInput(emailString))
      {
        if(InputValidator.isEmailValid(emailString))
        {
          emailFieldIsValid = true;

          // clear any errors
          binding.tilSignUpActivityEmailAddress.setError(null);
        }
        else
        {
          // email is invalid give a hint to the user
          binding.tilSignUpActivityEmailAddress.setError(getString(R.string.error_sign_up_activity_email_address_string));
        }
      }
      else
      {
        // error empty give a hint to the user
        binding.tilSignUpActivityEmailAddress.setError(getString(R.string.error_sign_up_activity_email_address_string));
      }

      // Validate the password field.
      String passwordString = binding.etSignUpActivityPassword.getText().toString();
      boolean passwordFieldIsValid = false;

      passwordString = InputValidator.sanitizeInput(passwordString);

      if(InputValidator.isValidInput(passwordString))
      {
        passwordFieldIsValid = true;

        // clear any errors
        binding.tilSignUpActivityPassword.setError(null);
      }
      else
      {
        // error must contain a password
        binding.tilSignUpActivityPassword.setError(getString(R.string.error_sign_up_activity_password_string));
      }

      if(firstNameIsValid && lastNameIsValid && emailFieldIsValid && passwordFieldIsValid)
      {
        if(TaskMasterUtils.isNetworkAvailable(SignUpActivity.this))
        {
          final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

          // show progress bar
          binding.progressBarSignUpActivity.setVisibility(View.VISIBLE);

          firebaseAuth.createUserWithEmailAndPassword(emailString, passwordString)
                  .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>()
                  {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                      // hide progress bar
                      binding.progressBarSignUpActivity.setVisibility(View.GONE);

                      if(task.isSuccessful())
                      {
                        // user creation successful

                        // NOTE: Normally you would send an email verification on account creation
                        //       success, from there you would check the verification flag
                        //       (isEmailVerified) during log in and accept or reject a log in.

                        // launch the log in activity
                        Intent intent = LogInActivity.getStartIntent(SignUpActivity.this);

                        startActivity(intent);
                      }
                      else
                      {
                        // create user failed

                        Exception exception = task.getException();

                        if(exception != null)
                        {
                          Log.w(SignUpActivity.TAG, "createUserWithEmail: failed: ", exception);

                          try
                          {
                            throw exception;
                          }
                          catch(FirebaseAuthWeakPasswordException e)
                          {
                            binding.tilSignUpActivityPassword.setError(e.getReason());
                            binding.tilSignUpActivityPassword.requestFocus();
                          }
                          catch(FirebaseAuthInvalidCredentialsException e)
                          {
                            binding.tilSignUpActivityEmailAddress.setError(getString(R.string.error_firebase_create_account_failed_email_invalid_string));
                            binding.tilSignUpActivityEmailAddress.requestFocus();
                          }
                          catch(FirebaseAuthUserCollisionException e)
                          {
                            binding.tilSignUpActivityEmailAddress.setError(getString(R.string.error_firebase_create_account_failed_email_exists_string));
                            binding.tilSignUpActivityEmailAddress.requestFocus();
                          }
                          catch(Exception e)
                          {
                            Toast.makeText(SignUpActivity.this,
                                    getString(R.string.error_firebase_create_account_failed),
                                    Toast.LENGTH_LONG).show();
                          }
                        }
                      }
                    }
                  });
        }
        else
        {
          // no network available
          Toast.makeText(SignUpActivity.this,
                  getString(R.string.error_network_not_available),
                  Toast.LENGTH_LONG).show();
        }
      }

    }
  } // end class CreateAccountClickHandler
}
