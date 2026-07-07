SUMMARY = "Initramfs r8169 autoload policy for ROCK 5T"
DESCRIPTION = "Installs a modprobe blacklist into initramfs so udev does not probe ROCK 5T RTL8125B Ethernet during active-root discovery."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit allarch

SRC_URI = "file://r8169-initramfs-blacklist.conf"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${sysconfdir}/modprobe.d
    install -m 0644 ${WORKDIR}/r8169-initramfs-blacklist.conf ${D}${sysconfdir}/modprobe.d/r8169-initramfs-blacklist.conf
}

FILES:${PN} = "${sysconfdir}/modprobe.d/r8169-initramfs-blacklist.conf"
