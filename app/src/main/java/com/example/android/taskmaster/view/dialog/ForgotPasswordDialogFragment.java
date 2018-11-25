package com.example.android.taskmaster.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.DialogSendPasswordBinding;

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
                listener.onSendPasswordClick(binding.etDialogEmailAddress.getText().toString());
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

    return builder.create();
  }
}
