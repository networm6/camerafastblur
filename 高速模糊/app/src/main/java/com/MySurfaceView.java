package com;
import android.view.SurfaceHolder;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.content.Context;
import android.view.SurfaceView;
import android.util.AttributeSet;
import android.graphics.Matrix;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback{

	private SurfaceHolder sfh;
	private Paint paint;

	Canvas canvas;
	Bitmap pic;
	public MySurfaceView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		sfh=this.getHolder();
		sfh.addCallback(this);
		paint=new Paint();

	}

	public MySurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context,attrs,defStyle);
		init(context);
	}

	public MySurfaceView(Context context, AttributeSet attrs) {
		super(context,attrs);
		init(context);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {


	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if(pic!=null)
			myDraw();
	}
	public  Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
		// 获得图片的宽高
		int width = bm.getWidth();
		int height = bm.getHeight();
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
		return newbm;
	}
	public void myDraw() {
		try {

			canvas = sfh.lockCanvas();

			if (canvas != null) {

				Matrix matrix=new Matrix();

				matrix.postTranslate(0, 0);

				canvas.drawBitmap(zoomImg(pic,(int)this.getWidth(),(int)this.getHeight()), matrix, paint);

				sfh.unlockCanvasAndPost(canvas);

				pic.recycle();
				System.gc();
			}

		} catch (Exception e) {}


	}


	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}


	public void inbit(Bitmap in){
		pic=in;

	}


}



