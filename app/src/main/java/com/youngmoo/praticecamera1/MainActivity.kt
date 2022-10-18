package com.youngmoo.praticecamera1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.youngmoo.praticecamera1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    //Manifest 에서 설정한 권한을 가지고옴
    val CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA)
    val STORAGE_PERMISSION = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    //권한 플래그값 정의
    val FLAG_PERM_CAMERA = 98
    val FLAG_PERM_STORAGE = 99

    //카메라와 갤러리를 호출하는 플래그
    val FLAG_REQ_CAMERA = 101
    val FLAG_REA_STORAGE = 102

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //권한이 승인되어 있으면 카메라를 호출하는 메소드 실행
        if(checkPermission(STORAGE_PERMISSION,FLAG_PERM_STORAGE)){
            setViews()
        }
    }

    private fun setViews() {
        //카메라 버튼 클릭
        binding.btnCamera.setOnClickListener {
            //카메라 호출 메소드
            openCamera()
        }
    }

    private fun openCamera() {
        //카메라 권한이 있는지 확인
        if(checkPermission(CAMERA_PERMISSION,FLAG_PERM_CAMERA)){
            //권한이 있으면 카메라를 실행시킵니다.
            val intent:Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,FLAG_REQ_CAMERA)
        }
    }

    //권한이 있는지 체크하는 메소드
    fun checkPermission(permissions:Array<out String>,flag:Int):Boolean{
        //안드로이드 버전이 마쉬멜로우 이상일때
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for(permission in permissions){
                //만약 권한이 승인되어 있지 않다면 권한승인 요청을 사용해 화면에 호출합니다.
                if(ContextCompat.checkSelfPermission(this,permission) !=PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,permissions,flag)
                    return false
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            FLAG_PERM_STORAGE ->{
                for(grant in grantResults){
                    if(grant != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this,"카메라 권한을 승인해야지만 카메라를 사용할 수 있습니다",Toast.LENGTH_SHORT).show()
                        return
                    }
                }
                openCamera()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                FLAG_REQ_CAMERA ->{
                    if(data?.extras?.get("data") != null){
                        //카메라로 방금 촬영한 이미지를 미리 만들어 놓은 이미지뷰로 전달 합니다.
                        val bitmap = data?.extras?.get("data") as Bitmap
                        binding.imgCamera.setImageBitmap(bitmap)
                    }
                }
            }
        }
    }
}