package com.uroad.dubai.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.uroad.dubai.R
import com.uroad.dubai.api.presenter.LoginPresenter
import com.uroad.dubai.api.view.LoginView
import com.uroad.dubai.common.BaseActivity
import com.uroad.dubai.widget.NumberEditText
import com.uroad.dubai.local.UserPreferenceHelper
import com.uroad.dubai.model.UserMDL
import com.uroad.dubai.utils.PackageInfoUtils
import com.uroad.dubai.webService.WebApi
import com.uroad.library.utils.DeviceUtils
import io.reactivex.Observable
import io.reactivex.Observer
import kotlinx.android.synthetic.main.activity_verify_code.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit


class VerificationCodeActivity : BaseActivity() ,LoginView, NumberEditText.OnInputFinishListener {

    var phone : String  = ""
    var d : Disposable? = null
    var code : String? = ""
    var verificationcode : String = ""
    var hasContent : Boolean = false
    var millis : Int = 60
    var isCreateNewAccount : Boolean = false
    private lateinit var presenter : LoginPresenter

    @SuppressLint("LogNotTimber")
    override fun setUp(savedInstanceState: Bundle?) {
        setBaseContentView(R.layout.activity_verify_code,true)

        init()
    }

    override fun onInputFinish(text: String) {
        if (TextUtils.equals(text,verificationcode) && isCreateNewAccount){
            presenter.login(WebApi.USER_LOGIN,
                    WebApi.login("1", this.phone,verificationcode,
                            PackageInfoUtils.getVersionName(this@VerificationCodeActivity),
                            DeviceUtils.getAndroidID(this@VerificationCodeActivity),
                            DeviceUtils.getManufacturer(),
                            DeviceUtils.getModel(),
                            "${DeviceUtils.getSDKVersion()}"))
        } else if(!TextUtils.equals(text,verificationcode) && isCreateNewAccount){
            showShortToast("Verification code error")
        } else{
            hasContent = TextUtils.equals(text,verificationcode)
        }

        if (btnVerify.visibility == View.VISIBLE && !btnVerify.isEnabled){
            btnVerify.isEnabled = true
            btnVerify.background = drawable(R.drawable.selector_verify_btn_bg)
        }
    }

    private fun init() {
        presenter = LoginPresenter(this@VerificationCodeActivity)

        intent.extras?.let {
            isCreateNewAccount = it.getBoolean("isCreate", false)
            phone = it.getString("phone")
            tvSendPhone.text = "+971 $phone"
        }

        edVerify.setOnInputFinish(this@VerificationCodeActivity)
        if (isCreateNewAccount) {
            btnVerify.visibility = View.GONE
            presenter.sendVerificationCode(WebApi.SEND_VERIFICATION_CODE,WebApi.sendVerificationCode(phone?:""))
        }

        btnVerify.setOnClickListener {
            if (hasContent && edVerify.condition()){
                openActivity(CreatePasswordActivity::class.java)
                finish()
            }
        }
        btnVerify.isEnabled = false
        btnVerify.background = drawable(R.drawable.shape_btn_bg_not_enabled)
        interval(1000)
    }

    private fun interval(milliseconds : Long){
        Observable.interval(milliseconds, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Long> {
                    override fun onComplete() {}
                    override fun onSubscribe(d: Disposable) {
                        this@VerificationCodeActivity.d = d
                    }
                    override fun onError(e: Throwable) {}

                    override fun onNext(t: Long) {
                        if (millis<0){
                            presenter.sendVerificationCode(WebApi.SEND_VERIFICATION_CODE,WebApi.sendVerificationCode(phone?:""))
                            millis = 60
                        }
                        tvRemainingTime.text = "${millis}s"
                        millis--
                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        d?.dispose()
    }

    override fun loginSuccess(user: UserMDL?) {
        user?.let {
            UserPreferenceHelper.save(this@VerificationCodeActivity,it)
        }
        UserPreferenceHelper.saveAccount(this@VerificationCodeActivity, this.phone)
        UserPreferenceHelper.login(this@VerificationCodeActivity)
        openActivity(MainActivity::class.java)
        finish()
    }

    override fun getVerificationCode(code: String) {
        this@VerificationCodeActivity.verificationcode =  code
    }


    override fun loginError(e: String) {
        showLongToast(e)
    }

    override fun onShowLoading() {
    }

    override fun onHideLoading() {
    }

    override fun onShowError(msg: String?) {
        showLongToast(msg)
    }
}