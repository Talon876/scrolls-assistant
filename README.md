Scrolls Assistant
=================

An assistant chat-bot for Mojang's Scrolls game.

Eclipse Setup
-------------
Clone the repository to your eclipse workspace and open a shell window to this folder.

Execute `gradlew eclipse`. The first time this runs it will download Gradle to a .gradle folder in the repository, then it will build eclipse configuration files that allow you to import it as a project.

If any changes are made to the dependencies in the build.gradle file, you will have to rerun `gradlew eclipse` and then refresh the project in eclipse.

After running the project, open the Run Configurations and add `-Dlog4j.configuration=file:log4j.properties` as a VM argument if you wish to alter the logging level.

[![Build Status](https://travis-ci.org/Talon876/scrolls-assistant.png?branch=master)](https://travis-ci.org/Talon876/scrolls-assistant)
