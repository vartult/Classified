package com.example.classified

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import java.util.HashMap
import android.widget.Toast
import android.os.AsyncTask.execute
import android.os.AsyncTask
import java.io.IOException
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.OnProgressListener
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream


class PostFragment : Fragment(), Dialog_selector.OnPhotoSelectedListener {
    override fun getImagePath(imagePath: Uri) {
        UniversalImageLoader.initImageLoader(this.context!!)
        UniversalImageLoader.setImage(imagePath.toString(),post_image!!)
        mSelectedBitmap = null
        mSelectedUri = imagePath
    }

    override fun getImageBitmap(bitmap: Bitmap) {
        post_image!!.setImageBitmap(bitmap)
        mSelectedUri = null
        mSelectedBitmap = bitmap
    }


    private var mSelectedBitmap: Bitmap? = null
    private var mSelectedUri: Uri? = null
    private var mUploadBytes: ByteArray? = null
    private var mProgress = 0.0

    private var post_image: ImageView? = null
    private var input_title: TextView?= null
    private var input_description: TextView?= null
    private var price: TextView?= null
    private var input_country: TextView?= null
    private var input_state_province: TextView?= null
    private var input_city: TextView?= null
    private var input_email: TextView?= null
    private var btn_post: Button?= null
    private var mprogressBar: ProgressBar?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflate = inflater!!.inflate(R.layout.fragment_post, container, false)
        post_image=inflate.findViewById(R.id.post_image)
        input_title=inflate.findViewById(R.id.input_title)
        input_description=inflate.findViewById(R.id.input_description)
        price=inflate.findViewById(R.id.input_price)
        input_country=inflate.findViewById(R.id.input_country)
        input_state_province=inflate.findViewById(R.id.input_state_province)
        input_city=inflate.findViewById(R.id.input_city)
        input_email=inflate.findViewById(R.id.input_email)
        mprogressBar=inflate.findViewById(R.id.progressBar)
        btn_post=inflate.findViewById(R.id.btn_post)

        init()

        return inflate

    }

    fun init(){
        post_image!!.setOnClickListener{

            //Toast.makeText(context,"hello",Toast.LENGTH_LONG).show()
            var dialog = Dialog_selector()
            dialog.show(fragmentManager,"Select Photo")
            dialog.setTargetFragment(this@PostFragment,1)

        }

        btn_post!!.setOnClickListener(View.OnClickListener {

            if (!isEmpty(input_title!!.text.toString())
                && !isEmpty(input_description!!.text.toString())
                && !isEmpty(price!!.text.toString())
                && !isEmpty(input_country!!.text.toString())
                && !isEmpty(input_state_province!!.text.toString())
                && !isEmpty(input_city!!.text.toString())
                && !isEmpty(input_email!!.text.toString())
            ) {

                //we have a bitmap and no Uri
                if (mSelectedBitmap != null && mSelectedUri == null) {
                    uploadNewPhoto(mSelectedBitmap!!)
                } else if (mSelectedBitmap == null && mSelectedUri != null) {
                    uploadNewPhoto(mSelectedUri!!)
                }//we have no bitmap and a uri
            } else {
                Toast.makeText(activity, "You must fill out all the fields", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun uploadNewPhoto(bitmap: Bitmap) {
        var resize: BackgroundImageResize = BackgroundImageResize(bitmap);
        var uri:Uri ?= null
        resize.execute(uri)
    }

    private fun uploadNewPhoto(imagePath: Uri) {
        var resize: BackgroundImageResize =  BackgroundImageResize(null)
        resize.execute(imagePath)
    }


    inner class BackgroundImageResize(bitmap: Bitmap?) : AsyncTask<Uri, Int, ByteArray>() {

        internal var mBitmap: Bitmap? = null

        init {
            if (bitmap != null) {
                this.mBitmap = bitmap
            }
        }

        override fun onPreExecute() {
            super.onPreExecute()
            Toast.makeText(activity, "compressing image", Toast.LENGTH_SHORT).show()
            showProgressBar()
        }

        override fun doInBackground(vararg params: Uri): ByteArray {

            if (mBitmap == null) {
                try {
                    val rotateBitmap = RotateBitmap()
                    mBitmap = rotateBitmap.HandleSamplingAndRotationBitmap(context!!, params[0])
                } catch (e: IOException) {
                }

            }
            var bytes: ByteArray? = null
            bytes = getBytesFromBitmap(mBitmap!!, 100)
            return bytes
        }

        override fun onPostExecute(bytes: ByteArray) {
            super.onPostExecute(bytes)
            mUploadBytes = bytes
            hideProgressBar()
            //execute the upload task
            executeUploadTask()
        }
    }


    private fun executeUploadTask() {
Toast.makeText(activity, "uploading image", Toast.LENGTH_SHORT).show()

val postId: String = FirebaseDatabase.getInstance().reference.push().key!!

val storageReference: StorageReference = FirebaseStorage.getInstance().reference
.child(("posts/users/" + FirebaseAuth.getInstance().currentUser!!.uid +
"/" + postId + "/post_image"))

val uploadTask = storageReference.putBytes(mUploadBytes!!)
uploadTask.addOnSuccessListener {
    Toast.makeText(activity, "Post Success", Toast.LENGTH_SHORT).show()

    //insert the download url into the firebase database
    val firebaseUri= storageReference.downloadUrl
    val reference: DatabaseReference = FirebaseDatabase.getInstance().reference

    val post = Post()
    post.setImage(firebaseUri.toString())
    post.setCity(input_city!!.text.toString())
    post.setContact_email(input_email!!.text.toString())
    post.setCountry(input_country!!.text.toString())
    post.setDescription(input_description!!.text.toString())
    post.setPost_id(postId!!)
    post.setPrice(price!!.text.toString())
    post.setState_province(input_state_province!!.text.toString())
    post.setTitle(input_title!!.text.toString())
    post.setUser_id(FirebaseAuth.getInstance().currentUser!!.uid)
    reference.child("posts")
        .child(postId)
        .setValue(post)

    resetFields()
}.addOnFailureListener { Toast.makeText(activity, "could not upload photo", Toast.LENGTH_SHORT).show() }.addOnProgressListener { taskSnapshot ->
    val currentProgress = ((100 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount).toDouble()
    if (currentProgress > (mProgress + 15)) {
        mProgress = ((100 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount).toDouble()
        Toast.makeText(activity, (mProgress).toString() + "%", Toast.LENGTH_SHORT).show()
    }
}
    }

     fun getBytesFromBitmap(bitmap:Bitmap, quality:Int):ByteArray {
val stream = ByteArrayOutputStream()
bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
return stream.toByteArray()
}



    private fun resetFields(){
        post_image=null
        input_title!!.text = " "
        input_description!!.text=" "
        price!!.text=" "
        input_country!!.text=" "
        input_state_province!!.text=" "
        input_city!!.text= " "
        input_email!!.text=" "
    }

    private fun showProgressBar() {
        mprogressBar?.visibility = View.VISIBLE

    }

    private fun hideProgressBar() {
        if (mprogressBar?.visibility == View.VISIBLE) {
            mprogressBar?.visibility = View.INVISIBLE
        }
    }

    companion object {
        fun newInstance(): PostFragment = PostFragment()
    }

    private fun isEmpty(string: String): Boolean {
        return string == ""
    }
}