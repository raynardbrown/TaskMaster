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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.DialogCreateGroupBinding;

public class CreateGroupDialogFragment extends DialogFragment
{
  private static final String DIALOG_FRAGMENT_TAG = "create_group_tag";

  private DialogCreateGroupBinding binding;
  private ICreateGroupDialogListener listener;

  public static CreateGroupDialogFragment newInstance()
  {
    CreateGroupDialogFragment fragment = new CreateGroupDialogFragment();
    Bundle bundle = new Bundle();
    fragment.setArguments(bundle);
    return fragment;
  }

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

    final AlertDialog alertDialog = builder.create();

    alertDialog.setOnShowListener(new DialogInterface.OnShowListener()
    {
      @Override
      public void onShow(DialogInterface dialog)
      {
        // Initially disable the create button
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
      }
    });

    setupTextChangeListener(alertDialog);

    return alertDialog;
  }

  private void setupTextChangeListener(AlertDialog dialog)
  {
    binding.etDialogGroupName.addTextChangedListener(new GroupNameTextWatcher(dialog));
  }

  class GroupNameTextWatcher implements TextWatcher
  {
    private AlertDialog alertDialog;

    GroupNameTextWatcher(AlertDialog alertDialog)
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

  public void show(FragmentManager fragmentManager)
  {
    super.show(fragmentManager, CreateGroupDialogFragment.DIALOG_FRAGMENT_TAG);
  }
}
