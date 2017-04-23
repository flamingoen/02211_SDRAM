SRC=src/main/scala
SRC_TEST=src/test/scala

BOARD?=altde2-115_ramctrl
BOOTAPP?=bootable-bootloader

PAT_DIR=~/t-crest/patmos
PAT_HW_DIR=$(PAT_DIR)/hardware
PAT_DEV_DIR=$(PAT_HW_DIR)/src/io
PAT_TEST_DIR=$(PAT_DEV_DIR)/test

copydesign:
	cp config/altde2-115_ramctrl.xml $(PAT_HW_DIR)/config/altde2-115_ramctrl.xml
	cp $(SRC)/SdramController.scala $(PAT_DEV_DIR)/SdramController.scala
	cp $(SRC_TEST)/SdramControllerTester.scala $(PAT_TEST_DIR)/SdramControllerTester.scala

copyquartus:
	cp vhdl/patmos_de2-115_ramctrl.vhdl $(PAT_HW_DIR)/vhdl
	cp -r quartus/altde2-115_ramctrl/ $(PAT_HW_DIR)/quartus

patmos: copydesign copyquartus
	$(MAKE) $(PAT_DIR) patmos BOARD=$(BOARD)

gen: copydesign
	$(MAKE) -C $(PAT_DIR) gen BOARD=$(BOARD)

synth: copydesign copyquartus
	$(MAKE) -C $(PAT_DIR)/hardware synth_quartus BOOTAPP=$(BOOTAPP) BOARD=$(BOARD)

sdram: copydesign
	export MEMCTRL_ADDR_WIDTH=32 && cd $(PAT_HW_DIR) && make $(PAT_HW_DIR)/build/SdramController.v
