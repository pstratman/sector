**Table of Contents**  *generated with [DocToc](http://doctoc.herokuapp.com/)*

- [Sector](#)
	- [An Android application for security monitoring and reporting.](#)
			- [Sector will:](#)
	- [Project Timeline](#)
	- [Risk List](#)
	- [Application Requirements](#)
		- [Important User Stories](#)
			- [As a Sector user, I want to view the applications on my android phone so I can monitor them.](#)
		- [Use and Misuse case diagram](#)
	- [Resources Required](#)

# Sector
## An Android application for security monitoring and reporting.

Mobile platforms have become commonplace in the lives of millions of people in the United States and abroad. This technology is evolving so quickly that it is sometimes difficult to track the new trends and products as they become available to consumers. In particular, the permissions requests from the applications on the app store are troubling and ambiguous in their generality. All Android users have seen the familiar, “Application X needs access to the following items:” and then a long list of resources listed below. The goal of Sector is to address the question "Why does Application X need access to that?"

Sector will address this question by fleshing out what resources these applications are using in an attempt to shed some light on why they may need them. 
#### Sector will:
* Show a list of the applications on the phone
* Give details about what resources the applications are currently using
* Show details about what the application requested when it was installed on the device
* And, provide a good looking/intuitive interface for the user

Sector is meant to be a monitoring application and will never be intrusive or bug you with popup notifications and reminders to check back in with it. The merit in Sector is its ability to bring you up to speed about what your technology is doing behind the scenes and give you some comfort about the applications that you are downloading from the app store.

## Project Timeline
![alt tag](https://raw.githubusercontent.com/pstratman/sector/master/GANTT.JPG)

## Risk List
|Risk name (value)  | Impact     | Likelihood | Description |
|-------------------|------------|------------|-------------|
| Lack of Experience (48) | 8 | 6 | No experience with Android developement  |
| Lack of Time (30) | 6 | 5 | Other classes, work and personal things getting in the way |
| Loss of Work (28) | 7 | 4 | Loosing my work here at home or in the cloud |
| Lack of Tools (24) | 8 | 3 | I don't know how many tools are out there for developing Android applications |
| Technical Problems (6) | 3 | 2 | Computer problems here are home causing delay |

## Application Requirements

### Important User Stories

#### As a **Sector user**, I want to **view the applications on my android phone** so I can **monitor them.**

Acceptance Criteria:

1. The application shows a list of the running applications on the phone.
2. The application provides the description of the application from the operating system.

As a **Sector user**, I want to **view the resources allocated to the applications running on my Android phone** so I can **monitor them.**

Acceptance Criteria:

1. Each application in Sector can be selected.
2. The selected application will provide further details about what resources the application is using.

As a **Sector user**, I want to **view the resources the applications originally requested** so I can **make comparisons with actual use. ** 

Acceptance Criteria:

1. Sector provides the original set of permissions associated with each application.
2. This information is accessible from that applications information page

As a **Sector user**, I need to be able to **click on a list view item** so I can **view detailed information about an application**

Acceptance Criteria:

1. The application has an onClick event listener that launches another activity.
2. The new activity displays meta-data about an application.

As a **Sector user**, I need to be able to **refreash the list of applications** so I can **view the most up-to-date information about each application.**

Acceptance Criteria:

1. Sector provides a method to refresh the information on the screen.
2. This information is line with the current program patterns.
3. The information is accurate and effecient.


### Use and Misuse case diagram
![alt tag](https://raw.githubusercontent.com/pstratman/sector/master/Use%20and%20Misuse%20-%20Page%201.jpeg)

## Resources Required
|Resource  | Dr. Hale needed? | Investigating Team member | Description |
|-------------------|---------|---------------------------|-------------|
| Jave 1.8 JDK | No | Paul | Need the dev kit for Java |
| Android Studio or other IDE | No | Paul | Need a good IDE for development |
| Test Platform | No | Paul | A tablet or phone I don't care about that I can deploy to |
| Inspiration and Teacherly Advice | Yes | Paul | In case I have questions about the project |




