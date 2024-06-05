import * as React from 'react';
import { StyleSheet, View, Text, Button } from 'react-native';
import {
  printView,
  ledMethod,
  beeperMethod,
  cameraMethod,
  startingServices,
  checkConnections,
  cardStopConnectAntenna,
  cardStartConnectAntenna,
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
  function CardMagnetic() {
    if (connection) {
      cardStartConnectAntenna('magnetic', 30).then((response) => {
        console.log(response);
      });
    }
  }
  function CardStop() {
    if (connection) {
      cardStopConnectAntenna().then((response) => {
        console.log(response);
      });
    }
  }
  function LogPrintMethod() {
    if (connection) {
      const data = {
        textPrint: [
          {
            position: 'left',
            fontSize: 'small',
            text: 'Printable text on the left with small size',
          },
          {
            position: 'left',
            fontSize: 'medium',
            text: 'Printable text on the left with medium size',
          },
          {
            position: 'left',
            fontSize: 'large',
            text: 'Printable text on the left with large size',
          },
          {
            position: 'right',
            fontSize: 'small',
            text: 'Printable text on the right with small size',
          },
          {
            position: 'center',
            fontSize: 'small',
            text: 'Printable text in the center with small size',
          },
          {
            position: 'bitmap',
            fontSize: 'large',
            text: 'bitmap text',
          },
        ],
      };
      printView(data).then((result) => {
        console.log(result);
      });
    }
  }

  return (
    <View style={styles.container}>
      <Text>Connection: {connection?.toString()}</Text>
      <Button title="Click to Beep" onPress={Beep} />
      <Button title="Click to Turn On/Off LEDs" onPress={LEDs} />
      <Button title="Click to Open Rear Camera" onPress={Camera} />
      <Button title="Click to Test Card - Magnetic" onPress={CardMagnetic} />
      <Button title="Click to Stop Card" onPress={CardStop} />
      <Button title="Click to Log Print Method" onPress={LogPrintMethod} />
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
