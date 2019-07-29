# material-dialogfragments
Common dialog fragments based on https://github.com/afollestad/material-dialogs

Including some special dialogs lile a custom color dialog, text and number dialogs, multi text and number dialogs, a fast adapter recycler view dialog and maybe more to come.

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
	implementation "com.github.MFlisar.material-dialogfragments:dialogs:<LATEST-VERSION>"
	
	// optional:
	implementation "com.github.MFlisar.material-dialogfragments:dialogs-input:<LATEST-VERSION>"
	implementation "com.github.MFlisar.material-dialogfragments:dialogs-datetime:<LATEST-VERSION>"
	implementation "com.github.MFlisar.material-dialogfragments:dialogs-fastadapter:<LATEST-VERSION>"
	implementation "com.github.MFlisar.material-dialogfragments:dialogs-color:<LATEST-VERSION>"
	implementation "com.github.MFlisar.material-dialogfragments:dialogs-frequency:<LATEST-VERSION>"

	// alternatively, to include ALL modules at once
	// implementation 'com.github.MFlisar:material-dialogfragments:<LATEST-VERSION>'
}
```