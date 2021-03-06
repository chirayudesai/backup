package com.stevesoltys.backup.service.restore;

import android.app.Activity;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.stevesoltys.backup.R;
import com.stevesoltys.backup.session.restore.RestoreResult;
import com.stevesoltys.backup.session.restore.RestoreSessionObserver;

/**
 * @author Steve Soltys
 */
class RestoreObserver implements RestoreSessionObserver {

    private final Activity context;

    private final PopupWindow popupWindow;

    private final int packageCount;

    RestoreObserver(Activity context, PopupWindow popupWindow, int packageCount) {
        this.context = context;
        this.popupWindow = popupWindow;
        this.packageCount = packageCount;
    }

    @Override
    public void restoreSessionStarted(int packageCount) {
        context.runOnUiThread(() -> {
            TextView textView = popupWindow.getContentView().findViewById(R.id.popup_text_view);
            textView.setText(R.string.initializing);
        });
    }

    @Override
    public void restorePackageStarted(int packageIndex, String packageName) {
        context.runOnUiThread(() -> {
            ProgressBar progressBar = popupWindow.getContentView().findViewById(R.id.popup_progress_bar);

            if (progressBar != null) {
                progressBar.setMax(packageCount);
                progressBar.setProgress(packageIndex);
            }

            TextView textView = popupWindow.getContentView().findViewById(R.id.popup_text_view);

            if (textView != null) {
                textView.setText(packageName);
            }
        });
    }

    @Override
    public void restoreSessionCompleted(RestoreResult restoreResult) {
        context.runOnUiThread(() -> {
            if (restoreResult == RestoreResult.SUCCESS) {
                Toast.makeText(context, R.string.restore_success, Toast.LENGTH_LONG).show();

            } else if (restoreResult == RestoreResult.CANCELLED) {
                Toast.makeText(context, R.string.restore_cancelled, Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(context, R.string.restore_failure, Toast.LENGTH_LONG).show();
            }

            popupWindow.dismiss();
            context.finish();
        });
    }
}
