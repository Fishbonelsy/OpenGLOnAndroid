package com.live.longsiyang.openglonandroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.live.longsiyang.openglonandroid.camera.PreviewFragment
import com.live.longsiyang.openglonandroid.picture.fragment.PictureFragment
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.AdapterView
import android.widget.Toast
import android.R.array
import android.view.View
import android.widget.AdapterView.OnItemSelectedListener
import com.live.longsiyang.openglonandroid.utils.LogUtils


class MainActivity : AppCompatActivity() {


    lateinit var mFragments : Map<String,BaseFragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFragments()
        initSpinner()
        val fragment = PictureFragment()
        fragmentManager.beginTransaction().add(R.id.fl_main_container,fragment).commit()

    }

    fun initFragments(){
        mFragments = HashMap()
        addFragment(PictureFragment())
        addFragment(PreviewFragment())
    }

    fun addFragment(fragment: BaseFragment){
        (mFragments as HashMap<String, BaseFragment>)[fragment.getFragmentName()] = fragment
    }

    fun initSpinner(){
        val arr = mFragments.keys.toTypedArray()
        //创建ArrayAdapter对象
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arr)
        sp_fragment_menu.adapter = adapter
        sp_fragment_menu.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val fragmentName  = arr[position]
                LogUtils.d("select fragment : " + fragmentName)
                replaceFragment(mFragments[fragmentName])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    fun replaceFragment(fragment: BaseFragment?){
        fragmentManager.beginTransaction().replace(R.id.fl_main_container , fragment).commit()
    }

}

