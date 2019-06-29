package com.mybulr;

import android.graphics.Bitmap;
import android.content.res.Resources;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;

public class BlurDrawable{   
Bitmap bp;
    public BlurDrawable(Context context,Resources res, Bitmap bitmap,int bu,int sc) {     
		bp= new BitmapDrawable(res,BitmapBlurHelper.doBlur(context,bitmap,bu,sc)).getBitmap();	
    }

    
    public Bitmap getBlurDrawable() {
        return bp;
    }

  
}

