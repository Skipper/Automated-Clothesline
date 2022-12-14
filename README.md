## Description

Project developed with **Raspberry Pi 3** in **C, C++** programming language with **I2C** communication protocol for sensor control and data collection projected on LCD screen.

Application developed in Android Studio with programming language **Kotlin** and **REST API**, implementing Material Design standards; that allows the user to manage the information from the sensors that is stored in a **ThingsBoard IoT platform.**

The solution is made up of a series of sensors/actuators connected to a **Raspberry Pi**, which collect data from the environment in which the clothesline is located. The collection of that data is sent for **EDGE** processing in **Thingsboard**, which determines what actions the actuators will take on the clothesline. In addition, a Mobile Application is implemented through which the user can view the conditions of the clothesline in real time, as well as execute actions on their own.

It connects via **MQTT** to send data and via **REST API** to receive data from **Thingsboard.**

Devices used:

**To capture parameters:**

* Color sensor (TCS34725)
* Temperature and humidity sensor (SI7021 or AM2320)
* Color sensor to capture brightness (TCS3472)
* Precipitation sensor
* RGB Led
* Button

**In addition, the following actuators were connected:**

* DC motor (Direct Current) 
* 16x2 RGB backlit LCD display (JHD1313/PCA9633)

For more details, go to the specific project folder: images/readme.md

## Authors

**Alejandro Martinez**

* [LinkedIn](https://www.linkedin.com/in/diego-alejandro-martinez-espinosa-571086134)

**Francisco Lupi**

* [LinkedIn](https://www.linkedin.com/in/francisco-martin-lupi)

## Screenshots 

<img src="images/Design.PNG" width="468" height="350" /> <img src="images/Structure.png" width="468" height="350" /> 

<img src="images/Assemble.jpg" width="468" height="350" /> <img src="images/Dashboard.PNG" width="468" height="350" />

<img src="images/Mobile.png" width="350" height="468" /> 

## Installation

This project requires to be installed on a **Raspberry Pi 3** and to use the aforementioned **Sensors** with their respective connections.

This project requires to be installed on **Android mobile devices.** 

| Sdk      | Version      |
| :------- | :----------- |
| `min`    | **26**       |
| `target` | **31**       |

## Feedback

If you have any feedback, please reach out to us at dreamstime@outlook.es
