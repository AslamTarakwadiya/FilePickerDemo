package com.example.filepickerdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.os.Build
import android.app.Activity
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileNotFoundException


class MainActivity : AppCompatActivity() {
    var txtSDK: TextView? = null
    var txtUriPath: TextView? = null
    var txtRealPath: TextView? = null
    var imageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // get reference to views
        txtSDK = findViewById(R.id.txtSDK)
//        btnSelectImage =  findViewById(R.id.btnSelectImage)
        txtUriPath = findViewById(R.id.txtUriPath)
        txtRealPath = findViewById(R.id.txtRealPath)
        imageView = findViewById(R.id.imgView)

        // add click listener to button
        btnSelectImage.setOnClickListener(View.OnClickListener {
            // 1. on Upload click call ACTION_GET_CONTENT intent
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            // 2. pick image only
            intent.type = "image/*"
            // 3. start activity
            startActivityForResult(intent, 0)

            // define onActivityResult to do something with picked image
        })
    }

    override fun onActivityResult(reqCode: Int, resCode: Int, data: Intent?) {
        if (resCode == Activity.RESULT_OK && data != null) {
            val realPath: String
            // SDK < API11
            if (Build.VERSION.SDK_INT < 11)
                realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.data!!)
            else if (Build.VERSION.SDK_INT < 19)
                realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.data!!).toString()
            else
                realPath = RealPathUtil.getRealPathFromURI_API19(this, data.data!!)// SDK > 19 (Android 4.4)
            // SDK >= 11 && SDK < 19


            setTextViews(Build.VERSION.SDK_INT, data.data?.path!!, realPath)
        }
    }

    private fun setTextViews(sdk: Int, uriPath: String, realPath: String) {

        this.txtSDK?.setText("Build.VERSION.SDK_INT: $sdk")
        this.txtUriPath?.setText("URI Path: $uriPath")
        this.txtRealPath?.setText("Real Path: $realPath")

        val uriFromPath = Uri.fromFile(File(realPath))

        // you have two ways to display selected image

        // ( 1 ) imageView.setImageURI(uriFromPath);

        // ( 2 ) imageView.setImageBitmap(bitmap);
        var bitmap: Bitmap? = null
        try {
            bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uriFromPath))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        imageView?.setImageBitmap(bitmap)

        Log.d("HMKCODE", "Build.VERSION.SDK_INT:$sdk")
        Log.d("HMKCODE", "URI Path:$uriPath")
        Log.d("HMKCODE", "Real Path: $realPath")
    }
}
