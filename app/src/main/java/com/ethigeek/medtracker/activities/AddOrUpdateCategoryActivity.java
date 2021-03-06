package com.ethigeek.medtracker.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


import java.util.Arrays;

import com.ethigeek.medtracker.domain.Category;
import com.ethigeek.medtracker.managers.CategoryManager;
import com.ethigeek.medtracker.R;
import com.ethigeek.medtracker.fragments.CategoryFragment;

import static com.ethigeek.medtracker.utils.Constants.*;

/**
 * Class for add and update category
 * @author Ethiraj Srinivasan
 */
public class AddOrUpdateCategoryActivity extends AppCompatActivity implements View.OnClickListener {

    Button saveButton;
    EditText name, code, description;
    CheckBox reminder;
    private Context context;
    private boolean isUpdate = false;

    private CategoryManager categoryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        context = getApplicationContext();
        findViewsById();
        setListeners();
        Bundle b = getIntent().getExtras();
        if (b != null && b.getString(ACTION).equalsIgnoreCase(EDIT)) {
            isUpdate = true;
            updateSaveButton();
            updateCategoryValues(b.getInt(ID));
            setTitle(EDIT_CATEGORY);
        } else {
            isUpdate = false;
            setTitle(NEW_CATEGORY);
        }

    }

    /**
     *
     * @param id
     */
    private void updateCategoryValues(int id) {

        categoryManager = new CategoryManager();

        Category category = categoryManager.findById(context, id);
        name.setText(category.getCategoryName());
        code.setText(category.getCode().toUpperCase());

        if (Arrays.asList(CategoryManager.safeCategoryCodes).contains(category.getCode())) {

            reminder.setEnabled(false);
            code.setEnabled(false);
        }

        description.setText(category.getDescription());
        reminder.setChecked(category.getRemind());
        name.setTag(id);
    }

    private void updateSaveButton() {

        saveButton.setTag(UPDATE);
        saveButton.setText(UPDATE);
    }

    private void setListeners() {

        saveButton.setOnClickListener(this);
    }

    private void findViewsById() {

        name = (EditText) findViewById(R.id.categoryName);
        code = (EditText) findViewById(R.id.categoryCode);
        description = (EditText) findViewById(R.id.categoryDescription);
        reminder = (CheckBox) findViewById(R.id.categoryReminder);
        saveButton = (Button) findViewById(R.id.saveCategory);
        saveButton.setTag(NEW);
    }

    /**
     * view
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveCategory:
                saveOrUpdateCategory();
                break;

        }
    }

    /**
     * Save or update category
     */
    public void saveOrUpdateCategory() {

        String categoryName = name.getText().toString();
        String categoryCode = code.getText().toString().toUpperCase();
        String categoryDescription = description.getText().toString();
        Boolean categoryReminder = reminder.isChecked();

        if (saveButton.getTag().toString().equalsIgnoreCase(NEW)) {

            categoryManager = new CategoryManager(categoryName, categoryCode, categoryDescription, categoryReminder);

            if (isValid()) {

                new SaveCategory().execute();
            }

        } else {

            categoryManager.getCategory().setRemind(categoryReminder);
            categoryManager.getCategory().setCategoryName(categoryName);
            categoryManager.getCategory().setCode(categoryCode);
            categoryManager.getCategory().setDescription(categoryDescription);
            if (isValid()) {
                new UpdateCategory().execute();
            }
        }
    }

    private class UpdateCategory extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return categoryManager.update(context)==-1;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result){
                Toast.makeText(context, CATEGORY_NOT_UPDATED, Toast.LENGTH_SHORT).show();
            } else {
                navigateToMainAcitivity();
            }

        }
    }


    private class SaveCategory extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return categoryManager.save(context)==-1;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result){
                Toast.makeText(context, CATEGORY_NOT_SAVED, Toast.LENGTH_SHORT).show();
            } else {
                navigateToMainAcitivity();
            }

        }
    }

    public void navigateToMainAcitivity() {
        Intent intent = new Intent(context, MainActivity.class);
        MainActivity.currentFragment = CategoryFragment.class.getName();
        startActivity(intent);
        finish();
    }

    /**
     * validate fields
     * @return
     */
    private boolean isValid() {

        boolean isValid = true;
        if (categoryManager.getCategory().getCategoryName().isEmpty()) {
            name.setError(CATEGORY_NAME_ERROR_MESSAGE);
            name.requestFocus();
            isValid = false;
        } else if (categoryManager.getCategory().getCode().isEmpty()) {
            code.setError(CATEGORY_CODE_ERROR_MESSAGE);
            code.requestFocus();
            isValid = false;
        } else if (categoryManager.getCategory().getDescription().isEmpty()) {
            description.setError(CATEGORY_DESCRIPTION_ERROR_MESSAGE);
            description.requestFocus();
            isValid = false;
        } else {
            isValid = validCategoryDBFields();
        }
        return isValid;
    }

    /**
     * Validate Fields
     * @return
     */
    private boolean validCategoryDBFields() {
        String nameText = categoryManager.getCategory().getCategoryName();
        String codeText = categoryManager.getCategory().getCode();
        Boolean isValid = true;
        String cateGoryName = categoryManager.findByName(context, nameText).getCategoryName();
        String cateGoryCode = categoryManager.findByCode(context, codeText).getCode();

        if (cateGoryName != null && cateGoryName.equals(nameText)) {

            if (isUpdate) {

                if (!cateGoryName.equals(nameText)) {

                    name.setError(CATEGORY_CATEGORY_UNIQUE_ERROR_MESSAGE);
                    name.requestFocus();
                    isValid = false;

                }
            } else {

                name.setError(CATEGORY_CATEGORY_UNIQUE_ERROR_MESSAGE);
                name.requestFocus();
                isValid = false;

            }

        } else if (cateGoryCode != null && cateGoryCode.equals(codeText)) {

            if (isUpdate) {

                if (!cateGoryCode.equals(codeText)) {

                    code.setError(CATEGORY_CODE_UNIQUE_ERROR_MESSAGE);
                    code.requestFocus();
                    isValid = false;
                }
            } else {

                code.setError(CATEGORY_CODE_UNIQUE_ERROR_MESSAGE);
                code.requestFocus();
                isValid = false;
            }
        }
        return isValid;
    }
}
