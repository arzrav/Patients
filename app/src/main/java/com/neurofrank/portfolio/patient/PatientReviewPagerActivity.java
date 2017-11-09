package com.neurofrank.portfolio.patient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.neurofrank.portfolio.R;

import java.util.List;
import java.util.UUID;

import static com.neurofrank.portfolio.util.Constants.EXTRA_PATIENT_ID;


public class PatientReviewPagerActivity extends AppCompatActivity {

    private static List<Patient> mPatients;

    public static Intent newIntent(Context packageContext, UUID patientId, List<Patient> patients) {
        Intent intent = new Intent(packageContext, PatientReviewPagerActivity.class);
        intent.putExtra(EXTRA_PATIENT_ID, patientId);
        mPatients = patients;
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        final UUID patientId = (UUID) getIntent().getSerializableExtra(EXTRA_PATIENT_ID);

        ViewPager viewPager = (ViewPager) findViewById(R.id.activity_view_pager);
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Patient patient = mPatients.get(position);
                return PatientReviewFragment.newInstance(patient.getId());
            }

            @Override
            public int getCount() {
                return mPatients.size();
            }
        });

        for (int i = 0; i < mPatients.size(); i++) {
            if (mPatients.get(i).getId().equals(patientId)) {
                viewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
