SBT = sbt

# Generate Verilog code
controller:
	$(SBT) "run-main SdramController"
