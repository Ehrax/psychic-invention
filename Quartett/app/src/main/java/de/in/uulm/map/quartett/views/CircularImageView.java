package de.in.uulm.map.quartett.views;

/**
 * Created by maxka on 23.12.2016. Simple ImageView which displays the given
 * Image as a circle with white border. Use it the same way you would use the
 * android imageview.
 *
 * example: <de.in.uulm.map.quartett.views.CircularImageView
 * android:layout_width="60dp" android:layout_height="60dp"
 * android:src="your_picture_here"/>
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircularImageView extends ImageView {

    private int borderWidth = 2;
    private int viewWidth;
    private int viewHeight;
    private Bitmap image;
    private Paint paint;
    private Paint paintBorder;
    private BitmapShader shader;

    /**
     * Simple Constructor calls the setup() method to initialise the View
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public CircularImageView(Context context) {

        super(context);
        setup();
    }

    /**
     * Simple Constructor calls the setup() method to initialise the View
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public CircularImageView(Context context, AttributeSet attrs) {

        super(context, attrs);
        setup();
    }

    /**
     * Simple Constructor calls the setup() method to initialise the View
     *
     * @param context  The Context the view is running in, through which it can
     *                 access the current theme, resources, etc.
     * @param attrs    The attributes of the XML tag that is inflating the
     *                 view.
     * @param defStyle An attribute in the current theme that contains a
     *                 reference to a style resource that supplies default
     *                 values for the view. Can be 0 to not look for defaults.
     */
    public CircularImageView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        setup();
    }

    /**
     * Called by the constructor to initialise the views Paint and BorderColor
     */
    private void setup() {
        // init paint
        paint = new Paint();
        paint.setAntiAlias(true);

        paintBorder = new Paint();
        setBorderColor(Color.WHITE);
        paintBorder.setAntiAlias(true);
    }

    /**
     * Use this method to change the initial 2dp border width.
     *
     * @param borderWidth width if the ImageViews border
     */
    public void setBorderWidth(int borderWidth) {

        this.borderWidth = borderWidth;
        this.invalidate();
    }

    /**
     * Use this method to change the initial white border color.
     *
     * @param borderColor the new color as int. Call Color.argb(int a,int r, int
     *                    g,int b) to get an Integer representing a argb color.
     */
    public void setBorderColor(int borderColor) {

        if (paintBorder != null)
            paintBorder.setColor(borderColor);

        this.invalidate();
    }

    /**
     * This method is called from the onDraw method to load the views Image
     * which is set per the src attribute in xml.
     */
    private void loadBitmap() {

        BitmapDrawable bitmapDrawable = (BitmapDrawable) this.getDrawable();

        if (bitmapDrawable != null)
            image = bitmapDrawable.getBitmap();
    }

    /**
     * This method overrides the onDraw method from the extended ImageView.
     * Rendering the loaded Image as a circle.
     *
     * @param canvas the canvas where we draw the ImageView.
     */
    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        //load the bitmap
        loadBitmap();

        // init shader
        if (image != null) {
            shader = new BitmapShader(Bitmap.createScaledBitmap(image, canvas.getWidth(),
                    canvas.getHeight(), false), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setShader(shader);
            int circleCenter = viewWidth / 2;

            // circleCenter is the x or y of the view's center
            // radius is the radius in pixels of the circle to be drawn
            // paint contains the shader that will texture the shape
            canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth,
                    circleCenter + borderWidth, paintBorder);
            canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth,
                    circleCenter, paint);
        }
    }

    /**
     * Measure the view and its content to determine the measured width and the
     * measured height.
     *
     * @param widthMeasureSpec  horizontal space requirements as imposed by the
     *                          parent.
     * @param heightMeasureSpec vertical space requirements as imposed by the
     *                          parent.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec, widthMeasureSpec);

        viewWidth = width - (borderWidth * 2);
        viewHeight = height - (borderWidth * 2);

        setMeasuredDimension(width, height);
    }

    /**
     * Calculating the width of the view.
     *
     * @param measureSpec the measure specification to extract the mode and size
     *                    from
     * @return width of the view
     */
    private int measureWidth(int measureSpec) {

        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text
            result = viewWidth;

        }

        return result;
    }

    /**
     * Calculating the Height of the View.
     *
     * @param measureSpecHeight used to extract the mode and height from
     * @param measureSpecWidth  not used here because we have a circle, but
     *                          needed because we override ImageView which is a
     *                          rectangle.
     * @return height of the view
     */
    private int measureHeight(int measureSpecHeight, int measureSpecWidth) {

        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpecHeight);
        int specSize = MeasureSpec.getSize(measureSpecHeight);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text (beware: ascent is a negative number)
            result = viewHeight;
        }
        return result;
    }
}
