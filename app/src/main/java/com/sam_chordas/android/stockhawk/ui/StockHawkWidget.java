package com.sam_chordas.android.stockhawk.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.service.StockWidgetRemoteViewsService;

/**
 * Created by mandeep.condle on 5/27/16.
 *
 * ref: http://www.vogella.com/tutorials/AndroidWidgets/article.html
 *
 */
public class StockHawkWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stock_widget_layout);

            Intent intent = new Intent(context, MyStocksActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.stock_widget_header, pendingIntent);

            views.setRemoteAdapter(R.id.stock_widget_list, new Intent(context, StockWidgetRemoteViewsService.class));

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
