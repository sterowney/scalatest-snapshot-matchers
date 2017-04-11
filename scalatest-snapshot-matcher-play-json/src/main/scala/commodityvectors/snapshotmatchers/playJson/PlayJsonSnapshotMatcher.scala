package commodityvectors.snapshotmatchers.playJson

import commodityvectors.snapshotmatchers.{SnapshotLoader, SnapshotMessages, SnapshotSerializer}
import org.scalactic.Equality
import org.scalatest.matchers.{MatchResult, Matcher}
import play.api.libs.json.{JsValue, Json, Reads}

trait PlayJsonSnapshotMatcher extends SnapshotLoader with SnapshotMessages {
  implicit lazy val playJsonSerializer = new SnapshotSerializer[JsValue] {
    override def serialize(in: JsValue): String = Json.prettyPrint(in)
  }

  class JsonDeserializerShouldMatch[T](in: T)(implicit reads: Reads[T], equals: Equality[T]) extends Matcher[String] {
    override def apply(explicitId: String): MatchResult = {
      loadSnapshot(explicitId) match {
        case Some(content) =>
          val parsed = Json.parse(content).as[T]
          val isEquals = equals.areEqual(parsed, in)
          MatchResult(isEquals, errorMessage(in.toString, parsed.toString), ContentsAreEqual)
        case None => MatchResult(matches = false, s"Could not find snapshot for id: $explicitId", ContentsAreEqual)
      }
    }
  }

  def deserializeAs[T](in: T)(implicit reads: Reads[T], equals: Equality[T]) = new JsonDeserializerShouldMatch[T](in)
}