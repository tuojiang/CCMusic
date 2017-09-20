package oyh.ccmusic.Provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import oyh.ccmusic.activity.AppliContext;

public class PlayListContentProvider extends ContentProvider {

    private static final String SCHEME="content://";
    private static final String PATH_SONG="/songs";
    public static final String AUTHORITY="oyh.ccmusic.PlayListContentProvider";
    public static final Uri CONTENT_SONGS_URI=Uri.parse(SCHEME+AUTHORITY+PATH_SONG);
    private DBHelper mDbHelper;

    public PlayListContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db=mDbHelper.getWritableDatabase();
        int count=db.delete(DBHelper.PLAYLIST_TABLE_NAME,selection,selectionArgs);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri result=null;
        SQLiteDatabase db=mDbHelper.getWritableDatabase();
        long id=db.insert(DBHelper.PLAYLIST_TABLE_NAME,null,values);
        if (id>0){
            result= ContentUris.withAppendedId(CONTENT_SONGS_URI,id);
        }
        return result;
    }

    @Override
    public boolean onCreate() {
        mDbHelper=new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db=mDbHelper.getWritableDatabase();
        Cursor cursor=db.query(DBHelper.PLAYLIST_TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db=mDbHelper.getWritableDatabase();
        int count = db.update(DBHelper.PLAYLIST_TABLE_NAME,values,selection,selectionArgs);
        return count;
    }
}
