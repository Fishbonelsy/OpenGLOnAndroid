package com.live.longsiyang.openglonandroid

import android.annotation.SuppressLint
import android.app.Fragment
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.live.longsiyang.openglonandroid.camera.CameraGlSurfaceShowActivity
import com.live.longsiyang.openglonandroid.camera.PreviewFragment
import com.live.longsiyang.openglonandroid.picture.fragment.PictureFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    lateinit var mFragmentManager: FragmentManager
    lateinit var mFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFragmentManager = getFragmentManager()
        mFragment = PictureFragment()

        mFragmentManager.beginTransaction().add(R.id.fl_main_container,mFragment).commit()

        btn_test.setOnClickListener {
            val intent = Intent()
            //获取intent对象
            intent.setClass(this, CameraGlSurfaceShowActivity::class.java)
            // 获取class是使用::反射
            startActivity(intent)
        }

    }







}

