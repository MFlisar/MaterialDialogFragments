# MaterialDialogFragments  [![Release](https://jitpack.io/v/MFlisar/material-dialogfragments.svg)](https://jitpack.io/#MFlisar/MaterialDialogFragments)

**V2 - WORK IN PROGRESS - WILL BE FINISHED NEXT WEEK (Calendar Week 35)**

This library helps to show a `Dialog` - actually a `DialogFragment` - and takes care of sending events to the parent `Activity`/`Fragment` without leaking it. It's made for the `Theme.Material3` theme and tries to follow styling that's described here:

[M3 Material Dialogs](https://m3.material.io/components/dialogs/implementation/android)

It supports following 3 styling "types" and changing between styles is as simple as defining a flag to inidcate which style to use:

* **Dialog**
* **BottomSheet**
* **FullscreenDialog** 

# Introduction

It works as simply as following: From within an `Activity`/`Fragment` create a dialog like following:

```kotlin
DialogInfo(
  id = 1,
  title = "Info Title".asText(), // Int, String and any CharSequence are supported (e.g. SpannableString)
  text = "Some info text...".asText()
)
  .show(parent) // parent is a fragment or an activity
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
* [Number](#number)
* [DateTime](#datetime)
* [Color](#color)

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

## Number

```gradle
dependencies {
  implementation "com.github.MFlisar.MaterialDialogFragments:number:<LATEST-VERSION>"
}
```

## DateTime

```gradle
dependencies {
  implementation "com.github.MFlisar.MaterialDialogFragments:datetime:<LATEST-VERSION>"
}
```

## Color

```gradle
dependencies {
  implementation "com.github.MFlisar.MaterialDialogFragments:color:<LATEST-VERSION>"
}
```

# DEMO APP

Check the [demo app](app/src/main/java/com/michaelflisar/dialogs/MainActivity.kt) for more informations.

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
	- [x] List
	- [x] NumberPicker
	- [ ] DateTime
	- [ ] Color
	- [ ] Ads
	- [ ] Frequency
- [ ] Optional Features
	- [x] List - Filtering
	- [ ] List - Providing a full custom adapter??? eventually...
	- [ ] Better default value handling? e.g. ListDialog default icon size? would need to be some extensible solution so that each dialog can register its defaults in there...
