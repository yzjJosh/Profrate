package com.josh.profrate.viewContents;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.josh.profrate.R;
import com.josh.profrate.dataStructures.User;
import com.josh.profrate.elements.Credential;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class ViewProfile extends ViewContent {

    public static final int SELECT_PICTURE = 0;
    public static final int TAKE_PHOTO = 1;

    private static final int TASK_EDIT_USER_NAME = 0;
    private static final int TASK_UPLOAD_PHOTO = 1;

    private TaskHandler handler;
    private boolean isActive;
    private ImageView photo;
    private TextView name_text;
    private String photo_path;
    private String name;
    private Dialog processingDialog;
    private boolean isUploadingPhoto = false;
    private boolean isEditingName = false;

    public ViewProfile(Context context, ViewGroup parentLayout){
        super(context, parentLayout);
        isActive = false;
        handler = new TaskHandler(context, this);
    }

    @Override
    public void show() {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_profile, parentLayout, true);
        User user = Credential.getCurrentUser();
        name_text = (TextView)parentLayout.findViewById(R.id.name);
        name_text.setText(user.name);
        ((TextView)parentLayout.findViewById(R.id.email)).setText(user.email);
        photo = (ImageView) parentLayout.findViewById(R.id.photo);
        photo.setImageBitmap(Credential.getCurrentUserPhoto());
        photo.setOnClickListener(onPhotoClick);
        parentLayout.findViewById(R.id.edit_name_btn).setOnClickListener(onEditNameClick);
        parentLayout.findViewById(R.id.update_profile_btn).setOnClickListener(onUpdateClick);
        name = user.name;
        isActive = true;
    }

    @Override
    public void clear() {
        parentLayout.removeAllViews();
        isActive = false;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK ) {
            if (requestCode == SELECT_PICTURE )
                photo_path = getGalleryPicturePath(data.getData());
            else if(requestCode == TAKE_PHOTO)
                photo_path = getCameraPicturePath(data.getData());
            if(photo_path != null){
                Bitmap bitmap = BitmapFactory.decodeFile(photo_path);
                Matrix matrix = new Matrix();
                try {
                    ExifInterface ei = new ExifInterface(photo_path);
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    switch(orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            matrix.postRotate(90);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            matrix.postRotate(180);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            matrix.postRotate(270);
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                photo.setImageBitmap(bitmap);
            }
        }
    }

    public String getCameraPicturePath(Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private String getGalleryPicturePath(Uri uri) {
        // Will return "image:x*"
        String wholeID = DocumentsContract.getDocumentId(uri);
        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];
        String[] column = { MediaStore.Images.Media.DATA };
        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";
        Cursor cursor = context.getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{ id }, null);
        String filePath = "";
        int columnIndex = cursor.getColumnIndex(column[0]);
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    private View.OnClickListener onPhotoClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Dialog dialog = new Dialog(context, R.style.theme_dialog);
            dialog.setContentView(R.layout.select_picture_source);
            dialog.findViewById(R.id.select_source_gallery).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    ((Activity)context).startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.select_source_camera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(context.getPackageManager()) != null)
                        ((Activity)context).startActivityForResult(takePictureIntent, TAKE_PHOTO);
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    };

    private View.OnClickListener onEditNameClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle("Edit Name");
            alertDialog.setMessage("Please enter your new name");

            final EditText input = new EditText(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            lp.setMarginStart(200);
            lp.setMarginEnd(200);
            input.setLayoutParams(lp);
            input.setSingleLine();
            input.setText(name_text.getText());
            alertDialog.setView(input);
            alertDialog.setIcon(R.drawable.edit);

            alertDialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            name_text.setText(input.getText().toString());
                        }
                    });

            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            alertDialog.show();
        }
    };

    private View.OnClickListener onUpdateClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(name.length() == 0)
                Toast.makeText(context, "Please enter a user name!", Toast.LENGTH_SHORT).show();
            String cur_name = name_text.getText().toString();
            if(!cur_name.equals(name)) {
                isEditingName = true;
                new EditUserNameThread(cur_name).start();
            }
            if(photo_path != null) {
                isUploadingPhoto = true;
                new UploadPhotoThread(photo_path).start();
            }
            if(isEditingName || isUploadingPhoto){
                processingDialog = new Dialog(context, R.style.theme_dialog);
                processingDialog.setContentView(R.layout.processing_dialog);
                processingDialog.setCancelable(false);
                processingDialog.show();
            }
        }
    };

    private class EditUserNameThread extends Thread{

        private final String name;

        public EditUserNameThread(String name){
            this.name = name;
        }

        @Override
        public void run(){
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("type", TASK_EDIT_USER_NAME);
            data.put("name", name);
            try{
                if(Credential.getCurrentUser().editName(name) && Credential.loadCurrentUser())
                    data.put("success", true);
                else {
                    data.put("success", false);
                    data.put("reason", "Server rejection");
                }
            }catch (IOException e){
                e.printStackTrace();
                data.put("success", false);
                data.put("reason", "IOException");
            }
            message.obj = data;
            handler.sendMessage(message);
        }

    }

    private class UploadPhotoThread extends Thread{

        private static final String TAG = "UploadThread";
        private final String path;

        public UploadPhotoThread(String path){
            this.path = path;
        }

        @Override
        public void run(){
            Message msg = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("type", TASK_UPLOAD_PHOTO);
            try (CloseableHttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build()){
                String url = Credential.getCurrentUser().getPhotoUploadUrl();
                if(url == null)
                    throw new Exception("Unable to fetch the upload url, got null pointer!");
                Log.i(TAG, "Got uploading url " + url);
                HttpContext localContext = new BasicHttpContext();
                HttpPost httppost = new HttpPost(url);
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                builder.addPart("photo", new FileBody(new File(path)));
                httppost.setEntity(builder.build());
                HttpResponse response = httpClient.execute(httppost, localContext);
                Log.i(TAG, response.getStatusLine().toString());
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String line = reader.readLine();
                if(line.equals("success") && Credential.loadCurrentUser())
                    data.put("success", true);
                else
                    data.put("success", false);
            }catch (Exception e){
                e.printStackTrace();
                data.put("success", false);
            }
            msg.obj = data;
            handler.sendMessage(msg);
        }
    }

    public static class TaskHandler extends Handler {

        private final Context context;
        private final ViewProfile content;

        public TaskHandler(Context contex, ViewProfile content){
            this.context = contex;
            this.content = content;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg){
            HashMap<String, Object> data = (HashMap<String, Object>)msg.obj;
            int type = (int)data.get("type");
            switch (type){
                case TASK_EDIT_USER_NAME:
                    content.isEditingName = false;
                    if(!content.isUploadingPhoto)
                        content.processingDialog.dismiss();
                    if(!(Boolean)data.get("success")){
                        if("Server rejection".equals(data.get("reason")))
                            Toast.makeText(context, "Name \""+data.get("name")+"\" is already used! Please use another name!", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(context, "Unable to connect Internet, please try again!", Toast.LENGTH_LONG).show();
                    }
                    break;
                case TASK_UPLOAD_PHOTO:
                    content.isUploadingPhoto = false;
                    if(!content.isEditingName)
                        content.processingDialog.dismiss();
                    if(!(Boolean)data.get("success"))
                        Toast.makeText(context, "Unable to upload photo!", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

    }

}
