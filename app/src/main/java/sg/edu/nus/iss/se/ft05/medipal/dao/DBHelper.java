package sg.edu.nus.iss.se.ft05.medipal.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Connection;

import java.util.ArrayList;

import sg.edu.nus.iss.se.ft05.medipal.model.Category;

import sg.edu.nus.iss.se.ft05.medipal.constants.DbConstants;


/**
 * Created by ethi on 10/03/17.
 * Updated by Ashish Katre on 13-03-17.
 */

public class DBHelper extends SQLiteOpenHelper {

    protected static final String TABLE_CATEGORY = "categories";
    protected static final String TABLE_MEDICINE = "medicines";
    protected static final String TABLE_REMINDER = "reminders";
    private static final String DATABASE_NAME = "medipal";

    private static final String DATABASE_COMMAND_DROP = "DROP TABLE IF EXISTS ";
    private static final String DATABASE_COMMAND_CREATE = "CREATE TABLE ";
    private static final String DATABASE_COMMAND_LEFT_BRACKET = "(";
    private static final String DATABASE_COMMAND_INTEGER = " INTEGER";
    private static final String DATABASE_COMMAND_INTEGER_COMMA = " INTEGER,";
    private static final String DATABASE_COMMAND_PRIMARY_KEY = " INTEGER,";
    private static final String DATABASE_COMMAND_TEXT = " TEXT,";
    private static final String DATABASE_COMMAND_RIGHT_BRACKET = ")";

    // ICE CONTACTS
    protected static final String TABLE_ICE_CONTACTS = "icecontacts";
    public static final String ICE_CONTACTS_KEY_ID = "id";
    public static final String ICE_CONTACTS_KEY_NAME = "name";
    public static final String ICE_CONTACTS_KEY_DESC = "description";
    public static final String ICE_CONTACTS_KEY_PHONE = "phone";
    public static final String ICE_CONTACTS_KEY_TYPE = "type";
    public static final String ICE_CONTACTS_KEY_PRIORITY = "priority";


    private static final int DATABASE_VERSION = 1;
    public static final String CATEGORY_KEY_ID = "id";
    public static final String CATEGORY_KEY_CATEGORY = "category";
    public static final String CATEGORY_KEY_CODE = "code";
    public static final String CATEGORY_KEY_DESCRIPTION = "description";
    public static final String CATEGORY_KEY_REMIND = "remind";

    public static final String MEDICINE_KEY_ID = "id";
    public static final String MEDICINE_KEY_MEDICINE = "medicine";
    public static final String MEDICINE_KEY_DESCRIPTION = "description";
    public static final String MEDICINE_KEY_CATID = "catId";
    public static final String MEDICINE_KEY_REMINDERID = "reminderId";
    public static final String MEDICINE_KEY_REMIND = "remind";
    public static final String MEDICINE_KEY_QUANTITY = "quantity";
    public static final String MEDICINE_KEY_DOSAGE = "dosage";
    public static final String MEDICINE_KEY_CONSUME_QUALITY = "consumeQuantity";
    public static final String MEDICINE_KEY_THRESHOLD = "threshold";
    public static final String MEDICINE_KEY_DATE_ISSUED = "dateIssued";
    public static final String MEDICINE_KEY_EXPIRE_FACTOR = "expireFactor";
    public static final String REMINDER_KEY_ID = "id";
    public static final String REMINDER_KEY_STARTTIME = "startTime";
    public static final String REMINDER_KEY_FREQUENCY = "frequency";


    public static final String REMINDER_KEY_INTERVAL = "interval";


    // Table Create Statements
    // Todo table create statement
    private static final String CREATE_TABLE_CATEGORY = "CREATE TABLE "
            + TABLE_CATEGORY + "(" + CATEGORY_KEY_ID + " INTEGER PRIMARY KEY," + CATEGORY_KEY_CATEGORY
            + " TEXT UNIQUE," + CATEGORY_KEY_CODE + " TEXT UNIQUE," + CATEGORY_KEY_DESCRIPTION
            + " TEXT," + CATEGORY_KEY_REMIND + " INTEGER DEFAULT 0" + ")";


    // Create table ICE ICEContactsManager
    private static final String CREATE_TABLE_ICE_CONTACTS = DATABASE_COMMAND_CREATE
            + TABLE_ICE_CONTACTS + DATABASE_COMMAND_LEFT_BRACKET + ICE_CONTACTS_KEY_ID + DATABASE_COMMAND_INTEGER + DATABASE_COMMAND_PRIMARY_KEY + ICE_CONTACTS_KEY_NAME
            + DATABASE_COMMAND_TEXT + ICE_CONTACTS_KEY_DESC + DATABASE_COMMAND_TEXT + ICE_CONTACTS_KEY_PHONE
            + DATABASE_COMMAND_INTEGER_COMMA + ICE_CONTACTS_KEY_TYPE + DATABASE_COMMAND_TEXT + ICE_CONTACTS_KEY_PRIORITY + DATABASE_COMMAND_INTEGER + DATABASE_COMMAND_RIGHT_BRACKET;

    private static final String CREATE_TABLE_MEDICINE = "CREATE TABLE "
            + TABLE_MEDICINE + "(" + MEDICINE_KEY_ID + " INTEGER PRIMARY KEY," + MEDICINE_KEY_MEDICINE
            + " TEXT," + MEDICINE_KEY_DESCRIPTION + " TEXT," + MEDICINE_KEY_CATID + " INTEGER,"
            + MEDICINE_KEY_REMINDERID + " INTEGER," + MEDICINE_KEY_REMIND
            + " INTEGER DEFAULT 0," + MEDICINE_KEY_QUANTITY + " INTEGER," + MEDICINE_KEY_DOSAGE + " INTEGER,"
            + MEDICINE_KEY_CONSUME_QUALITY + " INTEGER," + MEDICINE_KEY_THRESHOLD + " INTEGER," + MEDICINE_KEY_DATE_ISSUED + " TEXT," +
            MEDICINE_KEY_EXPIRE_FACTOR + " INTEGER" + ")";
    private static final String CREATE_TABLE_REMINDER = "CREATE TABLE "
            + TABLE_REMINDER + "(" + REMINDER_KEY_ID + " INTEGER PRIMARY KEY," + REMINDER_KEY_FREQUENCY
            + " INTEGER," + REMINDER_KEY_STARTTIME + " TEXT," + REMINDER_KEY_INTERVAL
            + " INTEGER" + ")";

    Connection connection = null;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_CATEGORY);

        db.execSQL(CREATE_TABLE_MEDICINE);
        db.execSQL(CREATE_TABLE_REMINDER);
        insertDefaultValues(db);
        db.execSQL(getCreateTableHealthBioQuery());
        db.execSQL(CREATE_TABLE_ICE_CONTACTS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICINE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDER);
        db.execSQL(DATABASE_COMMAND_DROP + TABLE_ICE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + DbConstants.TABLE_HEALTH_BIO);
        onCreate(db);
    }


    public static void insertDefaultValues(SQLiteDatabase db) {
        ArrayList<Category> categoryList = new ArrayList<Category>();
        categoryList.add(new Category("Supplement", "SUP", "Supplement type of medicines", true));
        categoryList.add(new Category("Chronic", "CHR", "Chronice type of medicines", true));
        categoryList.add(new Category("Incidental", "INC", "Incidental type of medicines", true));
        categoryList.add(new Category("Complete Course", "COM", "Complete type of medicines", true));
        categoryList.add(new Category("Self Apply", "SEL", "Self Apply type of medicines", true));
        for (Category category : categoryList) {
            ContentValues values = new ContentValues();
            values.put(CATEGORY_KEY_CATEGORY, category.getCategoryName());
            values.put(CATEGORY_KEY_CODE, category.getCode());
            values.put(CATEGORY_KEY_DESCRIPTION, category.getDescription());
            values.put(CATEGORY_KEY_REMIND, category.getRemind());
            // insert row
            db.insert(TABLE_CATEGORY, null, values);
        }
    }

    //Creating HealthBio table
    private String getCreateTableHealthBioQuery() {
        final StringBuilder CREATE_TABLE_HEALTHBIO = new StringBuilder()
                .append("CREATE TABLE ")
                .append(DbConstants.TABLE_HEALTH_BIO)
                .append(" (")
                .append(DbConstants.HEALTH_BIO_KEY_ID)
                .append(" INTEGER PRIMARY KEY,")
                .append(DbConstants.HEALTH_BIO_KEY_CONDITION)
                .append(" TEXT,")
                .append(DbConstants.HEALTH_BIO_KEY_CONDITION_TYPE)
                .append(" TEXT,")
                .append(DbConstants.HEALTH_BIO_KEY_START_DATE)
                .append(" TEXT")
                .append(")");
        return CREATE_TABLE_HEALTHBIO.toString();

    }

}