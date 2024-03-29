import { NativeEventEmitter, NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-transtracker-library' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const TranstrackerLibrary = NativeModules.TranstrackerLibrary
  ? NativeModules.TranstrackerLibrary
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );


export const trackerEmitter = new NativeEventEmitter(TranstrackerLibrary);

export function initiateService(apiKey:string, externalId:string, trackerId: string): void {
  return TranstrackerLibrary.initiateService(apiKey, externalId, trackerId);
}

export function startService(error: Function): void {
  return TranstrackerLibrary.startService(error);
}

export function stopService(error: Function): void {
  return TranstrackerLibrary.stopService(error);
}

// export function getLatestLocation(error: Function, success: Function): void {
//   return TranstrackerLibrary.getLatestLocation(error, success);
// }

