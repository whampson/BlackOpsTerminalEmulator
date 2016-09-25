# BLOTE
The _Call of Duty: **Bl**ack **O**ps_ **T**erminal **E**mulator.

## General Information
This program emulates the fake computer terminal found in the videogame
_Call of Duty: Black Ops_. The aim of BLOTE is to match the behavior of the
fake terminal exactly; it should feel as if one were using the original
terminal found in the game. This involves including all text files, images,
sound files, and mail found on the original terminal, as well as incorporating
its various bugs and shortcomings.

BLOTE is still a work-in-progress, although it is nearly complete. It currently
lacks many of the text files found on the original terminal, but all of the
core functionality has been implemented. Give it a try!

## How to Use
### Compiling
#### Requirements
* Java SE Development Kit 7 or later
* Apache Ant or NetBeans

#### Compilation Steps
* To compile with NetBeans, simply open the project in NetBeans and click "Build".
* To compile with Ant, navigate to the repo directory and run `ant`.

The compiled sources will be output to `dist/BLOTE.jar`.

### Running
Launch a shell and navigate to the repo directory. Launch BLOTE by running  
`java -jar dist/BLOTE.jar`

If using NetBeans, simply click the "Run" button.

You may run BLOTE from anywhere on your computer if you'd like, but **be sure
the `data` and `res` folders are in the working directory** or else BLOTE will
not run correctly.

## To-Do
* Finish adding text, image, and sound files.
* Test all commands, compare behavior with actual terminal

## Screenshots
![](http://i.imgur.com/YOKDznn.png)
![](http://i.imgur.com/jIVKU1W.png)
![](http://i.imgur.com/R47uxjM.png)
![](http://i.imgur.com/S5ku5bh.png)
