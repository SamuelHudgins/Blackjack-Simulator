# Description
Blackjack Simulator is a 2D desktop application developed in Java. It's based on the popular blackjack casino game and allows players to perform several actions associated with most blackjack games, including hitting, standing, doubling, splitting, and accepting insurance. The player and dealer play with a single 52-card deck in this version.

# Features
- Single-player matches against a dealer.
- Text-based tutorial to teach new players how to play blackjack.
- Creates a local database and allows players to register, log into, and delete accounts.
- Tracks player balances, wins, and losses and provides a stats screen for comparing stats against other players. 
- Displays the best possible decisions during blackjack matches.

# How To Install
## Prerequisites
- Computer with Windows 10 operating system (this application may work on Windows 11 and other operating systems).
- You will need to have Java installed on your computer and set the PATH variable to point to the bin directory of the Java installation.

## Installation Steps
1. Download the [CMSC495-Captsone.jar](https://github.com/SamuelHudgins/Blackjack-Simulator/blob/main/build/RunnableJAR/CMSC495-Captsone.jar) file to a folder in a convenient location.
1. Open your operating system's command line interface (the Command Prompt for Windows or Terminal for Mac).
1. Type `Java -jar pathToJARLocation/CMSC495-Captsone.jar` into the command line window (change `pathToJARLocation` to the path to the JAR's folder).
   - Alternatively, you can type `Java -jar ` into the command line window, then select and drag the JAR file into the window, and it should automatically fill out the file path.
1. Press the "Enter" or "Return" key on your keyboard.

### How to run the program with a batch (.bat) file:
1. Download the [CMSC495-Captsone.jar](https://github.com/SamuelHudgins/Blackjack-Simulator/blob/main/build/RunnableJAR/CMSC495-Captsone.jar) and [JarRunner.bat](https://github.com/SamuelHudgins/Blackjack-Simulator/blob/main/build/RunnableJAR/JarRunner.bat) files to a folder in a convenient location.
	- Alternatively, you can create a new batch file in the folder containing the 'CMSC495-Captsone.jar' file, then copy and paste the code in 'JarRunner.bat' into the newly created batch file.
1. Make sure the 'CMSC495-Captsone.jar' and the batch file ('JarRunner.bat' or the one you created) are in the same folder.
1. Double-click on the batch file.

# Built With
- [JavaFX](https://openjfx.io/)
- [SQLite JDBC](https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc)

# Acknowledgments
- The YouTube channel [Bro Code](https://www.youtube.com/watch?v=9XJicRt_FaI) for their tutorials on JavaFX applications.
- [Lokesh Gupta](https://howtodoinjava.com/java/java-security/aes-256-encryption-decryption/) for their password encryption and decryption algorithm.
- [Blackjack Apprenticeship](https://www.blackjackapprenticeship.com/blackjack-strategy-charts/) for their blackjack strategy chart.

# Contributors
- Robert Carswell
- Jordan DeLaGarza
- Patrick Smith
- Jose Reyes
- Samuel Hudgins
