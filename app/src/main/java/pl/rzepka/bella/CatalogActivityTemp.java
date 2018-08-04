package pl.rzepka.bella;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.Random;

import pl.rzepka.bella.data.BookContract.BookEntry;
import pl.rzepka.bella.data.BookDbHelper;

public class CatalogActivityTemp extends AppCompatActivity {

    private BookDbHelper mDBHelper;

    BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        mDBHelper = new BookDbHelper(this);
        insertData();

        Cursor inventory = queryData();

        ListView catalog = (ListView) findViewById(R.id.list);
        mCursorAdapter = new BookCursorAdapter(this, inventory, 0);
        catalog.setAdapter(mCursorAdapter);

        Button editor = findViewById(R.id.editor);
        editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertData();
                insertData();
                insertData();

                Intent intent = new Intent(CatalogActivityTemp.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        Button details = findViewById(R.id.details);
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertData();
                insertData();
                insertData();
                Intent intent = new Intent(CatalogActivityTemp.this, DetailsActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return super.onCreateOptionsMenu(menu);
    }


    /*
    Touch screen to insert more dummy data
     */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        insertData();
        queryData();
        mCursorAdapter.notifyDataSetChanged();
        return super.onTouchEvent(event);
    }

    public String generateTitle() {
        int length = new Random().nextInt((64 - 12) + 1) + 1;
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = length;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();
        return generatedString;
    }

    private void insertData() {


        String productNameString = generateTitle();
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

        long newRowId = db.insert(BookEntry.TABLE_NAME, null, item);

    }

    private Cursor queryData() {

        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        String[] projection = {BookEntry._ID, BookEntry.COLUMN_PRODUCT_NAME, BookEntry.COLUMN_PRICE, BookEntry.COLUMN_SUPPLIER_NAME, BookEntry.COLUMN_QUANTITY};
        String selection = BookEntry.COLUMN_SUPPLIER_NAME + "=? AND " + BookEntry.COLUMN_QUANTITY + ">?";
        String[] selectionArgs = {"ABC", "1"};

        Cursor cursor = db.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, BookEntry._ID + " DESC", null);

        try {

            cursor.moveToFirst();

            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int supplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);

            int resultId = cursor.getInt(idColumnIndex);
            String resultName = cursor.getString(nameColumnIndex);
            String resultSupplier = cursor.getString(supplierColumnIndex);
            int resultQuantity = cursor.getInt(quantityColumnIndex);

        } finally {

        }

        return cursor;
    }

 }
