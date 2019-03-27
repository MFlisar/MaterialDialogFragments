# material-dialogfragments
Common dialog fragments based on https://github.com/afollestad/material-dialogs

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
	implementation "com.github.MFlisar.material-dialogfragments:dialogs-fastadapter:<LATEST-VERSION>"
	implementation "com.github.MFlisar.material-dialogfragments:dialogs-color:<LATEST-VERSION>"

	// alternatively, to include ALL modules at once
	// implementation 'com.github.MFlisar:material-dialogfragments:<LATEST-VERSION>'
}
```

#### State:

- [x] Dialogs
- [x] Color Dialog
- [ ] FastAdapter Dialog DEMO