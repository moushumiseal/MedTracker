package com.ethigeek.medtracker.managers;

import android.content.Context;
import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.ethigeek.medtracker.dao.MedicineDAO;
import com.ethigeek.medtracker.dao.MedicineDAOImpl;
import com.ethigeek.medtracker.domain.Category;
import com.ethigeek.medtracker.domain.Consumption;
import com.ethigeek.medtracker.domain.Medicine;
import com.ethigeek.medtracker.domain.Reminder;

import static com.ethigeek.medtracker.utils.Constants.TIME_FORMAT;
import static com.ethigeek.medtracker.daoutils.DBHelper.MEDICINE_KEY_ID;
import static com.ethigeek.medtracker.daoutils.DBHelper.MEDICINE_KEY_REMINDERID;



/**
 * Class for Medicine Manager
 *  Created by ethiraj srinivasan on 12/03/17.
 */
public class MedicineManager {

    private static MedicineDAO medicineAll;
    private MedicineDAO medicineDAO;


    private Medicine medicine;

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }


    public MedicineManager(String name, String description, int categoryId, int reminderId, Boolean remind, int quantity, int dosage, int consumeQuantity, int threshold, String dateIssued, int expireFactor) {

        medicine = new Medicine(name, description, categoryId, reminderId, remind, quantity, dosage, consumeQuantity, threshold, dateIssued, expireFactor);
    }

    public MedicineManager() {

    }

    /**
     * Method for finding all medicine
     * @param context
     * @return
     */
    public static Cursor findAll(Context context) {
        medicineAll = new MedicineDAOImpl(context);
        Cursor cursor = medicineAll.findAll();
        return cursor;
    }

    /**
     * Method to find medicine by id
     * @param context
     * @param id
     * @return
     */
    public Medicine findById(Context context, int id) {
        medicineDAO = new MedicineDAOImpl(context);
        medicine = medicineDAO.findById(id);
        return medicine;
    }

    /**
     * Delete Medicine
     * @param context
     * @return
     */
    public int delete(Context context) {
        medicineDAO = new MedicineDAOImpl(context);
        ReminderManager reminderManager = new ReminderManager();
        reminderManager.setReminder(getReminder(context));
        reminderManager.delete(context);
        ConsumptionManager.deleteAllForMedicine(context, medicine.getId());
        return medicineDAO.delete(medicine.getId());
    }

    /**
     * Save Medicine
     * @param context
     * @return
     */
    public long save(Context context) {
        medicineDAO = new MedicineDAOImpl(context);
        return medicineDAO.insert(medicine);
    }

    /**
     * Update Medicine
     * @param context
     * @return
     */
    public int update(Context context) {
        medicineDAO = new MedicineDAOImpl(context);
        return medicineDAO.update(medicine);
    }

    /**
     * List Medicine
     * @param context
     * @return
     */
    public static Map<Integer, Integer> listAllMedicine(Context context) {
        Cursor cursor = MedicineManager.findAll(context);
        Map<Integer, Integer> medicineHashMap = new HashMap();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            medicineHashMap.put(cursor.getInt(cursor.getColumnIndex(MEDICINE_KEY_ID)), cursor.getInt(cursor.getColumnIndex(MEDICINE_KEY_REMINDERID)));
        }
        return medicineHashMap;
    }


    public int updateReminder(Context context, boolean isChecked) {
        medicineDAO = new MedicineDAOImpl(context);
        medicine.setRemind(isChecked);
        return medicineDAO.update(medicine);

    }

    public Category getCategory(Context context) {

        return new CategoryManager().findById(context, medicine.getCategoryId());
    }

    public static Cursor fetchAllMedicinesWithId(Context context) {
        medicineAll = new MedicineDAOImpl(context);
        return medicineAll.fetchAllMedicinesWithId();
    }

    public Reminder getReminder(Context context) {

        return new ReminderManager().findById(context, medicine.getReminderId());
    }

    public List<Consumption> consumptions(Context context) {

        return ConsumptionManager.findByMedicineID(context, medicine.getId());

    }

    public List<String> findConsumptionTime(Context context, int consumptionMedicine) {

        List<String> timelist = new ArrayList<>();
        medicine = findById(context, consumptionMedicine);
        Reminder reminder = getReminder(context);
        String startTime = reminder.getStartTime();
        int frequency = reminder.getFrequency();
        int interval = reminder.getInterval();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT, Locale.ENGLISH);
        try {
            cal.setTime(sdf.parse(startTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < frequency; i++) {
            cal.add(Calendar.MINUTE, interval * i);
            timelist.add(sdf.format(cal.getTime()));
        }
        return timelist;

    }

    public Medicine fetchMedicineByNameandDateIssued(Context context,String medicineName, String medicineDateIssued) {
        medicineAll = new MedicineDAOImpl(context);
        return medicineAll.fetchMedicineByNameandDateIssued(medicineName,medicineDateIssued);
    }
}
