package com.example.recorder

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.recorder.databinding.ActivityMainBinding
import java.io.IOException


class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_RECORD_AUDIO_CODE = 200
    }
    private var recoder:MediaRecorder?=null
    private var fileName:String=""
    private lateinit var binding: ActivityMainBinding

    // 상태 관리 : 릴리즈 ->녹음중 -> 저장(릴리즈)
    //릴리즈 ->재생 -> 릴리즈
    private enum class State{
        RELEASE,RECODINGRECODING,PLAING
    }
    private var state:State = State.RELEASE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileName="${externalCacheDir?.absolutePath}/audiorecordtest.3gp" //$(externalCacheDir?.absolutePath}절대경로



        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.recordButton.setOnClickListener {
            when(state){
                State.RELEASE->{
                    record() //녹음 기능
                }
                State.RECODINGRECODING ->{
                    onRecord(false)
                }
                State.PLAING->{

                }


            }


        }

    }

    private fun showPermissionRationalDialog() {
        AlertDialog.Builder(this)
            .setMessage("녹음 권한을 켜야 앱을 정상적으로 사용할 수 있습니다.")
            .setPositiveButton("권한 허용하기") { _, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.RECORD_AUDIO),
                    REQUEST_RECORD_AUDIO_CODE
                )
            }.setNegativeButton("취소") { dialogInterface, _ -> dialogInterface.cancel() }.show()
    }

    private fun showpermissionDisallowDialog() {
        AlertDialog.Builder(this)
            .setMessage(
                "녹음 권한을 켜야 앱을 정상적으로 사용할 수 있습니다." +
                        "앱 설정 화면으로 진입하셔서 권한을 켜주세요"
            )
            .setPositiveButton("권한 변경하러 가기") { _, _ ->
                navigateToAppSetting()

            }.setNegativeButton("취소") { dialogInterface, _ -> dialogInterface.cancel() }.show()
    }

    private fun record(){
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                onRecord(true)

            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.RECORD_AUDIO
            ) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
                showPermissionRationalDialog()
            }

            else -> {
                // You can directly ask for the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.RECORD_AUDIO),
                    REQUEST_RECORD_AUDIO_CODE
                )
            }
        }
    }


    private fun navigateToAppSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {//다양한 곳으로 이동 가능
            data = Uri.fromParts("package", packageName, null) //디테일 세팅으로 가는데 우리 디테일 세팅페이지로 이동
        }
        startActivity(intent)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //다른 권한인지 확인하기 위해 적어야 됨
        val audioRecordPermission = requestCode == REQUEST_RECORD_AUDIO_CODE
                && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED

        if (audioRecordPermission) {
            //todo 녹음 작업을 시작함
            onRecord(true)
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.RECORD_AUDIO
                )
            ) {
                showPermissionRationalDialog()
            } else {
                showpermissionDisallowDialog()
            }
        }

    }


    private fun onRecord(start:Boolean) =if(start){
        startRecoding()
    }else {
        stopRecoding()
    }
    private fun startRecoding() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { 30버전 이상부터 사용가능해서 빨간줄
//            recoder= MediaRecorder(this) //버전 별로 활용해주는 방법
//        }
        state=State.RECODINGRECODING
        recoder= MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)//MediaRecorder의 마이크를 사용하겠다는 뜻
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try{
                prepare()
            }catch (e: IOException){
                Log.e("APP","prepare() falied $e")
            }

            start()
        }

        binding.recordButton.setImageDrawable(//클릭시 이미지 변환
            ContextCompat.getDrawable(
                this,
                R.drawable.baseline_stop_24
            )
        )
        binding.recordButton.imageTintList= ColorStateList.valueOf(Color.BLACK)
        //만약 리소스에서 내가 설정한 색 갖고오고 싶으면
//        ContextCompat.getColor(this,R.color.black)

        binding.playButton.isEnabled=false //재생버튼시 플레이버튼 비활성화
        binding.playButton.alpha=0.3f //흐림도 표시
    }

    private fun stopRecoding(){
        recoder?.apply {
            stop()
            release()
        }
        recoder=null
        state=State.RELEASE

        binding.recordButton.setImageDrawable(//클릭시 이미지 변환
            ContextCompat.getDrawable(
                this,
                R.drawable.baseline_fiber_manual_record_24
            )
        )
        binding.recordButton.imageTintList= ColorStateList.valueOf(Color.RED)
        binding.playButton.isEnabled=true //재생버튼시 플레이버튼 비활성화
        binding.playButton.alpha=1f
    }

}


