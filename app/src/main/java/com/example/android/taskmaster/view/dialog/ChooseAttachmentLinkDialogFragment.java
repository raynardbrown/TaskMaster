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
import com.example.android.taskmaster.databinding.DialogAttachALinkBinding;
import com.example.android.taskmaster.utils.TaskMasterConstants;

public class ChooseAttachmentLinkDialogFragment extends DialogFragment
{
  private DialogAttachALinkBinding binding;
  private IChooseAttachmentDialogListener listener;

  @Override
  public void onAttach(Context context)
  {
    super.onAttach(context);

    try
    {
      listener = (IChooseAttachmentDialogListener)context;
    }
    catch(ClassCastException e)
    {
      throw new ClassCastException(context.toString()  + " must implement IChooseAttachmentDialogListener");
    }
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    LayoutInflater inflater = getActivity().getLayoutInflater();
    binding = DataBindingUtil.inflate(inflater, R.layout.dialog_attach_a_link, null, false);

    builder.setView(binding.getRoot())
            .setTitle(R.string.card_detail_activity_dialog_attach_a_link_title_string)
            .setPositiveButton(R.string.card_detail_activity_dialog_attach_a_link_ok_button_string, new DialogInterface.OnClickListener()
            {
              @Override
              public void onClick(DialogInterface dialog, int which)
              {
                String optionalString = "";
                int optionalType = 0;

                // Check to make sure the edit text length is greater than 0. Believe it or not the edit text
                // function getText().toString() can actually return the hint?!?!
                if(binding.etAttachLinkOptional.length() > 0)
                {
                  optionalString = binding.etAttachLinkOptional.getText().toString();
                  optionalType = TaskMasterConstants.ATTACHMENT_TYPE_TEXT_URL;
                }

                listener.onNewAttachment(binding.etAttachLink.getText().toString(),
                        TaskMasterConstants.ATTACHMENT_TYPE_TEXT_URL,
                        optionalString,
                        optionalType);
              }
            })
            .setNegativeButton(R.string.card_detail_activity_dialog_attach_a_link_cancel_button_string, new DialogInterface.OnClickListener()
            {
              @Override
              public void onClick(DialogInterface dialog, int which)
              {
                ChooseAttachmentLinkDialogFragment.this.getDialog().cancel();
              }
            });

    final AlertDialog alertDialog = builder.create();

    alertDialog.setOnShowListener(new DialogInterface.OnShowListener()
    {
      @Override
      public void onShow(DialogInterface dialog)
      {
        // Initially disable the OK card button
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
      }
    });

    setupTextChangeListener(alertDialog);

    return alertDialog;
  }

  private void setupTextChangeListener(AlertDialog dialog)
  {
    binding.etAttachLink.addTextChangedListener(new AttachmentLinkTextWatcher(dialog));
  }

  class AttachmentLinkTextWatcher implements TextWatcher
  {
    private AlertDialog alertDialog;

    AttachmentLinkTextWatcher(AlertDialog alertDialog)
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
