package com.example.android.taskmaster.view;

import android.databinding.DataBindingUtil;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.ActivityCardDetailBinding;

import java.util.ArrayList;
import java.util.List;

public class CardDetailActivity extends AppCompatActivity
{
  ActivityCardDetailBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_card_detail);

    // set up the toolbar
    setSupportActionBar(binding.tbCardDetailActivity);

    ActionBar actionBar = getSupportActionBar();
    if(actionBar != null)
    {
      actionBar.setDisplayHomeAsUpEnabled(true);
      // TODO: Put the actual title of the card as sent from the intent
      actionBar.setTitle("Card Name");
    }

    // TODO: Testing only
    binding.tvCardDetailActivityDueDate.setText("Due, Monday, June 4, 2018 at 12:00 AM");

    setupRecyclerViews();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_card_detail_activity, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {
      case R.id.action_due_date:
      {
        // TODO: Handle click
        return true;
      }

      case R.id.action_checklist:
      {
        // TODO: Handle click
        return true;
      }

      case R.id.action_attachment:
      {
        // TODO: Handle click
        return true;
      }

      default:
      {
        return super.onOptionsItemSelected(item);
      }
    }
  }

  private void setupRecyclerViews()
  {
    // set up checklist recycler view
    LinearLayoutManager checklistLayoutManager = new LinearLayoutManager(this);
    binding.rvCardDetailActivityChecklists.setLayoutManager(checklistLayoutManager);

    List<String> checklistList = new ArrayList<>();

    // TODO: Dummy data delete this
    checklistList.add("Checklist One");
    checklistList.add("Checklist Two");
    checklistList.add("Checklist Three");
    checklistList.add("Checklist Four");
    checklistList.add("Checklist Five");
    checklistList.add("Checklist Six");

    CardDetailChecklistAdapter checklistAdapter = new CardDetailChecklistAdapter(checklistList);
    binding.rvCardDetailActivityChecklists.setAdapter(checklistAdapter);

    ViewCompat.setNestedScrollingEnabled(binding.rvCardDetailActivityChecklists, false);

    // set up attachment recycler view
    LinearLayoutManager attachmentLayoutManager = new LinearLayoutManager(this);
    binding.rvCardDetailActivityAttachmentAttachments.setLayoutManager(attachmentLayoutManager);

    List<String> attachmentList = new ArrayList<>();

    // TODO: Dummy data delete this
    attachmentList.add("google.com");
    attachmentList.add("apple.com");
    attachmentList.add("microsoft.com");
    attachmentList.add("youtube.com");
    attachmentList.add("amazon.com");

    CardDetailAttachmentAdapter attachmentAdapter = new CardDetailAttachmentAdapter(attachmentList);
    binding.rvCardDetailActivityAttachmentAttachments.setAdapter(attachmentAdapter);
  }
}
