package com.uroad.dubai.fragment

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.activity.*
import com.uroad.dubai.adapter.NearMeTabAdapter
import com.uroad.dubai.common.BaseFragment
import com.uroad.dubai.common.BaseRecyclerAdapter
import com.uroad.dubai.local.UserPreferenceHelper
import com.uroad.library.utils.DisplayUtils
import kotlinx.android.synthetic.main.content_mainnearby.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.home_content_scroll.*
import kotlinx.android.synthetic.main.home_top_collapse.*
import kotlinx.android.synthetic.main.home_top_expand.*
import kotlinx.android.synthetic.main.home_top_flexhead.*

/**
 * @author MFB
 * @create 2018/12/12
 * @describe 首页
 */
class MainFragment : BaseFragment() {

    private var statusHeight = 0
    private var isNeedRefresh = false

    companion object {
        private const val TAG_BANNER = "banner"
        private const val TAG_NOTICE = "notice"
        private const val TAG_FAVORITES = "favorites"
        private const val TAG_ROADS = "roads"
        private const val TAG_EVENTS = "events"
        private const val TAG_NEWS = "news"
        private const val TAG_HOTEL = "hotel"
        private const val TAG_RESTAURANTS = "restaurants"
        private const val TAG_ATTRACTIONS = "attractions"
    }

    override fun setUp(view: View, savedInstanceState: Bundle?) {
        setContentView(R.layout.fragment_main)
        statusHeight = DisplayUtils.getStatusHeight(context)
        initLayout()
        initMenu()
        initBanner()
        initNotice()
        initFavorites()
        initNearBy()
    }

    private fun initLayout() {
        ablBar.setPadding(0, statusHeight, 0, 0)
//        tlCollapse.layoutParams = (tlCollapse.layoutParams as Toolbar.LayoutParams).apply { topMargin = statusHeight }
        ablBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val offset = Math.abs(verticalOffset)
            val total = appBarLayout.totalScrollRange
            if (offset <= total / 2) {
                tlExpand.visibility = View.VISIBLE
                tlCollapse.visibility = View.GONE
            } else {
                tlExpand.visibility = View.GONE
                tlCollapse.visibility = View.VISIBLE
            }
        })
    }

    private fun initMenu() {
        ivMessage.setOnClickListener {
            if (check()) return@setOnClickListener
            openActivity(MessagesListActivity::class.java)
        }
        ivMessageColl.setOnClickListener {
            if (check()) return@setOnClickListener
            openActivity(MessagesListActivity::class.java)
        }
        ivSearch.setOnClickListener { showTipsDialog(getString(R.string.developing)) }
        ivSearchColl.setOnClickListener { showTipsDialog(getString(R.string.developing)) }
        tvNavigation.setOnClickListener { openActivity(RoadNavigationActivity::class.java) }
        ivNavigation.setOnClickListener { openActivity(RoadNavigationActivity::class.java) }
        tvHighWay.setOnClickListener { openActivity(RoadsListActivity::class.java) }
        ivHighWay.setOnClickListener { openActivity(RoadsListActivity::class.java) }
        tvNews.setOnClickListener { openActivity(NewsListActivity::class.java) }
        ivNews.setOnClickListener { openActivity(NewsListActivity::class.java) }
        tvMore.setOnClickListener { openActivity(MoreActivity::class.java) }
        ivMore.setOnClickListener { openActivity(MoreActivity::class.java) }
    }

    private fun initBanner() {
        childFragmentManager.beginTransaction().replace(R.id.flBanner, MainBannerFragment(), TAG_BANNER).commitAllowingStateLoss()
    }

    private fun initNotice() {
        childFragmentManager.beginTransaction().replace(R.id.flNotice, MainNoticeFragment().apply {
            setOnRequestCallback(object : MainNoticeFragment.OnRequestCallback {
                override fun callback(isEmpty: Boolean) {
                    if (isEmpty) this@MainFragment.flNotice.visibility = View.GONE
                    else this@MainFragment.flNotice.visibility = View.VISIBLE
                }
            })
        }, TAG_NOTICE).commitAllowingStateLoss()
    }

    private fun initFavorites() {
        childFragmentManager.beginTransaction().replace(R.id.flFavorites, MainFavoritesFragment().apply {
            setOnRequestCallback(object : MainFavoritesFragment.OnRequestCallback {
                override fun callback(isEmpty: Boolean) {
                    if (isEmpty) this@MainFragment.flFavorites.visibility = View.GONE
                    else this@MainFragment.flFavorites.visibility = View.VISIBLE
                }
            })
        }, TAG_FAVORITES).commitAllowingStateLoss()
    }

    private fun initNearBy() {
        rvNearByTab.isNestedScrollingEnabled = false
        val tabs = ArrayList<String>().apply {
            add(context.getString(R.string.nearMe_roads))
            add(context.getString(R.string.nearMe_events))
            add(context.getString(R.string.nearMe_news))
            add(context.getString(R.string.nearMe_hotel))
            add(context.getString(R.string.nearMe_restaurants))
            add(context.getString(R.string.nearMe_attractions))
        }
        rvNearByTab.adapter = NearMeTabAdapter(context, tabs).apply {
            setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(adapter: BaseRecyclerAdapter, holder: BaseRecyclerAdapter.RecyclerHolder, view: View, position: Int) {
                    if (position in 0 until tabs.size) {
                        setSelected(position)
                        rvNearByTab.smoothScrollToPosition(position)
                        setCurrentTab(position)
                    }
                }
            })
        }
        initFragments()
    }

    private fun initFragments() {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.flNearMeRoads, NearMeRoadsFragment(), TAG_ROADS)
        transaction.replace(R.id.flNearMeEvents, NearMeEventsFragment(), TAG_EVENTS)
        transaction.replace(R.id.flNearMeNews, NearMeNewsFragment(), TAG_NEWS)
        transaction.replace(R.id.flNearMeHotel, NearMeHotelFragment(), TAG_HOTEL)
        transaction.replace(R.id.flNearMeRestaurants, NearMeRestaurantsFragment(), TAG_RESTAURANTS)
        transaction.replace(R.id.flNearMeAttractions, NearMeAttractionsFragment(), TAG_ATTRACTIONS)
        transaction.commitAllowingStateLoss()
        setCurrentTab(0)
    }

    private fun setCurrentTab(tab: Int) {
        hideFragments()
        when (tab) {
            0 -> flNearMeRoads.visibility = View.VISIBLE
            1 -> flNearMeEvents.visibility = View.VISIBLE
            2 -> flNearMeNews.visibility = View.VISIBLE
            3 -> flNearMeHotel.visibility = View.VISIBLE
            4 -> flNearMeRestaurants.visibility = View.VISIBLE
            5 -> flNearMeAttractions.visibility = View.VISIBLE
        }
    }

    private fun hideFragments() {
        flNearMeRoads.visibility = View.GONE
        flNearMeEvents.visibility = View.GONE
        flNearMeNews.visibility = View.GONE
        flNearMeHotel.visibility = View.GONE
        flNearMeRestaurants.visibility = View.GONE
        flNearMeAttractions.visibility = View.GONE
    }

    private fun check(): Boolean {
        if (!UserPreferenceHelper.isLogin(context)) {
            openActivity(LoginActivity::class.java)
            return true
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        onRefresh()
    }

    private fun onRefresh() {
        if (!isNeedRefresh) return
        else {
            refreshFavorites()
            isNeedRefresh = false
        }
    }

    private fun refreshFavorites() {
        val fragment = childFragmentManager.findFragmentByTag(TAG_FAVORITES)
        if (fragment != null && fragment is MainFavoritesFragment) fragment.initData()
    }

    override fun onPause() {
        super.onPause()
        isNeedRefresh = true
    }
}