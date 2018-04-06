# KodYaC

A platform to securely and easily perform KYC for yoru app to smooth the onboarding process of potential users.

## Getting Started

Simply clone the repository:

```
git clone https://github.com/ashiswin/Kodyac.git
```

and launch it in Android Studio.

### Prerequisites

This project was built using Android Studio 3.0.1. Ensure that you have at least the same version of Android Studio to prevent any errors.
Missing SDK versions will be automatically downloaded by Android Studio upon build.

## Running the tests

**System Test**
android System testing was conducted using Espresso. System Testing for our android app consists of our app's UI (checking if buttons are enabled/ not enabled, if instructions and error messages shown are correct). <br />
Only BasicInfoTest and BlinkIDAccuracy requires user interaction and a physcial device for test to be successful. <br />
All Tests run the app by launching company 52's app link. This company contains all KYC methods our team has and none of the methods are completed. The test can run with another company app link by changing the Rules of the Tests.  <br />
<br />
1) Overall UI Test <br />
Within a single session, it checks the buttons and display of all the activities (that do not interact with the phone's hardware) in our app. <br />
2) Individual Method Test <br />
Tests the UI of 1 KYC method on each run of the test. Checks UI of each KYC method.<br />
3) SMSVerificationNumberActivityTest <br />
Tests the UI of SMSVerificationNumberActivity. It checks that: editText only accepts 8 characters/numbers, send button is only enabled after 8 characters/numbers have been entered, correct error messages appear on erronous input and alert dialog constructed can be dismissed. <br />
4) BasicInfo Test & 5) BlinkIDAccuracy <br />
These test the accuracy of ZXing Barcode scanner and BlinkID API in reading a barcode/NRIC. These test must be run on a physical phone. Enter the relevant/personal details at the top of the test. Run the test on your physcial phone. Once the Barcode scanner/ Blink ID scanner is initiated, scan your NRIC. The test checks if the details extracted from the NRIC corresppond to what you have entered.  <br />

**Unit Test**
1) UtilTest
Tests prettyDate function in Util class. Checks if function returns the correct date in proper format and throws an error on invalid input format/date.

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

## Deployment

Simply connect your phone to your computer and launch it in Android Studio. Ensure you approve ADB to run on your phone.

## Future Development

Photo Verification includes OCR (client must take a selfie with unique code). <br />
Liviness detection in video verification so video cannot be spoofed with multiple photos. <br />
Video Verification requires client to rotate head in z-axis, and ult imately constructs a 3D model of client's head. This makes it harder for clients to spoof our KYC method. <br />
Enable BaiscInforVerification for anybody with a SingPass account. <br />


## Authors

* **Cheria Widayanto (1002246)** - [cwidz](https://github.com/cwidz)
* **Isaac Ashwin (1002151)** - [ashiswin](https://github.com/ashiswin)
* **Lim Jing Yun (1002261)** - [limJingYun](https://github.com/limJingYun)
    BlinkID Api implementation <br />
    Google Mobile Vision implementation (Video Verification & taking screen shot from video)<br />
    Google phone library implementation - Check phone number and throw error<br />
    Android unit and system testing<br />
    User Testing<br />
    API (FaceAPI,GetMyInfo) endpoints implementation <br />
    Coming up with content for project meeting reports<br />
* **Shobhit (1002315)** - [OmegAshEnr01n](https://github.com/OmegAshEnr01n)
