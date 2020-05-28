![cubixel](https://github.com/cubixel/TutorPoint/blob/development/client/src/main/resources/application/media/icons/cubixel_icon_with_text_smaller.png?raw=true "cubixel")

# TutorPoint


This is the repository for the CUBIXEL TutorPoint application.

Group 2, MEng Software Engineering Project.


## Getting Started

This section explains how to download, compile and start the TutorPoint application.
This guide recommends [JetBrains IntelliJ IDEA](https://www.jetbrains.com/idea/) as the 
IDE to use when building this project.

### Step 1. Clone GitHub Repository
Clone the repository for the TutorPoint project.
https://github.com/cubixel/TutorPoint.git

### Step 2. Open Project 
Start IntelliJ, locate the folder containing the cloned repository and 
open the folder the project using IntelliJ but selecting 'Open or Import'.

![Imgur](https://i.imgur.com/TY8QkjS.png)

### Step 3. Select Maven Auto Import
This is a Maven project, Maven will download all necessary dependencies needed
automatically. You may need to select the option in the pop-up at the 
bottom right of IntelliJ to 'Enable Auto-Import'.

![Imgur](https://imgur.com/IF97Eek.png)

### Step 4. Allow Parallel run for Launcher
To allow multiple Clients to run at the same time you need to enable Parallel
Runs for the Launcher Class on the Client module. This is useful to test the 
networking features of TutorPoint such as the 'Interactive Whiteboard'.

Open the file structure in the Project view on the left side of IntelliJ to 
find the Launcher class.

TutorPoint -> client -> src -> main -> java -> application -> Launcher

Right click on the Launcher file and select "Create 'Launcher.main()'...".

![Imgur](https://i.imgur.com/gESS8Ns.png)

On the open window select 'Allow Parallel Run'.

Then select Apply and OK.

![Imgur](https://i.imgur.com/67PtJd0.png)

This will now enable you to run two copies of the Launcher class on the 
Client side to simulate two interacting users.

### Step 5. Start MainServer first
You must start the Server module first before the Client so that the Client 
can connect to it.

Select the MainServer class:

TutorPoint -> server -> src -> main -> java -> MainServer

Right click on it and choose "Run 'MainServer.main()'"

![Imgur](https://i.imgur.com/xwk1pLp.png)

Wait for it to finish starting, once the command line states that the 
DataServer - Started then it is safe to launch the Client.

![Imgur](https://i.imgur.com/XLTdxx1.png)

### Step 6. Start Launcher second

Right click on the Launcher class 

TutorPoint -> client -> src -> main -> java -> application -> Launcher

And choose "Run 'Launcher.main()'"

![Imgur](https://i.imgur.com/CeztAVo.png)

You are now ready to use the TutorPoint application.