import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-getnet-pos' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const GetnetPos = NativeModules.GetnetPos
  ? NativeModules.GetnetPos
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function startingServices(): Promise<boolean> {
  return GetnetPos.startingServices();
}

interface CheckConnectionsResult {
  connection: boolean;
}
export function checkConnections(): Promise<CheckConnectionsResult> {
  return GetnetPos.checkConnections();
}
