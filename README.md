# MaterialDialogFragments  [![Release](https://jitpack.io/v/MFlisar/material-dialogfragments.svg)](https://jitpack.io/#MFlisar/mMaterialDialogFragments)

Common dialog fragments based on https://github.com/afollestad/material-dialogs

Including some special dialogs (separates into their own optional modules) like a custom color dialog, text and number dialogs, multi text and number dialogs, a fast adapter recycler view dialog, an ads dialog and maybe even more to come.

### Screenshots


| | | |
|:-------------------------:|:-------------------------:|:-------------------------:|
| ![Dialog](images/info_dialog.jpg?raw=true "Title") Simple info dialog | ![Dialog](images/info_dialog2.jpg?raw=true "Title") Info dialog with Timeout + Warning | ![Dialog](images/info_dialog3.jpg?raw=true "Title") Info dialog with HTML content |
| ![Dialog](images/list_dialog.jpg?raw=true "Title") Simple list dialog | ![Dialog](images/list_dialog2.jpg?raw=true "Title") Multi select list dialog with images | ![Dialog](images/info_dialog3.jpg?raw=true "Title") Multi select list dialog with custom check marks |
| ![Dialog](images/list_dialog4.jpg?raw=true "Title") Multi click list dialog | ![Dialog](images/list_dialog5.jpg?raw=true "Title") Custom parcelable data list dialog | ![Dialog](images/color_dialog.jpg?raw=true "Title") Color picker dialog |
| ![Dialog](images/datetime_dialog.jpg?raw=true "Title") Datetime dialog | ![Dialog](images/edittext_dialog.jpg?raw=true "Title") EditText dialog | ![Dialog](images/edittext_dialog2.jpg?raw=true "Title") Multi EditText dialog |
| ![Dialog](images/number_dialog.jpg?raw=true "Title") Number dialog | ![Dialog](images/number_dialog2.jpg?raw=true "Title") Custom number dialog | ![Dialog](images/number_dialog3.jpg?raw=true "Title") Multi number dialog |
| ![Dialog](images/progress_dialog.jpg?raw=true "Title") Progress dialog | ![Dialog](images/frequency_dialog.jpg?raw=true "Title") Frequency dialog | ![Dialog](images/debug_dialog.jpg?raw=true "Title") Debug settings dialog |

### Gradle (via [JitPack.io](https://jitpack.io/))

1. add jitpack to your project's `build.gradle`:
```groovy
repositories {
    maven { url "https://jitpack.io" }
}
```
2. add the implementation statement(s) to your module's `build.gradle`:
```groovy
dependencies {

	// --------
	// core - DialogInfo, DialogList, DialogProgress
	// --------
	
	implementation "com.github.MFlisar.MaterialDialogFragments:dialogs:<LATEST-VERSION>"
	
	// --------
	// optional
	// --------
	
	// input - DialogInput, DialogNumber, DialogNumberPicker
	implementation "com.github.MFlisar.MaterialDialogFragments:dialogs-input:<LATEST-VERSION>"
	
	// specials	
	implementation "com.github.MFlisar.MaterialDialogFragments:dialogs-datetime:<LATEST-VERSION>"
	implementation "com.github.MFlisar.MaterialDialogFragments:dialogs-fastadapter:<LATEST-VERSION>"	
	implementation "com.github.MFlisar.MaterialDialogFragments:dialogs-color:<LATEST-VERSION>"
	implementation "com.github.MFlisar.MaterialDialogFragments:dialogs-frequency:<LATEST-VERSION>"
	implementation "com.github.MFlisar.MaterialDialogFragments:dialogs-ads:<LATEST-VERSION>"

	// --------
	// alternatively, to include ALL modules at once
	// --------
	
	// implementation 'com.github.MFlisar:MaterialDialogFragments:<LATEST-VERSION>'
}
```

### Usage

Usage is very simply, you only need to do following:

* `Activities` or `Fragments` using the dialog fragments must implement the simple `DialogFragmentCallback` interface:

        interface DialogFragmentCallback {
			fun onDialogResultAvailable(event: BaseDialogEvent): Boolean
		}

* you create a dialog with the corresponding setup class like e.g.:

        DialogInfo(
			1,
			"Info Title".asText(),
			"Some info label".asText()
		)
				.create()
				.show(this)
				
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
