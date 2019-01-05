package com.example.android.taskmaster.view.dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.example.android.taskmaster.R;
import com.example.android.taskmaster.databinding.DialogDueDateBinding;
import com.example.android.taskmaster.utils.TaskMasterConstants;
import com.example.android.taskmaster.utils.TaskMasterUtils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DueDateDialogFragment extends DialogFragment
{
  private static final String DIALOG_FRAGMENT_TAG = "due_date_tag";

  private DialogDueDateBinding binding;
  private IDueDateDialogListener dueDateDialogListener;
  private IDueDateDialogCancelListener dueDateDialogCancelListener;

  private DueDateSpinnerItemAdapter dateAdapter;
  private DueDateSpinnerItemAdapter timeAdapter;

  private List<String> dateList;
  private List<String> timeList;
  private List<String> timeValuesList;

  private Date dueDate;

  private Integer iconColorValue;

  public static class Builder
  {
    private Context context;
    private DueDateDialogFragment fragment;
    private Bundle bundle;

    public Builder(Context context)
    {
      this.context = context;
      fragment = new DueDateDialogFragment();
      bundle = new Bundle();
    }

    public Builder setDueDate(long dueDate)
    {
      bundle.putLong(context.getString(R.string.due_date_key), dueDate);

      return this;
    }

    public Builder setCurrentDueDateDrawableColor(int currentDueDateColorResId)
    {
      bundle.putInt(context.getString(R.string.due_date_current_drawable_color_key),
              currentDueDateColorResId);

      return this;
    }

    public DueDateDialogFragment build()
    {
      fragment.setArguments(bundle);
      return fragment;
    }
  }

  @Override
  public void onAttach(Context context)
  {
    super.onAttach(context);

    try
    {
      dueDateDialogListener = (IDueDateDialogListener) context;
      dueDateDialogCancelListener = (IDueDateDialogCancelListener) context;
    }
    catch(ClassCastException e)
    {
      throw new ClassCastException(context.toString() + " must implement IDueDateDialogListener and dueDateDialogCancelListener");
    }
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    LayoutInflater inflater = getActivity().getLayoutInflater();
    binding = DataBindingUtil.inflate(inflater, R.layout.dialog_due_date, null, false);

    Bundle bundle = getArguments();
    if(bundle != null)
    {
      if(bundle.containsKey(getString(R.string.due_date_key)))
      {
        dueDate = new Date(bundle.getLong(getString(R.string.due_date_key)));
      }

      if(bundle.containsKey(getString(R.string.due_date_current_drawable_color_key)))
      {
        iconColorValue = bundle.getInt(getString(R.string.due_date_current_drawable_color_key));
      }
    }

    setupUi();

    builder.setView(binding.getRoot())
            .setTitle(R.string.card_detail_activity_dialog_due_date_title_string)
            .setPositiveButton(R.string.card_detail_activity_dialog_due_date_done_button_string, new DialogInterface.OnClickListener()
            {
              @Override
              public void onClick(DialogInterface dialog, int which)
              {
                dueDateDialogListener.onDueDateSelectionClick(dateAdapter.getSelectionText(),
                        timeAdapter.getSelectionText());
              }
            })
            .setNegativeButton(R.string.card_detail_activity_dialog_due_date_cancel_button_string, new DialogInterface.OnClickListener()
            {
              @Override
              public void onClick(DialogInterface dialog, int which)
              {
                // Don't forget to restore the icon color if we canceled the operation and the due
                // date object is valid
                if(dueDate != null)
                {
                  dueDateDialogCancelListener.onDueDateDialogCancel(iconColorValue);
                }

                DueDateDialogFragment.this.getDialog().cancel();
              }
            });

    setupSelectionListeners();

    return builder.create();
  }

  public void show(FragmentManager fragmentManager)
  {
    super.show(fragmentManager, DueDateDialogFragment.DIALOG_FRAGMENT_TAG);
  }

  private void setupUi()
  {
    binding.buttonTaskGroupDialogDueDateDelete.setOnClickListener(new DueDateDeleteClickListener());
    binding.clDueDateAddDueDateContainer.setOnClickListener(new AddDueDateClickListener());

    dateList = Arrays.asList(getResources().getStringArray(R.array.due_date_spinner_date_array));

    dateAdapter = new DueDateSpinnerItemAdapter(getContext(),
            R.layout.due_date_spinner_item,
            R.id.tv_due_date_spinner_item_name,
            dateList);

    binding.spinnerTaskGroupDialogDueDateDate.setAdapter(dateAdapter);

    timeList = Arrays.asList(getResources().getStringArray(R.array.due_date_spinner_time_array));
    timeValuesList = Arrays.asList(getResources().getStringArray(R.array.due_date_spinner_time_array_values));

    timeAdapter = new DueDateSpinnerItemAdapter(getContext(),
            R.layout.due_date_spinner_item,
            R.id.tv_due_date_spinner_item_name,
            timeList);


    binding.spinnerTaskGroupDialogDueDateTime.setAdapter(timeAdapter);

    initializeDateAndTimeEntries();
  }

  private void initializeDateAndTimeEntries()
  {
    // Set the date entry
    SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_time_format_abbreviated_month_name_day_year_string));
    if(dueDate != null)
    {
      dateAdapter.setSelectionText(dateFormat.format(dueDate));
    }
    else
    {
      // Today
      dateAdapter.setSelectionText(dateFormat.format(Calendar.getInstance().getTime()));
    }

    // Set the time entry
    if(dueDate != null)
    {
      SimpleDateFormat timeFormat = new SimpleDateFormat(getString(R.string.date_time_format_hour_without_leading_zero_minute_period_string));
      timeAdapter.setSelectionText(timeFormat.format(dueDate));
    }
    else
    {
      timeAdapter.setSelectionText(timeValuesList.get(TaskMasterConstants.DUE_DATE_TIME_MORNING));
    }
  }

  private void setupSelectionListeners()
  {
    binding.spinnerTaskGroupDialogDueDateDate.setOnItemSelectedListener(new DateSelectionListener());
    binding.spinnerTaskGroupDialogDueDateTime.setOnItemSelectedListener(new TimeSelectionListener());
  }

  class DueDateDeleteClickListener implements View.OnClickListener
  {
    @Override
    public void onClick(View v)
    {
      // Clear the both adapters
      dateAdapter.setSelectionText("");
      timeAdapter.setSelectionText("");

      // Hide the main container
      binding.clDueDateMainContainer.setVisibility(View.GONE);

      // Set the default color of the icon
      Drawable drawable = TaskMasterUtils.setDrawableResColorRes(getContext(),
              R.drawable.ic_baseline_schedule_24px,
              R.color.due_date_soon);

      binding.ivDueDateAddDueDate.setImageDrawable(drawable);

      // Show the add due date container
      binding.clDueDateAddDueDateContainer.setVisibility(View.VISIBLE);
    }
  }

  class AddDueDateClickListener implements View.OnClickListener
  {
    @Override
    public void onClick(View v)
    {
      // Hide the add due date container
      binding.clDueDateAddDueDateContainer.setVisibility(View.GONE);

      // Show the main container
      binding.clDueDateMainContainer.setVisibility(View.VISIBLE);

      initializeDateAndTimeEntries();
    }
  }

  class DateSelectionListener implements AdapterView.OnItemSelectedListener
  {
    /**
     * Flag to check if the initial selection has trigger. We want to prevent onItemSelected from
     * executing the first time so that it doesn't wipe the the initial state that we set via
     * Adapter.setSelectionText
     */
    private boolean hasInitialSelectionTriggered;

    DateSelectionListener()
    {
      hasInitialSelectionTriggered = false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
      // clear the selection, so we can select the same item over and over again
      parent.setSelection(dateAdapter.getCount() - 1);

      if(hasInitialSelectionTriggered)
      {
        if(position == (dateList.size() - 1))
        {
          // The selected spinner position is the last item from the date list, so show the date picker
          final Calendar currentDate = Calendar.getInstance();

          DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener()
          {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
              SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_time_format_abbreviated_month_name_day_year_string));

              currentDate.set(Calendar.MONTH, month);
              currentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
              currentDate.set(Calendar.YEAR, year);

              dateAdapter.setSelectionText(dateFormat.format(currentDate.getTime()));
            }
          }, currentDate.get(Calendar.YEAR),
                  currentDate.get(Calendar.MONTH),
                  currentDate.get(Calendar.DAY_OF_MONTH));

          datePickerDialog.setTitle(getString(R.string.card_detail_activity_dialog_due_date_date_picker_title_string));
          datePickerDialog.show();
        }
        else
        {
          SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_time_format_abbreviated_month_name_day_year_string));
          if(position == TaskMasterConstants.DUE_DATE_DATE_TODAY)
          {
            // Today
            dateAdapter.setSelectionText(dateFormat.format(Calendar.getInstance().getTime()));
          }
          else if(position == TaskMasterConstants.DUE_DATE_DATE_TOMORROW)
          {
            // Tomorrow
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, 1);
            dateAdapter.setSelectionText(dateFormat.format(calendar.getTime()));
          }
        }
      }
      else
      {
        hasInitialSelectionTriggered = true;
      }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }
  }

  class TimeSelectionListener implements AdapterView.OnItemSelectedListener
  {
    /**
     * Flag to check if the initial selection has trigger. We want to prevent onItemSelected from
     * executing the first time so that it doesn't wipe the the initial state that we set via
     * Adapter.setSelectionText
     */
    private boolean hasInitialSelectionTriggered;

    TimeSelectionListener()
    {
      hasInitialSelectionTriggered = false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
      // clear the selection, so we can select the same item over and over again
      parent.setSelection(timeAdapter.getCount() - 1);

      if(hasInitialSelectionTriggered)
      {
        if(position == (timeList.size() - 1))
        {
          // Show time picker

          final Calendar currentTime = Calendar.getInstance();
          final boolean is24HourView = false;

          TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener()
          {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute)
            {
              SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_time_format_hour_without_leading_zero_minute_period_string));

              currentTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
              currentTime.set(Calendar.MINUTE, minute);

              timeAdapter.setSelectionText(dateFormat.format(currentTime.getTime()));
            }
          }, currentTime.get(Calendar.HOUR_OF_DAY),
                  currentTime.get(Calendar.MINUTE),
                  is24HourView);

          timePickerDialog.setTitle(getString(R.string.card_detail_activity_dialog_due_date_time_picker_title_string));
          timePickerDialog.show();
        }
        else
        {
          switch(position)
          {
            case TaskMasterConstants.DUE_DATE_TIME_MORNING:
            case TaskMasterConstants.DUE_DATE_TIME_NOON:
            case TaskMasterConstants.DUE_DATE_TIME_NIGHT:
            {
              timeAdapter.setSelectionText(timeValuesList.get(position));
              break;
            }
          }
        }
      }
      else
      {
        hasInitialSelectionTriggered = true;
      }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }
  }
}
