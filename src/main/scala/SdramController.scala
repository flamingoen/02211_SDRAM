
package io

import scala.math._

import Chisel._
import Node._

import ocp._
import patmos.Constants._

object SdramController extends DeviceObject {
  private val sdramAddrWidth = 13
  var sdramDataWidth = 32
  var ocpAddrWidth   = 25

  def init(params: Map[String, String]) = {
    sdramDataWidth = getPosIntParam(params, "sdramDataWidth")
    ocpAddrWidth   = getPosIntParam(params, "ocpAddrWidth")
  }

  def create(params: Map[String, String]): SdramController = {
    Module(new SdramController(ocpAddrWidth))
  }

  trait Pins {
    val sdramControllerPins = new Bundle {
      val ramOut = new Bundle {
        val dq   = Bits(OUTPUT, width = sdramDataWidth)
        val dqm  = Bits(OUTPUT, width = 4)
        val addr = Bits(OUTPUT, width = sdramDataWidth)
        val ba   = Bits(OUTPUT, width = 2)
        val clk  = Bits(OUTPUT, width = 1)
        val cke  = Bits(OUTPUT, width = 1)
        val ras  = Bits(OUTPUT, width = 1)
        val cas  = Bits(OUTPUT, width = 1)
        val we   = Bits(OUTPUT, width = 1)
        val cs   = Bits(OUTPUT, width = 1)
      }
      val ramIn = new Bundle {
      val dq    = Bits(INPUT, width = sdramAddrWidth)
      }
    }
  }
}

class SdramController(ocpAddrWidth: Int, burstLen : Int) extends BurstDevice(ocpAddrWidth) {
  override val io = new BurstDeviceIO(ocpAddrWidth) with SdramController.Pins
  val cmd         = io.ocp.M.Cmd
  // Controller states
  val idle :: write :: read :: Nil = Enum(UInt(), 3)
  val state = Reg(init = idle);
  // counter used for burst
  val burstCount = burstLen

  // Examples of use of the encoder/decoder of memCmd. We just need to send orders to the memory, so the decode function may be not used in the future
  val memCmd      = MemCmd.decode(
                    clk = io.sdramControllerPins.ramOut.clk,
                    cs  = io.sdramControllerPins.ramOut.cs,
                    ras = io.sdramControllerPins.ramOut.ras,
                    cas = io.sdramControllerPins.ramOut.cas,
                    we  = io.sdramControllerPins.ramOut.we,
                    ba  = io.sdramControllerPins.ramOut.ba,
                    a10 = io.sdramControllerPins.ramOut.addr(10)
                  )

  // We need to provide a default value for the full word in order to be able to do subword assignation. This is to avoid the "Subword assignment requires a default value to have been assigned" chisel error
  io.sdramControllerPins.ramOut.addr := UInt(0)

  MemCmd.encode(
    memCmd = MemCmd.read,
    clk    = io.sdramControllerPins.ramOut.clk,
    cs     = io.sdramControllerPins.ramOut.cs,
    ras    = io.sdramControllerPins.ramOut.ras,
    cas    = io.sdramControllerPins.ramOut.cas,
    we     = io.sdramControllerPins.ramOut.we,
    ba     = io.sdramControllerPins.ramOut.ba,
    a10    = io.sdramControllerPins.ramOut.addr(10)
  ) 
  
  // state machine for the ocp signal
  when(state === idle) {
    
    when (cmd === OcpCmd.RD) {

        // Send ACT signal to mem where addr = OCP addr 22-13, ba1 = OCP addr 24, ba2 = OCP addr 23
        // reset burst counter
        // io.ocp.S.CmdAccept should be low
        // Set next state to read

    }
    
    .elsewhen (cmd === OcpCmd.WR) {

        // Send ACT signal to mem where addr = OCP addr 22-13, ba1 = OCP addr 24, ba2 = OCP addr 23
        // reset burst counter
        // io.ocp.S.CmdAccept should be low
        // Set next state to write

    }
    
    .elsewhen (cmd === OcpCmd.IDLE) {

        // Send Nop
        // Set next state to idle

    }
    
    .otherwise {
    
        // Manage all the other OCP commands that at the moment of writing this are not implemented
        
    }
  } 
  
  .elsewhen (state === write) {
  
    // Send write signal to memCmd with address and AUTO PRECHARGE enabled
    // set io.ocp.S.CmdAccept to HIGH
    // set io.ocp.S.SDataAccept to HIGH
    
    // if burstCounter > 0
        // burstcounter--
        // set next address = addr + 4
        // set next state to write
    // else set next state to idle
  
  } 
  
  .elsewhen (state === read) {
  
    // Send read signal to memCmd with address and AUTO PRECHARGE enabled
    
    // if burstCounter > 0
        // burstcounter--
        // set next address = addr + 4
        // set next state to read
    
    // after 2 cycles
        // set io.ocp.S.CmdAccept to HIGH
        
    // after 6 cycles
        // set next state to idle
    // before that set next state to read
  
  } 
  
  .otherwise { 
  
    // Not used - could be replaced by one of the states
    
  }
  
  
// Not sure what to do with this
//
//   when(memCmd === MemCmd.read) {
// 
//   }.elsewhen(memCmd === MemCmd.write) {
// 
//   }.elsewhen(memCmd === MemCmd.cbrAutoRefresh) {
// 
//   }.elsewhen(memCmd === MemCmd.burstStop) {
// 
//   }.elsewhen(memCmd === MemCmd.bankActivate) {
// 
//   }.elsewhen(memCmd === MemCmd.noOperation) {
// 
//   }.otherwise {
// 
//     // Manage possible future cases
// 
//   }

}

object MemCmd {
  // States obtained from the IS42/45R86400D/16320D/32160D datasheet, from the table "COMMAND TRUTH TABLE"
  val deviceDeselect :: noOperation :: burstStop :: read :: readWithAutoPrecharge :: write :: writeWithAutoPrecharge :: bankActivate :: prechargeSelectBank :: prechargeAllBanks :: cbrAutoRefresh :: selfRefresh :: modeRegisterSet :: Nil = Enum(UInt(), 13)
  
  // Syntactic sugar
  private val high = Bits(1)
  private val low  = Bits(0)

  /* 
    According to the datasheet there are other signals not considered in this function. This is why:
      - clk(n-1): in all cases of the "COMMAND TRUTH TABLE" is high
      - A12, A11, A9 to A0: data is allways going to be high or low, allways valid values
      - Valid states are not considered: they are allways going to be valid (it is not possible to have a signal with a value between low and high)
  */
  def decode(clk: Bits, cs:Bits, ras:Bits, cas:Bits, we:Bits, ba:Bits, a10:Bits): UInt = {
    val reg  = Reg(UInt())

    when(cs === high) {
      reg := deviceDeselect
    }.elsewhen(cs === low && ras === high && cas === high && we === high) {
      reg := noOperation
    }.elsewhen(cs === low && ras === high && cas === high && we === low) {
      reg := burstStop
    }.elsewhen(cs === low && ras === high && cas === low && we === high && a10 === low) {
      reg := read
    }.elsewhen(cs === low && ras === high && cas === low && we === high && a10 === high) {
      reg := writeWithAutoPrecharge
    }.elsewhen(cs === low && ras === high && cas === low && we === low && a10 === low) {
      reg := write
    }.elsewhen(cs === low && ras === high && cas === low && we === low && a10 === high) {
      reg := writeWithAutoPrecharge
    }.elsewhen(cs === low && ras === low && cas === high && we === high) {
      reg := bankActivate
    }.elsewhen(cs === low && ras === low && cas === high && we === low && a10 === low) {
      reg := prechargeSelectBank
    }.elsewhen(cs === low && ras === low && cas === high && we === low && a10 === high) {
      reg := prechargeAllBanks
    }.elsewhen(clk === high && cs === low && ras === low && cas === low && we === high) {
      reg := cbrAutoRefresh
    }.elsewhen(clk === low && cs === low && ras === low && cas === low && we === high) {
      reg := selfRefresh
    }.elsewhen(cs === low && ras === low && cas === low && we === low && ba(0) === low && ba(1) === low && a10 === low) {
      reg := modeRegisterSet
    }.otherwise {
      // Entering here is an error
      reg := deviceDeselect
    }

    return reg
  }

  def encode(memCmd: UInt, clk: Bits, cs:Bits, ras:Bits, cas:Bits, we:Bits, ba:Bits, a10:Bits) = {
    when(memCmd === deviceDeselect) {
      cs := high
    }.elsewhen(memCmd === noOperation) {
      cs := low; ras := high; cas := high; we := high
    }.elsewhen(memCmd === burstStop) {
      cs := low; ras := high; cas := high; we := low
    }.elsewhen(memCmd === read) {
      cs := low; ras := high; cas := low; we := high; a10 := low
    }.elsewhen(memCmd === writeWithAutoPrecharge) {
      cs := low; ras := high; cas := low; we := high; a10 := high
    }.elsewhen(memCmd === write) {
      cs := low; ras := high; cas := low; we := low; a10 := low
    }.elsewhen(memCmd === writeWithAutoPrecharge) {
      cs := low; ras := high; cas := low; we := low; a10 := high
    }.elsewhen(memCmd === bankActivate) {
      cs := low; ras := low; cas := high; we := high
    }.elsewhen(memCmd === prechargeSelectBank) {
      cs := low; ras := low; cas := high; we := low; a10 := low
    }.elsewhen(memCmd === prechargeAllBanks) {
      cs := low; ras := low; cas := high; we := low; a10 := high
    }.elsewhen(memCmd === cbrAutoRefresh) {
      clk := high; cs := low; ras := low; cas := low; we := high
    }.elsewhen(memCmd === selfRefresh) {
      clk := low; cs := low; ras := low; cas := low; we := high
    }.elsewhen(memCmd === modeRegisterSet) {
      cs := low; ras := low; cas := low; we := low; ba := low; a10 := low
    }.otherwise {
      // Entering here is an error
    }
  }
}

object sdramControllerMain {
  def main(args: Array[String]): Unit = {
    val chiselArgs   = args.slice(1, args.length)
    val ocpAddrWidth = args(0).toInt
    chiselMain(chiselArgs, () => Module(new SdramController(ocpAddrWidth)))
  }
}
