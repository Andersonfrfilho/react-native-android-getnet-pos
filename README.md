# react-native-getnet-pos

react-native module to work getnet-pos by react-native module

## Installation

```sh
npm install react-native-getnet-pos
```

## Usage

```js
import { startingServices, checkConnections, beeperMethod, ledMethod } from 'react-native-getnet-pos';

// ...

const result = await startingServices();
// returns always true

const connection = await checkConnections();
// returns a object { "connection": boolean }. 'true' for connected and 'false' for not connected.

const beeper = await beeperMethod("nfc"); //input type: "ncf" | "error" | "digit" | "success"
// returns a object { "beeper": boolean, "type": string }.

const leds = await ledMethod("all", true); //inputs color and turn on: "all" | "red" | "green" | "blue" | "yellow"; 'true' or 'false'
// returns a object { "color": string, "turn": boolean}.

const leds = await cameraMethod("back", 30); //inputs camera type and timeout: "back" | "front"; interger.
// returns a object { "message": string, "error": boolean}.
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
