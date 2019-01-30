package com.example.android.taskmaster.view.widget;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class DueDateWidgetContentProvider extends ContentProvider
{
  private DueDateWidgetDbHelper dueDateWidgetDbHelper;

  public static final int DUE_DATE = 100;

  public static final int DUE_DATE_WITH_TASK_LIST_CARD_ID = 101;

  public static final int TASK_GROUP = 200;

  public static final int TASK_GROUP_WITH_TASK_GROUP_ID = 201;

  private static final UriMatcher uriMatcher = buildUriMatcher();

  private static UriMatcher buildUriMatcher()
  {
    UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // match all due dates
    uriMatcher.addURI(DueDateWidgetContract.URI_CONTENT_AUTHORITY,
            DueDateWidgetContract.DueDateWidgetColumns.TABLE_NAME,
            DueDateWidgetContentProvider.DUE_DATE);

    // match a due date with a task list card id (* any string)
    uriMatcher.addURI(DueDateWidgetContract.URI_CONTENT_AUTHORITY,
            DueDateWidgetContract.DueDateWidgetColumns.TABLE_NAME + "/*",
            DueDateWidgetContentProvider.DUE_DATE_WITH_TASK_LIST_CARD_ID);

    // match all task groups
    uriMatcher.addURI(DueDateWidgetContract.URI_CONTENT_AUTHORITY,
            DueDateWidgetContract.DueDateWidgetTaskGroupColumns.TABLE_NAME,
            DueDateWidgetContentProvider.TASK_GROUP);

    // match a task group with a task group id (* any string)
    uriMatcher.addURI(DueDateWidgetContract.URI_CONTENT_AUTHORITY,
            DueDateWidgetContract.DueDateWidgetTaskGroupColumns.TABLE_NAME + "/*",
            DueDateWidgetContentProvider.TASK_GROUP_WITH_TASK_GROUP_ID);

    return uriMatcher;
  }

  @Override
  public boolean onCreate()
  {
    Context context = getContext();

    dueDateWidgetDbHelper = new DueDateWidgetDbHelper(context);

    return true;
  }

  @Nullable
  @Override
  public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                      @Nullable String[] selectionArgs, @Nullable String sortOrder)
  {
    final SQLiteDatabase db = dueDateWidgetDbHelper.getReadableDatabase();

    int match = uriMatcher.match(uri);

    Cursor returnCursor;

    switch(match)
    {
      case DueDateWidgetContentProvider.DUE_DATE:
      {
        // return all the rows in the database.
        returnCursor = db.query(DueDateWidgetContract.DueDateWidgetColumns.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

        break;
      }

      case DueDateWidgetContentProvider.TASK_GROUP:
      {
        returnCursor = db.query(DueDateWidgetContract.DueDateWidgetTaskGroupColumns.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

        break;
      }

      default:
      {
        throw new UnsupportedOperationException("Unknown uri: " + uri);
      }
    }

    return returnCursor;
  }

  @Nullable
  @Override
  public String getType(@NonNull Uri uri)
  {
    // We are not using getType in this app
    throw new UnsupportedOperationException("Unknown uri: " + uri);
  }

  @Nullable
  @Override
  public Uri insert(@NonNull Uri uri, @Nullable ContentValues values)
  {
    final SQLiteDatabase db = dueDateWidgetDbHelper.getWritableDatabase();

    int match = uriMatcher.match(uri);

    Uri returnUri;

    switch(match)
    {
      case DueDateWidgetContentProvider.DUE_DATE:
      {
        long newInsertedRowId = db.insertWithOnConflict(DueDateWidgetContract.DueDateWidgetColumns.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        if(newInsertedRowId != -1)
        {
          returnUri = ContentUris.withAppendedId(DueDateWidgetContract.DueDateWidgetColumns.CONTENT_URI, newInsertedRowId);
        }
        else
        {
          throw new SQLiteException("Failed to insert due date row into " + uri);
        }

        break;
      }

      case DueDateWidgetContentProvider.TASK_GROUP:
      {
        long newInsertedRowId = db.insertWithOnConflict(DueDateWidgetContract.DueDateWidgetTaskGroupColumns.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        if(newInsertedRowId != -1)
        {
          returnUri = ContentUris.withAppendedId(DueDateWidgetContract.DueDateWidgetTaskGroupColumns.CONTENT_URI, newInsertedRowId);
        }
        else
        {
          throw new SQLiteException("Failed to insert task group row into " + uri);
        }

        break;
      }

      default:
      {
        throw new UnsupportedOperationException("Unknown uri: " + uri);
      }
    }

    // notify the content resolver that the uri has changed
    if(getContext() != null)
    {
      getContext().getContentResolver().notifyChange(uri, null);
    }

    return returnUri;
  }

  @Override
  public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs)
  {
    final SQLiteDatabase db = dueDateWidgetDbHelper.getWritableDatabase();

    int match = uriMatcher.match(uri);

    int numberDeleted;

    switch(match)
    {
      case DueDateWidgetContentProvider.DUE_DATE:
      {
        // delete all rows. We must pass "1" in the where clause in order to get a valid value for
        // numberDeleted otherwise 0 is returned.
        numberDeleted = db.delete(DueDateWidgetContract.DueDateWidgetColumns.TABLE_NAME, "1", null);

        break;
      }

      case DueDateWidgetContentProvider.DUE_DATE_WITH_TASK_LIST_CARD_ID:
      {
        String task_list_card_id = uri.getPathSegments().get(1);

        String tempSelection = DueDateWidgetContract.DueDateWidgetColumns.TASK_LIST_CARD_ID + "=?"; // column name
        String[] tempSelectionArgs = new String[]{task_list_card_id}; // column value

        numberDeleted = db.delete(DueDateWidgetContract.DueDateWidgetColumns.TABLE_NAME, tempSelection, tempSelectionArgs);
        break;
      }

      case DueDateWidgetContentProvider.TASK_GROUP:
      {
        // delete all rows. We must pass "1" in the where clause in order to get a valid value for
        // numberDeleted otherwise 0 is returned.
        numberDeleted = db.delete(DueDateWidgetContract.DueDateWidgetTaskGroupColumns.TABLE_NAME, "1", null);

        break;
      }

      case DueDateWidgetContentProvider.TASK_GROUP_WITH_TASK_GROUP_ID:
      {
        String task_group_id = uri.getPathSegments().get(1);

        String tempSelection = DueDateWidgetContract.DueDateWidgetTaskGroupColumns.TASK_GROUP_ID + "=?"; // column name
        String[] tempSelectionArgs = new String[]{task_group_id}; // column value

        numberDeleted = db.delete(DueDateWidgetContract.DueDateWidgetTaskGroupColumns.TABLE_NAME, tempSelection, tempSelectionArgs);
        break;
      }

      default:
      {
        throw new UnsupportedOperationException("Unknown uri: " + uri);
      }
    }

    // notify the content resolver that the uri has changed
    if(getContext() != null && numberDeleted > 0)
    {
      getContext().getContentResolver().notifyChange(uri, null);
    }

    return numberDeleted;
  }

  @Override
  public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                    @Nullable String[] selectionArgs)
  {
    // We are not using update in this app
    throw new UnsupportedOperationException("Unknown uri: " + uri);
  }
}
