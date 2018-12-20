package com.example.android.taskmaster.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.DialogSendPasswordBinding;
import com.example.android.taskmaster.utils.InputValidator;

public class ForgotPasswordDialogFragment extends DialogFragment
{
  private DialogSendPasswordBinding binding;
  private IForgotPasswordDialogListener listener;

  @Override
  public void onAttach(Context context)
  {
    super.onAttach(context);

    try
    {
      listener = (IForgotPasswordDialogListener)context;
    }
    catch(ClassCastException e)
    {
      throw new ClassCastException(context.toString()  + " must implement IForgotPasswordDialogListener");
    }
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    LayoutInflater inflater = getActivity().getLayoutInflater();
    binding = DataBindingUtil.inflate(inflater, R.layout.dialog_send_password, null, false);

    builder.setView(binding.getRoot())
            .setTitle(R.string.log_in_activity_dialog_send_password_title_string)
            .setPositiveButton(R.string.log_in_activity_dialog_send_password_send_password_button_string, new DialogInterface.OnClickListener()
            {
              @Override
              public void onClick(DialogInterface dialog, int which)
              {
                // unused, we are using a custom click handler so that we can validate user input.
              }
            })
            .setNegativeButton(R.string.log_in_activity_dialog_send_password_cancel_button_string, new DialogInterface.OnClickListener()
            {
              @Override
              public void onClick(DialogInterface dialog, int which)
              {
                ForgotPasswordDialogFragment.this.getDialog().cancel();
              }
            });

    final AlertDialog alertDialog = builder.create();

    alertDialog.setOnShowListener(new DialogInterface.OnShowListener()
    {
      @Override
      public void onShow(DialogInterface dialog)
      {
        // Initially disable the send password button
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        // add a custom click handler to the send password button so that we can verify that
        // the user entered a valid password. The default click handler for the dialog will just
        // dismiss the dialog after the positive button has been pressed.
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new SendPasswordClickHandler(alertDialog));
      }
    });

    setupTextChangeListener(alertDialog);

    return alertDialog;
  }

  private void setupTextChangeListener(AlertDialog dialog)
  {
    binding.etDialogEmailAddress.addTextChangedListener(new EmailAddressTextWatcher(dialog));
  }

  class EmailAddressTextWatcher implements TextWatcher
  {
    private AlertDialog alertDialog;

    EmailAddressTextWatcher(AlertDialog alertDialog)
    {
      this.alertDialog = alertDialog;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
      if(TextUtils.isEmpty(s))
      {
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
      }
      else
      {
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
      }
    }

    @Override
    public void afterTextChanged(Editable s)
    {

    }
  }

  class SendPasswordClickHandler implements View.OnClickListener
  {
    private AlertDialog alertDialog;

    SendPasswordClickHandler(AlertDialog alertDialog)
    {
      this.alertDialog = alertDialog;
    }

    @Override
    public void onClick(View v)
    {
      // Validate the email email address

      EditText emailAddressEditText = binding.etDialogEmailAddress;
      String emailString = emailAddressEditText.getText().toString();

      emailString = InputValidator.sanitizeInput(emailString);

      if(InputValidator.isValidInput(emailString))
      {
        if(InputValidator.isEmailValid(emailString))
        {
          emailAddressEditText.setError(null);

          listener.onSendPasswordClick(emailString);

          alertDialog.dismiss();
        }
        else
        {
          emailAddressEditText.setError(getString(R.string.error_log_in_activity_dialog_send_password_email_address_string));
        }
      }
      else
      {
        emailAddressEditText.setError(getString(R.string.error_log_in_activity_dialog_send_password_email_address_string));
      }
    }
  }
}
