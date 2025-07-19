# **Contributing to ServerPulse**

First off, thank you for considering contributing to ServerPulse! It's people like you that make ServerPulse such a great tool. We welcome any and all contributions.  
Following these guidelines helps to communicate that you respect the time of the developers managing and developing this open source project. In return, they should reciprocate that respect in addressing your issue, assessing changes, and helping you finalize your pull requests.

## **Code of Conduct**

We have a [Code of Conduct](https://github.com/renvins/serverpulse/blob/master/CODE_OF_CONDUCT.md) that we expect all contributors to adhere to. Please read it before contributing.

## **How Can I Contribute?**

There are many ways to contribute to ServerPulse, from writing code and documentation to reporting bugs and suggesting new features.

### **Reporting Bugs**

If you find a bug, please open an issue on our [GitHub issue tracker](https://github.com/renvins/serverpulse/issues).  
When you are creating a bug report, please include as many details as possible. Fill out the required template, the information it asks for helps us resolve issues faster.

### **Suggesting Enhancements**

If you have an idea for a new feature or an enhancement to an existing one, please open an issue on our [GitHub issue tracker](https://www.google.com/search?q=https://github.com/Renvins/ServerPulse/issues). This allows us to discuss the idea and its implementation before you start working on it.

### **Your First Code Contribution**

Unsure where to begin contributing to ServerPulse? You can start by looking through good-first-issue and help-wanted issues:

* [Good First Issues](https://www.google.com/search?q=https://github.com/Renvins/ServerPulse/issues?q%3Dis%253Aissue%2Bis%253Aopen%2Blabel%253A%2522good%2Bfirst%2Bissue%2522) \- issues which should only require a few lines of code, and a test or two.  
* [Help Wanted Issues](https://www.google.com/search?q=https://github.com/Renvins/ServerPulse/issues?q%3Dis%253Aissue%2Bis%253Aopen%2Blabel%253A%2522help%2Bwanted%2522) \- issues which should be a bit more involved than good-first-issue issues.

### **Pull Requests**

We welcome pull requests for bug fixes, new features, and improvements.

1. **Fork the repository** and create your branch from main.  
2. **Set up your development environment**. ServerPulse is a Gradle project. You can import it into your favorite IDE like IntelliJ IDEA or Eclipse.  
3. **Make your changes**. Please follow the coding style of the project.  
4. **Do tests** for your changes. Make sure they work properly.
5. **Commit your changes** with a clear and descriptive commit message.  
6. **Push your changes** to your fork.  
7. **Open a pull request** to the main branch of the ServerPulse repository.

## **Development Setup**

ServerPulse is a multi-module Gradle project. The main modules are:

* **api**: Contains the API for the plugin.  
* **common**: Contains the common code used by all platforms.  
* **bukkit**: Implementation for the Bukkit platform.  
* **bungeecord**: Implementation for the BungeeCord platform.  
* **fabric**: Implementation for the Fabric platform.  
* **velocity**: Implementation for the Velocity platform.

To build the project, you will need:

* Java 21 or higher  
* Git

To build the project, run the following command from the root of the repository:  
./gradlew build

The compiled JAR files will be located in the build/libs directory of each module.

## **Coding Style**

We don't have a strict coding style guide, but please try to follow the existing style of the project.

## **Final Words**

Thank you for your interest in contributing to ServerPulse. We're excited to see what you'll bring to the project\!
