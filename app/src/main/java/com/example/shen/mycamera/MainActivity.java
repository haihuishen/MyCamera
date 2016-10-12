package com.example.shen.mycamera;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shen.mycamera.R;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    private Button button;

    private String path = Environment.getExternalStorageDirectory().getAbsolutePath();

    private ImageFactory imageFactory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageFactory = new ImageFactory();

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // 从  path + "123.jpg"  获取的"照片"，是原图，是没有压缩过的!
                // 从 intent 中的 data数据是：压缩过后的"位图"
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(path ,"123.jpg")));
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                Log.i("TestFile","SD card is not avaiable/writeable right now.");
                return;
            }
//            String name = new DateFormat().format("yyyyMMdd_hhmmss",Calendar.getInstance(Locale.CHINA)) + ".jpg";
//            Toast.makeText(this, name, Toast.LENGTH_LONG).show();

                /** 此方法获取的"位图"，是压缩过后的——压缩图*/
//            Bundle bundle = data.getExtras();
//            Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
//
//            FileOutputStream b = null;
//            //???????????????????????????????为什么不能直接保存在系统相册位置呢？？？？？？？？？？？？
//            File file = new File("data/data/com.example.shen.mycamera/image/");
//            file.mkdirs();// 创建文件夹
//            String fileName = "data/data/com.example.shen.mycamera/image/"+name;
//            System.out.println("文件大小"+file.length()+"");
//            try {
//                b = new FileOutputStream(fileName);
//                // 图片压缩
//                // ***参1:图片格式
//                // ***参2:压缩比例0-100; %
//                // ***参3:输出流 (压缩到哪里)
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    b.flush();
//                    b.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }

//            Bitmap bitmap = BitmapFactory.decodeFile(path+"/123.jpg");
//
//            FileOutputStream b = null;
//            try {
//                b = new FileOutputStream(path+"/321.jpg");
//                // 图片压缩
//                // ***参1:图片格式
//                // ***参2:压缩比例0-100; %
//                // ***参3:输出流 (压缩到哪里)
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 0, b);// 把数据写入文件
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    b.flush();
//                    b.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }

            long fileSize = imageFactory.getImageSize(path+"/123.jpg");
            Bitmap bitmap = imageFactory.getBitmap(path+"/123.jpg");

            int[] wAhSize = imageFactory.getImageWidthAndHeight(path+"/123.jpg");

            ((TextView) findViewById(R.id.tv_source)).setText("图片大小："+SizeConverter.BTrim.convert(fileSize) +
                    "\n宽："+wAhSize[0]+"  高："+wAhSize[1]);
            ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bitmap);// 将图片显示在ImageView里

        }


    }

    public void Compress_quality(View view) {
        EditText editText = (EditText) findViewById(R.id.et_quality);
        int quality = 1;
        if(!editText.getText().toString().isEmpty())
            quality = Integer.valueOf(editText.getText().toString());
        else {
            Toast.makeText(this, "文本不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            imageFactory.compressAndGenImage(path+"/123.jpg", path+"/321-1.jpg", quality, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap1 = imageFactory.getBitmap(path+"/321-1.jpg");

        long fileSize = imageFactory.getImageSize(path+"/321-1.jpg");
        int[] wAhSize = imageFactory.getImageWidthAndHeight(path+"/321-1.jpg");

        ((TextView) findViewById(R.id.tv_quality_source)).setText("图片大小："+SizeConverter.BTrim.convert(fileSize) +
                "\n宽："+wAhSize[0]+"  高："+wAhSize[1]);
        ((ImageView) findViewById(R.id.imageView1)).setImageBitmap(bitmap1);// 将图片显示在ImageView里
    }

    public void Compress_Size(View view) {
        EditText et_width = (EditText) findViewById(R.id.et_width);
        EditText et_height = (EditText) findViewById(R.id.et_height);
        int width = 1;
        int height = 1;
        if(!et_width.getText().toString().isEmpty())
            width = Integer.valueOf(et_width.getText().toString());
        else {
            Toast.makeText(this, "宽不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!et_height.getText().toString().isEmpty())
            height = Integer.valueOf(et_height.getText().toString());
        else {
            Toast.makeText(this, "高不能为空", Toast.LENGTH_SHORT).show();
            return;
        }


        try {
            imageFactory.ratioAndGenThumb(path+"/123.jpg", path+"/321-2.jpg", width, height, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap2 = imageFactory.getBitmap(path+"/321-2.jpg");

        long fileSize = imageFactory.getImageSize(path+"/321-2.jpg");
        int[] wAhSize = imageFactory.getImageWidthAndHeight(path+"/321-2.jpg");

        ((TextView) findViewById(R.id.tv_size_source)).setText("图片大小："+SizeConverter.BTrim.convert(fileSize) +
                "\n宽："+wAhSize[0]+"  高："+wAhSize[1]);

        ((ImageView) findViewById(R.id.imageView2)).setImageBitmap(bitmap2);// 将图片显示在ImageView里
    }

}