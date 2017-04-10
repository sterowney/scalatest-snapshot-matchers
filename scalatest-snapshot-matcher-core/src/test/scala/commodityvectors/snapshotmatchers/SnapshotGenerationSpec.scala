package commodityvectors.snapshotmatchers

import java.io.File

import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfterEach, Matchers, fixture}

import scala.util.Try

class SnapshotGenerationSpec extends fixture.WordSpec with Matchers with SnapshotMatcher with BeforeAndAfterEach {

  val snapshotFolder: String = "scalatest-snapshot-matcher-core/src/test/__snapshots__"
  val currentSpecPath: String = s"$snapshotFolder/commodityvectors/snapshotmatchers/SnapshotGenerationSpec"

  override def afterEach(): Unit = {
    Try(FileUtils.deleteDirectory(new File(snapshotFolder)))
  }

  "SnapshotMatcher" should {
    "generate snapshot file with expectation" in { implicit test =>
      val value: Int = 1
      value should matchSnapshot[Int]()
      FileUtils.readFileToString(
        new File(s"$currentSpecPath/snapshotmatcher-should-generate-snapshot-file-with-expectation.snap")
      ) shouldEqual "1"
    }

    "generate file with custom id" in { implicit test =>
      val value = 10
      value should matchSnapshot[Int]("customId")
      FileUtils.readFileToString(
        new File(s"$currentSpecPath/customId.snap")
      ) shouldEqual "10"
    }
  }
}
