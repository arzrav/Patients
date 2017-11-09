package com.neurofrank.portfolio.patient;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.neurofrank.portfolio.interfaces.OnDataPass;
import com.neurofrank.portfolio.R;
import com.neurofrank.portfolio.util.RegexInputFilter;

import org.joda.time.LocalDate;

import java.util.Calendar;
import java.util.UUID;

import br.com.sapereaude.maskedEditText.MaskedEditText;

import static com.neurofrank.portfolio.util.Constants.ARG_PATIENT_ID;
import static com.neurofrank.portfolio.util.Constants.MOBILE_NUMBER_LENGTH;
import static com.neurofrank.portfolio.util.Constants.jodaTimeDmySlashFormatter;

public class PatientEditFragment extends Fragment {

    public static final String PATIENT_DATA_HAS_BEEN_CHANGED = "patient_data_has_been_changed";
    public static final String PATIENT_DATA_HAS_NOT_BEEN_CHANGED = "patient_data_has_not_been_changed";

    private Patient mPatient;
    private EditText mPatientFirstName;
    private EditText mPatientMiddleName;
    private EditText mPatientLastName;
    private EditText mPatientBirthday;
    private MaskedEditText mPatientMobilePhone;
    private EditText mPatientEmail;
    private Button mCardCreateButton;

    OnDataPass dataPasser;

    public static PatientEditFragment newInstance(UUID patientId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PATIENT_ID, patientId);

        PatientEditFragment fragment = new PatientEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (OnDataPass) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID patientId = (UUID) getArguments().getSerializable(ARG_PATIENT_ID);
        mPatient = PatientLab.get(getActivity()).getPatient(patientId);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_patient_edit, container, false);
        RegexInputFilter nameFilter = new RegexInputFilter("[А-Яа-яёЁA-Za-z -]+");
        RegexInputFilter emailFilter = new RegexInputFilter("[0-9a-z_.@]+");

        mPatientFirstName = (EditText) v.findViewById(R.id.patient_first_name_edit_text);
        if (!TextUtils.isEmpty(mPatient.getFirstName())) {
            mPatientFirstName.setText(mPatient.getFirstName());
        }
        mPatientFirstName.setFilters(new InputFilter[]{
                nameFilter,
                new InputFilter.LengthFilter(50)
        });
        mPatientMiddleName = (EditText) v.findViewById(R.id.patient_middle_name_edit_text);
        if (!TextUtils.isEmpty(mPatient.getMiddleName())) {
            mPatientMiddleName.setText(mPatient.getMiddleName());
        }
        mPatientMiddleName.setFilters(new InputFilter[]{
                nameFilter,
                new InputFilter.LengthFilter(50)
        });
        mPatientLastName = (EditText) v.findViewById(R.id.patient_last_name_edit_text);
        if (!TextUtils.isEmpty(mPatient.getLastName())) {
            mPatientLastName.setText(mPatient.getLastName());
        }
        mPatientLastName.setFilters(new InputFilter[]{
                nameFilter,
                new InputFilter.LengthFilter(50)
        });
        mPatientBirthday = (EditText) v.findViewById(R.id.patient_birthday_edit_text);
        if (mPatient.getBirthday() != null) {
            mPatientBirthday.setText(mPatient.getBirthday().toString("dd/MM/yyyy"));
        }
        mPatientBirthday.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "ДДММГГГГ";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = current.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        if(mon > 12) mon = 12;
                        cal.set(Calendar.MONTH, mon-1);
                        year = (year<1900)?1900:(year>2100)?2100:year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    mPatientBirthday.setText(current);
                    mPatientBirthday.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mPatientMobilePhone = (MaskedEditText) v.findViewById(R.id.patient_mobile_phone_edit_text);
        mPatientMobilePhone.setKeepHint(true);
        if (!TextUtils.isEmpty(mPatient.getMobilePhone())) {
            mPatientMobilePhone.setText(mPatient.getMobilePhone());
        }
        mPatientMobilePhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mPatientMobilePhone.focusSearch(View.FOCUS_DOWN).requestFocus();
                }
                return false;
            }
        });
        mPatientEmail = (EditText) v.findViewById(R.id.patient_email_edit_text);
        if (!TextUtils.isEmpty(mPatient.getEmail())) {
            mPatientEmail.setText(mPatient.getEmail());
        }
        mPatientEmail.setFilters(new InputFilter[]{
                emailFilter,
                new InputFilter.LengthFilter(50)
        });
        mCardCreateButton = (Button) v.findViewById(R.id.patient_card_create_button);
        mCardCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean requiredFieldsState = true;

                if (!TextUtils.isEmpty(mPatientFirstName.getText())) {
                    mPatient.setFirstName(mPatientFirstName.getText().toString());
                } else {
                    requiredFieldsState = false;
                }
                if (!TextUtils.isEmpty(mPatientMiddleName.getText())) {
                    mPatient.setMiddleName(mPatientMiddleName.getText().toString());
                } else {
                    requiredFieldsState = false;
                }
                if (!TextUtils.isEmpty(mPatientLastName.getText())) {
                    mPatient.setLastName(mPatientLastName.getText().toString());
                } else {
                    requiredFieldsState = false;
                }
                if (!TextUtils.isEmpty(mPatientBirthday.getText())) {
                    try {
                        LocalDate birthday = jodaTimeDmySlashFormatter.parseLocalDate(mPatientBirthday.getText().toString());
                        mPatient.setBirthday(birthday);
                    } catch (Exception e) {
                        requiredFieldsState = false;
                    }
                } else {
                    requiredFieldsState = false;
                }
                if (mPatientMobilePhone.getRawText().length() == MOBILE_NUMBER_LENGTH) {
                    mPatient.setMobilePhone(mPatientMobilePhone.getRawText());
                }
                if (!TextUtils.isEmpty(mPatientEmail.getText())) {
                    mPatient.setEmail(mPatientEmail.getText().toString());
                }

                if (requiredFieldsState) {
                    dataPasser.onDataPass(PATIENT_DATA_HAS_BEEN_CHANGED);
                    PatientLab.get(getActivity()).updatePatient(mPatient);
                    getActivity().onBackPressed();
                } else {
                    Toast toast = Toast.makeText(getContext(), R.string.alert_patient_edit_patient_not_created, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                dataPasser.onDataPass(PATIENT_DATA_HAS_NOT_BEEN_CHANGED);
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
