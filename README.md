# MaterialDialogFragments  [![Release](https://jitpack.io/v/MFlisar/material-dialogfragments.svg)](https://jitpack.io/#MFlisar/MaterialDialogFragments)

This library helps to show a `Dialog` - actually a `DialogFragment` - and takes care of sending events to parent `Activity`/`Fragment` without leaking and with a very easy mechanism. It's made for the `Theme.Material3` theme and tries to follow styling that's described here on [M3 Material Dialogs](https://m3.material.io/components/dialogs/implementation/android) and uses the [MaterialAlertDialogBuilder](https://developer.android.com/reference/com/google/android/material/dialog/MaterialAlertDialogBuilder) if possible.

It supports 3 stylings, namely **Dialog**, **BottomSheet** and **FullscreenDialog** and it allows to change the style decision at any point easily without the need of any code adjustments.

# State

- [ ] General
	- [x] Dialog Style
	- [ ] BottomSheet Style
		- [ ] sticky footer
		- [x] position bug on screen rotation (also in combination with show/hide keyboard)
		- [ ] elevate header/footer on scroll
		- [ ] keyboard should push up the whole layout (e.g. for InputDialog)
	- [x] Fullscreen Style
- [ ] Features
	- [ ] Swipe Dismiss + support of nested scrolling containers
	- [ ] BottomSheet - flag to support "expand to fullscreen style" (pos button in toolbar is enabled, pos button in footer is removed, toolbar replaces the title)
- [ ] Dialogs
	- [x] Info
	- [x] Input
	- [ ] List
	- [ ] DateTime
	- [ ] Color
	- [ ] Ads
	- [ ] Frequency

# Example

It works as simply as following: From within an `Activity`/`Fragment` create a dialog like following:

```kotlin
DialogInfo(
  id = 1,
  title = "Info Title".toText(), // int, string and CharSequence are supported, simply call 'toText()' on an instance of this type
  text = "Some info text...".toText()
).show(parent) // parent is a fragment or an activity
```

From any lifecycle aware component (like e.g. an `Activity`/`Fragment`) you can do then following:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  
  // ID is optional, you can also listen to all events of a special type if desired
  onMaterialDialogEvent<DialogInfo.Event>(id = 1) { event ->
    // dialog event received -> process it...
  }
}
```

That's it, the library will take care to unregister the listener if the `Activity`/`Fragment` is destroyed and will avoid leaks like this. Event though the `DialogFragments` are recreated automatically after restoration and screen rotation the parent will be able to receive all events without any further code requirements by the developer. 

# Modules

* [Core](#core)
* [Info](#info)
* [Input](#input)
* [List](#list)

## Core

```gradle
dependencies {
  implementation "com.github.MFlisar.MaterialDialogFragments:core:<LATEST-VERSION>"
}
```

## Info

```gradle
dependencies {
  implementation "com.github.MFlisar.MaterialDialogFragments:info:<LATEST-VERSION>"
}
```

## Input

```gradle
dependencies {
  implementation "com.github.MFlisar.MaterialDialogFragments:input:<LATEST-VERSION>"
}
```

## List

```gradle
dependencies {
  implementation "com.github.MFlisar.MaterialDialogFragments:list:<LATEST-VERSION>"
}
```














# OLD

**Features**

* handles fragments save/restore states for you automatically
* includes mechanism to handle dialog events (even after screen restoration) inside activity and/or fragments
* includes some special dialogs (extracted in own modules) like e.g. a custom color dialog, text and number dialogs, multi text and number dialogs, a fast adapter recycler view dialog, an ads dialog - maybe even more to come
* also supports to show dialogs as dialog or bottom sheet
* easily extendable - create you own dialogs, simply check out the extension modules

### Screenshots

| | | |
|:-------------------------:|:-------------------------:|:-------------------------:|
| ![Dialog](images/info_dialog.jpg?raw=true "Dialog") Simple info dialog | ![Dialog](images/info_dialog2.jpg?raw=true "Dialog") Info dialog with Timeout + Warning | ![Dialog](images/info_dialog3.jpg?raw=true "Dialog") Info dialog with HTML content |
| ![Dialog](images/list_dialog.jpg?raw=true "Dialog") Simple list dialog | ![Dialog](images/list_dialog2.jpg?raw=true "Dialog") Multi select list dialog with images | ![Dialog](images/list_dialog3.jpg?raw=true "Dialog") Multi select list dialog with custom check marks |
| ![Dialog](images/list_dialog4.jpg?raw=true "Dialog") Multi click list dialog | ![Dialog](images/list_dialog5.jpg?raw=true "Dialog") Custom parcelable data list dialog | ![Dialog](images/color_dialog.jpg?raw=true "Dialog") Color picker dialog |
| ![Dialog](images/datetime_dialog.jpg?raw=true "Dialog") Datetime dialog | ![Dialog](images/edittext_dialog.jpg?raw=true "Dialog") EditText dialog | ![Dialog](images/edittext_dialog2.jpg?raw=true "Dialog") Multi EditText dialog |
| ![Dialog](images/number_dialog.jpg?raw=true "Dialog") Number dialog | ![Dialog](images/number_dialog2.jpg?raw=true "Dialog") Custom number dialog | ![Dialog](images/number_dialog3.jpg?raw=true "Dialog") Multi number dialog |
| ![Dialog](images/progress_dialog.jpg?raw=true "Dialog") Progress dialog | ![Dialog](images/frequency_dialog.jpg?raw=true "Dialog") Frequency dialog | ![Dialog](images/debug_dialog.jpg?raw=true "Dialog") Debug settings dialog |

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
			1, // ID - this allows you to identify dialog events in the callback
			"Info Title".asText(),
			"Some info label".asText()
		)
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
