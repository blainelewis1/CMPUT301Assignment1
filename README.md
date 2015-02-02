# CMPUT301Assignment1

This is a very basic local claim management system built for CMPUT 301 (software engineering)

The app is compiled as bin/CMPUT301Assignment1.apk

I made use of the GSON library for serialization, it can be found here under the Apache 2.0 license: https://code.google.com/p/google-gson/

I also used some of the icons provided here which are free for use: https://developer.android.com/design/downloads/index.html#action-bar-icon-pack

My app is entirely localized, if you view with a french locale then your numbers will be displayed with , decimals etc etc. However, it is not translated. 

My UML diagram is minimal because UML is intended as a quick overview of class relationships. If you need to find inner workings of classes then you should be reading documentation not looking at models. The notes complicate the diagram, but the assignment spec indicated I should have them.

Ideally I would have separate packages for different things eg. models, activities, util but when I tried doing that Eclipse corrupted my manifest file and it was taking too long to fix so I reverted

#Citations

http://stackoverflow.com/a/2057163/1036813 02-02-2015 Blaine Lewis
I used this code snippet to create nice looking representations of currencies.

https://sites.google.com/site/gson/gson-user-guide 02-02-2015 Blaine Lewis
Used a reflection snippet to make serialization work. Same snippet as was used in class


#License:

Copyright 2015 Blaine Lewis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
