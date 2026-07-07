FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:rockchip-rk3588-rock-5t = " file://rock5t-rootfs file://udev"

do_install:append:rockchip-rk3588-rock-5t() {
    install -m 0755 ${WORKDIR}/udev ${D}/init.d/01-udev
}

# Override the base 90-rootfs after the recipe installs its own, so the ROCK 5T
# root-discovery fallback (wait for rootA / mmcblk0p4) wins. Keyed off the machine
# via a postfunc that only ships rock5t-rootfs when SRC_URI fetched it.
do_install[postfuncs] += "rock5t_rootfs_override"
rock5t_rootfs_override() {
    if [ -f "${WORKDIR}/rock5t-rootfs" ]; then
        install -m 0755 ${WORKDIR}/rock5t-rootfs ${D}/init.d/90-rootfs
    fi
}
