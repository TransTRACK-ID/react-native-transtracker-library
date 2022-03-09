# react-native-transtracker-library

Tracker Library

## Installation

```sh
npm install react-native-transtracker-library
```

## Usage

```js
// Import service
import { trackerEmitter, initiateService, startService, stopService } from 'react-native-transtracker-library';

// Received event from native
trackerEmitter.addListener('onLocationChanged', function (e) {
    setLocation(e);
});

// Initiate service location before initiate try to check permission location
// PARAM1: imei / code unique of the device
initiateService('code');
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
