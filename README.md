# compose-permissions
A Jetpack Compose permissions library using the Accompanist Permissions library. Fixes the revokedPermissions value being true on first permission request.

## Importing the library into your project

### settings.gradle file

```gradle
repositories {
  // When using build.gradle
  maven { url 'https://jitpack.io' } 
        
  // When using build.gradle.kts
  maven { setUrl("https://jitpack.io") }
}
```

### build.gradle file

```gradle
dependencies {
  implementation 'com.github.rajndev:compose-permissions:<latest-version>'
}
```

## Included Components

### Permission: A composable to handle permission requests

```kotlin
@Composable
fun Permission(
    showPermissionState: MutableState<Boolean>,
    permissions: List<String>,
    permissionNotGrantedContent: @Composable (MultiplePermissionsState, String) -> Unit,
    permissionPermanentlyDeniedContent: @Composable (String) -> Unit,
    permissionsGrantedContent: @Composable () -> Unit
)
```

## Contributions

Any and all contributions are welcome and appreciated. Please open a pull request when you are ready. Thank you.

## License

```
MIT License

Copyright (c) 2022 Raj Narayanan

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
