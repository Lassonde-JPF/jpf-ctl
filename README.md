[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![GNU License][license-shield]][license-url]

<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/Lassonde-JPF/jpf-ctl">
    <img src="resources/jpf-ctl-logo.png" alt="Logo" width="150" height="120">
  </a>

  <h3 align="center">jpf-ctl</h3>

  <p align="center">
    CTL model checking of Java code
    <br />
    <a href=""><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="">View Demo</a>
    ·
    <a href="https://github.com/Lassonde-JPF/jpf-ctl-extension/issues">Report Bug</a>
    ·
    <a href="https://github.com/Lassonde-JPF/jpf-ctl-extension/issues">Request Feature</a>
  </p>
</p>



<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgements">Acknowledgements</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About jpf-ctl

The most common approach to finding errors in software is testing. However, testing is of limited use when checking *concurrent software* for errors. Hence, other techniques, including *model checking*, have been developed to supplement testing. We focus on Java as Java being one of the most widely used programming language. Presently, the most popular tool for model checking of Java code, [*Java PathFinder*](https://github.com/javapathfinder) (JPF), does not support the checking of properties expressed in temporal logics; instead, it checks properties such as deadlocks and data races. To resolve this, we present jpf-ctl, an extension of JPF which supports the checking of properties specified in *computational tree logic* (CTL). 

Let us consider a simple example. The class [Account](code/jpf-ctl/src/main/java/example/Account.java) models a bank account.  The classes [Deposit](code/jpf-ctl/src/main/java/example/Deposit.java) and [Withdraw](code/jpf-ctl/src/main/java/example/Withdraw.java) are threads that deposit to and withdraw from a bank account.  The class [Main](code/jpf-ctl/src/main/java/example/Main.java) is an app that creates a single bank account and multiple threads that deposit to and withdraw from that bank account.  It takes three command line arguments:
- the initial balance of the bank account,
- the number of Deposit threads that do one deposit transaction to the bank account, and
- the number of Withdraw threads that do one withdrawal transaction to the back account.

The withdraw method of the Account class
```java
public boolean withdraw(double amount) {
  if (amount < 0) {
    throw new IllegalArgumentException("A negative amount cannot be withdrawn");
  }
  if (this.balance >= amount) {
    this.balance -= amount;
    return true;
  } else {
    return false;
  }
}
```
attempts to ensure that the balance never becomes negative. However, as the method is not synchronized, the balance may become negative. The Main class contains a static boolean field named negative which captures whether the balance of the account is negative. This field is regularly updated in the app.

The JUnit test [AccountTest](code/jpf-ctl/src/main/java/example/AccountTest.java), which performs one million runs of the app with an initial balance of one, two Deposit threads, and two Withdraw threads, does *not* detect a negative balance. The property that the balance never becomes negative can be captured by the CTL formula AG !example.negative. This formula specifies that for every run of the Java code (*A* stands for "for all"), and for every state of such a run (*G* stands for "globally"), the static boolean field example.negative is false (! represents negation). As will be shown in the demo below, jpf-ctl is *successful* in determining that the CTL formula does not hold for this Java app, that is, the balance can become negative, and jpf-ctl will provide a counterexample that shows why the CTL formula does not hold.

### Limitations

Since jpf-ctl relies on jpf-core, the core of JPF, and jpf-core currently supports Java 8, only apps that use Java 8 constructs can be checked. Furthermore, jpf-core can only handle apps, that is, it needs a main method of a class as its starting point. Currently, jpf-ctl only supports static boolean fields as the basic building blocks of CTL formulas.

## Installation instructions for using jpf-ctl

### Java

Use [Java](https://www.oracle.com/ca-en/java/technologies/javase/javase8-archive-downloads.html)'s version 8 (we have successfully used 1.8.0_251, 1.8.0_281, and 1.8.0_301).  To check which version of Java (if any) is currently in use, issue the following command.
```
> java -version
java version "1.8.0_251"
Java(TM) SE Runtime Environment (build 1.8.0_251-b08)
Java HotSpot(TM) 64-Bit Server VM (build 25.251-b08, mixed mode)
```

### Git

To check if any version of [Git](https://git-scm.com/downloads) is currently in use, issue the following command.
```
> git --version
git version 2.26.2.windows.1
```

### Java PathFinder (jpf-core)

Install [jpf-core](https://github.com/javapathfinder/jpf-core) following the instructions on the [jpf-core wiki](https://github.com/javapathfinder/jpf-core/wiki/How-to-install-JPF). We recommend cloning the master branch using Git and building JPF with the Gradle wrapper. Note that some tests may fail. This should not prevent you from using JPF.

### jpf-label

1. Clone [jpf-label](https://github.com/javapathfinder/jpf-label) using Git: go the directory where you want to put jpf-label and issue the following command.
```
> git clone https://github.com/javapathfinder/jpf-label.git
Cloning into 'jpf-label'...
remote: Enumerating objects: 153, done.
remote: Counting objects: 100% (153/153), done.
remote: Compressing objects: 100% (84/84), done.
Receiving objects:  75% (115/153)sed 141 (delta 58), pack-reused 0
Receiving objects: 100% (153/153), 107.48 KiB | 1.28 MiB/s, done.
Resolving deltas: 100% (67/67), done.
```
2. Build jpf-label with the Gradle wrapper: inside the jpf-label directory, issue the following command.
```
> .\gradlew

> Task :compileJava
Note: C:\Users\montreal\Downloads\jpf-label\src\main\label\StateLabel.java uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.

BUILD SUCCESSFUL in 47s
4 actionable tasks: 4 executed
```
3. Add
```
jpf-label=<path to jpf-label's directory>
```
to JPF's site.properties file.

### jpf-ctl

1. Clone jpf-ctl using Git: go the directory where you want to put jpf-ctl and issue the following command.

```
> git clone https://github.com/Lassonde-JPF/jpf-ctl.git
Cloning into 'jpf-ctl'...
remote: Enumerating objects: 3159, done.
remote: Counting objects: 100% (1816/1816), done.
remote: Compressing objects: 100% (1061/1061), done.
remote: Total 3159 (delta 875), reused 1548 (delta 639), pack-reused 1343
Receiving objects: 100% (3159/3159), 14.39 MiB | 1.17 MiB/s, done.
Resolving deltas: 100% (1491/1491), done.
```
2. Build jpf-ctl with the Gradle wrapper: inside the jpf-ctl directory, issue the following command.
```
> .\gradlew


```
## Installation instructions for developing jpf-ctl



**Using Direct Source Code:**
1. Clone the repository
2. Navigate to the build directory
3. Run `gradlew build`
4. Navigate to the `build/libs` directory
5. Open a command window here
6. Run jpf-ctl using `java -jar jpf-ctl-all.jar`

**Through downloading a release**
1. Download the latest version of jpf-ctl 
2. Place the downloaded jar in some location on your machine
3. Run jpf-ctl using `java -jar jpf-ctl-all.jar`

#### Development
The following prerequisites are required to develop with jpf-ctl and are required _in addition to_ the normal usage requirements defined above

* **[ANTLR4 (4.9.2)](https://github.com/antlr/antlr4/blob/master/doc/getting-started.md)** <br/>
  Used for developing/defining the grammar of CTL which jpf-ctl uses as properties to model check
* **[jpf-nhandler](https://github.com/javapathfinder/jpf-nhandler)** <br/>


```
> git clone https://github.com/javapathfinder/jpf-nhandler.git
Cloning into 'jpf-nhandler'...
remote: Enumerating objects: 1882, done.
remote: Counting objects: 100% (1882/1882), done.
remote: Compressing objects: 100% (581/581), done.
remote: Total 1882 (delta 1122), reused 1882 (delta 1122), pack-reused 0
Receiving objects: 100% (1882/1882), 6.84 MiB | 1.18 MiB/s, done.
Resolving deltas: 100% (1122/1122), done.
```



  Used for testing the jpf-partial-transition-system-listener, specifically, to setup testing environments outside of JPF

### Installation

<< installation instructions >>

## Documentation

Documentation for the jpf-ctl project can be found within the `/documentation` directory; however, the PDF version can be directly accessed using the following 
[link](/documentation/report.pdf)

<!-- USAGE EXAMPLES -->
## Usage

<< how to use >>

<!-- ROADMAP -->
## Roadmap

See the [open issues](https://github.com/Lassonde-JPF/jpf-ctl-extension/issues) for a list of proposed features (and known issues).

<!-- CONTRIBUTING -->
## Contributing

If you wish to contribute to jpf-ctl by addressing some of the [open issues](https://github.com/Lassonde-JPF/jpf-ctl-extension/issues) or suggesting new features, please note that we do not have time to review minor changes such as variable renames, spelling mistakes, etc. If you would wish to point these out, please create an issue rather than a pull request.

1. Fork ctl-jpf 
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<!-- LICENSE -->
## License

Distributed under the GNU General Public License v3. See [LICENSE](LICENSE.md) for more information.

<!-- CONTACT -->
## Contact

The following contacts are responsible for the development and management of the jpf-ctl project

### Development Team

**Parssa Khazra** - [ParssaKhazra](https://github.com/ParssaKhazra)
**Anto Nanah Ji** - [antoNanahJi](https://github.com/antoNanahJi)
**Matt Walker** - [matthewwalk](https://github.com/matthewwalk)
**Hongru Wang** - [HongruWang](https://github.com/HongruWang-123)
**Franck van Breugel** - [franck-van-breugel](https://github.com/franck-van-breugel)

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/othneildrew/Best-README-Template.svg?style=for-the-badge
[contributors-url]: https://github.com/Lassonde-JPF/jpf-ctl-extension/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/othneildrew/Best-README-Template.svg?style=for-the-badge
[forks-url]: https://github.com/Lassonde-JPF/jpf-ctl-extension/network/members
[stars-shield]: https://img.shields.io/github/stars/othneildrew/Best-README-Template.svg?style=for-the-badge
[stars-url]: https://github.com/Lassonde-JPF/jpf-ctl-extension/stargazers
[issues-shield]: https://img.shields.io/github/issues/othneildrew/Best-README-Template.svg?style=for-the-badge
[issues-url]: https://github.com/Lassonde-JPF/jpf-ctl-extension/issues
[license-shield]: https://img.shields.io/github/license/othneildrew/Best-README-Template.svg?style=for-the-badge
[license-url]: https://github.com/Lassonde-JPF/jpf-ctl-extension/blob/main/LICENSE
