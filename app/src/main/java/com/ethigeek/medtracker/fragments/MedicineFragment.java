package com.ethigeek.medtracker.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ethigeek.medtracker.utils.ReminderUtils;
import com.ethigeek.medtracker.utils.Constants;
import com.ethigeek.medtracker.managers.MedicineManager;
import com.ethigeek.medtracker.R;
import com.ethigeek.medtracker.activities.AddOrUpdateMedicineActivity;
import com.ethigeek.medtracker.activities.MainActivity;
import com.ethigeek.medtracker.adapters.MedicineListAdapter;

import static com.ethigeek.medtracker.utils.Constants.*;


/**
 * A placeholder fragment containing a simple view.
 * @author Ethiraj Srinivasan
 */
public class MedicineFragment extends Fragment {

    private MedicineListAdapter mAdapter;
    private Context context;
    private MedicineManager medicineManager;
    private TextView noMedicine;
    private RecyclerView medicineRecyclerView;

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.medicine_fragment, container, false);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        FloatingActionButton fabSOS = (FloatingActionButton) getActivity().findViewById(R.id.fabSOS);
        TextView tvSOS = (TextView) getActivity().findViewById(R.id.tv_sos);
        fabSOS.setVisibility(View.GONE);
        tvSOS.setVisibility(View.GONE);

        ((MainActivity) getActivity()).setFloatingActionButtonAction(AddOrUpdateMedicineActivity.class);

        context = getActivity().getApplicationContext();
        medicineRecyclerView = (RecyclerView) view.findViewById(R.id.all_medicines_list_view);
        // Set layout for the RecyclerView, because it's a list we are using the linear layout
        medicineRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        noMedicine = (TextView) view.findViewById(R.id.tv_noMedicine);

        // Get all medicine info from the database and save in a cursor
        Cursor cursor = MedicineManager.findAll(context);


        // Create an adapter for that cursor to display the data
        mAdapter = new MedicineListAdapter(context, getActivity(), cursor,medicineRecyclerView,noMedicine);

        // Link the adapter to the RecyclerView
        medicineRecyclerView.setAdapter(mAdapter);
        checkForEmptyList();

        //hide the share button
        setHasOptionsMenu(true);
        getActivity().invalidateOptionsMenu();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //do nothing, we only care about swiping
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //get the id of the item being swiped
                int id = (int) viewHolder.itemView.getTag();
                //remove from DB
                medicineManager = new MedicineManager();
                medicineManager.findById(context, id);
                //update the list
                mAdapter.swapCursor(MedicineManager.findAll(context));

                AlertDialog.Builder warningDialog = new AlertDialog.Builder(getActivity(),R.style.AppTheme_Dialog);
                warningDialog.setTitle(Constants.TITLE_WARNING);
                warningDialog.setMessage(R.string.warning_delete);
                warningDialog.setPositiveButton(Constants.BUTTON_YES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface alert, int which) {
                        //remove from DB
                        new DeleteMedicine().execute();
                        alert.dismiss();
                    }
                });
                warningDialog.setNegativeButton(Constants.BUTTON_NO, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface alert, int which) {
                        alert.dismiss();
                    }
                });
                warningDialog.show();
            }

        }).attachToRecyclerView(medicineRecyclerView);
        getActivity().setTitle(MEDICINES);

        return view;


    }
    private class DeleteMedicine extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return medicineManager.delete(context)==-1;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            ReminderUtils.syncMedicineReminder(context);
            mAdapter.swapCursor(MedicineManager.findAll(context));
            Toast.makeText(context, R.string.delete_success, Toast.LENGTH_SHORT).show();
            checkForEmptyList();
        }
    }
    private void checkForEmptyList(){
        if(mAdapter != null ){
            noMedicine.setVisibility((mAdapter.getItemCount() == 0)? View.VISIBLE : View.GONE);
            medicineRecyclerView.setVisibility((mAdapter.getItemCount() == 0)? View.GONE : View.VISIBLE);
        }
    }
}
