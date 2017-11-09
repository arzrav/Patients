package com.neurofrank.portfolio.patient;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.provider.DocumentFile;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.neurofrank.portfolio.DeleteDialogFragment;
import com.neurofrank.portfolio.R;
import com.neurofrank.portfolio.gallery.PhotoGalleryPagerActivity;
import com.neurofrank.portfolio.interfaces.CopyFileAsyncResponse;
import com.neurofrank.portfolio.reception.Reception;
import com.neurofrank.portfolio.reception.ReceptionEditActivity;
import com.neurofrank.portfolio.reception.ReceptionLab;
import com.neurofrank.portfolio.util.CopyFileAsyncTask;
import com.neurofrank.portfolio.util.FileOperation;
import com.neurofrank.portfolio.util.RegexInputFilter;
import com.neurofrank.portfolio.util.SimpleDividerItemDecoration;
import com.squareup.picasso.Picasso;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.neurofrank.portfolio.util.Constants.ARG_PATIENT_ID;
import static com.neurofrank.portfolio.util.Constants.DIALOG_DELETE_IMAGE;
import static com.neurofrank.portfolio.util.Constants.DIALOG_DELETE_RECEPTION;
import static com.neurofrank.portfolio.util.Constants.EXTRA_IMAGE;
import static com.neurofrank.portfolio.util.Constants.REQUEST_CODE_DELETE_IMAGE;
import static com.neurofrank.portfolio.util.Constants.REQUEST_CODE_DELETE_RECEPTION;
import static com.neurofrank.portfolio.util.Constants.REQUEST_CODE_LOAD_MAIN_IMAGE;
import static com.neurofrank.portfolio.util.Constants.SQUARE_SIZE;

public class PatientReviewFragment extends Fragment implements CopyFileAsyncResponse {

    private Patient mPatient;
    private ReceptionAdapter mReceptionAdapter;
    private PhotoAdapter mPhotoAdapter;
    private RecyclerView mReceptionRecyclerView;
    private RecyclerView mImageGalleryRecyclerView;
    private DocumentFile mPatientDir;
    private List<DocumentFile> mMainImages = new ArrayList<>();

    public static PatientReviewFragment newInstance(UUID patientId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PATIENT_ID, patientId);

        PatientReviewFragment fragment = new PatientReviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID patientId = (UUID) getArguments().getSerializable(ARG_PATIENT_ID);
        mPatient = PatientLab.get(getActivity()).getPatient(patientId);
        setHasOptionsMenu(true);

        DocumentFile storageDir = FileOperation.GetStorageDirectory(getActivity());
        mPatientDir = storageDir.findFile("Patient{" + patientId + "}");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_patient_review, container, false);
        RegexInputFilter historyFilter = new RegexInputFilter("[0-9А-Яа-яёЁA-Za-z ,\\.-]+");
        TextView patientReviewFullName = (TextView) v.findViewById(R.id.patient_review_full_name_text_view);
        patientReviewFullName.setText(mPatient.getFullName());
        TextView patientReviewAge = (TextView) v.findViewById(R.id.patient_review_age_text_view);
        patientReviewAge.setText(mPatient.getPluralAge(getContext()));
        EditText patientHistory = (EditText) v.findViewById(R.id.patient_review_history_edit_text);
        if (mPatient.getHistory() != null) {
            patientHistory.setText(mPatient.getHistory());
        }
        patientHistory.setFilters(new InputFilter[]{historyFilter});
        patientHistory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPatient.setHistory(s.toString());
                PatientLab.get(getActivity()).updatePatient(mPatient);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        FloatingActionButton addMainImage = (FloatingActionButton) v.findViewById(R.id.patient_review_add_main_image_fab);
        addMainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_LOAD_MAIN_IMAGE);
            }
        });

        mImageGalleryRecyclerView = (RecyclerView) v.findViewById(R.id.patient_image_gallery_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mImageGalleryRecyclerView.setLayoutManager(layoutManager);
        mReceptionRecyclerView = (RecyclerView) v.findViewById(R.id.reception_recycler_view);
        mReceptionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mReceptionRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

        if (mMainImages.size() == 0) {
            for (DocumentFile file : mPatientDir.listFiles()) {
                if (file.isFile() && !mMainImages.contains(file)) {
                    mMainImages.add(file);
                }
            }
        }
        updateImageGallery();
        updateReceptions();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateReceptions();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_patient_review, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_reception:
                Reception reception = new Reception(mPatient.getId());
                reception.setDate(new LocalDate());
                ReceptionLab.get(getActivity()).addReception(reception);
                DocumentFile receptionDir = FileOperation.CreateDirectory(mPatientDir, "Reception{" + reception.getId() + "}");
                FileOperation.CreateDirectory(receptionDir, "Before");
                FileOperation.CreateDirectory(receptionDir, "After");
                updateReceptions();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_LOAD_MAIN_IMAGE && data != null) {
            Uri[] selectedImages = new Uri[1];
            selectedImages[0] = data.getData();
            CopyFileAsyncTask copyTask = new CopyFileAsyncTask(mPatientDir, getActivity());
            copyTask.delegate = this;
            copyTask.execute(selectedImages);
        }
        if (requestCode == REQUEST_CODE_DELETE_RECEPTION) {
            updateReceptions();
        }
        if (requestCode == REQUEST_CODE_DELETE_IMAGE) {
            ArrayList<DocumentFile> imageToDelete = (ArrayList<DocumentFile>) data.getSerializableExtra(EXTRA_IMAGE);
            mMainImages.removeAll(imageToDelete);
            updateImageGallery();
        }
    }

    private void updateReceptions() {
        ReceptionLab receptionLab = ReceptionLab.get(getActivity());
        List<Reception> receptions = receptionLab.getReceptions(mPatient.getId());
        if (mReceptionAdapter == null) {
            mReceptionAdapter = new ReceptionAdapter(receptions);
            mReceptionRecyclerView.setAdapter(mReceptionAdapter);
        } else {
            mReceptionAdapter.setReceptions(receptions);
            mReceptionAdapter.notifyDataSetChanged();
        }
    }

    private void updateImageGallery() {
        if (mPhotoAdapter == null) {
            mPhotoAdapter = new PhotoAdapter(mMainImages);
            mImageGalleryRecyclerView.setAdapter(mPhotoAdapter);
        } else {
            mPhotoAdapter.setGalleryItems(mMainImages);
            mPhotoAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void processFinish(DocumentFile documentFile, DocumentFile destDir) {
        mMainImages.add(documentFile);
        mPhotoAdapter.setGalleryItems(mMainImages);
        mPhotoAdapter.notifyDataSetChanged();
    }

    private class ReceptionHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mReceptionBeforeImageView;
        private ImageView mReceptionAfterImageView;
        private Reception mReception;
        private DocumentFile mReceptionDir;

        public ReceptionHolder (View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_reception_title_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_reception_date_text_view);
            mReceptionBeforeImageView = (ImageView) itemView.findViewById(R.id.list_item_reception_before_image_view);
            mReceptionAfterImageView = (ImageView) itemView.findViewById(R.id.list_item_reception_after_image_view);
        }

        public void bindReception(Reception reception) {
            mReception = reception;
            mReceptionDir = mPatientDir.findFile("Reception{" + mReception.getId() + "}");
            mTitleTextView.setText(mReception.getTitle());
            mDateTextView.setText(mReception.getDate().toString("dd MMM, yyyy, EE"));
            DocumentFile beforeDir = mReceptionDir.findFile("Before");
            DocumentFile afterDir = mReceptionDir.findFile("After");
            for (DocumentFile image : beforeDir.listFiles()) {
                Picasso.with(getActivity())
                        .load(image.getUri())
                        .resize(SQUARE_SIZE, SQUARE_SIZE)
                        .centerCrop()
                        .into(mReceptionBeforeImageView);
                break;
            }
            for (DocumentFile image : afterDir.listFiles()) {
                Picasso.with(getActivity())
                        .load(image.getUri())
                        .resize(SQUARE_SIZE, SQUARE_SIZE)
                        .centerCrop()
                        .into(mReceptionAfterImageView);
                break;
            }
        }

        @Override
        public void onClick(View v) {
            Intent intent = ReceptionEditActivity.newIntent(getActivity(), mReception.getId());
            startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            FragmentManager manager = getFragmentManager();
            DeleteDialogFragment dialog = DeleteDialogFragment.newInstance("reception", mReception.getId(), null, null);
            dialog.setTargetFragment(PatientReviewFragment.this, REQUEST_CODE_DELETE_RECEPTION);
            dialog.show(manager, DIALOG_DELETE_RECEPTION);
            return true;
        }
    }

    private class ReceptionAdapter extends RecyclerView.Adapter<ReceptionHolder> {

        private List<Reception> mReceptions;

        public ReceptionAdapter(List<Reception> receptions) {
            mReceptions = receptions;
        }

        @Override
        public ReceptionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_reception, parent, false);
            return new ReceptionHolder(view);
        }

        @Override
        public void onBindViewHolder(ReceptionHolder holder, int position) {
            Reception reception = mReceptions.get(position);
            holder.bindReception(reception);
        }

        @Override
        public int getItemCount() {
            return mReceptions.size();
        }

        public void setReceptions(List<Reception> receptions) {
            mReceptions = receptions;
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private ImageView mItemImageView;
        private DocumentFile mGalleryItem;

        public PhotoHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mItemImageView = (ImageView) itemView;
        }

        public void bindGalleryItem(DocumentFile documentFile) {
            mGalleryItem = documentFile;
            Picasso.with(getActivity())
                    .load(documentFile.getUri())
                    .resize(SQUARE_SIZE, SQUARE_SIZE)
                    .centerCrop()
                    .into(mItemImageView);
        }

        @Override
        public void onClick(View v) {
            Intent intent = PhotoGalleryPagerActivity.newIntent(getActivity(), mGalleryItem, mMainImages);
            startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            FragmentManager manager = getFragmentManager();
            DeleteDialogFragment dialog = DeleteDialogFragment.newInstance("image", null, mGalleryItem, null);
            dialog.setTargetFragment(PatientReviewFragment.this, REQUEST_CODE_DELETE_IMAGE);
            dialog.show(manager, DIALOG_DELETE_IMAGE);
            return true;
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<DocumentFile> mGalleryItems;

        public PhotoAdapter(List<DocumentFile> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(getActivity());
            return new PhotoHolder(imageView);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            DocumentFile galleryItem = mGalleryItems.get(position);
            holder.bindGalleryItem(galleryItem);
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }

        public void setGalleryItems(List<DocumentFile> galleryItems) {
            mGalleryItems = galleryItems;
        }
    }

}