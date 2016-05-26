package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * Created by mscndle on 5/24/16.
 */
public class StockDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String STOCK_SYMBOL = "stock_symbol";
    private static final int LOADER_ID = 123456;

    private LineChartView lineChartView;
    private String stockSymbol;

    public static void startWith(Activity origin, @Nullable String stockSymbol) {
        Intent intent = new Intent(origin, StockDetailActivity.class);

        if (stockSymbol != null) {
            intent.putExtra(STOCK_SYMBOL, stockSymbol);
        }

        origin.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_line_graph);

        lineChartView = (LineChartView) findViewById(R.id.linechart);

        // set the detail page title to the stock symbol
        stockSymbol = getIntent().getStringExtra(STOCK_SYMBOL);
        if (stockSymbol != null) {
            setTitle(stockSymbol);
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);    // kick off

        } else {
            Toast.makeText(this, "stock information unavailable at this time. Please try again", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ID) {
            return new CursorLoader(
                    this,                                   // context
                    QuoteProvider.Quotes.CONTENT_URI,       // uri
                    new String[] {                          // projection
                            QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                            QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP
                    },
                    QuoteColumns.SYMBOL + " = ?",           // selection
                        new String[] {                      // selectionArgs
                            stockSymbol
                    },
                    null);                                  // sort order
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            displayChart(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //no-op
    }

    /**
     * Implementation guide - https://github.com/diogobernardino/WilliamChart/wiki/%283%29-Line-Chart
     */
    private void displayChart(Cursor data) {
        LineSet lineSet = new LineSet();
        int total = data.getCount();

        for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
            int bidPriceColIndex = data.getColumnIndexOrThrow(QuoteColumns.BIDPRICE);
            String label = data.getString(bidPriceColIndex);
            float value = data.getFloat(bidPriceColIndex);

            lineSet.addPoint(label, value);
        }

        lineChartView.addData(lineSet);
        lineChartView.show();

        data.close();
    }
}
