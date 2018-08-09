package pl.rzepka.bella;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import static pl.rzepka.bella.data.BookContract.*;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {
        TextView productNameTextView = (TextView) view.findViewById(R.id.product_name_text_view);
        TextView productPriceTextView = (TextView) view.findViewById(R.id.price_text_view);
        TextView productQuantityTextView = (TextView) view.findViewById(R.id.quantity_text_view);

        final String productID = cursor.getString(cursor.getColumnIndex(BookEntry._ID));
        final String quantity =  cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY));
        String productName = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME));
        String productPrice = "$" + String.format("%.2f", cursor.getDouble(cursor.getColumnIndex(BookEntry.COLUMN_PRICE)));
        String productQuantity = cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY)) + " in stock";

        productNameTextView.setText(productName);
        productPriceTextView.setText(productPrice);
        productQuantityTextView.setText(productQuantity);

        Button sellItemButton = view.findViewById(R.id.sell_button);
        sellItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CatalogActivity Activity = (CatalogActivity) context;
                Activity.sellItem(Integer.valueOf(productID), Integer.valueOf(quantity));
            }
        });


        Button editItemButton = view.findViewById(R.id.edit_button);
        editItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), EditorActivity.class);
                Uri selectedItemUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, Long.parseLong(productID));
                intent.setData(selectedItemUri);
                context.startActivity(intent);
            }
        });

    }
}
