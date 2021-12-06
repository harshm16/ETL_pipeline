import Actors.{Extractor, Watcher}
import akka.actor.{ActorRef, ActorSystem}
import constants.AppConstants.START_MONITORING
import constants.{ActorConstants, AppConstants}
import service.{ExtractorService, WatcherService}

import java.io.File
import java.util.logging.Logger

/**
 * Driver code for file watcher
 **/
object FileWatcher {

  def main(args: Array[String]): Unit = {
    val path = args(0)
    val folder = new File(path)
    val system = ActorSystem(AppConstants.ACTOR_SYSTEM)
    val actors = scala.collection.mutable.Map[File, (ActorRef, ActorRef)]()
    val logger: Logger = Logger.getLogger(FileWatcher.getClass.getName)

    folder.listFiles().foreach { f =>
      logger.info("Creating actors for file " + f.getName)
      val watcherService = new WatcherService
      val extractorService = new ExtractorService
      val extractor = system.actorOf(Extractor.props(extractorService, f), name = ActorConstants.extractorSubName + f.getName)
      val watcher = system.actorOf(Watcher.props(watcherService, extractor, f), name = ActorConstants.watcherSubName + f.getName)
      actors += (f -> (watcher, extractor))
    }

    actors.foreach { entry =>
      entry._2._1 ! START_MONITORING
    }
  }

}
