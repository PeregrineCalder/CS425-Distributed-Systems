# CS425-Distributed-Systems - MP1 Distributed Log Querier

This project implements a distributed `grep` system using servers and clients on each machine. The client can issue `grep` commands to search logs across multiple servers, and the results are aggregated and returned to the client.

## Project Structure

- **Client**: The client sends `grep` commands to multiple servers, aggregates results, and displays matched lines and file counts.
- **Server**: Each server listens for incoming `grep` commands, executes them on its local log files, and returns the results to the client.
- **logFileGenerator**: This module generates log files with random contents for testing purposes.

## Prerequisites

- Java 17
- Maven 3.6 or higher

## How to Run

### 1. Set up the virtual machines

You must set up several virtual machines or servers and distribute the log files.

1. SSH into each VM and clone this repository:
    ```bash
    git clone https://github.com/PeregrineCalder/CS425-Distributed-Systems.git
   ```
2. Navigate to the project directory:
    ```bash
    cd CS425-Distributed-Systems/MP1
   ```
### 2. Grep Server

1. Navigate to the project directory:
    ```bash
    cd CS425-Distributed-Systems/MP1/Server
   ```

2. Build the project:
    ```bash
    mvn clean install
   ```

3. Start the Server on each VM by running:
   
   ```bash
   java -jar target/Server-1.0-SNAPSHOT-jar-with-dependencies.jar 50234
   ```

   The port number to which the server will listen must be specified in this project.
   We have unified the port number to `50234`,
   as defined in the configuration file `virtualMachineNetworkConfig.properties`.
   Therefore, it is recommended that port `50234` be used for all VMs.

   
   
   - If you want to run the server in the background:
   
      ```bash
      java -jar target/Server-1.0-SNAPSHOT-jar-with-dependencies.jar 50234 & 
      ```

### 3. Grep Client

1. Navigate to the project directory:
    ```bash
    cd CS425-Distributed-Systems/MP1/Client
   ```

2. Build the project:
    ```bash
    mvn clean install
   ```

3. Start the Client on each VM by running:
   ```bash
   java -jar target/Client-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

4. Input the `grep` command:
   ```bash
   grep ERROR
   ```
   **Important:**

   - When running the Client, there is no need to specify the file in the `grep` command, 
   as the file paths have already been set in the configuration file `virtualMachineNetworkConfig.properties`. 
   The Client will handle the `grep` command by appending the file path from the configuration file to the command, 
   ensuring that each VM `grep` a different file according to its configuration.
   - Enclosing the entire grep pattern in double quotes is recommended when running the grep command. This ensures that the pattern is treated as a whole. For example:
       ```bash
       grep "404 NOT FOUND"
       ```
   - When using the -e option for multiple pattern matching, it is recommended to separate each pattern with its own -e. Avoid combining patterns into a single one. For example:
       ```bash
       grep -e "404 Not Found" -e "Machine failed"
       ```
   - If you are using regex in your grep command, you must include the -E option to enable extended regex matching. For example:
       ```bash
       grep -E "\\d{4}-\\d{2}-\\d{2}"
       ```
     
### 4. Log File Generator

1. Navigate to the project directory:
    ```bash
    cd CS425-Distributed-Systems/MP1/logFileGenerator
    ```

2. Build the project:
    ```bash
    mvn clean install
    ```

3. Run the Log File Generator by specifying the machine ID:
    ```bash
    java -jar target/logFileGenerator-1.0-SNAPSHOT-jar-with-dependencies.jar 1
    ```
   This command will generate random log entries and save them to a file named `machine.<machineID>.log`.

## Unit Test

### 1. Overview of Unit Tests

- **ClientProcessorTest**:
   - Simulates a client sending `grep` commands to the server.
   - Validates that the client can correctly process responses from the server, count matched lines, and handle server connectivity issues.
   - Tests include:
      - `test_ClientProcessor`: Verifies that a valid `grep` command sent to the server returns the correct number of matched lines and files.
      - `test_ClientProcessorServerDown`: Simulates a scenario where the server is down and ensures that the client gracefully handles the error, without crashing.

- **GrepHandlerTest**: 
   - Verifies the functionality of the `GrepHandler`, which executes the `grep` command locally on the server and processes the output.
   - Tests include:
       - `test_GrepCommand`: Tests that the `GrepHandler` can execute a simple `grep` command and return results.
       - `test_GrepCommandWithQuotation`: Tests that a pattern wrapped in double quotes is correctly handled and returns expected results.
       - `test_GrepCommandNoMatch`: Tests cases where no lines match the `grep` command and ensures the correct result is returned.
       - `test_GrepCommandWithSpace`: Tests the handling of patterns with spaces.
       - `test_GrepCommandWithQuotationMarks`: Tests a `grep` command with patterns containing quotation marks.
       - `test_GrepCommandWithCountOption`: Tests the `-c` option that returns the count of matched lines.
       - `test_GrepCommandWitheOption`: Tests the `-e` option with multiple patterns.
       - `test_GrepCommandWithiOption`: Tests the `-i` option to ignore case distinctions.
       - `test_GrepCommandWithlOption`: Tests the `-l` option to only print the names of files with matching lines.
       - `test_GrepCommandWithLOption`: Tests the `-L` option to print the names of files that do not contain the pattern.
       - `test_GrepCommandWithQuietOption`: Tests the `-q` (quiet) option to ensure no output is returned.
       - `test_GrepCommandRegexWithOption`: Tests regex-based `grep` commands with the `-E` option.
       - `test_GrepCommandRegexWithoutOption`: Tests a regex `grep` command without explicitly specifying `-E`, ensuring the correct result is returned without regex handling errors.
     
- **logFileGeneratorTest**:
  - Tests the `logFileGenerator`, which generates log files for testing purposes.
  - Test includes:
      - `test_GenerateOutputFile`: Ensures that the `logFileGenerator` correctly creates a log file with the specified machine ID. It checks if the file exists after running the generator.


### 2. How to Run Unit Tests

Unit tests can be run using Maven’s testing framework. Maven’s `maven-surefire-plugin` is used to run the tests automatically.

To run the unit tests, execute the following command in the root directory of each module (e.g., `Server`, `Client`, `logFileGenerator`):

```bash
mvn test
```
The command will run all the tests, and the results will be displayed in the terminal. If there are any test failures, you can find more detailed logs in the target/surefire-reports directory.