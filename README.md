# SafePlace-GDSC-Solution-Challenge-2022

## About
### Motivation..

Although traffic in Taiwan is already well-developed, walking from stations to homes is still quite distant and dangerous. Some roads are desolate and lack streetlights. We students are often anxious about walking home alone. There are many reports of schoolgirls being attacked and raped on their way to school or women being abducted and raped. We also conducted a survey among friends about whether it is worrying walking home alone at night. Most of them agree it is quite dangerous. Therefore, we design this app, **“Safe Place”**, to help users walking on the street more safely and securely. 

***

### We try to slove in SDGs...
* #####  *Goal 3*: Good Health and Well-being.
   - ###### Ensure healthy lives and promote well-being for all at all ages.
 
* #####  Goal *5*: Gender Equality.
   - ###### Eliminate all forms of violence against all women and girls in the public and private spheres, including trafficking and sexual and other types of exploitation.

* #####  *Goal 11*: Sustainable Citys and Communitues.
   - ###### By 2030, provide access to safe, affordable, accessible and sustainable transport systems for all, improving road safety, notably by expanding public transport, with special attention to the needs of those in vulnerable situations, women, children, persons with disabilities and older persons.

***

### Safe Place is a service for...


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
#### (Click it will take you to Demo video .)
<a href="https://www.youtube.com/watch?v=M1qu2-gCb5k"><img src="https://github.com/yeeecheng/SafePlace-GDSC-Solution-Challenge-2022/blob/main/README_Assets/demo_video.png"  width=70%/></a>

<br/>

## Introduce the function...

<img src=https://github.com/yeeecheng/SafePlace-GDSC-Solution-Challenge-2022/blob/main/README_Assets/nearest_opening_shop.png  width=70% alt="nearest opening shop "/>
<img src=https://github.com/yeeecheng/SafePlace-GDSC-Solution-Challenge-2022/blob/main/README_Assets/navigation_button.png  width=70% "navigation button"/>
<img src=https://github.com/yeeecheng/SafePlace-GDSC-Solution-Challenge-2022/blob/main/README_Assets/switch_direction.png  width=70% "switch direction"/>
<img src=https://github.com/yeeecheng/SafePlace-GDSC-Solution-Challenge-2022/blob/main/README_Assets/siren_button.png  width=70% "siren button"/>
<img src=https://github.com/yeeecheng/SafePlace-GDSC-Solution-Challenge-2022/blob/main/README_Assets/phone_button.png  width=70% "phone button"/>
<img src=https://github.com/yeeecheng/SafePlace-GDSC-Solution-Challenge-2022/blob/main/README_Assets/contact_location.png  width=70% "contact button"/>
<img src=https://github.com/yeeecheng/SafePlace-GDSC-Solution-Challenge-2022/blob/main/README_Assets/add_new_Store.png  width=70% "add new store"/>
<img src=https://github.com/yeeecheng/SafePlace-GDSC-Solution-Challenge-2022/blob/main/README_Assets/report_wrong_store.png  width=70% "report wrong store"/>

<br/>



## IDE and Platform for Project

<img src=https://github.com/yeeecheng/SafePlace-GDSC-Solution-Challenge-2022/blob/main/README_Assets/platform_used.png width=70% alt="platform we used" />
                                                            

|  Products        |Functions           | 
| ------------- |:-------------|
| Android        | We build our project with Android Studio.      | 
| Kotlin         | This is our main development language.       |  
| Google Map Platform      | We use the Maps SDK and Google Direction API to display the information on the map, as shown in *Figure 1*.      |
| Google Cloud Platform    |  We use Google Compute Engine to set up Server ,as shown in *Figure 2*.      |

<hr>
<img src=https://github.com/yeeecheng/SafePlace-GDSC-Solution-Challenge-2022/blob/main/README_Assets/Google_Map_platform.png width=70% alt="Google Map platform"/>

##### *Figure 1.*
#### In its Architecture, we use Map SDK to present maps and Direction API to plan a route for navigation. 
#### Then, the route will be painted by Maps SDK. 

<hr>
<img src=https://github.com/yeeecheng/SafePlace-GDSC-Solution-Challenge-2022/blob/main/README_Assets/upload_local_data.png width=70%  alt="upload local data"/>

##### *Figure 2.*
#### Server is activated by Computer Engine for updating, revising app’s data and uploading,  
#### acquiring user’s location. 

<br/>


## How to use our SafePlace...

#### 1. Android Studio Setup:
 - #####  Following the steps of the https://developer.android.com/studio to download the Android Studio.

#### 2. Downlaod our project and open it with Anrdroid Studio.
 - ##### SafePlace-GDSC-Solution-Challenge-2022 -> Code ->Download ZIP.
 <img src=https://github.com/yeeecheng/SafePlace-GDSC-Solution-Challenge-2022/blob/main/README_Assets/download_project.png width=50% />
 
 - ##### File -> New -> New Project -> "find our project and open it " .
 <img src=https://github.com/yeeecheng/SafePlace-GDSC-Solution-Challenge-2022/blob/main/README_Assets/open_project.png width=50% />
 
#### 3. You need to apply a MAP API KEY for Google Map API and Google Direction API
   - #####  Following the steps of the https://developers.google.com/maps/documentation/android-sdk/get-api-key?hl=zh-tw to get your own MAP API KEY.
   - #####  Then put your MAP_API_KEY in the position in the image below.
  <img src=https://github.com/yeeecheng/SafePlace-GDSC-Solution-Challenge-2022/blob/main/README_Assets/set_map_api.png width=50% />

#### 4. You need to have a Phone with Android System and make sure your Android version is 9.0 .
  - ##### Because of the google API restriction, this has to be run on the Android 9.0 or above Android 9.0.

#### 5. Connect the phone to the computer and confirm that your phone version is displayed on android studio.
  - ##### You can follow the steps of the https://developer.android.com/studio/run/device.
  - <img src=https://github.com/yeeecheng/SafePlace-GDSC-Solution-Challenge-2022/blob/main/README_Assets/Set_phone.png width=50% />

#### 6. Finally , execute and experience SafePlace!

<br/>

## Notice...

  
##### 1. Please ensure that the network connection and GPS are continuously turned on during use
   * ##### Internet and GPS are required as our project will send request to Google API to get information .

##### 2. Please use SafePlace in Taiwan .
   * ##### Because all our data processing is set based on Taiwan, if it is used in other countries, it may not be able to use all functions or even bugs.

##### 3.  Please do not run on the emulator.
   * ##### Since our project uses GPS to plan a route and mark user's position, and make phone calls to ask for help, it does not work in virtual devices.

   * ##### Thus, you have to connect it with real device.

<br/>

##  Contributors
| YiCheng Liao | YuYi Chuang | YuYi Chu | PinYu Li |
| :-----|:-----|:-----|:-----|
