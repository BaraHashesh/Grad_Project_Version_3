# Wireless Storage Device

This project was generated using the [Intellij IDE](https://www.jetbrains.com/idea/)

## Project Description

This project is implemented in order to create a connection between storage devices 
and the user device (PC/MAC/Mobile) via a wireless connection.

## What's to Come

The project currently is nearly finished as basic functionalities for a storage device
have already been implemented (store, delete, retrieve, and browse operations), however
several new features are planned to be added soon, which are:

1. Optimize File Meta data	

		This has already been implemented
	
2. Compression 

		Dropped due to long execution time

3. Encryption

	 	Have been implemented using java WebSockets SSL options
	 
4. Try to change the structure of the system as such it supports multiple storage devices 
at any given time (previous structure supports one device) 

		Server side semaphore have been implemented,
        only integrating them in the project is required.

5. Create a functioning mobile version of the project (android) 

		Work in progress (nearly completed)

6. Create pop-ups to appear in case of errors

		Have been implemented using the JavaFx Alert module
