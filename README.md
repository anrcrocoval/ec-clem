# Ec-clem

This project is an [Icy](http://icy.bioimageanalysis.org/) plugin for point based image registration. The plugin implements rigid as well as non-rigid registrations.




## Install for Beta-test
Ec-clem should be installed from ICY directly http://icy.bioimageanalysis.org/plugin/ec-clem/ 
HOWEVER here you will find the dev version, not released yet under ICY plugin repository.
To test a dev version not released yet, download the .jar under binary https://github.com/anrcrocoval/ec-clem/raw/master/binary/ec_clem-2.0.1-SNAPSHOT.jar and copy it under 
`<your-icy-directory>/plugins/perrine/easyclemv0` 

### Documentation
Online tutorials are available on the icy webpage http://icy.bioimageanalysis.org/plugin/ec-clem/
Further tutorials are available here:
+  on error estimation at 95% ofconfidence
https://youtu.be/Rz1_MLqn6-k
+ on creating a cascaded transform
https://youtu.be/5yNuP8hQuJM
+ on applying a transformation such as the one created by cascaded transforms
https://youtu.be/ZVB1mUZXGA4
+ to visualize both registered images without any interpolation or resampling of the data
https://github.com/anrcrocoval/CorrelativeView

## Install for development
+ Install `java` and [maven](https://maven.apache.org/install.html)
+ Download Icy from [here](http://icy.bioimageanalysis.org/download/)
+ [STEP TO BE KEPT until tv denoising and filterToolbox are deposited by ICY team on their MAVEN repository] Create a shortcut to the icy directory under the `lib` folder : `lib/icy -> <your-icy-directory>` (NOTE: under Windows OS, use mklink under the prompt command as administrator, not shortcut)
+ Run `mvn` in a command line (if you use ELCIPSE IDE you can import the project as an existing MAVEN project , 
further details on http://icy.bioimageanalysis.org/developer/setting-icy-development-environment/ and Building section of http://icy.bioimageanalysis.org/developer/create-a-new-icy-plugin/. (use Maven Install)
+ There is a  `main` class in EasyClemv0.java, which is useless but allow you to run it directly as a standalone application for testing (on Eclipse, right click on the class and Debug as or run as .. a stand alone application)

### Code Organisation
+ Note that some injection of code is used (using Dagger) , so some code is generated while compiling with Maven. Do not edit any code in generated source directory. Ignore all errors related to dagger Component in your IDE.
+ The code is organized in packages and provide some unit tests during compilation for code integrity.
+ Some functional unit tests are run everytime the code is built, you can add (purpose is to check the integrity of functions)

### Validation
the project cli-tools https://github.com/anrcrocoval/cli_tools/ provides tools for testing and validation the error and transform estimations.


