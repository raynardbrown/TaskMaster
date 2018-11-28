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
import com.example.android.taskmaster.databinding.DialogAddCardBinding;

public class AddCardDialogFragment extends DialogFragment
{
  private DialogAddCardBinding binding;
  private IAddCardDialogListener listener;
  private int position;

  @Override
  public void onAttach(Context context)
  {
    super.onAttach(context);

    try
    {
      listener = (IAddCardDialogListener)context;
    }
    catch(ClassCastException e)
    {
      throw new ClassCastException(context.toString()  + " must implement IAddCardDialogListener");
    }
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    if(getArguments() != null)
    {
      position = getArguments().getInt(getString(R.string.task_list_position_key));
    }

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    LayoutInflater inflater = getActivity().getLayoutInflater();
    binding = DataBindingUtil.inflate(inflater, R.layout.dialog_add_card, null, false);

    builder.setView(binding.getRoot())
            .setTitle(R.string.task_group_activity_dialog_add_card_title_string)
            .setPositiveButton(R.string.task_group_activity_dialog_add_card_add_card_button_string, new DialogInterface.OnClickListener()
            {
              @Override
              public void onClick(DialogInterface dialog, int which)
              {
                listener.onAddCardClick(binding.etTaskGroupActivityDialogAddCardCardTitle.getText().toString(),
                        binding.etTaskGroupActivityDialogAddCardDetailedDescriptionTitle.getText().toString(),
                        position);
              }
            })
            .setNegativeButton(R.string.task_group_activity_dialog_add_card_cancel_button_string, new DialogInterface.OnClickListener()
            {
              @Override
              public void onClick(DialogInterface dialog, int which)
              {
                AddCardDialogFragment.this.getDialog().cancel();
              }
            });

    final AlertDialog alertDialog = builder.create();

    alertDialog.setOnShowListener(new DialogInterface.OnShowListener()
   {
     @Override
      public void onShow(DialogInterface dialog)
      {
        // Initially disable the add card button
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
      }
    });

    setupTextChangeListener(alertDialog);

    return alertDialog;
  }

  private void setupTextChangeListener(AlertDialog dialog)
  {
    binding.etTaskGroupActivityDialogAddCardCardTitle.addTextChangedListener(new CardTitleTextWatcher(dialog));
  }

  class CardTitleTextWatcher implements TextWatcher
  {
    private AlertDialog alertDialog;

    CardTitleTextWatcher(AlertDialog alertDialog)
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
