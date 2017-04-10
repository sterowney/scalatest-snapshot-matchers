package commodityvectors.snapshotmatchers

trait DefaultSerializers {
  implicit def anySerializer[T] = new SnapshotSerializer[T] {
    override def serialize(in: T): String = in.toString
  }
}
