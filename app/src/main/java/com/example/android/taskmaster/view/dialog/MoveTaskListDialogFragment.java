package com.example.android.taskmaster.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.DialogMoveTaskListBinding;

import java.util.ArrayList;
import java.util.List;

public class MoveTaskListDialogFragment extends DialogFragment
{
  private static final String DIALOG_FRAGMENT_TAG = "move_task_list_tag";

  private DialogMoveTaskListBinding binding;

  private IMoveTaskListDialogListener listener;

  private List<TaskGroupSpinnerItem> taskGroupSpinnerItemList;

  private int currentTaskGroup;

  private int taskListIndex;

  public static MoveTaskListDialogFragment newInstance(Context context,
                                                       List<TaskGroupSpinnerItem> taskGroupSpinnerItemList,
                                                       int currentTaskGroup,
                                                       int taskListIndex)
  {
    MoveTaskListDialogFragment fragment = new MoveTaskListDialogFragment();
    Bundle bundle = new Bundle();

    bundle.putParcelableArrayList(context.getString(R.string.move_task_list_task_group_list_key),
            new ArrayList<>(taskGroupSpinnerItemList));

    bundle.putInt(context.getString(R.string.move_task_list_task_group_selection_index_key),
            currentTaskGroup);

    bundle.putInt(context.getString(R.string.move_task_list_task_list_index_key), taskListIndex);

    fragment.setArguments(bundle);
    return fragment;
  }

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
    Bundle bundle = getArguments();
    if(bundle != null)
    {
      taskGroupSpinnerItemList = bundle.getParcelableArrayList(getString(R.string.move_task_list_task_group_list_key));

      currentTaskGroup = bundle.getInt(getString(R.string.move_task_list_task_group_selection_index_key));

      taskListIndex = bundle.getInt(getString(R.string.move_task_list_task_list_index_key));
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
                listener.onTaskListMoveClick(taskListIndex,
                        currentTaskGroup,
                        binding.spinnerTaskGroupDialogMoveTaskList.getSelectedItemPosition());
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

    return builder.create();
  }

  private void setupUi()
  {
    TaskGroupSpinnerItemAdapter adapter = new TaskGroupSpinnerItemAdapter(getContext(),
            R.layout.task_group_spinner_item,
            R.id.tv_task_group_spinner_item,
            taskGroupSpinnerItemList);

    binding.spinnerTaskGroupDialogMoveTaskList.setAdapter(adapter);

    binding.spinnerTaskGroupDialogMoveTaskList.setSelection(currentTaskGroup);
  }

  public void show(FragmentManager fragmentManager)
  {
    super.show(fragmentManager, MoveTaskListDialogFragment.DIALOG_FRAGMENT_TAG);
  }
}
