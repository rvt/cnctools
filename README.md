cnctools
========

CNCTools - A G-Code editor, parser and transformer written in java.

(c) 2013 R. van Twisk - http://riesvantwisk.com/cnctools - Source repository : https://github.com/rvt/cnctools

Compilation instructions
========
You need to have a recent version of Java 7 insatlled on your system before you can build cnctools
You can verify this bu entering on your command promot 'java -version', you should see something like:

<pre>
java version "1.7.0_45"
Java(TM) SE Runtime Environment (build 1.7.0_45-b18)
Java HotSpot(TM) 64-Bit Server VM (build 24.45-b08, mixed mode)
</pre>

After that you can compile with: **mvn clean install** and you will end up with a jar file in the target directory.
Please not that the initial compilation might take a while to download all needed libraries. However,
after that they are cached and compilation should be easy.

_Note 1:_
_On OS/X you need to do one extra step to ensure the JavaFX environment is setup properly, see also : http://zenjava.com/javafx/maven/fix-classpath.html_

<pre>
sudo bash
mvn com.zenjava:javafx-maven-plugin:2.0:fix-classpath
</pre>

_Note 2:_
If you are planning to make changes you can re-compile the project without issuing **clean** just use **mvn install**, then
you can re-run as normally (see below).


Starting from prompt
========
Simply run: **mvn jfx:run**


Word of WARNING
========
This is a alpha version and I am still developing this application to get feedback from everybody.
The file format that's currently used to store all program settings is likely to change and not backwards compatible when I move through alpha,
beta and release candidates, if you feel this is important please drop me a note and I can see if I can put htis higher on the list.


TODO
========
* Stabalise the g-code generator so it will beaser to generate gcode in little snippets
* Groovy based dialog's and that can hook into CNCtool without re-compiling
* Various tools like facing, pocketing
* DXF to G-Code generator
* G-Code wrapper to wrap a file from XYZ to XZA for indexers
* Various transformations
* Optimise G-Code preview with better control of zooming, panning, rotating etc, locators (XYZ etc)
* Handle more G-Code words and do better error checking on G-Code

License:
========
BSD3, see LICENSE file

Screenshot
========

Example showing G-Code editor with life preview
![G-Code editor with life preview](http://skitch.rvantwisk.nl/~rvt/bmk/AppMain-20131223-144808.jpg)
