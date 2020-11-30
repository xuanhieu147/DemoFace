package com.example.demoface;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;


public class BlankFragment extends Fragment {
    ShareDialog shareDialog;
    ShareLinkContent shareLinkContent;
    TextView tvShare;
    CallbackManager callbackManager;


    public BlankFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_blank, container, false);
        tvShare = v.findViewById(R.id.tv);

        shareDialog = new ShareDialog(BlankFragment.this);
        FacebookSdk.sdkInitialize(this.getContext());
        callbackManager = CallbackManager.Factory.create();
        tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date now = new Date();
                android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

                try {
                    // image naming and path  to include sd card  appending name you choose for file
                    String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

                    // create bitmap screen capture
                    View v1 = getActivity().getWindow().getDecorView().getRootView();
                    v1.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                    v1.setDrawingCacheEnabled(false);

                    File imageFile = new File(mPath);

                    FileOutputStream outputStream = new FileOutputStream(imageFile);
                    int quality = 100;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                        @Override
                        public void onSuccess(Sharer.Result result) {
                            Toast.makeText(getContext(), "Share sucessfull", Toast.LENGTH_SHORT).show();
                            Log.d("A","ok");
                        }

                        @Override
                        public void onCancel() {
                            Toast.makeText(getContext(), "Share cancel", Toast.LENGTH_SHORT).show();
                            Log.d("A","fail");
                        }

                        @Override
                        public void onError(FacebookException error) {
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("A",error.getMessage());
                        }
                    });
                    Picasso.with(getActivity().getBaseContext())
                            .load(imageFile)
                            .into(target);
                    //  openScreenshot(imageFile);
                } catch (Throwable e) {
                    // Several error may come out with file handling or DOM
                    e.printStackTrace();
                }

            }
        });


        return v;
    }
    final Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            SharePhoto sharePhoto = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            if (shareDialog.canShow(SharePhotoContent.class)) {
                SharePhotoContent sharePhotoContent = new SharePhotoContent.Builder()
                        .addPhoto(sharePhoto).build();
                shareDialog.show(sharePhotoContent);
            }

        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };
}