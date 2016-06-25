package com.intelequia.intelequiawebhooklauncher.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.intelequia.intelequiawebhooklauncher.model.Webhook;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alberto on 25/06/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper sInstance;
    private static final String TAG = "DatabaseHelper";

    // Database Info
    private static final String DATABASE_NAME = "IntelequiaWebhook";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_WEBHOOKS = "Webhooks";

    // Webhooks Table Columns
    private static final String KEY_WEBHOOK_ID = "id";
    private static final String KEY_WEBHOOK_NAME = "name";
    private static final String KEY_WEBHOOK_URL = "url";
    private static final String KEY_WEBHOOK_EXPIRE = "expire";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_WEBHOOKS_TABLE = "CREATE TABLE " + TABLE_WEBHOOKS +
                "(" +
                KEY_WEBHOOK_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_WEBHOOK_NAME + " TEXT," + // Define a foreign key
                KEY_WEBHOOK_URL + " TEXT,"+
                KEY_WEBHOOK_EXPIRE +" TEXT"+
                ")";

        db.execSQL(CREATE_WEBHOOKS_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEBHOOKS);
            onCreate(db);
        }
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }
    public long addOrUpdateWebhook(Webhook webhook) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        long webhookId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_WEBHOOK_NAME, webhook.name);
            values.put(KEY_WEBHOOK_URL, webhook.url);
            values.put(KEY_WEBHOOK_EXPIRE, webhook.expire);

            // First try to update the user in case the user already exists in the database
            // This assumes userNames are unique
            int rows = db.update(TABLE_WEBHOOKS, values, KEY_WEBHOOK_ID + "= ?", new String[]{webhook.id});

            // Check if update succeeded
            if (rows == 1) {
                // Get the primary key of the user we just updated
                String usersSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                        KEY_WEBHOOK_ID, TABLE_WEBHOOKS, KEY_WEBHOOK_ID);
                Cursor cursor = db.rawQuery(usersSelectQuery, new String[]{String.valueOf(webhook.id)});
                try {
                    if (cursor.moveToFirst()) {
                        webhookId = cursor.getInt(0);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                // user with this userName did not already exist, so insert new user
                webhookId = db.insertOrThrow(TABLE_WEBHOOKS, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add or update webhook");
        } finally {
            db.endTransaction();
        }
        return webhookId;
    }

    public List<Webhook> getAllWebhooks() {
        List<Webhook> webhooks = new ArrayList<>();

        // SELECT * FROM WEBHOOKS
        String POSTS_SELECT_QUERY =
                String.format("SELECT * FROM %s",
                        TABLE_WEBHOOKS);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Webhook webhook = new Webhook();
                    webhook.id = cursor.getString(cursor.getColumnIndex(KEY_WEBHOOK_ID));
                    webhook.name = cursor.getString(cursor.getColumnIndex(KEY_WEBHOOK_NAME));
                    webhook.url = cursor.getString(cursor.getColumnIndex(KEY_WEBHOOK_URL));
                    webhook.expire = cursor.getString(cursor.getColumnIndex(KEY_WEBHOOK_EXPIRE));
                    webhooks.add(webhook);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get webhooks from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return webhooks;
    }

    public Webhook getWebhook(String id) {
        Webhook webhook = new Webhook();

        // SELECT * FROM WEBHOOKS
        String POSTS_SELECT_QUERY =
                String.format("SELECT * FROM %s WHERE %s = ?",
                        TABLE_WEBHOOKS,
                        KEY_WEBHOOK_ID);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, new String[]{String.valueOf(id)});
        try {
            if (cursor.moveToFirst()) {
                do {
                    webhook.id = cursor.getString(cursor.getColumnIndex(KEY_WEBHOOK_ID));
                    webhook.name = cursor.getString(cursor.getColumnIndex(KEY_WEBHOOK_NAME));
                    webhook.url = cursor.getString(cursor.getColumnIndex(KEY_WEBHOOK_URL));
                    webhook.expire = cursor.getString(cursor.getColumnIndex(KEY_WEBHOOK_EXPIRE));

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get webhooks from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return webhook;
    }

    public void deleteWebhook(String id) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            db.delete(TABLE_WEBHOOKS, KEY_WEBHOOK_ID + "= ?", new String[]{id});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete " + id + " webhook");
        } finally {
            db.endTransaction();
        }
    }
}
