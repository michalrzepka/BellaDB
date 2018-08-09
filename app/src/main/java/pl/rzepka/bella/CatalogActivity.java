package pl.rzepka.bella;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import pl.rzepka.bella.data.BookContract.BookEntry;


public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int BOOKS_LOADER = 0;

    BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton addFAB = findViewById(R.id.add_fab);
        addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView catalog = (ListView) findViewById(R.id.list);

        TextView emptyView = findViewById(R.id.empty_text_view);
        catalog.setEmptyView(emptyView);

        mCursorAdapter = new BookCursorAdapter(this, null);
        catalog.setAdapter(mCursorAdapter);

        catalog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, final long id) {
                Intent intent = new Intent(CatalogActivity.this, DetailsActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(BOOKS_LOADER, null, this).forceLoad();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE
        };

        return new CursorLoader(this, BookEntry.CONTENT_URI, projection, null, null, "_ID DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        insertData();
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                confirmDeleteAll();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sellItem(int productID, int productQuantity) {
        productQuantity -= 1;
        if (productQuantity >= 0) {
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_QUANTITY, productQuantity);
            Uri updateUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, productID);
            getContentResolver().update(updateUri, values, null, null);
            Toast.makeText(this, getString(R.string.quantity_changed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.stock_empty), Toast.LENGTH_SHORT).show();
        }
    }

    private void insertData() {

        String productNameString = "Item #" + new Random().nextInt((999 - 100) + 1) + 1;
        double price = new Random().nextInt((50 - 1) + 1) + 1 / Math.random();
        double priceDouble = ((double) ((int) (price * 100.0))) / 100.0;
        int quantityInteger = new Random().nextInt((99 - 1) + 1) + 1;
        String supplierNameString = "Supplier No. " + new Random().nextInt((77 - 7) + 1) + 1;
        String supplierPhoneString = "\uD83D\uDCDE " + new Random().nextInt((889999999 - 2010000) + 1) + 1;

        ContentValues item = new ContentValues();
        item.put(BookEntry.COLUMN_PRODUCT_NAME, productNameString);
        item.put(BookEntry.COLUMN_PRICE, priceDouble);
        item.put(BookEntry.COLUMN_QUANTITY, quantityInteger);
        item.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        item.put(BookEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneString);

        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, item);

    }

    private void deleteAllItems() {
        int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
        Toast.makeText(this, getString(R.string.items_deleted), Toast.LENGTH_SHORT).show();

    }

    private void confirmDeleteAll() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_items);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAllItems();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
