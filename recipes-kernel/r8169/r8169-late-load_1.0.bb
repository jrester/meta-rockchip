SUMMARY = "Delayed r8169 load policy for ROCK 5T RTL8125B Ethernet"
DESCRIPTION = "Prevents early udev autoload of r8169, then loads it from systemd after root/userspace are stable."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit allarch systemd

SRC_URI = " \
    file://r8169-rock5t.conf \
    file://r8169-delayed-load.service \
    file://r8169-delayed-load \
"

do_install() {
    install -d ${D}${sysconfdir}/modprobe.d
    install -m 0644 ${WORKDIR}/r8169-rock5t.conf ${D}${sysconfdir}/modprobe.d/r8169-rock5t.conf

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/r8169-delayed-load.service ${D}${systemd_system_unitdir}/r8169-delayed-load.service

    install -d ${D}${sbindir}
    install -m 0755 ${WORKDIR}/r8169-delayed-load ${D}${sbindir}/r8169-delayed-load
}

FILES:${PN} = " \
    ${sysconfdir}/modprobe.d/r8169-rock5t.conf \
    ${systemd_system_unitdir}/r8169-delayed-load.service \
    ${sbindir}/r8169-delayed-load \
"

RDEPENDS:${PN} += "kernel-module-r8169"

SYSTEMD_SERVICE:${PN} = "r8169-delayed-load.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
