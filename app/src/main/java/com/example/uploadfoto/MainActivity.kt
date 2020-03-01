package com.example.uploadfoto

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.uploadfoto.BackEnd.ApiClient
import com.example.uploadfoto.Model.ResponseData
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.EasyImage.ImageSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var imgFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnKirimData.setOnClickListener {
            val nama = edtNama.text.toString().trim()
            val alamat = edtAlamat.text.toString().trim()

            if (nama.isEmpty() || alamat.isEmpty()) {
                Toast.makeText(applicationContext, "Jangan di kosongkan lur", Toast.LENGTH_SHORT)
                    .show()
            } else {
                dataDikirimSekarang()
            }

        }

        pickGambar.setOnClickListener {
            EasyImage.openChooserWithGallery(
                this@MainActivity,
                "Pilih",
                3
            );
        }

    }

    private fun dataDikirimSekarang() {
        loadingPG.visibility = View.VISIBLE
        btnKirimData.visibility = View.GONE

        val requestFile = RequestBody.create(MediaType.parse("multipart/from-data"), imgFile)
        val foto = MultipartBody.Part.createFormData("foto", imgFile.name, requestFile)

        val nama : RequestBody = RequestBody.create(MediaType.parse("text/plain"), edtNama.text.toString().trim())
        val alamat : RequestBody = RequestBody.create(MediaType.parse("text/plain"), edtAlamat.text.toString().trim())

        ApiClient.getApiClient.uploadData(nama, alamat, foto)
            .enqueue(object : Callback<ResponseData> {
                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
                    loadingPG.visibility = View.GONE
                    btnKirimData.visibility = View.VISIBLE
                }

                override fun onResponse(
                    call: Call<ResponseData>,
                    response: Response<ResponseData>
                ) {
                    Toast.makeText(this@MainActivity, response.body()!!.message, Toast.LENGTH_SHORT)
                        .show()
                    loadingPG.visibility = View.GONE
                    btnKirimData.visibility = View.VISIBLE
                    edtNama.text.clear()
                    edtAlamat.text.clear()
                }

            })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, object : DefaultCallback() {

            override fun onImagePicked(imageFile: File, source: ImageSource, type: Int) {
                CropImage.activity(Uri.fromFile(imageFile))
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setFixAspectRatio(true)
                    .start(this@MainActivity)
            }

            override fun onImagePickerError(e: Exception, source: ImageSource, type: Int) {
                super.onImagePickerError(e, source, type)
                Toast.makeText(this@MainActivity, "" + e.message, Toast.LENGTH_SHORT).show()
            }

        })

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            val result = CropImage.getActivityResult(data)

            if (resultCode == Activity.RESULT_OK) {

                val uri: Uri = result.uri

                Glide.with(applicationContext)
                    .load(File(uri.path))
                    .into(pickGambar)

                imgFile = File(uri.path)

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val exception = result.error
                Toast.makeText(this, "" + exception.toString(), Toast.LENGTH_SHORT).show()
            }

        }
    }



}
