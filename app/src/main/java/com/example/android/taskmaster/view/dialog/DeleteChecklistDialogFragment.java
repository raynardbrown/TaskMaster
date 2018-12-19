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
import com.example.android.taskmaster.databinding.DialogDeleteChecklistBinding;

public class DeleteChecklistDialogFragment extends DialogFragment
{
  private IDeleteChecklistListener listener;
  private int position;

  @Override
  public void onAttach(Context context)
  {
    super.onAttach(context);

    try
    {
      listener = (IDeleteChecklistListener)context;
    }
    catch(ClassCastException e)
    {
      throw new ClassCastException(context.toString()  + " must implement IDeleteChecklistListener");
    }
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    if(getArguments() != null)
    {
      position = getArguments().getInt(getString(R.string.check_list_position_key));
    }

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    LayoutInflater inflater = getActivity().getLayoutInflater();
    DialogDeleteChecklistBinding binding = DataBindingUtil.inflate(inflater, R.layout.dialog_delete_checklist, null, false);

    builder.setView(binding.getRoot())
            .setTitle(R.string.card_detail_activity_dialog_delete_checklist_title_string)
            .setPositiveButton(R.string.card_detail_activity_dialog_delete_checklist_delete_button_string, new DialogInterface.OnClickListener()
            {
              @Override
              public void onClick(DialogInterface dialog, int which)
              {
                listener.onChecklistDelete(position);
              }
            })
            .setNegativeButton(R.string.card_detail_activity_dialog_delete_checklist_cancel_button_string, new DialogInterface.OnClickListener()
            {
              @Override
              public void onClick(DialogInterface dialog, int which)
              {
                DeleteChecklistDialogFragment.this.getDialog().cancel();
              }
            });

    return builder.create();
  }
}
