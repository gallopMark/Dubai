package com.uroad.dubai.activity

import android.os.Bundle
import com.uroad.dubai.R
import com.uroad.dubai.common.BaseActivity
import com.uroad.dubai.enumeration.NewsType
import kotlinx.android.synthetic.main.activity_more.*

/**
 * @author MFB
 * @create 2018/12/29
 * @describe more function activity
 */
class MoreActivity : BaseActivity() {
    override fun setUp(savedInstanceState: Bundle?) {
        withTitle(getString(R.string.home_menu_more))
        setBaseContentView(R.layout.activity_more)
        initView()
    }

    private fun initView() {
        val bundle = Bundle()
        tvHotels.setOnClickListener {
            bundle.putString("userstatus", NewsType.HOTEL.code)
            openActivity(AttractionsListActivity::class.java, bundle)
        }
        tvRestaurants.setOnClickListener {
            bundle.putString("userstatus", NewsType.RESTAURANT.code)
            openActivity(AttractionsListActivity::class.java, bundle)
        }
        tvAttractions.setOnClickListener {
            bundle.putString("userstatus", NewsType.ATTRACTION.code)
            openActivity(AttractionsListActivity::class.java, bundle)
        }

        tvEvents.setOnClickListener { openActivity(EventsListActivity::class.java) }
        tvTransport.setOnClickListener { openActivity(BusStopListActivity::class.java) }
        tvParking.setOnClickListener { openActivity(ParkingListActivity::class.java) }
        tvPolice.setOnClickListener { openActivity(PoliceListActivity::class.java) }
        tvGroups.setOnClickListener { openActivity(GroupsSetupActivity::class.java) }
        tvWeather.setOnClickListener { openActivity(WeatherActivity::class.java) }
        tvReport.setOnClickListener { openActivity(ReportActivity::class.java) }
    }
}