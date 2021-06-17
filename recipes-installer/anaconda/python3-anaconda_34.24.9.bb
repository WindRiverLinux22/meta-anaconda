SUMMARY = "The anaconda package"
DESCRIPTION = "The anaconda package"
HOMEPAGE = "http://fedoraproject.org/wiki/Anaconda"
LICENSE = "GPLv2"
SECTION = "devel"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "e2fsprogs gettext libarchive \
           pango python3 rpm \
           "

DEPENDS += "libxklavier glade libxml2-native \
            gdk-pixbuf-native \
            "

S = "${WORKDIR}/git"

RDEPENDS_${PN} = "e2fsprogs e2fsprogs-e2fsck e2fsprogs-mke2fs \
                   e2fsprogs-tune2fs e2fsprogs-resize2fs \
                   ntfsprogs xfsprogs btrfs-tools nfs-utils-client \
                   parted dosfstools gzip libarchive lvm2 \
                   squashfs-tools openssh python3-core python3-misc \
                   python3-modules  python3-dbus python3-pyparted \
                   python3-pykickstart \
                   dmidecode python3-meh python3-libreport localedef \
                   python3-pygobject python3-rpm grub usermode tigervnc \
                   tzdata tzdata-misc tzdata-posix tzdata-right tzdata-africa \
                   tzdata-americas tzdata-antarctica tzdata-arctic tzdata-asia \
                   tzdata-atlantic tzdata-australia tzdata-europe tzdata-pacific \
                   keybinder module-init-tools dnf util-linux efibootmgr \
                   ca-certificates isomd5sum \
                   btrfs-tools ntfs-3g iproute2 mdadm shadow \
                   util-linux-swaponoff util-linux-uuidgen python3-blivet \
                   xrandr glibc-charmaps glibc-localedatas \
                   python3-pytz python3-langtable libpwquality \
                   python3-ntplib libgnomekbd libtimezonemap \
                   procps rsync glibc-utils python3-pid \
                   python3-ordered-set python3-wrapt python3-coverage \
                   python3-requests-file python3-requests-ftp \
                   python3-blivetgui librsvg librsvg-gtk bash \
                   python3-systemd python3-pydbus python3-simpleline \
                   python3-productmd python3-dasbus libxkbcommon \
                "

RDEPENDS_${PN} += "networkmanager \
                   network-manager-applet \
"

SRC_URI = "git://github.com/rhinstaller/anaconda;protocol=https;branch=f34-release \
           file://81-edit-sudoers.ks \
           file://81-systemd-preset-all.ks \
           file://81-add-env-file-for-sshd.ks \
           file://0001-do-not-build-po-and-doc.patch \
           file://0002-do-not-load-the-system-wide-Xresources.patch \
           file://0003-tweak-iso-mount-dir-and-kernel-name.patch \
           file://0004-customize-default-option-for-OE.patch \
           file://0010-reboot-unconditionally.patch \
           file://0011-disable-audit.patch \
           file://0017-invisible-help-button.patch \
           file://0018-disable-non-implemented-functions.patch \
           file://0023-do-not-support-ISO-hold-by-hard-drive-partitions.patch \
           file://0024-fix-cannot-stat-usr-share-gettext-gettext.h.patch \
           file://0030-detect-existence-of-Xorg.patch \
           file://0031-fix-write-network-conf-failed-while-liveinst.patch \
           file://0038-support-to-get-kickstart-from-network.patch \
           file://0041-live-install-supports-kickstart.patch \
           file://0045-installation-destination-disable-iSCSI-network-disks.patch \
           file://0046-update-region-while-city-changes.patch \
           file://0047-remove-incorrect-prefix-of-addon-repo-url.patch \
           file://0048-fix-write-sysconfig-network-failed.patch \
           file://0049-pyanaconda-ui-gui-utils.py-tweak-mouse-arrow.patch \
           file://0058-tweak-product-short-name.patch \
           file://0060-tweak-shebang-of-bash.patch \
           file://0064-users.py-Fix-multilib-user-creation-error.patch \
           file://0068-disable-chronyd.patch \
           file://0069-disable-location-detection-based-on-IP-address.patch \
           file://0001-pyanaconda-payload-Fix-xserver-detection.patch \
          "

SRCREV = "43ce5dfb0566d14926bfb60e11564a656829e92d"

FILES_${PN}-dbg += "${libexecdir}/anaconda/.debug ${PYTHON_SITEPACKAGES_DIR}/pyanaconda/.debug"
FILES_${PN}-staticdev += "${PYTHON_SITEPACKAGES_DIR}/pyanaconda/_isys.a"
FILES_${PN} = "/lib ${libdir} ${sysconfdir} ${bindir} ${sbindir} ${libexecdir} \
              ${datadir}/anaconda ${datadir}/applications ${datadir}/glade \
              ${PYTHON_SITEPACKAGES_DIR}/pyanaconda ${PYTHON_SITEPACKAGES_DIR}/log_picker \
              ${datadir}/themes \
"
FILES_${PN}-misc = "/usr/lib"
PACKAGES += "${PN}-misc"
RDEPENDS_${PN}-misc += "bash python3-core"

EXTRA_OECONF += "--disable-selinux \
         --with-sysroot=${PKG_CONFIG_SYSROOT_DIR} \
"

inherit features_check
REQUIRED_DISTRO_FEATURES = "systemd x11"

inherit autotools-brokensep gettext python3native pkgconfig gobject-introspection

do_configure_prepend() {
    ( cd ${S}; STAGING_DATADIR_NATIVE=${STAGING_DATADIR_NATIVE} ${S}/autogen.sh --noconfigure)
}

do_install_append() {
    install -m 644 ${WORKDIR}/81-edit-sudoers.ks ${D}${datadir}/anaconda/post-scripts
    install -m 644 ${WORKDIR}/81-systemd-preset-all.ks ${D}${datadir}/anaconda/post-scripts
    install -m 644 ${WORKDIR}/81-add-env-file-for-sshd.ks ${D}${datadir}/anaconda/post-scripts
    install -m 644 ${S}/widgets/src/resources/*.svg ${D}${datadir}/anaconda/pixmaps
    install -m 644 ${S}/widgets/src/resources/*.png ${D}${datadir}/anaconda/pixmaps
}

python __anonymous () {
    if not bb.utils.contains("PACKAGE_CLASSES", "package_rpm", True, False, d):
        raise bb.parse.SkipPackage('Anaconda requires RPM packages to be the default in PACKAGE_CLASSES.')
}

COMPATIBLE_HOST = '(x86_64.*|i.86.*|arm.*|aarch64.*)-(linux.*|freebsd.*)'
COMPATIBLE_HOST_armv7a = 'null'
COMPATIBLE_HOST_armv7ve = 'null'