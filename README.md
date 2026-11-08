# java-webdriver-cucumber

Base framework for Java Webdriver automation with Cucumber and Page Object

# java-webdriver-cucumber-pom

## Running tests

The project requires Java 17+. The runner script selects JDK 17 automatically on
macOS, downloads the latest ChromeDriver and the exactly matching Chrome for
Testing build, and runs the suite headless:

```bash
./run-tests.sh                  # all features
./run-tests.sh @ups             # only @ups scenarios
./run-tests.sh "@ups or @usps"  # a Cucumber tag expression
```

Useful options:

```bash
HEADLESS=false ./run-tests.sh @ups       # watch the run in a visible browser
SKIP_DRIVER_UPDATE=1 ./run-tests.sh      # reuse the current driver/browser
CHROME_BINARY=/path/to/chrome ./run-tests.sh   # use a specific Chrome binary
```

The downloaded Chrome for Testing build is cached in
`~/.cache/chrome-for-testing`, and its path is passed to the tests via
`~/.cache/chrome-for-testing/current-binary.txt`.
