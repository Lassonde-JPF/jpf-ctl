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
## About the Project

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

### Built With

- [Java](https://www.oracle.com/ca-en/java/technologies/javase/javase8-archive-downloads.html)
- [jpf-core](https://github.com/javapathfinder/jpf-core)
- [jpf-label](https://github.com/javapathfinder/jpf-label)

## Documentation

Documentation for the jpf-ctl project can be found within the `/documentation` directory; however, the PDF version can be directly accessed using the following 
[link](/documentation/report.pdf)

<!-- GETTING STARTED -->
## Getting Started

jpf-ctl is an extension for the Java Pathfinder program (accessible [here](https://github.com/javapathfinder/jpf-core)) and as such, is to be setup using the following normal steps for using a jpf extension. 

### Prerequisites

Depending on your desired use cases the number of prerequisites for using jpf-ctl can widely vary. Therefore, the following section will be divided based on whether you wish to _use_ jpf-ctl or _develop_ with jpf-ctl.

Regardless, the following prerequisites are absolutely required to run jpf-ctl
1. [jpf](https://github.com/javapathfinder)
2. [jpf-label](https://github.com/javapathfinder/jpf-label)

#### Normal Usage

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
  Used for testing the jpf-partial-transition-system-listener, specifically, to setup testing environments outside of JPF

### Installation

<< installation instructions >>

<!-- USAGE EXAMPLES -->
## Usage

<< how to use >>

<!-- ROADMAP -->
## Roadmap

See the [open issues](https://github.com/Lassonde-JPF/jpf-ctl-extension/issues) for a list of proposed features (and known issues).

<!-- CONTRIBUTING -->
## Contributing

If you are a developer who wishes to contribute to this project, please note that we do not have time to review minor changes such as variable renames, spelling mistakes, etc. If you would still wish to point these out, please create an issue rather than a PR.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<!-- LICENSE -->
## License

Distributed under the GNU General Public License v3. See `LICENSE` for more information.

<!-- CONTACT -->
## Contact
The following contacts are responsible for the development and management of the jpf-ctl project

### Development Team
**Franck van Breugel** - _Software Engineer_ - [franck-van-breugel](https://github.com/franck-van-breugel) <br/>
**Matt Walker** - _Software Engineer_ - [matthewwalk](https://github.com/matthewwalk)<br/>
**Anto Nanah Ji** - _title_ - [antoNanahJi](https://github.com/antoNanahJi)<br/>
**Parssa Khazra** - _title_ - [ParssaKhazra](https://github.com/ParssaKhazra)<br/>
**Hongru Wang** - _title_ - [HongruWang](https://github.com/HongruWang-123)<br/>

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
