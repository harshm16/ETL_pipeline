package Actors

import akka.actor.ActorRef
import akka.testkit.TestKit
import constants.AppConstants
import service.{ExtractorService, WatcherService}

import java.io.File

class WatcherTestSpec extends ActorBaseSpec {

  val file: File = new File("test.txt")
  val watcherServiceMock: WatcherService  = mock[WatcherService]
  val extractorServiceMock: ExtractorService = mock[ExtractorService]
  val extractor: ActorRef = system.actorOf(Extractor.props(extractorServiceMock, file))
  val watcher: ActorRef = system.actorOf(Watcher.props(watcherServiceMock, extractor, file))


  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "Watcher actor" must {

    "receive valid message" in {
      watcher ! AppConstants.START_MONITORING
    }

    "receive invalid message" in {
      watcher ! "random-string"
    }

  }

}
