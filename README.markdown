Sourcecloud is an Eclipse plug-in that lets you create tag clouds of your
source code.

It makes heavy use of
[Cloudio](http://fsteeg.com/2011/09/07/cloudio-swt-based-tag-cloud-visualization-for-zest/)
(i.e.  most of the code comes directly from Cloudio's demo application), the SWT
based tag cloud visualization for Zest. The font size indicates how often a word
occurs in the source code.

Here are some examples:

* [Debug Visualisation Plugin for Eclipse](http://cubussapiens.hu/2011/09/source-code-visualisation-for-my-projects/)
* [Scala Refactoring source code](https://github.com/misto/Sourcecloud/blob/master/examples/refactorings.png)
* [Simple Lambda Calculus parser and evaluator](https://github.com/misto/Sourcecloud/blob/master/examples/untyped.png)

## Why would you want to make tag clouds for source code?

It can give you a quick first impression of the quality of a code base. Ideally,
you should see many names of the project's domain. On the other hand, if
you see lots of `nulls`, `ints` and `Strings`, chances are that the code will be
hard to understand because there are not many domain specific types in
it.

The idea for this project came from Kevlin Henney, who used such tag clouds in a
presentation at Jazoon 2010.

## Installation

There's an [update site for integration builds](http://geometa.hsr.ch/hudson/job/Sourcecloud/ws/ch.misto.sourcecloud.update/target/site/) (no release has been made yet).
Note that you need at least Eclipse Indigo (3.7) and it has not been tested on Eclipse 4. The update site includes the
needed Cloudio/Zest plug-ins.


## But I'm not using Eclipse!

A similar project for .NET languages is the [Source Code Word Cloud
Generator](http://sourcecodecloud.codeplex.com/).

Or, you could just use a few shell commands together with
[Wordle](http://www.wordle.net/advanced):

    grep -E '\w+' -h -o **/*.java | awk ' { words[$1] += 1 } END { for (w in words) print w ":" words[w] } '

