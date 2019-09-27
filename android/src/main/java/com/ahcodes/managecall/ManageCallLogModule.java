package com.ahcodes.managecall;

import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.database.Cursor;
import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

public class ManageCallLogModule extends ReactContextBaseJavaModule {

    private Context context;

    public ManageCallLogModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext;
    }

    @Override
    public String getName() {
        return "ManageCallLogs";
    }

    @ReactMethod
    public void getAll(Promise promise) {
        get(-1, promise);
    }

    @ReactMethod
    public void removeAll(){
        this.context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, null, null);
    }

    @ReactMethod
    public void removeById(String id) {   
        String queryString = "_ID=" + id;
        this.context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, queryString, null);
    }

    @ReactMethod
    public void removeByNumber(String number) {   
        String queryString = "NUMBER=" + number; 
        this.context.getContentResolver().delete(CallLog.Calls.CONTENT_URI, queryString, null);  
    }

    @ReactMethod
    public void get(int limit, Promise promise) {
        Cursor cursor = this.context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                null, null, null, CallLog.Calls.DATE + " DESC");

        WritableArray result = Arguments.createArray();

        if (cursor == null) {
            promise.resolve(result);
            return;
        }

        int callCount = 0;
        final int ID_COLUMN_INDEX = cursor.getColumnIndex(Calls._ID);
        final int NUMBER_COLUMN_INDEX = cursor.getColumnIndex(Calls.NUMBER);
        final int NAME_COLUMN_INDEX = cursor.getColumnIndex(Calls.CACHED_NAME);
        final int TYPE_COLUMN_INDEX = cursor.getColumnIndex(Calls.TYPE);
        final int DATE_COLUMN_INDEX = cursor.getColumnIndex(Calls.DATE);
        final int DURATION_COLUMN_INDEX = cursor.getColumnIndex(Calls.DURATION);
        final int PHOTO_COLUMN_INDEX = cursor.getColumnIndex(Calls.CACHED_PHOTO_URI);

        while (cursor.moveToNext() && this.shouldContinue(limit, callCount++)) {
            String id = cursor.getString(ID_COLUMN_INDEX);
            String phoneNumber = cursor.getString(NUMBER_COLUMN_INDEX);
            String name = cursor.getString(NAME_COLUMN_INDEX);
            String type = this.callType(cursor.getInt(TYPE_COLUMN_INDEX));
            int duration = cursor.getInt(DURATION_COLUMN_INDEX);
            String photo = cursor.getString(PHOTO_COLUMN_INDEX);

            String timestampStr = cursor.getString(DATE_COLUMN_INDEX);
            DateFormat df = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.MEDIUM, SimpleDateFormat.MEDIUM);
            String dateTime = df.format(new Date(Long.valueOf(timestampStr)));

            WritableMap callLog = Arguments.createMap();
            callLog.putString("id", id);
            callLog.putString("phoneNumber", phoneNumber);
            callLog.putString("name", name);
            callLog.putString("type", type);
            callLog.putString("dateTime", dateTime);
            callLog.putInt("duration", duration);
            callLog.putString("timestamp", timestampStr);
            callLog.putString("photo", photo);
            callLog.putInt("rawType", cursor.getInt(TYPE_COLUMN_INDEX));

            result.pushMap(callLog);
        }
        cursor.close();
        promise.resolve(result);
    }

    private String callType(int callTypeCode) {
        switch (callTypeCode) {
            case Calls.OUTGOING_TYPE:
                return "OUTGOING";
            case Calls.INCOMING_TYPE:
                return "INCOMING";
            case Calls.MISSED_TYPE:
                return "MISSED";
            default:
                return "UNKNOWN";
        }
    }

    private boolean shouldContinue(int limit, int count) {
        return limit < 0 || count < limit;
    }
}