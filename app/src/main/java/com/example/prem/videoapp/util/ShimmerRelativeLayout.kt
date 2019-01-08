package com.example.prem.videoapp.util

import android.animation.*
import android.content.Context
import android.graphics.*
import android.util.*
import android.view.ViewTreeObserver
import android.widget.RelativeLayout
import com.example.prem.videoapp.R

/**
 * Class for creating animated shimmer layouts.
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class ShimmerRelativeLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RelativeLayout(context, attrs, defStyle) {

    companion object {
        private const val TAG = "ShimmerLayout"
        private val DST_IN_PORTER_DUFF_TRANSFER_MODE = PorterDuffXfermode(PorterDuff.Mode.DST_IN)

        private fun clamp(value: Float): Float {
            return Math.min(1.toFloat(), Math.max(0.toFloat(), value))
        }

        /**
         * Creates a bitmap with the given width and height.
         *
         *
         * If it fails with an OutOfMemory error, it will force a GC and then try to create the bitmap
         * one more time.
         *
         * @param width  width of the bitmap
         * @param height height of the bitmap
         */
        private fun createBitmapAndGcIfNecessary(width: Int, height: Int): Bitmap {
            return try {
                Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            } catch (e: OutOfMemoryError) {
                System.gc()
                Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            }

        }
    }

    private val mAlphaPaint: Paint
    private val mMaskPaint: Paint

    private val mMask: Mask
    private var mMaskTranslation: MaskTranslation? = null

    private var mRenderMaskBitmap: Bitmap? = null
    private var mRenderUnmaskBitmap: Bitmap? = null

    /**
     * Is 'auto start' enabled for this layout. When auto start is enabled, the layout will start animating automatically
     * whenever it is attached to the current window.
     *
     * @return True if 'auto start' is enabled, false otherwise
     */
    /**
     * Enable or disable 'auto start' for this layout. When auto start is enabled, the layout will start animating
     * automatically whenever it is attached to the current window.
     *
     * [isAutoStart] Whether auto start should be enabled or not
     */
    var isAutoStart: Boolean = false
        set(autoStart) {
            field = autoStart
            resetAll()
        }
    /**
     * Get the duration of the current animation i.e. the time it takes for the highlight to move from one end
     * of the layout to the other. The default value is 1000 ms.
     *
     * @return Duration of the animation, in milliseconds
     */
    /**
     * Set the duration of the animation i.e. the time it will take for the highlight to move from one end of the layout
     * to the other.
     *
     * [duration] Duration of the animation, in milliseconds
     */
    var duration: Int = 0
        set(duration) {
            field = duration
            resetAll()
        }
    /**
     * Get the number of times of the current animation will repeat. The default value is -1, which means the animation
     * will repeat indefinitely.
     *
     * @return Number of times the current animation will repeat, or -1 for indefinite.
     */
    /**
     * Set the number of times the animation should repeat. If the repeat count is 0, the animation stops after reaching
     * the end. If greater than 0, or -1 (for infinite), the repeat mode is taken into account.
     *
     * [repeatCount] Number of times the current animation should repeat, or -1 for indefinite.
     */
    var repeatCount: Int = 0
        set(repeatCount) {
            field = repeatCount
            resetAll()
        }
    /**
     * Get the delay after which the current animation will repeat. The default value is 0, which means the animation
     * will repeat immediately, unless it has ended.
     *
     * @return Delay after which the current animation will repeat, in milliseconds.
     */
    /**
     * Set the delay after which the animation repeat, unless it has ended.
     *
     * [repeatDelay] Delay after which the animation should repeat, in milliseconds.
     */
    var repeatDelay: Int = 0
        set(repeatDelay) {
            field = repeatDelay
            resetAll()
        }
    /**
     * Get what the current animation will do after reaching the end. One of
     * [REVERSE](http://developer.android.com/reference/android/animation/ValueAnimator.html#REVERSE) or
     * [RESTART](http://developer.android.com/reference/android/animation/ValueAnimator.html#RESTART)
     *
     * @return Repeat mode of the current animation
     */
    /**
     * Set what the animation should do after reaching the end. One of
     * [REVERSE](http://developer.android.com/reference/android/animation/ValueAnimator.html#REVERSE) or
     * [RESTART](http://developer.android.com/reference/android/animation/ValueAnimator.html#RESTART)
     *
     * [repeatMode] Repeat mode of the animation
     */
    var repeatMode: Int = 0
        set(repeatMode) {
            field = repeatMode
            resetAll()
        }

    private var mMaskOffsetX: Int = 0
    private var mMaskOffsetY: Int = 0

    /**
     * Whether the shimmer animation is currently underway.
     *
     * @return True if the shimmer animation is playing, false otherwise.
     */
    var isAnimationStarted: Boolean = false
        private set
    private var mGlobalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    private var mAnimator: ValueAnimator? = null
    private var mMaskBitmap: Bitmap? = null

    /**
     * Get the alpha currently used to render the base view i.e. the unHighlighted view over which the highlight is drawn.
     *
     * @return Alpha (opacity) of the base view
     */
    /**
     * Set the alpha to be used to render the base view i.e. the unHighlighted view over which the highlight is drawn.
     *
     * [baseAlpha] Alpha (opacity) of the base view
     */
    var baseAlpha: Float
        get() = mAlphaPaint.alpha.toFloat() / 0xff
        set(alpha) {
            mAlphaPaint.alpha = (clamp(alpha) * 0xff).toInt()
            resetAll()
        }

    /**
     * Get the shape of the current animation's highlight mask. One of [MaskShape.LINEAR] or
     * [MaskShape.RADIAL]
     *
     * @return The shape of the highlight mask
     */
    /**
     * Set the shape of the animation's highlight mask. One of [MaskShape.LINEAR] or [MaskShape.RADIAL]
     *
     * [maskShape] The shape of the highlight mask
     */
    var maskShape: MaskShape?
        get() = mMask.shape
        set(shape) {
            mMask.shape = shape
            resetAll()
        }

    /**
     * Get the angle at which the highlight mask is animated. One of:
     *
     *  * [MaskAngle.CW_0] which animates left to right,
     *  * [MaskAngle.CW_90] which animates top to bottom,
     *  * [MaskAngle.CW_180] which animates right to left, or
     *  * [MaskAngle.CW_270] which animates bottom to top
     *
     *
     * @return The [MaskAngle] of the current animation
     */
    /**
     * Set the angle of the highlight mask animation. One of:
     *
     *  * [MaskAngle.CW_0] which animates left to right,
     *  * [MaskAngle.CW_90] which animates top to bottom,
     *  * [MaskAngle.CW_180] which animates right to left, or
     *  * [MaskAngle.CW_270] which animates bottom to top
     *
     * [angle] The [MaskAngle] of the new animation
     */
    var angle: MaskAngle?
        get() = mMask.angle
        set(angle) {
            mMask.angle = angle
            resetAll()
        }

    /**
     * Get the dropOff of the current animation's highlight mask. DropOff controls the size of the fading edge of the
     * highlight.
     *
     *
     * The default value of dropOff is 0.5.
     *
     * @return DropOff of the highlight mask
     */
    /**
     * Set the dropOff of the animation's highlight mask, which defines the size of the highlight's fading edge.
     *
     * It is the relative distance from the center at which the highlight mask's opacity is 0 i.e it is fully transparent.
     * For a linear mask, the distance is relative to the center towards the edges. For a radial mask, the distance is
     * relative to the center towards the circumference. So a dropOff of 0.5 on a linear mask will create a band that
     * is half the size of the corresponding edge (depending on the [MaskAngle]), centered in the layout.
     */
    var dropOff: Float
        get() = mMask.dropOff
        set(dropOff) {
            mMask.dropOff = dropOff
            resetAll()
        }

    /**
     * Get the fixed width of the highlight mask, or 0 if it is not set. By default it is 0.
     *
     * @return The width of the highlight mask if set, in pixels.
     */
    /**
     * Set the fixed width of the highlight mask, regardless of the size of the layout.
     *
     * [fixedWidth] The width of the highlight mask in pixels.
     */
    var fixedWidth: Int
        get() = mMask.fixedWidth
        set(fixedWidth) {
            mMask.fixedWidth = fixedWidth
            resetAll()
        }

    /**
     * Get the fixed height of the highlight mask, or 0 if it is not set. By default it is 0.
     *
     * @return The height of the highlight mask if set, in pixels.
     */
    /**
     * Set the fixed height of the highlight mask, regardless of the size of the layout.
     *
     * [fixedHeight] The height of the highlight mask in pixels.
     */
    var fixedHeight: Int
        get() = mMask.fixedHeight
        set(fixedHeight) {
            mMask.fixedHeight = fixedHeight
            resetAll()
        }

    /**
     * Get the intensity of the highlight mask, in the [0..1] range. The intensity controls the brightness of the
     * highlight; the higher it is, the greater is the opaque region in the highlight. The default value is 0.
     *
     * @return The intensity of the highlight mask
     */
    /**
     * Set the intensity of the highlight mask, in the [0..1] range.
     *
     *
     * Intensity is the point relative to the center where opacity starts dropping off, so an intensity of 0 would mean
     * that the highlight starts becoming translucent immediately from the center (the spread is controlled by 'dropOff').
     *
     * [intensity] The intensity of the highlight mask.
     */
    var intensity: Float
        get() = mMask.intensity
        set(intensity) {
            mMask.intensity = intensity
            resetAll()
        }

    /**
     * Get the width of the highlight mask relative to the layout's width. The default is 1.0, meaning that the mask is
     * of the same width as the layout.
     *
     * @return Relative width of the highlight mask.
     */
    val relativeWidth: Float
        get() = mMask.relativeWidth

    /**
     * Get the height of the highlight mask relative to the layout's height. The default is 1.0, meaning that the mask is
     * of the same height as the layout.
     *
     * @return Relative height of the highlight mask.
     */
    val relativeHeight: Float
        get() = mMask.relativeHeight

    /**
     * Get the tilt angle of the highlight, in degrees. The default value is 20.
     *
     * @return The highlight's tilt angle, in degrees.
     */
    /**
     * Set the tile angle of the highlight, in degrees.
     *
     * [tilt] The highlight's tilt angle, in degrees.
     */
    var tilt: Float
        get() = mMask.tilt
        set(tilt) {
            mMask.tilt = tilt
            resetAll()
        }

    private val layoutListener: ViewTreeObserver.OnGlobalLayoutListener
        get() = ViewTreeObserver.OnGlobalLayoutListener {
            val animationStarted = isAnimationStarted
            resetAll()
            if (isAutoStart || animationStarted) {
                startShimmerAnimation()
            }
        }

    // Return the mask bitmap, creating it if necessary.
    private// We need to increase the rect size to account for the tilt
    val maskBitmap: Bitmap?
        get() {
            if (mMaskBitmap != null) {
                return mMaskBitmap
            }

            val width = mMask.maskWidth(width)
            val height = mMask.maskHeight(height)

            mMaskBitmap = createBitmapAndGcIfNecessary(width, height)
            val canvas = Canvas(mMaskBitmap!!)
            val gradient: Shader
            when (mMask.shape) {
                ShimmerRelativeLayout.MaskShape.LINEAR -> {
                    val x1: Int
                    val y1: Int
                    val x2: Int
                    val y2: Int
                    when (mMask.angle) {
                        ShimmerRelativeLayout.MaskAngle.CW_0 -> {
                            x1 = 0
                            y1 = 0
                            x2 = width
                            y2 = 0
                        }
                        ShimmerRelativeLayout.MaskAngle.CW_90 -> {
                            x1 = 0
                            y1 = 0
                            x2 = 0
                            y2 = height
                        }
                        ShimmerRelativeLayout.MaskAngle.CW_180 -> {
                            x1 = width
                            y1 = 0
                            x2 = 0
                            y2 = 0
                        }
                        ShimmerRelativeLayout.MaskAngle.CW_270 -> {
                            x1 = 0
                            y1 = height
                            x2 = 0
                            y2 = 0
                        }
                        else -> {
                            x1 = 0
                            y1 = 0
                            x2 = width
                            y2 = 0
                        }
                    }
                    gradient = LinearGradient(
                        x1.toFloat(), y1.toFloat(),
                        x2.toFloat(), y2.toFloat(),
                        mMask.gradientColors,
                        mMask.gradientPositions,
                        Shader.TileMode.REPEAT
                    )
                }
                ShimmerRelativeLayout.MaskShape.RADIAL -> {
                    val x = width / 2
                    val y = height / 2
                    gradient = RadialGradient(
                        x.toFloat(),
                        y.toFloat(),
                        (Math.max(width, height) / Math.sqrt(2.0)).toFloat(),
                        mMask.gradientColors,
                        mMask.gradientPositions,
                        Shader.TileMode.REPEAT
                    )
                }
                else -> {
                    val x1: Int
                    val y1: Int
                    val x2: Int
                    val y2: Int
                    when (mMask.angle) {
                        ShimmerRelativeLayout.MaskAngle.CW_0 -> {
                            x1 = 0
                            y1 = 0
                            x2 = width
                            y2 = 0
                        }
                        ShimmerRelativeLayout.MaskAngle.CW_90 -> {
                            x1 = 0
                            y1 = 0
                            x2 = 0
                            y2 = height
                        }
                        ShimmerRelativeLayout.MaskAngle.CW_180 -> {
                            x1 = width
                            y1 = 0
                            x2 = 0
                            y2 = 0
                        }
                        ShimmerRelativeLayout.MaskAngle.CW_270 -> {
                            x1 = 0
                            y1 = height
                            x2 = 0
                            y2 = 0
                        }
                        else -> {
                            x1 = 0
                            y1 = 0
                            x2 = width
                            y2 = 0
                        }
                    }
                    gradient = LinearGradient(
                        x1.toFloat(),
                        y1.toFloat(),
                        x2.toFloat(),
                        y2.toFloat(),
                        mMask.gradientColors,
                        mMask.gradientPositions,
                        Shader.TileMode.REPEAT
                    )
                }
            }
            canvas.rotate(mMask.tilt, (width / 2).toFloat(), (height / 2).toFloat())
            val paint = Paint()
            paint.shader = gradient
            val padding = (Math.sqrt(2.0) * Math.max(width, height)).toInt() / 2
            canvas.drawRect(
                (-padding).toFloat(),
                (-padding).toFloat(),
                (width + padding).toFloat(),
                (height + padding).toFloat(),
                paint
            )

            return mMaskBitmap
        }

    // Get the shimmer <a href="http://developer.android.com/reference/android/animation/Animator.html">Animator</a>
    // object, which is responsible for driving the highlight mask animation.
    private val shimmerAnimation: Animator
        get() {
            if (mAnimator != null) {
                return mAnimator!!
            }
            val width = width
            val height = height
            when (mMask.shape) {
                ShimmerRelativeLayout.MaskShape.LINEAR -> when (mMask.angle) {
                    ShimmerRelativeLayout.MaskAngle.CW_0 -> mMaskTranslation!![-width, 0, width] = 0
                    ShimmerRelativeLayout.MaskAngle.CW_90 -> mMaskTranslation!![0, -height, 0] = height
                    ShimmerRelativeLayout.MaskAngle.CW_180 -> mMaskTranslation!![width, 0, -width] = 0
                    ShimmerRelativeLayout.MaskAngle.CW_270 -> mMaskTranslation!![0, height, 0] = -height
                    else -> mMaskTranslation!![-width, 0, width] = 0
                }
                else -> when (mMask.angle) {
                    ShimmerRelativeLayout.MaskAngle.CW_0 -> mMaskTranslation!![-width, 0, width] = 0
                    ShimmerRelativeLayout.MaskAngle.CW_90 -> mMaskTranslation!![0, -height, 0] = height
                    ShimmerRelativeLayout.MaskAngle.CW_180 -> mMaskTranslation!![width, 0, -width] = 0
                    ShimmerRelativeLayout.MaskAngle.CW_270 -> mMaskTranslation!![0, height, 0] = -height
                    else -> mMaskTranslation!![-width, 0, width] = 0
                }
            }
            mAnimator = ValueAnimator.ofFloat(0.0f, 1.0f + repeatDelay.toFloat() / duration)
            mAnimator?.duration = (duration + repeatDelay).toLong()
            mAnimator?.repeatCount = repeatCount
            mAnimator?.repeatMode = repeatMode
            mAnimator?.addUpdateListener { animation ->
                val value = Math.max(0.0f, Math.min(1.0f, animation.animatedValue as Float))
                setMaskOffsetX((mMaskTranslation!!.fromX * (1 - value) + mMaskTranslation!!.toX * value).toInt())
                setMaskOffsetY((mMaskTranslation!!.fromY * (1 - value) + mMaskTranslation!!.toY * value).toInt())
            }
            return mAnimator!!
        }

    // enum specifying the shape of the highlight mask applied to the contained view
    enum class MaskShape {
        LINEAR,
        RADIAL
    }

    // enum controlling the angle of the highlight mask animation
    enum class MaskAngle {
        CW_0, // left to right
        CW_90, // top to bottom
        CW_180, // right to left
        CW_270
        // bottom to top
    }

    // struct storing various mask related parameters, which are used to construct the mask bitmap
    private class Mask {

        internal var angle: MaskAngle? = null
        internal var tilt: Float = 0.toFloat()
        internal var dropOff: Float = 0.toFloat()
        internal var fixedWidth: Int = 0
        internal var fixedHeight: Int = 0
        internal var intensity: Float = 0.toFloat()
        internal var relativeWidth: Float = 0.toFloat()
        internal var relativeHeight: Float = 0.toFloat()
        var shape: MaskShape? = null

        /**
         * Get the array of colors to be distributed along the gradient of the mask bitmap
         *
         * @return An array of black and transparent colors
         */
        internal val gradientColors: IntArray
            get() {
                return when (shape) {
                    ShimmerRelativeLayout.MaskShape.LINEAR -> intArrayOf(
                        Color.TRANSPARENT,
                        Color.BLACK,
                        Color.BLACK,
                        Color.TRANSPARENT
                    )
                    ShimmerRelativeLayout.MaskShape.RADIAL -> intArrayOf(
                        Color.BLACK,
                        Color.BLACK,
                        Color.TRANSPARENT
                    )
                    else -> intArrayOf(Color.TRANSPARENT, Color.BLACK, Color.BLACK, Color.TRANSPARENT)
                }
            }

        /**
         * Get the array of relative positions [0..1] of each corresponding color in the colors array
         *
         * @return A array of float values in the [0..1] range
         */
        internal val gradientPositions: FloatArray
            get() {
                when (shape) {
                    ShimmerRelativeLayout.MaskShape.LINEAR -> return floatArrayOf(
                        Math.max(
                            (1.0f - intensity - dropOff) / 2,
                            0.0f
                        ),
                        Math.max((1.0f - intensity) / 2, 0.0f),
                        Math.min((1.0f + intensity) / 2, 1.0f),
                        Math.min((1.0f + intensity + dropOff) / 2, 1.0f)
                    )
                    ShimmerRelativeLayout.MaskShape.RADIAL -> return floatArrayOf(
                        0.0f,
                        Math.min(intensity, 1.0f),
                        Math.min(intensity + dropOff, 1.0f)
                    )
                    else -> return floatArrayOf(
                        Math.max((1.0f - intensity - dropOff) / 2, 0.0f),
                        Math.max((1.0f - intensity) / 2, 0.0f),
                        Math.min((1.0f + intensity) / 2, 1.0f),
                        Math.min((1.0f + intensity + dropOff) / 2, 1.0f)
                    )
                }
            }

        internal fun maskWidth(width: Int): Int {
            return if (fixedWidth > 0) fixedWidth else (width * relativeWidth).toInt()
        }

        internal fun maskHeight(height: Int): Int {
            return if (fixedHeight > 0) fixedHeight else (height * relativeHeight).toInt()
        }
    }

    // struct for storing the mask translation animation values
    private class MaskTranslation {
        internal var fromX: Int = 0
        internal var fromY: Int = 0
        internal var toX: Int = 0
        internal var toY: Int = 0

        operator fun set(fromX: Int, fromY: Int, toX: Int, toY: Int) {
            this.fromX = fromX
            this.fromY = fromY
            this.toX = toX
            this.toY = toY
        }
    }

    init {
        setWillNotDraw(false)

        mMask = Mask()
        mAlphaPaint = Paint()
        mMaskPaint = Paint()
        mMaskPaint.isAntiAlias = true
        mMaskPaint.isDither = true
        mMaskPaint.isFilterBitmap = true
        mMaskPaint.xfermode = DST_IN_PORTER_DUFF_TRANSFER_MODE

        useDefaults()

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.ShimmerRelativeLayout, 0, 0)
            try {
                if (a.hasValue(R.styleable.ShimmerRelativeLayout_auto_start)) {
                    isAutoStart = a.getBoolean(R.styleable.ShimmerRelativeLayout_auto_start, false)
                }
                if (a.hasValue(R.styleable.ShimmerRelativeLayout_base_alpha)) {
                    baseAlpha = a.getFloat(R.styleable.ShimmerRelativeLayout_base_alpha, 0f)
                }
                if (a.hasValue(R.styleable.ShimmerRelativeLayout_duration)) {
                    duration = a.getInt(R.styleable.ShimmerRelativeLayout_duration, 0)
                }
                if (a.hasValue(R.styleable.ShimmerRelativeLayout_repeat_count)) {
                    repeatCount = a.getInt(R.styleable.ShimmerRelativeLayout_repeat_count, 0)
                }
                if (a.hasValue(R.styleable.ShimmerRelativeLayout_repeat_delay)) {
                    repeatDelay = a.getInt(R.styleable.ShimmerRelativeLayout_repeat_delay, 0)
                }
                if (a.hasValue(R.styleable.ShimmerRelativeLayout_repeat_mode)) {
                    repeatMode = a.getInt(R.styleable.ShimmerRelativeLayout_repeat_mode, 0)
                }

                if (a.hasValue(R.styleable.ShimmerRelativeLayout_angle)) {
                    val angle = a.getInt(R.styleable.ShimmerRelativeLayout_angle, 0)
                    when (angle) {
                        0 -> mMask.angle = MaskAngle.CW_0
                        90 -> mMask.angle = MaskAngle.CW_90
                        180 -> mMask.angle = MaskAngle.CW_180
                        270 -> mMask.angle = MaskAngle.CW_270
                        else -> mMask.angle = MaskAngle.CW_0
                    }
                }

                if (a.hasValue(R.styleable.ShimmerRelativeLayout_shape)) {
                    val shape = a.getInt(R.styleable.ShimmerRelativeLayout_shape, 0)
                    when (shape) {
                        0 -> mMask.shape = MaskShape.LINEAR
                        1 -> mMask.shape = MaskShape.RADIAL
                        else -> mMask.shape = MaskShape.LINEAR
                    }
                }

                if (a.hasValue(R.styleable.ShimmerRelativeLayout_dropOff)) {
                    mMask.dropOff = a.getFloat(R.styleable.ShimmerRelativeLayout_dropOff, 0f)
                }
                if (a.hasValue(R.styleable.ShimmerRelativeLayout_fixed_width)) {
                    mMask.fixedWidth = a.getDimensionPixelSize(R.styleable.ShimmerRelativeLayout_fixed_width, 0)
                }
                if (a.hasValue(R.styleable.ShimmerRelativeLayout_fixed_height)) {
                    mMask.fixedHeight = a.getDimensionPixelSize(R.styleable.ShimmerRelativeLayout_fixed_height, 0)
                }
                if (a.hasValue(R.styleable.ShimmerRelativeLayout_intensity)) {
                    mMask.intensity = a.getFloat(R.styleable.ShimmerRelativeLayout_intensity, 0f)
                }
                if (a.hasValue(R.styleable.ShimmerRelativeLayout_relative_width)) {
                    mMask.relativeWidth = a.getFloat(R.styleable.ShimmerRelativeLayout_relative_width, 0f)
                }
                if (a.hasValue(R.styleable.ShimmerRelativeLayout_relative_height)) {
                    mMask.relativeHeight = a.getFloat(R.styleable.ShimmerRelativeLayout_relative_height, 0f)
                }
                if (a.hasValue(R.styleable.ShimmerRelativeLayout_tilt)) {
                    mMask.tilt = a.getFloat(R.styleable.ShimmerRelativeLayout_tilt, 0f)
                }
            } finally {
                a.recycle()
            }
        }
    }

    /**
     * Resets the layout to its default state. Any parameters that were set or modified will be reverted back to their
     * original value. Also, stops the shimmer animation if it is currently playing.
     */
    fun useDefaults() {
        // Set defaults
        isAutoStart = false
        duration = 1000
        repeatCount = ObjectAnimator.INFINITE
        repeatDelay = 0
        repeatMode = ObjectAnimator.RESTART

        mMask.angle = MaskAngle.CW_0
        mMask.shape = MaskShape.LINEAR
        mMask.dropOff = 0.5f
        mMask.fixedWidth = 0
        mMask.fixedHeight = 0
        mMask.intensity = 0.0f
        mMask.relativeWidth = 1.0f
        mMask.relativeHeight = 1.0f
        mMask.tilt = 20f

        mMaskTranslation = MaskTranslation()

        baseAlpha = 0.3f

        resetAll()
    }

    /**
     * Set the width of the highlight mask relative to the layout's width, in the [0..1] range.
     *
     * @param relativeWidth Relative width of the highlight mask.
     */
    fun setRelativeWidth(relativeWidth: Int) {
        mMask.relativeWidth = relativeWidth.toFloat()
        resetAll()
    }

    /**
     * Set the height of the highlight mask relative to the layout's height, in the [0..1] range.
     *
     * @param relativeHeight Relative height of the highlight mask.
     */
    fun setRelativeHeight(relativeHeight: Int) {
        mMask.relativeHeight = relativeHeight.toFloat()
        resetAll()
    }

    /**
     * Start the shimmer animation. If the 'auto start' property is set, this method is called automatically when the
     * layout is attached to the current window. Calling this method has no effect if the animation is already playing.
     */
    fun startShimmerAnimation() {
        if (isAnimationStarted) {
            return
        }
        val animator = shimmerAnimation
        animator.start()
        isAnimationStarted = true
    }

    /**
     * Stop the shimmer animation. Calling this method has no effect if the animation hasn't been started yet.
     */
    fun stopShimmerAnimation() {
        if (mAnimator != null) {
            mAnimator!!.end()
            mAnimator!!.removeAllUpdateListeners()
            mAnimator!!.cancel()
        }
        mAnimator = null
        isAnimationStarted = false
    }

    /**
     * Translate the mask offset horizontally. Used by the animator.
     *
     * @param maskOffsetX Horizontal translation offset of the mask
     */
    private fun setMaskOffsetX(maskOffsetX: Int) {
        if (mMaskOffsetX == maskOffsetX) {
            return
        }
        mMaskOffsetX = maskOffsetX
        invalidate()
    }

    /**
     * Translate the mask offset vertically. Used by the animator.
     *
     * @param maskOffsetY Vertical translation offset of the mask
     */
    private fun setMaskOffsetY(maskOffsetY: Int) {
        if (mMaskOffsetY == maskOffsetY) {
            return
        }
        mMaskOffsetY = maskOffsetY
        invalidate()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mGlobalLayoutListener == null) {
            mGlobalLayoutListener = layoutListener
        }
        viewTreeObserver.addOnGlobalLayoutListener(mGlobalLayoutListener)
    }

    override fun onDetachedFromWindow() {
        stopShimmerAnimation()
        if (mGlobalLayoutListener != null) {
            viewTreeObserver.removeOnGlobalLayoutListener(mGlobalLayoutListener)
            mGlobalLayoutListener = null
        }
        super.onDetachedFromWindow()
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (!isAnimationStarted || width <= 0 || height <= 0) {
            super.dispatchDraw(canvas)
            return
        }
        dispatchDrawUsingBitmap(canvas)
    }

    /**
     * Draws and masks the children using a Bitmap.
     *
     * @param canvas Canvas that the masked children will end up being drawn to.
     */
    private fun dispatchDrawUsingBitmap(canvas: Canvas) {
        val unmaskBitmap = tryObtainRenderUnmaskBitmap()
        val maskBitmap = tryObtainRenderMaskBitmap()
        if (unmaskBitmap == null || maskBitmap == null) {
            return
        }
        // First draw a desaturated version
        drawUnmasked(Canvas(unmaskBitmap))
        canvas.drawBitmap(unmaskBitmap, 0f, 0f, mAlphaPaint)

        // Then draw the masked version
        drawMasked(Canvas(maskBitmap))
        canvas.drawBitmap(maskBitmap, 0f, 0f, null)
    }

    private fun tryObtainRenderUnmaskBitmap(): Bitmap? {
        if (mRenderUnmaskBitmap == null) {
            mRenderUnmaskBitmap = tryCreateRenderBitmap()
        }
        return mRenderUnmaskBitmap
    }

    private fun tryObtainRenderMaskBitmap(): Bitmap? {
        if (mRenderMaskBitmap == null) {
            mRenderMaskBitmap = tryCreateRenderBitmap()
        }
        return mRenderMaskBitmap
    }

    private fun tryCreateRenderBitmap(): Bitmap? {
        val width = width
        val height = height
        try {
            return createBitmapAndGcIfNecessary(width, height)
        } catch (e: OutOfMemoryError) {
            var logMessage = "ShimmerLayout failed to create working bitmap"
            val logMessageStringBuilder = StringBuilder(logMessage)
            logMessageStringBuilder.append(" (width = ")
            logMessageStringBuilder.append(width)
            logMessageStringBuilder.append(", height = ")
            logMessageStringBuilder.append(height)
            logMessageStringBuilder.append(")\n\n")
            for (stackTraceElement in Thread.currentThread().stackTrace) {
                logMessageStringBuilder.append(stackTraceElement.toString())
                logMessageStringBuilder.append("\n")
            }
            logMessage = logMessageStringBuilder.toString()
            Log.d(TAG, logMessage)
        }

        return null
    }

    // Draws the children without any mask.
    private fun drawUnmasked(renderCanvas: Canvas) {
        renderCanvas.drawColor(0, PorterDuff.Mode.CLEAR)
        super.dispatchDraw(renderCanvas)
    }

    // Draws the children and masks them on the given Canvas.
    private fun drawMasked(renderCanvas: Canvas) {
        val maskBitmap = maskBitmap ?: return

        renderCanvas.clipRect(
            mMaskOffsetX,
            mMaskOffsetY,
            mMaskOffsetX + maskBitmap.width,
            mMaskOffsetY + maskBitmap.height
        )
        renderCanvas.drawColor(0, PorterDuff.Mode.CLEAR)
        super.dispatchDraw(renderCanvas)

        renderCanvas.drawBitmap(maskBitmap, mMaskOffsetX.toFloat(), mMaskOffsetY.toFloat(), mMaskPaint)
    }

    private fun resetAll() {
        stopShimmerAnimation()
        resetMaskBitmap()
        resetRenderedView()
    }

    // If a mask bitmap was created, it's recycled and set to null so it will be recreated when needed.
    private fun resetMaskBitmap() {
        if (mMaskBitmap != null) {
            mMaskBitmap!!.recycle()
            mMaskBitmap = null
        }
    }

    // If a working bitmap was created, it's recycled and set to null so it will be recreated when needed.
    private fun resetRenderedView() {
        if (mRenderUnmaskBitmap != null) {
            mRenderUnmaskBitmap!!.recycle()
            mRenderUnmaskBitmap = null
        }

        if (mRenderMaskBitmap != null) {
            mRenderMaskBitmap!!.recycle()
            mRenderMaskBitmap = null
        }
    }
}