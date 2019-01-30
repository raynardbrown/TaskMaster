package com.example.android.taskmaster.utils;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.model.DueDateModel;
import com.example.android.taskmaster.model.TaskGroupModel;
import com.example.android.taskmaster.view.widget.TaskMasterWidgetProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

  /**
   * Remove the underline from the specified edit text returning the underline. Clients should
   * cache the drawable in the event that the underline needs to be restored.
   *
   * @param editText the EditText whose underline shall be removed.
   *
   * @return the underline of the specified EditText as a Drawable.
   *
   * @see TaskMasterUtils#restoreEditTextUnderline(EditText, Drawable)
   */
  public static Drawable removeEditTextUnderline(EditText editText)
  {
    Drawable editTextDetailDescriptionBackgroundSave = TaskMasterUtils.retrieveEditTextUnderline(editText);

    editText.setBackgroundResource(android.R.color.transparent);

    return editTextDetailDescriptionBackgroundSave;
  }

  /**
   * Restore the underline of the specified edit text using the specified drawable.
   *
   * @param editText the edit text whose underline shall be restored.
   *
   * @param underline the underline that shall be restore to the specified edit text.
   *
   * @see TaskMasterUtils#removeEditTextUnderline(EditText)
   */
  public static void restoreEditTextUnderline(EditText editText, Drawable underline)
  {
    editText.setBackground(underline);
  }

  public static Drawable retrieveEditTextUnderline(EditText editText)
  {
    return editText.getBackground();
  }

  public static Drawable setDrawableResColorRes(Context context,
                                                @DrawableRes int resId,
                                                @ColorRes int colorResId)
  {
    Drawable drawable = ContextCompat.getDrawable(context, resId).mutate();
    drawable = DrawableCompat.wrap(drawable);
    DrawableCompat.setTint(drawable, context.getResources().getColor(colorResId));

    return drawable;
  }

  public static Drawable setDrawableResColor(Context context,
                                             @DrawableRes int resId,
                                             int colorValue)
  {
    Drawable drawable = ContextCompat.getDrawable(context, resId);
    drawable = DrawableCompat.wrap(drawable);
    DrawableCompat.setTint(drawable, colorValue);

    return drawable;
  }

  public static void setStatusBarColorHelper(Window window, int newColor)
  {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
    {
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(newColor);
    }
  }

  public static boolean isUserLoggedIn(Context context)
  {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    return sharedPreferences.getBoolean(context.getString(R.string.shared_pref_user_logged_in_key), false);
  }

  public static void clearUiDirtyState(Context context)
  {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putBoolean(context.getString(R.string.shared_pref_app_state_changed_key), false);
    editor.apply();
  }

  public static void clearTaskGroupSharedState(Context context)
  {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = sharedPreferences.edit();

    editor.putString(context.getString(R.string.task_group_id_key), "");
    editor.putString(context.getString(R.string.task_group_title_key), "");
    editor.putInt(context.getString(R.string.task_group_color_key), 0);

    editor.apply();
  }

  public static int dpToPixel(Context context, float dp)
  {
    float density = context.getResources().getDisplayMetrics().density;

    return (int)(dp * density);
  }

  public static void notifyWidget(Context con)
  {
    Context context = con.getApplicationContext();

    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, TaskMasterWidgetProvider.class));
    TaskMasterWidgetProvider.updateTaskMasterWidgets(context, appWidgetManager, appWidgetIds);
  }

  public static void notifyWidget(Context con,
                                  List<TaskGroupModel> taskGroupModelList,
                                  String action)
  {
    Context context = con.getApplicationContext();

    Intent broadcastIntent = new Intent(context, TaskMasterWidgetProvider.class);
    broadcastIntent.setAction(action);
    broadcastIntent.putExtra(context.getString(R.string.task_group_model_object_list_key), new ArrayList<>(taskGroupModelList));

    context.sendBroadcast(broadcastIntent);
  }

  public static void startWidgetDueDateFetch(Context con)
  {
    Context context = con.getApplicationContext();

    TaskMasterWidgetProvider.startDueDateFetchService(context);
  }

  public static void startWidgetDueDateDelete(Context con,
                                              String taskListCardId)
  {
    Context context = con.getApplicationContext();

    TaskMasterWidgetProvider.startDeleteDueDateService(context, taskListCardId);
  }

  public static void startWidgetDueDateDeleteAll(Context con)
  {
    Context context = con.getApplicationContext();

    TaskMasterWidgetProvider.startDeleteAllDueDateService(context);
  }

  public static void startWidgetDueDateUpdate(Context con,
                                              String taskListCardId,
                                              String taskListCardTitle,
                                              String taskListCardDetailed,
                                              int taskListCardIndex,
                                              int taskListIndex,
                                              String taskListId,
                                              String taskListTitle,
                                              String taskGroupId,
                                              String taskGroupTitle,
                                              int taskGroupColorKey,
                                              DueDateModel dueDateModel,
                                              List<TaskGroupModel> taskGroupModelList)
  {
    Context context = con.getApplicationContext();

    TaskMasterWidgetProvider.startUpdateDueDateService(context,
            taskListCardId,
            taskListCardTitle,
            taskListCardDetailed,
            taskListCardIndex,
            taskListIndex,
            taskListId,
            taskListTitle,
            taskGroupId,
            taskGroupTitle,
            taskGroupColorKey,
            dueDateModel,
            taskGroupModelList);
  }

  public static int getImageResIdForDueDate(Context context,
                                            Date date)
  {
    // Build the string
    Calendar dueDateAsCalendarObject = Calendar.getInstance();
    dueDateAsCalendarObject.setTime(date);

    // Set up a comparison of the current time against the due date
    int compareDate = Calendar.getInstance().compareTo(dueDateAsCalendarObject);

    // Check the current time against the due date and set the icon color
    int imageResId;

    if(compareDate <= 0)
    {
      // Not due yet

      // determine how close we are to the due date
      Calendar calendar24HoursFromNow = Calendar.getInstance();
      calendar24HoursFromNow.add(Calendar.HOUR_OF_DAY, 24);

      int compareTime = calendar24HoursFromNow.compareTo(dueDateAsCalendarObject);

      if(compareTime <= 0)
      {
        // we have at least 24 hours left
        imageResId = R.drawable.ic_baseline_schedule_24px;
      }
      else
      {
        // we have less than 24 hours left
        imageResId = R.drawable.ic_baseline_schedule_orange_24px;
      }
    }
    else
    {
      // past due

      // set the past due color
      imageResId = R.drawable.ic_baseline_schedule_red_24px;
    }

    return imageResId;
  }

  public static void setAppStateDirty(Context context)
  {
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = sharedPreferences.edit();

    editor.putBoolean(context.getString(R.string.shared_pref_app_state_changed_key), true);
    editor.apply();
  }

  @Nullable
  public static Float getScreenWidthDp(Context context)
  {
    WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);

    if(windowManager != null)
    {
      Display display = windowManager.getDefaultDisplay();
      DisplayMetrics outMetrics = new DisplayMetrics();
      display.getMetrics(outMetrics);

      float density = context.getResources().getDisplayMetrics().density;

      return (outMetrics.widthPixels / density);
    }

    return null;
  }

  public static boolean isNetworkAvailable(Context context)
  {
    ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

    if(connectivityManager != null)
    {
      // the connectivity service exists

      // Requires ACCESS_NETWORK_STATE permission
      NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

      return (networkInfo != null && networkInfo.isConnected());
    }

    return false;
  }

  public static boolean onTouchHelper(Context context, View v, MotionEvent event)
  {
    final int action = event.getAction();

    boolean allowClick = false;

    // Note: We need to handle both action up and action down. If we just handle action up, then
    //       we are left with the click bounding box being displayed on the checkbox or whatever
    //       widget was clicked.
    switch(action)
    {
      case MotionEvent.ACTION_UP:
      {
        if(TaskMasterUtils.isNetworkAvailable(context))
        {
          allowClick = true;
        }

        break;
      }

      case MotionEvent.ACTION_DOWN:
      {
        if(TaskMasterUtils.isNetworkAvailable(context))
        {
          allowClick = true;
        }

        break;
      }
    }

    if(allowClick)
    {
      // allow the click
      return false;
    }
    else
    {
      if(action == MotionEvent.ACTION_UP)
      {
        // only send a toast when we release the press

        Toast.makeText(context,
                context.getString(R.string.error_network_not_available),
                Toast.LENGTH_LONG).show();
      }

      // ignore the click
      return true;
    }
  }
}
