package waterbird.space.http.utils;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

import waterbird.space.http.log.HttpLog;

/**
 * @author gonvind
 * @date 2016-12-16
 */
public class HttpUtil {
    private static final String TAG = "HttpUtil";
    private static final String PATH_CPU = "/sys/devices/system/cpu/";
    private static final String CPU_FILTER = "cpu[0-9]+";
    private static int CPU_CORES = 0;

    public static String formatDate(long millis) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date(millis));
    }

    /**
     * Get available processors.
     */
    public static int getProcessorsCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * Gets the number of cores available in this device, across all processors.
     * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
     *
     * @return The number of cores, or available processors if failed to get result
     */
    public static int getCoresNumbers() {
        if (CPU_CORES > 0) {
            return CPU_CORES;
        }
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                if (Pattern.matches(CPU_FILTER, pathname.getName())) {
                    return true;
                }
                return false;
            }
        }
        try {
            //Get directory containing CPU info
            File dir = new File(PATH_CPU);
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            //Return the number of cores (virtual CPU devices)
            CPU_CORES = files.length;

            /*
                droidphone@990:~$ cd /sys/devices/system/cpu
                droidphone@990:/sys/devices/system/cpu$ ls
                    cpu0  cpu3  cpu6     cpuidle     offline   power    release
                    cpu1  cpu4  cpu7     kernel_max  online    present  uevent
                    cpu2  cpu5  cpufreq  modalias    possible  probe
            */

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (CPU_CORES < 1) {
            CPU_CORES = Runtime.getRuntime().availableProcessors();
        }
        if (CPU_CORES < 1) {
            CPU_CORES = 1;
        }
        HttpLog.i(TAG, "CPU cores: " + CPU_CORES);
        return CPU_CORES;
    }

    //TODO
    public static ArrayList<Field> getAllParamModelFields(Class<?> claxx) {
        // find all field.
        ArrayList<Field> fieldList = new ArrayList<Field>();
/*        while (claxx != null && claxx != HttpRichParamModel.class && claxx != Object.class) {
            Field[] fs = claxx.getDeclaredFields();
            for (int i = 0; i < fs.length; i++) {
                Field f = fs[i];
                if (!f.isSynthetic()) {
                    fieldList.add(f);
                }
            }
            claxx = claxx.getSuperclass();
        }*/
        return fieldList;
    }
}
