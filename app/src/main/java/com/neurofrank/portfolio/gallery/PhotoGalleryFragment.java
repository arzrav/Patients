package com.neurofrank.portfolio.gallery;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.provider.DocumentFile;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.neurofrank.portfolio.R;
import com.squareup.picasso.Picasso;

import static com.neurofrank.portfolio.util.Constants.ARG_IMAGE;

public class PhotoGalleryFragment extends Fragment {

    private Uri mCurrentImage;

    public static PhotoGalleryFragment newInstance(Uri currentImage) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_IMAGE, currentImage.toString());

        PhotoGalleryFragment fragment = new PhotoGalleryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentImage = Uri.parse((String) getArguments().getSerializable(ARG_IMAGE));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        PhotoView photoView = (PhotoView) v.findViewById(R.id.gallery_photo_view);
        Picasso.with(getActivity())
                .load(mCurrentImage)
                .into(photoView);
        return v;
    }
}
