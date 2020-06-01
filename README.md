![cubixel](https://github.com/cubixel/TutorPoint/blob/development/client/src/main/resources/application/media/icons/cubixel_icon_with_text_smaller.png?raw=true "cubixel")

# TutorPoint


This is the repository for the CUBIXEL TutorPoint application.

Group 2, 3rd Year MEng Software Engineering Project, University of York.

TutorPoint is a desktop based, Java application intended to provide a digital online lecture 
environment for both students and lecturers. TutorPoint includes functionality for 
XML presentations, an interactive whiteboard, both text and video chat and media streaming.

Visit the [www.cubixel.co.uk](https://www.cubixel.co.uk) for more information.

## Getting Started

This section explains how to download, compile and start the TutorPoint application.
This guide recommends [JetBrains IntelliJ IDEA](https://www.jetbrains.com/idea/) as the 
IDE to use when building this project.

### Step 1. Clone GitHub Repository
Clone the repository for the TutorPoint project.
https://github.com/cubixel/TutorPoint.git

### Step 2. Open Project 
Start IntelliJ, locate the folder containing the cloned repository and 
open the folder the project using IntelliJ by selecting 'Open or Import'.

![Imgur](https://i.imgur.com/TY8QkjS.png)

### Step 3. Select Maven Auto Import
This is a Maven project, Maven will download all necessary dependencies needed
automatically. You may need to select the option in the pop-up at the 
bottom right of IntelliJ to 'Enable Auto-Import'.

![Imgur](https://imgur.com/IF97Eek.png)

### Step 4. Allow Parallel run for Launcher
**If you do not plan on running multiple Client modules on the same machine you may skip this step.**

If you want test networking features of TutorPoint such as the 'Interactive Whiteboard' locally,
without the need for another user, you will need to allow multiple Client modules to run at the 
same time. To do this you need to enable Parallel Runs for the Launcher Class on the Client module.

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

**WARNING! Running the Server module and two Clients simultaneously on one computer can be very demanding on
CPU resources. It is highly recommended that you run the Server module on a separate PC for the 
best experience. See Step 7 for how to set this up.**

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

### Step 7. (Optional) Connecting using a Server hosted remotely

For the best performance and experience of TutorPoint it is recommended that the Server module is running 
on a separate machine. 

You will need to point the Client at that computers IP address in order to connect. This is 
done by changing the variable **connectionAddress** *(Line 44 of the Launcher Class)* to the IP 
address of your remote machine.

TutorPoint -> client -> src -> main -> java -> application -> Launcher

You will need to open ports 5000 and 5001 on your remote network to allow connections from the 
TutorPoint client.