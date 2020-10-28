# Ec-clem

This project is an [Icy](http://icy.bioimageanalysis.org/) plugin for point based image registration. The plugin implements rigid as well as non-rigid registrations.

## Install for development
+ Install `java` and [maven](https://maven.apache.org/install.html/)
+ Download Icy from [here](http://icy.bioimageanalysis.org/download/)
+ Create a shortcut to the icy directory under the `lib` folder : `lib/icy -> <your-icy-directory>` (NOTE: under Windows OS, use mklink under the prompt command as administrator, not shortcut)
+ Run `mvn clean` and `mvn install`
+ Copy the artifact `target/ec-clem-<version>.jar` to `lib/icy/plugins/perrine/easyclemv0`
+ Run icy and use `easyclemv0` plugin

## Code Organisation
+ Note that some injection of code is used (using Dagger) , so some code is generated while compiling with Maven. Do not edit any code in generated source directory.
+ The code is organized in packages and provide some unit tests during compilation for code integrity.


### Validation
the project cli-tools https://github.com/anrcrocoval/cli_tools/ provides tools for testing and validation the error and transform estimations.

### Documentation
Online tutorials are available on the icy webpage http://icy.bioimageanalysis.org/plugin/ec-clem/
Further tutorial including on error estimation are available here:
https://youtu.be/Rz1_MLqn6-k
