# Software-Engineering-Project 'User Registration' Story 001, 002, 004 Branch
Repository for Group 2, MEng Software Engineering Project.

“As a user, I want to securely register a new account which is stored on a server so that I can access my accounts from any computer.”

| REQUIREMENTS        | TESTS         |
| --------------------|:-------------:|
| Can create a new account.          |“Make a connection between client and server.”|
| Account information is stored on the server.          |“Send information to server, with hashed password.”|
| User details received by client.      |“Check if username already exists.”| 
| |“Server creates account.”| 
| |“Return user data or error back to client.”| 

“As a user, I want to securely sign into an existing account stored on a server so that I can continue a session from where I left off.”

| REQUIREMENTS        | TESTS         |
| --------------------|:-------------:|
| User enters details into login screen.          |“Hash entered password and send to server alongside username.”|
| Password is hashed before sending to Server.         |“Verify server responds positively with correct log-in results.”|
| Details are checked against know user and logged in on client side. |“Verify server displays an error message if not correct.”| 
| |“Client requests user details.”| 

“User is taken from login screen to the main screen of client app after successfully logging in to access the main home screen.”

| REQUIREMENTS        | TESTS         |
| --------------------|:-------------:|
| The login screen closes and the main window opens after user logs in.  |“Confirm client side is taken to main screen after logging in.”|
