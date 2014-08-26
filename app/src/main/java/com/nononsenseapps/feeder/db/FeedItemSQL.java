package com.nononsenseapps.feeder.db;

import android.content.ContentValues;
import android.database.Cursor;

import org.joda.time.DateTime;

/**
 * SQL which handles items belonging to a Feed
 */
public class FeedItemSQL {
    // SQL convention says Table name should be "singular", so not Persons
    public static final String TABLE_NAME = "FeedItem";
    // Naming the id column with an underscore is good to be consistent
    // with other Android things. This is ALWAYS needed
    public static final String COL_ID = "_id";
    // These fields can be anything you want.
    public static final String COL_TITLE = "title";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_PLAINTITLE = "plaintitle";
    public static final String COL_PLAINSNIPPET = "plainsnippet";
    public static final String COL_IMAGEURL = "imageurl";
    public static final String COL_LINK = "link";
    public static final String COL_AUTHOR = "author";
    public static final String COL_PUBDATE = "pubdate";
    public static final String COL_UNREAD = "unread";
    // These fields corresponds to columns in Feed table
    public static final String COL_FEED = "feed";
    public static final String COL_TAG = "tag";

    // For database projection so order is consistent
    public static final String[] FIELDS =
            {COL_ID, COL_TITLE, COL_DESCRIPTION, COL_PLAINTITLE, COL_PLAINSNIPPET, COL_IMAGEURL,
                    COL_LINK, COL_AUTHOR,
                    COL_PUBDATE, COL_UNREAD, COL_FEED, COL_TAG};

    /*
     * The SQL code that creates a Table for storing Persons in.
     * Note that the last row does NOT end in a comma like the others.
     * This is a common source of error.
     */
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COL_ID + " INTEGER PRIMARY KEY,"
                    + COL_TITLE + " TEXT NOT NULL,"
                    + COL_DESCRIPTION + " TEXT NOT NULL,"
                    + COL_PLAINTITLE + " TEXT NOT NULL,"
                    + COL_PLAINSNIPPET + " TEXT NOT NULL," +
                    COL_IMAGEURL + " TEXT," +
                    COL_LINK + " TEXT," +
                    COL_AUTHOR + " TEXT," +
                    COL_PUBDATE + " TEXT," +
                    COL_UNREAD + " INTEGER NOT NULL DEFAULT 1," +
                    COL_FEED + " INTEGER NOT NULL," +
                    COL_TAG + " TEXT," +
                    // Handle foreign key stuff
                    " FOREIGN KEY(" + COL_FEED + ") REFERENCES " + FeedSQL.TABLE_NAME + "(" +
                    FeedSQL.COL_ID + ") ON DELETE CASCADE," +
                    // Handle unique constraint
                    " UNIQUE(" + COL_LINK + "," + COL_FEED + ") ON CONFLICT " +
                    "REPLACE"
                    + ")";

    // Fields corresponding to database columns
    public long id = -1;
    public String title = null;
    public String description = null;
    public String plaintitle = null;
    public String plainsnippet = null;
    public String imageurl = null;
    public String author = null;
    private DateTime pubDate = null;
    public String link = null;
    public String tag = null;

    public boolean isUnread() {
        return unread == 1;
    }

    public void setUnread(boolean unread) {
        this.unread = unread ? 1 : 0;
    }

    private int unread = 1;
    public long feed_id = -1;

    /**
     * No need to do anything, fields are already set to default values above
     */
    public FeedItemSQL() {
    }

    /**
     * Convert information from the database into a Person object.
     */
    public FeedItemSQL(final Cursor cursor) {
        // Indices expected to match order in FIELDS!
        this.id = cursor.getLong(0);
        this.title = cursor.getString(1);
        this.description = cursor.getString(2);
        this.plaintitle = cursor.getString(3);
        this.plainsnippet = cursor.getString(4);
        this.imageurl = cursor.getString(5);
        this.link = cursor.getString(6);
        this.author = cursor.getString(7);
        setPubDate(cursor.getString(8));
       this.unread = cursor.getInt(9);
        this.feed_id = cursor.getLong(10);
        this.tag = cursor.getString(11);
    }

    public DateTime getPubDate() {
        return pubDate;
    }

    public void setPubDate(DateTime datetime) {
        pubDate = datetime;
    }

    /**
     * Output the date time in ISO8601 format (yyyy-MM-ddTHH:mm:ss.SSSZZ).
     *
     * @return ISO8601 time formatted string.
     */
    public String getPubDateString() {
        if (pubDate == null)
            return null;

        return pubDate.toString();
    }

    /**
     * Set the date time in ISO8601 format (yyyy-MM-ddTHH:mm:ss.SSSZZ).
     */
    public void setPubDate(String datetime) {
        if (datetime == null)
            pubDate = null;
        else
            pubDate = DateTime.parse(datetime);
    }

    /**
     * Return the fields in a ContentValues object, suitable for insertion
     * into the database.
     */
    public ContentValues getContent() {
        final ContentValues values = new ContentValues();
        // Note that ID is NOT included here
        values.put(COL_TITLE, title);
        values.put(COL_DESCRIPTION, description);
        values.put(COL_PLAINTITLE, plaintitle);
        values.put(COL_PLAINSNIPPET, plainsnippet);
        values.put(COL_FEED, feed_id);
        values.put(COL_UNREAD, unread);

        Util.PutNullable(values, COL_IMAGEURL, imageurl);
        Util.PutNullable(values, COL_LINK, link);
        Util.PutNullable(values, COL_AUTHOR, author);
        Util.PutNullable(values, COL_PUBDATE, getPubDateString());
        Util.PutNullable(values, COL_TAG, tag);

        return values;
    }
}
