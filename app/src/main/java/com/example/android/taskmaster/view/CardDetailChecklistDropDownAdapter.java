package com.example.android.taskmaster.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.ChecklistDropDownItemBinding;
import com.example.android.taskmaster.model.ChecklistItemModel;
import com.example.android.taskmaster.utils.TaskMasterUtils;

import java.util.List;

public class CardDetailChecklistDropDownAdapter extends RecyclerView.Adapter<CardDetailChecklistDropDownAdapter.CardDetailChecklistDropDownAdapterViewHolder>
{
  private String parentChecklistId;
  private List<ChecklistItemModel> checklistList;
  private IChecklistItemClickListener checklistItemClickListener;
  private Drawable checkListItemUnderlineSave;

  CardDetailChecklistDropDownAdapter(String parentChecklistId,
                                     List<ChecklistItemModel> checklistList,
                                     IChecklistItemClickListener checklistItemClickListener)
  {
    this.parentChecklistId = parentChecklistId;
    this.checklistList = checklistList;
    this.checklistItemClickListener = checklistItemClickListener;
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

      if(checkListItemUnderlineSave == null)
      {
        // cache the underline if we haven't already
        checkListItemUnderlineSave = TaskMasterUtils.retrieveEditTextUnderline(itemBinding.etCardDetailActivityChecklistItemName);
      }
      else
      {
        // Make sure that the underline is visible
        TaskMasterUtils.restoreEditTextUnderline(itemBinding.etCardDetailActivityChecklistItemName,
                checkListItemUnderlineSave);
      }

      // add the listeners
      itemBinding.etCardDetailActivityChecklistItemName.setOnTouchListener(new ChecklistItemOnTouchListener());
      itemBinding.etCardDetailActivityChecklistItemName.setOnFocusChangeListener(new AddItemFocusListener(itemBinding.etCardDetailActivityChecklistItemName));
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
      itemBinding.etCardDetailActivityChecklistItemName.setOnTouchListener(new ChecklistItemOnTouchListener());
      itemBinding.etCardDetailActivityChecklistItemName.setOnFocusChangeListener(new ChecklistItemFocusListener(itemBinding.etCardDetailActivityChecklistItemName,
              position,
              itemBinding.menuButtonCardDetailActivityChecklistItemDelete));
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

    AddItemCompleteListener(EditText editText)
    {
      this.editText = editText;
    }

    @Override
    public void onEditComplete()
    {
      // Strip the edit text of focus
      editText.setFocusableInTouchMode(false);
      editText.setFocusable(false);
      editText.setFocusableInTouchMode(true);
      editText.setFocusable(true);

      // Check to make sure the edit text length is greater than 0. Believe it or not the edit text
      // function getText().toString() can actually return the hint?!?!
      if(editText.length() > 0)
      {
        // Grab the text and add the item to the list if and only if the string is not empty
        ChecklistItemModel checklistItemModel = new ChecklistItemModel(parentChecklistId,
                editText.getText().toString(),
                checklistList.size(),
                false);

        checklistList.add(checklistItemModel);

        // notify the adapter
        notifyDataSetChanged();
      }

      // TODO: update the database too

      Context context = editText.getContext();
      editText.setHint(context.getString(R.string.card_detail_activity_add_check_list_item_string));
      editText.setText("");
    }
  }

  class ChecklistItemCompleteListener implements IEditCompleteListener
  {
    private EditText editText;
    private int position;

    ChecklistItemCompleteListener(EditText editText, int position)
    {
      this.editText = editText;
      this.position = position;
    }

    @Override
    public void onEditComplete()
    {
      // Strip the edit text of focus
      editText.setFocusableInTouchMode(false);
      editText.setFocusable(false);
      editText.setFocusableInTouchMode(true);
      editText.setFocusable(true);

      String changedText = editText.getText().toString();

      ChecklistItemModel itemModel = checklistList.get(position);

      itemModel.setItemTitle(changedText);

      // notify the adapter
      notifyDataSetChanged();

      // TODO: update the database too
    }
  }

  class AddItemFocusListener implements View.OnFocusChangeListener
  {
    private EditText editText;

    AddItemFocusListener(EditText editText)
    {
      this.editText = editText;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
      if(hasFocus)
      {
        Context context = editText.getContext();

        final boolean showCommitButton = true;

        checklistItemClickListener.onChecklistItemClick(context.getString(R.string.card_detail_activity_new_check_list_item_string),
                showCommitButton,
                new AddItemCompleteListener(editText));
      }
      else
      {
        final boolean hideCommitButton = false;

        checklistItemClickListener.onChecklistItemClick("",
                hideCommitButton,
                null);
      }
    }
  }

  class ChecklistItemFocusListener implements View.OnFocusChangeListener
  {
    private EditText editText;
    private int position;
    ImageButton deleteButton;

    ChecklistItemFocusListener(EditText editText,
                               int position,
                               ImageButton deleteButton)
    {
      this.editText = editText;
      this.position = position;
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
                new ChecklistItemCompleteListener(editText, position));
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
      }
    }
  }

  /**
   * See the EditDescriptionOnTouchListener class description.
   */
  class ChecklistItemOnTouchListener implements View.OnTouchListener
  {
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
      return false;
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
