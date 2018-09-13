# ScalaTest Snapshot Matchers [![Build Status](https://travis-ci.org/commodityvectors/scalatest-snapshot-matchers.svg?branch=master)](https://travis-ci.org/commodityvectors/scalatest-snapshot-matchers)

1. Getting Started
    1. [Installing](#installing)
    1. [Test Requirements](#test-requirements)
    1. [Using the Matchers](#using-the-matchers)
    1. [Updating expectations](#updating-expectations)
    1. [Custom Serializers](#custom-serializers)
1. Extensions
    1. [PlayJson](#playjson)
    
## Getting started

### Installing

Add this library as a test dependency of your project

Add bintray resolver

```scala
resolvers += Resolver.bintrayRepo("commodityvectors", "commodityvectors-releases")
```

```scala
libraryDependencies += "com.commodityvectors" %% "scalatest-snapshot-matcher-core" % "2.0.2"
```

| scalatest version | snapshot matcher version |
|-------------------|--------------------------|
|      2.2.x        |          1.1.0           |
|      3.0.x        |          2.x.x           |

### Test requirements

Tests should not run in `fork` mode. If they do, prompting for snapshots update won't work and you will have to remove them manually.

To ensure that you can do the following:

```
fork in Test := false
```

This is a totally optional step and you should add only if you have issues when prompting for an update.

Your test needs to be a fixture test in order for the matcher to have access to an implicit `TestData` and properly generate snapshots.

### Using the Matchers

```scala
package my.pckg

import com.commodityvectors.snapshotmatchers.SnapshotMatcher
import org.scalatest.{ Matchers, fixture }

class MySpec extends fixture.WordSpec with Matchers with SnapshotMatcher {
  case class StateProcessorValue(value: Int)
  
  class StateProcessor {
    private var value = StateProcessorValue(10)
    def mutate(newValue: Int): Unit = value = StateProcessorValue(newValue)
    def state: StateProcessorValue = value
  }

  "MySpec" should {
    "mutate state properly" in { implicit test => // This is very important  
      val myStateProcessor = new StateProcessor
      myStateProcessor.state should matchSnapshot()
      myStateProcessor.mutate(20)
      myStateProcessor.state should matchSnapshot()
    }
  }
}
```

The output of the first run of this test will output two snapshot files under

`project_root/src/test/__snapshots__/my/package/MySpec/myspec-mutate-state-properly.snap` and `project_root/src/test/__snapshots__/my/package/MySpec/myspec-mutate-state-properly-1.snap`

Containing `StateProcessorValue(10)` and `StateProcessorValue(20)` respectively

### Updating expectations

Often on your tests one of your expectations will stop complying with the currently saved snapshot. This means that either the cod is not working as expected or the new output is the correct one.

When running your tests locally, you can run the following `updateSnapshots=true sbt test`
 
### Custom Serializers

You can choose how your data should be serialized byt defining an implicit `SnapshotSerializer[T]`

```scala
case class ComplexType(label: String, count: Int)

implicit lazy val complexTypeSerializer = new SnapshotSerializer[ComplexType] {
  def serialize(in: ComplexType): String = s"""${in.label}:${in.count}"""
}
```

This can be a way to handle non deterministic fields like `UUID` and `DateTime.now`

## Extensions

### PlayJson

There is another project to give the ability to work with PlayJson. You can add like below:

```scala
libraryDependencies += "com.commodityvectors" %% "scalatest-snapshot-matcher-play-json" % "2.0.2"
```

To use it just extend your test with `PlayJsonSnapshotMatcher` as well as `SnapshotMatcher`

You can use it the same way as before but now the snapshot will be stored as a pretty json.

#### PlayJson matchers

This extensions comes with another matcher to facilitate deserialization. 
You can do this by giving the `matchSnapshot` an Id by using `matchSnapshot("myId"")` and then
checking the deserialization with `"myId" should deserializeAs(yourThing)`

```scala
import com.commodityvectors.snapshotmatchers.SnapshotMatcher
import com.commodityvectors.snapshotmatchers.playJson.PlayJsonSnapshotMatcher
import org.scalatest.{ Matchers, fixture }

case class MyType(value: Int)

class MySpec extends fixture.WordSpec with Matchers with SnapshotMatcher with PlayJsonSnapshotMatcher {
  implicit lazy val formats: Format[MyType] = Json.format[MyType]
  
  "MySpec" should {
    "serialize" in { implicit test =>
      Json.toJson(MyType(1)) should matchSnapshot("simpleInstance")
    }
    
    "deserialize" in { implicit test =>
      "simpleInstance" should deserializeAs(MyType(1))
    }
  }
}

```

### Future Changes

- [x] Add Support for scalatest 3
- [ ] Remove requirement on fixture tests
- [x] Add configurable snapshot folder
- [x] Improve base serialization for improved diffs
- [ ] Add support for more extensions (json4s, circe, scalajs, etc)
