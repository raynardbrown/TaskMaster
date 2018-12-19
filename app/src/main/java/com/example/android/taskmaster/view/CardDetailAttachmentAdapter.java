package com.example.android.taskmaster.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.AttachmentItemBinding;
import com.example.android.taskmaster.model.AttachmentCreationInfo;
import com.example.android.taskmaster.utils.TaskMasterConstants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;

public class CardDetailAttachmentAdapter extends RecyclerView.Adapter<CardDetailAttachmentAdapter.CardDetailAttachmentAdapterViewHolder>
{
  private List<AttachmentCreationInfo> attachmentList;
  private IAttachmentListener attachmentListener;

  CardDetailAttachmentAdapter(List<AttachmentCreationInfo> attachmentList,
                              IAttachmentListener attachmentListener)
  {
    this.attachmentList = attachmentList;
    this.attachmentListener = attachmentListener;
  }

  @Override
  public CardDetailAttachmentAdapterViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType)
  {
    Context context = parent.getContext();
    int layoutIdForListItem = R.layout.attachment_item;
    LayoutInflater inflater = LayoutInflater.from(context);
    final boolean shouldAttachToParentImmediately = false;

    AttachmentItemBinding itemBinding = DataBindingUtil.inflate(inflater,
            layoutIdForListItem,
            parent,
            shouldAttachToParentImmediately);

    return new CardDetailAttachmentAdapterViewHolder(itemBinding);
  }

  @Override
  public void onBindViewHolder(CardDetailAttachmentAdapterViewHolder holder, int position)
  {
    AttachmentItemBinding itemBinding = holder.itemBinding;

    final AttachmentCreationInfo attachmentCreationInfo = attachmentList.get(position);

    if(attachmentCreationInfo.getAttachmentType() == TaskMasterConstants.ATTACHMENT_TYPE_IMAGE)
    {
      // show image layout, hide text layout
      itemBinding.clAttachmentImageTypeContainer.setVisibility(View.VISIBLE);
      itemBinding.clAttachmentLinkTypeContainer.setVisibility(View.GONE);

      // bind the image layout data, we need to check if the data is empty and if it is grab the data
      // from the attachment path, otherwise just use the data

      // load the image
      final ImageView imageView = itemBinding.ivAttachmentImageType;

      Picasso.with(imageView.getContext())
              .load(attachmentCreationInfo.getAttachmentPath())
              .into(imageView, new AttachmentLoadedCallback(holder, attachmentCreationInfo));

      // fill in the time ui
      fillInTimeUi(itemBinding.tvAttachmentImageTypeTime, attachmentCreationInfo);

      // TODO: set up menu click listener
    }
    else if(attachmentCreationInfo.getAttachmentType() == TaskMasterConstants.ATTACHMENT_TYPE_TEXT_URL)
    {
      // Do the url

      // show text url layout, hide the image layout
      itemBinding.clAttachmentLinkTypeContainer.setVisibility(View.VISIBLE);
      itemBinding.clAttachmentImageTypeContainer.setVisibility(View.GONE);

      if(TextUtils.isEmpty(attachmentCreationInfo.getAttachmentExtraPath()))
      {
        itemBinding.tvAttachmentLinkType.setText(attachmentCreationInfo.getAttachmentPath());
      }
      else
      {
        itemBinding.tvAttachmentLinkType.setText(attachmentCreationInfo.getAttachmentExtraPath());
      }

      // fill in the time ui
      fillInTimeUi(itemBinding.tvAttachmentLinkTypeTime, attachmentCreationInfo);

      String base64AttachmentLinkData = Base64.encodeToString(attachmentCreationInfo.getAttachmentPath().getBytes(),
              Base64.NO_WRAP | Base64.URL_SAFE);

      attachmentCreationInfo.getAttachmentModel().setAttachmentData(base64AttachmentLinkData);

      if(!TextUtils.isEmpty(attachmentCreationInfo.getAttachmentExtraPath()))
      {
        String base64AttachmentLinkExtraData = Base64.encodeToString(attachmentCreationInfo.getAttachmentExtraPath().getBytes(),
                Base64.NO_WRAP | Base64.URL_SAFE);

        attachmentCreationInfo.getAttachmentExtraDataModel().setAttachmentExtraData(base64AttachmentLinkExtraData);
      }

      // TODO: Notify the card activity so that the database can be updated

      // TODO: set up menu click listener
    }
  }

  @Override
  public int getItemCount()
  {
    return attachmentList.size();
  }

  private void fillInTimeUi(TextView textView, AttachmentCreationInfo attachmentCreationInfo)
  {
    Context context = textView.getContext();

    Format dateFormatter = new SimpleDateFormat(context.getString(R.string.date_time_format_day_of_week_month_day_year_string));
    String dateFormatted = dateFormatter.format(attachmentCreationInfo.getAttachmentModel().getAttachmentTime());

    Format timeFormatter = new SimpleDateFormat(context.getString(R.string.date_time_format_hour_minute_period_string));
    String timeFormatted = timeFormatter.format(attachmentCreationInfo.getAttachmentModel().getAttachmentTime());

    String dateTimeString = String.format(textView.getResources().getString(R.string.card_detail_activity_attachment_time_format_string),
            dateFormatted,
            timeFormatted);

    textView.setText(dateTimeString);
  }

  class AttachmentLoadedCallback implements Callback
  {
    private CardDetailAttachmentAdapterViewHolder holder;
    private AttachmentCreationInfo attachmentCreationInfo;

    AttachmentLoadedCallback(CardDetailAttachmentAdapterViewHolder holder,
                             AttachmentCreationInfo attachmentCreationInfo)
    {
      this.holder = holder;
      this.attachmentCreationInfo = attachmentCreationInfo;
    }

    @Override
    public void onSuccess()
    {
      ImageView imageView = holder.itemBinding.ivAttachmentImageType;

      // Save the image data once it is loaded
      Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

      bitmap.compress(Bitmap.CompressFormat.PNG, // use png so we don't lose quality
              100,
              byteArrayOutputStream);

      String base64AttachmentData = Base64.encodeToString(byteArrayOutputStream.toByteArray(),
              Base64.NO_WRAP);

      attachmentCreationInfo.getAttachmentModel().setAttachmentData(base64AttachmentData);

      // Notify the card activity so that the database can be updated
      attachmentListener.onImageAttachmentLoaded(holder.getAdapterPosition(), imageView);
    }

    @Override
    public void onError()
    {
      // TODO: Handle error, maybe putting in a placeholder
    }
  }

  class CardDetailAttachmentAdapterViewHolder extends RecyclerView.ViewHolder
  {
    private AttachmentItemBinding itemBinding;

    CardDetailAttachmentAdapterViewHolder(AttachmentItemBinding itemBinding)
    {
      super(itemBinding.getRoot());

      this.itemBinding = itemBinding;
    }
  }
}
