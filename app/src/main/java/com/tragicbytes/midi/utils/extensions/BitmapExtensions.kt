package com.tragicbytes.midi.utils.extensions

import android.Manifest
import android.app.ActionBar
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import android.util.Base64
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.RequiresPermission
import com.tragicbytes.midi.models.AdDetails
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.net.URL


/**
 * Returns Bitmap Width And Height Presented as a Pair of two Int where pair.first is width and pair.second is height
 */
fun Bitmap.size(): Pair<Int, Int> = Pair(width, height)

/**
 * Save Bitmap to the provided Path.
 * Make Sure you have the permission to write the file to.
 */
@RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
fun Bitmap.save(to: String, format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG, quality: Int = 100, recycle: Boolean = true): Boolean = FileOutputStream(File(to)).use {
    this.compress(format, quality, it)
    it.flush()
    it.close()
    if (recycle)
        recycle()
    true
}

/**
 * Save Bitmap to the provided Path <b>Asynchronously</b> and private a callback when its done.
 *
 * Make Sure you have the permission to write the file to.
 */
@RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
fun Bitmap.saveAsync(to: String, format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG, quality: Int = 100, recycle: Boolean = true, onComplete: (isSaved: String) -> Unit) = asyncAwait({
    this.save(to, format, quality, recycle)
}, {
    onComplete(to)
})

fun <T> asyncAwait(asyncRunnable: () -> T?, awaitRunnable: (result: T?) -> Unit) = object : AsyncTask<Void, Void, T>() {
    override fun doInBackground(vararg params: Void?): T? {
        return try {
            asyncRunnable()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onPostExecute(result: T?) {
        super.onPostExecute(result)
        awaitRunnable(result)
    }

}.execute()!!

/**
 * get Pixel from Bitmap Easily
 */
operator fun Bitmap.get(x: Int, y: Int) = getPixel(x, y)

/**
 * get Pixel to Bitmap Easily
 */
operator fun Bitmap.set(x: Int, y: Int, pixel: Int) = setPixel(x, y, pixel)

/**
 * Crop image easily.
 * @param r is the Rect to crop from the Bitmap
 *
 * @return cropped #android.graphics.Bitmap
 */
fun Bitmap.crop(r: Rect) = if (Rect(0, 0, width, height).contains(r)) Bitmap.createBitmap(this, r.left, r.top, r.width(), r.height()) else null

/**
 * Converts Bitmap to Base64 Easily.
 */
fun Bitmap.toBase64(compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG): String {
    val result: String
    val baos = ByteArrayOutputStream()
    compress(compressFormat, 100, baos)
    baos.flush()
    baos.close()
    val bitmapBytes = baos.toByteArray()
    result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT)
    baos.flush()
    baos.close()
    return result
}

/**
 * resize Bitmap With a ease. Just call [resize] with the [w] and [h] and you will get new Resized Bitmap
 */
fun Bitmap.resize(w: Number, h: Number, recycle: Boolean = true): Bitmap {
    val width = width
    val height = height
    val scaleWidth = w.toFloat() / width
    val scaleHeight = h.toFloat() / height
    val matrix = Matrix()
    matrix.postScale(scaleWidth, scaleHeight)
    if (width > 0 && height > 0) {
        val newBitmap = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
        if (recycle && isRecycled.not() && newBitmap != this)
            recycle()
        return newBitmap
    }
    return this
}

/**
 * rotate Bitmap With a ease. Just call [rotateTo] with the [angle] and you will get new Resized Bitmap
 */
fun Bitmap.rotateTo(angle: Float, recycle: Boolean = true): Bitmap {
    val matrix = Matrix()
    matrix.setRotate(angle)
    val newBitmap = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    if (recycle && isRecycled.not() && newBitmap != this)
        recycle()
    return newBitmap
}

/**
 * Makes the Bitmap Round with given params
 */
fun Bitmap.toRound(borderSize: Float = 0f, borderColor: Int = 0, recycle: Boolean = true): Bitmap {
    val width = width
    val height = height
    val size = Math.min(width, height)
    val paint = Paint(ANTI_ALIAS_FLAG)
    val ret = Bitmap.createBitmap(width, height, config)
    val center = size / 2f
    val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
    rectF.inset((width - size) / 2f, (height - size) / 2f)
    val matrix = Matrix()
    matrix.setTranslate(rectF.left, rectF.top)
    if (width != height) {
        matrix.preScale(size.toFloat() / width, size.toFloat() / height)
    }
    val shader = BitmapShader(this, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    shader.setLocalMatrix(matrix)
    paint.shader = shader
    val canvas = Canvas(ret)
    canvas.drawRoundRect(rectF, center, center, paint)
    if (borderSize > 0) {
        paint.shader = null
        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderSize
        val radius = center - borderSize / 2f
        canvas.drawCircle(width / 2f, height / 2f, radius, paint)
    }
    if (recycle && !isRecycled && ret != this) recycle()
    return ret
}

/**
 * Blend the Bitmap Corners to Round with Given radius
 */
fun Bitmap.toRoundCorner(radius: Float, borderSize: Float = 0f, @ColorInt borderColor: Int = 0, recycle: Boolean): Bitmap {
    val width = width
    val height = height
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val ret = Bitmap.createBitmap(width, height, config)
    val shader = BitmapShader(this, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
    paint.shader = shader
    val canvas = Canvas(ret)
    val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
    val halfBorderSize = borderSize / 2f
    rectF.inset(halfBorderSize, halfBorderSize)
    canvas.drawRoundRect(rectF, radius, radius, paint)
    if (borderSize > 0) {
        paint.shader = null
        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderSize
        paint.strokeCap = Paint.Cap.ROUND
        canvas.drawRoundRect(rectF, radius, radius, paint)
    }
    if (recycle && !isRecycled && ret != this) recycle()
    return ret
}

/**
 * Want the Image to GreyScale? Just call [toGrayScale] and get the grey Image.
 */
fun Bitmap.toGrayScale(recycle: Boolean): Bitmap? {
    val ret = Bitmap.createBitmap(width, height, config)
    val canvas = Canvas(ret)
    val paint = Paint()
    val colorMatrix = ColorMatrix()
    colorMatrix.setSaturation(0f)
    val colorMatrixColorFilter = ColorMatrixColorFilter(colorMatrix)
    paint.colorFilter = colorMatrixColorFilter
    canvas.drawBitmap(this, 0f, 0f, paint)
    if (recycle && !isRecycled && ret != this) recycle()
    return ret
}

/**
 * Converts Bitmap to ByteArray Easily.
 */
fun Bitmap.toByteArray(compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG): ByteArray {
    val stream = ByteArrayOutputStream()
    compress(compressFormat, 100, stream)
    return stream.toByteArray()
}

/**
 * Compress Bitmap by Sample Size
 */
fun Bitmap.compressBySampleSize(maxWidth: Int, maxHeight: Int, recycle: Boolean = true): Bitmap? {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    val baos = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val bytes = baos.toByteArray()
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
    options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
    options.inJustDecodeBounds = false
    if (recycle && !isRecycled) recycle()
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
}

/**
 * Compress Bitmap by Quality
 */
fun Bitmap.compressByQuality(quality: Int, recycle: Boolean = true): Bitmap? {
    val baos = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, quality, baos)
    val bytes = baos.toByteArray()
    if (recycle && !isRecycled) recycle()
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}


//***********Private Methods Are below**********************
private fun calculateInSampleSize(options: BitmapFactory.Options, maxWidth: Int, maxHeight: Int): Int {
    var height = options.outHeight
    var width = options.outWidth
    var inSampleSize = 1
    do {
        width = width shr 1
        height = height shr 1
        val bool = width >= maxWidth && height >= maxHeight
        if (bool.not())
            break
        else
            inSampleSize = inSampleSize shl 1
    } while (true)
    return inSampleSize
}

fun Bitmap.makeCircle(): Bitmap? {
    val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)

    val color = -0xbdbdbe
    val paint = Paint()
    val rect = Rect(0, 0, width, height)

    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)
    paint.color = color

    canvas.drawCircle(width.div(2f), height.div(2f), width.div(2f), paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(this, rect, rect, paint)

    return output
}

fun Bitmap.toDrawable(context: Context): Drawable {
    return BitmapDrawable(context.resources, this)
}

fun Drawable.toBitmap(): Bitmap? {
    val bitmap: Bitmap? = if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    } else {
        Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
    }

    if (this is BitmapDrawable) {
        if (this.bitmap != null) {
            return this.bitmap
        }
    }

    val canvas = Canvas(bitmap!!)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)

    return bitmap
}

@Throws(FileNotFoundException::class)
fun Uri.toBitmap(context: Context): Bitmap {
    return MediaStore.Images.Media.getBitmap(context.contentResolver, this)
}
/**
 * Draw text over bitmap
 * **/

fun drawTextToBitmap(mContext: Context, bitmap: Bitmap, adDetails: AdDetails): Bitmap? {
    return try {
        val resources: Resources = mContext.resources
        val scale: Float = resources.displayMetrics.density
        val widthPxl: Int = resources.displayMetrics.widthPixels
        val heightPxl: Int = resources.displayMetrics.heightPixels
        var bitmap = bitmap
        var bitmapConfig = bitmap.config
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true)
        val canvas = Canvas(bitmap)
        // new antialised Paint
        //val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        // text color - #3D3D3D
        //paint.color = Color.rgb(110, 110, 110)
        // text size in pixels
        //paint.textSize = (50 * scale).toFloat()
        // text shadow
        //paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY)
        // draw text to the Canvas center
        val bounds = Rect()
        //paint.getTextBounds(mText, 0, mText.length, bounds)
        /*val x: Int = (bitmap.width - bounds.width()) / 6
        val y: Int = (bitmap.height + bounds.height()) / 5*/
        val x=widthPxl-widthPxl/4
        val y=heightPxl/4
        /*val layout = LinearLayout(mContext)
        layout.gravity=Gravity.CENTER
        val textView = TextView(mContext)
        textView.text = "Hello world"
        textView.textSize=140F
        textView.gravity=Gravity.CENTER
        textView.layoutParams = LinearLayout.LayoutParams(canvas.width, LinearLayout.LayoutParams.WRAP_CONTENT)
        layout.addView(textView)*/
        val parent = LinearLayout(mContext)

        parent.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.WRAP_CONTENT
        )
        parent.orientation = LinearLayout.VERTICAL
        parent.gravity=Gravity.CENTER

        //children of parent linearlayout
        val iv = ImageView(mContext)
        val imageAsBytes=
            Base64.decode(adDetails.logoUrl, Base64.DEFAULT)
        iv.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size))


        val layout2 = LinearLayout(mContext)

        layout2.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layout2.orientation = LinearLayout.VERTICAL

        parent.addView(iv)
        parent.addView(layout2)

        //children of layout2 LinearLayout
        val tv1 = TextView(mContext)
        tv1.text = adDetails.adName
        tv1.textSize=140F
        tv1.gravity=Gravity.CENTER
        val tv2 = TextView(mContext)
        tv2.text = adDetails.adBrandName
        tv2.textSize=100F
        tv2.gravity=Gravity.CENTER
        val tv3 = TextView(mContext)
        tv3.text = adDetails.adTagline
        tv3.textSize=80F
        tv3.gravity=Gravity.CENTER
        val tv4 = TextView(mContext)
        tv4.text = adDetails.adDesc
        tv4.textSize=50F
        tv4.gravity=Gravity.CENTER

        layout2.addView(tv1)
        layout2.addView(tv2)
        layout2.addView(tv3)
        layout2.addView(tv4)
        parent.measure(canvas.width, canvas.height)
        parent.layout(0, 0, canvas.width, canvas.height)

        // To place the text view somewhere specific:
        //canvas.translate(x.toFloat(), y.toFloat());
        parent.draw(canvas)
        //canvas.drawText(mText, x.toFloat(), y.toFloat(), paint)
        bitmap
    } catch (e: Exception) {
        null
    }
}

/**
 * Pull image from Path <b>Asynchronously</b> and private a callback when its done.
 *
 * Make Sure you have the permission to Internet
 */
@RequiresPermission(Manifest.permission.INTERNET)
fun fetchImageAsync(imageUrl:String, onComplete: (bitmap: Bitmap?) -> Unit) = imageAsyncAwait({
    val url = URL(imageUrl)
    return@imageAsyncAwait BitmapFactory.decodeStream(url.openStream())
}) {
    onComplete(it)
}

fun <T> imageAsyncAwait(asyncRunnable: () -> T?, awaitRunnable: (result: T?) -> Unit) = object : AsyncTask<Void, Void, T>() {
    override fun doInBackground(vararg params: Void?): T? {
        return try {
            asyncRunnable()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onPostExecute(result: T?) {
        super.onPostExecute(result)
        awaitRunnable(result)
    }

}.execute()!!

