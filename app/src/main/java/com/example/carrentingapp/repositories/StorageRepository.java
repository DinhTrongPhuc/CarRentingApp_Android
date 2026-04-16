package com.example.carrentingapp.repositories;

import android.net.Uri;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StorageRepository {
    public interface SimpleUploadCallback {
        void onSuccess(String downloadUrl);
        void onFailure(String error);
    }
    public interface MultiUploadCallback {
        void onSuccess(List<String> urls);
        void onFailure(String error);
    }

    // FR2.3 - Upload single image (Cloudinary implementation)
    public void uploadImage(Uri imageUri, String folder, SimpleUploadCallback cb) {
        MediaManager.get().upload(imageUri)
            .option("folder", folder)
            .callback(new UploadCallback() {
                @Override
                public void onStart(String requestId) {}

                @Override
                public void onProgress(String requestId, long bytes, long totalBytes) {}

                @Override
                public void onSuccess(String requestId, Map resultData) {
                    String url = (String) resultData.get("secure_url");
                    cb.onSuccess(url);
                }

                @Override
                public void onError(String requestId, ErrorInfo error) {
                    cb.onFailure(error.getDescription());
                }

                @Override
                public void onReschedule(String requestId, ErrorInfo error) {}
            })
            .dispatch();
    }

    // Upload multiple images
    public void uploadImages(List<Uri> uris, String folder, MultiUploadCallback cb) {
        List<String> urls = new ArrayList<>();
        if (uris == null || uris.isEmpty()) { cb.onSuccess(urls); return; }
        uploadNext(uris, 0, folder, urls, cb);
    }

    private void uploadNext(List<Uri> uris, int index, String folder, List<String> urls, MultiUploadCallback cb) {
        if (index >= uris.size()) { cb.onSuccess(urls); return; }
        uploadImage(uris.get(index), folder, new SimpleUploadCallback() {
            @Override
            public void onSuccess(String url) {
                urls.add(url);
                uploadNext(uris, index + 1, folder, urls, cb);
            }

            @Override
            public void onFailure(String error) { cb.onFailure("Lỗi upload: " + error); }
        });
    }
}
