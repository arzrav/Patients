package com.neurofrank.portfolio.patient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.provider.DocumentFile;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.neurofrank.portfolio.DeleteDialogFragment;
import com.neurofrank.portfolio.R;
import com.neurofrank.portfolio.database.PortfolioContract.PatientTable;
import com.neurofrank.portfolio.database.PortfolioContract.ReceptionTable;
import com.neurofrank.portfolio.tag.TagNewDialogFragment;
import com.neurofrank.portfolio.tag.TagRemoveDialogFragment;
import com.neurofrank.portfolio.tag.TagsSelectDialogFragment;
import com.neurofrank.portfolio.util.FileOperation;
import com.neurofrank.portfolio.util.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import static com.neurofrank.portfolio.util.Constants.APP_PREFERENCES_COMPLETED_FIRST_LAUNCH;
import static com.neurofrank.portfolio.util.Constants.APP_PREFERENCES_PATH_TO_DATA;
import static com.neurofrank.portfolio.util.Constants.APP_STORAGE_NAME;
import static com.neurofrank.portfolio.util.Constants.DIALOG_DELETE_PATIENT;
import static com.neurofrank.portfolio.util.Constants.DIALOG_NEW_TAG;
import static com.neurofrank.portfolio.util.Constants.DIALOG_REMOVE_TAG;
import static com.neurofrank.portfolio.util.Constants.DIALOG_SELECT_TAGS;
import static com.neurofrank.portfolio.util.Constants.EXTRA_AND_TAGS_CONDITION;
import static com.neurofrank.portfolio.util.Constants.EXTRA_TAGS;
import static com.neurofrank.portfolio.util.Constants.REQUEST_CODE_DELETE_PATIENT;
import static com.neurofrank.portfolio.util.Constants.REQUEST_CODE_EDIT_PATIENT;
import static com.neurofrank.portfolio.util.Constants.REQUEST_CODE_PATH_TO_DATA;
import static com.neurofrank.portfolio.util.Constants.REQUEST_CODE_SELECT_TAGS;


public class PatientListFragment extends Fragment {

    private RecyclerView mPatientRecyclerView;
    private PatientAdapter mAdapter;
    private List<Patient> mPatients;

    private static ArrayList<String> selectedTags = new ArrayList<>();

    private static boolean andTagsCondition = false;
    private static boolean isSortedByName = false;
    private static boolean isSortedByNumber = false;

    private static String table = null;
    private static String[] columns = null;
    private static String whereClause = null;
    private static String[] whereArgs = null;
    private static String groupBy = null;
    private static String having = null;
    private static String orderBy = PatientTable.COLUMN_LAST_NAME;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (!sharedPreferences.getBoolean(APP_PREFERENCES_COMPLETED_FIRST_LAUNCH, false)) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            intent.putExtra("android.content.extra.SHOW_ADVANCED", true);
            startActivityForResult(intent, REQUEST_CODE_PATH_TO_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient_list, container, false);
        mPatientRecyclerView = (RecyclerView) view.findViewById(R.id.patient_recycler_view);
        mPatientRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPatientRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        updateUI();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_EDIT_PATIENT) {
            if (data == null) {
                return;
            }
            String mPatientAction = PatientEditActivity.whatPatientAction(data);
            switch (mPatientAction) {
                case "created":
                    Toast toastPatientCreated = Toast.makeText(getContext(), R.string.alert_patient_list_new_patient_created, Toast.LENGTH_LONG);
                    toastPatientCreated.setGravity(Gravity.CENTER, 0, 0);
                    toastPatientCreated.show();
                    String mPatientId = PatientEditActivity.getExtraPatientId(data);
                    DocumentFile storageDir = FileOperation.GetStorageDirectory(getActivity());
                    FileOperation.CreateDirectory(storageDir, "Patient{" + mPatientId + "}");
                    break;
                case "changed":
                    Toast toastPatientChanged = Toast.makeText(getContext(), R.string.alert_patient_list_patient_changed, Toast.LENGTH_LONG);
                    toastPatientChanged.setGravity(Gravity.CENTER, 0, 0);
                    toastPatientChanged.show();
                    break;
                default:
                    break;
            }
        }

        if (requestCode == REQUEST_CODE_DELETE_PATIENT) {
            updateUI();
        }

        if (requestCode == REQUEST_CODE_SELECT_TAGS) {
            selectedTags = (ArrayList<String>) data.getSerializableExtra(EXTRA_TAGS);
            andTagsCondition = (Boolean) data.getSerializableExtra(EXTRA_AND_TAGS_CONDITION);

            if (selectedTags.size() > 0 && !andTagsCondition) {
                table = PatientTable.TABLE_NAME + " p INNER JOIN " + ReceptionTable.TABLE_NAME + " r ON p." + PatientTable.COLUMN_UUID + " = r." + ReceptionTable.COLUMN_PATIENT_UUID;
                columns = new String[] {"DISTINCT p." + PatientTable.COLUMN_UUID, "p." + PatientTable.COLUMN_FIRST_NAME, "p." + PatientTable.COLUMN_MIDDLE_NAME, "p." + PatientTable.COLUMN_LAST_NAME, "p." + PatientTable.COLUMN_BIRTHDAY, "p." + PatientTable.COLUMN_MOBILE_PHONE, "p." + PatientTable.COLUMN_EMAIL, "p." + PatientTable.COLUMN_HISTORY};
                whereClause = ReceptionTable.COLUMN_TITLE + " = ?";
                for (int i = 0; i < selectedTags.size() - 1; i++) {
                    whereClause = whereClause + " OR " + ReceptionTable.COLUMN_TITLE + " = ?";
                }
                whereArgs = new String[selectedTags.size()];
                for (int i = 0; i < selectedTags.size(); i++) {
                    whereArgs[i] = selectedTags.get(i);
                }
                groupBy = null;
                having = null;
            } else if (selectedTags.size() > 0 && andTagsCondition) {
                table = " FROM " + PatientTable.TABLE_NAME + " p INNER JOIN " + ReceptionTable.TABLE_NAME + " r ON p." + PatientTable.COLUMN_UUID + " = r." + ReceptionTable.COLUMN_PATIENT_UUID;
                columns = new String[] {"p." + PatientTable.COLUMN_UUID, "p." + PatientTable.COLUMN_FIRST_NAME, "p." + PatientTable.COLUMN_MIDDLE_NAME, "p." + PatientTable.COLUMN_LAST_NAME, "p." + PatientTable.COLUMN_BIRTHDAY, "p." + PatientTable.COLUMN_MOBILE_PHONE, "p." + PatientTable.COLUMN_EMAIL, "p." + PatientTable.COLUMN_HISTORY};
                whereClause = " WHERE ";
                for (int i = 0; i < selectedTags.size(); i++) {
                    whereClause = whereClause  + ReceptionTable.COLUMN_TITLE + " = " + "\"" + selectedTags.get(i) + "\"" + " OR ";
                }
                groupBy = " GROUP BY " + "p." + PatientTable.COLUMN_UUID + ", r." + ReceptionTable.COLUMN_TITLE + ") g";
                String subquerySQL = "(SELECT ";
                for (String column : columns) {
                    subquerySQL = subquerySQL + column + ", ";
                }
                subquerySQL = subquerySQL.substring(0, subquerySQL.length() - 2) + table + whereClause;
                subquerySQL = subquerySQL.substring(0, subquerySQL.length() - 4) + groupBy;
                table = subquerySQL;
                columns = null;
                whereClause = null;
                whereArgs = null;
                groupBy = "g." + PatientTable.COLUMN_UUID;
                having = "COUNT(*) = " + selectedTags.size();
            } else {
                table = null;
                columns = null;
                whereClause = null;
                whereArgs = null;
                groupBy = null;
                having = null;
            }

            updateUI();
        }

        if (requestCode == REQUEST_CODE_PATH_TO_DATA) {
            if (data == null) {
                return;
            }
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
            editor.putBoolean(APP_PREFERENCES_COMPLETED_FIRST_LAUNCH, true);
            DocumentFile pickedDir = DocumentFile.fromTreeUri(getActivity(), data.getData());
            getActivity().grantUriPermission(getActivity().getPackageName(), data.getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getActivity().getContentResolver().takePersistableUriPermission(data.getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            FileOperation.CreateDirectory(pickedDir, APP_STORAGE_NAME);
            editor.putString(APP_PREFERENCES_PATH_TO_DATA, data.getData().toString());
            editor.apply();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_patient_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager manager = getFragmentManager();

        switch (item.getItemId()) {
            case R.id.menu_item_new_patient:
                Patient patient = new Patient();
                PatientLab.get(getActivity()).addPatient(patient);
                Intent intent = PatientEditActivity.newIntent(getActivity(), patient.getId(), item.getItemId());
                startActivityForResult(intent, REQUEST_CODE_EDIT_PATIENT);
                return true;
            case R.id.menu_item_new_tag:
                TagNewDialogFragment tagNewDialogFragment = new TagNewDialogFragment();
                tagNewDialogFragment.show(manager, DIALOG_NEW_TAG);
                return true;
            case R.id.menu_item_remove_tag:
                TagRemoveDialogFragment tagRemoveDialogFragment = new TagRemoveDialogFragment();
                tagRemoveDialogFragment.show(manager, DIALOG_REMOVE_TAG);
                return true;
            case R.id.menu_item_sort_by_last_name_asc:
                isSortedByName = !isSortedByName;
                isSortedByNumber = false;
                orderBy = PatientTable.COLUMN_LAST_NAME;
                getActivity().invalidateOptionsMenu();
                updateUI();
                return true;
            case R.id.menu_item_sort_by_last_name_desc:
                isSortedByName = !isSortedByName;
                isSortedByNumber = false;
                orderBy = PatientTable.COLUMN_LAST_NAME + " DESC";
                getActivity().invalidateOptionsMenu();
                updateUI();
                return true;
            case R.id.menu_item_sort_by_birthday_asc:
                isSortedByNumber = !isSortedByNumber;
                isSortedByName = false;
                orderBy = PatientTable.COLUMN_BIRTHDAY + " DESC";
                getActivity().invalidateOptionsMenu();
                updateUI();
                return true;
            case R.id.menu_item_sort_by_birthday_desc:
                isSortedByNumber = !isSortedByNumber;
                isSortedByName = false;
                orderBy = PatientTable.COLUMN_BIRTHDAY;
                getActivity().invalidateOptionsMenu();
                updateUI();
                return true;
            case R.id.menu_item_sort_by_tags:
                TagsSelectDialogFragment dialog = TagsSelectDialogFragment.newInstance(selectedTags, andTagsCondition);
                dialog.setTargetFragment(PatientListFragment.this, REQUEST_CODE_SELECT_TAGS);
                dialog.show(manager, DIALOG_SELECT_TAGS);
                isSortedByName = false;
                isSortedByNumber = false;
                orderBy = PatientTable.COLUMN_LAST_NAME;
                getActivity().invalidateOptionsMenu();
                return true;
            case R.id.menu_item_sort_by_default:
                andTagsCondition = false;
                isSortedByName = false;
                isSortedByNumber = false;
                table = null;
                columns = null;
                whereClause = null;
                whereArgs = null;
                groupBy = null;
                having = null;
                orderBy = PatientTable.COLUMN_LAST_NAME;
                selectedTags = new ArrayList<>();
                getActivity().invalidateOptionsMenu();
                updateUI();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (isSortedByName) {
            menu.findItem(R.id.menu_item_sort_by_last_name_asc).setVisible(false);
            menu.findItem(R.id.menu_item_sort_by_last_name_desc).setVisible(true);
        } else {
            menu.findItem(R.id.menu_item_sort_by_last_name_asc).setVisible(true);
            menu.findItem(R.id.menu_item_sort_by_last_name_desc).setVisible(false);
        }
        if (isSortedByNumber) {
            menu.findItem(R.id.menu_item_sort_by_birthday_asc).setVisible(false);
            menu.findItem(R.id.menu_item_sort_by_birthday_desc).setVisible(true);
        } else {
            menu.findItem(R.id.menu_item_sort_by_birthday_asc).setVisible(true);
            menu.findItem(R.id.menu_item_sort_by_birthday_desc).setVisible(false);
        }
    }

    private void updateUI() {

        PatientLab patientLab = PatientLab.get(getActivity());
        mPatients = patientLab.getPatients(
                table,
                columns,
                whereClause,
                whereArgs,
                groupBy,
                having,
                orderBy
        );
        if (mAdapter == null) {
            mAdapter = new PatientAdapter(mPatients);
            mPatientRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setPatients(mPatients);
            mAdapter.notifyDataSetChanged();
        }

    }

    private class PatientHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageButton mEditCardImageButton;
        private ImageButton mDeleteCardImageButton;
        private Patient mPatient;

        public PatientHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_patient_title_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_patient_date_text_view);
            mEditCardImageButton = (ImageButton) itemView.findViewById(R.id.list_item_patient_edit_card_image_button);
            mEditCardImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = PatientEditActivity.newIntent(getActivity(), mPatient.getId(), mEditCardImageButton.getId());
                    startActivityForResult(intent, REQUEST_CODE_EDIT_PATIENT);
                }
            });
            mDeleteCardImageButton = (ImageButton) itemView.findViewById(R.id.list_item_patient_delete_card_image_button);
            mDeleteCardImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager manager = getFragmentManager();
                    DeleteDialogFragment dialog = DeleteDialogFragment.newInstance("patient", mPatient.getId(), null, null);
                    dialog.setTargetFragment(PatientListFragment.this, REQUEST_CODE_DELETE_PATIENT);
                    dialog.show(manager, DIALOG_DELETE_PATIENT);
                }
            });
        }

        public void bindPatient(Patient patient) {
            mPatient = patient;
            mTitleTextView.setText(mPatient.getFullName());
            mDateTextView.setText(mPatient.getPluralAge(getContext()));
        }

        @Override
        public void onClick(View v) {
            Intent intent = PatientReviewPagerActivity.newIntent(getActivity(), mPatient.getId(), mPatients);
            startActivity(intent);
        }
    }

    private class PatientAdapter extends RecyclerView.Adapter<PatientHolder> {

        private List<Patient> mPatients;

        public PatientAdapter(List<Patient> patients) {
            mPatients = patients;
        }

        @Override
        public PatientHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_patient, parent, false);
            return new PatientHolder(view);
        }

        @Override
        public void onBindViewHolder(PatientHolder holder, int position) {
            Patient patient = mPatients.get(position);
            holder.bindPatient(patient);
        }

        @Override
        public int getItemCount() {
            return mPatients.size();
        }

        public void setPatients(List<Patient> patients) {
            mPatients = patients;
        }
    }

}
