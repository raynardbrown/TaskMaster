package com.example.android.taskmaster.view.widget;

import android.net.Uri;
import android.provider.BaseColumns;

public class DueDateWidgetContract
{
  private static final String URI_SCHEME = "content://";

  public static final String URI_CONTENT_AUTHORITY = "com.example.android.taskmaster";

  private static final Uri BASE_URL = Uri.parse(DueDateWidgetContract.URI_SCHEME + DueDateWidgetContract.URI_CONTENT_AUTHORITY);

  private DueDateWidgetContract()
  {

  }

  public static final class DueDateWidgetColumns implements BaseColumns
  {
    public static final String TABLE_NAME = "due_date_widget";

    /**
     * The unique id for a task list card.
     *
     * <p>Type: TEXT</p>
     */
    public static final String TASK_LIST_CARD_ID = "task_list_card_id";

    /**
     * The title of a task list card.
     *
     * <p>Type: TEXT</p>
     */
    public static final String TASK_LIST_CARD_TITLE = "task_list_card_title";

    /**
     * The detailed description of a task list card.
     *
     * <p>Type: TEXT</p>
     */
    public static final String TASK_LIST_CARD_DETAILED = "task_list_card_detailed";

    /**
     * The position of a task list card within a task list.
     *
     * <p>Type: INTEGER</p>
     */
    public static final String TASK_LIST_CARD_INDEX = "task_list_card_index";

    /**
     * <p>Type: INTEGER</p>
     */
    public static final String TASK_LIST_INDEX = "task_list_index";

    /**
     * The unique id for a task group.
     *
     * <p>Type: TEXT</p>
     */
    public static final String TASK_GROUP_ID = "task_group_id";

    /**
     * The title for a task group.
     *
     * <p>Type: TEXT</p>
     */
    public static final String TASK_GROUP_TITLE = "task_group_title";

    /**
     * The color key for the task group.
     *
     * <p>Type: INTEGER</p>
     */
    public static final String TASK_GROUP_COLOR_KEY = "task_group_color_key";

    /**
     * The unique id for a task list.
     *
     * <p>Type: TEXT</p>
     */
    public static final String TASK_LIST_ID = "task_list_id";

    /**
     * The title for a task list.
     *
     * <p>Type: TEXT</p>
     */
    public static final String TASK_LIST_TITLE = "task_list_title";

    /**
     * The task list card due date.
     *
     * <p>Type: INTEGER</p>
     */
    public static final String DUE_DATE = "due_date";

    /**
     * The content:// style URI for this table.
     */
    public static final Uri CONTENT_URI = DueDateWidgetContract.BASE_URL.buildUpon().appendPath(DueDateWidgetColumns.TABLE_NAME).build();
  }

  public static final class DueDateWidgetTaskGroupColumns implements BaseColumns
  {
    public static final String TABLE_NAME = "due_date_widget_task_group";

    /**
     * The unique id for a task group.
     *
     * <p>Type: TEXT</p>
     */
    public static final String TASK_GROUP_ID = "task_group_id";

    /**
     * The title for a task group.
     *
     * <p>Type: TEXT</p>
     */
    public static final String TASK_GROUP_TITLE = "task_group_title";

    /**
     * The color key for the task group.
     *
     * <p>Type: INTEGER</p>
     */
    public static final String TASK_GROUP_COLOR_KEY = "task_group_color_key";

    /**
     * The content:// style URI for this table.
     */
    public static final Uri CONTENT_URI = DueDateWidgetContract.BASE_URL.buildUpon().appendPath(DueDateWidgetTaskGroupColumns.TABLE_NAME).build();
  }
}
