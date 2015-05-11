package persistence.dal

import persistence.dal.SuppliersDAA.{GetSupplierById, Save, CreateTables}
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
class SuppliersDAATest extends FunSuite with AbstractPersistenceTest with BeforeAndAfterAll{
  implicit val timeout = Timeout(5.seconds)

  val modules = new Modules {
  }

  test("SuppliersActor: Testing Suppliers Actor") {
    Await.result((modules.suppliersDAA ? CreateTables).mapTo[Future[Unit]],5.seconds)
    val numberOfEntities : Int = Await.result((modules.suppliersDAA ? Save(Supplier(None,"sup","desc"))).mapTo[Future[Int]].flatMap(x => x),5.seconds)
    assert (numberOfEntities == 1)
    val supplier : Seq[Supplier] = Await.result((modules.suppliersDAA ? GetSupplierById(1)).mapTo[Future[Seq[Supplier]]].flatMap(x => x),5.seconds)
    assert (supplier.length == 1 &&  supplier.head.name.compareTo("sup") == 0)
    val empty : Seq[Supplier] = Await.result((modules.suppliersDAA ? GetSupplierById(2)).mapTo[Future[Seq[Supplier]]].flatMap(x => x),5.seconds)
    assert (empty.length == 0)
  }

  override def afterAll: Unit ={
    modules.db.close()
  }
}