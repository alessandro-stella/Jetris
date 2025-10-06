#!/bin/bash
CP=".:lib/jline-terminal-3.26.0.jar:lib/jline-reader-3.26.0.jar:lib/jline-terminal-jna-3.26.0.jar"
javac -cp "$CP" Jetris.java util/*.java tetrominoes/*.java && \
java -cp "$CP" Jetris
