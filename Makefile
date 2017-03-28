SBT = sbt

# Generate Verilog code
local:
	$(SBT) "run-main SdramController"

patmos:
	cd ~/t-crest/patmos && make patmos
