[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0.html)

# Telegram Example

A telegram client for android created using tdlib library and built with [Jetpack Compose](https://developer.android.com/jetpack/compose).

To try out this app, you need to: 
* Install the latest **Canary** of Android Studio
* Download the **tdlib** android library from [https://core.telegram.org/tdlib/tdlib.zip](https://core.telegram.org/tdlib/tdlib.zip) and extract the zip file to the root folder of the project
* Obtain application identifier hash for Telegram API access at [https://my.telegram.org](https://my.telegram.org) and store them in the android resources. For example in values/api_keys.xml:
```
<resources>
    <integer name="telegram_api_id">your integer api id</integer>
    <string name="telegram_api_hash">your string api hash</string>
</resources>
```

This app is **work in progress**. Features implemented so far:
- [x] Login
- [x] Show chat list
- [ ] Show chat messages
- [ ] Send messages
- [ ] ...

## License
    Copyright (c) 2020 Indrit Bashkimi

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
