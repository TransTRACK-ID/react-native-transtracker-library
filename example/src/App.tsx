import * as React from 'react';

import {
  StyleSheet,
  View,
  Button,
  Text,
  TextInput,
  Platform,
} from 'react-native';

import AsyncStorage from '@react-native-async-storage/async-storage';

import {request, RESULTS, PERMISSIONS} from 'react-native-permissions';
import { trackerEmitter, initiateService, startService, stopService } from 'react-native-transtracker-library';

export default function App() {
  const [statusLocationService, setStatusLocationService] = React.useState<string>('not initiated');
  const [location, setLocation] = React.useState();
  const [trackerId, setTrackerId] = React.useState('example');

  const apiKey = 'eyJpdiI6Im9WbEpmT2ZlVlU2bldVQWEvZ3c1Z1E9PSIsInZhbHVlIjoidUZ0Q1BINXp6czBvZ2s3WjliM05DbzVtWWZOcDFuam81M2FJcytTTitIQTJEdzRnMTdHTVRZYnhJMElpM3RQTSIsIm1hYyI6ImQyMTQxZGY4MDgyNTRhMzYzZGI4MjdmZDMxYzI0NWQ1OTUwZTE1MTAxOGRmYzdkYzAzODE2MTIyYWZmYWJmZTMiLCJ0YWciOiIifQ==';

  React.useEffect(() => {
    trackerEmitter.addListener('onLocationChanged', function (e) {
      console.log(e);
      setLocation(e);
    });

    // get access to the tracker id
    AsyncStorage.getItem('TRACKERID', (err, result) => {
      if (result) {
        setTrackerId(result);
      }
    });
  }, []);

  return (
    <View style={styles.container}>
      <TextInput
        style={{ height: 40, borderColor: 'gray', borderWidth: 1 }}
        value={trackerId}
        onChangeText={text => {
          try {
            setTrackerId(text)

            AsyncStorage.setItem(
              'TRACKERID',
              text,
            );
          } catch (error) {
            console.log(error);
          }
        }}
        placeholder="Set Tracker ID"
      />
      <View style={{padding:10}}></View>
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

                    initiateService(apiKey, trackerId, trackerId);
                    let state = 'initiated';
                    setStatusLocationService(state);
                    try {
                      AsyncStorage.setItem(
                        'STATE',
                        state,
                      );
                    } catch (error) {
                      console.log(error);
                    }
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
                    initiateService(apiKey, trackerId, trackerId);
                    let state = 'initiated';
                    setStatusLocationService(state);

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
            let state = 'started';
            setStatusLocationService(state);
          } else if (statusLocationService === 'started') {
            stopService((err: any, data: any) => {
              console.log(err, data);
            });
            let state = 'stoped';
            setStatusLocationService(state);
          } else if (statusLocationService === 'stoped') {
            startService((err: any, data: any) => {
              console.log(err, data);
            });
            let state = 'started';
            setStatusLocationService(state);
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
