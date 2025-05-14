# Shop Checkout Simulator

Simulation project for Metropolia AMK.

Group 5: Phong Le, Casper Kapiainen, Leevi Tiihonen, Tino Vuorela

Java program that simulates store checkout queues in realtime, with interface built with JavaFX, and full Javadoc documentation.

## Features

### Visualisation

Realtime animation of customers going through service points.

* Entrance: This is where the customers enter the store. Each customer is given a unique ID and item amount.
* Shopping area: Customers move from the entrance to the shopping area where they will spend most of their time, after which they will move on to one of the three checkouts.
* Self-checkout: 30% of regular customers exit through this checkout.
* Regular checkout: 70 % of regular customers exit through this checkout.
* Express checkout: Express customers exit through this checkout.

### Statistics

The statistics tab displays information about the simulation in real time and can also be viewed after the simulation has finished running. The graph visualizes queue lengths over time, while additional information such as total customers, average wait time and maximum queue lengths are shown below.

### Configuration

The configuration tab contains multiple different configurable parameters that affect how the simulation behaves:

* Customer Arrival Distribution: Choose between Negexp, Normal, and Uniform.
* Arrival Rate Parameter: The customers’ rate of arrival.
* Customer Parameters
* Express Customer Percentage: What percentage of the customers will be express customers. (0-40%)
* Regular Customer Items: The minimum and maximum amount of items regular customers may have. (11-50)
* Express Customer Items: The minimum and maximum amount of items express customers may have. (1-10)
* Service Time Distribution: Choose between Negexp, Normal, and Uniform.
* Service Time Parameter: How long customers will spend at each service point.
* Shopping Time Multiplier: The multiplier for the time customers spend in the shopping area.
* Regular Checkout Multiplier: The multiplier for the amount of customers that will enter the regular checkout.
* Express Checkout Multiplier: The multiplier for the amount of customers that will enter the express checkout.
* Self Checkout Multiplier: The multiplier for the amount of customers that will enter the self-checkout.

Different configurations can be saved, loaded and deleted.

## Instructions

Explanation of the simulation controls:
* Simulation time: Configures how long the simulation will run for. The time is calculated by multiplying the delay with the simulation time. (For example: 1000 * 150ms = 150000ms = 150s)
* Delay (ms): Configures how long the simulation clock waits for before processing the next event.
* Start: Begins the simulation.
* Pause: Pauses the simulation.
* Resume: Resumes the simulation if paused.
* Reset: Resets the simulation.
* Slow: Increases the simulation delay by 20ms.
* Speed Up: Decreases the simulation delay by 20ms.

## Dependencies

The project dependencies are included in the pom.xml file, which can be installed using Maven.

* JavaFX: The project uses several JavaFX libraries, all of which are crucial for building the graphical user interface (GUI) and handling visualization. These libraries are:
  * javafx (version 20.0.1): The core JavaFX library, providing essential functionality for JavaFX applications.
  * javafx-graphics (version 20.0.1): Provides graphics support, enabling drawing and rendering within the JavaFX environment.
  * javafx-fxml (version 20.0.1): Used for defining the user interface using FXML, which allows for separating the layout from the application logic.
  * javafx-controls (version 20.0.1): Provides UI controls like buttons, text fields, labels, and other interactive components. 

* JUnit 5: The project uses JUnit 5 testing framework for testing some of the classes. The library used is:
  * junit-jupiter (version 5.8.1): Provides features for testing purposes such as annotations and assertion methods. 

* Mockito: One of the JUnit tests uses the Mockito framework for testing purposes. The library used is:
  * mockito (version 5.17.0): Provides features to create mock implementations of classes. 