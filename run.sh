#!/bin/bash
CP=".:jline/jline-terminal-3.26.0.jar:jline/jline-reader-3.26.0.jar:jline/jline-terminal-jna-3.26.0.jar"
javac -cp "$CP" Jetris.java util/*.java tetrominoes/*.java && \
java -cp "$CP" Jetris
