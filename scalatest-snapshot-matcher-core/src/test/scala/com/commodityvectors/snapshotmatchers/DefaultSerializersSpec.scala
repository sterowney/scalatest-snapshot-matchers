package com.commodityvectors.snapshotmatchers

import org.scalatest.{Matchers, fixture}

class DefaultSerializersSpec extends fixture.WordSpec with Matchers with SnapshotMatcher {
  case class Test(value: Integer)

  "DefaultSerializers" should {
    "Serialize simple case classes" in { implicit test =>
      SnapshotSerializer.serialize(Test(1)) shouldEqual "Test(1)"
    }

    "Serialize option" in { implicit test =>
      val element: Option[Test] = None
      SnapshotSerializer.serialize(Option(Test(1))) shouldEqual "Some(Test(1))"
      SnapshotSerializer.serialize(element) shouldEqual "None"
    }

    "Serializer array" in { implicit test =>
      SnapshotSerializer.serialize(List(Test(1))) shouldEqual "List(Test(1))"
      SnapshotSerializer.serialize(Seq(Test(1))) shouldEqual "List(Test(1))"
      SnapshotSerializer.serialize(Vector(Test(1))) shouldEqual "Vector(Test(1))"
    }

     "Serializer maps" in { implicit test =>
       SnapshotSerializer.serialize(Map(Test(1) -> Test(2))) shouldEqual "Map(Test(1) -> Test(2))"
       SnapshotSerializer.serialize(Map("key" -> Test(2))) shouldEqual "Map(key -> Test(2))"
       SnapshotSerializer.serialize(Map(10 -> Test(2))) shouldEqual "Map(10 -> Test(2))"
       SnapshotSerializer.serialize(Map(10.0 -> Test(2))) shouldEqual "Map(10.0 -> Test(2))"
     }

    "Serialize composed types" in { implicit test =>
      case class Complex(v1: Int, v2: String, v3: Double, v4: List[Option[String]], v5: Map[Option[String], Seq[Complex]])
      val child = Complex(1, "2", 3.0, List(Option("Me")), Map())
      val instance = Complex(1, "2", 3.0, List(Option("Me")), Map(Option("you") -> Seq(child)))
      SnapshotSerializer.serialize(instance) shouldEqual
        s"""|Complex(
            |  v1 = 1,
            |  v2 = "2",
            |  v3 = 3.0,
            |  v4 = List(Some(Me)),
            |  v5 = Map(Some(you) -> List(Complex(1,2,3.0,List(Some(Me)),Map())))
            |)""".stripMargin
    }

    "Allow custom serializers" in { implicit test =>
      implicit lazy val customSerializer = new SnapshotSerializer[Test] {
        override def serialize(in: Test): String = s"CustomSerializer: ${in.value}"
      }

      SnapshotSerializer.serialize(Test(1)) shouldEqual "CustomSerializer: 1"
    }
  }
}
