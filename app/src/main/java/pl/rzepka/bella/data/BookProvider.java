package pl.rzepka.bella.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import pl.rzepka.bella.R;
import pl.rzepka.bella.data.BookContract.BookEntry;


public class BookProvider extends ContentProvider {

    public static final int BOOKS = 100;
    public static final int BOOKS_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOKS_ID);
    }

    private BookDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return false;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOKS_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Error matching URI: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = db.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOKS_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Error querying URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Error inserting URI: " + uri);
        }
    }

    private Uri insertBook(Uri uri, ContentValues values) {
        String productName = values.getAsString(BookEntry.COLUMN_PRODUCT_NAME);
        if (productName == null) {
            throw new IllegalArgumentException("Please provide valid product name");
        }

        Double productPrice = values.getAsDouble(BookEntry.COLUMN_PRICE);
        if (productPrice != null && productPrice < 0) {
            throw new IllegalArgumentException("Please provide valid product price");
        }

        Integer productQuantity = values.getAsInteger(BookEntry.COLUMN_QUANTITY);
        if (productQuantity != null && productQuantity < 0) {
            throw new IllegalArgumentException("Please provide valid product quantity");
        }

        String supplierName = values.getAsString(BookEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Please provide valid supplier name (max 60 characters)");
        }

        String supplierPhone = values.getAsString(BookEntry.COLUMN_SUPPLIER_PHONE);
        if (supplierPhone != null && !(BookContract.isValidPhone(supplierPhone))) {
            Log.v("phone",supplierPhone);
            throw new IllegalArgumentException("Please provide valid supplier phone (numbers only)");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(BookEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.v("Error", "Error inserting with URI: " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOKS_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Error updating with URI: " + uri);
        }
    }

    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(BookEntry.COLUMN_PRODUCT_NAME)) {
            String productName = values.getAsString(BookEntry.COLUMN_PRODUCT_NAME);
            if (productName == null) {
                throw new IllegalArgumentException(Resources.getSystem().getString(R.string.wrong_product_name));
            }
        }

        if (values.containsKey(BookEntry.COLUMN_PRICE)) {
            Double productPrice = values.getAsDouble(BookEntry.COLUMN_PRICE);
            if (productPrice != null && productPrice < 0) {
                throw new IllegalArgumentException(Resources.getSystem().getString(R.string.wrong_price));
            }
        }

        if (values.containsKey(BookEntry.COLUMN_QUANTITY)) {
            Integer productQuantity = values.getAsInteger(BookEntry.COLUMN_QUANTITY);
            if (productQuantity != null && productQuantity < 0) {
                throw new IllegalArgumentException(Resources.getSystem().getString(R.string.wrong_quantity));
            }
        }

        if (values.containsKey(BookEntry.COLUMN_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(BookEntry.COLUMN_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException(Resources.getSystem().getString(R.string.wrong_supplier_name));
            }
        }

        if (values.containsKey(BookEntry.COLUMN_SUPPLIER_PHONE)) {
            String supplierPhone = values.getAsString(BookEntry.COLUMN_SUPPLIER_PHONE);
            if (supplierPhone == null || !(BookContract.isValidPhone(supplierPhone))) {
                throw new IllegalArgumentException(Resources.getSystem().getString(R.string.wrong_supplier_phone));
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                rowsDeleted = db.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOKS_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Error deleting with URI: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }
}
