import * as React from 'react';
import { StyleSheet, View, Text, Button } from 'react-native';
import {
  ledMethod,
  beeperMethod,
  cameraMethod,
  startingServices,
  checkConnections,
} from 'react-native-getnet-pos';

export default function App() {
  const [connection, setConnection] = React.useState<boolean | undefined>();

  React.useEffect(() => {
    startingServices().then((response) => {
      console.log(response);
    });
    checkConnections().then((response) => {
      setConnection(response.connection);
    });
  }, []);

  function Beep() {
    if (connection) {
      beeperMethod('nfc').then((response) => {
        console.log(response);
      });
    }
  }
  function LEDs() {
    if (connection) {
      ledMethod('all', true).then((response) => {
        console.log(response);
      });
      setTimeout(() => {
        ledMethod('all', false).then((response) => {
          console.log(response);
        });
      }, 1000);
    }
  }
  function Camera() {
    if (connection) {
      cameraMethod('back', 30).then((response) => {
        console.log(response);
      });
    }
  }

  return (
    <View style={styles.container}>
      <Text>Connection: {connection?.toString()}</Text>
      <Button title="Click to Beep" onPress={Beep} />
      <Button title="Click to Turn On/Off LEDs" onPress={LEDs} />
      <Button title="Click to Open Rear Camera" onPress={Camera} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    gap: 10,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
