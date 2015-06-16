AndroidSDK
==========
* Installation
 * Android Studio (recommended)
 * Eclipse
* Usage
 * Set up
 * Send messages
 * Receive and susbcrive to channels
* Orion specific usage

#Installation <a name="installation"></a>

##Android Studio installation

In your build.graddle add bastly SDK as a dependency

    compile 'com.bastly:bastlysdk:1.0.3'

Your build.graddle should look something like this:

```java
     apply plugin: 'com.android.application'
   
     android {
     compileSdkVersion 22
     buildToolsVersion "21.1.2"
   
     defaultConfig {
        applicationId "com.mgl.testsdk"
        minSdkVersion 19
        targetSdkVersion 22
        versionCode 1
        versionName "1.0"
     }
     buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
     }
     }
     dependencies {
     compile fileTree(dir: 'libs', include: ['*.jar'])  
     compile 'com.android.support:appcompat-v7:22.1.1'  
     compile 'com.bastly:bastlysdk:1.0.5'   
     }  
```

Bastly needs internet connection so don't forget the INTERNET PERMISSION on your manifest!
```java
    <uses-permission android:name="android.permission.INTERNET" />
```
##Eclipse installation

Download the jar sources from our [bintray repo](https://bintray.com/bastly/releases/AndroidSDK/view) and add them to your libs folder.


#Usage <a name="usage"></a>

##Set up

Declare a bastly field in your activity and set it up on your onCreate method:
```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bastly = new Bastly(FROM, APIKEY, MESSAGECALLBACK, MODEL);
```
Bastly takes some parameters being:

* FROM  
 id for this connection/user, a String, identifies this specific connection/user on the system.  
* APIKEY  
 your apikey, you can find it in your profile at dashboard.bastly.com
* MESSAGECALLBACK  
 a messageListener implementation, usually define your activity that implements this interface and you will get the callbacks methods where your messages will be received.  
* MODEL  
 your java class that represents the messages you are sending and receiving, its a class that you generate to facilitate sending and receiving information. Ex: a Play class with spell, strength, material fields representing a play from a player.  
 
An example usage could be something like this.

***MainActivity.java***
```java
    public class MainActivity extends ActionBarActivity implements MessageListener<Play> {  
    private Bastly<Play> bastly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bastly = new Bastly("USER1234", "DEMOKEY", this, Play.class);
    }
    
    @Override
    public void onMessageReceived(String channel, Play receivedPlay) {
       Log.d("SAMPLE", "spell received" + receivedPlay.getSpell());
    }
```
Example of a message class, you should create this class as a holder of the information that you want to send and receive.
Its important that your class implements the ***serializable*** interface.

***Play.java***
```java
public class Play implements Serializable {  
    private String spell;
    private String material;
    private int strength;
    
    public Play(String spell, String material, int strength) {
        this.spell = spell;
        this.material = material;
        this.strength = strength;
    }
    
    public String getSpell() {
        return spell;
    }
    
    public String getMaterial() {
        return material;
    }
    
    public int getStrength() {
        return strength;
    }
}  
```
##Send messages

To send message to a channel or user just call bastly.send

Send params:

* TO  
 Channel you want to send the message to. 
* OBJECT  
 The object instance you want to send 

```java
bastly.send("destinationChannel", new Play("fireball", "fire", 8));
```   
##Subscribe to channels

If you want to subscribe to a new channel you just have to call

```java
bastly.registerChannel("newChannel");
```

and you will get the updates on your defined callback.

#Orion specific usage <a name="orion"></a>

If you want to receive messages from Orion you just have to implement OrionListener as a new interface on your Activity.

```java
public class MainActivity extends ActionBarActivity implements MessageListener<Play>, OrionListener {
``` 

This will force a new callback method on your activity where you will receive your Orion updates.

```java
@Override
public void onOrionMessageReceived(String channel, Orion message) {
    Log.d(TAG, "update from Orion received");
    for (Attributes attribute : message.getAttributes()) {
        // read your updated message here
    }
}
```

Android SDK for Bastly
