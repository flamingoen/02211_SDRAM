patmos:
	cp copyToPatmosProject/altde2-115.xml ~/t-crest/patmos/hardware/config/altde2-115.xml
	cp src/main/scala/SdramController.scala ~/t-crest/patmos/hardware/src/io/SdramController.scala
	cd ~/t-crest/patmos && make patmos

sdram:
	cp src/main/scala/SdramController.scala ~/t-crest/patmos/hardware/src/io/SdramController.scala
	cp copyToPatmosProject/Makefile ~/t-crest/patmos/hardware/Makefile
	export MEMCTRL_ADDR_WIDTH=32 && cd ~/t-crest/patmos/hardware && make /home/patmos/t-crest/patmos/hardware/build/SdramController.v
