import * as React from 'react';

import {
  StyleSheet,
  View,
  Button,
  Text,
  PermissionsAndroid,
  DeviceEventEmitter,
} from 'react-native';

import { initiateService, startService, stopService } from 'react-native-transtracker-library';

const grantPermission = async () => {
  try {
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
      {
        title: 'Location Permission',
        message: 'We needs access to your location',
        buttonNeutral: 'Ask Me Later',
        buttonNegative: 'Cancel',
        buttonPositive: 'OK',
      }
    );
    if (granted === PermissionsAndroid.RESULTS.GRANTED) {
      console.log('You can use the location');
      return true;
    } else {
      console.log('Location permission denied');
    }
  } catch (err) {
    console.warn(err);
  }

  return false;
};

export default function App() {
  const [statusLocationService, setStatusLocationService] = React.useState<string>('not initiated');
  const [location, setLocation] = React.useState();

  React.useEffect(() => {
    DeviceEventEmitter.addListener('onLocationChanged', function (e) {
      console.log(e);
      setLocation(e);
    });
  }, []);

  return (
    <View style={styles.container}>
      <Button
        title={statusLocationService}
        onPress={async () => {
          if (statusLocationService === 'not initiated') {
            var isGranted = await grantPermission();
            if(isGranted) {
              initiateService('zein');
              setStatusLocationService('initiated');
            }
          } else if (statusLocationService === 'initiated') {
            startService(() => {});
            setStatusLocationService('started');
          } else if (statusLocationService === 'started') {
            stopService(() => {});
            setStatusLocationService('stopped');
          } else if (statusLocationService === 'stopped') {
            startService(() => {});
            setStatusLocationService('started');
          }
        }}
      />

      {location != null && (
        <Text>
          {`Latitude: ${location['latitude']}\nLongitude:  ${location['longitude']}\nSpeed: ${location['speed']}`}
        </Text>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
