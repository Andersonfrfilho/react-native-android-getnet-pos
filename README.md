# react-native-getnet-pos

react-native module to work getnet-pos by react-native module

## Installation

```sh
npm install react-native-getnet-pos
```

## Usage

```js
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

// ...

const result = await startingServices();
// returns always true

const connection = await checkConnections();
// returns a object { "connection": boolean }. 'true' for connected and 'false' for not connected.

const beeper = await beeperMethod('nfc'); // input type: 'ncf' | 'error' | 'digit' | 'success'
// returns a object { "beeper": boolean, "type": string }.

const leds = await ledMethod('all', true); // inputs color and turnOn:
// to color: 'all' | 'red' | 'green' | 'blue' | 'yellow'
// to turnOn: boolean
// returns a object { "color": string, "turn": boolean}.

const camera = await cameraMethod('back', 30); // inputs camera type and timeout:
// to cameraType: 'back' | 'front'
// to timeOut: a number
// returns a object { "message": string, "error": boolean}.

const startCard = await cardStartConnectAntenna('magnetic', 30); // inputs card type and timeout:
// to cardType: 'magnetic' | 'nfc' | 'chip' | 'all'
// to timeOut: a number
// on success returns a object { "pan": string,  "type": string, "track1": string, "track2": string, "track3": string, "dataExpired": string, "numberCard": number }
// on error returns a object { "message": string, "error": boolean}.

const stopCard = await cardStopConnectAntenna();
// returns a object { "stop": boolean}.

 const data = [
  {
    type: 'text',
    align: 'left',
    fontSize: 'small', // max 54 cacharacters until break line
    value: 'Printable text on the left with small size',
  },
  {
    type: 'text',
    align: 'left',
    fontSize: 'medium', // max 42 cacharacters until break line
    value: 'Printable text on the left with medium size',
  },
  {
    type: 'text',
    align: 'left',
    fontSize: 'large', // max 29 cacharacters until break line
    value: 'Printable text on the left with large size',
  },
  {
    type: 'text',
    align: 'right',
    fontSize: 'small', // max 54 cacharacters until break line
    value: 'Printable text on the right with small size',
  },
  {
    type: 'text',
    align: 'center',
    fontSize: 'small', // max 54 cacharacters until break line
    value: 'Printable text in the center with small size',
  },
  {
    type: 'image',
    align: 'center',
    fontSize: 'large',
    value: 'image',
  },
];
const printView = await printView(data); // an array with the following properties: type, align, fontSize and value.
// to type: 'image' | 'text'
// to align: 'center' | 'left' | 'right'
// to fontSize: 'large' | 'medium' | 'small'
// to value: a custom string
// returns arrays with formatted texts.

const data = [
  {
    weight: 5,
    type: 'text',
    align: 'left',
    fontSize: 'small', // max 54 cacharacters until break line
    value: 'Printable large text on the left with small size',
  },
  {
    weight: 10,
    type: 'text',
    align: 'center',
    fontSize: 'medium', // max 42 cacharacters until break line
    value: 'Printable text on the center',
  },
  {
    weight: 15,
    type: 'text',
    align: 'right',
    fontSize: 'large', // max 29 cacharacters until break line
    value: 'Printable text on the right',
  },
  {
    weight: 15,
    type: 'imagem',
    align: 'center',
    fontSize: '', // no relevance
    value: 'iiVBORw0...AP//GY5XuWK0Wk0AAAAASUVORK5CYII=',
  },
]
const printMethod = await printMethod(data); // an array with the following properties: weight, type,align, fontSize and value.
// to weight: a number
// to type: 'image' | 'barcode' | 'qrcode' | 'text'
// to align: 'center' | 'left' | 'right'
// to fontSize: 'large' | 'medium' | 'small'
// to value: a custom string
// returns a object { "printer": boolean, "message": string}.
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)

Step to use
- Install serviceposdigital-2.2.2-30-realses.apk
  - Option 1 Genymotion
    - Install Genymotion
    - Create Android device with 5.1 (Pixel 2)
    - Install Genymotion ARM Translator
      - [Download](https://github.com/m9rco/Genymotion_ARM_Translation/blob/master/package/Genymotion-ARM-Translation_for_5.1.zip)
      - Drag and drop into emulator or [Access-Link](https://stackoverflow.com/questions/58459833/unable-to-install-apk-in-android-emulator-app-contains-arm-native-code)
  - Option 2 Native Emulator
