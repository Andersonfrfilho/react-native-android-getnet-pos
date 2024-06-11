import * as React from 'react';
import { StyleSheet, View, Text, Button } from 'react-native';
import {
  ledMethod,
  printView,
  printMethod,
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
      const data = [
        {
          type: 'text',
          align: 'left',
          fontSize: 'small', //max 54 cacharacters
          value: 'Printable text on the left with small size',
        },
        {
          type: 'text',
          align: 'left',
          fontSize: 'medium', //max 42 cacharacters
          value: 'Printable text on the left with medium size',
        },
        {
          type: 'text',
          align: 'left',
          fontSize: 'large', //max 29 cacharacters
          value: 'Printable text on the left with large size',
        },
        {
          type: 'text',
          align: 'right',
          fontSize: 'small', //max 54 cacharacters
          value: 'Printable text on the right with small size',
        },
        {
          type: 'text',
          align: 'center',
          fontSize: 'small', //max 54 cacharacters
          value: 'Printable text in the center with small size',
        },
        {
          type: 'image',
          align: 'center',
          fontSize: 'large',
          value: 'Image not yet printable',
        },
      ];
      printView(data).then((result) => {
        console.log(result);
      });
    }
  }
  function PrintMethod() {
    if (connection) {
      const data = [
        {
          weight: 5,
          type: 'text',
          align: 'left',
          fontSize: 'small',
          value: 'Printable large text on the left with small size',
        },
        {
          weight: 10,
          type: 'text',
          align: 'center',
          fontSize: 'medium',
          value: 'Printable text on the center',
        },
        {
          weight: 15,
          type: 'text',
          align: 'right',
          fontSize: 'large',
          value: 'Printable text on the right',
        },
        {
          weight: 15,
          type: 'barcode',
          align: 'center',
          fontSize: '',
          value: '1234567890',
        },
        {
          weight: 15,
          type: 'qrcode',
          align: 'center',
          fontSize: '',
          value: 'Printable qrcode on the center',
        },
        {
          weight: 15,
          type: 'image',
          align: 'center',
          fontSize: '',
          value:
            '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',
        },
      ];
      printMethod(data).then((result) => {
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
      <Button title="Click to Print" onPress={PrintMethod} />
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
