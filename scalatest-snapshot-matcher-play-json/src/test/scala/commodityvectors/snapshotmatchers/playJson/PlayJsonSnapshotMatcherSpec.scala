package commodityvectors.snapshotmatchers.playJson

import commodityvectors.snapshotmatchers.{SnapshotMatcher, SnapshotSerializer}
import org.scalatest.{Matchers, fixture}
import play.api.libs.json.{Format, Json}

class PlayJsonSnapshotMatcherSpec extends fixture.WordSpec with Matchers with SnapshotMatcher with PlayJsonSnapshotMatcher {
  case class Test(value: Int)
  implicit lazy val jsonFormat: Format[Test] = Json.format[Test]

  "PlayJsonSnapshotMatcherSpec" should {
    "pretty print json" in { implicit test =>
      val instance = Test(1)
      SnapshotSerializer.serialize(Json.toJson(instance)) shouldEqual
        s"""{
           |  "value" : 1
           |}""".stripMargin
    }
  }
}
