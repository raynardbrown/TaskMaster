package com.example.android.taskmaster.view;

import android.content.Context;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.ChecklistDropDownItemBinding;
import com.example.android.taskmaster.model.ChecklistItemModel;
import com.example.android.taskmaster.utils.TaskMasterUtils;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CardDetailChecklistDropDownAdapter extends RecyclerView.Adapter<CardDetailChecklistDropDownAdapter.CardDetailChecklistDropDownAdapterViewHolder>
{
  private String parentChecklistId;
  private List<ChecklistItemModel> checklistList;
  private int checkListIndex;
  private IChecklistItemClickListener checklistItemClickListener;
  private IDeleteChecklistItemListener deleteChecklistItemListener;
  private Drawable checkListItemUnderlineSave;
  private Drawable checkListAddItemUnderlineSave;

  CardDetailChecklistDropDownAdapter(String parentChecklistId,
                                     List<ChecklistItemModel> checklistList,
                                     int checkListIndex,
                                     IChecklistItemClickListener checklistItemClickListener,
                                     IDeleteChecklistItemListener deleteChecklistItemListener)
  {
    this.parentChecklistId = parentChecklistId;
    this.checklistList = checklistList;
    this.checkListIndex = checkListIndex;
    this.checklistItemClickListener = checklistItemClickListener;
    this.deleteChecklistItemListener = deleteChecklistItemListener;
  }

  @Override
  public CardDetailChecklistDropDownAdapterViewHolder onCreateViewHolder(ViewGroup parent,
                                                                         int viewType)
  {
    Context context = parent.getContext();
    int layoutIdForListItem = R.layout.checklist_drop_down_item;
    LayoutInflater inflater = LayoutInflater.from(context);
    final boolean shouldAttachToParentImmediately = false;

    ChecklistDropDownItemBinding itemBinding = DataBindingUtil.inflate(inflater,
            layoutIdForListItem,
            parent,
            shouldAttachToParentImmediately);

    return new CardDetailChecklistDropDownAdapterViewHolder(itemBinding);
  }

  @Override
  public void onBindViewHolder(CardDetailChecklistDropDownAdapterViewHolder holder,
                               int position)
  {
    ChecklistDropDownItemBinding itemBinding = holder.itemBinding;

    if(position == (getItemCount() - 1))
    {
      Context context = itemBinding.etCardDetailActivityChecklistItemName.getContext();
      itemBinding.etCardDetailActivityChecklistItemName.setHint(context.getString(R.string.card_detail_activity_add_check_list_item_string));
      itemBinding.etCardDetailActivityChecklistItemName.setText("");

      // The add item is not a check list item, so it doesn't get a check box nor delete button
      itemBinding.cbCardDetailActivityChecklistItem.setVisibility(View.INVISIBLE);
      itemBinding.menuButtonCardDetailActivityChecklistItemDelete.setVisibility(View.INVISIBLE);

      if(checkListAddItemUnderlineSave == null)
      {
        // cache the underline if we haven't already
        checkListAddItemUnderlineSave = TaskMasterUtils.retrieveEditTextUnderline(itemBinding.etCardDetailActivityChecklistItemName);

        // Save a copy of the drawable so that the state is not shared with a "new checklist item"
        // drawable.
        if(checkListAddItemUnderlineSave.getConstantState() != null)
        {
          checkListAddItemUnderlineSave = checkListAddItemUnderlineSave.getConstantState().newDrawable().mutate();
        }
      }
      else
      {
        // Make sure that the underline is visible
        TaskMasterUtils.restoreEditTextUnderline(itemBinding.etCardDetailActivityChecklistItemName,
                checkListAddItemUnderlineSave);
      }

      // add the listeners
      itemBinding.etCardDetailActivityChecklistItemName.setOnTouchListener(new ChecklistItemOnTouchListener());
      itemBinding.etCardDetailActivityChecklistItemName.setOnFocusChangeListener(new AddItemFocusListener(itemBinding.etCardDetailActivityChecklistItemName));

      // remove the change listener
      itemBinding.cbCardDetailActivityChecklistItem.setOnCheckedChangeListener(null);
    }
    else
    {
      itemBinding.etCardDetailActivityChecklistItemName.setText(checklistList.get(position).getItemTitle());

      // Show the checklist item check box, remove the delete button
      itemBinding.cbCardDetailActivityChecklistItem.setVisibility(View.VISIBLE);
      itemBinding.menuButtonCardDetailActivityChecklistItemDelete.setVisibility(View.INVISIBLE);

      // Remove the underline
      Drawable checkListItemUnderlineSaveTemp = TaskMasterUtils.removeEditTextUnderline(itemBinding.etCardDetailActivityChecklistItemName);

      if(checkListItemUnderlineSave == null)
      {
        checkListItemUnderlineSave = checkListItemUnderlineSaveTemp;
      }

      // add the listeners
      itemBinding.cbCardDetailActivityChecklistItem.setOnTouchListener(new ChecklistItemCheckListener());
      itemBinding.cbCardDetailActivityChecklistItem.setOnCheckedChangeListener(new ChecklistItemCheckChangeListener(holder));
      itemBinding.etCardDetailActivityChecklistItemName.setOnTouchListener(new ChecklistItemOnTouchListener());
      itemBinding.etCardDetailActivityChecklistItemName.setOnFocusChangeListener(new ChecklistItemFocusListener(itemBinding.etCardDetailActivityChecklistItemName,
              holder,
              itemBinding.menuButtonCardDetailActivityChecklistItemDelete));
      itemBinding.menuButtonCardDetailActivityChecklistItemDelete.setOnClickListener(new ChecklistItemDeleteButtonClickListener(holder));

      // set the check state
      itemBinding.cbCardDetailActivityChecklistItem.setChecked(checklistList.get(position).isCompleted());
    }
  }

  @Override
  public int getItemCount()
  {
    // Plus one for the "Add Item..." at the end of all checklists.
    return checklistList.size() + 1;
  }

  class AddItemCompleteListener implements IEditCompleteListener
  {
    private EditText editText;
    private boolean completeSuccess;

    AddItemCompleteListener(EditText editText)
    {
      this.editText = editText;
      completeSuccess = false;
    }

    @Override
    public void onEditComplete()
    {
      // Check to make sure the edit text length is greater than 0. Believe it or not the edit text
      // function getText().toString() can actually return the hint?!?!
      if(editText.length() > 0)
      {
        String newText = editText.getText().toString();

        // Grab the text and add the item to the list if and only if the string is not empty
        ChecklistItemModel checklistItemModel = new ChecklistItemModel(parentChecklistId,
                UUID.randomUUID().toString(),
                newText,
                checklistList.size(),
                false);

        checklistList.add(checklistItemModel);

        // notify the adapter
        notifyDataSetChanged();

        completeSuccess = true;

        // update the database

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rootDatabaseReference = database.getReference();

        Resources res = editText.getResources();

        String checklistItemModelRoot = String.format("/%s/%s/", res.getString(R.string.db_checklist_item_object),
                checklistItemModel.getChecklistItemId());

        String checklistChecklistItemRoot = String.format("/%s/%s/", res.getString(R.string.db_checklist_checklist_item_key),
                checklistItemModel.getChecklistId());

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put(checklistItemModelRoot + res.getString(R.string.db_checklist_item_title_key),
                checklistItemModel.getItemTitle());

        childUpdates.put(checklistItemModelRoot + res.getString(R.string.db_checklist_item_index_key),
                checklistItemModel.getItemIndex());

        childUpdates.put(checklistItemModelRoot + res.getString(R.string.db_checklist_item_completed_key),
                checklistItemModel.isCompleted());

        // link the checklist item to its checklist
        childUpdates.put(checklistChecklistItemRoot + checklistItemModel.getChecklistItemId(),
                checklistItemModel.getItemIndex());

        rootDatabaseReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener()
        {
          @Override
          public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
          {
            if(databaseError == null)
            {
              // write success
              Log.i("CardDetDropDownAdp", "wrote checklist item to database");
            }
            else
            {
              // write failure
              Log.i("CardDetDropDownAdp", "failed to write checklist item to database");
            }
          }
        });

        TaskMasterUtils.setAppStateDirty(editText.getContext());
      }

      // normally you would strip the focus first, however clearing the focus triggers our focus
      // lost listener which clears the edit text. So we must wait to strip the focus until after
      // we have gathered the data from the edit text.
      stripFocus();
    }

    boolean getCompleteSuccess()
    {
      return completeSuccess;
    }

    private void stripFocus()
    {
      // Strip the edit text of focus
      editText.setFocusableInTouchMode(false);
      editText.setFocusable(false);
      editText.setFocusableInTouchMode(true);
      editText.setFocusable(true);
    }
  }

  class AddItemFocusListener implements View.OnFocusChangeListener
  {
    private EditText editText;
    private AddItemCompleteListener addItemCompleteListener;

    AddItemFocusListener(EditText editText)
    {
      this.editText = editText;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
      if(hasFocus)
      {
        addItemCompleteListener = new AddItemCompleteListener(editText);

        Context context = editText.getContext();

        final boolean showCommitButton = true;

        checklistItemClickListener.onChecklistItemClick(context.getString(R.string.card_detail_activity_new_check_list_item_string),
                showCommitButton,
                addItemCompleteListener);
      }
      else
      {
        final boolean hideCommitButton = false;

        if(!addItemCompleteListener.getCompleteSuccess())
        {
          commitUiChanges();

          TaskMasterUtils.restoreEditTextUnderline(editText,
                  checkListAddItemUnderlineSave);
        }

        checklistItemClickListener.onChecklistItemClick("",
                hideCommitButton,
                null);
      }
    }

    private void commitUiChanges()
    {
      Context context = editText.getContext();
      editText.setHint(context.getString(R.string.card_detail_activity_add_check_list_item_string));

      // use clear instead of setText("")
      editText.getText().clear();
    }
  }

  class ChecklistItemCompleteListener implements IEditCompleteListener
  {
    private EditText editText;

    ChecklistItemCompleteListener(EditText editText)
    {
      this.editText = editText;
    }

    @Override
    public void onEditComplete()
    {
      stripFocus();
    }

    private void stripFocus()
    {
      // Strip the edit text of focus
      editText.setFocusableInTouchMode(false);
      editText.setFocusable(false);
      editText.setFocusableInTouchMode(true);
      editText.setFocusable(true);
    }
  }

  class ChecklistItemFocusListener implements View.OnFocusChangeListener
  {
    private EditText editText;
    private CardDetailChecklistDropDownAdapterViewHolder holder;
    private ImageButton deleteButton;

    ChecklistItemFocusListener(EditText editText,
                               CardDetailChecklistDropDownAdapterViewHolder holder,
                               ImageButton deleteButton)
    {
      this.editText = editText;
      this.holder = holder;
      this.deleteButton = deleteButton;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
      if(hasFocus)
      {
        // Add the underline back to the edit text
        TaskMasterUtils.restoreEditTextUnderline(editText, checkListItemUnderlineSave);

        // show delete button
        deleteButton.setVisibility(View.VISIBLE);

        Context context = editText.getContext();

        final boolean showCommitButton = true;

        checklistItemClickListener.onChecklistItemClick(context.getString(R.string.card_detail_activity_edit_check_list_item_string),
                showCommitButton,
                new ChecklistItemCompleteListener(editText));
      }
      else
      {
        // remove the underline
        TaskMasterUtils.removeEditTextUnderline(editText);

        // hide delete button
        deleteButton.setVisibility(View.INVISIBLE);

        final boolean hideCommitButton = false;

        checklistItemClickListener.onChecklistItemClick("",
                hideCommitButton,
                null);

        final boolean changed = hasChanged();

        commitUiChanges();

        // Update the database
        if(changed)
        {
          FirebaseDatabase database = FirebaseDatabase.getInstance();
          DatabaseReference rootDatabaseReference = database.getReference();

          Resources res = editText.getResources();

          ChecklistItemModel checklistItemModel = checklistList.get(holder.getAdapterPosition());

          String checklistItemRoot = String.format("/%s/%s/", res.getString(R.string.db_checklist_item_object),
                  checklistItemModel.getChecklistItemId());

          Map<String, Object> childUpdates = new HashMap<>();

          childUpdates.put(checklistItemRoot + res.getString(R.string.db_checklist_item_title_key),
                  checklistItemModel.getItemTitle());

          rootDatabaseReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener()
          {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
            {
              if(databaseError == null)
              {
                // write success
                Log.i("CardDetDropDownAdp", "updated checklist item title in database");
              }
              else
              {
                // write failure
                Log.i("CardDetDropDownAdp", "failed to update checklist item title in database");
              }
            }
          });

          TaskMasterUtils.setAppStateDirty(editText.getContext());
        }
      }
    }

    private void commitUiChanges()
    {
      int position = holder.getAdapterPosition();

      // We need to check if we clicked the delete button when the edit text has focus since the
      // item would no longer exist at that point
      if(position != RecyclerView.NO_POSITION)
      {
        String changedText = editText.getText().toString();

        ChecklistItemModel itemModel = checklistList.get(holder.getAdapterPosition());

        itemModel.setItemTitle(changedText);
      }
    }

    private boolean hasChanged()
    {
      int position = holder.getAdapterPosition();

      // We need to check if we clicked the delete button when the edit text has focus since the
      // item would no longer exist at that point
      if(position != RecyclerView.NO_POSITION)
      {
        String changedText = editText.getText().toString();

        ChecklistItemModel itemModel = checklistList.get(holder.getAdapterPosition());

        String checkListItemTitle = itemModel.getItemTitle();

        return !checkListItemTitle.equals(changedText);
      }
      else
      {
        return false;
      }
    }
  }

  /**
   * See the EditDescriptionTouchListener class description.
   */
  class ChecklistItemOnTouchListener implements View.OnTouchListener
  {
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
      return TaskMasterUtils.onTouchHelper(v.getContext(), v, event);
    }
  }

  class ChecklistItemDeleteButtonClickListener implements View.OnClickListener
  {
    private CardDetailChecklistDropDownAdapterViewHolder holder;

    ChecklistItemDeleteButtonClickListener(CardDetailChecklistDropDownAdapterViewHolder holder)
    {
      this.holder = holder;
    }

    @Override
    public void onClick(View v)
    {
      deleteChecklistItemListener.onChecklistItemDelete(holder.getAdapterPosition(), checkListIndex);
    }
  }

  class ChecklistItemCheckListener implements View.OnTouchListener
  {
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
      return TaskMasterUtils.onTouchHelper(v.getContext(), v, event);
    }
  }

  class ChecklistItemCheckChangeListener implements CompoundButton.OnCheckedChangeListener
  {
    private CardDetailChecklistDropDownAdapterViewHolder holder;

    ChecklistItemCheckChangeListener(CardDetailChecklistDropDownAdapterViewHolder holder)
    {
      this.holder = holder;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
      ChecklistItemModel checklistItemModel = checklistList.get(holder.getAdapterPosition());

      if(isChecked)
      {
        checklistItemModel.setCompleted(true);
      }
      else
      {
        checklistItemModel.setCompleted(false);
      }

      // Update the check state in the database
      FirebaseDatabase database = FirebaseDatabase.getInstance();
      DatabaseReference rootDatabaseReference = database.getReference();

      Resources res = buttonView.getResources();

      String checklistItemRoot = String.format("/%s/%s/", res.getString(R.string.db_checklist_item_object),
              checklistItemModel.getChecklistItemId());

      Map<String, Object> childUpdates = new HashMap<>();

      childUpdates.put(checklistItemRoot + res.getString(R.string.db_checklist_item_completed_key),
              checklistItemModel.isCompleted());

      rootDatabaseReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener()
      {
        @Override
        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
        {
          if(databaseError == null)
          {
            // write success
            Log.i("CardDetDropDownAdp", "updated checklist item completed state in database");
          }
          else
          {
            // write failure
            Log.i("CardDetDropDownAdp", "failed to update checklist item completed state in database");
          }
        }
      });

      TaskMasterUtils.setAppStateDirty(buttonView.getContext());
    }
  }

  class CardDetailChecklistDropDownAdapterViewHolder extends RecyclerView.ViewHolder
  {
    private ChecklistDropDownItemBinding itemBinding;

    CardDetailChecklistDropDownAdapterViewHolder(ChecklistDropDownItemBinding itemBinding)
    {
      super(itemBinding.getRoot());

      this.itemBinding = itemBinding;
    }
  }
}
