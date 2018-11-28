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

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.DialogAddTaskListBinding;

public class AddTaskListDialogFragment extends DialogFragment
{
  private DialogAddTaskListBinding binding;
  private IAddTaskListDialogListener listener;

  @Override
  public void onAttach(Context context)
  {
    super.onAttach(context);

    try
    {
      listener = (IAddTaskListDialogListener)context;
    }
    catch(ClassCastException e)
    {
      throw new ClassCastException(context.toString()  + " must implement IAddTaskListDialogListener");
    }
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    LayoutInflater inflater = getActivity().getLayoutInflater();
    binding = DataBindingUtil.inflate(inflater, R.layout.dialog_add_task_list, null, false);

    builder.setView(binding.getRoot())
            .setTitle(R.string.task_group_activity_dialog_add_task_list_title_string)
            .setPositiveButton(R.string.task_group_activity_dialog_add_task_list_add_button_string, new DialogInterface.OnClickListener()
            {
              @Override
              public void onClick(DialogInterface dialog, int which)
              {
                listener.onAddTaskListClick(binding.etTaskGroupActivityDialogAddTaskListTaskListName.getText().toString());
              }
            })
            .setNegativeButton(R.string.task_group_activity_dialog_add_task_list_cancel_button_string, new DialogInterface.OnClickListener()
            {
              @Override
              public void onClick(DialogInterface dialog, int which)
              {
                AddTaskListDialogFragment.this.getDialog().cancel();
              }
            });

    final AlertDialog alertDialog = builder.create();

    alertDialog.setOnShowListener(new DialogInterface.OnShowListener()
    {
      @Override
      public void onShow(DialogInterface dialog)
      {
        // Initially disable the add task list button
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
      }
    });

    setupTextChangeListener(alertDialog);

    return alertDialog;
  }

  private void setupTextChangeListener(AlertDialog dialog)
  {
    binding.etTaskGroupActivityDialogAddTaskListTaskListName.addTextChangedListener(new TaskListNameTextWatcher(dialog));
  }

  class TaskListNameTextWatcher implements TextWatcher
  {
    private AlertDialog alertDialog;

    TaskListNameTextWatcher(AlertDialog alertDialog)
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
}
