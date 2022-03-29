import * as React from 'react';

import {
  StyleSheet,
  View,
  Button,
  Text,
  Platform
} from 'react-native';

import {request, RESULTS, PERMISSIONS} from 'react-native-permissions';
import { trackerEmitter, initiateService, startService, stopService } from 'react-native-transtracker-library';

export default function App() {
  const [statusLocationService, setStatusLocationService] = React.useState<string>('not initiated');
  const [location, setLocation] = React.useState();
  
  const randomId = Math.floor(Math.random() * 1000000);
  const apiKey = 'eyJpdiI6Imd1UTA0WXJPOG4zZVZxY21HNEVIWEE9PSIsInZhbHVlIjoiSHQrZFVIZllmV3I0QmhLMjJBZzlaSXVhazJoSkdaaUNoaUV5YmNCQktJRkt0Ui9WUHdlZHpKa2paQTVpWmZmMSIsIm1hYyI6ImVkNTM0N2E1NDc2YzI2ZWI5NjU3ZjNjOGZmYjA2ZDI3OTVkYmU4NzA3NDRhMjdjYzU2ZGViNjBlMjY3ZTFiOWIiLCJ0YWciOiIifQ==';
  const externalId = 'user_id' + randomId;
  const imei = 'test' + randomId;

  React.useEffect(() => {
    trackerEmitter.addListener('onLocationChanged', function (e) {
      console.log(e);
      setLocation(e);
    });
  }, []);

  return (
    <View style={styles.container}>
      <Button
        title={statusLocationService}
        onPress={ () => {
          if (statusLocationService === 'not initiated') {
            if(Platform.OS === 'ios') {
              request(PERMISSIONS.IOS.LOCATION_ALWAYS).then((result) => {
                switch (result) {
                  case RESULTS.LIMITED:
                    console.log('The permission is limited: some actions are possible');
                    break;
                  case RESULTS.GRANTED:
                    console.log('The permission is granted');

                    initiateService(apiKey, externalId, imei);
                    setStatusLocationService('initiated');
                    break;
                }
              });
            }

            else if(Platform.OS === 'android') {
              request(PERMISSIONS.ANDROID.ACCESS_FINE_LOCATION).then((result) => {
                switch (result) {
                  case RESULTS.LIMITED:
                    console.log('The permission is limited: some actions are possible');
                    break;
                  case RESULTS.GRANTED:
                    console.log('The permission is granted');

                    initiateService(apiKey, externalId, imei);
                    setStatusLocationService('initiated');
                    break;
                }
              });
            }
          } else if (statusLocationService === 'initiated') {
            startService((err: any, data: any) => {
              console.log(err, data);
            });
            setStatusLocationService('started');
          } else if (statusLocationService === 'started') {
            stopService((err: any, data: any) => {
              console.log(err, data);
            });
            setStatusLocationService('stopped');
          } else if (statusLocationService === 'stopped') {
            startService((err: any, data: any) => {
              console.log(err, data);
            });
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
