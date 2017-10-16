package thinkreed.jcodec;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by thinkreed on 2017/10/16.
 */

public class CustomImageView extends View {

    private Bitmap bitmap;
    private Paint paint;
    private Rect srcRect;
    private Rect dstRect;

    public CustomImageView(Context context) {
        this(context, null);
    }

    public CustomImageView(Context context,
                           @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomImageView(Context context,
                           @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setFilterBitmap(true);
        srcRect = new Rect(0, 0, 300, 300);
        dstRect = new Rect(0, 0, 300, 300);
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
    }
}
