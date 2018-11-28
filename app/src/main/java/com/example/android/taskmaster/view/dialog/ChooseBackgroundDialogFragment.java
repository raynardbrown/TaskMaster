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

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.DialogChooseBackgroundBinding;
import com.example.android.taskmaster.utils.TaskMasterConstants;

public class ChooseBackgroundDialogFragment extends DialogFragment
{
  private DialogChooseBackgroundBinding binding;
  private IChooseBackgroundDialogListener listener;

  private int currentBackgroundColor;

  @Override
  public void onAttach(Context context)
  {
    super.onAttach(context);

    try
    {
      listener = (IChooseBackgroundDialogListener)context;
    }
    catch(ClassCastException e)
    {
      throw new ClassCastException(context.toString()  + " must implement IChooseBackgroundDialogListener");
    }
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    LayoutInflater inflater = getActivity().getLayoutInflater();
    binding = DataBindingUtil.inflate(inflater, R.layout.dialog_choose_background, null, false);

    if(getArguments() != null)
    {
      currentBackgroundColor = getArguments().getInt(getString(R.string.choose_background_color_key));

      setSelection(currentBackgroundColor);
    }

    setupClickHandlers();

    builder.setView(binding.getRoot())
            .setTitle(R.string.task_group_activity_dialog_choose_background_title_string)
            .setPositiveButton(R.string.task_group_activity_dialog_choose_background_ok_button_string, new DialogInterface.OnClickListener()
            {
              @Override
              public void onClick(DialogInterface dialog, int which)
              {
                listener.onBackgroundSelected(currentBackgroundColor);
              }
            })
            .setNegativeButton(R.string.task_group_activity_dialog_choose_background_cancel_button_string, new DialogInterface.OnClickListener()
            {
              @Override
              public void onClick(DialogInterface dialog, int which)
              {
                ChooseBackgroundDialogFragment.this.getDialog().cancel();
              }
            });

    return builder.create();
  }

  private void setupClickHandlers()
  {
    binding.viewDialogChooseBackgroundRed.setOnClickListener(new RedClickHandler());
    binding.viewDialogChooseBackgroundGreen.setOnClickListener(new GreenClickHandler());
    binding.viewDialogChooseBackgroundIndigo.setOnClickListener(new IndigoClickHandler());
  }

  class RedClickHandler implements View.OnClickListener
  {
    @Override
    public void onClick(View v)
    {
      clearLastSelection();

      setSelection(TaskMasterConstants.RED_BACKGROUND);
    }
  }

  class GreenClickHandler implements View.OnClickListener
  {
    @Override
    public void onClick(View v)
    {
      clearLastSelection();

      setSelection(TaskMasterConstants.GREEN_BACKGROUND);
    }
  }

  class IndigoClickHandler implements View.OnClickListener
  {
    @Override
    public void onClick(View v)
    {
      clearLastSelection();

      setSelection(TaskMasterConstants.INDIGO_BACKGROUND);
    }
  }

  private void clearLastSelection()
  {
    switch(currentBackgroundColor)
    {
      case TaskMasterConstants.RED_BACKGROUND:
      {
        ChooseBackgroundDialogFragment.this.binding.ivDialogChooseBackgroundRedSelected.setVisibility(View.INVISIBLE);
        break;
      }

      case TaskMasterConstants.GREEN_BACKGROUND:
      {
        ChooseBackgroundDialogFragment.this.binding.ivDialogChooseBackgroundGreenSelected.setVisibility(View.INVISIBLE);
        break;
      }

      case TaskMasterConstants.INDIGO_BACKGROUND:
      {
        ChooseBackgroundDialogFragment.this.binding.ivDialogChooseBackgroundIndigoSelected.setVisibility(View.INVISIBLE);
        break;
      }
    }

    currentBackgroundColor = 0;
  }

  private void setSelection(int newSelection)
  {
    switch(newSelection)
    {
      case TaskMasterConstants.RED_BACKGROUND:
      {
        ChooseBackgroundDialogFragment.this.binding.ivDialogChooseBackgroundRedSelected.setVisibility(View.VISIBLE);
        break;
      }

      case TaskMasterConstants.GREEN_BACKGROUND:
      {
        ChooseBackgroundDialogFragment.this.binding.ivDialogChooseBackgroundGreenSelected.setVisibility(View.VISIBLE);
        break;
      }

      case TaskMasterConstants.INDIGO_BACKGROUND:
      {
        ChooseBackgroundDialogFragment.this.binding.ivDialogChooseBackgroundIndigoSelected.setVisibility(View.VISIBLE);
        break;
      }
    }

    currentBackgroundColor = newSelection;
  }
}
