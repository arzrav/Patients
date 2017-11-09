package com.neurofrank.portfolio.tag;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;


import com.neurofrank.portfolio.R;
import com.neurofrank.portfolio.util.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import static com.neurofrank.portfolio.util.Constants.ARG_AND_TAGS_CONDITION;
import static com.neurofrank.portfolio.util.Constants.ARG_TAGS;
import static com.neurofrank.portfolio.util.Constants.EXTRA_AND_TAGS_CONDITION;
import static com.neurofrank.portfolio.util.Constants.EXTRA_TAGS;


public class TagsSelectDialogFragment extends DialogFragment {

    private ArrayList<String> selectedTags;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        selectedTags = (ArrayList<String>) getArguments().getSerializable(ARG_TAGS);
        Boolean andCondition = (Boolean) getArguments().getSerializable(ARG_AND_TAGS_CONDITION);

        TagLab tagLab = TagLab.get(getActivity());
        List<String> tags = tagLab.getTags();
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_select_tags, null);
        final CheckBox andTagsCondition = (CheckBox) v.findViewById(R.id.dialog_select_tags_check_box);
        andTagsCondition.setChecked(andCondition);
        final RecyclerView tagRecyclerView = (RecyclerView) v.findViewById(R.id.dialog_select_tags_recycler_view);
        tagRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        tagRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        final TagAdapter adapter = new TagAdapter(tags, selectedTags);
        tagRecyclerView.setAdapter(adapter);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.select_tags_dialog_title)
                .setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtra(EXTRA_TAGS, selectedTags);
                        intent.putExtra(EXTRA_AND_TAGS_CONDITION, andTagsCondition.isChecked());
                        sendResult(Activity.RESULT_OK, intent);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setCancelable(true)
                .create();
    }

    public static TagsSelectDialogFragment newInstance(ArrayList<String> selectedTags, Boolean andTagsCondition) {
        Bundle args = new Bundle();
        ArrayList<String> tmpTags = new ArrayList<>(selectedTags);
        args.putSerializable(ARG_TAGS, tmpTags);
        args.putSerializable(ARG_AND_TAGS_CONDITION, andTagsCondition);
        TagsSelectDialogFragment fragment = new TagsSelectDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private class TagHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CheckBox mTagCheckBox;
        private String mTag;

        public TagHolder(View itemView) {
            super(itemView);
            mTagCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_tag_check_box);
            mTagCheckBox.setOnClickListener(this);
        }

        public void bindTag(String tag, Boolean isChecked) {
            mTag = tag;
            mTagCheckBox.setText(mTag);
            mTagCheckBox.setChecked(isChecked);
        }

        @Override
        public void onClick(View v) {
            if (mTagCheckBox.isChecked()) {
                selectedTags.add(mTag);
            } else {
                selectedTags.remove(mTag);
            }
        }
    }

    private class TagAdapter extends RecyclerView.Adapter<TagHolder> {

        private List<String> mTags;
        private ArrayList<String> mSelectedTags;

        public TagAdapter(List<String> tags, ArrayList<String> selectedTags) {
            mTags = tags;
            mSelectedTags = selectedTags;
        }

        @Override
        public TagHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_tag, parent, false);
            return new TagHolder(view);
        }

        @Override
        public void onBindViewHolder(TagHolder holder, int position) {
            String tag = mTags.get(position);
            holder.bindTag(tag, mSelectedTags.contains(tag));
        }

        @Override
        public int getItemCount() {
            return mTags.size();
        }
    }

    private void sendResult(int resultCode, Intent intent) {
        if (getTargetFragment() == null) {
            return;
        }

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

}
