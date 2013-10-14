Scrolls Assistant
=================
[![Build Status](https://travis-ci.org/Talon876/scrolls-assistant.png?branch=master)](https://travis-ci.org/Talon876/scrolls-assistant)

An assistant chat-bot and api for Mojang's Scrolls game.

Eclipse Setup
-------------
Clone the repository to your eclipse workspace and open a shell window to this folder.

Execute `gradlew eclipse`. The first time this runs it will download Gradle to a .gradle folder in the repository, then it will build eclipse configuration files that allow you to import it as a project.

If any changes are made to the dependencies in the build.gradle file, you will have to rerun `gradlew eclipse` and then refresh the project in eclipse.

After running the project, open the Run Configurations and add `-Dlog4j.configuration=file:log4j.properties` as a VM argument if you wish to alter the logging level.

How to Run
----------
Create a settings.json file then run the jar.

Settings File:

    {
      "name": "--your rsa encrypted username/email",
      "server": "--your rsa encrypted password--"
    }


Settings Description:

|Key|Description|
|---|-----------|
|encryptedUsername| RSA encrypted Scrolls username/email obtained with the -e option|
|encryptedPassword | RSA encrypted Scrolls password obtained with the -e option|

Running the jar:

    Usage: java -jar scrolls-assistant.jar [options]
		Examples: Run: java -jar scrolls-assistant.jar -f settings.json
		Encrypt: java -jar scrolls-assistant.jar -e -u someone@somewhere.net -p myp@ssw0rd!

		Help:
		  [-h|--help]
		        Displays this help message
		
		  [-e|--encrypt]
		        Encrypt's username/password
		
		  [(-f|--file) <file>]
		        The path to the file to load settings from (default: settings.json)
		
		  [(-u|--username) <username>]
		        The username to encrypt when using --encrypt
		
		  [(-p|--password) <password>]
		        The password to encrypt when using --encrypt

