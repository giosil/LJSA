#!/bin/sh

CP="./target/LJSA-gui-2.0.0.jar:./target/swingup-1.0.0.jar:./lib/swing3rd.jar"

if [ "x$JAVA_HOME" = "x" ]; then
  echo "JAVA_HOME not setted"
else
  "$JAVA_HOME"/bin/java -cp "$CP" org.dew.swingup.main.Main
fi
