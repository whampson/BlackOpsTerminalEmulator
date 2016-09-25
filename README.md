# BLOTE
The _Call of Duty: **Bl**ack **O**ps_ **t**erminal **e**mulator.

![](http://i.imgur.com/YOKDznn.png)
![](http://i.imgur.com/jIVKU1W.png)
![](http://i.imgur.com/R47uxjM.png)

## General Information
This program emulates the fake computer terminal found in the videogame _Call of Duty: Black Ops_. The aim of BLOTE is to match the behavior of the fake terminal exactly; it should feel as if one were using the original terminal found in the game. This involves including all text files, images, sound files, and mail found on the original terminal, as well as incorporating its various bugs and shortcomings.

BLOTE is still a work-in-progress, although it is nearly complete. It currently lacks many of the text files found on the original terminal, but all of the core functionality has been implemented. Give it a try!

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
To run BLOTE, simply double-click `BLOTE.jar`, but **be sure the `data` folder is in the same directory as `BLOTE.jar`**.

You may specifiy an alternative data directory by running BLOTE from the command-line like so:  
`java -jar BLOTE.jar --data-dir=PATH`

## To-Do
* Finish adding text, image, and sound files.
* Test all commands, compare behavior with actual terminal
