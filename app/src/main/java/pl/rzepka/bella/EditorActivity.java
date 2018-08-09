package pl.rzepka.bella;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import pl.rzepka.bella.data.BookContract;
import pl.rzepka.bella.data.BookContract.BookEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int BOOKS_LOADER = 1;
    private Uri mSelectedItemUri;

    private EditText mProductNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;

    private String mProductName;
    private String mProductPrice;
    private String mProductQuantity;
    private String mSupplierName;
    private String mSupplierPhone;

    private String mNewProductName;
    private String mNewPrice;
    private String mNewQuantity;
    private String mNewSupplierName;
    private String mNewSupplierPhone;

    private boolean mDataChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mSelectedItemUri = intent.getData();

        if (mSelectedItemUri == null) {
            setTitle(R.string.add_item);
        } else {
            setTitle(R.string.edit_item);
            getLoaderManager().initLoader(BOOKS_LOADER, null, this);
        }

        mProductNameEditText = findViewById(R.id.product_name_edit_text);
        mPriceEditText = findViewById(R.id.price_edit_text);
        mQuantityEditText = findViewById(R.id.quantity_edit_text);
        mSupplierNameEditText = findViewById(R.id.supplier_name_edit_text);
        mSupplierPhoneEditText = findViewById(R.id.supplier_phone_edit_text);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveItem();
                return true;
            case android.R.id.home:
            if (dataChanged()) {
                confirmDiscardChanges();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (dataChanged()) {
            confirmDiscardChanges();
        } else {
            finish();
        }
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

        return new CursorLoader(this, mSelectedItemUri, projection,null, null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {

            mProductName = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME));
            mProductPrice = "" + cursor.getDouble(cursor.getColumnIndex(BookEntry.COLUMN_PRICE));
            mProductQuantity = "" + cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY));
            mSupplierName = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME));
            mSupplierPhone = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE));

            mProductNameEditText.setText(mProductName);
            mPriceEditText.setText(mProductPrice);
            mQuantityEditText.setText(mProductQuantity);
            mSupplierNameEditText.setText(mSupplierName);
            mSupplierPhoneEditText.setText(mSupplierPhone);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneEditText.setText("");

    }

    private void saveItem() {
        mNewProductName = mProductNameEditText.getText().toString().trim();
        mNewPrice = mPriceEditText.getText().toString().trim();
        mNewQuantity = mQuantityEditText.getText().toString().trim();
        mNewSupplierName = mSupplierNameEditText.getText().toString().trim();
        mNewSupplierPhone = mSupplierPhoneEditText.getText().toString().trim();

        Double newPriceDouble = 0.0;
        Integer newQuantityInt = 0;

        if (mSelectedItemUri == null) {
            if (TextUtils.isEmpty(mNewProductName)) {
                Toast.makeText(this, getString(R.string.wrong_product_name), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(mNewPrice)) {
                Toast.makeText(this, getString(R.string.wrong_price), Toast.LENGTH_SHORT).show();
                return;
            } else {
                newPriceDouble = Double.valueOf(mPriceEditText.getText().toString().trim());
            }
            if (TextUtils.isEmpty(mNewQuantity)) {
                Toast.makeText(this, getString(R.string.wrong_quantity), Toast.LENGTH_SHORT).show();
                return;
            } else {
                newQuantityInt = Integer.valueOf(mQuantityEditText.getText().toString().trim());
            }
            if (TextUtils.isEmpty(mNewSupplierName)) {
                Toast.makeText(this, getString(R.string.wrong_supplier_name), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(mNewSupplierPhone) || !(BookContract.isValidPhone(mNewSupplierPhone))) {
                Toast.makeText(this, getString(R.string.wrong_supplier_phone), Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues item = new ContentValues();
            item.put(BookEntry.COLUMN_PRODUCT_NAME, mNewProductName);
            item.put(BookEntry.COLUMN_PRICE, newPriceDouble);
            item.put(BookEntry.COLUMN_QUANTITY, newQuantityInt);
            item.put(BookEntry.COLUMN_SUPPLIER_NAME, mNewSupplierName);
            item.put(BookEntry.COLUMN_SUPPLIER_PHONE, mNewSupplierPhone);

            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, item);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.error_adding), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.item_added), Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            if (TextUtils.isEmpty(mNewProductName)) {
                Toast.makeText(this, getString(R.string.wrong_product_name), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(mNewPrice)) {
                Toast.makeText(this, getString(R.string.wrong_price), Toast.LENGTH_SHORT).show();
                return;
            } else {
                newPriceDouble = Double.valueOf(mPriceEditText.getText().toString().trim());
            }
            if (TextUtils.isEmpty(mNewQuantity)) {
                Toast.makeText(this, getString(R.string.wrong_quantity), Toast.LENGTH_SHORT).show();
                return;
            } else {
                newQuantityInt = Integer.valueOf(mQuantityEditText.getText().toString().trim());
            }
            if (TextUtils.isEmpty(mNewSupplierName)) {
                Toast.makeText(this, getString(R.string.wrong_supplier_name), Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(mNewSupplierPhone) || !(BookContract.isValidPhone(mNewSupplierPhone))) {
                Toast.makeText(this, getString(R.string.wrong_supplier_phone), Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues item = new ContentValues();
            item.put(BookEntry.COLUMN_PRODUCT_NAME, mNewProductName);
            item.put(BookEntry.COLUMN_PRICE, newPriceDouble);
            item.put(BookEntry.COLUMN_QUANTITY, newQuantityInt);
            item.put(BookEntry.COLUMN_SUPPLIER_NAME, mNewSupplierName);
            item.put(BookEntry.COLUMN_SUPPLIER_PHONE, mNewSupplierPhone);

            int rowsChanged = getContentResolver().update(mSelectedItemUri, item, null, null);
            if (rowsChanged == 0) {
                Toast.makeText(this, getString(R.string.error_updating), Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, getString(R.string.item_updated), Toast.LENGTH_LONG).show();
                finish();
            }
        }

    }

    public boolean dataChanged() {
        mNewProductName = mProductNameEditText.getText().toString().trim();
        mNewPrice = mPriceEditText.getText().toString().trim();
        mNewQuantity = mQuantityEditText.getText().toString().trim();
        mNewSupplierName = mSupplierNameEditText.getText().toString().trim();
        mNewSupplierPhone = mSupplierPhoneEditText.getText().toString().trim();

        if (mProductName != mNewProductName && mProductPrice != mNewPrice && mProductQuantity != mNewQuantity &&
                mSupplierName != mNewSupplierName && mSupplierPhone != mNewSupplierPhone) {
            mDataChanged = true;
        }

        return mDataChanged;
    }

    private void confirmDiscardChanges() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_changes);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
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
