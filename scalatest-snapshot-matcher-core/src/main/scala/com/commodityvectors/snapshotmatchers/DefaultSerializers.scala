package com.commodityvectors.snapshotmatchers

import com.commodityvectors.snapshotmatchers.utils.PrettyPrint

trait DefaultSerializers {
  implicit def anySerializer[T] = new SnapshotSerializer[T] {
    override def serialize(in: T): String = PrettyPrint.print(in)
  }
}
