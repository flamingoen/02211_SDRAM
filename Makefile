SRC=src/main/scala

BOARD?=altde2-115_ramctrl
BOOTAPP?=bootable-bootloader

PAT_DIR=~/t-crest/patmos
PAT_HW_DIR=$(PAT_DIR)/hardware
PAT_DEV_DIR=$(PAT_HW_DIR)/src/io

copydesign:
	cp config/altde2-115_ramctrl.xml $(PAT_HW_DIR)/config/altde2-115_ramctrl.xml
	cp $(SRC)/SdramController.scala $(PAT_DEV_DIR)/SdramController.scala

copyquartus:
	cp vhdl/patmos_de2-115_ramctrl.vhdl $(PAT_HW_DIR)/vhdl
	cp -r quartus/altde2-115_ramctrl/ $(PAT_HW_DIR)/quartus

patmos: copydesign copyquartus
	$(MAKE) $(PAT_DIR) patmos BOARD=$(BOARD)

gen: copydesign
	$(MAKE) -C $(PAT_DIR) gen BOARD=$(BOARD)

synth: copydesign copyquartus
	$(MAKE) -C $(PAT_DIR)/hardware synth_quartus BOOTAPP=$(BOOTAPP) BOARD=$(BOARD)

sdram:
	cp $(SRC)/SdramController.scala $(PAT_DEV_DIR)/SdramController.scala
	cp copyToPatmosProject/Makefile $(PAT_HW_DIR)/Makefile
	export MEMCTRL_ADDR_WIDTH=32 && cd ~/t-crest/patmos/hardware && make ~/t-crest/patmos/hardware/build/SdramController.v
