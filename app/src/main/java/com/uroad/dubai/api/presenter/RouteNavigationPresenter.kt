package com.uroad.dubai.api.presenter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v4.widget.PopupWindowCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.geojson.Point
import com.uroad.dubai.R
import com.uroad.dubai.adapter.CarmenFeatureAdapter
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.api.BaseObserver
import com.uroad.dubai.api.BasePresenter
import com.uroad.dubai.api.view.RouteNavigationView
import com.uroad.library.utils.DisplayUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class RouteNavigationPresenter(private val context: Context,
                               private val naviView: RouteNavigationView)
    : BasePresenter<RouteNavigationView>(naviView) {

    private var disposable: Disposable? = null

    fun getPoi(content: String, type: Int): MapboxGeocoding {
        return MapboxGeocoding.builder()
                .accessToken(context.getString(R.string.mapBoxToken))
//                .country("ae")
                .query(content).build().apply {
                    this.enqueueCall(object : Callback<GeocodingResponse> {
                        override fun onResponse(call: Call<GeocodingResponse>, response: Response<GeocodingResponse>) {
                            response.body()?.features()?.let { naviView.onPoiResult(it, type) }
                        }

                        override fun onFailure(call: Call<GeocodingResponse>, t: Throwable) {

                        }
                    })
                }
    }

    fun getRoutes(origin: Point, destination: Point, profile: String) {
        disposable?.dispose()
        val directions = buildDirections(origin, destination, profile)
        disposable = Observable.fromCallable { directions.executeCall() }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : BaseObserver<Response<DirectionsResponse>>(naviView) {
                    override fun onSuccess(result: Response<DirectionsResponse>) {
                        naviView.onNavigationRoutes(result.body()?.routes())
                    }
                })
        addDisposable(disposable)
    }

    private fun buildDirections(origin: Point, destination: Point, profile: String): MapboxDirections {
        return MapboxDirections.builder()
                .profile(profile)
                .origin(origin)
                .destination(destination)
                .annotations(DirectionsCriteria.ANNOTATION_CONGESTION, DirectionsCriteria.ANNOTATION_DISTANCE)
                .accessToken(context.getString(R.string.mapBoxToken))
                .steps(true)
                .continueStraight(true)
                .geometries(DirectionsCriteria.GEOMETRY_POLYLINE6)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .voiceInstructions(true)
                .bannerInstructions(true)
                .alternatives(true)
                .roundaboutExits(true)
                .build()
    }

    fun showPoiWindow(parent: View, features: MutableList<CarmenFeature>, onItemClickListener: BaseRecyclerAdapter.OnItemClickListener): PopupWindow {
        val recyclerView = RecyclerView(context).apply {
            setBackgroundColor(ContextCompat.getColor(this@RouteNavigationPresenter.context, R.color.white))
            layoutManager = LinearLayoutManager(this@RouteNavigationPresenter.context).apply { orientation = LinearLayoutManager.VERTICAL }
        }
        val popupWindow = PopupWindow(recyclerView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            isFocusable = false
            setBackgroundDrawable(ColorDrawable())
            isOutsideTouchable = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                val location = IntArray(2)
                parent.getLocationInWindow(location)
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) { // 7.1 版本处理
                    val screenHeight = DisplayUtils.getWindowHeight(context)
                    height = screenHeight - location[1] - parent.height
                }
                showAtLocation(parent, Gravity.NO_GRAVITY, location[0], location[1] + parent.height)
            } else
                PopupWindowCompat.showAsDropDown(this, parent, 0, 0, Gravity.NO_GRAVITY)
        }
        recyclerView.adapter = CarmenFeatureAdapter(context, features).apply {
            setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                    onItemClickListener.onItemClick(adapter, holder, view, position)
                }
            })
        }
        return popupWindow
    }
}