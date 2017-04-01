
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
    
    // States obtained from the IS42/45R86400D/16320D/32160D datasheet, from the table "COMMAND TRUTH TABLE"
    val deviceDeselect :: noOperation :: burstStop :: read :: readWithAutoPrecharge :: write :: writeWithAutoPrecharge :: bankActivate :: prechargeSelectBank :: prechargeAllBanks :: cbrAutoRefresh :: selfRefresh :: modeRegisterSet :: Nil = Enum(UInt(), 13)
    val memState = Reg(init = noOperation)

    when(cmd === OcpCmd.RD) {
    
        // Read logic
        
    } .elsewhen (cmd === OcpCmd.WR) {
        
        // Write logic
        
    } .elsewhen (cmd === OcpCmd.IDLE) {

        // Idle logic

    }.otherwise {
    
        // Manage all the other OCP commands that at the moment of writing this are not implemented
    
    }
        
    when(memState === read) {
    
    
    
    } .elsewhen (memState === write) {


    
    } .elsewhen (memState === cbrAutoRefresh) {
    
    
    
    } .elsewhen (memState === burstStop) {

    
    
    } .elsewhen (memState === bankActivate) {
    
    

    } .elsewhen (memState === noOperation) {
        


    } .otherwise { 

        // Manage possible future cases
    
    }
}

object sdramControllerMain {
    def main(args: Array[String]): Unit = {
        val chiselArgs = args.slice(1,args.length)
        val ocpAddrWidth = args(0).toInt
        chiselMain(chiselArgs, () => Module(new SdramController(ocpAddrWidth)))
    }
}