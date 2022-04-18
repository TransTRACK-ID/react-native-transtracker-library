# react-native-transtracker-library

TransTracker Library

## Installation

```sh
npm install react-native-transtracker-library
```

## Configuration
We recommend you to use the [react-native-permissions](https://www.npmjs.com/package/react-native-permissions) ( or any similliar ) library to request the permission.

- iOS permission you can request PERMISSIONS.IOS.LOCATION_ALWAYS
Info.plist
```
<key>NSLocationAlwaysUsageDescription</key>
<string>Needed to access location</string>

<key>UIBackgroundModes</key>
<array>
	<string>location</string>
	<string>processing</string>
</array>
```
Podfile
```Podfile
# ( Check react-native-permissions for setup)
permissions_path = '../node_modules/react-native-permissions/ios'
pod 'Permission-LocationAccuracy', :path => "#{permissions_path}LocationAccuracy"
pod 'Permission-LocationAlways', :path => "#{permissions_path}LocationAlways"

```

- Android permission you can request PERMISSIONS.ANDROID.ACCESS_FINE_LOCATION and PERMISSIONS.ANDROID.ACCESS_BACKGROUND_LOCATION
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

Please see the example folder for more detail.
## Usage

```js
/// Import service
import { trackerEmitter, initiateService, startService, stopService } from 'react-native-transtracker-library';

/// Received event from native ( if needed )
trackerEmitter.addListener('onLocationChanged', function (e) {
    setLocation(e);
});

/// Initiate service location before initiate try to check permission location
/// [apiKey] from TransTRACK
/// [externalId] is unique id to identify the account from telematics ( FMS ) web application
/// [trackerId] is used to identify the device ( IMEI or other ) it can be same as externalId
/// different IMEI, will be charged different fee for each device
initiateService(apiKey, externalId, trackerId);
```

## Support and feedback
- To renew the subscription, contact our sales team at contact@transtrack.id
- For any other info, reach our support or post and submit a feature request or a bug through our [Github issues](https://github.com/TransTRACK-ID/transtracker-library/issues).
- See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## About [TransTRACK.ID](https://www.transtrack.id)
We are here with the aim of providing the right solutions for the telematics world in Indonesia. Different & unique cultural wealth is the main key to our solution for Indonesia.
Since the beginning of 2019, we have been operating, we have always learned to provide solutions. In almost all regions in Indonesia our solution has been present and we are just starting this story.
