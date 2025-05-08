# Shop Checkout Simulator
Group 5: Phong Le, Casper Kapiainen, Leevi Tiihonen, Tino Vuorela


Java program that simulates store checkout queues in realtime. The simulator interface was built using JavaFX.

## Features
### Visualisation
Realtime animation of customers going through service points.
* Entrance: This is where the customers enter the store. Each customer is given a unique ID and item amount.
* Shopping area: Customers move from the entrance to the shopping area where they will spend most of their time, after which they will move on to one of the three checkouts.
* Self-checkout: 30% of regular customers exit through this checkout.
* Regular checkout: 70 % of regular customers exit through this checkout.
* Express checkout: Express customers exit through this checkout.

### Statistics

### Configuration

## Dependencies
* JavaFX: The project uses several JavaFX libraries, all of which are crucial for building the graphical user interface (GUI) and handling visualization. These libraries are:
  * javafx (version 20.0.1): The core JavaFX library, providing essential functionality for JavaFX applications.
  * javafx-graphics (version 20.0.1): Provides graphics support, enabling drawing and rendering within the JavaFX environment.
  * javafx-fxml (version 20.0.1): Used for defining the user interface using FXML, which allows for separating the layout from the application logic.
  * javafx-controls (version 20.0.1): Provides UI controls like buttons, text fields, labels, and other interactive components. 

* JUnit 5: The project uses JUnit 5 testing framework for testing some of the classes. The library used is:
  * junit-jupiter (version 5.8.1): Provides features for testing purposes such as annotations and assertion methods. 