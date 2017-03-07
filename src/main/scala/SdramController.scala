
import Chisel._

class UserInterface extends Bundle {
    val cmd         = UInt(INPUT, 3)
    val addr        = UInt(INPUT, 25)
    val m_data      = UInt(INPUT, 32)
    val dataByteEn  = UInt(INPUT, 4)
    val dataValid   = UInt(INPUT, 1)
    
    val cmd_accept  = UInt(OUTPUT, 1)
    val data_accept = UInt(OUTPUT, 1)
    val s_data      = UInt(OUTPUT, 1)
    val resp        = UInt(OUTPUT, 1)
}

class MemoryInterface extends Bundle {
    val dq_o        = UInt(OUTPUT, 32)
    val dq_i        = dq_o.flip
    val dqm         = UInt(OUTPUT, 4)
    
    val clk         = UInt(OUTPUT, 1)
    val cke         = UInt(OUTPUT, 1)
    val ras         = UInt(OUTPUT, 1)
    val cas         = UInt(OUTPUT, 1)
    val we          = UInt(OUTPUT, 1)
    val cs          = UInt(OUTPUT, 1)
    val BA          = UInt(OUTPUT, 2)
    val addr        = UInt(OUTPUT, 13)
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
