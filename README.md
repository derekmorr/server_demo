# Demo web server

This project shows two versions of a mock web server. One doesn't use actors, and has poor error handling. The other uses actors, and recovers from errors and meets a response SLA.

# Prerequisites

You will need [Java installed](http://java.oracle.com/). The app will download any additional dependencies.

# Running the app:

Once on the desired branch, run

    ./sbt run

# Tests

There are basic tests ensuring that WorkerActor responds in a given SLA. To run them:

    ./sbt test

# Manually installing dependencies

The project depends on [Scala](http://www.scala-lang.org/) and its build tool, [sbt](http://www.scala-sbt.org/). If the `sbt` script fails to install Scala and SBT, you can manually install them.

On OS X, assuming you have [homebrew](http://brew.sh/) installed, the easiest way to install these is via:

    brew install scala sbt

Alternatively, you can download native OS packages from the links above.
