package scalatris.clientAI

import scalatris.clientAI.network._
import scalatris.server.network._
import scalatris.lib._

import java.lang.Thread

object AIClient {
  def main(args: Array[String]) {
    val grid = new TetrisGrid(10,20)

    val tcpHandler = new TCPClientHandler(grid)
    val tcpPF = new TCPServerPipelineFactory(tcpHandler)
    val tcpClient = new TCPClient(tcpPF)

    var aig = new AIgrid(grid)

    while (!grid.gameLost) {
      while (!grid.shapeChanged) {
        java.lang.Thread.sleep(100)
      }

      aig.shape = grid.shape
      grid.shapeChanged = false // on a pris en compte la nouvelle forme

      aig = AI.computeAI (new AIgrid(aig))

      aig.printGrid
      aig.moveList.foreach(d => {
        tcpHandler.send(d.toString) // on envoie les directions au serveur
        grid.move(d)}) // on applique les directions sur la grille locale
      tcpHandler.send("fall") // on fait tomber la piece sur le serveur
      grid.fall               // et sur la grille locale
    }
  }
}
