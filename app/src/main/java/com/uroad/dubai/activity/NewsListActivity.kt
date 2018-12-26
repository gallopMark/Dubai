package com.uroad.dubai.activity

import com.uroad.dubai.R
import com.uroad.dubai.adapter.NewsListAdapter
import com.uroad.dubai.common.BaseRefreshPresenterActivity
import com.uroad.dubai.enumeration.NewsType
import com.uroad.dubai.model.NewsMDL
import com.uroad.dubai.api.presenter.NewsPresenter
import com.uroad.dubai.api.view.NewsView
import com.uroad.dubai.webService.WebApi
import kotlinx.android.synthetic.main.activity_base_refresh.*

/**
 * @author MFB
 * @create 2018/12/21
 * @describe News list activity
 */
class NewsListActivity : BaseRefreshPresenterActivity<NewsPresenter>(), NewsView {

    private var index = 1
    private var size = 10
    private lateinit var data: MutableList<NewsMDL>
    private lateinit var adapter: NewsListAdapter

    override fun initViewData() {
        withTitle(getString(R.string.home_menu_news))
        data = ArrayList()
        adapter = NewsListAdapter(this, data)
        recyclerView.adapter = adapter
        baseRefreshLayout.autoRefresh()
    }

    override fun createPresenter(): NewsPresenter = NewsPresenter(this)

    override fun onPresenterCreate() {
        getNewsList()
    }

    private fun getNewsList() {
        presenter?.getNewsList(WebApi.GET_NEWS_LIST, WebApi.getNewsListParams(NewsType.NEWS.code, "", index, size))
    }

    override fun onPullToRefresh() {
        index = 1
        getNewsList()
    }

    override fun onPullToLoadMore() {
        getNewsList()
    }

    override fun onGetNewList(news: MutableList<NewsMDL>) {
        onPullToLoadSuccess()
        if (index == 1) {
            data.clear()
        }
        data.addAll(news)
        adapter.notifyDataSetChanged()
        if (news.size < size) {
           onFinishLoadMoreWithNoMoreData()
        }
        if (index == 1 && data.size == 0) {
            onPageNoData()
        } else {
            index++
        }
    }

    override fun onHttpResultError(errorMsg: String?, errorCode: Int?) {
        showShortToast(errorMsg)
    }
}