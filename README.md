cnctools
========

CNCTools - A G-Code editor, parser and transformer written in java.

(c) 2013-2014 R. van Twisk - http://riesvantwisk.com/cnctools - Source repository : https://github.com/rvt/cnctools

Compilation instructions
========
You need to have a recent version of Java 7 insatlled on your system before you can build cnctools
You can verify this by entering on your command promot 'java -version', you should see something like:

<pre>
java version "1.7.0_45"
Java(TM) SE Runtime Environment (build 1.7.0_45-b18)
Java HotSpot(TM) 64-Bit Server VM (build 24.45-b08, mixed mode)
</pre>

Get a clone of CNC tools using, I choose my home directory for explanation, but you might want to choose a better location for your perpose.

<pre>
rvt@rvt:~$ git clone http://github.com/rvt/cnctools
Cloning into 'cnctools'...
remote: Reusing existing pack: 679, done.
remote: Counting objects: 158, done.
remote: Compressing objects: 100% (101/101), done.
remote: Total 837 (delta 50), reused 130 (delta 30)
Receiving objects: 100% (837/837), 342.84 KiB | 389.00 KiB/s, done.
Resolving deltas: 100% (429/429), done.
Checking connectivity... done
rvt@rvt:~$ cd cnctools
rvt@rvt:~/cnctools$
</pre>

After that you can compile with: **mvn clean install** and you will end up with a jar file in the target directory under **/cnctools/target**

Please note that the first compilation might take a while to download all needed libraries. However, after that they are cached and compilation should be quickly (seconds).

<pre>
rvt@rvt:~/cnctools$ mvn install
[INFO] Scanning for projects...
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Build Order:
[INFO]
[INFO] CNC Tools
[INFO] CNC - G-Code generator
[INFO] CNC - G-Code Parser
[INFO] CNC - Tools
..
..
..
[INFO]
[INFO] --- maven-jar-plugin:2.3.2:jar (default-jar) @ cnctools ---
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO]
[INFO] CNC Tools ......................................... SUCCESS [0.002s]
[INFO] CNC - G-Code generator ............................ SUCCESS [2.617s]
[INFO] CNC - G-Code Parser ............................... SUCCESS [0.144s]
[INFO] CNC - Tools ....................................... SUCCESS [4.618s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 7.653s
[INFO] Finished at: Mon Jan 13 19:59:58 ECT 2014
[INFO] Final Memory: 10M/245M
[INFO] ------------------------------------------------------------------------
rvt@rvt:~/cnctools$
</pre>

_Note 1:_
_On OS/X and Linux you need to do one extra step to ensure the JavaFX environment is setup properly, see also : http://zenjava.com/javafx/maven/fix-classpath.html_

<pre>
rvt@rvt:~$ sudo bash
root@rvt:~# mvn com.zenjava:javafx-maven-plugin:2.0:fix-classpath
[INFO] Scanning for projects...
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] Building Maven Stub Project (No POM) 1
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] --- javafx-maven-plugin:2.0:fix-classpath (default-cli) @ standalone-pom ---
..
..
Are you sure you want to continue? (y/n)
y
[WARNING] Fixing JRE bootclasspath to include JavaFX runtime and native files
[WARNING] All applications using JRE will be affected: ....../jdk1.7.0_45.jdk/Contents/Home/jre
[INFO] JFX Runtime JAR already exists in the JRE extensions directory, no action was taken (...../jdk1.7.0_45.jdk/Contents/Home/jre/lib/ext/jfxrt.jar)
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 3.048s
[INFO] Finished at: Mon Jan 13 20:04:30 ECT 2014
[INFO] Final Memory: 7M/310M
[INFO] ------------------------------------------------------------------------
root@rvt:~# exit
</pre>


_Note 2:_
If you are planning to make changes you can re-compile the project without issuing **clean** just use **mvn install**, then
you can re-run as normally (see below).


Starting from prompt
========
Simply run:

<pre>
rvt@rvt:~/cnctools$ cd cnctools
rvt@rvt:~/cnctools/cnctools$ export _JAVA_OPTIONS="-Djava.library.path=target/natives -Xmx256m -XX:+UseConcMarkSweepGC -XX:MaxPermSize=64m -Xss1m"
rvt@rvt:~/cnctools/cnctools$ mvn jfx:run
</pre>


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
![G-Code editor with life preview](http://skitch.rvantwisk.nl/~rvt/blog/AppMain-20140112-163515.jpg)
