
package io

import scala.math._

import Chisel._
import Node._

import ocp._
import patmos.Constants._

object SdramController extends DeviceObject {
     var sdramAddrWidth  = 13   
     var sdramDataWidth  = 32
     var ocpAddrWidth    = 25
    
     def init(params: Map[String, String]) = {
         sdramAddrWidth = getPosIntParam(params, "sdramAddrWidth")
         sdramDataWidth = getPosIntParam(params, "sdramDataWidth")
         ocpAddrWidth = getPosIntParam(params, "ocpAddrWidth")
     }

    def create(params: Map[String, String]) : SdramController = {
        Module(new SdramController(ocpAddrWidth))
    }
    
    trait Pins {
        val sdramControllerPins = new Bundle {
            val ramOut = new Bundle {
                val dq      = Bits(OUTPUT , width = sdramDataWidth)
                val dqm     = Bits(OUTPUT , width = 4)
                val addr    = Bits(OUTPUT , width = sdramDataWidth)
                val ba      = Bits(OUTPUT , width = 2)
                val clk     = Bits(OUTPUT , width = 1)
                val cke     = Bits(OUTPUT , width = 1)
                val ras     = Bits(OUTPUT , width = 1)
                val cas     = Bits(OUTPUT , width = 1)
                val we      = Bits(OUTPUT , width = 1)
                val cs      = Bits(OUTPUT , width = 1)
            }
            val ramIn = new Bundle {
                val dq      = Bits(INPUT , width = sdramAddrWidth)
            }
        }
    }
}


class SdramController(ocpAddrWidth : Int) extends BurstDevice(ocpAddrWidth) {
    override val io = new BurstDeviceIO(ocpAddrWidth) with SdramController.Pins
    
    val cmd = io.ocp.M.Cmd
    
    val read :: write :: precharge :: auto_precharge :: auto_refresh_command :: burst_terminate :: command_inhibit :: no_operation :: load_mode_register :: active_command :: Nil = Enum(UInt(), 10)
    val SDState = Reg(init = no_operation)
    
    when(cmd === OcpCmd.RD) {
    
        // Read logic
        
    } .elsewhen (cmd === OcpCmd.WR) {
        
        // Write logic
        
    } .otherwise { //Assumes cmd === OcpCmd.IDLE
    
        // Idle logic here
    
    }
    
    
    SDState := no_operation
    
    when(SDState === read) {
    
    
    
    } .elsewhen (SDState === write) {
    
    
    
    } .elsewhen (SDState === precharge) {
    
    
    
    } .elsewhen (SDState === auto_precharge) {
    
    
    
    } .elsewhen (SDState === auto_refresh_command) {
    
    
    
    } .elsewhen (SDState === burst_terminate) {
    
    
    
    } .elsewhen (SDState === command_inhibit) {
    
    
    
    } .elsewhen (SDState === load_mode_register) {
    
    
    
    } .elsewhen (SDState === active_command) {
    
    
    
    } 
    .otherwise { // Assumes SDState === no_operation
    
    
    
    }
}

object sdramControllerMain {
    def main(args: Array[String]): Unit = {
        val chiselArgs = args.slice(1,args.length)
        val ocpAddrWidth = args(0).toInt
        chiselMain(chiselArgs, () => Module(new SdramController(ocpAddrWidth)))
    }
}
