package commodityvectors.snapshotmatchers

import java.io.{File, PrintWriter}

import difflib.DiffUtils
import org.scalatest.matchers.{MatchResult, Matcher}
import org.scalatest.{Outcome, SuiteMixin, TestData, fixture}

import collection.JavaConverters._
import scala.io.{Source, StdIn}
import scala.util.Try

trait SnapshotLoader {
  private val testFolder = getClass.getName.replaceAll("\\.", "/")
  private val snapshotFolder = "/__snapshots__"
  private val fullWritePath = s"${getClass.getResource("").getPath.split("target").head}src/test$snapshotFolder/$testFolder"

  private def folderPath: String = new File(s"$fullWritePath").getAbsolutePath
  private def filePath(id: String): String = new File(s"$folderPath/$id.snap").getAbsolutePath

  def loadSnapshot(id: String): Option[String] = Try(Source.fromFile(filePath(id)).mkString).toOption
  def writeSnapshot[T](id: String, content: T)(implicit s: SnapshotSerializer[T]): Unit = {
    new File(folderPath).mkdirs()

    val file = new File(filePath(id))
    new PrintWriter(file) {
      write(s.serialize(content))
      close()
    }
  }
}

trait TestDataArgs extends SuiteMixin { this: fixture.Suite =>
  type FixtureParam = TestData
  override def withFixture(test: OneArgTest): Outcome = {
    withFixture(test.toNoArgTest(test))
  }
}

trait TestDataEnhancer {
  implicit class TestDataEnhancer(in: TestData) {
    def key: String = in.name.replaceAll("[^A-Za-z0-9]", "-").toLowerCase()
  }
}

trait SnapshotMessages {
  val ContentsAreEqual = "Contents Are Equal"

  def errorMessage(current: String, found: String): String = {
    val patch = DiffUtils.diff(found.split("\n").toList.asJava, current.split("\n").toList.asJava)
    val diff = DiffUtils.generateUnifiedDiff("Original Snapshot", "New Snapshot", found.split("\n").toList.asJava, patch, 10).asScala
    val parsedLines = diff.map { line =>
      if (line.startsWith("+"))
        s"${Console.GREEN}$line"
      else if (line.startsWith("-"))
        s"${Console.RED}$line"
      else
        s"${Console.WHITE}$line"
    }

    s"""|Text Did not match:
        |${parsedLines.mkString("\n")}
        |
        |End Diff;""".stripMargin
  }
}

trait SnapshotMatcher extends SnapshotLoader with SnapshotMessages with TestDataArgs with DefaultSerializers { self: fixture.Suite =>

  private var testMap: Map[String, Int] = Map.empty

  private def getCurrentAndSetNext(id: String, isExplicit: Boolean): String = {
    val next = testMap.getOrElse(id, 0) + 1
    testMap += (id -> next)
    val current = next - 1
    if (current == 0) id
    else if (!isExplicit) s"$id-$current"
    else throw new Exception("Cannot reuse snapshot for explicit identifier. There should be only a single snapshot match")
  }

  class SnapshotShouldMatch[T](explicitId: Option[String])(implicit s: SnapshotSerializer[T], test: TestData) extends Matcher[T] with TestDataEnhancer {
    override def apply(left: T): MatchResult = {
      val testIdentifier = getCurrentAndSetNext(explicitId.getOrElse(test.key), isExplicit = explicitId.nonEmpty)
      loadSnapshot(testIdentifier) match {
        case Some(content) =>
          val serialized = s.serialize(left)
          val isEquals = serialized == content

          if(!isEquals) {
            println(
              s"""
                 |
                 |TEST SCOPE: ${test.name}
                 |
                 |${errorMessage(serialized, content)}""".stripMargin)
            val answer = StdIn.readLine(s"${Console.YELLOW}Do you want to update the snapshot? [y/n] ")
            if(answer == "y") {
              writeSnapshot(testIdentifier, left)
              MatchResult(matches = true, "", ContentsAreEqual)
            } else MatchResult(serialized == content, errorMessage(serialized, content), ContentsAreEqual)
          } else MatchResult(matches = true, "", ContentsAreEqual)
        case _ =>
          writeSnapshot(testIdentifier, left)
          MatchResult(matches = true, "", ContentsAreEqual)
      }
    }
  }

  def matchSnapshot[T]()(implicit s: SnapshotSerializer[T], test: TestData) = new SnapshotShouldMatch[T](None)
  def matchSnapshot[T](explicitId: String)(implicit s: SnapshotSerializer[T], test: TestData) = new SnapshotShouldMatch[T](Option(explicitId))
}