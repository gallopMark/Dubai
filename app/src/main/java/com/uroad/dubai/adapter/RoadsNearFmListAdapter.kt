package com.uroad.dubai.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseArrayRecyclerAdapter
import com.uroad.dubai.model.RoadsMDL
import com.uroad.library.utils.DisplayUtils

class RoadsNearFmListAdapter(private val context: Context, data: MutableList<RoadsMDL>)
      : BaseArrayRecyclerAdapter<RoadsMDL>(context,data) {

    override fun bindView(viewType: Int): Int = R.layout.item_roads_near

    override fun onBindHoder(holder: RecyclerHolder, t: RoadsMDL, position: Int) {
        holder.displayImage(R.id.ivRoadsType,t.icon)
        holder.setText(R.id.tvRoadsTitle,t.roadname)

        val recyclerView = holder.obtainView<RecyclerView>(R.id.recyclerView)
        if(t.getRoadColors().size > 0){
            recyclerView.visibility = View.VISIBLE
            recyclerView.adapter = RoadColorAdapter(context, t.getRoadColors())
        } else {
            recyclerView.visibility = View.GONE
        }
    }

    private inner class RoadColorAdapter(context: Context, colors: MutableList<Int>)
        : BaseArrayRecyclerAdapter<Int>(context, colors) {
        private val totalWidth = DisplayUtils.getWindowWidth(context) - DisplayUtils.dip2px(context, 40f)
        private val measureSize = totalWidth / colors.size + totalWidth % colors.size

        override fun bindView(viewType: Int): Int = R.layout.item_nearmeroads_color

        override fun onBindHoder(holder: RecyclerHolder, t: Int, position: Int) {
            holder.itemView.layoutParams = (holder.itemView.layoutParams as RecyclerView.LayoutParams).apply { width = measureSize }
            holder.setBackgroundColor(R.id.vColor, t)
        }
    }

}