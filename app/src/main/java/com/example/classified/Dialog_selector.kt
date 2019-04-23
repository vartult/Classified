package com.example.classified

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.provider.MediaStore
import android.content.Intent
import android.graphics.Bitmap
import android.app.Activity
import android.content.Context
import android.net.Uri


class Dialog_selector : DialogFragment() {

    private val PICKFILE_REQUEST_CODE = 1234
    private val CAMERA_REQUEST_CODE = 4321

    interface OnPhotoSelectedListener {
        fun getImagePath(imagePath: Uri)
        fun getImageBitmap(bitmap: Bitmap)
    }

    var mOnPhotoSelectedListener: OnPhotoSelectedListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflate = inflater!!.inflate(R.layout.activity_dialog_selector, container, false)

        var choose=inflate.findViewById<TextView>(R.id.choose)
        var camera=inflate.findViewById<TextView>(R.id.camera)

        choose.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, PICKFILE_REQUEST_CODE)
        }

        camera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        }
        return inflate
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        /*
            Results when selecting a new image from memory
         */
        if (requestCode == PICKFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data!!.data

            //send the uri to PostFragment & dismiss dialog
            mOnPhotoSelectedListener!!.getImagePath(selectedImageUri)
            dialog.dismiss()
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val bitmap: Bitmap = data!!.extras!!.get("data") as Bitmap

            //send the bitmap to PostFragment and dismiss dialog
            mOnPhotoSelectedListener!!.getImageBitmap(bitmap)
            dialog.dismiss()
        }/*
            Results when taking a new photo with camera
         */
    }

    override fun onAttach(context: Context) {
        try {
            mOnPhotoSelectedListener = targetFragment as OnPhotoSelectedListener?
        } catch (e: ClassCastException) {
        }

        super.onAttach(context)
    }
}
