[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![GNU License][license-shield]][license-url]

<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/Lassonde-JPF/jpf-ctl-extension">
    <img src="resources/jpf-ctl-logo.png" alt="Logo" width="150" height="120">
  </a>

  <h3 align="center">jpf-ctl-extension</h3>

  <p align="center">
    jpf-ctl model checking of java code
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
## About The Project

<< short summary here >>

### Built With

* [Java](https://www.java.com/en/)
* [JPF](https://github.com/javapathfinder)
* [ANTLR4](https://www.antlr.org/)
* [Gradle](https://gradle.org/)

## Documentation

Documentation for the jpf-ctl project can be found within the `/documentation` directory; however, the PDF version can be directly accessed using the following 
[link](/documentation/report.pdf)

<!-- GETTING STARTED -->
## Getting Started

jpf-ctl is an extension for the Java Pathfinder program (accessible [here](https://github.com/javapathfinder/jpf-core)) and as such, is to be setup using the following normal steps for using a jpf extension. 

### Prerequisites

Depending on your desired use cases the number of prerequisites for using jpf-ctl can widely vary. Therefore, the following section will be divided based on whether you wish to _use_ jpf-ctl or _develop_ with jpf-ctl.

#### Normal Usage

* npm
  ```sh
  npm install npm@latest -g
  ```

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
**Franck van Breugel** - _Software Engineer_ - [franck-van-breugel](https://github.com/franck-van-breugel)
**Matt Walker** - _Software Engineer_ - [matthewwalk](https://github.com/matthewwalk)
**Anto Nanah Ji** - _title_ - [antoNanahJi](https://github.com/antoNanahJi)
**Parssa Khazra** - _title_ - [ParssaKhazra](https://github.com/ParssaKhazra)
**Hongru Wang** - _title_ - [HongruWang](https://github.com/HongruWang-123)

<!-- ACKNOWLEDGEMENTS -->
## Acknowledgements

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
