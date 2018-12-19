package com.example.android.taskmaster.view.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.DialogAttachmentTypeSelectorBinding;
import com.example.android.taskmaster.utils.TaskMasterConstants;

public class ChooseAttachmentTypeDialogFragment extends DialogFragment
{
  private DialogAttachmentTypeSelectorBinding binding;
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
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    LayoutInflater inflater = getActivity().getLayoutInflater();
    binding = DataBindingUtil.inflate(inflater, R.layout.dialog_attachment_type_selector, null, false);

    setupUi();

    builder.setView(binding.getRoot())
            .setTitle(R.string.card_detail_activity_dialog_attach_type_chooser_title_string);

    return builder.create();
  }

  private void setupUi()
  {
    binding.clAttachTypeFileContainer.setOnClickListener(new FileClickListener());
    binding.clAttachTypeLinkContainer.setOnClickListener(new LinkClickListener());
  }

  class FileClickListener implements View.OnClickListener
  {
    @Override
    public void onClick(View v)
    {
      Intent intent = new Intent(Intent.ACTION_PICK);

      intent.setType(getString(R.string.card_detail_activity_dialog_attach_type_chooser_image_mime_type_string));

      if(intent.resolveActivity(getActivity().getPackageManager()) != null)
      {
        // safe to call now
        startActivityForResult(Intent.createChooser(intent, getString(R.string.card_detail_activity_dialog_attach_type_chooser_image_picker_title_string)),
                TaskMasterConstants.IMAGE_REQUEST_CODE);
      }
    }
  }

  class LinkClickListener implements View.OnClickListener
  {
    @Override
    public void onClick(View v)
    {
      ChooseAttachmentLinkDialogFragment dialogFragment = new ChooseAttachmentLinkDialogFragment();

      dialogFragment.show(getActivity().getSupportFragmentManager(),
              getString(R.string.card_detail_activity_dialog_attach_a_link_tag_string));

      dismiss();
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent resultData)
  {
    if(requestCode == TaskMasterConstants.IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK)
    {
      Uri uri;
      if(resultData != null)
      {
        // The URI of the user selected image
        uri = resultData.getData();
        if(uri != null)
        {
          listener.onNewAttachment(uri.toString(),            // path to the image
                  TaskMasterConstants.ATTACHMENT_TYPE_IMAGE,  // image attachment
                  "",                                         // images don't have extras
                  0);                                         // images don't have extras

          // Got the attachment URI we don't need this dialog anymore
          dismiss();
        }
      }
    }
  }
}
