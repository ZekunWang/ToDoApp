package com.zekunwang.todoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;


public class EditItemActivity extends Activity {

    EditText mdEditText;
    Button btnSaveItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        mdEditText = (EditText) findViewById(R.id.mdEditText);
        btnSaveItem = (Button) findViewById(R.id.btnSaveItem);
        int pos = getIntent().getIntExtra("item", 0);
        mdEditText.setText(MainActivity.todoItems.get(pos));
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSaveItem(View view) {
        int pos = getIntent().getIntExtra("item", 0);
        String s = mdEditText.getText().toString();
        Intent data = new Intent();
        data.putExtra("item", pos);
        data.putExtra("str", s);
        setResult(RESULT_OK, data);
        this.finish();  // back to main activity
    }
}
