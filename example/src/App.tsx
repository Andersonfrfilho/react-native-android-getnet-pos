import * as React from 'react';
import { StyleSheet, View, Text } from 'react-native';
import { startingServices, checkConnections } from 'react-native-getnet-pos';

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

  return (
    <View style={styles.container}>
      <Text>Connection: {connection?.toString()}</Text>
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
