package com.akg.akg_sales.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import com.akg.akg_sales.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
ImagePickerHelper picker = new ImagePickerHelper(this,this,
        new ImagePickerHelper.Callback() {
            @Override
            public void onImageReady(File file) {
                // FINAL COMPRESSED IMAGE
            }

            @Override
            public void onError(Exception e) {e.printStackTrace();}
        }
);
picker.launchImagePicker();
*/

public class ImagePickerHelper {

    public interface Callback {
        void onImageReady(File file);
        void onError(Exception e);
    }

    private final Context context;
    private final Callback callback;
    private Uri cameraUri;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final ActivityResultLauncher<Uri> cameraLauncher;
    private final ActivityResultLauncher<PickVisualMediaRequest> pickerLauncher;
    private final ActivityResultLauncher<String> permissionLauncher;
    private final ActivityResultLauncher<Intent> cropLauncher;

    public ImagePickerHelper(ActivityResultCaller caller,
                             Context context,
                             Callback callback) {

        this.context = context;
        this.callback = callback;

        // CAMERA
        cameraLauncher = caller.registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && cameraUri != null) {
                        startCrop(cameraUri);
                    } else {
                        callback.onError(new Exception("Camera capture failed"));
                    }
                });

        // PHOTO PICKER
        pickerLauncher = caller.registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        startCrop(uri);
                    }
                });

        // PERMISSION
        permissionLauncher = caller.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) openCamera();
                    else callback.onError(new Exception("Camera permission denied"));
                });

        // CROP RESULT
        cropLauncher = caller.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == Activity.RESULT_OK) {

                        Uri resultUri = UCrop.getOutput(result.getData());

                        if (resultUri == null) {
                            callback.onError(new Exception("Crop failed: Uri null"));
                            return;
                        }

                        // 🔥 Background thread
                        executor.execute(() -> {
                            try {
                                File file = compressWithExif(resultUri, 1280, 1280, 70);

                                ((Activity) context).runOnUiThread(() ->
                                        callback.onImageReady(file)
                                );

                            } catch (Exception e) {
                                ((Activity) context).runOnUiThread(() ->
                                        callback.onError(e)
                                );
                            }
                        });

                    } else if (result.getResultCode() == UCrop.RESULT_ERROR) {
                        Throwable cropError = null;
                        if (result.getData() != null) cropError = UCrop.getError(result.getData());

                        if (cropError != null) Log.e("UCropError", "Crop failed", cropError);

                        callback.onError(new Exception(
                                cropError != null ? cropError.getMessage() : "Unknown crop error"
                        ));
                    }
                });
    }

    // ======================
    // PUBLIC METHODS
    // ======================

    public void launchImagePicker(){
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_attachment_image_source, null);
        dialog.setContentView(view);

        LinearLayout galleryLayout = view.findViewById(R.id.layout_gallery);
        LinearLayout cameraLayout = view.findViewById(R.id.layout_camera);

        galleryLayout.setOnClickListener(v -> {pickFromGallery();dialog.dismiss();});
        cameraLayout.setOnClickListener(v -> {captureFromCamera();dialog.dismiss();});
        dialog.show();
    }

    private void pickFromGallery() {
        pickerLauncher.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()
        );
    }

    private void captureFromCamera() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    // ======================
    // CAMERA
    // ======================

    private void openCamera() {
        try {
            File file = new File(context.getCacheDir(),
                    "camera_" + System.currentTimeMillis() + ".jpg");

            cameraUri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".provider",
                    file
            );

            cameraLauncher.launch(cameraUri);

        } catch (Exception e) {
            callback.onError(e);
        }
    }

    // ======================
    // CROP
    // ======================

    private void startCrop(Uri sourceUri) {

        Uri destinationUri = Uri.fromFile(
                new File(context.getCacheDir(),
                        "crop_" + System.currentTimeMillis() + ".jpg"));

        UCrop.Options options = new UCrop.Options();
        options.setFreeStyleCropEnabled(true);
        options.setHideBottomControls(false);

        UCrop uCrop = UCrop.of(sourceUri, destinationUri)
                .withOptions(options)
                .withMaxResultSize(1280, 1280);

        cropLauncher.launch(uCrop.getIntent(context));
    }

    // ======================
    // COMPRESSION + EXIF FIX
    // ======================

    private File compressWithExif(Uri uri,
                                  int maxW,
                                  int maxH,
                                  int quality) throws Exception {

        InputStream input = context.getContentResolver().openInputStream(uri);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeStream(input, null, options);
        input.close();

        options.inSampleSize = calculateSample(options, maxW, maxH);
        options.inJustDecodeBounds = false;

        input = context.getContentResolver().openInputStream(uri);

        Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
        input.close();

        // 🔥 FIX ROTATION
        bitmap = fixExifRotation(uri, bitmap);

        // SCALE
        Bitmap scaled = scale(bitmap, maxW, maxH);

        File file = new File(context.getCacheDir(),
                "IMG_" + System.currentTimeMillis() + ".jpg");

        FileOutputStream fos = new FileOutputStream(file);

        scaled.compress(Bitmap.CompressFormat.JPEG, quality, fos);

        fos.flush();
        fos.close();

        return file;
    }

    private Bitmap fixExifRotation(Uri uri, Bitmap bitmap) throws Exception {

        InputStream input = context.getContentResolver().openInputStream(uri);
        ExifInterface exif = new ExifInterface(input);

        int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
        );

        input.close();

        Matrix matrix = new Matrix();

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;

            default:
                return bitmap;
        }

        return Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.getWidth(),
                bitmap.getHeight(),
                matrix,
                true
        );
    }

    private int calculateSample(BitmapFactory.Options options, int reqW, int reqH) {

        int height = options.outHeight;
        int width = options.outWidth;
        int sample = 1;

        while ((height / sample) > reqH || (width / sample) > reqW) {
            sample *= 2;
        }

        return sample;
    }

    private Bitmap scale(Bitmap bmp, int maxW, int maxH) {

        float ratio = Math.min(
                (float) maxW / bmp.getWidth(),
                (float) maxH / bmp.getHeight());

        int width = Math.round(bmp.getWidth() * ratio);
        int height = Math.round(bmp.getHeight() * ratio);

        return Bitmap.createScaledBitmap(bmp, width, height, true);
    }
}
