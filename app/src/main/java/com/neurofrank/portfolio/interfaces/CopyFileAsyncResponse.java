package com.neurofrank.portfolio.interfaces;

import android.support.v4.provider.DocumentFile;

public interface CopyFileAsyncResponse {
    void processFinish(DocumentFile documentFile, DocumentFile destDir);
}
