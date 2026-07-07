# rtw89: Realtek RTL8852BE WiFi 6 PCIe firmware (PCI ID 10ec:b852, AzureWave on ROCK 5T)
# Trailing '*' on every path is required: balena-linux-firmware.bbclass xz-compresses
# all firmware (do_firmware_compression), so the deployed files are *.bin.xz / *.fw.xz.
# Exact names without the '*' match nothing at do_package -> empty package -> opkg
# "No candidates to install". Every meta-balena FILES entry uses the same trailing '*'.
PACKAGES =+ "${PN}-rtw89 ${PN}-rtl8125b"

LICENSE:${PN}-rtw89 = "WHENCE"
RDEPENDS:${PN}-rtw89 += "${PN}-whence-license"
FILES:${PN}-rtw89 = " \
    ${nonarch_base_libdir}/firmware/rtw89/rtw8852b_fw.bin* \
    ${nonarch_base_libdir}/firmware/rtw89/rtw8852b_fw-1.bin* \
"

# rtl8125b: firmware for RTL8125B 2.5GbE NIC (two ports on ROCK 5T)
LICENSE:${PN}-rtl8125b = "WHENCE"
RDEPENDS:${PN}-rtl8125b += "${PN}-whence-license"
FILES:${PN}-rtl8125b = " \
    ${nonarch_base_libdir}/firmware/rtl_nic/rtl8125b-1.fw* \
    ${nonarch_base_libdir}/firmware/rtl_nic/rtl8125b-2.fw* \
"
