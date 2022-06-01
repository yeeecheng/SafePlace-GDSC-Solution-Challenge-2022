# 2022-SafePlace-SolutionChallenge

## About
### Motivation

Although traffic in Taiwan is already well-developed, walking from stations to homes is still quite distant and dangerous. Some roads are desolate and lack streetlights. We students are often anxious about walking home alone. There are many reports of schoolgirls being attacked and raped on their way to school or women being abducted and raped. We also conducted a survey among friends about whether it is worrying walking home alone at night. Most of them agree it is quite dangerous. Therefore, we design this app, **“Safe Place”**, to help users walking on the street more safely and securely. 

***

### We try to slove in SDGs...
* #####  United Nations' Sustainable Development Goal 3: Good Health and Well-being
 ###### &emsp;&emsp; Ensure healthy lives and promote well-being for all at all ages
 
* #####  United Nations' Sustainable Development Goal 5: Gender Equality
 ###### &emsp;&emsp; Eliminate all forms of violence against all women and girls in the public and private spheres, including trafficking and sexual and other types of exploitation 

* #####  United Nations' Sustainable Development Goal 11: Sustainable Citys and Communitues
###### &emsp;&emsp; By 2030, provide access to safe, affordable, accessible and sustainable transport systems for all, improving road safety, notably by expanding public transport,
###### &emsp;&emsp; with special attention to the needs of those in vulnerable situations, women, children, persons with disabilities and older persons.” 

***

### Safe Place is a service for


##### 1. Demonstrating opening shops on the map which helps user plan a safer path to their destination.
##### 2. Regularly update new store information in the app.
##### 3. Upload new store information.
##### 4. Report wrong store information.
##### 5. Navigating to the nearest shop and update user’s location and direction.
##### 6. Calling to user’s emergency contact and send message with location to ask for help.
##### 7. Send a hyperlink to user’s emergency contact to show user’s instant location for the next 30 minutes when user danger.
##### 8. Playing the loudly siren to ask for help from those around user.

<br/>

## Demo Video 
#### (Click it will take you to Demo video of our APP.)
<a href="https://www.youtube.com/watch?v=4DOmRT_W_Do&t=18s"><img src="https://i.imgur.com/JCnoj3q.jpg"  width=70%/></a>

<br/>

## How to use


<img src=https://github.com/yeeecheng/SafePlace-GDSC-Solution-Challenge-2022/blob/main/README_Assets/nearest_opening_shop.png  width=70% />
<img src=https://github.com/yeeecheng/SafePlace-GDSC-Solution-Challenge-2022/blob/main/README_Assets/navigation_button.png  width=70% />
<img src=https://github.com/yeeecheng/SafePlace-GDSC-Solution-Challenge-2022/blob/main/README_Assets/switch_direction.png  width=70% />
<img src=https://github.com/yeeecheng/SafePlace-GDSC-Solution-Challenge-2022/blob/main/README_Assets/siren_button.png  width=70% />
<img src=https://github.com/yeeecheng/SafePlace-GDSC-Solution-Challenge-2022/blob/main/README_Assets/phone_button.png  width=70% />
<img src=https://github.com/yeeecheng/SafePlace-GDSC-Solution-Challenge-2022/blob/main/README_Assets/contact_location.png  width=70% />
<img src=https://github.com/yeeecheng/SafePlace-GDSC-Solution-Challenge-2022/blob/main/README_Assets/add_new_Store.png  width=70% />
<img src=https://github.com/yeeecheng/SafePlace-GDSC-Solution-Challenge-2022/blob/main/README_Assets/report_wrong_store.png  width=70% />

<br/>



## IDE and Platform for Project

<img src=https://github.com/yeeecheng/SafePlace-GDSC-Solution-Challenge-2022/blob/main/README_Assets/platform_used.png width=70% />
                                                            

|  Products        |Functions           | 
| ------------- |:-------------|
| Android        | We build our project with Android Studio.      | 
| Kotlin         | This is our main development language.       |  
| Google Map Platform      | We use the Maps SDK and Google Direction API to display the information on the map, as shown in *Figure 1*.      |
| Google Cloud Platform    |  We use Google Compute Engine to set up Server ,as shown in *Figure 2*.      |

<hr>
<img src=https://github.com/yeeecheng/SafePlace-GDSC-Solution-Challenge-2022/blob/main/README_Assets/Google_Map_platform.png width=70% />

##### Figure 1.
#### In its Architecture, we use Map SDK to present maps and Direction API to plan a route for navigation. 
#### Then, the route will be painted by Maps SDK. 

<hr>
<img src=https://github.com/yeeecheng/SafePlace-GDSC-Solution-Challenge-2022/blob/main/README_Assets/upload_local_data.png width=70% />

##### Figure 2.
#### Server is activated by Computer Engine for updating, revising app’s data and uploading,  
#### acquiring user’s location. 

<br/>


## Execution method

##### 1. You have to make sure your Android version is 9.0
   * ##### Because of the google API restriction, this has to be run on the Android 9.0 or above Android 9.0.

##### 2. You need to apply a MAP API KEY for Google Map API and Google Direction API
  * #####  Following the steps of the https://developers.google.com/maps/documentation/android-sdk/get-api-key?hl=zh-tw to get your own MAP API KEY.
  * #####  Then put your MAP_API_KEY in the position in the image below.
  <img src=https://i.imgur.com/ZzHaNab.jpeg width=50% />

  
   
##### 3. Please ensure that the network connection and GPS are continuously turned on during use
   * ##### Internet and GPS are required as our project will send request to Google API to get information .

##### 4.  Please do not run on the emulator.
   * ##### Since our project uses GPS to plan a route and mark user's position, and make phone calls to ask for help, it does not work in virtual devices.

   * ##### Thus, you have to connect it with real device.

<br/>

##  Contributors
| YiCheng Liao | YuYi Chuang | YuYi Chu | PinYu Li |
| :-----|:-----|:-----|:-----|
