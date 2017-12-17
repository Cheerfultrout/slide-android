##Slide Android
Readme needs work. So does docs.

Reasons for updating Slide:
-saw it, thought it was cool, wanted it do more stuff. (And run on newer devices)

known issues:
-On-screen keyboard is sending only '/' when keys are pressed (multiple for multi-char inputs)
-when connected over USB, device name displays simply as "local host" since the 7.0+ crash fix
-desktop client won't launch if adb.exe is already running. This is a good thing, except that
on closing, adb.exe stays running in the background (when it probably should be closed on exit)
-apparently the app is no longer indexable by google search or something? (doesn't matter to me)
-connecting to the server application is finicky (may need an update)

NB:
-While I've done a decent amount of java coding, this is the first Android app I've really worked
on, so progress may be slow
-I don't have access to an SPen, and I'm unlikely to gain access, so I probably won't be
updating anything SPen-related beyond refactoring/cleanup
--> a reported issue was SPen input not working on certain devices - I won't be fixing that
-Emulated devices cannot be used for any in-depth debugging, because (as far as I know) there
isn't a way to actually connect to the desktop client from an emulated device. (oh well)
-I'm not sure what form most README files take, so I hope this is fine.
-cannot debug over USB properly, since when connected to android studio over USB, you can't
connect to the desktop client via USB. I'm planning to try and add in an option to default
to a WIFI connection even if USB is connected and USB debug is enabled -> this should work
as a suitable workaround.

Planned updates (after known issues are dealt with):
-implement a reverse tethering solution, akin to that used by TrinusVR, as a way to connect
->this may ultimately not be any different than connecting the current way, but it allows
users to connect via USB without accessing the developer options, which is a good thing.

fixed:
-no longer crashes on Android 7.0+