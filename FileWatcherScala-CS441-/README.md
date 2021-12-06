# Real-time file monitoring

This repository monitors and extracts the real-time file changes in the observed directory and publishes them to a Kafka topic.

## Build and run configuration
1. Requirements: JDK >= 1.8, sbt >= 1.5, Unix-based OS
2. To run the project: `sbt run path/to/dir` where `path/to/dir` is the observed directory.
3. Run test suite: `sbt test` should output
```console
[info] Run completed in 1 second, 212 milliseconds.
[info] Total number of tests run: 4
[info] Suites: completed 3, aborted 0
[info] Tests: succeeded 4, failed 0, canceled 0, ignored 0, pending 0
[info] All tests passed.
```
4. To run the code without publishing to a Kafka topic, checkout `wo-kafka` branch.

## Project Flow
1. Driver code spawns 2 connected actors: `Watcher` & `Extractor` for each file in the directory specified as a runtime argument
2. `WatcherService` monitors the number of lines in the `Watcher` associated file and notifies the next actor in chain, `Extractor` if the number of lines change.
3. `ExtractorService` extracts the lines within a given range and publishes as a stream to a Kafka topic.
4. `WatcherService` and `ExtractorService` execute Unix-based shell commands to perform their tasks

## Deploying to AWS EC2
1. Create a default EC2 instance with required security group from the AWS console [here]()
2. `ssh` into the EC2 instance using `aws-cli`. Make sure you have added your system's IP address to the inbound route of the security group.
3. To run clone and run this repo you will need to install:
   1. JDK 8:
   ```console
        sudo amazon-linux-extras enable corretto8
        sudo yum install java-1.8.0-amazon-corretto-devel
   ```
   2. git:
   `sudo yum install git`
   3. sbt:
   ```console
    curl -L https://www.scala-sbt.org/sbt-rpm.repo > sbt-rpm.repo
    sudo mv sbt-rpm.repo /etc/yum.repos.d/
    sudo yum -y install sbt
   ```
   4. Clone this repo and to the project root directory and execute `sbt run path/to/dir`

### Youtube video: [link](https://youtu.be/TDNmTYhNl1E)

## Troubleshooting
1. Make sure the directory path passed as runtime argument exists
2. This path is provided as a relative path
3. This project isn't compatible with non-Unix based OS