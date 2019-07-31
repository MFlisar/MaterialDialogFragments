# material-dialogfragments  [![Release](https://jitpack.io/v/MFlisar/material-dialogfragments.svg)](https://jitpack.io/#MFlisar/material-dialogfragments)

Common dialog fragments based on https://github.com/afollestad/material-dialogs

Including some special dialogs (separates into their own optional modules) like a custom color dialog, text and number dialogs, multi text and number dialogs, a fast adapter recycler view dialog, an ads dialog and maybe even more to come.

### Gradle (via [JitPack.io](https://jitpack.io/))

1. add jitpack to your project's `build.gradle`:
```groovy
repositories {
    maven { url "https://jitpack.io" }
}
```
2. add the compile statement to your module's `build.gradle`:
```groovy
dependencies {

	// --------
	// core - DialogInfo, DialogList, DialogProgress
	// --------
	
	implementation "com.github.MFlisar.material-dialogfragments:dialogs:<LATEST-VERSION>"
	
	// --------
	// optional
	// --------
	
	// input - DialogInput, DialogNumber, DialogNumberPicker
	implementation "com.github.MFlisar.material-dialogfragments:dialogs-input:<LATEST-VERSION>"
	
	// specials	
	implementation "com.github.MFlisar.material-dialogfragments:dialogs-datetime:<LATEST-VERSION>"
	implementation "com.github.MFlisar.material-dialogfragments:dialogs-fastadapter:<LATEST-VERSION>"	
	implementation "com.github.MFlisar.material-dialogfragments:dialogs-color:<LATEST-VERSION>"
	implementation "com.github.MFlisar.material-dialogfragments:dialogs-frequency:<LATEST-VERSION>"
	implementation "com.github.MFlisar.material-dialogfragments:dialogs-ads:<LATEST-VERSION>"

	// --------
	// alternatively, to include ALL modules at once
	// --------
	
	// implementation 'com.github.MFlisar:material-dialogfragments:<LATEST-VERSION>'
}
```

### Usage

Usage is very simply, you only need to do following:

* `Activities` or `Fragments` using the dialog fragments must implement the simple `DialogFragmentCallback`

        interface DialogFragmentCallback {
			fun onDialogResultAvailable(event: BaseDialogEvent): Boolean
		}

* you create a dialog with the corresponding setup class like e.g.:

    https://github.com/MFlisar/material-dialogfragments/blob/152d242099000fde822b25c78e49f8cbe6f91588/app/src/main/java/com/michaelflisar/dialogs/MainActivity.kt#L145-L151
				
* in the `DialogFragmentCallback` you can handle the result now like following:

        override fun onDialogResultAvailable(event: BaseDialogEvent): Boolean {
		    return when (event) {
				is DialogInfoEvent -> {
					Toast.makeText(this, "Info dialog closed - ID = ${event.id}", Toast.LENGTH_SHORT).show()
					true
				} else false
			}
		}
				
That's all. Optionally you can set up some global settings like following, preferably in your application class once only:

    DialogSetup.SEND_CANCEL_EVENT_BY_DEFAULT = true
	
The `DialogSetup` offers some other settings as well.

Check the demo app for more informations.
