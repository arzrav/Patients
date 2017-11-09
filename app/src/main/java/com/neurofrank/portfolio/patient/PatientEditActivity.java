package com.neurofrank.portfolio.patient;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.neurofrank.portfolio.interfaces.OnDataPass;
import com.neurofrank.portfolio.R;
import com.neurofrank.portfolio.SingleFragmentActivity;

import java.util.UUID;

import static com.neurofrank.portfolio.util.Constants.EXTRA_PATIENT_ACTION;
import static com.neurofrank.portfolio.util.Constants.EXTRA_PATIENT_ID;
import static com.neurofrank.portfolio.util.Constants.EXTRA_WHO_CREATED;
import static com.neurofrank.portfolio.util.Constants.CHANGED;
import static com.neurofrank.portfolio.util.Constants.CREATED;

public class PatientEditActivity extends SingleFragmentActivity implements OnDataPass {

    private String mPassedData = PatientEditFragment.PATIENT_DATA_HAS_NOT_BEEN_CHANGED;

    public static Intent newIntent(Context packageContext, UUID patientId, int whoCreated) {
        Intent intent = new Intent(packageContext, PatientEditActivity.class);
        intent.putExtra(EXTRA_PATIENT_ID, patientId);
        intent.putExtra(EXTRA_WHO_CREATED, whoCreated);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID patientId = (UUID) getIntent().getSerializableExtra(EXTRA_PATIENT_ID);
        return PatientEditFragment.newInstance(patientId);
    }

    @Override
    public void onBackPressed() {
        if ((int) getIntent().getSerializableExtra(EXTRA_WHO_CREATED) == R.id.menu_item_new_patient && mPassedData == PatientEditFragment.PATIENT_DATA_HAS_NOT_BEEN_CHANGED) {
            PatientLab.get(this).removePatient((UUID) getIntent().getSerializableExtra(EXTRA_PATIENT_ID));
            super.onBackPressed();
        } else {
            Intent data = new Intent();
            if ((int) getIntent().getSerializableExtra(EXTRA_WHO_CREATED) == R.id.menu_item_new_patient) {
                data.putExtra(EXTRA_PATIENT_ACTION, CREATED);
                data.putExtra(EXTRA_PATIENT_ID, (getIntent().getSerializableExtra(EXTRA_PATIENT_ID)).toString());
                setResult(RESULT_OK, data);
            } else if ((int) getIntent().getSerializableExtra(EXTRA_WHO_CREATED) == R.id.list_item_patient_edit_card_image_button && mPassedData == PatientEditFragment.PATIENT_DATA_HAS_BEEN_CHANGED) {
                data.putExtra(EXTRA_PATIENT_ACTION, CHANGED);
                setResult(RESULT_OK, data);
            }
            super.onBackPressed();
        }
    }

    public static String whatPatientAction (Intent result) {
        return result.getStringExtra(EXTRA_PATIENT_ACTION);
    }

    public static String getExtraPatientId (Intent result) {
        return result.getStringExtra(EXTRA_PATIENT_ID);
    }

    @Override
    public void onDataPass(String data) {
        mPassedData = data;
    }
}
