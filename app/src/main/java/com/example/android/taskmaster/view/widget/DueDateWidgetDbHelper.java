package com.example.android.taskmaster.view.widget;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DueDateWidgetDbHelper extends SQLiteOpenHelper
{
  private static final String DATABASE_NAME = "due_date_widget.db";

  private static final int DATABASE_VERSION = 1;

  private static final String SQL_CREATE_DUE_WIDGET_TABLE = "CREATE TABLE " +
          DueDateWidgetContract.DueDateWidgetColumns.TABLE_NAME + " (" +
          DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_CARD_ID + " TEXT PRIMARY KEY NOT NULL, " +
          DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_CARD_TITLE + " TEXT NOT NULL, " +
          DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_CARD_DETAILED + " TEXT NOT NULL, " +
          DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_CARD_INDEX + " INTEGER, " +
          DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_INDEX + " INTEGER, " +
          DueDateWidgetContract.DueDateWidgetColumns.TASK_GROUP_ID + " TEXT NOT NULL, " +
          DueDateWidgetContract.DueDateWidgetColumns.TASK_GROUP_TITLE + " TEXT NOT NULL, " +
          DueDateWidgetContract.DueDateWidgetColumns.TASK_GROUP_COLOR_KEY + " INTEGER, " +
          DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_ID + " TEXT NOT NULL, " +
          DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_TITLE + " TEXT NOT NULL, " +
          DueDateWidgetContract.DueDateWidgetColumns.DUE_DATE + " INTEGER" +
          ")";

  private static final String SQL_CREATE_DUE_WIDGET_TASK_GROUP_TABLE = "CREATE TABLE " +
          DueDateWidgetContract.DueDateWidgetTaskGroupColumns.TABLE_NAME + " (" +
          DueDateWidgetContract.DueDateWidgetTaskGroupColumns.TASK_GROUP_ID + " TEXT PRIMARY KEY NOT NULL, " +
          DueDateWidgetContract.DueDateWidgetTaskGroupColumns.TASK_GROUP_TITLE + " TEXT NOT NULL, " +
          DueDateWidgetContract.DueDateWidgetTaskGroupColumns.TASK_GROUP_COLOR_KEY + " INTEGER" +
          ")";

  DueDateWidgetDbHelper(Context context)
  {
    super(context, DueDateWidgetDbHelper.DATABASE_NAME, null, DueDateWidgetDbHelper.DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db)
  {
    db.execSQL(DueDateWidgetDbHelper.SQL_CREATE_DUE_WIDGET_TABLE);
    db.execSQL(DueDateWidgetDbHelper.SQL_CREATE_DUE_WIDGET_TASK_GROUP_TABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  {
    recreateDb(db);
  }

  private void recreateDb(SQLiteDatabase db)
  {
    db.execSQL("DROP TABLE IF EXISTS " + DueDateWidgetContract.DueDateWidgetColumns.TABLE_NAME);
    db.execSQL("DROP TABLE IF EXISTS " + DueDateWidgetContract.DueDateWidgetTaskGroupColumns.TABLE_NAME);
    onCreate(db);
  }
}
