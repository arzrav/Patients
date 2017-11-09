package com.neurofrank.portfolio.reception;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.neurofrank.portfolio.SingleFragmentActivity;

import java.util.UUID;

import static com.neurofrank.portfolio.util.Constants.EXTRA_RECEPTION_ID;

public class ReceptionEditActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context packageContext, UUID receptionId) {
        Intent intent = new Intent(packageContext, ReceptionEditActivity.class);
        intent.putExtra(EXTRA_RECEPTION_ID, receptionId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID receptionId = (UUID) getIntent().getSerializableExtra(EXTRA_RECEPTION_ID);
        return ReceptionEditFragment.newInstance(receptionId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
