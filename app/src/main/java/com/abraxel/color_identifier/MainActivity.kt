package com.abraxel.color_identifier

import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Palette.Swatch
import java.io.FileDescriptor
import java.io.IOException
import java.util.*
import kotlin.math.floor


class MainActivity : AppCompatActivity() {

    lateinit var imageView: ImageView
    lateinit var dominantColorImgView: ImageView
    lateinit var button: Button
    private val pickImage = 100
    var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.img_view)
        dominantColorImgView = findViewById(R.id.img_color_shower)
        button = findViewById(R.id.chooser_button)

        button.setOnClickListener{
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

    }
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imageUri = data?.data
        if (resultCode == RESULT_OK && requestCode == pickImage) {

            var bitmap = uriToBitmap(imageUri)
            imageView.setImageBitmap(bitmap)

            bitmap = (imageView.drawable as BitmapDrawable).bitmap

            val dominantColor = getDominantColor(bitmap)
            val hexColor = String.format("#%06X", 0xFFFFFF and dominantColor)
            val dominantImageView = drawRectForColor(dominantColor)
            dominantColorImgView.setImageBitmap(dominantImageView)



        }

    }

    private fun drawRectForColor(color : Int): Bitmap {
        val conf = Bitmap.Config.ARGB_8888
        val bmp = Bitmap.createBitmap(400,50,conf)
        val canvas = Canvas(bmp)
        val paint = Paint()
        paint.color = color
        canvas.drawColor(color)
        return bmp
    }

    private fun getDominantColor(bitmap: Bitmap?): Int {
        val swatchesTemp = Palette.from(bitmap!!).generate().swatches
        val swatches: List<Swatch> = ArrayList(swatchesTemp)
        Collections.sort(swatches
        ) { o1, o2 -> o2.population - o1.population }
        return if (swatches.isNotEmpty()) swatches[0].rgb else getRandomColor()
    }

    private fun getRandomColor(): Int {
        val random = Random()
        val RGB = 0xff + 1
        val a = 256
        val r1 = floor(Math.random() * RGB).toInt()
        val r2 = floor(Math.random() * RGB).toInt()
        val r3 = floor(Math.random() * RGB).toInt()
        return Color.rgb(r1, r2, r3)
    }

    private fun uriToBitmap(selectedFileUri: Uri?): Bitmap? {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(selectedFileUri!!, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}