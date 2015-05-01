package system

class Warehouse(level: LevelMap) {
  val nReceivers = 5
  val nSenders = 5
  val receivers: Array[Robot] = Array.tabulate(nReceivers)(x =>
    new Robot(level.randomEmptyPosition())
  )
  val senders: Array[Robot] = Array.tabulate(nSenders)(x =>
    new Robot(level.randomEmptyPosition())
  )

  @volatile
  var simulationStopRequested = false
  val simulationThread = new Thread(new Runnable {
    override def run(): Unit = {
      val millis = 1000
      while (!simulationStopRequested) {
        for (robot <- receivers ++ senders) {
          robot.progress(millis / 1000.0)
          println(robot.position)
        }
        Thread.sleep(millis)
      }
    }
  })

  def startRobots(): Unit = {
    for (robot <- receivers ++ senders)
      robot.startActor()
  }

  def startSimulation(): Unit ={
    simulationThread.start()
  }

  def start(): Unit = {
    startRobots()
    startSimulation()
  }

  def stopRobots(): Unit = {
    for (robot <- receivers ++ senders)
      robot.stopActor()
    Robot.system.shutdown()
  }

  def stopSimulation(): Unit = {
    simulationStopRequested = true
    simulationThread.join()
  }

  def stop(): Unit = {
    stopSimulation()
    stopRobots()
  }
}