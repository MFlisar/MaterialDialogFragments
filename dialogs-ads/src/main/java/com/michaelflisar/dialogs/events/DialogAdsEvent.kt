package com.michaelflisar.dialogs.events

import com.michaelflisar.dialogs.classes.BaseDialogSetup

class DialogAdsEvent(setup: BaseDialogSetup, buttonIndex: Int?, val data: Data) :
    BaseDialogEvent(setup, buttonIndex) {

    sealed class Data {

        class RewardReceived(val amount: Int) : Data()
        object InterstitialShown : Data()
        class ClosedByUser(
            val errorLoadingBanner: Exception?,
            val errorLoadingBigAd: Exception?
        ) : Data()
    }
}