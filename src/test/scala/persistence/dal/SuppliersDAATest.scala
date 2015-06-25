package persistence.dal

import persistence.entities.{Supplier}
import scala.concurrent.Future
import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.scalatest.junit.JUnitRunner
import akka.pattern.ask
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.util.Timeout


@RunWith(classOf[JUnitRunner])
class SuppliersDalTest extends FunSuite with AbstractPersistenceTest with BeforeAndAfterAll{
  implicit val timeout = Timeout(5.seconds)

  val modules = new Modules {
  }

  test("SuppliersActor: Testing Suppliers Actor") {
    Await.result(modules.suppliersDal.createTables(),5.seconds)
    val numberOfEntities : Int = Await.result((modules.suppliersDal.save(Supplier(None,"sup","desc"))),5.seconds)
    assert (numberOfEntities == 1)
    val supplier : Seq[Supplier] = Await.result((modules.suppliersDal.getSupplierById(1)),5.seconds)
    assert (supplier.length == 1 &&  supplier.head.name.compareTo("sup") == 0)
    val empty : Seq[Supplier] = Await.result((modules.suppliersDal.getSupplierById(2)),5.seconds)
    assert (empty.length == 0)
  }

  override def afterAll: Unit ={
    modules.db.close()
  }
}