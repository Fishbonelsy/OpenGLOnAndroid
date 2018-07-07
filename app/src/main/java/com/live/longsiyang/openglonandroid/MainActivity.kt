package com.live.longsiyang.openglonandroid

import android.annotation.SuppressLint
import android.app.Fragment
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.live.longsiyang.openglonandroid.picture.fragment.PictureFragment


class MainActivity : AppCompatActivity() {


    lateinit var mFragmentManager: FragmentManager
    lateinit var mFragmentTransaction: FragmentTransaction
    lateinit var mFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mFragmentManager = getFragmentManager()
        mFragment = PictureFragment()

        mFragmentManager.beginTransaction().add(R.id.fl_main_container,mFragment).commit()

    }







}

