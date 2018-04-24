package com.commodityvectors.snapshotmatchers.playJson

import java.io.File

import com.commodityvectors.snapshotmatchers.{SnapshotMatcher, SnapshotSerializer}
import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfterEach, Matchers, fixture}
import play.api.libs.json.{Format, JsValue, Json}

import scala.util.Try

class PlayJsonSnapshotMatcherSpec
    extends fixture.WordSpec
    with Matchers
    with SnapshotMatcher
    with PlayJsonSnapshotMatcher
    with BeforeAndAfterEach {
  case class Test(value: Int)
  implicit lazy val jsonFormat: Format[Test] = Json.format[Test]

  val snapshotFolder: String = "scalatest-snapshot-matcher-play-json/src/test/__snapshots__"
  val currentSpecPath: String =
    s"$snapshotFolder/com/commodityvectors/snapshotmatchers/playJson/PlayJsonSnapshotMatcherSpec"

  override def afterEach(): Unit = {
    Try(FileUtils.deleteDirectory(new File(snapshotFolder)))
  }

  "PlayJsonSnapshotMatcherSpec" should {
    "pretty print json" in { implicit test =>
      val instance = Test(1)
      SnapshotSerializer.serialize(Json.toJson(instance)) shouldEqual
        s"""{
           |  "value" : 1
           |}""".stripMargin
    }

    "generate json snapshot file" in { implicit test =>
      val instance = Test(1)
      Json.toJson(instance) should matchSnapshot("customId")
      FileUtils.readFileToString(
        new File(s"$currentSpecPath/customId.snap"),
        "utf8"
      ) shouldEqual
        s"""{
           |  "value" : 1
           |}""".stripMargin
    }

    "allow deserialization" in { implicit test =>
      val instance = Test(1)
      Json.toJson(instance) should matchSnapshot("anotherId")
      "anotherId" should deserializeAs(instance)
    }
  }
}
