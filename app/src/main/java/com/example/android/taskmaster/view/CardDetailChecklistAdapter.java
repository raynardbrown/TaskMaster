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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.ChecklistItemBinding;
import com.example.android.taskmaster.db.FirebaseRealtimeDbProvider;
import com.example.android.taskmaster.model.ChecklistItemModel;
import com.example.android.taskmaster.model.ChecklistModel;
import com.example.android.taskmaster.model.ChecklistModelContainer;
import com.example.android.taskmaster.utils.TaskMasterUtils;
import com.example.android.taskmaster.view.dialog.DeleteChecklistDialogFragment;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardDetailChecklistAdapter extends RecyclerView.Adapter<CardDetailChecklistAdapter.CardDetailChecklistAdapterViewHolder> implements IDeleteChecklistItemListener
{
  private List<ChecklistModelContainer> checklistList;

  private AppCompatActivity activity;

  private IChecklistItemClickListener checklistItemClickListener;

  private Drawable checklistUnderlineSave;

  CardDetailChecklistAdapter(AppCompatActivity activity,
                             List<ChecklistModelContainer> checklistList,
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

    itemBinding.etChecklistName.setText(checklistList.get(position).getChecklistModel().getChecklistTitle());

    Drawable checklistUnderlineSaveTemp = TaskMasterUtils.removeEditTextUnderline(itemBinding.etChecklistName);

    if(checklistUnderlineSave == null)
    {
      checklistUnderlineSave = checklistUnderlineSaveTemp;
    }

    // attach the listeners to the edit text
    itemBinding.etChecklistName.setOnFocusChangeListener(new ChecklistFocusListener(itemBinding.etChecklistName, holder));
    itemBinding.etChecklistName.setOnTouchListener(new ChecklistOnTouchListener());

    itemBinding.chevronButtonChecklist.setOnClickListener(new CheckListChevronClickListener(holder,
            holder.itemBinding.chevronButtonChecklist,
            itemBinding.rvChecklist));

    itemBinding.menuButtonChecklist.setOnClickListener(new ChecklistDeleteChecklistMenuClickListener(holder));

    setupCheckListItemRecyclerView(itemBinding, position);

    if(checklistList.get(holder.getAdapterPosition()).getChecklistModel().isCollapsed())
    {
      itemBinding.rvChecklist.setVisibility(View.GONE);
      itemBinding.chevronButtonChecklist.setImageDrawable(itemBinding.rvChecklist.getResources().getDrawable(R.drawable.ic_baseline_expand_more_24px));
    }
    else
    {
      itemBinding.rvChecklist.setVisibility(View.VISIBLE);
      itemBinding.chevronButtonChecklist.setImageDrawable(itemBinding.rvChecklist.getResources().getDrawable(R.drawable.ic_baseline_expand_less_24px));
    }
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

    ChecklistModelContainer container = checklistList.get(position);

    CardDetailChecklistDropDownAdapter adapter = new CardDetailChecklistDropDownAdapter(container.getChecklistModel().getChecklistId(),
            container.getChecklistItemModelList(),
            position,
            checklistItemClickListener,
            this);

    container.setCardDetailChecklistDropDownAdapter(adapter);

    itemBinding.rvChecklist.setAdapter(adapter);

    ViewCompat.setNestedScrollingEnabled(itemBinding.rvChecklist, false);
  }

  @Override
  public void onChecklistItemDelete(int checklistIndex, int itemIndex)
  {
    ChecklistModelContainer container = checklistList.get(checklistIndex);

    List<ChecklistItemModel> checklistItemModelList = container.getChecklistItemModelList();

    ChecklistItemModel removedChecklistItemModel = checklistItemModelList.remove(itemIndex);

    for(int i = itemIndex; i < checklistItemModelList.size(); ++i)
    {
      ChecklistItemModel checklistItemModel = checklistItemModelList.get(i);
      checklistItemModel.setItemIndex(i);

      // Update the database
      FirebaseRealtimeDbProvider.updateChecklistItemWithIndex(activity,
              checklistItemModel.getChecklistItemId(),
              i,
              container.getChecklistModel().getChecklistId(),
              new DatabaseReference.CompletionListener()
      {
        @Override
        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
        {
          if(databaseError == null)
          {
            // write success
            Log.i("CardDetChecklistAdp", "updated the checklist item index in the database");
          }
          else
          {
            // write failure
            Log.i("CardDetailAct", "failed to update the checklist item index in the database");
          }
        }
      });
    }

    FirebaseRealtimeDbProvider.removeChecklistItemFromChecklist(activity,
            removedChecklistItemModel.getChecklistItemId(),
            container.getChecklistModel().getChecklistId(),
            new DatabaseReference.CompletionListener()
    {
      @Override
      public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
      {
        if(databaseError == null)
        {
          // write success
          Log.i("CardDetChecklistAdp", "removed the checklist item from the checklist in the database");
        }
        else
        {
          // write failure
          Log.i("CardDetailAct", "failed to remove the checklist item from the checklist in the database");
        }
      }
    });

    FirebaseRealtimeDbProvider.removeChecklistItem(activity,
            removedChecklistItemModel.getChecklistItemId(),
            new DatabaseReference.CompletionListener()
    {
      @Override
      public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
      {
        if(databaseError == null)
        {
          // write success
          Log.i("CardDetChecklistAdp", "removed the checklist item from the database");
        }
        else
        {
          // write failure
          Log.i("CardDetailAct", "failed to remove the checklist item from the database");
        }
      }
    });

    container.getCardDetailChecklistDropDownAdapter().notifyDataSetChanged();

    TaskMasterUtils.setAppStateDirty(activity);
  }

  class ChecklistCompleteListener implements IEditCompleteListener
  {
    private EditText editText;

    ChecklistCompleteListener(EditText editText)
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

  class ChecklistFocusListener implements View.OnFocusChangeListener
  {
    private EditText editText;
    private CardDetailChecklistAdapterViewHolder holder;

    ChecklistFocusListener(EditText editText, CardDetailChecklistAdapterViewHolder holder)
    {
      this.editText = editText;
      this.holder = holder;
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
                new ChecklistCompleteListener(editText));
      }
      else
      {
        // remove the underline
        TaskMasterUtils.removeEditTextUnderline(editText);

        final boolean hideCommitButton = false;

        checklistItemClickListener.onChecklistItemClick("",
                hideCommitButton,
                null);

        final boolean changed = hasChanged();

        commitUiChanges();

        // update the database too
        if(changed)
        {
          FirebaseDatabase database = FirebaseDatabase.getInstance();
          DatabaseReference rootDatabaseReference = database.getReference();

          ChecklistModel checklistModel = checklistList.get(holder.getAdapterPosition()).getChecklistModel();

          String checkListModelRoot = String.format("/%s/%s/", activity.getString(R.string.db_checklist_object),
                  checklistModel.getChecklistId());

          Map<String, Object> childUpdates = new HashMap<>();

          childUpdates.put(checkListModelRoot + activity.getString(R.string.db_checklist_title_key),
                  checklistModel.getChecklistTitle());

          rootDatabaseReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener()
          {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
            {
              if(databaseError == null)
              {
                // write success
                Log.i("CardDetChecklistAdp", "updated the checklist title in the database");
              }
              else
              {
                // write failure
                Log.i("CardDetChecklistAdp", "failed to update the checklist title in the database");
              }
            }
          });

          TaskMasterUtils.setAppStateDirty(activity);
        }
      }
    }

    private void commitUiChanges()
    {
      String changedText = editText.getText().toString();

      ChecklistModel itemModel = checklistList.get(holder.getAdapterPosition()).getChecklistModel();

      itemModel.setChecklistTitle(changedText);
    }

    private boolean hasChanged()
    {
      String changedText = editText.getText().toString();

      ChecklistModel itemModel = checklistList.get(holder.getAdapterPosition()).getChecklistModel();

      String checklistTitle = itemModel.getChecklistTitle();

      return !checklistTitle.equals(changedText);
    }
  }

  /**
   * See the EditDescriptionTouchListener class description.
   */
  class ChecklistOnTouchListener implements View.OnTouchListener
  {
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
      return TaskMasterUtils.onTouchHelper(activity, v, event);
    }
  }

  class CheckListChevronClickListener implements View.OnClickListener
  {
    private CardDetailChecklistAdapterViewHolder holder;
    private ImageButton chevronButton;
    private RecyclerView recyclerView;

    CheckListChevronClickListener(CardDetailChecklistAdapterViewHolder holder,
                                  ImageButton chevronButton,
                                  RecyclerView recyclerView)
    {
      this.holder = holder;
      this.chevronButton = chevronButton;
      this.recyclerView = recyclerView;
    }

    @Override
    public void onClick(View v)
    {
      if(TaskMasterUtils.isNetworkAvailable(activity))
      {
        // show hide the recycler view for the check list change the chevron button too
        if(recyclerView.getVisibility() == View.GONE)
        {
          recyclerView.setVisibility(View.VISIBLE);
          chevronButton.setImageDrawable(v.getResources().getDrawable(R.drawable.ic_baseline_expand_less_24px));

          checklistList.get(holder.getAdapterPosition()).getChecklistModel().setCollapsed(false);
        }
        else
        {
          recyclerView.setVisibility(View.GONE);
          chevronButton.setImageDrawable(v.getResources().getDrawable(R.drawable.ic_baseline_expand_more_24px));

          checklistList.get(holder.getAdapterPosition()).getChecklistModel().setCollapsed(true);
        }

        // Update the database

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rootDatabaseReference = database.getReference();

        ChecklistModel checklistModel = checklistList.get(holder.getAdapterPosition()).getChecklistModel();

        String checkListModelRoot = String.format("/%s/%s/", activity.getString(R.string.db_checklist_object),
                checklistModel.getChecklistId());

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put(checkListModelRoot + activity.getString(R.string.db_checklist_collapsed_key),
                checklistModel.isCollapsed());

        rootDatabaseReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener()
        {
          @Override
          public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
          {
            if(databaseError == null)
            {
              // write success
              Log.i("CardDetChecklistAdp", "updated the checklist collapse state in the database");
            }
            else
            {
              // write failure
              Log.i("CardDetChecklistAdp", "failed to update the checklist collapse state in the database");
            }
          }
        });
      }
      else
      {
        Toast.makeText(activity,
                activity.getString(R.string.error_network_not_available),
                Toast.LENGTH_LONG).show();
      }
    }
  }

  class ChecklistDeleteChecklistMenuClickListener implements View.OnClickListener
  {
    private CardDetailChecklistAdapterViewHolder holder;

    ChecklistDeleteChecklistMenuClickListener(CardDetailChecklistAdapterViewHolder holder)
    {
      this.holder = holder;
    }

    @Override
    public void onClick(View v)
    {
      PopupMenu popup = new PopupMenu(v.getContext(), v);
      popup.setOnMenuItemClickListener(new ChecklistDeleteChecklistMenuItemClickListener(holder));
      MenuInflater inflater = popup.getMenuInflater();
      inflater.inflate(R.menu.menu_checklist, popup.getMenu());
      popup.show();
    }
  }

  class ChecklistDeleteChecklistMenuItemClickListener implements PopupMenu.OnMenuItemClickListener
  {
    private CardDetailChecklistAdapterViewHolder holder;

    ChecklistDeleteChecklistMenuItemClickListener(CardDetailChecklistAdapterViewHolder holder)
    {
      this.holder = holder;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
      switch(item.getItemId())
      {
        case R.id.action_delete_checklist:
        {
          if(TaskMasterUtils.isNetworkAvailable(activity))
          {
            DeleteChecklistDialogFragment dialogFragment = new DeleteChecklistDialogFragment();

            Bundle bundle = new Bundle();
            bundle.putInt(activity.getString(R.string.check_list_position_key), holder.getAdapterPosition());

            dialogFragment.setArguments(bundle);

            dialogFragment.show(activity.getSupportFragmentManager(),
                    activity.getString(R.string.card_detail_activity_dialog_delete_checklist_tag_string));
          }
          else
          {
            Toast.makeText(activity,
                    activity.getString(R.string.error_network_not_available),
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
