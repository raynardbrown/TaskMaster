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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.AttachmentItemBinding;
import com.example.android.taskmaster.db.FirebaseRealtimeDbProvider;
import com.example.android.taskmaster.model.AttachmentCreationInfo;
import com.example.android.taskmaster.model.AttachmentExtraDataModel;
import com.example.android.taskmaster.model.AttachmentModel;
import com.example.android.taskmaster.utils.TaskMasterConstants;
import com.example.android.taskmaster.utils.TaskMasterUtils;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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

        Context context = imageView.getContext();

        int targetWidth = TaskMasterUtils.dpToPixel(context,
                context.getResources().getDimensionPixelSize(R.dimen.image_attachment_default_width_dp));

        int targetHeight = TaskMasterUtils.dpToPixel(context,
                context.getResources().getDimensionPixelSize(R.dimen.image_attachment_default_height_dp));

        Picasso.with(context)
                .load(attachmentCreationInfo.getAttachmentPath())
                .resize(targetWidth, targetHeight)
                .onlyScaleDown()
                .into(imageView, new AttachmentLoadedCallback(holder, attachmentCreationInfo));
      }
      else
      {
        // we already have data
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

      String attachmentData = attachmentCreationInfo.getAttachmentModel().getAttachmentData();

      if(attachmentData.isEmpty())
      {
        if(TextUtils.isEmpty(attachmentCreationInfo.getAttachmentExtraPath()))
        {
          itemBinding.tvAttachmentLinkType.setText(attachmentCreationInfo.getAttachmentPath());
        }
        else
        {
          itemBinding.tvAttachmentLinkType.setText(attachmentCreationInfo.getAttachmentExtraPath());
        }

        String base64AttachmentLinkData = Base64.encodeToString(attachmentCreationInfo.getAttachmentPath().getBytes(),
                Base64.NO_WRAP | Base64.URL_SAFE);

        attachmentCreationInfo.getAttachmentModel().setAttachmentData(base64AttachmentLinkData);


        if(!TextUtils.isEmpty(attachmentCreationInfo.getAttachmentExtraPath()))
        {
          String base64AttachmentLinkExtraData = Base64.encodeToString(attachmentCreationInfo.getAttachmentExtraPath().getBytes(),
                  Base64.NO_WRAP | Base64.URL_SAFE);

          attachmentCreationInfo.getAttachmentExtraDataModel().setAttachmentExtraData(base64AttachmentLinkExtraData);
        }

        // update the database
        Context context = itemBinding.tvAttachmentLinkType.getContext();

        AttachmentModel attachmentModel = attachmentCreationInfo.getAttachmentModel();

        FirebaseRealtimeDbProvider.addAttachment(context,
                attachmentModel,
                new DatabaseReference.CompletionListener()
                {
                  @Override
                  public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                  {
                    if(databaseError == null)
                    {
                      // write success
                      Log.i("CardDetailAttAdp", "wrote link attachment to database");
                    }
                    else
                    {
                      // write failure
                      Log.i("CardDetailAttAdp", "failed to write link attachment to database");
                    }
                  }
                });

        if(!TextUtils.isEmpty(attachmentCreationInfo.getAttachmentExtraPath()))
        {
          AttachmentExtraDataModel attachmentExtraDataModel = attachmentCreationInfo.getAttachmentExtraDataModel();

          FirebaseRealtimeDbProvider.addAttachmentExtraData(context,
                  attachmentModel.getAttachmentId(),
                  attachmentExtraDataModel,
                  new DatabaseReference.CompletionListener()
                  {
                    @Override
                    public void onComplete(DatabaseError databaseError,
                                           DatabaseReference databaseReference)
                    {
                      if(databaseError == null)
                      {
                        // write success
                        Log.i("CardDetailAttAdp", "wrote attachment extra data to database");
                      }
                      else
                      {
                        // write failure
                        Log.i("CardDetailAttAdp", "failed to write attachment extra data to database");
                      }
                    }
                  });
        }
      }
      else
      {
        // we already have data
        String attachmentExtraData = attachmentCreationInfo.getAttachmentExtraDataModel().getAttachmentExtraData();

        if(TextUtils.isEmpty(attachmentExtraData))
        {
          byte[] base64AttachmentData = Base64.decode(attachmentData,
                  Base64.NO_WRAP | Base64.URL_SAFE);

          itemBinding.tvAttachmentLinkType.setText(new String(base64AttachmentData));
        }
        else
        {
          byte[] base64AttachmentExtraData = Base64.decode(attachmentExtraData,
                  Base64.NO_WRAP | Base64.URL_SAFE);

          itemBinding.tvAttachmentLinkType.setText(new String(base64AttachmentExtraData));
        }
      }

      // fill in the time ui
      fillInTimeUi(itemBinding.tvAttachmentLinkTypeTime, attachmentCreationInfo);

      // set up menu click listener
      itemBinding.menuButtonAttachmentLinkType.setOnClickListener(new AttachmentLinkMenuClickListener(holder));
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

    Format dateFormatter = new SimpleDateFormat(context.getString(R.string.date_time_format_day_of_week_full_month_name_day_year_string), Locale.getDefault());
    String dateFormatted = dateFormatter.format(attachmentCreationInfo.getAttachmentModel().getAttachmentTime());

    Format timeFormatter = new SimpleDateFormat(context.getString(R.string.date_time_format_hour_with_leading_zero_minute_period_string), Locale.getDefault());
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

      AttachmentModel attachmentModel = attachmentCreationInfo.getAttachmentModel();
      attachmentModel.setAttachmentData(base64AttachmentData);

      // Notify the card activity so that the database can be updated
      attachmentListener.onImageAttachmentLoaded(holder.getAdapterPosition(), imageView);

      Context context = imageView.getContext();

      // update the database
      FirebaseRealtimeDbProvider.addAttachment(context,
              attachmentModel,
              new DatabaseReference.CompletionListener()
      {
        @Override
        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
        {
          if(databaseError == null)
          {
            // write success
            Log.i("CardDetailAttAdp", "wrote image attachment to database");
          }
          else
          {
            // write failure
            Log.i("CardDetailAttAdp", "failed to write image attachment to database");
          }
        }
      });
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
          Context context = holder.itemBinding.tvAttachmentLinkType.getContext();

          if(TaskMasterUtils.isNetworkAvailable(context))
          {
            attachmentListener.onAttachmentRemoveRequest(holder.getAdapterPosition());
          }
          else
          {
            Toast.makeText(context,
                    context.getString(R.string.error_network_not_available),
                    Toast.LENGTH_LONG).show();
          }

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
      Context context = holder.itemBinding.ivAttachmentImageType.getContext();

      switch(item.getItemId())
      {
        case R.id.action_delete_attachment_image:
        {
          if(TaskMasterUtils.isNetworkAvailable(context))
          {
            attachmentListener.onAttachmentRemoveRequest(holder.getAdapterPosition());
          }
          else
          {
            Toast.makeText(context,
                    context.getString(R.string.error_network_not_available),
                    Toast.LENGTH_LONG).show();
          }

          return true;
        }

        case R.id.action_add_attachment_image_to_toolbar:
        {
          if(TaskMasterUtils.isNetworkAvailable(context))
          {
            attachmentListener.onAttachmentBindRequest(holder.getAdapterPosition(),
                    holder.itemBinding.ivAttachmentImageType);
          }
          else
          {
            Toast.makeText(context,
                    context.getString(R.string.error_network_not_available),
                    Toast.LENGTH_LONG).show();
          }

          return true;
        }

        case R.id.action_remove_attachment_image_from_toolbar:
        {
          if(TaskMasterUtils.isNetworkAvailable(context))
          {
            attachmentListener.onAttachmentUnbindRequest(holder.getAdapterPosition());
          }
          else
          {
            Toast.makeText(context,
                    context.getString(R.string.error_network_not_available),
                    Toast.LENGTH_LONG).show();
          }

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
