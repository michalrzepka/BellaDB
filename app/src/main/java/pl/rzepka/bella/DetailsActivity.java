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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import pl.rzepka.bella.data.BookContract.BookEntry;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int EXISTING_CATALOG_LOADER = 0;
    private Uri mCurrentProductUri;

    private TextView mProductNameTextView;
    private TextView mPriceTextView;
    private TextView mQuantityTextView;
    private TextView mSupplierNameTextView;
    private TextView mSupplierPhoneTextView;
    private Button mDecreaseQuantityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mProductNameTextView = findViewById(R.id.details_product_name_text_view);
        mPriceTextView = findViewById(R.id.details_price_text_view);
        mQuantityTextView = findViewById(R.id.details_quantity_text_view);
        mSupplierNameTextView = findViewById(R.id.details_supplier_name_text_view);
        mSupplierPhoneTextView = findViewById(R.id.details_supplier_phone_text_view);
        mDecreaseQuantityButton = findViewById(R.id.decrease_quantity_button);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();
        if (mCurrentProductUri == null) {
            invalidateOptionsMenu();
        } else {
            getLoaderManager().initLoader(EXISTING_CATALOG_LOADER, null, this);
        }

    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE
        };
        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {

            final int quantity = cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY));

            String productName = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME));
            String productPrice = "$" + String.format("%.2f", cursor.getDouble(cursor.getColumnIndex(BookEntry.COLUMN_PRICE)));
            String productQuantity = quantity + " in stock";
            String supplierName = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME));
            final String supplierPhone = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE));

            mProductNameTextView.setText(productName);
            mPriceTextView.setText(productPrice);
            mQuantityTextView.setText(productQuantity);
            mSupplierNameTextView.setText(supplierName);
            mSupplierPhoneTextView.setText(supplierPhone);

            if (quantity < 1) {
                mDecreaseQuantityButton.setVisibility(View.GONE);
            }

            Button productDecreaseButton = findViewById(R.id.decrease_quantity_button);
            productDecreaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    decreaseQuantity(quantity);
                }
            });

            Button productIncreaseButton = findViewById(R.id.increase_quantity_button);
            productIncreaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    increaseQuantity(quantity);
                }
            });

            Button productDeleteButton = findViewById(R.id.delete_button);
            productDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmDelete();
                }
            });

            Button orderButton = findViewById(R.id.order_button);
            orderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String phone = String.valueOf(supplierPhone);
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                    startActivity(intent);
                }
            });

        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    public void decreaseQuantity(int productQuantity) {
        productQuantity -= 1;
        if (productQuantity == 0) {
            mDecreaseQuantityButton.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.stock_empty), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.quantity_changed), Toast.LENGTH_SHORT).show();
        }
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_QUANTITY, productQuantity);
        getContentResolver().update(mCurrentProductUri, values, null, null);
    }

    public void increaseQuantity(int productQuantity) {
        if (productQuantity == 0) {
            mDecreaseQuantityButton.setVisibility(View.VISIBLE);
        }
        productQuantity += 1;
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_QUANTITY, productQuantity);
        getContentResolver().update(mCurrentProductUri, values, null, null);
        Toast.makeText(this, getString(R.string.quantity_changed), Toast.LENGTH_SHORT).show();


    }

    private void deleteItem() {
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.error_deleting), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.item_deleted), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void confirmDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_item);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
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

