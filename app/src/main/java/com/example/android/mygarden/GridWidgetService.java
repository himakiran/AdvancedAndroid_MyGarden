package com.example.android.mygarden;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.mygarden.R;
import com.example.android.mygarden.provider.PlantContract;
import com.example.android.mygarden.utils.PlantUtils;

import static com.example.android.mygarden.provider.PlantContract.BASE_CONTENT_URI;
import static com.example.android.mygarden.provider.PlantContract.PATH_PLANTS;

/**
 * Created by userhk on 30/06/17.
 */

public class GridWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GridRemoteViewsFactory(this.getApplicationContext());
    }

}
    class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        Context mContext;
        Cursor mCursor;

        public GridRemoteViewsFactory(Context applicationContext) {
                   mContext = applicationContext;

                        }

        



    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        //Get all plant info ordered by creation time
        Uri PLANT_URI =BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build();
        if(mCursor != null) mCursor.close();
        mCursor = mContext.getContentResolver().query(
            PLANT_URI,
            null,
            null,
            null,
            PlantContract.PlantEntry.COLUMN_CREATION_TIME);
        }



    @Override
    public void onDestroy() {
        mCursor.close();
    }

    @Override
    public int getCount() {
        if(mCursor == null) return 0;
        else {
            return mCursor.getCount();
        }

    }

    @Override
    public RemoteViews getViewAt(int i) {
        if(mCursor == null || mCursor.getCount() == 0) return null;
        else{
            mCursor.moveToPosition(i);
            int idIndex = mCursor.getColumnIndex(PlantContract.PlantEntry._ID);
            int createTimeIndex = mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME);
            int waterTimeIndex = mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME);
            int plantTypeIndex = mCursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE);

            long plantId = mCursor.getLong(idIndex);
            int plantType = mCursor.getInt(plantTypeIndex);
            long createdAt = mCursor.getLong(createTimeIndex);
            long wateredAt = mCursor.getLong(waterTimeIndex);
            long timeNow = System.currentTimeMillis();

            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.plant_widget);
            int imgRes = PlantUtils.getPlantImageRes(mContext,createdAt,wateredAt,plantType);
            views.setImageViewResource(R.id.widget_plant_image,imgRes);
            views.setTextViewText(R.id.widget_plant_name,String.valueOf(plantId));
            views.setViewVisibility(R.id.widget_water_button, View.GONE);
            return views;

        }
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
    }


