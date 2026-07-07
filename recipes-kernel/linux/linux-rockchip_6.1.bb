# Copyright (C) 2024, Rockchip Electronics Co., Ltd
# Released under the MIT license (see COPYING.MIT for the terms)

require recipes-kernel/linux/linux-yocto.inc
require linux-rockchip.inc

inherit local-git

# armbian/linux-rockchip rk-6.1-rkr5.1 (6.1.115). This is the tree the NixOS Rock 5B+/5T
# reference builds and runs on real hardware with working GPU/NIC/WiFi + docker, so we use
# it instead of radxa/kernel linux-6.1-stan-rkr4.1-buildroot. The full kernel config comes
# from that same NixOS reference (see linux-rockchip_6.1.bbappend, rock-5t only).
# Refresh with:
#   git ls-remote https://github.com/armbian/linux-rockchip refs/heads/rk-6.1-rkr5.1
SRCREV = "b908c7339f51eddcfe8402cd15d1e1f8f4e67c29"
SRC_URI = " \
	git://github.com/armbian/linux-rockchip.git;protocol=https;branch=rk-6.1-rkr5.1; \
	file://${THISDIR}/files/cgroups.cfg \
"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

KERNEL_VERSION_SANITY_SKIP = "1"
LINUX_VERSION ?= "6.1"

SRC_URI:append = " ${@bb.utils.contains('IMAGE_FSTYPES', 'ext4', \
		   'file://${THISDIR}/files/ext4.cfg', \
		   '', \
		   d)}"

EXTRA_OEMAKE += "KCFLAGS=-Wno-error"
