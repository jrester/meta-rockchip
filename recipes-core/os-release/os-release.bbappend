# Rebrand the human-facing OS name. os-release.bb sets
#   NAME = "${DISTRO_NAME}"  and  PRETTY_NAME = "${DISTRO_NAME} ${VERSION}"
# so overriding DISTRO_NAME here renames /etc/os-release NAME and PRETTY_NAME.
# ID ("${DISTRO}") and VERSION_ID are NOT derived from DISTRO_NAME, so balenaCloud
# HUP/supervisor OS identity is unaffected by this cosmetic rename.
DISTRO_NAME = "Edge OS"
