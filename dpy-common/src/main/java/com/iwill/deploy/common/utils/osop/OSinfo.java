package com.iwill.deploy.common.utils.osop;

/**
 * 操作系统类：
 * 获取System.getProperty("os.name")对应的操作系统
 *
 * @author isea533
 */
public class OSinfo {

    private static String OS = System.getProperty("os.name").toLowerCase();

    private static OSinfo _instance = new OSinfo();

    private OSPlatform platform;

    private OSinfo() {
    }

    public static boolean isLinux() {
        return OS.contains("linux");
    }

    public static boolean isMacOS() {
        return OS.contains("mac") && OS.indexOf("os") > 0 && !OS.contains("x");
    }

    public static boolean isMacOSX() {
        return OS.contains("mac") && OS.indexOf("os") > 0 && OS.indexOf("x") > 0;
    }

    public static boolean isWindows() {
        return OS.contains("windows");
    }

    public static boolean isOS2() {
        return OS.contains("os/2");
    }

    public static boolean isSolaris() {
        return OS.contains("solaris");
    }

    public static boolean isSunOS() {
        return OS.contains("sunos");
    }

    public static boolean isMPEiX() {
        return OS.contains("mpe/ix");
    }

    public static boolean isHPUX() {
        return OS.contains("hp-ux");
    }

    public static boolean isAix() {
        return OS.contains("aix");
    }

    public static boolean isOS390() {
        return OS.contains("os/390");
    }

    public static boolean isFreeBSD() {
        return OS.contains("freebsd");
    }

    public static boolean isIrix() {
        return OS.contains("irix");
    }

    public static boolean isDigitalUnix() {
        return OS.contains("digital") && OS.indexOf("unix") > 0;
    }

    public static boolean isNetWare() {
        return OS.contains("netware");
    }

    public static boolean isOSF1() {
        return OS.contains("osf1");
    }

    public static boolean isOpenVMS() {
        return OS.contains("openvms");
    }

    /**
     * 获取操作系统名字
     *
     * @return 操作系统名
     */
    public static OSPlatform getPlatformName() {
        if (isAix()) {
            _instance.platform = OSPlatform.AIX;
        } else if (isDigitalUnix()) {
            _instance.platform = OSPlatform.Digital_Unix;
        } else if (isFreeBSD()) {
            _instance.platform = OSPlatform.FreeBSD;
        } else if (isHPUX()) {
            _instance.platform = OSPlatform.HP_UX;
        } else if (isIrix()) {
            _instance.platform = OSPlatform.Irix;
        } else if (isLinux()) {
            _instance.platform = OSPlatform.Linux;
        } else if (isMacOS()) {
            _instance.platform = OSPlatform.Mac_OS;
        } else if (isMacOSX()) {
            _instance.platform = OSPlatform.Mac_OS_X;
        } else if (isMPEiX()) {
            _instance.platform = OSPlatform.MPEiX;
        } else if (isNetWare()) {
            _instance.platform = OSPlatform.NetWare_411;
        } else if (isOpenVMS()) {
            _instance.platform = OSPlatform.OpenVMS;
        } else if (isOS2()) {
            _instance.platform = OSPlatform.OS2;
        } else if (isOS390()) {
            _instance.platform = OSPlatform.OS390;
        } else if (isOSF1()) {
            _instance.platform = OSPlatform.OSF1;
        } else if (isSolaris()) {
            _instance.platform = OSPlatform.Solaris;
        } else if (isSunOS()) {
            _instance.platform = OSPlatform.SunOS;
        } else if (isWindows()) {
            _instance.platform = OSPlatform.Windows;
        } else {
            _instance.platform = OSPlatform.Others;
        }
        return _instance.platform;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(System.getProperty("os.name"));
    }

}