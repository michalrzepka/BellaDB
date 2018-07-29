package pl.rzepka.bella;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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

    private void insertData() {

        String productNameString = "name";
        double priceDouble = 99.99;
        int quantityInteger = 1;
        String supplierNameString = "ABC";
        String supplierPhoneString = "212-366-2000";

        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        ContentValues item = new ContentValues();
        item.put(BookEntry.COLUMN_PRODUCT_NAME, productNameString);
        item.put(BookEntry.COLUMN_PRICE, priceDouble);
        item.put(BookEntry.COLUMN_QUANTITY, quantityInteger);
        item.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        item.put(BookEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneString);

        status += "Item parameters: " + item.toString() +"\n";
        statusTextView.setText(status);

        long newRowId = db.insert(BookEntry.TABLE_NAME, null, item);

        status += "Item inserted at row: " + newRowId +"\n";
        statusTextView.setText(status);

    }

    private Cursor queryData() {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        String[] projection = {BookEntry._ID, BookEntry.COLUMN_PRODUCT_NAME, BookEntry.COLUMN_SUPPLIER_NAME, BookEntry.COLUMN_QUANTITY};
        String selection = BookEntry.COLUMN_SUPPLIER_NAME + "=?";
        String[] selectionArgs = {"ABC"};

        Cursor cursor = db.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, BookEntry._ID + " DESC");
        try {

            status += "Items in DB: " + cursor.getCount() + "\n";
            statusTextView.setText(status);
        } finally {
            cursor.close();
        }

        return cursor;
    }

 }
