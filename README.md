### Requirements:
1. JDK 1.8
2. Internet connection to Maven Central and JCenter

### Run tests
1. `git clone` this repo, `cd` in the root directory.
2. macOS / Linux:
    ```
    ./gradlew clean test
    ```
    Windows (not tested but should work):
    ```
    gradlew.bat clean test
    ```
3. Open `<root directory>/build/reports/allure/index.html` in any modern browser.

### Notes
1. One test always fails. It is done on purpose to demonstrate failed test case in Allure report.
2. OAuth token may be expired/invalidated. Tests expect OAuth token for particular user which has special disk state. OAuth token for another user won't fix tests.
