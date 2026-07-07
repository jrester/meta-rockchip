# Mainline U-Boot v2025.04 for the Radxa ROCK 5T (RK3588).
#
# Reproduces from source the armbian vendor bootloader blob that the NixOS
# Rock 5B+/5T reference boots on real hardware. Blueprint (armbian-build):
#   config/boards/rock-5t.conf -> post_family_config__rock5t_use_mainline_uboot:
#     BOOTSOURCE = https://github.com/u-boot/u-boot.git
#     BOOTBRANCH = tag:v2025.04
#     BOOTCONFIG = rock5t-rk3588_defconfig   (added by the 000x patches below)
#     UBOOT_TARGET_MAP = "BL31=<rkbin bl31> ROCKCHIP_TPL=<rkbin ddr> ;; u-boot-rockchip.bin"
#   config/sources/families/include/rockchip64_common.inc (rk3588):
#     DDR_BLOB  = rk35/rk3588_ddr_lp4_2112MHz_lp5_2400MHz_v1.18.bin
#     BL31_BLOB = rk35/rk3588_bl31_v1.48.elf
#
# binman assembles the idbloader (TPL/DDR + SPL) and the U-Boot FIT (U-Boot proper
# + BL31/ATF) into a single u-boot-rockchip.bin with the IDB header at offset 0, so
# the image dd's it once at sector 64. No GPT scan / gunzip like the old radxa 2017.09
# split flow that exhausted the SPL malloc pool.

require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc

COMPATIBLE_MACHINE = "rockchip-rk3588-rock-5t"

# The bootloader for rock-5t. The machine conf pins
# PREFERRED_PROVIDER_virtual/bootloader = "u-boot-rock5t" so this wins over the radxa
# 2017.09 u-boot-rockchip recipe (which stays for the other Radxa boards).
PROVIDES += "virtual/bootloader"

# binman + dtoc need pyelftools; FIT assembly needs openssl/gnutls on the host.
DEPENDS += "python3-pyelftools-native openssl-native gnutls-native"

PV = "2025.04"

# u-boot-common.inc pins its own SRCREV/SRC_URI (denx master + a CVE patch); override
# both for the mainline v2025.04 tag plus the rkbin blobs and armbian rock-5t patches.
SRCREV_uboot = "9f8c7a44c3812e4c781b5ce9a0400eebc218136e"
SRCREV_rkbin = "a45caf5db84fddb3422142a77cf2b50336f11161"
SRCREV_FORMAT = "uboot_rkbin"

SRC_URI = " \
    git://github.com/u-boot/u-boot.git;protocol=https;branch=master;name=uboot \
    git://github.com/radxa/rkbin.git;protocol=https;branch=develop-v2024.10;name=rkbin;destsuffix=rkbin \
    file://board_rock-5t/0000-Prior-Upstream-DT-hacks.patch \
    file://board_rock-5t/0001-Add-Rock5T-dt-modifications.patch \
    file://board_rock-5t/0002-Add-Rock5T-dts-to-upstream-dir.patch \
    file://board_rock-5t/0003-Add-Rock5T-support.patch \
"

# v2025.04 Licenses/README may differ from the version u-boot-common.inc pins; if the
# build reports a checksum mismatch, update this md5 to the value bitbake prints.
LIC_FILES_CHKSUM = "file://Licenses/README;md5=2ca5f2c35c8cc335f0a19756634782f1"

S = "${WORKDIR}/git"

# UBOOT_MACHINE (= rock5t-rk3588_defconfig, added by 0003-Add-Rock5T-support.patch) comes
# from the machine conf. Deploy the binman single-blob output.
UBOOT_BINARY = "u-boot-rockchip.bin"
UBOOT_IMAGE = "u-boot-rockchip-${MACHINE}-${PV}-${PR}.bin"
UBOOT_SYMLINK = "u-boot-rockchip-${MACHINE}.bin"

# Feed the rkbin DDR init (TPL) and ATF (BL31) to binman, like armbian's UBOOT_TARGET_MAP.
# Paths/versions are what radxa/rkbin develop-v2024.10 actually ships (blobs live under
# bin/rk35/; it carries BL31 v1.47, not armbian's v1.48 — close enough for RK3588 ATF).
DDR_BLOB = "bin/rk35/rk3588_ddr_lp4_2112MHz_lp5_2400MHz_v1.18.bin"
BL31_BLOB = "bin/rk35/rk3588_bl31_v1.47.elf"
EXTRA_OEMAKE:append = " ROCKCHIP_TPL=${WORKDIR}/rkbin/${DDR_BLOB} BL31=${WORKDIR}/rkbin/${BL31_BLOB}"

# resin-boot FAT layout: extlinux.conf tells U-Boot's distro_bootcmd how to boot.
# Generated here (was in u-boot-rockchip.bbappend) so this recipe fully replaces the
# old 2017.09 bootloader for rock-5t. Kernel args follow the NixOS reference.
do_deploy:append() {
    touch ${DEPLOYDIR}/extra_uEnv.txt

    mkdir -p ${DEPLOYDIR}/extlinux
    cat > ${DEPLOYDIR}/extlinux/extlinux.conf << 'EXTLINUX_EOF'
DEFAULT balena
LABEL balena
  KERNEL /Image-initramfs-rockchip-rk3588-rock-5t.bin
  FDT /rk3588-rock-5t.dtb
  APPEND balena_stage2 root=/dev/disk/by-state/active rootwait rootfstype=ext4 rw earlycon console=tty1 console=ttyS2,1500000n8 coherent_pool=2M irqchip.gicv3_pseudo_nmi=0 systemd.log_target=kmsg
EXTLINUX_EOF
}
