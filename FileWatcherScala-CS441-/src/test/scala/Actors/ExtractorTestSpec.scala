package Actors

import akka.actor.ActorRef
import akka.testkit.TestKit
import constants.AppConstants
import service.ExtractorService

import java.io.File


class ExtractorTestSpec extends ActorBaseSpec {

  val file: File = new File("text.txt")
  val extractorServiceMock: ExtractorService = mock[ExtractorService]
  val extractor: ActorRef = system.actorOf(Extractor.props(extractorServiceMock, file))

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "Extractor actor" must {

    "receive valid message" in {
      extractor ! AppConstants.START_MONITORING
    }

    "receive invalid message" in {
      extractor ! 0
    }

  }

}
