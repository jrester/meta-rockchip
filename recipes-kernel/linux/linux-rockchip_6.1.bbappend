FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}_6.1:"

SRC_URI:append = " file://overlay.cfg file://docker.cfg"

# ROCK 5T uses the NixOS reference's full kernel config (armbian rk-6.1-rkr5.1, known to
# boot with working GPU/NIC/WiFi + docker). The armbian tree does not ship
# rk3588_linux_defconfig, so linux-rockchip.inc's defconfig merge would produce an empty
# config. Install the NixOS config as the KBUILD_DEFCONFIG file the recipe expects; the
# subsequent `make ${KBUILD_DEFCONFIG}` (KCONFIG_MODE=--alldefconfig) seeds .config from it.
SRC_URI:append:rockchip-rk3588-rock-5t = " file://rk35xx_vendor_config"
do_kernel_metadata:prepend:rockchip-rk3588-rock-5t() {
    install -D -m 0644 ${WORKDIR}/rk35xx_vendor_config \
        ${S}/arch/${ARCH}/configs/${KBUILD_DEFCONFIG}
}

# Build a plain Image (as the NixOS reference does), not the Rockchip <dtb>.img make
# target. armbian's tree is not guaranteed to carry the rockchip *.img Makefile targets,
# and the balena extlinux.conf loads Image-initramfs directly anyway.
ROCKCHIP_KERNEL_IMAGES:rockchip-rk3588-rock-5t = "0"

# Rockchip sets KERNEL_IMAGETYPE_FOR_MAKE = "rk3588-rock-5t.img" so do_bundle_initramfs
# creates rk3588-rock-5t.img.initramfs but NOT Image.initramfs, boot.img.initramfs, or
# zboot.img.initramfs. The generic kernel_do_deploy iterates KERNEL_IMAGETYPES (which
# includes Image, boot.img, zboot.img) and tries to install .initramfs variants for each.

do_bundle_initramfs:append() {
    if [ ! -z "${INITRAMFS_IMAGE}" ] && [ x"${INITRAMFS_IMAGE_BUNDLE}" = x1 ]; then
        if [ -e "${B}/arch/${ARCH}/boot/Image" ] && [ ! -e "${B}/arch/${ARCH}/boot/Image.initramfs" ]; then
            ln -sf Image "${B}/arch/${ARCH}/boot/Image.initramfs"
            bbnote "Rockchip: linked Image.initramfs -> Image for do_deploy"
        fi
    fi
}

do_deploy:prepend() {
    if [ ! -z "${INITRAMFS_IMAGE}" ] && [ x"${INITRAMFS_IMAGE_BUNDLE}" = x1 ]; then
        for imgtype in boot.img zboot.img; do
            if [ -e "${B}/arch/${ARCH}/boot/${imgtype}" ] && [ ! -e "${B}/arch/${ARCH}/boot/${imgtype}.initramfs" ]; then
                ln -sf ${imgtype} "${B}/arch/${ARCH}/boot/${imgtype}.initramfs"
                bbnote "Rockchip: linked ${imgtype}.initramfs -> ${imgtype} for do_deploy"
            fi
        done
    fi
}
