package com.live.longsiyang.openglonandroid.picture.effects.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.live.longsiyang.openglonandroid.R
import com.live.longsiyang.openglonandroid.picture.effects.data.LocalEffect
import kotlinx.android.synthetic.main.effect_list_item_layout.view.*

/**
 * Created by oceanlong on 2018/6/25.
 */
open class EffectListAdapter : RecyclerView.Adapter<EffectListAdapter.EffectViewHolder> {

    var context: Context
    var dataList: List<LocalEffect>
    var itemListener: OnItemClickListener? = null;

    open interface OnItemClickListener{
        fun onItemClick(v:View , i:Int ,effect: LocalEffect);
    }

    constructor(context:Context , dataList:List<LocalEffect>){

        this.context = context
        this.dataList = dataList

    }

    fun setOnItemClickListener(listener:OnItemClickListener){
        itemListener = listener
    }

    override fun onBindViewHolder(holder: EffectViewHolder?, position: Int) {
        holder?.bindData(dataList.get(position) , position , itemListener)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): EffectViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.effect_list_item_layout , parent , false)
        val viewholder = EffectViewHolder(view)
        return viewholder
    }




    class EffectViewHolder(itemView: View? ) : RecyclerView.ViewHolder(itemView) {


        fun bindData(effect:LocalEffect , position:Int,listener: OnItemClickListener?){

            itemView.btn_effect_item.setText(effect.name)
            itemView.btn_effect_item.setOnClickListener {

                listener?.onItemClick(itemView , position,effect)
            }
        }
    }
}