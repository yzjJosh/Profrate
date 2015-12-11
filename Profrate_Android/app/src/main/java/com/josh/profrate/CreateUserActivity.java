package com.josh.profrate;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class CreateUserActivity extends Activity {

    private static final int SELECT_PICTURE = 0;
    private static final int TAKE_PHOTO = 1;

    private static final int TASK_CREATE_USER = 0;
    private static final int TASK_UPLOAD_PHOTO = 1;

    private EditText user_name;
    private View add_photo_btn;
    private ImageView photo;
    private TaskHandler handler = new TaskHandler(this);
    private String photo_path;
    private Dialog processingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        user_name = (EditText)findViewById(R.id.user_name);
        add_photo_btn = findViewById(R.id.add_photo_btn);
        photo = (ImageView)findViewById(R.id.photo_preview);
        ((TextView)findViewById(R.id.email)).setText(Credential.getCredential().getSelectedAccountName());
    }

    public void onCancelBtnClick(View v){
        Credential.logout();
        startActivity(new Intent(this, LogIn.class));
        finish();
    }

    public void onCompleteBtnClick(View v){
        String name = user_name.getText().toString();
        if(name.length() == 0) {
            Toast.makeText(this, "Please enter a user name!", Toast.LENGTH_SHORT).show();
            return;
        }
        processingDialog = new Dialog(this, R.style.theme_dialog);
        processingDialog.setContentView(R.layout.processing_dialog);
        processingDialog.setCancelable(false);
        processingDialog.show();
        new CreateUserThread(name).start();
    }

    public void onAddPhotoBtnClick(View v){
        final Dialog dialog = new Dialog(this, R.style.theme_dialog);
        dialog.setContentView(R.layout.select_picture_source);
        dialog.findViewById(R.id.select_source_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.select_source_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null)
                    startActivityForResult(takePictureIntent, TAKE_PHOTO);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK ) {
            if (requestCode == SELECT_PICTURE )
                photo_path = getGalleryPicturePath(data.getData());
            else if(requestCode == TAKE_PHOTO)
                photo_path = getCameraPicturePath(data.getData());
            if(photo_path != null){
                add_photo_btn.setVisibility(View.GONE);
                photo.setVisibility(View.VISIBLE);
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
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
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
        Cursor cursor = getContentResolver().
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

    private class CreateUserThread extends Thread{

        private final String name;

        public CreateUserThread(String name){
            this.name = name;
        }

        @Override
        public void run(){
            Message message = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("type", TASK_CREATE_USER);
            data.put("name", name);
            try{
                if(User.createUser(name) && Credential.loadCurrentUser())
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

    private static class UploadPhotoThread extends Thread{

        private static final String TAG = "UploadThread";
        private final String path;
        private final Handler handler;

        public UploadPhotoThread(String path, Handler handler){
            this.path = path;
            this.handler = handler;
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

        public TaskHandler(Context contex){
            this.context = contex;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg){
            HashMap<String, Object> data = (HashMap<String, Object>)msg.obj;
            CreateUserActivity activity = (CreateUserActivity)context;
            int type = (int)data.get("type");
            switch (type){
                case TASK_CREATE_USER:
                    if((Boolean)data.get("success")){
                        new UploadPhotoThread(activity.photo_path, activity.handler).start();
                    }else {
                        activity.processingDialog.dismiss();
                        if("Server rejection".equals(data.get("reason")))
                            Toast.makeText(context, "Name \""+data.get("name")+"\" is already used! Please use another name!", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(context, "Unable to connect Internet, please try again!", Toast.LENGTH_LONG).show();
                    }
                    break;
                case TASK_UPLOAD_PHOTO:
                    activity.processingDialog.dismiss();
                    if(!(Boolean)data.get("success"))
                        Toast.makeText(context, "User is created, but unable to upload photo!", Toast.LENGTH_LONG).show();
                    context.startActivity(new Intent(context, MainActivity.class).putExtra("view", MainActivity.VIEW_PROFESSORS));
                    activity.finish();
                    break;
                default:
                    break;
            }
        }

    }

}
