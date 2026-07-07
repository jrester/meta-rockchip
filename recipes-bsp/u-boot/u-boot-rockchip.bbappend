# meta-balena balena-image.bb appends extra_uEnv.txt to BALENA_BOOT_PARTITION_FILES
# whenever UBOOT_MACHINE is set. This file is normally created by resin-u-boot.bbclass
# (inherited via meta-balena u-boot_%.bbappend), but u-boot-rockchip does not use the
# standard u-boot recipe, so we create the empty file here explicitly.
# Note: rock-5t uses the u-boot-rock5t recipe (mainline v2025.04) instead of this one, so
# its extra_uEnv.txt + extlinux.conf are generated there, not here. This append covers the
# other Radxa boards that still use u-boot-rockchip.
do_deploy:append() {
    touch ${DEPLOYDIR}/extra_uEnv.txt
}
