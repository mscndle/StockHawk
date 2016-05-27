package com.sam_chordas.android.stockhawk.service;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * Created by mandeep.condle on 5/27/16.
 */
public class StockWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data;


            @Override
            public void onCreate() {
                //no-op
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                // https://developer.android.com/reference/android/os/Binder.html#clearCallingIdentity()
                long identityToken = Binder.clearCallingIdentity();

                /** from {@link com.sam_chordas.android.stockhawk.ui.MyStocksActivity} */
                data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                                QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null);

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                // error check
                if (position == AdapterView.INVALID_POSITION || data == null || !data.moveToPosition(position)) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.list_item_quote);
                String stockSymbol = data.getString(data.getColumnIndex(QuoteColumns.SYMBOL));

                views.setTextViewText(R.id.stock_symbol, stockSymbol);
                views.setTextViewText(R.id.bid_price, data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE)));
                views.setTextViewText(R.id.change, data.getString(data.getColumnIndex(QuoteColumns.PERCENT_CHANGE)));

                if (data.getInt(data.getColumnIndex(QuoteColumns.ISUP)) == 1) {
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                } else {
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                }

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position)) {
                    return data.getLong(data.getColumnIndexOrThrow(QuoteColumns._ID));
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
