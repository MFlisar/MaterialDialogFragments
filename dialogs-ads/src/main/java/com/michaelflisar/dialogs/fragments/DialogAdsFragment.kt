package com.michaelflisar.dialogs.fragments

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.michaelflisar.dialogs.*
import com.michaelflisar.dialogs.ads.R
import com.michaelflisar.dialogs.ads.databinding.DialogAdsBinding
import com.michaelflisar.dialogs.base.MaterialDialogFragment
import com.michaelflisar.dialogs.enums.MaterialDialogButton
import com.michaelflisar.dialogs.setups.DialogAds

class DialogAdsFragment : MaterialDialogFragment<DialogAds>() {

    companion object {

        fun create(setup: DialogAds): DialogAdsFragment {
            val dlg = DialogAdsFragment()
            dlg.setSetupArgs(setup)
            return dlg
        }
    }

    private lateinit var binding: DialogAdsBinding

    private var handlerTimer: Handler? = null
    private var timeLeft: Int = 0
    private var posButton: AppCompatButton? = null
    private var neutrButton: AppCompatButton? = null
    private var negButton: AppCompatButton? = null

    private var interstitialAd: InterstitialAd? = null
    private var rewardedVideoAd: RewardedVideoAd? = null

    private var errorBanner: Exception? = null
    private var errorBigAd: Exception? = null

    private var bannerLoaded: Boolean = false
    private var bigAdLoaded: Boolean = false

    override fun onHandleCreateDialog(savedInstanceState: Bundle?): Dialog {

        MobileAds.initialize(requireActivity(), setup.appId.get(requireActivity()))

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("timeLeft")) {
                timeLeft = savedInstanceState.getInt("timeLeft")
            }
        } else {
            if (setup.timeToShowDialogAfterError > 0) {
                timeLeft = setup.timeToShowDialogAfterError
            }
        }

        // create dialog with correct style, title and cancelable flags
        val dialog = setup.createMaterialDialog(requireActivity(), this)

        dialog
            .customView(
                R.layout.dialog_ads,
                horizontalPadding = true
            )
            .positiveButton(setup) {
                finishDialog(
                    WhichButton.POSITIVE,
                    DialogAds.Event.Data.ClosedByUser(errorBanner, errorBigAd)
                )
            }
            .negativeButton(setup) {
                finishDialog(
                    WhichButton.NEGATIVE,
                    DialogAds.Event.Data.ClosedByUser(errorBanner, errorBigAd)
                )
            }
            .neutralButton(setup) {
                finishDialog(
                    WhichButton.NEUTRAL,
                    DialogAds.Event.Data.ClosedByUser(errorBanner, errorBigAd)
                )
            }

        posButton = dialog.getActionButton(WhichButton.POSITIVE)
        negButton =
            if (setup.negButton != null) dialog.getActionButton(WhichButton.NEGATIVE) else null
        neutrButton =
            if (setup.neutrButton != null) dialog.getActionButton(WhichButton.NEUTRAL) else null

        if (timeLeft > 0) {
            posButton!!.isEnabled = false
            negButton?.isEnabled = false
            neutrButton?.isEnabled = false
        }

        binding = DialogAdsBinding.bind(dialog.getCustomView())
        onBindingReady()
        return dialog
    }

    override fun onResume() {
        rewardedVideoAd?.resume(requireActivity())
        super.onResume()
    }

    override fun onPause() {
        rewardedVideoAd?.pause(requireActivity())
        super.onPause()
    }

    override fun onDestroy() {
        rewardedVideoAd?.destroy(requireActivity())
        super.onDestroy()
    }

    private fun onBindingReady() {

        binding.tvInfo.text = setup.info.get(requireActivity())
        binding.btShow.isEnabled = false

        // set up AdView
        if (setup.bannerSetup != null) {
            val adId = setup.getAdId(requireActivity(), DialogAds.AdType.Banner)
            val adView = AdView(requireActivity()).apply {
                adSize = AdSize.BANNER
                adUnitId = adId
            }
            replaceAdView(adView)
            adView.adListener = createAdListener(DialogAds.AdType.Banner)
            adView.loadAd(createAdRequest())
        } else {
            binding.adView.visibility = View.GONE
        }

        // set up interstitial/reward button
        if (setup.bigAdSetup != null) {
            binding.btShow.text = setup.bigAdSetup!!.showAdButtonText.get(requireActivity())
            binding.btShow.setOnClickListener {
                showBigAd()
            }
            when (setup.bigAdSetup!!.type) {
                DialogAds.BigAdType.Interstitial -> {
                    val adId = setup.getAdId(requireActivity(), DialogAds.AdType.Interstitial)
                    interstitialAd = InterstitialAd(requireActivity())
                    interstitialAd?.adUnitId = adId
                    interstitialAd?.adListener = createAdListener(DialogAds.AdType.Interstitial)
                    interstitialAd?.loadAd(createAdRequest())
                }
                DialogAds.BigAdType.Reward -> {
                    val adId = setup.getAdId(requireActivity(), DialogAds.AdType.Reward)
                    rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(requireActivity())
                    rewardedVideoAd?.setRewardedVideoAdListener(
                        createRewardedVideoAdListener(
                            DialogAds.AdType.Reward
                        )
                    )
                    rewardedVideoAd?.loadAd(adId, createAdRequest())
                }
            }
        } else {
            binding.btShow.visibility = View.GONE
        }
    }

    private fun createAdRequest(): AdRequest {
        val adRequest = AdRequest.Builder()
        if (setup.testSetup != null) {
            if (setup.testSetup!!.addDeviceIdAsTestDeviceId) {
                Utils.getDeviceId(requireActivity())?.let {
                    adRequest.addTestDevice(it)
                }
            }
            setup.testSetup?.testDeviceIds?.forEach {
                adRequest.addTestDevice(it.get(requireActivity()))
            }
        }
        return adRequest.build()
    }

    private fun createAdListener(type: DialogAds.AdType): AdListener {
        return object : AdListener() {
            override fun onAdClosed() {
                if (type == DialogAds.AdType.Interstitial) {
                    finishDialog(null, DialogAds.Event.Data.InterstitialShown)
                }
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                onAdLoadingError(type, errorCode)
            }

            override fun onAdLeftApplication() {}

            override fun onAdOpened() {}

            override fun onAdLoaded() {
                onAdLoaded(type)
            }

            override fun onAdClicked() {}

            override fun onAdImpression() {}
        }
    }

    private fun createRewardedVideoAdListener(type: DialogAds.AdType): RewardedVideoAdListener {
        return object : RewardedVideoAdListener {
            override fun onRewardedVideoAdClosed() {
                finishDialog(null, DialogAds.Event.Data.ClosedByUser(errorBanner, errorBigAd))
            }

            override fun onRewardedVideoAdLeftApplication() {
            }

            override fun onRewardedVideoAdLoaded() {
                onAdLoaded(type)
            }

            override fun onRewardedVideoAdOpened() {
            }

            override fun onRewardedVideoCompleted() {
            }

            override fun onRewarded(reward: RewardItem?) {
                reward?.amount?.let {
                    finishDialog(null, DialogAds.Event.Data.RewardReceived(it))
                }
            }

            override fun onRewardedVideoStarted() {
            }

            override fun onRewardedVideoAdFailedToLoad(errorCode: Int) {
                onAdLoadingError(type, errorCode)
            }
        }
    }

    private fun onAdLoaded(type: DialogAds.AdType) {
        when (type) {
            DialogAds.AdType.Banner -> bannerLoaded = true
            DialogAds.AdType.Interstitial,
            DialogAds.AdType.Reward -> bigAdLoaded = true
        }
        onAdLoadingStatesChanged(type)
    }

    private fun onAdLoadingError(type: DialogAds.AdType, errorCode: Int) {

        val errorInfo = when (errorCode) {
            AdRequest.ERROR_CODE_INTERNAL_ERROR -> "ERROR_CODE_INTERNAL_ERROR"
            AdRequest.ERROR_CODE_INVALID_REQUEST -> "ERROR_CODE_INVALID_REQUEST"
            AdRequest.ERROR_CODE_NETWORK_ERROR -> "ERROR_CODE_NETWORK_ERROR"
            AdRequest.ERROR_CODE_NO_FILL -> "ERROR_CODE_NO_FILL"
            else -> null
        }

        val error = errorInfo?.let { "$errorCode ($it)" } ?: errorCode.toString()

        val e = Exception("Loading ad of type $type failed with error = $error!")
        DialogSetup.logger?.error(e)
        when (type) {
            DialogAds.AdType.Banner -> errorBanner = e
            DialogAds.AdType.Interstitial,
            DialogAds.AdType.Reward -> errorBigAd = e
        }
        onAdLoadingStatesChanged(type)
    }

    private fun showBigAd() {
        if (setup.bigAdSetup != null) {
            when (setup.bigAdSetup!!.type) {
                DialogAds.BigAdType.Interstitial -> {
                    if (interstitialAd?.isLoaded == true) {
                        DialogSetup.logger?.debug("Showing interstitial ad")
                        interstitialAd?.show()
                    } else {
                        DialogSetup.logger?.debug("Interstitial ad not ready - deferring show")
                    }
                }
                DialogAds.BigAdType.Reward -> {
                    if (rewardedVideoAd?.isLoaded == true) {
                        DialogSetup.logger?.debug("Showing reward ad")
                        rewardedVideoAd?.show()
                    } else {
                        DialogSetup.logger?.debug("Reward ad not ready - deferring show")
                    }
                }
            }
        }
    }

    private fun replaceAdView(adView: View) {
        val parent = binding.adView.getParent() as ViewGroup
        val index = parent.indexOfChild(binding.adView)
        parent.removeView(binding.adView)
        parent.addView(adView, index)
    }

    private fun finishDialog(button: WhichButton?, data: DialogAds.Event.Data) {
        sendEvent(DialogAds.Event(setup, button?.let { MaterialDialogButton.find(it.ordinal) }, data))
        dismiss()
    }

    private fun onAdLoadingStatesChanged(type: DialogAds.AdType) {
        if (setup.bigAdSetup != null && type.isBigAd) {
            if (errorBigAd != null) {
                startTimer(
                    R.string.mdf_dialogs_info_error_ad_timeout,
                    R.string.mdf_dialogs_info_error_ad_timeout_over
                )
            } else {
                binding.tvInfoPreparing.setText(R.string.mdf_dialogs_info_ad_ready)
                binding.btShow.isEnabled = true
                posButton?.isEnabled = true
                negButton?.isEnabled = true
                neutrButton?.isEnabled = true
            }
        } else if (setup.bigAdSetup == null && type == DialogAds.AdType.Banner) {
            if (errorBanner != null) {
                startTimer(
                    R.string.mdf_dialogs_info_error_ad_timeout,
                    R.string.mdf_dialogs_info_error_ad_timeout_over
                )
            } else {
                startTimer(
                    R.string.mdf_dialogs_info_ad_ready_banner_timeout,
                    R.string.mdf_dialogs_info_ad_ready_banner_timeout_over
                )
            }
        }
    }

    private fun startTimer(timeoutRes: Int, timeoutOverRes: Int) {
        if (timeLeft > 0 && handlerTimer == null) {
            handlerTimer = Handler()

            val runnable = object : Runnable {
                override fun run() {
                    timeLeft = timeLeft.dec()
                    if (timeLeft > 0) {
                        binding.tvInfoPreparing.text = getString(timeoutRes, timeLeft)
                        handlerTimer!!.postDelayed(this, 1000)
                    } else {
                        binding.tvInfoPreparing.text = getString(timeoutOverRes)
                        posButton?.isEnabled = true
                        negButton?.isEnabled = true
                        neutrButton?.isEnabled = true
                    }
                }
            }
            handlerTimer!!.postDelayed(runnable, 1000)
        }
    }

    override fun onDestroyView() {
        handlerTimer?.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (timeLeft > 0) {
            outState.putInt("timeLeft", timeLeft)
        }
    }
}