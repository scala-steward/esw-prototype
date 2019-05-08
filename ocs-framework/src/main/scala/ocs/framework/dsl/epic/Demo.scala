package ocs.framework.dsl.epic

import ocs.framework.CswSystem

object Demo {

  def main(args: Array[String]): Unit = {
    val cswSystem = new CswSystem("demo")
    new TemperatureMonitor(cswSystem).refresh("init")
    new RemoteRepl(cswSystem).server().start()
  }

}
