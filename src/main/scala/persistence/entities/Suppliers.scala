package persistence.entities

import utils.Profile


case class Supplier(id: Option[Int],name: String,desc: String)

case class SimpleSupplier(name: String,desc: String)


trait Suppliers extends Profile{
  import profile.api._

  class Suppliers(tag: Tag) extends Table[Supplier](tag, "SUPPLIERS") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("userID")
    def desc = column[String]("last_name")
    def * = (id.?, name, desc) <> (Supplier.tupled, Supplier.unapply)
  }
  val suppliers = TableQuery[Suppliers]

}