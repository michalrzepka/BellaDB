package pl.rzepka.bella;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import pl.rzepka.bella.data.BookContract.BookEntry;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final int EXISTING_CATALOG_LOADER = 0;
    private Uri mCurrentProductUri;

    private TextView mProductNameTextView;
    private TextView mPriceTextView;
    private TextView mQuantityNameTextView;
    private TextView mSupplierNameTextView;
    private TextView mSupplierPhoneNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mProductNameTextView = findViewById(R.id.details_product_name_text_view);
        mPriceTextView = findViewById(R.id.details_price_text_view);
        mQuantityNameTextView = findViewById(R.id.details_quantity_text_view);
        mSupplierNameTextView = findViewById(R.id.details_supplier_name_text_view);
        mSupplierPhoneNameTextView = findViewById(R.id.details_supplier_phone_text_view);

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
        }
    }


    @Override
    public void onLoaderReset(Loader loader) {

    }
}

