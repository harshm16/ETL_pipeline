package Actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class ActorBaseSpec extends TestKit(ActorSystem("MySpec")) with ImplicitSender
with AnyWordSpecLike
with Matchers
with BeforeAndAfterAll
with MockFactory {
}