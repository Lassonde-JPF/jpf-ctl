- [About jpf-logic](#About-jpf-logic)
  - [Limitations](#Limitations)
- [Installation instructions for using jpf-logic](#Installation-instructions-for-using-jpf-logic)
  - [Java](#Java)
  - [Git](#Git)
  - [Java PathFinder (jpf-core)](#Java-PathFinder-jpf-core)
  - [jpf-label](#jpf-label)
  - [jpf-ctl](#jpf-ctl)
- [Using jpf-logic](#Using-jpf-logic)
- [Questions about jpf-logic](#Questions-about-jpf-logic)
- [Contributing to jpf-logic](#Contributing-to-jpf-logic)
- [License](#License)
- [Contact](#Contact)

## About jpf-logic

The most common approach to finding errors in software is testing. However, testing is of limited use when checking *concurrent software* for errors. Hence, other techniques, including *model checking*, have been developed to supplement testing. We focus on Java as Java is one of the most widely used programming languages. Presently, the most popular tool for model checking of Java code, [*Java PathFinder*](https://github.com/javapathfinder) (JPF), does not support the checking of properties expressed in temporal logics; instead, it checks properties such as deadlocks and data races. To resolve this, we present jpf-logic, a framework to extend of JPF with support for the checking of properties specified in temporal logics such as *computational tree logic* (CTL). 

Let us consider a simple example. The class [Account](src/main/java/jpf/logic/examples/Account.java) models a bank account.  The classes [Deposit](src/main/java/jpf/logic/examples/Deposit.java) and [Withdraw](src/main/java/jpf/logic/examples/Withdraw.java) are threads that deposit to and withdraw from a bank account.  The class [Main](src/main/java/jpf/logic/examples/Main.java) is an app that creates a single bank account and multiple threads that deposit to and withdraw from that bank account.  It takes three command line arguments:
- the initial balance of the bank account,
- the number of Deposit threads that do one deposit transaction to the bank account, and
- the number of Withdraw threads that do one withdrawal transaction to the back account.

The withdraw method of the Account class
```java
public boolean withdraw(double amount) {
  if (amount < 0) {
    throw new IllegalArgumentException("A negative amount cannot be withdrawn");
  }
  boolean success;
  if (this.balance >= amount) {
    this.balance -= amount;
    success = true;
  } else {
    success = false;
  }
  return success;
}
```
attempts to ensure that the balance never becomes negative. However, as the method is not synchronized, the balance may become negative. The Main class contains a static boolean field named negative which captures whether the balance of the account is negative. This field is regularly updated in the app.

The JUnit test [AccountTest](src/test/java/jpf/logic/examples/AccountTest.java), which performs one thousand runs of the app with an initial balance of one, two Deposit threads, and two Withdraw threads, does *not* detect a negative balance. The property that the balance never becomes negative can be captured by the CTL formula AG ! negative. This formula specifies that for every run of the Java app (*A* stands for "for all"), and for every state of such a run (*G* stands for "globally"), the atomic proposition negative, which captures that the value of the boolean static field Example.negative is true, does not hold. As will be shown in the demo below, jpf-logic is *successful* in determining that the CTL formula does not hold for this Java app, that is, the balance can become negative.

### Limitations

Since jpf-logic relies on jpf-core, the core of JPF, and jpf-core currently supports Java 8, only apps that use Java 8 constructs can be checked. Furthermore, jpf-core can only handle apps, that is, it needs a main method of a class as its starting point.

## Installation instructions for using jpf-logic

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

It is convenient, yet not essential, to put the directories of jpf-core, jpf-ctl and jpf-label in a common directory.  Assume that this common directory is called jpf.

```
jpf/
| jpf-core/
| jpf-ctl/
| jpf-label/
```

1. Clone [jpf-core](https://github.com/javapathfinder/jpf-core) using Git: go the directory where you want to put jpf-core (for example, the directory jpf mentioned above) and issue the following command.
```
> git clone https://github.com/javapathfinder/jpf-core.git
Cloning into 'jpf-core'...
remote: Enumerating objects: 3868, done.
remote: Counting objects: 100% (333/333), done.
remote: Compressing objects: 100% (194/194), done.
remote: Total 3868 (delta 105), reused 242 (delta 60), pack-reused 3535
Receiving objects: 100% (3868/3868), 2.26 MiB | 1.18 MiB/s, done.
Resolving deltas: 100% (1865/1865), done.
```
2. Build jpf-core with the Gradle wrapper (you do not need to install Gradle): inside the jpf-core directory, issue the following command.
```
> .\gradlew

> Task :compileJava
C:\Users\montreal\Downloads\tmp\jpf-core\src\main\gov\nasa\jpf\vm\HashedAllocationContext.java:21: warning: sun.misc.SharedSecrets is internal proprietary API and may be removed in a future release
import sun.misc.SharedSecrets;
               ^
[lots of text deleted]

Deprecated Gradle features were used in this build, making it incompatible with Gradle 7.0.
Use '--warning-mode all' to show the individual deprecation warnings.
See https://docs.gradle.org/6.9/userguide/command_line_interface.html#sec:command_line_warnings

BUILD SUCCESSFUL in 15s
16 actionable tasks: 16 executed
```
3. Create a directory named .jpf inside your home directory.  To find your home directory, run the following Java app.
```java
public class PrintUserHome {
  public static void main(String[] args) {
    System.out.println(System.getProperty("user.home"));
  }
}
```
4. Inside the .jpf directory, create a file named site.properties with the following content.
```
# JPF site configuration
jpf-core=/path/to/directory/of/jpf-core/
extensions=${jpf-core}
```
5. Add the path to the bin directory of jpf-core to the environment variable PATH. For example, in a Windows 10 PowerShell this can be done by issuing the following command (do not forget the double quotes).
```
> $Env:PATH = "/path/to/bin/directory/of/jpf-core/;" + $Env:PATH
```
In a Linux sh this can be done by issuing the following command.
```
> PATH=/path/to/bin/directory/of/jpf-core/:$PATH
```
In a Linux csh this can be done by issuing the following command.
```
> set path=(/path/to/bin/directory/of/jpf-core/ $path)
```
6. Run jpf-core on the example HelloWorld, that comes with jpf-core, by issuing the following command.
```
> jpf HelloWorld
JavaPathfinder core system v8.0 - (C) 2005-2014 United States Government. All rights reserved.


====================================================== system under test
HelloWorld.main()

====================================================== search started: 30/06/22 8:39 AM
I won't say it!

====================================================== results
no errors detected

====================================================== statistics
elapsed time:       00:00:00
states:             new=1,visited=0,backtracked=1,end=1
search:             maxDepth=1,constraints=0
choice generators:  thread=1 (signal=0,lock=1,sharedRef=0,threadApi=0,reschedule=0), data=0
heap:               new=353,released=11,maxLive=0,gcCycles=1
instructions:       3278
max memory:         489MB
loaded code:        classes=60,methods=1338

====================================================== search finished: 30/06/22 8:39 AM
```

### jpf-label

1. Clone [jpf-label](https://github.com/javapathfinder/jpf-label) using Git: go the directory where you want to put jpf-label (for example, the directory jpf mentioned above) and issue the following command.
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
jpf-label=/path/to/directory/of/jpf-label/
```
to JPF's site.properties file.

### jpf-logic

1. Clone jpf-ctl using Git: go the directory where you want to put jpf-ctl (for example, the directory jpf mentioned above) and issue the following command.

```
> git clone https://github.com/Lassonde-JPF/jpf-ctl.git
Cloning into 'jpf-ctl'...
remote: Enumerating objects: 4493, done.
remote: Counting objects: 100% (1498/1498), done.
remote: Compressing objects: 100% (615/615), done.
remote: Total 4493 (delta 832), reused 1439 (delta 801), pack-reused 2995
Receiving objects: 100% (4493/4493), 14.47 MiB | 1.17 MiB/s, done.
Resolving deltas: 100% (2260/2260), done.
```
2. Switch to the jpf22 branch: inside the jpf-ctl directory, issue the following command.
```
> git checkout -b jpf22 origin/jpf22
Switched to a new branch 'jpf22'
Branch 'jpf22' set up to track remote branch 'jpf22' from 'origin'.
```
3. Build jpf-logic with the Gradle wrapper: inside the jpf-ctl directory, issue the following command.
```
> .\gradlew build

> Task :test
Test execution: SUCCESS
Summary: 303076 tests, 303076 passed,0 failed, 0 skipped

BUILD SUCCESSFUL in 34s
7 actionable tasks: 5 executed, 2 up-to-date
```
3. Add
```
jpf-logic=/path/to/directory/of/jpf-ctl/
```
to JPF's site.properties file.
4. Add the path to the bin directory of jpf-ctl to the environment variable PATH.

## Using jpf-logic

To use jpf-logic, you need to create two files.  The one file specifies the property that you want to check and the other file specifies the Java app of which you want to check the property.  Let us first discuss the file that specifies the property.  Assume that this file is called `property.txt`.  Your `property.txt` file consists of two parts.  First, you specify the atomic propositions and their aliases.  We have introduced aliases so that the formulas become easier to read.  The following types of atomic proposition are currently supported.

- `Initial`  
Labels the initial state.

- `End`  
Labels the final states.

- `BooleanStaticField <fully qualified name of a boolean static field> <boolean value>`  
For example, 
`BooleanStaticField jpf.logic.examples.Main.negative true`
labels those states in which the boolean static field `jpf.logic.examples.Main.negative` has the value true.

- `BooleanLocalVariable <fully qualified name of the method in which the boolean local variable is declared> <parameter types of the method> : <name of the boolean local variable> <boolean value>`  
For example,
`BooleanLocalVariable jpf.logic.examples.Account.withdraw(double) : success true`
labels those states in which the boolean local variable `success` of the method `jpf.logic.examples.Account.withdraw(double)` has the value true.  (Note: a local variable only has a value within its scope.)

- `InvokedMethod <fully qualified name of a method> <parameter types of the method>`  
For example,
`InvokedMethod jpf.logic.examples.Account.deposit(double)`
labels those states in which the method `jpf.logic.examples.Account.deposit(double)` is invoked.

- `ReturnedVoidMethod <fully qualified name of a void method> <parameter types of the method>`  
For example,
`ReturnedVoidMethod jpf.logic.examples.Main.main(java.lang.String[])`
labels those states in which the void method `jpf.logic.examples.Main.main(java.lang.String[])` returns.

- `ReturnedBooleanMethod <fully qualified name of a void method> <parameter types of the method> <boolean value>`  
For example,
`ReturnedBooleanMethod jpf.logic.examples.Account.withdraw(double) false`
labels those states in which the method `jpf.logic.examples.Account.withdraw(double)` returns the value false.

- `ThrownException <fully qualified name of an exception class>`  
For example,
`ThrownException java.lang.IllegalArgumentException`
labels those states in which a `java.lang.IllegalArgumentException` is thrown.

- `SynchronizedStaticMethod <fully qualified name of a synchronized static method> <parameter types of the method> <acquire or release>`  
For example,
`SynchronizedStaticMethod jpf.logic.examples.Account.getNumberOfAccounts() acquire`
labels those states in which a thread acquires the lock on the synchronized static method `jpf.logic.examples.Account.getNumberOfAccounts()`.

For example, the above introduced atomic propositions can be linked to aliases as follows.
```
negative: BooleanStaticField jpf.logic.examples.Main.negative true
return: ReturnedVoidMethod jpf.logic.examples.Main.main(java.lang.String[])
```

In the second part of the `property.txt` file, you specify the property you want to check in terms of a temporal logic supported by jpf-logic.  For example, computation tree logic (CTL) is supported by jpf-logic.  Its syntax is captured by the following grammar in Bachus-Naur format.
```
formula ::
      '(' formula ')'
    | '!' formula
    | 'true'
    | 'false'
    | ALIAS
    | 'AX' formula
    | 'AG' formula
    | 'AF' formula
    | 'EX' formula
    | 'EG' formula
    | 'EF' formula
    | formula 'AU' formula
    | formula 'EU' formula
    | formula '&&' formula
    | formula '||' formula
    | formula '->' formula
    | formula '<->' formula
```
For example, to express that the boolean static field `jpf.logic.examples.Main.negative` is never true, we can use for CTL formula `AG ! negative`.  The CTL formula `AF return` captures that the method jpf.logic.examples.Main.main(java.lang.String[]) always eventually returns.  Combining these two CTL formulas and adding them to the above atomic propositions and their aliases, we arrive at the following `property.txt` file.
```
negative: BooleanStaticField jpf.logic.examples.Main.negative true
return: ReturnedVoidMethod jpf.logic.examples.Main.main(java.lang.String[])

(AG ! negative) && (AF return)
```

As we already mentioned above, the second file specifies the Java app of which you want to check the property as well as some other details.  It is essential to set the property `target` to the fully qualified name of the Java app that is checked.  If the app needs command line arguments, then the property `target.args` should be set as well.  The `classpath' property should contain the path to the directory in which the bytecode of the app can be found.  For example, if the file Main.class can be found in the directory with path C:/Users/someone/Documents/jpf/jpf-ctl/build/classes/java/main/jpf/logic/examples/ then `classpath' should be set to C:/Users/someone/Documents/jpf/jpf-ctl/build/classes/java/main/.  The property `jpf.logic.formula` point to the file that contains the aliases and the formula such as the `property.txt` file described above.  Since jpf-logic only supports CTL at this time, the properties `jpf.logic.parser` and 
`jpf.logic.model-checker` are set as in the example below.

```
# name of the app being checked
target = jpf.logic.examples.Main
# commandline arguments for the app
target.args = 1,2,2

# path to the directory in which the bytecode of the app can be found
classpath = path/to/directory/bytecode/of/app

# name of the file that contains the aliases and formula
jpf.logic.formula = path/of/file/with/aliases/and/formula
# class to parse the formula
jpf.logic.parser = jpf.logic.ctl.CTLFormulaParser
# class to model check
jpf.logic.model-checker = jpf.logic.ctl.CTLModelChecker
```

Assume the above described file is named `Main.jpf`.  We can run jpf-logic by issuing the following command in the directory that contains the file `Main.jpf`.
```
> jpf-logic Main.jpf
The formula does not hold
```
If we replace the formula `(AG ! negative) && (AF return)` with `AF return` and run jpf-logic again, we get the following.
```
> jpf-logic Main.jpf
The formula holds
```

## Questions about jpf-logic

If you have any questions about jpf-logic, check the [JPF Google group](https://groups.google.com/g/java-pathfinder/).  If you cannot find the answer, post your question there.

## Contributing to jpf-logic

First, install jpf-logic as described above.  Next, run the examples provided with jpf-logic.  

### Add examples

Add examples to the src/main/java/jpf/logic/examples/ directory of jpf-logic.

### Tackle reported issues

Tackle one of the reported [issues](https://github.com/Lassonde-JPF/jpf-ctl-extension/issues/).

### Add tests

Add tests to the src/tests/ directory of jpf-logic.

### Add new features

Add a new feature to jpf-logic.

### How to contribute

1. Fork ctl-jpf. 
2. Create your feature branch (`git checkout -b feature/AmazingFeature`).
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the branch (`git push origin feature/AmazingFeature`).
5. Open a pull request.

Please note that we do not have time to review minor changes such as variable renames, spelling mistakes, etc. If you would wish to point these out, please create an issue rather than a pull request.

## License

Distributed under the GNU General Public License v3. See [LICENSE](LICENSE.md) for more information.

## Contact

The following contacts are responsible for the development and management of jpf-logic.

### Development Team

**Matt Walker** - [matthewwalk](https://github.com/matthewwalk)  
**Parssa Khazra** - [ParssaKhazra](https://github.com/ParssaKhazra)  
**Anto Nanah Ji** - [antoNanahJi](https://github.com/antoNanahJi)  
**Hongru Wang** - [HongruWang](https://github.com/HongruWang-123)  
**Franck van Breugel** - [franck-van-breugel](https://github.com/franck-van-breugel)

