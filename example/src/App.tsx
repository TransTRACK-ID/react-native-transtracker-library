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

  const apiKey = 'eyJpdiI6IkZkWnQ5UXMzZUwvTkxOTGcxbFVZekE9PSIsInZhbHVlIjoiN3UvUDdnK2gwUjlEZkVMNHFwNTQ3ZjRvMVBmclJGQTRJVUdQT2prZWllSDFtNHRnbTMzVFB2S3g1MzJIbnRaZCIsIm1hYyI6IjYwZWZkNDA4ZjkyOGI2ZmE5OGZmMDRlNDJmMWZmMDEwN2U3Zjg3NWI2NTc4OTg1ZGY4YjlhOWYxZjEyMjAzZDkiLCJ0YWciOiIifQ==';
  const externalId = 'testing1';
  const trackerId = 'testing1';

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

                    initiateService(apiKey, externalId, trackerId);
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
                    console.log('The permission ACCESS_FINE_LOCATION is granted');

                    /// Please Uncomment me

                    /// FOR API Below 29
                    // initiateService(apiKey, externalId, trackerId);
                    // setStatusLocationService('initiated');

                    /// FOR API >= 29
                    // request(PERMISSIONS.ANDROID.ACCESS_BACKGROUND_LOCATION).then((result) => {
                    //   switch (result) {
                    //     case RESULTS.LIMITED:
                    //       console.log('The permission is limited: some actions are possible');
                    //       break;
                    //     case RESULTS.GRANTED:
                    //       console.log('The permission ACCESS_BACKGROUND_LOCATION is granted');

                    //       initiateService(apiKey, externalId, trackerId);
                    //       setStatusLocationService('initiated');
                    //       break;
                    //   }
                    // });

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
