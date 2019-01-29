package com.example.android.taskmaster.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.ActivityCardDetailBinding;
import com.example.android.taskmaster.db.FirebaseRealtimeDbProvider;
import com.example.android.taskmaster.model.AttachmentCreationInfo;
import com.example.android.taskmaster.model.AttachmentExtraDataModel;
import com.example.android.taskmaster.model.AttachmentModel;
import com.example.android.taskmaster.model.ChecklistModel;
import com.example.android.taskmaster.model.ChecklistItemModel;
import com.example.android.taskmaster.model.ChecklistModelContainer;
import com.example.android.taskmaster.model.DueDateModel;
import com.example.android.taskmaster.model.TaskGroupModel;
import com.example.android.taskmaster.model.TaskListCardModel;
import com.example.android.taskmaster.utils.TaskMasterConstants;
import com.example.android.taskmaster.utils.TaskMasterUtils;
import com.example.android.taskmaster.view.dialog.ChooseAttachmentTypeDialogFragment;
import com.example.android.taskmaster.view.dialog.DueDateDialogFragment;
import com.example.android.taskmaster.view.dialog.IChooseAttachmentDialogListener;
import com.example.android.taskmaster.view.dialog.IDeleteChecklistListener;
import com.example.android.taskmaster.view.dialog.IDueDateDialogCancelListener;
import com.example.android.taskmaster.view.dialog.IDueDateDialogListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class CardDetailActivity extends AppCompatActivity implements IDueDateDialogListener,
                                                                     IDueDateDialogCancelListener,
                                                                     IDeleteChecklistListener,
                                                                     IChecklistItemClickListener,
                                                                     IChooseAttachmentDialogListener,
                                                                     IAttachmentListener,
                                                                     IAttachmentFetchListener
{
  private ActivityCardDetailBinding binding;

  private TaskListCardModel taskListCardModel;

  private TaskGroupModel taskGroupModel;

  private String taskListTitle;

  private DueDateModel dueDateModel;

  private boolean enableEditCommitMenu;

  private Drawable editTextDetailDescriptionBackgroundSave;
  private Drawable editTextToolbarTitleBackgroundSave;

  private List<ChecklistModelContainer> checklistList;
  private CardDetailChecklistAdapter checklistAdapter;

  private String commitActionAppBarTitle;

  private IEditCompleteListener editCompleteListener;

  private List<AttachmentCreationInfo> attachmentList;
  private CardDetailAttachmentAdapter attachmentAdapter;

  private int currentDueDateColorResId;

  private int indexOfAttachmentImageInToolbar;

  private boolean toolbarCollapsed;

  private Drawable defaultCollapsingToolbarScrim;

  private DueDateCheckListener dueDateCheckListener;
  private DueDateCheckChangeListener dueDateCheckChangeListener;

  private boolean lifecycleEventWithAttachment;
  private NewAttachmentState newAttachmentState;

  private List<TaskGroupModel> taskGroupModelList;

  public static Intent getStartIntent(Context context,
                                      TaskGroupModel taskGroupModel,
                                      String taskListTitle,
                                      TaskListCardModel taskListCardModel,
                                      List<TaskGroupModel> taskGroupModelList)
  {
    Intent intent = new Intent(context, CardDetailActivity.class);
    intent.putExtra(context.getString(R.string.task_group_model_object_key), taskGroupModel);
    intent.putExtra(context.getString(R.string.task_list_title_key), taskListTitle);
    intent.putExtra(context.getString(R.string.task_list_card_model_object_key), taskListCardModel);

    // need for creating the back stack when this activity is launched from the widget
    intent.putExtra(context.getString(R.string.task_group_model_object_list_key), new ArrayList<>(taskGroupModelList));

    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_card_detail);

    if(savedInstanceState == null)
    {
      Intent intent = getIntent();

      taskListCardModel = intent.getParcelableExtra(getString(R.string.task_list_card_model_object_key));

      taskGroupModel = intent.getParcelableExtra(getString(R.string.task_group_model_object_key));

      taskListTitle = intent.getStringExtra(getString(R.string.task_list_title_key));

      taskGroupModelList = intent.getParcelableArrayListExtra(getString(R.string.task_group_model_object_list_key));

      checklistList = new ArrayList<>();

      attachmentList = new ArrayList<>();

      enableEditCommitMenu = false;

      indexOfAttachmentImageInToolbar = -1;

      lifecycleEventWithAttachment = false;

      // save the task group model to the shared preferences in case we were created via the app
      // widget
      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
      SharedPreferences.Editor editor = sharedPreferences.edit();

      editor.putString(getString(R.string.task_group_id_key), taskGroupModel.getId());
      editor.putString(getString(R.string.task_group_title_key), taskGroupModel.getTitle());
      editor.putInt(getString(R.string.task_group_color_key), taskGroupModel.getColorKey());
      editor.apply();

      postUiInitialization();

      fetchRemoteData();
    }
    else
    {
      taskListCardModel = savedInstanceState.getParcelable(getString(R.string.task_list_card_model_object_key));

      taskGroupModel = savedInstanceState.getParcelable(getString(R.string.task_group_model_object_key));

      taskGroupModelList = savedInstanceState.getParcelableArrayList(getString(R.string.task_group_model_object_list_key));

      taskListTitle = savedInstanceState.getString(getString(R.string.task_list_title_key));

      dueDateModel = savedInstanceState.getParcelable(getString(R.string.due_date_model_object_key));

      checklistList = savedInstanceState.getParcelableArrayList(getString(R.string.check_list_container_list_object_key));

      attachmentList = new ArrayList<>();

      indexOfAttachmentImageInToolbar = savedInstanceState.getInt(getString(R.string.attachment_image_toolbar_index_key));

      lifecycleEventWithAttachment = savedInstanceState.getBoolean(getString(R.string.lifecycle_event_with_attachment_key));

      postUiInitialization();
    }
  }

  @Override
  protected void onResume()
  {
    super.onResume();

    if(lifecycleEventWithAttachment && newAttachmentState != null)
    {
      lifecycleEventWithAttachment = false;

      // fetch attachment data from database, call delayed onNewAttachment
      FirebaseRealtimeDbProvider.startAttachmentFetch(CardDetailActivity.this,
              taskListCardModel.getCardId(),
              attachmentList,
              attachmentAdapter,
              this,
              this);  // onNewAttachment callback
    }
    else
    {
      lifecycleEventWithAttachment = false;

      // fetch attachment data from database, do not call onNewAttachment
      FirebaseRealtimeDbProvider.startAttachmentFetch(CardDetailActivity.this,
              taskListCardModel.getCardId(),
              attachmentList,
              attachmentAdapter,
              this,
              null);  // no callback
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState)
  {
    super.onSaveInstanceState(outState);

    outState.putParcelable(getString(R.string.task_list_card_model_object_key), taskListCardModel);

    outState.putParcelable(getString(R.string.task_group_model_object_key), taskGroupModel);

    outState.putParcelableArrayList(getString(R.string.task_group_model_object_list_key), new ArrayList<Parcelable>(taskGroupModelList));

    outState.putString(getString(R.string.task_list_title_key), taskListTitle);

    if(dueDateModel != null)
    {
      outState.putParcelable(getString(R.string.due_date_model_object_key), dueDateModel);
    }

    outState.putParcelableArrayList(getString(R.string.check_list_container_list_object_key), new ArrayList<>(checklistList));

    // We must clear the attachment data since the Android platform has strict limits on the amount
    // of data that can be parceled. Failure to do so will lead to TransactionTooLargeException.
    attachmentList.clear();

    outState.putInt(getString(R.string.attachment_image_toolbar_index_key), indexOfAttachmentImageInToolbar);

    outState.putBoolean(getString(R.string.lifecycle_event_with_attachment_key), lifecycleEventWithAttachment);

    // TODO: save edit commit state
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_card_detail_activity, menu);

    MenuItem editCommitMenuItem = menu.findItem(R.id.action_edit_description_commit_menu_item);

    if(enableEditCommitMenu)
    {
      editCommitMenuItem.setVisible(true);
    }
    else
    {
      editCommitMenuItem.setVisible(false);
    }

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch(item.getItemId())
    {
      case R.id.action_due_date:
      {
        onDueDateClick();
        return true;
      }

      case R.id.action_checklist:
      {
        onCheckMenuClick();
        return true;
      }

      case R.id.action_attachment:
      {
        onAttachmentMenuClick();
        return true;
      }

      case R.id.action_edit_description_commit_menu_item:
      {
        dismissKeyboard();

        editCompleteListener.onEditComplete();

        return true;
      }

      default:
      {
        return super.onOptionsItemSelected(item);
      }
    }
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu)
  {
    setCommitMenuButtonEnabled(menu, enableEditCommitMenu);

    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public void onDueDateSelectionClick(String date, String time)
  {
    if(TaskMasterUtils.isNetworkAvailable(CardDetailActivity.this))
    {
      if(date.isEmpty() || time.isEmpty())
      {
        // hide the due date text view row
        binding.cbCardDetailActivityDueDate.setOnTouchListener(null);
        binding.cbCardDetailActivityDueDate.setOnCheckedChangeListener(null);
        dueDateCheckListener = null;
        dueDateCheckChangeListener = null;

        binding.linearCardDetailActivityDueDateRow.setVisibility(View.GONE);

        if(dueDateModel != null)
        {
          // Update the database by removing the due associated with this card
          FirebaseDatabase database = FirebaseDatabase.getInstance();
          DatabaseReference rootDatabaseReference = database.getReference();

          rootDatabaseReference.child(getString(R.string.db_due_date_object))
                  .child(taskListCardModel.getCardId())
                  .removeValue(new DatabaseReference.CompletionListener()
                  {
                    @Override
                    public void onComplete(DatabaseError databaseError,
                                           DatabaseReference databaseReference)
                    {
                      if(databaseError == null)
                      {
                        // write success
                        Log.i("CardDetailAct", "removed due date from database");

                        // update the widget
                        TaskMasterUtils.startWidgetDueDateDelete(CardDetailActivity.this, taskListCardModel.getCardId());
                      }
                      else
                      {
                        // write failure
                        Log.i("CardDetailAct", "failed to remove due date from database");
                      }
                    }
                  });

          dueDateModel = null;

          TaskMasterUtils.setAppStateDirty(CardDetailActivity.this);
        }
      }
      else
      {
        // Get a date object from the string results
        Date dueDateObject = new SimpleDateFormat(getString(R.string.date_time_format_full_month_name_day_year_hour_with_leading_zero_minute_period_string),
                Locale.getDefault())
                .parse(date + " " + time, new ParsePosition(0));

        if(dueDateObject != null)
        {
          if(dueDateModel == null)
          {
            // initialize the model
            dueDateModel = new DueDateModel(taskListCardModel.getCardId(), 0, false);
          }

          dueDateModel.setDueDate(dueDateObject.getTime());

          updateDueDateView(dueDateObject);

          // Update the database
          FirebaseDatabase database = FirebaseDatabase.getInstance();
          DatabaseReference rootDatabaseReference = database.getReference();

          String dueDateRoot = String.format("/%s/%s/", getString(R.string.db_due_date_object),
                  taskListCardModel.getCardId());

          Map<String, Object> childUpdates = new HashMap<>();

          childUpdates.put(dueDateRoot + getString(R.string.db_due_date_due_date_key),
                  dueDateModel.getDueDate());

          childUpdates.put(dueDateRoot + getString(R.string.db_due_date_completed_key),
                  dueDateModel.isCompleted());

          rootDatabaseReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener()
          {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
            {
              if(databaseError == null)
              {
                // write success
                Log.i("CardDetailAct", "wrote due date to database");

                TaskMasterUtils.startWidgetDueDateUpdate(CardDetailActivity.this,
                        taskListCardModel.getCardId(),
                        taskListCardModel.getCardTitle(),
                        taskListCardModel.getCardDetailedDescription(),
                        taskListCardModel.getCardIndex(),
                        taskListCardModel.getTaskIndex(),
                        taskListCardModel.getTaskListId(),
                        taskListTitle,
                        taskGroupModel.getId(),
                        taskGroupModel.getTitle(),
                        taskGroupModel.getColorKey(),
                        dueDateModel,
                        taskGroupModelList);
              }
              else
              {
                // write failure
                Log.i("CardDetailAct", "failed to write due date to database");
              }
            }
          });

          Log.i("CardDetailAct", "setting app state dirty");
          TaskMasterUtils.setAppStateDirty(CardDetailActivity.this);
        }
        else
        {
          // Due date object is null.
          //
          // Should never happen since we are getting the due date and time from Android Date/Time
          // pickers.
          Toast.makeText(this, "Failed to parse date and/or time", Toast.LENGTH_LONG).show();
        }
      }
    }
    else
    {
      Toast.makeText(CardDetailActivity.this,
              getString(R.string.error_network_not_available),
              Toast.LENGTH_LONG).show();
    }
  }

  @Override
  public void onDueDateDialogCancel(int colorResId)
  {
    Drawable drawable = TaskMasterUtils.setDrawableResColorRes(this,
            R.drawable.ic_baseline_schedule_24px,
            colorResId);

    binding.ivCardDetailActivityDueDate.setImageDrawable(drawable);
  }

  @Override
  public void onChecklistDelete(int checklistIndex)
  {
    if(TaskMasterUtils.isNetworkAvailable(CardDetailActivity.this))
    {
      // Remove the check list from the adapter
      ChecklistModelContainer removedChecklistModelContainer = checklistList.remove(checklistIndex);

      // Update all of the checklist indexes from the specified index until the end of the list
      for(int i = checklistIndex; i < checklistList.size(); ++i)
      {
        ChecklistModel checklistModel = checklistList.get(i).getChecklistModel();
        checklistModel.setChecklistIndex(i);

        // update the database to reflect the changes
        FirebaseRealtimeDbProvider.updateChecklistWithIndex(this,
                checklistModel.getChecklistId(),
                checklistModel.getChecklistIndex(),
                taskListCardModel.getCardId(),
                new DatabaseReference.CompletionListener()
                {
                  @Override
                  public void onComplete(DatabaseError databaseError,
                                         DatabaseReference databaseReference)
                  {
                    if(databaseError == null)
                    {
                      // write success
                      Log.i("CardDetailAct", "updated the checklist index in the database");
                    }
                    else
                    {
                      // write failure
                      Log.i("CardDetailAct", "failed to update the checklist index in the database");
                    }
                  }
                });
      }

      // Remove the child items of the removed checklist
      removedChecklistModelContainer.getChecklistItemModelList().clear();

      FirebaseDatabase database = FirebaseDatabase.getInstance();
      DatabaseReference rootDatabaseReference = database.getReference();

      final String removedChecklistId = removedChecklistModelContainer.getChecklistModel().getChecklistId();

      rootDatabaseReference.child(getString(R.string.db_checklist_checklist_item_key))
              .child(removedChecklistId)
              .addListenerForSingleValueEvent(new ValueEventListener()
              {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                  for(DataSnapshot checklistItemSnapshot : dataSnapshot.getChildren())
                  {
                    String checklistItemId = checklistItemSnapshot.getKey();

                    // Remove checklist item from checklist
                    FirebaseRealtimeDbProvider.removeChecklistItemFromChecklist(CardDetailActivity.this,
                            checklistItemId,
                            removedChecklistId,
                            new DatabaseReference.CompletionListener()
                            {
                              @Override
                              public void onComplete(DatabaseError databaseError,
                                                     DatabaseReference databaseReference)
                              {
                                if(databaseError == null)
                                {
                                  // remove success
                                  Log.i("CardDetailAct", "removed checklist item checklist in the database");
                                }
                                else
                                {
                                  // remove failure
                                  Log.i("CardDetailAct", "failed to remove checklist item checklist in the database");
                                }
                              }
                            });

                    // Remove checklist item from database
                    FirebaseRealtimeDbProvider.removeChecklistItem(CardDetailActivity.this,
                            checklistItemId,
                            new DatabaseReference.CompletionListener()
                            {
                              @Override
                              public void onComplete(DatabaseError databaseError,
                                                     DatabaseReference databaseReference)
                              {
                                if(databaseError == null)
                                {
                                  // remove success
                                  Log.i("CardDetailAct", "removed checklist item from the database");
                                }
                                else
                                {
                                  // remove failure
                                  Log.i("CardDetailAct", "failed to remove checklist item from the database");
                                }
                              }
                            });
                  }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                  Log.i("CardDetailAct", "failed to get checklist item id from the database");
                }
              });

      // Remove the checklist from the task list card
      FirebaseRealtimeDbProvider.removeChecklistFromTaskListCard(CardDetailActivity.this,
              removedChecklistModelContainer.getChecklistModel().getChecklistId(),
              removedChecklistModelContainer.getChecklistModel().getCardId(),
              new DatabaseReference.CompletionListener()
              {
                @Override
                public void onComplete(DatabaseError databaseError,
                                       DatabaseReference databaseReference)
                {
                  if(databaseError == null)
                  {
                    // remove success
                    Log.i("CardDetailAct", "removed checklist from task list card in the database");
                  }
                  else
                  {
                    // remove failure
                    Log.i("CardDetailAct", "failed to remove checklist from task list card in the database");
                  }
                }
              });

      // Remove the checklist from the database
      FirebaseRealtimeDbProvider.removeChecklist(CardDetailActivity.this,
              removedChecklistModelContainer.getChecklistModel().getChecklistId(),
              new DatabaseReference.CompletionListener()
              {
                @Override
                public void onComplete(DatabaseError databaseError,
                                       DatabaseReference databaseReference)
                {
                  if(databaseError == null)
                  {
                    // remove success
                    Log.i("CardDetailAct", "removed checklist from the database");
                  }
                  else
                  {
                    // remove failure
                    Log.i("CardDetailAct", "failed to remove checklist the database");
                  }
                }
              });

      // Notify the adapter
      checklistAdapter.notifyDataSetChanged();

      TaskMasterUtils.setAppStateDirty(CardDetailActivity.this);
    }
    else
    {
      Toast.makeText(CardDetailActivity.this,
              getString(R.string.error_network_not_available),
              Toast.LENGTH_LONG).show();
    }
  }

  @Override
  public void onChecklistItemClick(String actionBarTitle,
                                   boolean showCommitButton,
                                   IEditCompleteListener editCompleteListener)
  {
    if(showCommitButton)
    {
      setUpEditCommitMenu(actionBarTitle, editCompleteListener);
    }
    else
    {
      tearDownEditCommitMenu();
    }
  }

  @Override
  public void onNewAttachment(String attachmentPath,
                              int attachmentType,
                              String attachmentExtraPath,
                              int attachmentExtraType)
  {
    if(TaskMasterUtils.isNetworkAvailable(CardDetailActivity.this))
    {
      if(lifecycleEventWithAttachment)
      {
        // Delay the new attachment until we are done fetching the existing attachments from the
        // database
        newAttachmentState = new NewAttachmentState(attachmentPath,
                attachmentType,
                attachmentExtraPath,
                attachmentExtraType);
      }
      else
      {
        // only image attachments can be bound to the toolbar
        final boolean isBound = (indexOfAttachmentImageInToolbar == -1) &&
                attachmentType == TaskMasterConstants.ATTACHMENT_TYPE_IMAGE;

        AttachmentModel attachmentModel = new AttachmentModel(taskListCardModel.getCardId(),
                UUID.randomUUID().toString(),
                "", // the adapter will fill in the data
                attachmentList.size(),
                Calendar.getInstance().getTime().getTime(),
                attachmentType,
                isBound);

        AttachmentExtraDataModel attachmentExtraDataModel = new AttachmentExtraDataModel(taskListCardModel.getCardId(),
                "", // the adapter will fill in the data
                attachmentList.size(),
                attachmentExtraType);

        AttachmentCreationInfo attachmentCreationInfo = new AttachmentCreationInfo(attachmentModel,
                attachmentExtraDataModel,
                attachmentPath,
                attachmentType,
                attachmentExtraPath,
                attachmentExtraType);

        attachmentList.add(attachmentCreationInfo);

        attachmentAdapter.notifyDataSetChanged();

        // make sure you set the attachment main view visible since there is at least one attachment
        binding.linearCardDetailActivityAttachment.setVisibility(View.VISIBLE);

        TaskMasterUtils.setAppStateDirty(CardDetailActivity.this);
      }
    }
    else
    {
      Toast.makeText(CardDetailActivity.this,
              getString(R.string.error_network_not_available),
              Toast.LENGTH_LONG).show();
    }
  }

  public class NewAttachmentState
  {
    String attachmentPath;
    int attachmentType;
    String attachmentExtraPath;
    int attachmentExtraType;

    NewAttachmentState(String attachmentPath,
                       int attachmentType,
                       String attachmentExtraPath,
                       int attachmentExtraType)
    {

      this.attachmentPath = attachmentPath;
      this.attachmentType = attachmentType;
      this.attachmentExtraPath = attachmentExtraPath;
      this.attachmentExtraType = attachmentExtraType;
    }
  }

  @Override
  public void onNewAttachmentDelayed()
  {
    onNewAttachment(newAttachmentState.attachmentPath,
            newAttachmentState.attachmentType,
            newAttachmentState.attachmentExtraPath,
            newAttachmentState.attachmentExtraType);

    newAttachmentState = null;
  }

  @Override
  public void onImageAttachmentLoaded(int index, ImageView imageView)
  {
    if(attachmentList.get(index).getAttachmentModel().isBound())
    {
      bindAttachmentToToolbar(index, imageView);
    }
  }

  @Override
  public void onAttachmentRemoveRequest(int index)
  {
    if(TaskMasterUtils.isNetworkAvailable(CardDetailActivity.this))
    {
      // Check to see if the index is on the toolbar and remove the image from the toolbar
      if(attachmentList.get(index).getAttachmentModel().isBound())
      {
        onAttachmentUnbindRequest(index);
      }

      AttachmentCreationInfo attachmentCreationInfo = attachmentList.remove(index);

      AttachmentModel removedAttachmentModel = attachmentCreationInfo.getAttachmentModel();

      for(int i = index; i < attachmentList.size(); ++i)
      {
        AttachmentModel attachmentModel = attachmentList.get(i).getAttachmentModel();
        attachmentModel.setAttachmentIndex(i);

        FirebaseRealtimeDbProvider.updateAttachmentWithIndex(CardDetailActivity.this,
                attachmentModel.getAttachmentId(),
                i,
                taskListCardModel.getCardId(),
                new DatabaseReference.CompletionListener()
                {
                  @Override
                  public void onComplete(DatabaseError databaseError,
                                         DatabaseReference databaseReference)
                  {
                    if(databaseError == null)
                    {
                      Log.i("CardDetailAct", "updated attachment index in database");
                    }
                    else
                    {
                      Log.i("CardDetailAct", "failed to update attachment index in database");
                    }
                  }
                });
      }

      // update the database

      FirebaseRealtimeDbProvider.removeAttachmentFromAttachmentBound(CardDetailActivity.this,
              removedAttachmentModel.getAttachmentId(),
              new DatabaseReference.CompletionListener()
              {
                @Override
                public void onComplete(DatabaseError databaseError,
                                       DatabaseReference databaseReference)
                {
                  if(databaseError == null)
                  {
                    Log.i("CardDetailAct", "removed attachment from bound tree in database");
                  }
                  else
                  {
                    Log.i("CardDetailAct", "failed to remove attachment from bound tree in database");
                  }
                }
              });

      FirebaseRealtimeDbProvider.removeAttachmentFromTaskListCard(CardDetailActivity.this,
              removedAttachmentModel.getAttachmentId(),
              taskListCardModel.getCardId(),
              new DatabaseReference.CompletionListener()
              {
                @Override
                public void onComplete(DatabaseError databaseError,
                                       DatabaseReference databaseReference)
                {
                  if(databaseError == null)
                  {
                    Log.i("CardDetailAct", "removed attachment from task list card in database");
                  }
                  else
                  {
                    Log.i("CardDetailAct", "failed to remove attachment from task list card in database");
                  }
                }
              });

      FirebaseRealtimeDbProvider.removeAttachmentExtraData(CardDetailActivity.this,
              removedAttachmentModel.getAttachmentId(),
              new DatabaseReference.CompletionListener()
              {
                @Override
                public void onComplete(DatabaseError databaseError,
                                       DatabaseReference databaseReference)
                {
                  if(databaseError == null)
                  {
                    Log.i("CardDetailAct", "removed attachment extra data from database");
                  }
                  else
                  {
                    Log.i("CardDetailAct", "failed to remove attachment extra data from database");
                  }
                }
              });

      FirebaseRealtimeDbProvider.removeAttachment(CardDetailActivity.this,
              removedAttachmentModel.getAttachmentId(),
              new DatabaseReference.CompletionListener()
              {
                @Override
                public void onComplete(DatabaseError databaseError,
                                       DatabaseReference databaseReference)
                {
                  if(databaseError == null)
                  {
                    Log.i("CardDetailAct", "removed attachment from database");
                  }
                  else
                  {
                    Log.i("CardDetailAct", "failed to remove attachment from database");
                  }
                }
              });

      attachmentAdapter.notifyDataSetChanged();

      if(indexOfAttachmentImageInToolbar != -1 && indexOfAttachmentImageInToolbar > index)
      {
        --indexOfAttachmentImageInToolbar;
      }

      // check to see if there are any attachments left and remove the attachment bar if there are none
      if(attachmentList.size() == 0)
      {
        indexOfAttachmentImageInToolbar = -1;

        binding.linearCardDetailActivityAttachment.setVisibility(View.GONE);
      }

      TaskMasterUtils.setAppStateDirty(CardDetailActivity.this);
    }
    else
    {
      Toast.makeText(CardDetailActivity.this,
              getString(R.string.error_network_not_available),
              Toast.LENGTH_LONG).show();
    }
  }

  @Override
  public void onAttachment()
  {
    binding.linearCardDetailActivityAttachment.setVisibility(View.VISIBLE);
  }

  private void bindAttachmentToToolbar(int index, ImageView imageView)
  {
    indexOfAttachmentImageInToolbar = index;

    // set the attachment image in the toolbar
    Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
    binding.ivCardDetailToolbar.setImageBitmap(bitmap);

    // make sure the toolbar scrim is visible
    binding.viewToolbarScrim.setVisibility(View.VISIBLE);

    // Set the meta bar background to the palette color of the image
    Palette p = Palette.from(bitmap)
            .maximumColorCount(12)
            .generate();

    int metaBarColor = p.getDarkVibrantColor(getResources().getColor(R.color.card_detail_meta_bar_default_color));

    binding.metaBar.setBackgroundColor(metaBarColor);

    // Make sure you save the current color of the collapsing toolbar scrim so that it can be
    // restored once the image is removed
    if(defaultCollapsingToolbarScrim == null)
    {
      defaultCollapsingToolbarScrim = binding.collapsingToolbarLayoutCardDetail.getContentScrim();
    }

    // Need to set the collapsing toolbar the same color as the meta bar
    binding.collapsingToolbarLayoutCardDetail.setContentScrimColor(metaBarColor);

    // Need to set the action bar a darker version of the meta bar
    int statusBarColor = ColorUtils.blendARGB(metaBarColor, Color.BLACK, 0.4f);
    TaskMasterUtils.setStatusBarColorHelper(getWindow(), statusBarColor);
  }

  private void unbindAttachmentFromToolbar()
  {
    binding.ivCardDetailToolbar.setImageBitmap(null);

    binding.viewToolbarScrim.setVisibility(View.INVISIBLE);

    binding.metaBar.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    binding.collapsingToolbarLayoutCardDetail.setContentScrim(defaultCollapsingToolbarScrim);

    TaskMasterUtils.setStatusBarColorHelper(getWindow(), getResources().getColor(R.color.colorPrimaryDark));
  }

  private void setAttachmentBoundProperty(int index, boolean bound)
  {
    // set the bind property of the attachment

    attachmentList.get(index).getAttachmentModel().setBound(bound);

    // notify the adapter
    attachmentAdapter.notifyItemChanged(index);
  }

  @Override
  public void onAttachmentBindRequest(int index, ImageView imageView)
  {
    if(TaskMasterUtils.isNetworkAvailable(CardDetailActivity.this))
    {
      // check to see if there is another attachment already bound, if there is unbind that attachment
      // and bind the attachment at the specified index

      if(indexOfAttachmentImageInToolbar != -1)
      {
        onAttachmentUnbindRequest(indexOfAttachmentImageInToolbar);
      }

      bindAttachmentToToolbar(index, imageView);

      final boolean bound = true;

      setAttachmentBoundProperty(index, bound);

      // update database
      FirebaseRealtimeDbProvider.setAttachmentBound(this,
              attachmentList.get(index).getAttachmentModel().getAttachmentId(),
              bound,
              new DatabaseReference.CompletionListener()
              {
                @Override
                public void onComplete(DatabaseError databaseError,
                                       DatabaseReference databaseReference)
                {
                  if(databaseError == null)
                  {
                    // update success
                    Log.i("CardDetailAct", "set bound property on attachment in database");
                  }
                  else
                  {
                    // update failure
                    Log.i("CardDetailAct", "failed to set bound property on attachment in database");
                  }
                }
              });

      TaskMasterUtils.setAppStateDirty(CardDetailActivity.this);
    }
    else
    {
      Toast.makeText(CardDetailActivity.this,
              getString(R.string.error_network_not_available),
              Toast.LENGTH_LONG).show();
    }
  }

  @Override
  public void onAttachmentUnbindRequest(int index)
  {
    if(TaskMasterUtils.isNetworkAvailable(CardDetailActivity.this))
    {
      unbindAttachmentFromToolbar();

      indexOfAttachmentImageInToolbar = -1;

      final boolean bound = false;

      setAttachmentBoundProperty(index, bound);

      // update database
      FirebaseRealtimeDbProvider.setAttachmentBound(this,
              attachmentList.get(index).getAttachmentModel().getAttachmentId(),
              bound,
              new DatabaseReference.CompletionListener()
              {
                @Override
                public void onComplete(DatabaseError databaseError,
                                       DatabaseReference databaseReference)
                {
                  if(databaseError == null)
                  {
                    // update success
                    Log.i("CardDetailAct", "set bound property on attachment in database");
                  }
                  else
                  {
                    // update failure
                    Log.i("CardDetailAct", "failed to set bound property on attachment in database");
                  }
                }
              });

      TaskMasterUtils.setAppStateDirty(CardDetailActivity.this);
    }
    else
    {
      Toast.makeText(CardDetailActivity.this,
              getString(R.string.error_network_not_available),
              Toast.LENGTH_LONG).show();
    }
  }

  private void setCommitMenuButtonEnabled(Menu menu, boolean enableEditCommitMenu)
  {
    if(enableEditCommitMenu)
    {
      menu.findItem(R.id.action_edit_description_commit_menu_item).setVisible(true);

      // disable all the others
      menu.findItem(R.id.action_due_date).setVisible(false);
      menu.findItem(R.id.action_checklist).setVisible(false);
      menu.findItem(R.id.action_attachment).setVisible(false);

      ActionBar actionBar = getSupportActionBar();

      if(actionBar != null)
      {
        // disable the back button
        actionBar.setDisplayHomeAsUpEnabled(false);

        updateToolbar();
      }
    }
    else
    {
      menu.findItem(R.id.action_edit_description_commit_menu_item).setVisible(false);

      // disable all the others
      menu.findItem(R.id.action_due_date).setVisible(true);
      menu.findItem(R.id.action_checklist).setVisible(true);
      menu.findItem(R.id.action_attachment).setVisible(true);

      ActionBar actionBar = getSupportActionBar();

      if(actionBar != null)
      {
        // restore the title, enable the back button
        actionBar.setDisplayHomeAsUpEnabled(true);

        updateToolbar();
      }
    }
  }

  private void updateDueDateView(Date date)
  {
    // Build the string
    Calendar dueDateAsCalendarObject = Calendar.getInstance();
    dueDateAsCalendarObject.setTime(date);

    // Set up a comparison of the current time against the due date
    int compareDate = Calendar.getInstance().compareTo(dueDateAsCalendarObject);

    // Build formatted date and time strings
    Format dateFormatter = new SimpleDateFormat(getString(R.string.date_time_format_day_of_week_full_month_name_day_year_string),
            Locale.getDefault());

    String dateFormatted = dateFormatter.format(dueDateAsCalendarObject.getTime());

    Format timeFormatter = new SimpleDateFormat(getString(R.string.date_time_format_hour_with_leading_zero_minute_period_string),
            Locale.getDefault());

    String timeFormatted = timeFormatter.format(dueDateAsCalendarObject.getTime());

    // Check the current time against the due date and set the icon color
    Drawable drawable;

    String formattedString;

    if(compareDate <= 0)
    {
      // Not due yet
      formattedString = String.format(getString(R.string.card_detail_activity_not_due_time_format_string),
              dateFormatted,
              timeFormatted);

      // determine how close we are to the due date
      Calendar calendar24HoursFromNow = Calendar.getInstance();
      calendar24HoursFromNow.add(Calendar.HOUR_OF_DAY, 24);

      int compareTime = calendar24HoursFromNow.compareTo(dueDateAsCalendarObject);

      if(compareTime <= 0)
      {
        // we have at least 24 hours left
        currentDueDateColorResId = R.color.due_date_soon;

        if(!binding.cbCardDetailActivityDueDate.isChecked() && !dueDateModel.isCompleted())
        {
          drawable = TaskMasterUtils.setDrawableResColorRes(this,
                  R.drawable.ic_baseline_schedule_24px,
                  R.color.due_date_soon);

          binding.ivCardDetailActivityDueDate.setImageDrawable(drawable);
        }
      }
      else
      {
        // we have less than 24 hours left
        currentDueDateColorResId = R.color.due_date_sooner;

        if(!binding.cbCardDetailActivityDueDate.isChecked() && !dueDateModel.isCompleted())
        {
          drawable = TaskMasterUtils.setDrawableResColorRes(this,
                  R.drawable.ic_baseline_schedule_24px,
                  R.color.due_date_sooner);

          binding.ivCardDetailActivityDueDate.setImageDrawable(drawable);
        }
      }
    }
    else
    {
      // past due
      formattedString = String.format(getString(R.string.card_detail_activity_past_due_time_format_string),
              dateFormatted,
              timeFormatted);

      currentDueDateColorResId = R.color.due_date_past;

      if(!binding.cbCardDetailActivityDueDate.isChecked() && !dueDateModel.isCompleted())
      {
        // set the past due color
        drawable = TaskMasterUtils.setDrawableResColorRes(this,
                R.drawable.ic_baseline_schedule_24px,
                R.color.due_date_past);

        binding.ivCardDetailActivityDueDate.setImageDrawable(drawable);
      }
    }

    if(dueDateCheckChangeListener == null)
    {
      dueDateCheckListener = new DueDateCheckListener();
      binding.cbCardDetailActivityDueDate.setOnTouchListener(dueDateCheckListener);

      dueDateCheckChangeListener = new DueDateCheckChangeListener();
      binding.cbCardDetailActivityDueDate.setOnCheckedChangeListener(dueDateCheckChangeListener);
    }

    binding.linearCardDetailActivityDueDateRow.setVisibility(View.VISIBLE);
    binding.tvCardDetailActivityDueDate.setText(formattedString);

    // set the checked state
    binding.cbCardDetailActivityDueDate.setChecked(dueDateModel.isCompleted());
  }

  private void updateToolbar()
  {
    boolean editMode = !TextUtils.isEmpty(commitActionAppBarTitle);

    // check the collapse state
    if(toolbarCollapsed)
    {
      // The toolbar is collapsed

      // Disable clear the toolbar view title and re-enable the collapsing toolbar title
      if(getSupportActionBar() != null)
      {
        getSupportActionBar().setTitle("");
      }

      binding.collapsingToolbarLayoutCardDetail.setTitleEnabled(true);

      if(editMode)
      {
        // we are in edit mode so set the appropriate title
        binding.collapsingToolbarLayoutCardDetail.setTitle(commitActionAppBarTitle);
      }
      else
      {
        // set the original title
        binding.collapsingToolbarLayoutCardDetail.setTitle(taskListCardModel.getCardTitle());
      }
    }
    else
    {
      // We are no longer collapsed, remove the collapsed toolbar title
      binding.collapsingToolbarLayoutCardDetail.setTitle(" ");

      ActionBar actionBar = getSupportActionBar();

      if(actionBar != null)
      {
        if(editMode)
        {
          // however we are in edit mode, so show the appropriate title
          actionBar.setTitle(commitActionAppBarTitle);
          binding.collapsingToolbarLayoutCardDetail.setTitleEnabled(false);
        }
        else
        {
          actionBar.setTitle("");
          binding.collapsingToolbarLayoutCardDetail.setTitleEnabled(true);
        }
      }
    }
  }

  private void dismissKeyboard()
  {
    InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
    if(inputManager != null)
    {
      // get the view which currently has input focus
      View viewWithFocus = getCurrentFocus();
      if(viewWithFocus != null)
      {
        // hide the keyboard
        inputManager.hideSoftInputFromWindow(viewWithFocus.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
      }
    }
  }

  private void onDueDateClick()
  {
    if(dueDateModel != null)
    {
      // There is a current due date set so we need to save the icon color and restore it if the
      // operation is canceled.
      new DueDateDialogFragment.Builder(this)
              .setDueDate(dueDateModel.getDueDate())
              .setCurrentDueDateDrawableColor(currentDueDateColorResId)
              .build()
              .show(getSupportFragmentManager());
    }
    else
    {
      new DueDateDialogFragment.Builder(this)
              .build()
              .show(getSupportFragmentManager());
    }
  }

  private void onCheckMenuClick()
  {
    if(TaskMasterUtils.isNetworkAvailable(CardDetailActivity.this))
    {
      final boolean collapsed = false;

      ChecklistModel checklistModel = new ChecklistModel(taskListCardModel.getCardId(),
              UUID.randomUUID().toString(),
              getString(R.string.card_detail_activity_default_checklist_name_string),
              checklistList.size(),
              collapsed);

      ChecklistModelContainer container = new ChecklistModelContainer(checklistModel,
              new ArrayList<ChecklistItemModel>());

      checklistList.add(container);

      checklistAdapter.notifyDataSetChanged();

      // update the database too

      FirebaseDatabase database = FirebaseDatabase.getInstance();
      DatabaseReference rootDatabaseReference = database.getReference();

      String checklistModelRoot = String.format("/%s/%s/", getString(R.string.db_checklist_object),
              container.getChecklistModel().getChecklistId());

      String taskListCardChecklistRoot = String.format("/%s/%s/", getString(R.string.db_task_list_card_checklist_key),
              taskListCardModel.getCardId());

      Map<String, Object> childUpdates = new HashMap<>();

      childUpdates.put(checklistModelRoot + getString(R.string.db_checklist_title_key),
              container.getChecklistModel().getChecklistTitle());

      childUpdates.put(checklistModelRoot + getString(R.string.db_checklist_index_key),
              container.getChecklistModel().getChecklistIndex());

      childUpdates.put(checklistModelRoot + getString(R.string.db_checklist_collapsed_key),
              container.getChecklistModel().isCollapsed());

      // link the checklist to this card
      childUpdates.put(taskListCardChecklistRoot + container.getChecklistModel().getChecklistId(),
              container.getChecklistModel().getChecklistIndex());

      rootDatabaseReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener()
      {
        @Override
        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
        {
          if(databaseError == null)
          {
            // write success
            Log.i("TaskGroupAct", "wrote checklist to database");
          }
          else
          {
            // write failure
            Log.i("TaskGroupAct", "failed to write checklist to database");
          }
        }
      });

      TaskMasterUtils.setAppStateDirty(CardDetailActivity.this);
    }
    else
    {
      Toast.makeText(CardDetailActivity.this,
              getString(R.string.error_network_not_available),
              Toast.LENGTH_LONG).show();
    }
  }

  private void onAttachmentMenuClick()
  {
    if(TaskMasterUtils.isNetworkAvailable(CardDetailActivity.this))
    {
      lifecycleEventWithAttachment = true;

      ChooseAttachmentTypeDialogFragment dialogFragment = new ChooseAttachmentTypeDialogFragment();

      dialogFragment.show(getSupportFragmentManager(),
              getString(R.string.card_detail_activity_dialog_attach_type_chooser_tag_string));
    }
    else
    {
      Toast.makeText(CardDetailActivity.this,
              getString(R.string.error_network_not_available),
              Toast.LENGTH_LONG).show();
    }
  }

  private void postUiInitialization()
  {
    // set up the toolbar
    setSupportActionBar(binding.tbCardDetailActivity);

    ActionBar actionBar = getSupportActionBar();
    if(actionBar != null)
    {
      actionBar.setDisplayHomeAsUpEnabled(true);

      // set title in the meta bar too
      binding.etToolbarCardName.setText(taskListCardModel.getCardTitle());

      final String toolbarLocationString = String.format(getString(R.string.card_detail_activity_toolbar_location_format_string),
              taskGroupModel.getTitle(),
              taskListTitle);

      binding.tvToolbarCardLocationDetails.setText(toolbarLocationString);

      // Only show the toolbar title when collapsed because we are using an edit text as our title
      // in the action bar and we do not want the toolbar title and it to overlap
      binding.appBarLayoutCardDetail.addOnOffsetChangedListener(new CardDetailAppbarOffsetChangedListener());
    }

    setupUi();

    setupRecyclerViews();
  }

  private void setupUi()
  {
    binding.etCardDetailActivityDetailedDescription.setText(taskListCardModel.getCardDetailedDescription());

    binding.tvCardDetailActivityDueDate.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        onDueDateClick();
      }
    });

    if(dueDateModel == null)
    {
      binding.cbCardDetailActivityDueDate.setOnTouchListener(null);
      binding.cbCardDetailActivityDueDate.setOnCheckedChangeListener(null);
      dueDateCheckListener = null;
      dueDateCheckChangeListener = null;

      binding.linearCardDetailActivityDueDateRow.setVisibility(View.GONE);
    }
    else
    {
      updateDueDateView(new Date(dueDateModel.getDueDate()));
    }

    // remove the underline from the toolbar title, save the background too
    editTextToolbarTitleBackgroundSave = binding.etToolbarCardName.getBackground();
    binding.etToolbarCardName.setBackgroundResource(android.R.color.transparent);

    // save the background of the detail description edit text before removing the underline
    editTextDetailDescriptionBackgroundSave = binding.etCardDetailActivityDetailedDescription.getBackground();
    binding.etCardDetailActivityDetailedDescription.setBackgroundResource(android.R.color.transparent);

    // setup the focus change listeners for the edit text views
    binding.etToolbarCardName.setOnFocusChangeListener(new ToolbarNameFocusListener());
    binding.etToolbarCardName.setOnTouchListener(new ToolbarNameOnTouchListener());

    binding.etCardDetailActivityDetailedDescription.setOnFocusChangeListener(new EditDescriptionFocusListener());
    binding.etCardDetailActivityDetailedDescription.setOnTouchListener(new EditDescriptionTouchListener());
  }

  private void setupRecyclerViews()
  {
    // set up checklist recycler view
    LinearLayoutManager checklistLayoutManager = new LinearLayoutManager(this);
    binding.rvCardDetailActivityChecklists.setLayoutManager(checklistLayoutManager);

    checklistAdapter = new CardDetailChecklistAdapter(this, checklistList, this);
    binding.rvCardDetailActivityChecklists.setAdapter(checklistAdapter);

    ViewCompat.setNestedScrollingEnabled(binding.rvCardDetailActivityChecklists, false);

    // set up attachment recycler view
    LinearLayoutManager attachmentLayoutManager = new LinearLayoutManager(this);
    binding.rvCardDetailActivityAttachmentAttachments.setLayoutManager(attachmentLayoutManager);

    attachmentAdapter = new CardDetailAttachmentAdapter(attachmentList, this);
    binding.rvCardDetailActivityAttachmentAttachments.setAdapter(attachmentAdapter);

    if(attachmentList.size() > 0)
    {
      // we have at least one attachment show the attachment view
      binding.linearCardDetailActivityAttachment.setVisibility(View.VISIBLE);
    }
  }

  private void fetchRemoteData()
  {
    // Grab the due date from the database if it exists
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    databaseReference.child(getString(R.string.db_due_date_object))
            .child(taskListCardModel.getCardId())
            .addListenerForSingleValueEvent(new ValueEventListener()
            {
              @Override
              public void onDataChange(DataSnapshot dueDateSnapshot)
              {
                if(dueDateSnapshot.exists())
                {
                  Long dueDateLong = dueDateSnapshot.child(getString(R.string.db_due_date_due_date_key)).getValue(Long.class);

                  long dueDate = dueDateLong != null ? dueDateLong : 0;

                  Boolean completedBoolean = dueDateSnapshot.child(getString(R.string.db_due_date_completed_key)).getValue(Boolean.class);

                  boolean completed = completedBoolean != null ? completedBoolean : false;

                  dueDateModel = new DueDateModel(taskListCardModel.getCardId(), dueDate, completed);

                  // Refresh the UI state
                  updateDueDateView(new Date(dueDateModel.getDueDate()));
                }
              }

              @Override
              public void onCancelled(DatabaseError databaseError)
              {
                Log.i("CardDetailAct", "failed to get a due date the from database");
              }
            });

    // Grab the checklist ids associated with this card from the database
    databaseReference.child(getString(R.string.db_task_list_card_checklist_key))
            .child(taskListCardModel.getCardId())
            .orderByValue()
            .addListenerForSingleValueEvent(new ValueEventListener()
            {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot)
              {
                for(DataSnapshot taskListCardChecklistSnapshot : dataSnapshot.getChildren())
                {
                  // the value is the checklist index within the task list card
                  String checklistId = taskListCardChecklistSnapshot.getKey();

                  DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                  // grab the checklist associated with the specified checklist id
                  databaseReference.child(getString(R.string.db_checklist_object))
                          .child(checklistId)
                          .addListenerForSingleValueEvent(new ChecklistValueEventListener(checklistId));
                }
              }

              @Override
              public void onCancelled(DatabaseError databaseError)
              {
                Log.i("CardDetailAct", "failed to get checklist id from database");
              }
            });
  }

  class ChecklistValueEventListener implements ValueEventListener
  {
    private String checklistId;

    ChecklistValueEventListener(String checklistId)
    {
      this.checklistId = checklistId;
    }

    @Override
    public void onDataChange(DataSnapshot checklistSnapshot)
    {
      String checklistTitle = checklistSnapshot.child(getString(R.string.db_checklist_title_key)).getValue(String.class);

      Long checklistIndexLong = checklistSnapshot.child(getString(R.string.db_checklist_index_key)).getValue(Long.class);

      final int checklistIndex = checklistIndexLong != null ? checklistIndexLong.intValue() : 0;

      Boolean collapsedBoolean = checklistSnapshot.child(getString(R.string.db_checklist_collapsed_key)).getValue(Boolean.class);

      boolean collapsed = collapsedBoolean != null ? collapsedBoolean : false;

      ChecklistModel checklistModel = new ChecklistModel(taskListCardModel.getCardId(),
              checklistId,
              checklistTitle,
              checklistIndex,
              collapsed);

      ChecklistModelContainer checklistModelContainer = new ChecklistModelContainer(checklistModel, new ArrayList<ChecklistItemModel>());

      checklistList.add(checklistModelContainer);

      checklistAdapter.notifyDataSetChanged();

      DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

      // Grab all the checklist item ids that are associated with the specified checklist
      databaseReference.child(getString(R.string.db_checklist_checklist_item_key))
              .child(checklistModelContainer.getChecklistModel().getChecklistId())
              .orderByValue()
              .addListenerForSingleValueEvent(new ValueEventListener()
              {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                  for(DataSnapshot checklistChecklistItemSnapshot : dataSnapshot.getChildren())
                  {
                    // the value is the index of the checklist item within the checklist
                    String checklistItemId = checklistChecklistItemSnapshot.getKey();

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                    // grab the checklist item associated with the specified checklist item id
                    databaseReference.child(getString(R.string.db_checklist_item_object))
                            .child(checklistItemId)
                            .addListenerForSingleValueEvent(new ChecklistItemValueEventListener(checklistItemId, checklistIndex));
                  }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                  Log.i("CardDetailAct", "failed to get checklist item id from database");
                }
              });
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {
      Log.i("CardDetailAct", "failed to get checklist from database");
    }
  }

  private void removeFocusFromEditText(EditText editText)
  {
    editText.setFocusableInTouchMode(false);
    editText.setFocusable(false);
    editText.setFocusableInTouchMode(true);
    editText.setFocusable(true);
  }

  private void setUpEditCommitMenu(String commitActionAppBarTitle,
                                   IEditCompleteListener editCompleteListener)
  {
    enableEditCommitMenu = true;

    this.commitActionAppBarTitle = commitActionAppBarTitle;

    this.editCompleteListener = editCompleteListener;

    invalidateOptionsMenu();
  }

  private void tearDownEditCommitMenu()
  {
    enableEditCommitMenu = false;

    commitActionAppBarTitle = "";

    editCompleteListener = null;

    invalidateOptionsMenu();
  }

  class EditDescriptionCompleteListener implements IEditCompleteListener
  {
    @Override
    public void onEditComplete()
    {
      // Strip the edit text of focus
      removeFocusFromEditText(binding.etCardDetailActivityDetailedDescription);
    }
  }

  class EditDescriptionFocusListener implements View.OnFocusChangeListener
  {
    private String initialText;

    EditDescriptionFocusListener()
    {
      initialText = "";
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
      if(hasFocus)
      {
        // get the current text
        initialText = binding.etCardDetailActivityDetailedDescription.getText().toString();

        // Add the underline back to the detailed description edit text
        TaskMasterUtils.restoreEditTextUnderline(binding.etCardDetailActivityDetailedDescription,
                editTextDetailDescriptionBackgroundSave);

        setUpEditCommitMenu(getString(R.string.card_detail_activity_edit_description_title_string),
                new EditDescriptionCompleteListener());
      }
      else
      {
        // save the background of the detailed description edit text before removing the underline
        editTextDetailDescriptionBackgroundSave = TaskMasterUtils.removeEditTextUnderline(binding.etCardDetailActivityDetailedDescription);

        tearDownEditCommitMenu();

        final boolean changed = hasChanged();

        commitUiChanges();

        // update the database
        if(changed)
        {
          FirebaseDatabase database = FirebaseDatabase.getInstance();
          DatabaseReference rootDatabaseReference = database.getReference();

          String taskListCardRoot = String.format("/%s/%s/", getString(R.string.db_task_list_card_object_key),
                  taskListCardModel.getCardId());

          Map<String, Object> childUpdates = new HashMap<>();

          childUpdates.put(taskListCardRoot + getString(R.string.db_task_list_card_detailed_description_key),
                  taskListCardModel.getCardDetailedDescription());

          rootDatabaseReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener()
          {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
            {
              if(databaseError == null)
              {
                // update success
                Log.i("CardDetailAct", "updated the detailed description in database");
              }
              else
              {
                // update failure
                Log.i("CardDetailAct", "failed to update the detailed description in database");
              }
            }
          });

          TaskMasterUtils.setAppStateDirty(CardDetailActivity.this);
        }
      }
    }

    private void commitUiChanges()
    {
      String changedText = binding.etCardDetailActivityDetailedDescription.getText().toString();

      taskListCardModel.setCardDetailedDescription(changedText);
    }

    private boolean hasChanged()
    {
      String changedText = binding.etCardDetailActivityDetailedDescription.getText().toString();

      return !initialText.equals(changedText);
    }
  }

  /**
   * Helper class that is needed so that the edit text will trigger touch listeners on the first
   * click. If you just set an onclick listener to an edit text, the first click will not trigger
   * onclick only the second click will.
   *
   * This class will simply return false from onTouch which means that the touch event will not be
   * consumed. This when used with a focus listener shall act as you would think simply adding a
   * click listener would.
   */
  class EditDescriptionTouchListener implements View.OnTouchListener
  {
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
      return TaskMasterUtils.onTouchHelper(CardDetailActivity.this, v, event);
    }
  }

  class ToolbarNameCompleteListener implements IEditCompleteListener
  {
    @Override
    public void onEditComplete()
    {
      removeFocusFromEditText(binding.etToolbarCardName);
    }
  }

  class ToolbarNameFocusListener implements View.OnFocusChangeListener
  {
    private String initialText;

    ToolbarNameFocusListener()
    {
      initialText = "";
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
      if(hasFocus)
      {
        // Add the underline back to the toolbar edit text
        TaskMasterUtils.restoreEditTextUnderline(binding.etToolbarCardName,
                editTextToolbarTitleBackgroundSave);

        setUpEditCommitMenu(getString(R.string.card_detail_activity_edit_toolbar_title_string),
                new ToolbarNameCompleteListener());
      }
      else
      {
        // save the background of the toolbar edit text before removing the underline
        editTextToolbarTitleBackgroundSave = TaskMasterUtils.removeEditTextUnderline(binding.etToolbarCardName);

        taskListCardModel.setCardTitle(binding.etToolbarCardName.getText().toString());

        tearDownEditCommitMenu();

        final boolean changed = hasChanged();

        commitUiChanges();

        // Update the database
        if(changed)
        {
          FirebaseDatabase database = FirebaseDatabase.getInstance();
          DatabaseReference rootDatabaseReference = database.getReference();

          String taskListCardRoot = String.format("/%s/%s/", getString(R.string.db_task_list_card_object_key),
                  taskListCardModel.getCardId());

          Map<String, Object> childUpdates = new HashMap<>();

          childUpdates.put(taskListCardRoot + getString(R.string.db_task_list_card_title_key),
                  taskListCardModel.getCardTitle());

          rootDatabaseReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener()
          {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
            {
              if(databaseError == null)
              {
                // update success
                Log.i("CardDetailAct", "updated the card title in database");
              }
              else
              {
                // update failure
                Log.i("CardDetailAct", "failed to update the card title in database");
              }
            }
          });

          TaskMasterUtils.setAppStateDirty(CardDetailActivity.this);
        }
      }
    }

    private void commitUiChanges()
    {
      String changedText = binding.etToolbarCardName.getText().toString();

      taskListCardModel.setCardTitle(changedText);
    }

    private boolean hasChanged()
    {
      String changedText = binding.etToolbarCardName.getText().toString();

      return !initialText.equals(changedText);
    }
  }

  class ToolbarNameOnTouchListener implements View.OnTouchListener
  {
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
      return TaskMasterUtils.onTouchHelper(CardDetailActivity.this, v, event);
    }
  }

  class CardDetailAppbarOffsetChangedListener implements AppBarLayout.OnOffsetChangedListener
  {
    private int scrollRange;

    private boolean handledToolbarUnfolding;

    CardDetailAppbarOffsetChangedListener()
    {
      this.scrollRange = -1;

      this.handledToolbarUnfolding = false;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset)
    {
      if(scrollRange == -1)
      {
        scrollRange = appBarLayout.getTotalScrollRange();
      }

      boolean editMode = !TextUtils.isEmpty(commitActionAppBarTitle);

      toolbarCollapsed = (scrollRange + verticalOffset) == 0;

      if(toolbarCollapsed)
      {
        // The toolbar is collapsed
        if(editMode)
        {
          // we are in edit mode so set the appropriate title
          binding.collapsingToolbarLayoutCardDetail.setTitle(commitActionAppBarTitle);
        }
        else
        {
          // set the original title
          binding.collapsingToolbarLayoutCardDetail.setTitle(taskListCardModel.getCardTitle());
        }

        // we've just handled collapse, we are safe to handle another unfolding
        handledToolbarUnfolding = false;
      }
      else
      {
        if(!handledToolbarUnfolding)
        {
          // We are no longer collapsed, remove the collapsed toolbar title
          binding.collapsingToolbarLayoutCardDetail.setTitle(" ");

          final ActionBar actionBar = getSupportActionBar();

          if(actionBar != null)
          {
            if(editMode)
            {
              // however we are in edit mode, so show the appropriate title
              appBarLayout.post(new Runnable()
              {
                @Override
                public void run()
                {
                  actionBar.setTitle(commitActionAppBarTitle);
                  binding.collapsingToolbarLayoutCardDetail.setTitleEnabled(false);
                }
              });
            }
            else
            {
              appBarLayout.post(new Runnable()
              {
                @Override
                public void run()
                {
                  actionBar.setTitle("");
                  binding.collapsingToolbarLayoutCardDetail.setTitleEnabled(true);
                }
              });
            }
          }

          // make sure we don't call getSupportActionBar().setTitle again unless we have
          // seen a collapse. Handling this conditional block of the onOffsetChanged handler
          // multiple times will cause excessive warning messages during runtime.
          handledToolbarUnfolding = true;
        }
      }
    }
  }

  class DueDateCheckListener implements View.OnTouchListener
  {
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
      return TaskMasterUtils.onTouchHelper(CardDetailActivity.this, v, event);
    }
  }

  class DueDateCheckChangeListener implements CompoundButton.OnCheckedChangeListener
  {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
      if(isChecked)
      {
        // checked
        Drawable drawable = TaskMasterUtils.setDrawableResColorRes(CardDetailActivity.this,
                R.drawable.ic_baseline_schedule_24px,
                R.color.due_date_completed);

        binding.ivCardDetailActivityDueDate.setImageDrawable(drawable);

        dueDateModel.setCompleted(true);
      }
      else
      {
        // not checked

        // use the saved color
        Drawable drawable = TaskMasterUtils.setDrawableResColorRes(CardDetailActivity.this,
                R.drawable.ic_baseline_schedule_24px,
                currentDueDateColorResId);

        binding.ivCardDetailActivityDueDate.setImageDrawable(drawable);

        dueDateModel.setCompleted(false);
      }

      // Update the check state in the database
      FirebaseDatabase database = FirebaseDatabase.getInstance();
      DatabaseReference rootDatabaseReference = database.getReference();

      String dueDateRoot = String.format("/%s/%s/", getString(R.string.db_due_date_object),
              taskListCardModel.getCardId());

      Map<String, Object> childUpdates = new HashMap<>();

      childUpdates.put(dueDateRoot + getString(R.string.db_due_date_completed_key),
              dueDateModel.isCompleted());

      rootDatabaseReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener()
      {
        @Override
        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
        {
          if(databaseError == null)
          {
            // write success
            Log.i("CardDetailAct", "updated due date completed state in database");

            if(dueDateModel.isCompleted())
            {
              // delete
              TaskMasterUtils.startWidgetDueDateDelete(CardDetailActivity.this,
                      taskListCardModel.getCardId());
            }
            else
            {
              // insert
              TaskMasterUtils.startWidgetDueDateUpdate(CardDetailActivity.this,
                      taskListCardModel.getCardId(),
                      taskListCardModel.getCardTitle(),
                      taskListCardModel.getCardDetailedDescription(),
                      taskListCardModel.getCardIndex(),
                      taskListCardModel.getTaskIndex(),
                      taskListCardModel.getTaskListId(),
                      taskListTitle,
                      taskGroupModel.getId(),
                      taskGroupModel.getTitle(),
                      taskGroupModel.getColorKey(),
                      dueDateModel,
                      taskGroupModelList);
            }
          }
          else
          {
            // write failure
            Log.i("CardDetailAct", "failed to update due date completed state in database");
          }
        }
      });

      TaskMasterUtils.setAppStateDirty(CardDetailActivity.this);
    }
  }

  class ChecklistItemValueEventListener implements ValueEventListener
  {
    private String checklistItemId;
    private int checklistIndex;

    ChecklistItemValueEventListener(String checklistItemId, int checklistIndex)
    {
      this.checklistItemId = checklistItemId;
      this.checklistIndex = checklistIndex;
    }

    @Override
    public void onDataChange(DataSnapshot checklistItemSnapshot)
    {
      String checklistItemTitle = checklistItemSnapshot.child(getString(R.string.db_checklist_item_title_key)).getValue(String.class);

      Long checklistItemIndexLong = checklistItemSnapshot.child(getString(R.string.db_checklist_item_index_key)).getValue(Long.class);

      final int checklistItemIndex = checklistItemIndexLong != null ? checklistItemIndexLong.intValue() : 0;

      Boolean completedBoolean = checklistItemSnapshot.child(getString(R.string.db_checklist_item_completed_key)).getValue(Boolean.class);

      boolean completed = completedBoolean != null ? completedBoolean : false;

      ChecklistModelContainer checklistModelContainer = checklistList.get(checklistIndex);

      ChecklistItemModel checklistItemModel = new ChecklistItemModel(checklistModelContainer.getChecklistModel().getChecklistId(),
              checklistItemId,
              checklistItemTitle,
              checklistItemIndex,
              completed);

      checklistModelContainer.getChecklistItemModelList().add(checklistItemModel);

      CardDetailChecklistDropDownAdapter checklistDropDownAdapter = checklistModelContainer.getCardDetailChecklistDropDownAdapter();

      if(checklistDropDownAdapter != null)
      {
        checklistDropDownAdapter.notifyDataSetChanged();
      }
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {
      Log.i("CardDetailAct", "failed to get checklist item from database");
    }
  }
}
