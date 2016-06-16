package com.zekunwang.todoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
//import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.activeandroid.query.Update;

//import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class MainActivity extends Activity {
    static List<Item> todoItems;
    ArrayAdapter<Item> aToDoAdapter;
    Set<Integer> itemIdSet;
    ListView lvItems;
    EditText etEditText;
    private final int REQUEST_CODE = 20;
    static final int REQUEST_DELETE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        populateArrayItems();
        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(aToDoAdapter);
        etEditText = (EditText) findViewById(R.id.etEditText);
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(MainActivity.this)  // MainActivity.this refers to main activity
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to delete this note?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteItem(position);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switchToEdit(position);
            }
        });
    }

    public void populateArrayItems() {
        readItems();
        itemIdSet = new HashSet<>();
        for (Item i : todoItems) {
            itemIdSet.add(i.itemId);
        }
        aToDoAdapter = new ItemAdapter(this, todoItems);
    }

    private void sortByPriority() {
        Collections.sort(todoItems, new Comparator<Item>() {
            @Override
            public int compare(Item lhs, Item rhs) {
                if (lhs.priority == rhs.priority) {
                    return 0;
                }
                return lhs.priority > rhs.priority ? -1 : 1;
            }
        });
        aToDoAdapter.notifyDataSetChanged();
    }

    private long getTimeStamp(Item item) {
        long res = item.year;
        res = res * 100 + item.month;
        res = res * 100 + item.day;
        res = res * 100 + Math.max(0, item.hour);
        res = res * 100 + item.minute;
        return res != 0 ? res : Long.MAX_VALUE;
    }

    private void sortByDateAndTime() {
        Collections.sort(todoItems, new Comparator<Item>() {
            @Override
            public int compare(Item lhs, Item rhs) {
                long left = getTimeStamp(lhs);
                long right = getTimeStamp(rhs);
                System.out.println("" + lhs.title + ": " + left);
                System.out.println("" + rhs.title + ": " + right);
                if (left == right) {
                    return 0;
                }
                return left < right ? -1 : 1;
            }
        });
        aToDoAdapter.notifyDataSetChanged();
    }

    private void readItems() {
        todoItems = Item.getItems();
    }

    private void updateItem(int pos) {
        Item cur = todoItems.get(pos);
        new Update(Item.class)
                .set("title=?,content=?,priority=?,year=?,month=?,day=?,hour=?,minute=?",
                        cur.title, cur.content, cur.priority, cur.year, cur.month, cur.day, cur.hour, cur.minute)
                .where("item_id = ?", cur.itemId)
                .execute();
    }

    private void deleteItem(int pos) {
        Item cur = todoItems.get(pos);
        aToDoAdapter.remove(cur);
        itemIdSet.remove(cur.itemId);
        cur.delete();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            int pos = data.getIntExtra("position", 0);
            aToDoAdapter.notifyDataSetChanged();
            updateItem(pos);
        } else if (resultCode == REQUEST_DELETE && requestCode == REQUEST_CODE) {
            int pos = data.getIntExtra("position", 0);
            deleteItem(pos);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.addItem:
                createNewItem();
                return true;
            case R.id.sortByPriority:
                sortByPriority();
                return true;
            case R.id.sortByDateAndTime:
                sortByDateAndTime();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createNewItem() {
        Item newItem = getEmptyItem();
        aToDoAdapter.add(newItem);
        newItem.save(); // save newItem to SQLite.
        itemIdSet.add(newItem.itemId);
        switchToEdit(todoItems.size() - 1);
    }

    private void switchToEdit(int pos) {
        Intent i = new Intent(MainActivity.this, EditItemActivity.class);
        i.putExtra("position", pos);
        startActivityForResult(i, REQUEST_CODE);
    }

    private Item getEmptyItem() {
        Item newItem = new Item();
        newItem.itemId = getNewItemId();
        newItem.title = "";
        newItem.content = "";
        newItem.year = 0;
        newItem.month = 0;
        newItem.day = 0;
        newItem.hour = -1;
        newItem.minute = 0;
        newItem.priority = 1;
        return newItem;
    }

    public void onAddItem(View view) {
        Item newItem = getEmptyItem();
        newItem.title = etEditText.getText().toString();
        aToDoAdapter.add(newItem);
        etEditText.setText("");
        newItem.save(); // save newItem to SQLite
        itemIdSet.add(newItem.itemId);
    }

    private int getNewItemId() {
        Random rand = new Random();
        int newItemId = rand.nextInt(Integer.MAX_VALUE);
        if (!itemIdSet.add(newItemId)) {
            newItemId = rand.nextInt(Integer.MAX_VALUE);
        }
        return newItemId;
    }
}
