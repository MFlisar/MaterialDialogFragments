package com.michaelflisar.dialogs.setups

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import com.michaelflisar.dialogs.Constants
import com.michaelflisar.dialogs.DialogSetup
import com.michaelflisar.dialogs.Utils
import com.michaelflisar.dialogs.ads.BuildConfig
import com.michaelflisar.dialogs.ads.R
import com.michaelflisar.dialogs.classes.BaseDialogSetup
import com.michaelflisar.dialogs.classes.Text
import com.michaelflisar.dialogs.fragments.DialogAdsFragment
import com.michaelflisar.dialogs.interfaces.DialogFragment
import kotlinx.android.parcel.Parcelize
import java.util.*
import java.util.concurrent.TimeUnit

@Parcelize
class DialogAds(
    // base setup
    override val id: Int,
    override val title: Text? = null,
    override val posButton: Text = Text.TextRes(R.string.mdf_close_dialog),
    override val negButton: Text? = null,
    override val neutrButton: Text? = null,
    override val cancelable: Boolean = false,
    override val extra: Bundle? = null,
    override val sendCancelEvent: Boolean = DialogSetup.SEND_CANCEL_EVENT_BY_DEFAULT,

    // special setup
    val info: Text,
    val appId: Text,
    val bannerSetup: BannerSetup? = null,
    val bigAdSetup: BigAdSetup? = null,
    val testSetup: TestSetup? = null,
    val timeToShowDialogAfterError: Int = 10
) : BaseDialogSetup {

    override fun create(): DialogFragment<DialogAds> = DialogAdsFragment.create(this)

    internal fun getAdId(context: Context, type: AdType): String {
        return when (type) {
            AdType.Banner -> {
                val testId = if (BuildConfig.DEBUG && testSetup?.useGooglesTestIds == true) {
                    Constants.TEST_ID_BANNER
                } else null
                testId ?: bannerSetup!!.adIdBanner.get(context)
            }
            AdType.Interstitial -> {
                val testId: String? = if (BuildConfig.DEBUG && testSetup?.useGooglesTestIds == true) {
                    Constants.TEST_ID_INTERSTITIAL
                } else null
                testId ?: bigAdSetup!!.adId.get(context)
            }
            AdType.Reward -> {
                val testId: String? = if (BuildConfig.DEBUG && testSetup?.useGooglesTestIds == true) {
                    Constants.TEST_ID_REWARDED_VIDEO
                } else null
                testId ?: bigAdSetup!!.adId.get(context)
            }
        }
    }

    internal enum class AdType(val isBigAd: Boolean) {
        Banner(false),
        Interstitial(true),
        Reward(true)
    }

    enum class BigAdType(internal val adType: AdType) {
        Interstitial(AdType.Interstitial),
        Reward(AdType.Reward)
    }

    @Parcelize
    class BannerSetup(val adIdBanner: Text) : Parcelable

    @Parcelize
    class BigAdSetup(
        val adId: Text,
        val showAdButtonText: Text,
        val type: BigAdType
    ) : Parcelable

    @Parcelize
    class TestSetup(
        val addDeviceIdAsTestDeviceId: Boolean = true,
        val useGooglesTestIds: Boolean = true,
        val testDeviceIds: List<Text> = emptyList()

    ) : Parcelable {

        companion object {
            fun getDefaultByBuildType(debugBuild: Boolean): TestSetup? = if (debugBuild) TestSetup() else null
        }
    }

    sealed class ShowPolicy : Parcelable {

        abstract fun shouldShow(context: Context, preference: String? = null): Boolean

        @Parcelize
        object Always : ShowPolicy() {
            override fun shouldShow(context: Context, preference: String?): Boolean = true
        }

        @Parcelize
        object OnceDaily : ShowPolicy() {
            override fun shouldShow(context: Context, preference: String?): Boolean {
                val now = Calendar.getInstance()
                val last = Utils.getLastShowPolicyDate(context, preference)
                if (last == null) {
                    Utils.saveLastShowPolicyDate(context, now, preference)
                    return true
                } else {
                    if (last.get(Calendar.YEAR) < now.get(Calendar.YEAR) ||
                        last.get(Calendar.MONTH) < now.get(Calendar.MONTH) ||
                        last.get(Calendar.DAY_OF_MONTH) < now.get(Calendar.DAY_OF_MONTH)
                    ) {
                        Utils.saveLastShowPolicyDate(context, now, preference)
                        DialogSetup.logger?.debug("Showing ad dialog because of policy ${this::class.java.simpleName}!")
                        return true
                    } else {
                        DialogSetup.logger?.debug("SKIPPED showing ad dialog because of policy ${this::class.java.simpleName}!")
                        return false
                    }
                }
            }
        }

        @Parcelize
        class EveryXTime(val times: Int) : ShowPolicy() {
            override fun shouldShow(context: Context, preference: String?): Boolean {
                var currentTimes =  Utils.getLastPolicyCounter(context, preference)
                currentTimes++
                if (currentTimes < times ) {
                    Utils.setLastPolicyCounter(context, preference, currentTimes )
                    DialogSetup.logger?.debug("SKIPPED showing ad dialog because of policy ${this::class.java.simpleName} (currentTimes = $currentTimes, policy times = $times)!")
                    return false
                } else {
                    DialogSetup.logger?.debug("Showing ad dialog because of policy ${this::class.java.simpleName} (policy times = $times)!")
                    Utils.setLastPolicyCounter(context, preference, 0)
                    return true
                }
            }
        }
    }
}