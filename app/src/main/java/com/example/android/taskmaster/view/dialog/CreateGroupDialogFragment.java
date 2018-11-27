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
import com.example.android.taskmaster.databinding.DialogCreateGroupBinding;

public class CreateGroupDialogFragment extends DialogFragment
{
  private DialogCreateGroupBinding binding;
  private ICreateGroupDialogListener listener;

  @Override
  public void onAttach(Context context)
  {
    super.onAttach(context);

    try
    {
      listener = (ICreateGroupDialogListener)context;
    }
    catch(ClassCastException e)
    {
      throw new ClassCastException(context.toString()  + " must implement ICreateGroupDialogListener");
    }
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    LayoutInflater inflater = getActivity().getLayoutInflater();
    binding = DataBindingUtil.inflate(inflater, R.layout.dialog_create_group, null, false);

    builder.setView(binding.getRoot())
            .setTitle(R.string.main_activity_dialog_create_group_title_string)
            .setPositiveButton(R.string.main_activity_dialog_create_group_create_button_string, new DialogInterface.OnClickListener()
            {
              @Override
              public void onClick(DialogInterface dialog, int which)
              {
                listener.onCreateGroupClick(binding.etDialogGroupName.getText().toString());
              }
            })
            .setNegativeButton(R.string.main_activity_dialog_create_group_cancel_button_string, new DialogInterface.OnClickListener()
            {
              @Override
              public void onClick(DialogInterface dialog, int which)
              {
                CreateGroupDialogFragment.this.getDialog().cancel();
              }
            });

    return builder.create();
  }
}
