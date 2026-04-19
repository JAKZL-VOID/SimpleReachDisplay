#!/bin/sh
##############################################################################
# Gradle wrapper startup script for POSIX systems
##############################################################################

# Attempt to find JAVA_HOME
if [ -z "$JAVA_HOME" ] ; then
    JAVA_HOME=$(java -XshowSettings:properties -version 2>&1 | grep 'java.home' | sed 's/.*= //')
fi

APP_HOME=$(dirname "$0")
APP_HOME=$(cd "$APP_HOME" && pwd -P)
CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

if ! [ -f "$CLASSPATH" ]; then
    echo "ERROR: gradle-wrapper.jar missing. See README.md for setup instructions."
    exit 1
fi

exec "$JAVA_HOME/bin/java" $JAVA_OPTS -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
