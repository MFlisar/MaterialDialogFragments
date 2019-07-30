package com.michaelflisar.dialogs.classes

sealed class SendResultType {

    /**
     * result is send to the activity of the dialog AND to all fragments (including sub fragments, sub sub fragments, ...) of this activity
     */
    class All(val stopAfterFirstHandled: Boolean) : SendResultType()

    /**
     * result is ONLY send to parent fragment of the dialog
     */
    object ParentFragment : SendResultType()

    /**
     * result is ONLY send to target fragment of the dialog
     */
    object TargetFragment : SendResultType()

    /**
     * result is ONLY send to the dialog fragment itself
     */
    object Fragment : SendResultType()

    /**
     * result is ONLY send to activity of the dialog
     */
    object Activity : SendResultType()

    class First(
        val priority: List<FirstType> =
            listOf(
                FirstType.TargetFragment,
                FirstType.ParentFragment,
                FirstType.Fragment,
                FirstType.Activity
            )
    ) : SendResultType() {

        enum class FirstType {
            Activity,
            ParentFragment,
            TargetFragment,
            Fragment
        }
    }

    /**
     * events are not send anywhere, handle the results via the [com.michaelflisar.dialogs.DialogSetup.resultHandler] manually
     */
    object Manual : SendResultType()
}