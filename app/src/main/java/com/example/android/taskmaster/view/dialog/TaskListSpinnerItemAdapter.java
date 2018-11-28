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
import com.example.android.taskmaster.utils.TaskMasterUtils;

import java.util.List;

public class TaskListSpinnerItemAdapter extends ArrayAdapter<TaskListSpinnerItem>
{
  TaskListSpinnerItemAdapter(@NonNull Context context,
                             int resource,
                             int textViewResourceId,
                             @NonNull List<TaskListSpinnerItem> objects)
  {
    super(context, resource, textViewResourceId, objects);
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
  {
    if(convertView == null)
    {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_group_spinner_item, parent, false);
    }

    // initialize views
    TaskListSpinnerItem taskListSpinnerItem = getItem(position);
    if(taskListSpinnerItem != null)
    {
      TextView textView = convertView.findViewById(R.id.tv_task_group_spinner_item);
      textView.setText(taskListSpinnerItem.getTaskListName());

      // remove the square, we only want the text view in the selection box
      // the squares will be shown in the drop down list
      View view = convertView.findViewById(R.id.view_task_group_spinner_item_color_square);

      view.setVisibility(View.GONE);
    }

    return convertView;
  }

  @Override
  public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
  {
    if(convertView == null)
    {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_group_spinner_item, parent, false);
    }

    // initialize views
    TaskListSpinnerItem taskListSpinnerItem = getItem(position);
    if(taskListSpinnerItem != null)
    {
      TextView textView = convertView.findViewById(R.id.tv_task_group_spinner_item);
      textView.setText(taskListSpinnerItem.getTaskListName());

      View view = convertView.findViewById(R.id.view_task_group_spinner_item_color_square);
      view.setBackgroundColor(TaskMasterUtils.colorIdToColorValue(getContext(), taskListSpinnerItem.getColor()));
      view.setVisibility(View.VISIBLE);
    }

    return convertView;
  }
}
