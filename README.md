# Sector
## An Android application for security monitoring and reporting.

Mobile platforms have become commonplace in the lives of millions of people in the United States and abroad. This technology is evolving so quickly that it is sometimes difficult to track the new trends and products as they become available to consumers. As a Cyber Security professional, I have been trying to keep up with these trends and the security implications that they create. I have found the security concerns of applications from the app store the most troubling and ambiguous so far. All Android users have seen the familiar, “Application X needs access to the following items:” and then a long list of resources listed below on the phone and I know I’m not alone when I ask, “What the heck for?” 

The goal of Sector is to flesh out what resources these applications are using in an attempt to shed some light on why they may need them. 
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

#### As a **user**, I want to **view the applications on my android phone** so I can **monitor them in Sector.**

Acceptance Criteria:

1. The application shows a list of the running applications on the phone.
2. The application provides the description of the application from the operating system.

As a **user**, I want to **view the resources allocated to the applications running on my Android phone** so I can **monitor them in Sector.**

Acceptance Criteria:

1. Each application in Sector can be selected.
2. The selected application will provide further details about what resources the application is using.

As a **user**, I want to **view the resources the applications originally requested** so I can **make comparisons with actual use in Sector. ** 

Acceptance Criteria:

1. Sector provides the original set of permissions associated with each application.
2. This information is accessible from that applications information page

As a **developer **, I want to **create a clean intuitive user interface** so I can **provide a polished product to the user that is easy to use.**

Acceptance Criteria:

1. The application is not intrusive and will have no pop ups
2. The user interface is “clutter free” and will only show the information that is necessary.
3. The application is modern and act as the user expects in most cases by adhering to best practices.

As a **developer**, I want  to **establish a set of best practices in Android Development** so I can **provide a polished product to the user.**

Acceptance Criteria:

1. A list of resources is created with links and snippets from the programming community.


### Use and Misuse case diagram
![alt tag](https://raw.githubusercontent.com/pstratman/sector/master/Use%20and%20Misuse%20-%20Page%201.jpeg)

## Resources Required
|Resource  | Dr. Hale needed? | Investigating Team member | Description |
|-------------------|---------|---------------------------|-------------|
| Jave 1.8 JDK | No | Paul | Need the dev kit for Java |
| Android Studio or other IDE | No | Paul | Need a good IDE for development |
| Test Platform | No | Paul | A tablet or phone I don't care about that I can deploy to |
| Inspiration and Teacherly Advice | Yes | Paul | In case I have questions about the project |




