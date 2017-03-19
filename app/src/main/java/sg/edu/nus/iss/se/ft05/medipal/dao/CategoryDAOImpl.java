package sg.edu.nus.iss.se.ft05.medipal.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import sg.edu.nus.iss.se.ft05.medipal.model.Category;

/**
 * Created by ethi on 10/03/17.
 */

public class CategoryDAOImpl extends DBHelper implements CategoryDAO {

    public CategoryDAOImpl(Context context) {
        super(context);
    }

    @Override
    public int delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CATEGORY, CATEGORY_KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    @Override
    public Cursor findAll() {
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);


    }

    @Override
    public Category findByField(String column,Object value) {
        SQLiteDatabase db = this.getReadableDatabase();
        if(column == CATEGORY_KEY_CATEGORY || column == CATEGORY_KEY_CODE) {
            value = "'" + value + "'";
        }

        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY + " WHERE "
                + column + " = " + value;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null) {
            c.moveToFirst();
        }
        Category category = new Category();
        if (c.getCount() > 0 ){
            category.setId(c.getInt(c.getColumnIndex(CATEGORY_KEY_ID)));
            category.setCategoryName((c.getString(c.getColumnIndex(CATEGORY_KEY_CATEGORY))));
            category.setCode(c.getString(c.getColumnIndex(CATEGORY_KEY_CODE)));
            category.setDescription(c.getString(c.getColumnIndex(CATEGORY_KEY_DESCRIPTION)));
            category.setRemind(c.getInt(c.getColumnIndex(CATEGORY_KEY_REMIND)) == 1);
        }
        return category;
    }

    /*
     * Creating a category
     */
    @Override
    public long insert(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CATEGORY_KEY_CATEGORY, category.getCategoryName());
        values.put(CATEGORY_KEY_CODE, category.getCode());
        values.put(CATEGORY_KEY_DESCRIPTION, category.getDescription());
        values.put(CATEGORY_KEY_REMIND, category.getRemind());

        // insert row
        return db.insert(TABLE_CATEGORY, null, values);
    }

    @Override
    public int update(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CATEGORY_KEY_CATEGORY, category.getCategoryName());
        values.put(CATEGORY_KEY_CODE, category.getCode());
        values.put(CATEGORY_KEY_DESCRIPTION, category.getDescription());
        values.put(CATEGORY_KEY_REMIND, category.getRemind());
        // updating row
        return db.update(TABLE_CATEGORY, values, CATEGORY_KEY_ID + " = ?",
                new String[]{String.valueOf(category.getId())});
    }


    public Cursor fetchAllCategoriesWithId() {
        String selectQuery = "SELECT  id,category FROM " + TABLE_CATEGORY;
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(selectQuery, null);

    }

}