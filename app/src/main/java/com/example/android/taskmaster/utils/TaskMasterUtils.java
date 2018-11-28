package com.example.android.taskmaster.utils;

import android.content.Context;

import com.example.android.taskmaster.R;

public class TaskMasterUtils
{
  public static int colorIdToColorValue(Context context, int colorId)
  {
    switch(colorId)
    {
      case TaskMasterConstants.RED_BACKGROUND:
      {
        return context.getResources().getColor(R.color.task_red);
      }

      case TaskMasterConstants.GREEN_BACKGROUND:
      {
        return context.getResources().getColor(R.color.task_green);
      }

      case TaskMasterConstants.INDIGO_BACKGROUND:
      {
        return context.getResources().getColor(R.color.task_indigo);
      }

      default:
      {
        return context.getResources().getColor(R.color.task_indigo);
      }
    }
  }
}
