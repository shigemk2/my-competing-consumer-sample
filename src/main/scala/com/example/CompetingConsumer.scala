package com.example

import akka.actor._
import akka.routing.SmallestMailboxPool

object CompetingConsumerDriver extends CompletableApp(100) {
  val workItemsProvider = system.actorOf(
    Props[WorkConsumer]
      .withRouter(SmallestMailboxPool(nrOfInstances = 5))
  )

  for (itemCount <- 1 to 100) {
    workItemsProvider ! WorkItem("WorkItem" + itemCount)
  }
}

case class WorkItem(name: String)

class WorkConsumer extends Actor {
  def receive = {
    case workItem: WorkItem =>
      println(s"${self.path.name} for: ${workItem.name}")

      CompetingConsumerDriver.completedStep()
  }
}
