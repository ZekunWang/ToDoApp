package com.zekunwang.todoapp;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TimePicker;

import java.util.Calendar;

//import org.apache.commons.io.FileUtils;
//import java.io.File;
//import java.io.IOException;


public class EditItemActivity extends FragmentActivity
        implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {

    static final String[] ITEMS = {"Low", "Medium", "High"};
    EditText etTitle;
    CheckBox cbCompleted;
    Button btnDueDate;
    Switch swDate;
    Button btnDueTime;
    Switch swTime;
    EditText etContent;
    Spinner spnPriority;
    Item item;
    int year, month, day;
    int hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        etTitle = (EditText) findViewById(R.id.etTitle);
        cbCompleted = (CheckBox) findViewById(R.id.cbCompleted);
        btnDueDate = (Button) findViewById(R.id.btnDueDate);
        swDate = (Switch) findViewById(R.id.swDate);
        btnDueTime = (Button) findViewById(R.id.btnDueTime);
        swTime = (Switch) findViewById(R.id.swTime);
        etContent = (EditText) findViewById(R.id.etContent);
        spnPriority = (Spinner) findViewById(R.id.spnPriority);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnPriority.setAdapter(adapter);

        int pos = getIntent().getIntExtra("position", 0);
        item = MainActivity.todoItems.get(pos);
        etTitle.setText(item.title);
        cbCompleted.setChecked(item.completed);

        year = item.year;
        month = item.month;
        day = item.day;
        if (year == 0) {
            disableDate();
            swDate.setChecked(false);
        } else {
            setDate();
            swDate.setChecked(true);
        }

        hour = item.hour;
        minute = item.minute;
        if (hour == -1) {
            disableTime();
            swTime.setChecked(false);
        } else {
            setTime();
            swTime.setChecked(true);
        }
        etContent.setText(item.content);
        spnPriority.setSelection(item.priority);

        swDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    enableDate();
                } else {
                    swTime.setChecked(false);
                    disableDate();
                }
            }
        });

        swTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swDate.setChecked(true);
                    enableTime();
                } else {
                    disableTime();
                }
            }
        });
    }

    private void setDate() {
        StringBuilder sb = new StringBuilder();
        if (month < 10) {
            sb.append('0');
        }
        sb.append(month).append('/');
        if (day < 10) {
            sb.append('0');
        }
        sb.append(day);
        sb.append('/').append(year);
        btnDueDate.setText(sb.toString());
    }

    private void setTime() {
        StringBuilder sb = new StringBuilder();
        if (hour < 10) {
            sb.append('0');
        }
        sb.append("" + hour).append(" : ");
        if (minute < 10) {
            sb.append('0');
        }
        sb.append("" + minute);
        btnDueTime.setText(sb.toString());
    }

    private void enableDate() {
        if (year == 0) {
            Calendar cal = Calendar.getInstance();
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) + 1;
            day = cal.get(Calendar.DAY_OF_MONTH);
        }
        setDate();
    }

    private void disableDate() {
        btnDueDate.setText("OFF");
    }

    private void enableTime() {
        if (hour < 0) {
            hour = 8;
            minute = 0;
        }
        setTime();
    }

    private void disableTime() {
        btnDueTime.setText("OFF");
    }

    // attach to an onclick handler to show the date picker
    public void showDatePickerDialog(View v) {
        int pos = getIntent().getIntExtra("position", 0);
        DatePickerFragment newFragment = DatePickerFragment.newInstance(pos, year, month - 1, day);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        int pos = getIntent().getIntExtra("position", 0);
        TimePickerFragment newFragment = TimePickerFragment.newInstance(pos, hour, minute);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear + 1;   // read month (0 - 11)
        this.day = dayOfMonth;
        swDate.setChecked(true);
        enableDate();
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.hour = hourOfDay;
        this.minute = minute;
        swTime.setChecked(true);
        enableTime();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.saveItem) {
            onSaveItem(RESULT_OK);
            return true;
        } else if (id == R.id.deleteItem) {
            onDeleteItem();
        }

        return super.onOptionsItemSelected(item);
    }

    private void onDeleteItem() {
        new AlertDialog.Builder(EditItemActivity.this)  // MainActivity.this refers to main activity
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Are you sure?")
                .setMessage("Do you want to delete this note?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onSaveItem(MainActivity.REQUEST_DELETE);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void onSaveItem(final int result) {
        int pos = getIntent().getIntExtra("position", 0);
        item.title = etTitle.getText().toString();
        item.completed = cbCompleted.isChecked();
        if (!swDate.isChecked()) {
            item.year = 0;
            item.month = 0;
            item.day = 0;
        } else {
            item.year = year;
            item.month = month;
            item.day = day;
        }
        if (!swTime.isChecked()) {
            item.hour = -1;
            item.minute = 0;
        } else {
            item.hour = hour;
            item.minute = minute;
        }
        item.content = etContent.getText().toString();
        item.priority = spnPriority.getSelectedItemPosition();
        Intent data = new Intent();
        data.putExtra("position", pos);
        setResult(result, data);
        this.finish();  // back to main activity
    }
}
