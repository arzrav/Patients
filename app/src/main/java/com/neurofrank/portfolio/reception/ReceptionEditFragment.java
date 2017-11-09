package com.neurofrank.portfolio.reception;

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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;


import com.neurofrank.portfolio.DatePickerDialogFragment;
import com.neurofrank.portfolio.DeleteDialogFragment;
import com.neurofrank.portfolio.R;
import com.neurofrank.portfolio.gallery.PhotoGalleryPagerActivity;
import com.neurofrank.portfolio.interfaces.CopyFileAsyncResponse;
import com.neurofrank.portfolio.tag.TagLab;
import com.neurofrank.portfolio.util.CopyFileAsyncTask;
import com.neurofrank.portfolio.util.FileOperation;
import com.neurofrank.portfolio.util.RegexInputFilter;
import com.squareup.picasso.Picasso;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.neurofrank.portfolio.util.Constants.ARG_RECEPTION_ID;
import static com.neurofrank.portfolio.util.Constants.DIALOG_DELETE_IMAGE;
import static com.neurofrank.portfolio.util.Constants.EXTRA_DATE;
import static com.neurofrank.portfolio.util.Constants.EXTRA_DESCRIPTION;
import static com.neurofrank.portfolio.util.Constants.EXTRA_IMAGE;
import static com.neurofrank.portfolio.util.Constants.REQUEST_CODE_DATE_CHANGE;
import static com.neurofrank.portfolio.util.Constants.REQUEST_CODE_DELETE_IMAGE;
import static com.neurofrank.portfolio.util.Constants.REQUEST_CODE_LOAD_AFTER_IMAGE;
import static com.neurofrank.portfolio.util.Constants.REQUEST_CODE_LOAD_BEFORE_IMAGE;
import static com.neurofrank.portfolio.util.Constants.SPINNER_DEFAULT_TITLE;
import static com.neurofrank.portfolio.util.Constants.SQUARE_SIZE;


public class ReceptionEditFragment extends Fragment implements CopyFileAsyncResponse {

    private static final String DIALOG_DATE_PICKER = "DialogDatePicker";

    private Reception mReception;
    private Spinner mReceptionTitle;
    private Button mReceptionDate;
    private RecyclerView mImageGalleryBeforeRecyclerView;
    private RecyclerView mImageGalleryAfterRecyclerView;
    private List<DocumentFile> mBeforeImages = new ArrayList<>();
    private List<DocumentFile> mAfterImages = new ArrayList<>();
    private DocumentFile mBeforeDir;
    private DocumentFile mAfterDir;
    private PhotoAdapter mBeforePhotoAdapter;
    private PhotoAdapter mAfterPhotoAdapter;

    public static ReceptionEditFragment newInstance(UUID receptionId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_RECEPTION_ID, receptionId);

        ReceptionEditFragment fragment = new ReceptionEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID receptionID = (UUID) getArguments().getSerializable(ARG_RECEPTION_ID);
        mReception = ReceptionLab.get(getActivity()).getReception(receptionID);
        DocumentFile storageDir = FileOperation.GetStorageDirectory(getActivity());
        DocumentFile patientDir = storageDir.findFile("Patient{" + mReception.getPatientId().toString() + "}");
        DocumentFile receptionDir = patientDir.findFile("Reception{" + mReception.getId().toString() + "}");
        mBeforeDir = receptionDir.findFile("Before");
        mAfterDir = receptionDir.findFile("After");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reception_edit, container, false);
        RegexInputFilter historyFilter = new RegexInputFilter("[0-9А-Яа-яёЁA-Za-z ,\\.-]+");
        List<String> tags = TagLab.get(getActivity()).getTags();
        tags.add(0, SPINNER_DEFAULT_TITLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, tags);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mReceptionTitle = (Spinner) v.findViewById(R.id.reception_edit_title_spinner);
        mReceptionTitle.setAdapter(adapter);
        if (TextUtils.isEmpty(mReception.getTitle())) {
            mReceptionTitle.setSelection(0);
        } else {
            int i = -1;
            for (String s : tags) {
                i++;
                if (s.equals(mReception.getTitle())) {
                    mReceptionTitle.setSelection(i);
                }
            }
        }
        mReceptionTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!mReceptionTitle.getSelectedItem().toString().equals(SPINNER_DEFAULT_TITLE)) {
                    mReception.setTitle(mReceptionTitle.getSelectedItem().toString());
                    ReceptionLab.get(getActivity()).updateReception(mReception);
                } else {
                    mReception.setTitle("");
                    ReceptionLab.get(getActivity()).updateReception(mReception);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mReceptionDate = (Button) v.findViewById(R.id.reception_edit_date_button);
        updateDate();
        mReceptionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerDialogFragment dialog = DatePickerDialogFragment.newInstance(mReception.getDate());
                dialog.setTargetFragment(ReceptionEditFragment.this, REQUEST_CODE_DATE_CHANGE);
                dialog.show(manager, DIALOG_DATE_PICKER);
            }
        });
        EditText receptionHistory = (EditText) v.findViewById(R.id.reception_edit_history_edit_text);
        if (!TextUtils.isEmpty(mReception.getHistory())) {
            receptionHistory.setText(mReception.getHistory());
        }
        receptionHistory.setFilters(new InputFilter[]{historyFilter});
        receptionHistory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mReception.setHistory(s.toString());
                ReceptionLab.get(getActivity()).updateReception(mReception);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        FloatingActionButton addImageBefore = (FloatingActionButton) v.findViewById(R.id.reception_add_image_before_fab);
        addImageBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_LOAD_BEFORE_IMAGE);
            }
        });
        FloatingActionButton addImageAfter = (FloatingActionButton) v.findViewById(R.id.reception_add_image_after_fab);
        addImageAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_LOAD_AFTER_IMAGE);
            }
        });

        LinearLayoutManager layoutManagerBefore = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mImageGalleryBeforeRecyclerView = (RecyclerView) v.findViewById(R.id.reception_image_gallery_before_recycler_view);
        mImageGalleryBeforeRecyclerView.setLayoutManager(layoutManagerBefore);
        LinearLayoutManager layoutManagerAfter = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mImageGalleryAfterRecyclerView = (RecyclerView) v.findViewById(R.id.reception_image_gallery_after_recycler_view);
        mImageGalleryAfterRecyclerView.setLayoutManager(layoutManagerAfter);

        if (mBeforeImages.size() == 0) {
            for (DocumentFile file : mBeforeDir.listFiles()) {
                if (file.isFile() && !mBeforeImages.contains(file)) {
                    mBeforeImages.add(file);
                }
            }
        }
        updateBeforeImageGallery();
        if (mAfterImages.size() == 0) {
            for (DocumentFile file : mAfterDir.listFiles()) {
                if (file.isFile() && !mAfterImages.contains(file)) {
                    mAfterImages.add(file);
                }
            }
        }
        updateAfterImageGallery();

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_DATE_CHANGE) {
            LocalDate date = (LocalDate) data.getSerializableExtra(EXTRA_DATE);
            mReception.setDate(date);
            updateDate();
            ReceptionLab.get(getActivity()).updateReception(mReception);
        }
        if (requestCode == REQUEST_CODE_LOAD_BEFORE_IMAGE && data != null) {
            Uri[] selectedImages = new Uri[1];
            selectedImages[0] = data.getData();
            CopyFileAsyncTask copyTask = new CopyFileAsyncTask(mBeforeDir, getActivity());
            copyTask.delegate = this;
            copyTask.execute(selectedImages);
        }
        if (requestCode == REQUEST_CODE_LOAD_AFTER_IMAGE && data != null) {
            Uri[] selectedImages = new Uri[1];
            selectedImages[0] = data.getData();
            CopyFileAsyncTask copyTask = new CopyFileAsyncTask(mAfterDir, getActivity());
            copyTask.delegate = this;
            copyTask.execute(selectedImages);
        }
        if (requestCode == REQUEST_CODE_DELETE_IMAGE) {
            ArrayList<DocumentFile> imageToDelete = (ArrayList<DocumentFile>) data.getSerializableExtra(EXTRA_IMAGE);
            String description = (String) data.getSerializableExtra(EXTRA_DESCRIPTION);
            if (description.equals("before")) {
                mBeforeImages.removeAll(imageToDelete);
                updateBeforeImageGallery();
            } else if (description.equals("after")) {
                mAfterImages.removeAll(imageToDelete);
                updateAfterImageGallery();
            }
        }
    }

    private void updateDate() {
        mReceptionDate.setText(mReception.getDate().toString("dd MMMM yyyy, EEEE"));
    }

    private void updateBeforeImageGallery() {
        if (mBeforePhotoAdapter == null) {
            mBeforePhotoAdapter = new PhotoAdapter(mBeforeImages);
            mImageGalleryBeforeRecyclerView.setAdapter(mBeforePhotoAdapter);
        } else {
            mBeforePhotoAdapter.setGalleryItems(mBeforeImages);
            mBeforePhotoAdapter.notifyDataSetChanged();
        }
    }

    private void updateAfterImageGallery() {
        if (mAfterPhotoAdapter == null) {
            mAfterPhotoAdapter = new PhotoAdapter(mAfterImages);
            mImageGalleryAfterRecyclerView.setAdapter(mAfterPhotoAdapter);
        } else {
            mAfterPhotoAdapter.setGalleryItems(mAfterImages);
            mAfterPhotoAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void processFinish(DocumentFile documentFile, DocumentFile destDir) {
        if (destDir.equals(mBeforeDir)) {
            mBeforeImages.add(documentFile);
            mBeforePhotoAdapter.setGalleryItems(mBeforeImages);
            mBeforePhotoAdapter.notifyDataSetChanged();
        } else if (destDir.equals(mAfterDir)) {
            mAfterImages.add(documentFile);
            mAfterPhotoAdapter.setGalleryItems(mAfterImages);
            mAfterPhotoAdapter.notifyDataSetChanged();
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private ImageView mItemImageView;
        private DocumentFile mGalleryItem;
        private PhotoAdapter mPhotoAdapter;

        public PhotoHolder(View itemView, PhotoAdapter adapter) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mItemImageView = (ImageView) itemView;
            mPhotoAdapter = adapter;
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
            Intent intent = PhotoGalleryPagerActivity.newIntent(getActivity(), mGalleryItem, mPhotoAdapter.mGalleryItems);
            startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            FragmentManager manager = getFragmentManager();
            if (mPhotoAdapter.mGalleryItems == mBeforeImages) {
                DeleteDialogFragment dialog = DeleteDialogFragment.newInstance("image", null, mGalleryItem, "before");
                dialog.setTargetFragment(ReceptionEditFragment.this, REQUEST_CODE_DELETE_IMAGE);
                dialog.show(manager, DIALOG_DELETE_IMAGE);
            } else {
                DeleteDialogFragment dialog = DeleteDialogFragment.newInstance("image", null, mGalleryItem, "after");
                dialog.setTargetFragment(ReceptionEditFragment.this, REQUEST_CODE_DELETE_IMAGE);
                dialog.show(manager, DIALOG_DELETE_IMAGE);
            }
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
            return new PhotoHolder(imageView, this);
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
