package pl.rzepka.bella;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import static pl.rzepka.bella.data.BookContract.*;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView productNameTextView = (TextView) view.findViewById(R.id.product_name_text_view);
        TextView productPriceTextView = (TextView) view.findViewById(R.id.price_text_view);
        TextView productQuantityTextView = (TextView) view.findViewById(R.id.quantity_text_view);

        String productName = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME));
        String productPrice = "$" + cursor.getDouble(cursor.getColumnIndex(BookEntry.COLUMN_PRICE));
        String productQuantity = cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY)) + " in stock";

        productNameTextView.setText(productName);
        productPriceTextView.setText(productPrice);
        productQuantityTextView.setText(productQuantity);

    }
}
