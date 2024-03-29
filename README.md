# MiniSyncJ

[![Release](https://jitpack.io/v/molguin92/MiniSyncJ.svg)](https://jitpack.io/molguin92/MiniSyncJ)

Reference implementation in Java 8 (*) of the MiniSync/TinySync time synchronization algorithms detailed in [\[1, 2\]](#references).
Note that this implementation is still pretty naive and probably should not be used for anything critical (yet).

`(*) The reason for this outdated choice of Java is to maintain compatibility with old versions of Android...`

## Usage
### Gradle / Maven
This library is published through [Jitpack.io](https://jitpack.io) and can thus easily be integrated into existing Gradle projects:

```gradle
// Add Jitpack.io to the project repositories:
repositories {
// ...
    maven { url 'https://jitpack.io' }
}
// add the library as a build dependency
dependencies {
// ...
    implementation 'com.github.molguin92:MiniSyncJ:v0.1.1-alpha'
}
```

See additional documentation on [the project page at Jitpack.io](https://jitpack.io/#molguin92/MiniSyncJ).

### Manual
Download a compiled .jar from the [Releases](https://github.com/molguin92/MiniSyncJ/releases) page and put it in a folder in your projects' classpath.

## References
[1] S. Yoon, C. Veerarittiphan, and M. L. Sichitiu. 2007. Tiny-sync: Tight time synchronization for wireless sensor 
networks. ACM Trans. Sen. Netw. 3, 2, Article 8 (June 2007). 
DOI: 10.1145/1240226.1240228 

[2] M. L. Sichitiu and C. Veerarittiphan, "Simple, accurate time synchronization for wireless sensor networks," 2003 
IEEE Wireless Communications and Networking, 2003. WCNC 2003., New Orleans, LA, USA, 2003, pp. 1266-1273 vol.2. DOI: 
10.1109/WCNC.2003.1200555. URL: http://ieeexplore.ieee.org/stamp/stamp.jsp?tp=&arnumber=1200555&isnumber=27029

## Copyright
 [![License Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Code is Copyright© (2019 -) of Manuel Olguín Muñoz \<manuel@olguin.se\>\<molguin@kth.se\>, provided under an Apache v2.0 License.
See [LICENSE](LICENSE) for details.

The TinySync and MiniSync algorithms are owned by the authors of the referenced papers [1, 2].
