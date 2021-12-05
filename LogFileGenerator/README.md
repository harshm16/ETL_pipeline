# Log generator

This repository is a fork of: [0x1DOCD00D/LogFileGenerator](https://github.com/0x1DOCD00D/LogFileGenerator/tree/homework3)
Please refer the original [README.md](https://github.com/0x1DOCD00D/LogFileGenerator/blob/homework3/README.md) for the functional details of this repository

Changes are made to make this repository deployable as a web application over AWS Beanstalk.

## Build and run configuration
Clone and `zip` the repository with all contents in the root directory of the `.zip` file  [read more]( https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/applications-sourcebundle.html). Sample command to create a `zip` file:<br/>
`zip ./LogGenerator.zip -r * .[^.]* -x ".git/*" "project/target/*" "target/*" ".idea"`<br/>

Archive with default configuration for the source code has been provided in `src/main/resources/LogGenerator.zip`<br/>
This project can be build and  run locally using:
```console
sbt compile #builds the project
sbt run #run the project
```
Requirements: JDK >= 1.8, sbt >= 1.5


### Application configuration
1. The application requires an EFS volume to be created in the AWS account. It is in the root of this volume the application logs are written. User has to mention the id for this volume `.ebextensions/efs-mount.config`
   ```yaml
   option_settings:
   aws:elasticbeanstalk:application:environment:
   FILE_SYSTEM_ID: 'fs-0xxxxxxxxx' # your volume id
   MOUNT_DIRECTORY: '/efs'
   ```
2. Tweak the parameters in `src/main/resources/application.conf` to modify the  runtime of the application and frequency of log messages. With the default configuration, this application generates logs for 30 minutes and generates next message with 1 second frequency.
3. The application runs few scripts over the EC2 instance before the code gets deployed. These scripts are to mount the EFS volume and install scala. These implicitly assume the underlying Linux instance to be Linux-based OS.

### Deploying to AWS Elastic Beanstalk (EBS) 
1. Before we deploy our application, we create a storage where the LogGenerator will be writing the log files. This storage is an EFS (elastic file storage) volume. This volume can be mounted and accessed across multiple EC2 instances.
2. Go to EFS console [here](https://us-east-2.console.aws.amazon.com/efs/) and create an EFS with default settings.
3. Go to EBS in AWS console [here](https://us-east-2.console.aws.amazon.com/elasticbeanstalk/home). Make sure that the EBS and EFS are in the same region.
4. Create a Web app:
   1. Write an application name
   2. Choose Java platform with Corretto 8 platform branch
   3. Select upload your code and upload the `zip` archive of the source code
   4. Click Configure more options
5. Configure more options:
   1. Edit instances for any custom security rule needed. You might want to add inbound routes for `ssh` into the efs or viewing logs for debugging. Toggle checkbox to enable IMDSv1.
   2. Edit capacity to specify the number of instances. Edit the instance type to `t2.small`. 
   3. Edit security to add your public/private key-pair.
6. Deploy the zip using 

### Youtube video [link](https://youtu.be/O3BAjCCJt5k)

## Troubleshooting
1. Default `powershell` command for archiving makes the archive incompatible with Beanstalk. Follow this to test if archiving has been done correctly [here](https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/applications-sourcebundle.html#using-features.deployment.source.test)
2. If the application fails to deploy the failure logs  can be fetched via  ssh to the EC2 instance via  AWS CLI. The logs are located at `/var/log/`

