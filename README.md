# react-native-transtracker-library

Tracker Library

Please see the example folder for more detail.

## Installation

```sh
npm install react-native-transtracker-library
```

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
