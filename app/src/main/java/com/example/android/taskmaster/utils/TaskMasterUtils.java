package com.example.android.taskmaster.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

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
}
