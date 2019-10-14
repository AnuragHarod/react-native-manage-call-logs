# Package for managing call-logs in react-native (Android only)


## Installation:
`yarn add react-native-manage-call-logs`

or

`npm i -s react-native-manage-call-logs`
 

## Android Linking

#### Auto Linking (Version: >= 0.60.0)

if it doesn't work try...

`react-native link`
#### Manually Linking (Version: < 0.60.0)
* Add following to `android/settings.gradle`

```diff
+ include ':react-native-manage-call-logs'
+ project(':react-native-manage-call-logs').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-manage-call-logs/android')
```

* Add following to `android/app/build.gradle`

 ```diff
dependencies {
 + implementation project(':react-native-manage-call-logs')
 }
 ```

* Add following to `MainApplication.java` inside ( `android/app/src/main/java/...`)
```diff
+ import com.ahcodes.managecall.ManageCallLogPackage;

@Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
          new MainReactPackage(),
+         new ManageCallLogPackage()
      );
    }
```
### Add following permissions to `AndroidManifest.xml`
```diff
+ <uses-permission android:name="android.permission.READ_CALL_LOG" />
+ <uses-permission android:name="android.permission.WRITE_CALL_LOG" />

```

## Usage

```javascript
import { PermissionsAndroid } from 'react-native';
import ManageCallLogs from 'react-native-manage-call-logs'
 
 componentDidMount() {
    try {
    PermissionsAndroid.requestMultiple([
			PermissionsAndroid.PERMISSIONS.WRITE_CALL_LOG,
			PermissionsAndroid.PERMISSIONS.READ_CALL_LOG,
		]).then(result => {
			if (
				result['android.permission.WRITE_CALL_LOG'] &&
				result['android.permission.READ_CALL_LOG'] === 'granted'
			) {
                ManageCallLogs.getAll().then(data => {
                console.log(data)
			});
			} else {
				alert('permission denied')
			}
		});
    }
    catch (e) {
      console.log(e);
    }
   }
```

## Methods
Name       | Description
------------- | -------------
getAll()    | Get all records for call-logs.
get(`Number`)   | `Number: Integer` Get limited number of records for call-logs.  
removeAll  | Remove all call-logs record.
removeById(`id`)  | Remove the call-log record for the `id` passed in the argument.
removeByNumber(`String`)  |  Remove the call-log record for the `phone number' String passed in the argumnent


### Thanks
