AndroidSDK
==========
* [Installation](#installation)
 * Android Studio (recommended)
 * Eclipse
* [Usage](#usage)
 * Set up
 * Send messages
 * Receive and susbcrive to channels
* [Orion specific usage](#orion)

#Installation <a name="installation"></a>

##Android Studio installation

In your build.graddle add bastly SDK as a dependency

    compile 'com.bastly:bastlysdk:1.0.5'

Your build.graddle should look something like this:


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

##Eclipse installation

Download the jar sources from our [bintray repo](https://bintray.com/bastly/releases/AndroidSDK/view) and add them to your libs folder.


#Usage <a name="usage"></a>

##Set up

Declare a bastly field in your activity and set it up on your onCreate method:

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bastly = new Bastly(FROM, APIKEY, MESSAGECALLBACK, MODEL);

Bastly takes some parameters being:

* FROM  
 current userId, identifies this specific user in the system.  
* APIKEY  
 your apikey, you can find it in your profile in the bastly website.  
* MESSAGECALLBACK  
 a messageListener implementation, usually define your activity that implements this interface and you will get the callbacks methods where your messages will be received.  
* MODEL  
 your java class that represents the messages you are sending and receiving, its a class that you generate to facilitate sending and receiving information. Ex: a Play class with spell, strength, material fields representing a play from a player.  
 
An example usage could be something like this.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bastly = new Bastly("USER1234", "DEMOKEY", this, Play.class);
  
Activity class sample implementing the messageListener interface and method overriding message callback

***MainActivity.java***

    public class MainActivity extends ActionBarActivity implements MessageListener<Play> {  
    private Bastly<Play> bastly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bastly = new Bastly("USER1234", "DEMOKEY", this, Play.class);
    }
    
    @Override
    public void onMessageReceived(String channel, Play message) {
       Log.d("SAMPLE", "spell received" + message.getSpell());
    }

Example of a message class, you should create this class as a holder of the information that you want to send and receive.
Its important that your class implements the ***serializable*** interface.

***Play.java***

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

##Send messages

To send message to a channel or user just call bastly.send

   

Android SDK for Bastly
