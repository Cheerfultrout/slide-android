##Slide Android
Readme needs work. So does docs.

Reasons for updating Slide:
-saw it, thought it was cool, wanted it do more stuff. (And run on newer devices)

known issues:
-On-screen keyboard is sending only '/' when keys are pressed (multiple for multi-char inputs)
-crashes on android 7.0+ upon any user interaction which would result in data-packets being
sent to the computer eg touch-input, keyboard, etc. As noted in a reported issue this is because
android 7.0+ strict mode throws an error when certain operations are done on the main thread,
and I am currently working to track down where this is happening (disabling strict mode was
a suggested way of fixing it, but I'd rather fix the issue directly)
-cannot debug over USB properly, since when connected to android studio over USB, you can't
connect to the desktop client via USB. I'm planning to try and add in an option to default
to a WIFI connection even if USB is connected and USB debug is enabled -> this should work
as a suitable workaround.

NB:
-I don't have access to an SPen, and I'm unlikely to gain access, so I won't be updating
anything SPen-related beyond refactoring/cleanup
--> a reported issue was SPen input not working on certain devices - I won't be fixing that
-Emulated devices cannot be used for any in-depth debugging, because (as far as I know) there
isn't a way to actually connect to the desktop client from an emulated device. (oh well)
-I'm not sure what form most README files take, so I hope this is fine.

Planned updates (after known issues are dealt with):
-implement a reverse tethering solution, akin to that used by TrinusVR, as a way to connect
->this may ultimately not be any different than connecting the current way, but it allows
users to connect via USB without accessing the developer options, which is a good thing.