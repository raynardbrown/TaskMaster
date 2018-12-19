package com.example.android.taskmaster.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.ChecklistItemBinding;
import com.example.android.taskmaster.model.ChecklistModel;
import com.example.android.taskmaster.utils.TaskMasterUtils;
import com.example.android.taskmaster.view.dialog.DeleteChecklistDialogFragment;

import java.util.List;

public class CardDetailChecklistAdapter extends RecyclerView.Adapter<CardDetailChecklistAdapter.CardDetailChecklistAdapterViewHolder>
{
  private List<ChecklistModel> checklistList;

  private AppCompatActivity activity;

  private IChecklistItemClickListener checklistItemClickListener;

  private Drawable checklistUnderlineSave;

  CardDetailChecklistAdapter(AppCompatActivity activity,
                             List<ChecklistModel> checklistList,
                             IChecklistItemClickListener checklistItemClickListener)
  {
    this.activity = activity;
    this.checklistList = checklistList;
    this.checklistItemClickListener = checklistItemClickListener;
  }

  @Override
  public CardDetailChecklistAdapterViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType)
  {
    Context context = parent.getContext();
    int layoutIdForListItem = R.layout.checklist_item;
    LayoutInflater inflater = LayoutInflater.from(context);
    final boolean shouldAttachToParentImmediately = false;

    ChecklistItemBinding itemBinding = DataBindingUtil.inflate(inflater, layoutIdForListItem, parent, shouldAttachToParentImmediately);

    return new CardDetailChecklistAdapterViewHolder(itemBinding);
  }

  @Override
  public void onBindViewHolder(CardDetailChecklistAdapterViewHolder holder, int position)
  {
    ChecklistItemBinding itemBinding = holder.itemBinding;

    itemBinding.etChecklistName.setText(checklistList.get(position).getChecklistTitle());

    Drawable checklistUnderlineSaveTemp = TaskMasterUtils.removeEditTextUnderline(itemBinding.etChecklistName);

    if(checklistUnderlineSave == null)
    {
      checklistUnderlineSave = checklistUnderlineSaveTemp;
    }

    // attach the listeners to the edit text
    itemBinding.etChecklistName.setOnFocusChangeListener(new ChecklistFocusListener(itemBinding.etChecklistName, position));
    itemBinding.etChecklistName.setOnTouchListener(new ChecklistOnTouchListener());

    itemBinding.chevronButtonChecklist.setOnClickListener(new CheckListChevronClickListener(holder.itemBinding.chevronButtonChecklist,
            itemBinding.rvChecklist));

    itemBinding.menuButtonChecklist.setOnClickListener(new ChecklistDeleteChecklistMenuClickListener(position));

    setupCheckListItemRecyclerView(itemBinding, position);
  }

  @Override
  public int getItemCount()
  {
    return checklistList.size();
  }

  private void setupCheckListItemRecyclerView(ChecklistItemBinding itemBinding, int position)
  {
    LinearLayoutManager layoutManager = new LinearLayoutManager(itemBinding.getRoot().getContext());
    itemBinding.rvChecklist.setLayoutManager(layoutManager);

    CardDetailChecklistDropDownAdapter adapter = new CardDetailChecklistDropDownAdapter(checklistList.get(position).getChecklistId(),
            checklistList.get(position).getChecklistItemModelList(),
            checklistItemClickListener);

    itemBinding.rvChecklist.setAdapter(adapter);

    ViewCompat.setNestedScrollingEnabled(itemBinding.rvChecklist, false);
  }

  class ChecklistCompleteListener implements IEditCompleteListener
  {
    private EditText editText;
    private int position;

    ChecklistCompleteListener(EditText editText, int position)
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

      ChecklistModel itemModel = checklistList.get(position);

      itemModel.setChecklistTitle(changedText);

      // TODO: update the database too
    }
  }

  class ChecklistFocusListener implements View.OnFocusChangeListener
  {
    private EditText editText;
    private int position;

    ChecklistFocusListener(EditText editText, int position)
    {
      this.editText = editText;
      this.position = position;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
      if(hasFocus)
      {
        // Add the underline back to the edit text
        TaskMasterUtils.restoreEditTextUnderline(editText, checklistUnderlineSave);

        final boolean showCommitButton = true;

        Context context = editText.getContext();

        checklistItemClickListener.onChecklistItemClick(context.getString(R.string.card_detail_activity_edit_check_list_string),
                showCommitButton,
                new ChecklistCompleteListener(editText, position));
      }
      else
      {
        // remove the underline
        TaskMasterUtils.removeEditTextUnderline(editText);

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
  class ChecklistOnTouchListener implements View.OnTouchListener
  {
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
      return false;
    }
  }

  class CheckListChevronClickListener implements View.OnClickListener
  {
    private ImageButton chevronButton;
    private RecyclerView recyclerView;

    CheckListChevronClickListener(ImageButton chevronButton,
                                  RecyclerView recyclerView)
    {
      this.chevronButton = chevronButton;
      this.recyclerView = recyclerView;
    }

    @Override
    public void onClick(View v)
    {
      // show hide the recycler view for the check list change the chevron button too
      if(recyclerView.getVisibility() == View.GONE)
      {
        recyclerView.setVisibility(View.VISIBLE);
        chevronButton.setImageDrawable(v.getResources().getDrawable(R.drawable.ic_baseline_expand_less_24px));
      }
      else
      {
        recyclerView.setVisibility(View.GONE);
        chevronButton.setImageDrawable(v.getResources().getDrawable(R.drawable.ic_baseline_expand_more_24px));
      }
    }
  }

  class ChecklistDeleteChecklistMenuClickListener implements View.OnClickListener
  {
    private int position;

    ChecklistDeleteChecklistMenuClickListener(int position)
    {
      this.position = position;
    }

    @Override
    public void onClick(View v)
    {
      PopupMenu popup = new PopupMenu(v.getContext(), v);
      popup.setOnMenuItemClickListener(new ChecklistDeleteChecklistMenuItemCLickListener(position));
      MenuInflater inflater = popup.getMenuInflater();
      inflater.inflate(R.menu.menu_checklist, popup.getMenu());
      popup.show();
    }
  }

  class ChecklistDeleteChecklistMenuItemCLickListener implements PopupMenu.OnMenuItemClickListener
  {
    private int position;

    ChecklistDeleteChecklistMenuItemCLickListener(int position)
    {
      this.position = position;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
      switch(item.getItemId())
      {
        case R.id.action_delete_checklist:
        {
          DeleteChecklistDialogFragment dialogFragment = new DeleteChecklistDialogFragment();

          Bundle bundle = new Bundle();
          bundle.putInt(activity.getString(R.string.check_list_position_key), position);

          dialogFragment.setArguments(bundle);

          dialogFragment.show(activity.getSupportFragmentManager(),
                  activity.getString(R.string.card_detail_activity_dialog_delete_checklist_tag_string));
          return true;
        }

        default:
        {
          return false;
        }
      }
    }
  }

  class CardDetailChecklistAdapterViewHolder extends RecyclerView.ViewHolder
  {
    private ChecklistItemBinding itemBinding;

    CardDetailChecklistAdapterViewHolder(ChecklistItemBinding itemBinding)
    {
      super(itemBinding.getRoot());

      this.itemBinding = itemBinding;
    }
  }
}
