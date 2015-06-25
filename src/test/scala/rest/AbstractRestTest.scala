package rest

import akka.testkit.TestProbe
import persistence.dal.SuppliersDal
import utils.{PersistenceModule, ConfigurationModuleImpl, ActorModule}
import akka.actor.ActorRef
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import akka.testkit.TestActor
import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import org.specs2.mock.Mockito

trait AbstractRestTest extends Specification with Specs2RouteTest with Mockito{

  trait Modules extends ConfigurationModuleImpl with ActorModule with PersistenceModule {
    val system = AbstractRestTest.this.system

    override val suppliersDal = mock[SuppliersDal]

    override def config = getConfig.withFallback(super.config)
  }

  def getConfig: Config = ConfigFactory.empty();


}
