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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

//import org.apache.commons.io.FileUtils;
//import java.io.File;
//import java.io.IOException;


public class EditItemActivity extends FragmentActivity
        implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {

    static final String[] ITEMS = {"Low", "Medium", "High"};
    //static final String[] DAY_OF_WEEK = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
    //static final int DIAG_ID = 0;
    EditText etTitle;
    EditText etContent;
    Spinner spnPriority;
    Button btnDueDate;
    Button btnDueTime;
    Item item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        etTitle = (EditText) findViewById(R.id.etTitle);
        etContent = (EditText) findViewById(R.id.etContent);
        spnPriority = (Spinner) findViewById(R.id.spnPriority);
        btnDueDate = (Button) findViewById(R.id.btnDueDate);
        btnDueTime = (Button) findViewById(R.id.btnDueTime);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnPriority.setAdapter(adapter);

        int pos = getIntent().getIntExtra("position", 0);
        item = MainActivity.todoItems.get(pos);
        etTitle.setText(item.title);
        etContent.setText(item.content);
        spnPriority.setSelection(item.priority);
        if (item.year == 0) {
            btnDueDate.setText("OFF");
        } else {
            setDate();
        }
        if (item.hour == -1) {
            btnDueTime.setText("OFF");
        } else {
            setTime();
        }
    }

    private void setDate() {
        StringBuilder sb = new StringBuilder();
        if (item.month < 10) {
            sb.append('0');
        }
        sb.append(item.month).append('/');
        if (item.day < 10) {
            sb.append('0');
        }
        sb.append(item.day);
        sb.append('/').append(item.year);
        btnDueDate.setText(sb.toString());
    }

    private void setTime() {
        StringBuilder sb = new StringBuilder();
        if (item.hour < 10) {
            sb.append('0');
        }
        sb.append("" + item.hour).append(" : ");
        if (item.minute < 10) {
            sb.append('0');
        }
        sb.append("" + item.minute);
        btnDueTime.setText(sb.toString());
    }

    // attach to an onclick handler to show the date picker
    public void showDatePickerDialog(View v) {
        int pos = getIntent().getIntExtra("position", 0);
        DatePickerFragment newFragment = DatePickerFragment.newInstance(pos);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        int pos = getIntent().getIntExtra("position", 0);
        TimePickerFragment newFragment = TimePickerFragment.newInstance(pos);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        item.year = year;
        item.month = monthOfYear + 1;   // read month (0 - 11)
        item.day = dayOfMonth;
        setDate();
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        item.hour = hourOfDay;
        item.minute = minute;
        setTime();
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
        item.content = etContent.getText().toString();
        item.priority = spnPriority.getSelectedItemPosition();
        Intent data = new Intent();
        data.putExtra("position", pos);
        setResult(result, data);
        this.finish();  // back to main activity
    }
}
