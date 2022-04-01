# react-native-transtracker-library

TransTracker Library

## Installation

```sh
npm install react-native-transtracker-library
```

## Configuration
We recommend you to use the react-native-permissions library to request the permission.

- iOS permission you can request PERMISSIONS.IOS.LOCATION_ALWAYS
- Android permission you can request PERMISSIONS.ANDROID.ACCESS_FINE_LOCATION and PERMISSIONS.ANDROID.ACCESS_BACKGROUND_LOCATION

Please see the example folder for more detail.
## Usage

```js
/// Import service
import { trackerEmitter, initiateService, startService, stopService } from 'react-native-transtracker-library';

// Received event from native
trackerEmitter.addListener('onLocationChanged', function (e) {
    setLocation(e);
});

/// Initiate service location before initiate try to check permission location
/// [apiKey] from TransTRACK
/// [externalId] payload from driver id
/// [trackerId] is used to identify the device ( IMEI or other )
/// different IMEI, will be charged different fee for each device
initiateService(apiKey, externalId, trackerId);
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
