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
import android.view.View;
import android.widget.AdapterView;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.DialogMoveTaskListBinding;

import java.util.ArrayList;
import java.util.List;

public class MoveTaskListDialogFragment extends DialogFragment
{
  private DialogMoveTaskListBinding binding;
  private IMoveTaskListDialogListener listener;
  private int currentSelection;

  @Override
  public void onAttach(Context context)
  {
    super.onAttach(context);

    try
    {
      listener = (IMoveTaskListDialogListener)context;
    }
    catch(ClassCastException e)
    {
      throw new ClassCastException(context.toString()  + " must implement IMoveTaskListDialogListener");
    }
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    if(getArguments() != null)
    {
      // TODO: also grab a list of task spinner items
      currentSelection = getArguments().getInt(getString(R.string.task_list_spinner_selection_key));
    }

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    LayoutInflater inflater = getActivity().getLayoutInflater();
    binding = DataBindingUtil.inflate(inflater, R.layout.dialog_move_task_list, null, false);

    setupUi();

    builder.setView(binding.getRoot())
            .setTitle(R.string.task_group_activity_dialog_move_task_list_title_string)
            .setPositiveButton(R.string.task_group_activity_dialog_move_task_list_move_button_string, new DialogInterface.OnClickListener()
            {
              @Override
              public void onClick(DialogInterface dialog, int which)
              {
                listener.onTaskListMoveClick(binding.spinnerTaskGroupDialogMoveTaskList.getSelectedItem().toString());
              }
            })
            .setNegativeButton(R.string.task_group_activity_dialog_move_task_list_cancel_button_string, new DialogInterface.OnClickListener()
            {
              @Override
              public void onClick(DialogInterface dialog, int which)
              {
                MoveTaskListDialogFragment.this.getDialog().cancel();
              }
            });

    // TODO: Do I really need this? I already call getSelectedItem in onClick
    setupSelectionListener();

    return builder.create();
  }

  private void setupUi()
  {
    // TODO: Dummy items, actually get these values via fragment arguments
    List<TaskListSpinnerItem> listSpinnerItems = new ArrayList<>();
    listSpinnerItems.add(new TaskListSpinnerItem("Task Group One", 1));
    listSpinnerItems.add(new TaskListSpinnerItem("Task Group Two", 2));
    listSpinnerItems.add(new TaskListSpinnerItem("Task Group Three", 3));

    TaskListSpinnerItemAdapter adapter = new TaskListSpinnerItemAdapter(getContext(),
            R.layout.task_group_spinner_item,
            R.id.tv_task_group_spinner_item,
            listSpinnerItems);

    binding.spinnerTaskGroupDialogMoveTaskList.setAdapter(adapter);
  }

  private void setupSelectionListener()
  {
    binding.spinnerTaskGroupDialogMoveTaskList.setOnItemSelectedListener(new TaskListSelectionListener());
  }

  class TaskListSelectionListener implements AdapterView.OnItemSelectedListener
  {

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }
  }
}
