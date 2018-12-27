package com.example.android.taskmaster.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.AttachmentItemBinding;
import com.example.android.taskmaster.model.AttachmentCreationInfo;
import com.example.android.taskmaster.utils.TaskMasterConstants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
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

      String attachmentData = attachmentCreationInfo.getAttachmentModel().getAttachmentData();

      if(attachmentData.isEmpty())
      {
        // load the image
        final ImageView imageView = itemBinding.ivAttachmentImageType;

        WindowManager windowManager = (WindowManager)imageView.getContext().getSystemService(Context.WINDOW_SERVICE);

        if(windowManager != null)
        {
          Display display = windowManager.getDefaultDisplay();
          DisplayMetrics outMetrics = new DisplayMetrics();
          display.getMetrics(outMetrics);

          float density = imageView.getResources().getDisplayMetrics().density;
          float dpWidth = outMetrics.widthPixels / density;

          Picasso.with(imageView.getContext())
                  .load(attachmentCreationInfo.getAttachmentPath())
                  .resize(dpToPixel(imageView.getContext(), dpWidth), dpToPixel(imageView.getContext(),
                          300)) // TODO: Don't use this hardcoded value, add it to dimens.xml and pull it from there.
                  .onlyScaleDown()
                  .into(imageView, new AttachmentLoadedCallback(holder, attachmentCreationInfo));
        }
      }
      else
      {
        // TODO: fetch the attachment data from the database, DO NOT USE ATTACHMENT DATA!!! The attachment data could be too large to send throughout the app (i.e. intents)
        byte[] base64AttachmentData = Base64.decode(attachmentData,
                Base64.NO_WRAP);

        final ImageView imageView = itemBinding.ivAttachmentImageType;

        Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(base64AttachmentData));

        imageView.setImageBitmap(bitmap);

        attachmentListener.onImageAttachmentLoaded(holder.getAdapterPosition(), imageView);
      }

      // fill in the time ui
      fillInTimeUi(itemBinding.tvAttachmentImageTypeTime, attachmentCreationInfo);

      // set up menu click listener
      itemBinding.menuButtonAttachmentImageType.setOnClickListener(new AttachmentImageMenuClickListener(holder));
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

      // set up menu click listener
      itemBinding.menuButtonAttachmentLinkType.setOnClickListener(new AttachmentLinkMenuClickListener(holder));

      // TODO: Notify the card activity so that the database can be updated
    }
  }

  // TODO: Move this to utils
  public int dpToPixel(Context context, float dp)
  {
    float density = context.getResources().getDisplayMetrics().density;

    return (int)(dp * density);
  }

  @Override
  public int getItemCount()
  {
    return attachmentList.size();
  }

  private void fillInTimeUi(TextView textView, AttachmentCreationInfo attachmentCreationInfo)
  {
    Context context = textView.getContext();

    Format dateFormatter = new SimpleDateFormat(context.getString(R.string.date_time_format_day_of_week_full_month_name_day_year_string));
    String dateFormatted = dateFormatter.format(attachmentCreationInfo.getAttachmentModel().getAttachmentTime());

    Format timeFormatter = new SimpleDateFormat(context.getString(R.string.date_time_format_hour_with_leading_zero_minute_period_string));
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

  class AttachmentLinkMenuClickListener implements View.OnClickListener
  {
    private CardDetailAttachmentAdapterViewHolder holder;

    AttachmentLinkMenuClickListener(CardDetailAttachmentAdapterViewHolder holder)
    {
      this.holder = holder;
    }

    @Override
    public void onClick(View v)
    {
      PopupMenu popup = new PopupMenu(v.getContext(), v);
      popup.setOnMenuItemClickListener(new AttachmentLinkMenuItemClickListener(holder));
      MenuInflater inflater = popup.getMenuInflater();
      inflater.inflate(R.menu.menu_attachment_link, popup.getMenu());
      popup.show();
    }
  }

  class AttachmentLinkMenuItemClickListener implements PopupMenu.OnMenuItemClickListener
  {
    private CardDetailAttachmentAdapterViewHolder holder;

    AttachmentLinkMenuItemClickListener(CardDetailAttachmentAdapterViewHolder holder)
    {
      this.holder = holder;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
      switch(item.getItemId())
      {
        case R.id.action_delete_link_attachment:
        {
          attachmentListener.onAttachmentRemoveRequest(holder.getAdapterPosition());

          return true;
        }

        default:
        {
          return false;
        }
      }
    }
  }

  class AttachmentImageMenuClickListener implements View.OnClickListener
  {
    private CardDetailAttachmentAdapterViewHolder holder;

    AttachmentImageMenuClickListener(CardDetailAttachmentAdapterViewHolder holder)
    {
      this.holder = holder;
    }

    @Override
    public void onClick(View v)
    {
      PopupMenu popup = new PopupMenu(v.getContext(), v);
      popup.setOnMenuItemClickListener(new AttachmentImageMenuItemClickListener(holder));
      MenuInflater inflater = popup.getMenuInflater();
      inflater.inflate(R.menu.menu_attachment_image, popup.getMenu());

      if(attachmentList.get(holder.getAdapterPosition()).getAttachmentModel().isBound())
      {
        popup.getMenu().findItem(R.id.action_remove_attachment_image_from_toolbar).setVisible(true);
        popup.getMenu().findItem(R.id.action_add_attachment_image_to_toolbar).setVisible(false);
      }
      else
      {
        popup.getMenu().findItem(R.id.action_add_attachment_image_to_toolbar).setVisible(true);
        popup.getMenu().findItem(R.id.action_remove_attachment_image_from_toolbar).setVisible(false);
      }

      popup.show();
    }
  }

  class AttachmentImageMenuItemClickListener implements PopupMenu.OnMenuItemClickListener
  {
    private CardDetailAttachmentAdapterViewHolder holder;

    AttachmentImageMenuItemClickListener(CardDetailAttachmentAdapterViewHolder holder)
    {
      this.holder = holder;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
      switch(item.getItemId())
      {
        case R.id.action_delete_attachment_image:
        {
          attachmentListener.onAttachmentRemoveRequest(holder.getAdapterPosition());

          return true;
        }

        case R.id.action_add_attachment_image_to_toolbar:
        {
          attachmentListener.onAttachmentBindRequest(holder.getAdapterPosition(),
                  holder.itemBinding.ivAttachmentImageType);
          return true;
        }

        case R.id.action_remove_attachment_image_from_toolbar:
        {
          attachmentListener.onAttachmentUnbindRequest(holder.getAdapterPosition());
          return true;
        }

        default:
        {
          return false;
        }
      }
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
