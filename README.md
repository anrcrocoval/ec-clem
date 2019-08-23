# Ec-clem

This project is an [Icy](http://icy.bioimageanalysis.org/) plugin for point based image registration. The plugin implements rigid as well as non-rigid registrations.

## Install for development
+ Install `java` and [maven](https://maven.apache.org/install.html/) 
+ Download Icy from [here](http://icy.bioimageanalysis.org/download/)
+ Create a shortcut to the icy directory under the `libs` folder : `libs/icy -> <your-icy-directory>`
+ Run `mvn clean` and `mvn install`
+ Copy the artifact `target/ec-clem-<version>.jar` to `libs/icy/plugins/perrine/easyclemv0`
+ Run icy and use `easyclemv0` plugin
