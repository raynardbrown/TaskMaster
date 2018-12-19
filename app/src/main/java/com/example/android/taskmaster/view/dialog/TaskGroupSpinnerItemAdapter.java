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

public class TaskGroupSpinnerItemAdapter extends ArrayAdapter<TaskGroupSpinnerItem>
{
  TaskGroupSpinnerItemAdapter(@NonNull Context context,
                              int resource,
                              int textViewResourceId,
                              @NonNull List<TaskGroupSpinnerItem> objects)
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
    TaskGroupSpinnerItem taskGroupSpinnerItem = getItem(position);
    if(taskGroupSpinnerItem != null)
    {
      TextView textView = convertView.findViewById(R.id.tv_task_group_spinner_item);
      textView.setText(taskGroupSpinnerItem.getTaskGroupName());

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
    TaskGroupSpinnerItem taskGroupSpinnerItem = getItem(position);
    if(taskGroupSpinnerItem != null)
    {
      TextView textView = convertView.findViewById(R.id.tv_task_group_spinner_item);
      textView.setText(taskGroupSpinnerItem.getTaskGroupName());

      View view = convertView.findViewById(R.id.view_task_group_spinner_item_color_square);
      view.setBackgroundColor(TaskMasterUtils.colorIdToColorValue(getContext(), taskGroupSpinnerItem.getColor()));
      view.setVisibility(View.VISIBLE);
    }

    return convertView;
  }
}
