package com.example.android.taskmaster.view.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.taskmaster.R;

import java.util.List;

public class DueDateSpinnerItemAdapter extends ArrayAdapter<String>
{
  private String selectionText;

  DueDateSpinnerItemAdapter(@NonNull Context context,
                            int resource,
                            int textViewResourceId,
                            @NonNull List<String> objects)
  {
    super(context, resource, textViewResourceId, objects);

    selectionText = "";
  }

  public void setSelectionText(String newSelectedText)
  {
    selectionText = newSelectedText;
    notifyDataSetChanged();
  }

  /**
   * Retrieves the custom text in the spinner view. By default a spinner returns the text from the
   * index of drop down. This adapter allows the user to set a custom value into the view field.
   * As a consequence calling getSelectedItem is not an option since it will return the value
   * specified in the drop down.
   *
   * @return the text that the user selected via the spinner.
   */
  public String getSelectionText()
  {
    return selectionText;
  }

  @Override
  public int getCount()
  {
    // for the hidden item at the end of the list that we are adding
    return super.getCount() + 1;
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
  {
    if(convertView == null)
    {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.due_date_spinner_item, parent, false);
    }

    TextView textView = convertView.findViewById(R.id.tv_due_date_spinner_item_name);
    textView.setText(selectionText);

    return convertView;
  }

  @Override
  public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
  {
    if(convertView == null)
    {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.due_date_spinner_item, parent, false);
    }

    if(position == (getCount() - 1))
    {
      // the hidden item, hide the entire view
      TextView textView = convertView.findViewById(R.id.tv_due_date_spinner_item_name);
      textView.setHeight(0);
      convertView.setVisibility(View.GONE);

    }
    else
    {
      convertView = super.getDropDownView(position, null, parent);

      convertView.setVisibility(View.VISIBLE);
      TextView textView = convertView.findViewById(R.id.tv_due_date_spinner_item_name);
      textView.setText(getItem(position));
    }

    // Disable the scroll bar since it thinks we have more items than we actually do
    parent.setVerticalScrollBarEnabled(false);

    return convertView;
  }
}
