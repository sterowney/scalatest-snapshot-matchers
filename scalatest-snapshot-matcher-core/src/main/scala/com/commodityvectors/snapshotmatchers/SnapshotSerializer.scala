package com.commodityvectors.snapshotmatchers

trait SnapshotSerializer[-T] {
  def serialize(in: T): String
}

object SnapshotSerializer {
  def serialize[T](in: T)(implicit s: SnapshotSerializer[T]): String = s.serialize(in)
}
