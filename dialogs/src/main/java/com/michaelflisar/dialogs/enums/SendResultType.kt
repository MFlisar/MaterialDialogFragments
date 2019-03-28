package com.michaelflisar.dialogs.enums

enum class SendResultType {
    /*
     * activity of dialog + ALL fragments inside it
     */
    FullHierarchy,
    /*
     * only direct parent fragment of dialog
     */
    ParentFragmentOnly,
    /*
    * only target fragment of dialog
    */
    TargetFragmentOnly,
    /*
     * only the parent activity
     */
    ActivityOnly,
    /*
     * only the target fragment, parent fragment, fragment or activity - whatever exists
     */
    TargetOrParentOrFragmentOrActivity
}