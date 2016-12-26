package waterbird.space.http.utils;

import android.os.Build;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class StringCodingUtils {

    /**
     * Build.VERSION.SDK_INT是系统的版本
     * Build.VERSION_CODES.GINGERBREAD是版本号
     * 对比应用的版本与平台的版本做相应的处理
     */
    public static byte[] getBytes(String src, Charset charSet) {
        // Build.VERSION_CODES.GINGERBREAD = 9
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            try {
                return src.getBytes(charSet.name());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return src.getBytes(charSet);
        }
    }

}
