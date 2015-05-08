package rest

import akka.testkit.TestProbe
import utils.{PersistenceModule, ConfigurationModuleImpl, ActorModule}
import akka.actor.ActorRef
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import akka.testkit.TestActor
import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest

trait AbstractRestTest extends Specification with Specs2RouteTest {

  val suppliersActor = TestProbe()

  trait Modules extends ConfigurationModuleImpl with ActorModule with PersistenceModule {
    val system = AbstractRestTest.this.system

    override val suppliersDAA = system.actorSelection(suppliersActor.ref.path)

    override def config = getConfig.withFallback(super.config)
  }

  def getConfig: Config = ConfigFactory.empty();

  def addProbeBehaviour(actor: TestProbe)(behaviour: PartialFunction[(ActorRef, Any), Unit]){
    actor.setAutoPilot(new TestActor.AutoPilot {
      def run(sender: ActorRef, msg: Any) = {
        behaviour.applyOrElse((sender, msg), (x: (ActorRef, Any)) => throw new Exception("error") )
        TestActor.KeepRunning
      }
    })

  }
}
