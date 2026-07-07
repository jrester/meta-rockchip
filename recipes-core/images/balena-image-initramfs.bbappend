# Keep ROCK 5T PCIe Ethernet out of initramfs udev autoload. The main rootfs
# still installs r8169-late-load, which explicitly loads r8169 after userspace.
PACKAGE_INSTALL:append:rockchip-rk3588-rock-5t = " r8169-initramfs-blacklist"

# balena's default initramfs cap (IMAGE_ROOTFS_MAXSIZE = 32768K) is tuned for smaller
# boards; the base aarch64/scarthgap initramfs (busybox + udev + cryptsetup + kexec +
# gptfdisk) is ~48MB here. Raise the cap for ROCK 5T. The initramfs is bundled into the
# kernel Image, so this feeds into the boot-partition sizing in balena-image.bbappend.
IMAGE_ROOTFS_MAXSIZE:rockchip-rk3588-rock-5t = "65536"
