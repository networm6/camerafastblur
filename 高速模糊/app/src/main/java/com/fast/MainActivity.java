package com.fast;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.hardware.Camera;
import android.graphics.Bitmap;
import android.view.View;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import android.graphics.YuvImage;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import com.mybulr.BlurDrawable;
import java.io.IOException;
import android.view.Surface;
import com.MySurfaceView;
public class MainActivity extends Activity {
	private MySurfaceView surfaceView;
	private  SurfaceView sut;
	private  SurfaceHolder.Callback callback;
	private Camera camera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
		init();
		initSurfaceView();
    }
    private void init()
	{	

		sut = findViewById(R.id.surfaceViewt);

        surfaceView = (MySurfaceView) findViewById(R.id.surfaceView);
		sut.setVisibility(View.VISIBLE);
		surfaceView.setVisibility(View.VISIBLE);
	}
	Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree)
	{

        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        float targetX, targetY;
        if (orientationDegree == 90)
		{
			targetX = bm.getHeight();
			targetY = 0;
        }
		else
		{
			targetX = bm.getHeight();
			targetY = bm.getWidth();
		}

		final float[] values = new float[9];
		m.getValues(values);

		float x1 = values[Matrix.MTRANS_X];
		float y1 = values[Matrix.MTRANS_Y];

		m.postTranslate(targetX - x1, targetY - y1);

		Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Bitmap.Config.ARGB_8888);

		Paint paint = new Paint();
		Canvas canvas = new Canvas(bm1);
		canvas.drawBitmap(bm, m, paint);


		return bm1;
	}
	private void initSurfaceView()
	{
		surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		surfaceView.inbit(BitmapFactory.decodeResource(this.getResources(), R.drawable.cameraopen));

        sut.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        callback = new SurfaceHolder.Callback() {

            //在控件创建的时候，进行相应的初始化工作
            public void surfaceCreated(SurfaceHolder holder)
			{
                //打开相机，同时进行各种控件的初始化mediaRecord等
                camera = Camera.open();

            }

            //当控件发生变化的时候调用，进行surfaceView和camera进行绑定，可以进行画面的显示
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
			{
                doChange(holder);
			}

            @Override
            public void surfaceDestroyed(SurfaceHolder holder)
			{

                camera.stopPreview();

            }

        };
        //为SurfaceView设置回调函数
		sut.getHolder().addCallback(callback);
    }
	ByteArrayOutputStream baos;
    byte[] rawImage;
    Bitmap bitmap;
    Camera.Size previewSize;

    //当我们的程序开始运行，即使我们没有开始录制视频，我们的surFaceView中也要显示当前摄像头显示的内容
    private void doChange(SurfaceHolder holder)
	{
		try
		{
            camera.setPreviewDisplay(holder);
            //设置surfaceView旋转的角度，系统默认的录制是横向的画面，把这句话注释掉运行你就会发现这行代码的作用
            camera.setDisplayOrientation(getDegree());
			camera.setPreviewCallback(new Camera.PreviewCallback() {
					@Override
					public void onPreviewFrame(byte[] data, Camera camera)
					{
						//处理data
						previewSize = camera.getParameters().getPreviewSize();//获取尺寸,格式转换的时候要用到
						YuvImage yuvimage = new YuvImage(
                            data,
                            ImageFormat.NV21,
                            previewSize.width,
                            previewSize.height,
                            null);
						baos = new ByteArrayOutputStream();
						yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 75, baos);// 80--JPG图片的质量[0-100],100最高
						rawImage = baos.toByteArray();


						bitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length);

						BlurDrawable blurDrawable = new BlurDrawable(MainActivity.this, getResources(), adjustPhotoRotation(bitmap, getDegree()), 7, 20);

						surfaceView.inbit(blurDrawable.getBlurDrawable());
						surfaceView.myDraw();
						bitmap.recycle();
						bitmap = null;
						yuvimage = null;


					}
				});
            camera.startPreview();//开始预览

        }
		catch (IOException e)
		{
            e.printStackTrace();
        }
    }
	int getDegree()
	{
        //获取当前屏幕旋转的角度
        int rotating = this.getWindowManager().getDefaultDisplay().getRotation();
        int degree = 0;
        //根据手机旋转的角度，来设置surfaceView的显示的角度
        switch (rotating)
		{
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }
        return degree;
    }
}
