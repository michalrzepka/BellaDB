package pl.rzepka.bella;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import java.util.Random;

import pl.rzepka.bella.data.BookDbHelper;
import pl.rzepka.bella.data.BookContract.BookEntry;

public class CatalogActivity extends AppCompatActivity {

    private BookDbHelper mDBHelper;
    private String status = "";
    private TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        mDBHelper = new BookDbHelper(this);

        statusTextView = (TextView) findViewById(R.id.status);

        insertData();
        queryData();
    }

    /*
    Touch screen to insert more dummy data
     */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        status = "";
        insertData();
        queryData();
        return super.onTouchEvent(event);
    }

    private void insertData() {

        String productNameString = "Timequake";
        double priceDouble = 9.99;
        int quantityInteger = new Random().nextInt((99 - 1) + 1) + 1;
        String supplierNameString = "ABC";
        String supplierPhoneString = "212-366-2000";

        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        ContentValues item = new ContentValues();
        item.put(BookEntry.COLUMN_PRODUCT_NAME, productNameString);
        item.put(BookEntry.COLUMN_PRICE, priceDouble);
        item.put(BookEntry.COLUMN_QUANTITY, quantityInteger);
        item.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        item.put(BookEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneString);

        status += "Item parameters: " + item.toString() +"\n\n";
        statusTextView.setText(status);

        long newRowId = db.insert(BookEntry.TABLE_NAME, null, item);

        status += "Item inserted at row: " + newRowId +"\n\n";
        statusTextView.setText(status);

    }

    private Cursor queryData() {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        String[] projection = {BookEntry._ID, BookEntry.COLUMN_PRODUCT_NAME, BookEntry.COLUMN_SUPPLIER_NAME, BookEntry.COLUMN_QUANTITY};
        String selection = BookEntry.COLUMN_SUPPLIER_NAME + "=? AND " + BookEntry.COLUMN_QUANTITY + ">?";
        String[] selectionArgs = {"ABC", "1"};

        Cursor cursor = db.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, BookEntry._ID + " DESC", null);

        try {

            status += "Items found DB: " + cursor.getCount() + "\n\n";
            statusTextView.setText(status);

            cursor.moveToFirst();

            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int supplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);

            int resultId = cursor.getInt(idColumnIndex);
            String resultName = cursor.getString(nameColumnIndex);
            String resultSupplier = cursor.getString(supplierColumnIndex);
            int resultQuantity = cursor.getInt(quantityColumnIndex);

            status += "First found item:\n" +
                    "ID: " + resultId + "\n" +
                    "Name: " + resultName + "\n" +
                    "Supplier: " + resultSupplier + "\n" +
                    "Quantity: " + resultQuantity + "\n";

            statusTextView.setText(status);

        } finally {
            cursor.close();
        }

        return cursor;
    }

 }
