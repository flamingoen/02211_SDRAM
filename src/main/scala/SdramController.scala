
import Chisel._

/*
OCPburst 
References: 
- Section 3.6.1 of the Patmos Handbook
- Code from the file "patmos_de2-115-sdram.vhdl" located in https://github.com/t-crest/patmos
- Specification of OpenCoreProtocol (OCP) located in http://www.accellera.org/images/downloads/standards/ocp/OCP_3.0_Specification.zip
*/
class UserInterface extends Bundle {
    //Master
    val mCmd        = UInt(INPUT, 3)    //Command
    val mAddr       = UInt(INPUT, 27)   //Address, byte-based, lowest two bits always 0
    val mData       = UInt(INPUT, 32)   //Data for writes, 32 bits
    val mDataByteEn = UInt(INPUT, 4)    //Byte enables for sub-word writes, 4 bits
    val mDataValid  = UInt(INPUT, 1)    //Signal that data is valid, 1 bit

    //Slave
    val sResp       = UInt(OUTPUT, 2)   //Response
    val sData       = UInt(OUTPUT, 32)  //Data for reads, 32 bits
    val sCmdAccept  = UInt(OUTPUT, 1)   //Signal that command is accepted, 1 bit
    val sDataAccept = UInt(OUTPUT, 1)   //Signal that data is accepted, 1 bit
}

/*
References:
- Figure 4-34 from the DE2-115 User Manual
- ISSI IS42S16320D SDRAM datasheet
*/
class MemoryInterface extends Bundle {
    val dqO         = UInt(OUTPUT, 32)  //Data I/O
    val dqI         = dqO.flip         	//Data I/O
    val dqm         = UInt(OUTPUT, 4)   //x32 Input/Output Mask
    
    val cke         = UInt(OUTPUT, 1)   //The CKE input determines whether the CLK input is enabled. The next rising edge of the CLK signal will be valid when is CKE HIGH and invalid when LOW. When CKE is LOW, the device will be in either power-down mode, clock suspend mode, or self refresh mode. CKE is an asynchronous input.
    val clk         = UInt(OUTPUT, 1)   //CLK is the master clock input for this device. Except for CKE, all inputs to this device are acquired in synchronization with the rising edge of this pin.
    val csN         = UInt(OUTPUT, 1)   //The CS input determines whether command input is enabled within the device. Command input is enabled when CS is LOW, and disabled with CS is HIGH. The device remains in the previous state when CS is HIGH    
    val ba          = UInt(OUTPUT, 2)   //Bank Select Address       
    val addr        = UInt(OUTPUT, 13)  //Address
    //Device command
    val weN         = UInt(OUTPUT, 1)   //WE, in conjunction with RAS and CAS, forms the device command. See the "Command Truth Table" item for details on device commands
    val casN        = UInt(OUTPUT, 1)   //CAS, in conjunction with the RAS and WE, forms the device command. See the "Command Truth Table" for details on device commands
    val rasN        = UInt(OUTPUT, 1)   //RAS, in conjunction with CAS and WE, forms the device command. See the "Command Truth Table" item for details on device commands
}

class SdramController extends Module {
  val io = new Bundle {
    val userInterface = new UserInterface();
    val memInterface = new MemoryInterface();
  }
}

/**
 * An object containing a main() to invoke chiselMain()
 * to generate the Verilog code.
 */
object SdramController {
  def main(args: Array[String]): Unit = {
    chiselMain(Array("--backend", "v"), () => Module(new SdramController()))
  }
}
