# The meta-balena fatrw recipe fetches over SSH (protocol=ssh), which needs SSH keys
# in the build container. Rewrite the balena-os/fatrw git URL to HTTPS (public repo,
# no auth). Done as an anonymous python rewrite so it survives fatrw version bumps and
# changes to the trailing fetch params (nobranch/destsuffix). See plan 5.4.
python () {
    src = d.getVar('SRC_URI') or ''
    needle = 'git@github.com/balena-os/fatrw.git;protocol=ssh'
    if needle in src:
        src = src.replace('git://git@github.com/balena-os/fatrw.git;protocol=ssh',
                          'git://github.com/balena-os/fatrw.git;protocol=https')
        d.setVar('SRC_URI', src)
}
