package com.uroad.dubai.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.uroad.dubai.R
import com.uroad.dubai.widget.CurrencyLoadView
import com.uroad.library.compat.AppDialog
import com.uroad.library.utils.NetworkUtils

/**
 * @author MFB
 * @create 2018/12/11
 * @describe fragment基础类(viewpager切换不适宜运用 onDestroyView()方法后view会报空指针异常)
 */
abstract class BaseLucaFragment : Fragment() {

    private var rootView: View? = null
    open lateinit var context: Activity
    open lateinit var fgBaseParent: FrameLayout
    open lateinit var fgBaseLoadView: CurrencyLoadView
    open var contentView: View? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as Activity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_base, container, false).apply {
                fgBaseParent = findViewById(R.id.fgBaseParent)
                fgBaseLoadView = findViewById(R.id.fgBaseLoadView)
            }
        }
        rootView?.parent?.let { (it as ViewGroup).removeView(rootView) }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp(view, savedInstanceState)
    }

    open fun setUp(view: View, savedInstanceState: Bundle?) {

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
    }

    open fun initData() {

    }

    open fun setContentView(@LayoutRes layoutResID: Int) {
        contentView = LayoutInflater.from(context).inflate(layoutResID, fgBaseParent, false)
        fgBaseParent.addView(contentView)
    }

    open fun onPageLoading() {
        contentView?.visibility = View.GONE
        fgBaseLoadView.setState(CurrencyLoadView.STATE_LOADING)
    }

    open fun onPageResponse() {
        contentView?.visibility = View.VISIBLE
        fgBaseLoadView.setState(CurrencyLoadView.STATE_IDEA)
    }

    open fun onPageError() {
        contentView?.visibility = View.GONE
        if (!NetworkUtils.isConnected(this@BaseLucaFragment.context))
            fgBaseLoadView.setState(CurrencyLoadView.STATE_NO_NETWORK)
        else
            fgBaseLoadView.setState(CurrencyLoadView.STATE_ERROR)
        fgBaseLoadView.setOnRetryListener(object : CurrencyLoadView.OnRetryListener {
            override fun onRetry(view: View) {
                onPageRetry(view)
            }
        })
    }

    open fun onPageNoData() {
        onPageNoData(-1, null)
    }

    open fun onPageNoData(emptyTips: CharSequence?) {
        onPageNoData(-1, emptyTips)
    }

    open fun onPageNoData(emptyIcon: Int, emptyTips: CharSequence?) {
        contentView?.visibility = View.GONE
        fgBaseLoadView.setState(CurrencyLoadView.STATE_EMPTY)
        if (!TextUtils.isEmpty(emptyTips)) fgBaseLoadView.setEmptyText(emptyTips)
        if (emptyIcon != -1) fgBaseLoadView.setEmptyIco(emptyIcon)
    }

    open fun onPageRetry(view: View) {
        initData()
    }

    // 封装跳转
    fun openActivity(c: Class<*>) {
        openActivity(c, null)
    }

    // 跳转 传递数据 bundel
    fun openActivity(c: Class<*>, bundle: Bundle?) {
        openActivity(c, bundle, null)
    }

    fun openActivity(c: Class<*>, bundle: Bundle?, uri: Uri?) {
        if (activity == null) return
        val intent = Intent(context, c)
        bundle?.let { intent.putExtras(it) }
        uri?.let { intent.data = it }
        startActivity(intent)
    }

    fun openActivity(intent: Intent) {
        openActivity(intent, null)
    }

    open fun openActivity(intent: Intent, bundle: Bundle?) {
        openActivity(intent, bundle, null)
    }

    open fun openActivity(intent: Intent, bundle: Bundle?, uri: Uri?) {
        if (activity == null) return
        bundle?.let { intent.putExtras(it) }
        uri?.let { intent.data = uri }
        startActivity(intent)
    }

    fun openActivityForResult(c: Class<*>, requestCode: Int) {
        openActivityForResult(c, null, requestCode)
    }

    open fun openActivityForResult(c: Class<*>, bundle: Bundle?, requestCode: Int) {
        openActivityForResult(c, bundle, null, requestCode)
    }

    open fun openActivityForResult(c: Class<*>, bundle: Bundle?, uri: Uri?, requestCode: Int) {
        if (activity == null) return
        val intent = Intent(context, c)
        bundle?.let { intent.putExtras(it) }
        uri?.let { intent.data = it }
        startActivityForResult(intent, requestCode)
    }

    open fun openActivityForResult(intent: Intent, requestCode: Int) {
        openActivityForResult(intent, null, requestCode)
    }

    open fun openActivityForResult(intent: Intent, bundle: Bundle?, requestCode: Int) {
        openActivityForResult(intent, bundle, null, requestCode)
    }

    open fun openActivityForResult(intent: Intent, bundle: Bundle?, uri: Uri?, requestCode: Int) {
        if (activity == null) return
        bundle?.let { intent.putExtras(it) }
        uri?.let { intent.data = it }
        startActivityForResult(intent, requestCode)
    }

    open fun showTipsDialog(message: CharSequence?) {
        showTipsDialog(message, "", null)
    }

    open fun showTipsDialog(message: CharSequence?, textPositive: CharSequence?, listener: AppDialog.OnClickListener?) {
        val dialog = AppDialog(context)
        dialog.setTitle(getString(R.string.tips))
        dialog.setMessage(message)
        dialog.hideDivider()
        val text = if (TextUtils.isEmpty(textPositive)) getString(R.string.dialog_button_confirm) else textPositive
        dialog.setPositiveButton(text, object : AppDialog.OnClickListener {
            override fun onClick(v: View, dialog: AppDialog) {
                if (listener == null) dialog.dismiss()
                else listener.onClick(v, dialog)
            }
        })
        dialog.show()
    }

    open fun showDialog(title: CharSequence?, message: CharSequence?, listener: BaseLucaActivity.DialogViewClickListener?) {
        showDialog(title, message, getString(R.string.dialog_button_cancel), getString(R.string.dialog_button_confirm), listener)
    }

    open fun showDialog(title: CharSequence?, message: CharSequence?, cancelCs: CharSequence?, confirmCs: CharSequence?, listener: BaseLucaActivity.DialogViewClickListener?) {
        val dialog = AppDialog(context)
        dialog.setTitle(title)
        dialog.setMessage(message)
        dialog.setNegativeButton(cancelCs, object : AppDialog.OnClickListener {
            override fun onClick(v: View, dialog: AppDialog) {
                listener?.onCancel(v, dialog)
            }
        })
        dialog.setPositiveButton(confirmCs, object : AppDialog.OnClickListener {
            override fun onClick(v: View, dialog: AppDialog) {
                listener?.onConfirm(v, dialog)
            }
        })
        dialog.show()
    }

}