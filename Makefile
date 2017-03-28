SBT = sbt

# Generate Verilog code
local:
	$(SBT) "run-main SdramController"

patmos:
	cp altde2-115.xml ~/t-crest/patmos/hardware/config/altde2-115.xml
	cp src/main/scala/SdramController.scala ~/t-crest/patmos/hardware/src/io/SdramController.scala
	cd ~/t-crest/patmos && make patmos
