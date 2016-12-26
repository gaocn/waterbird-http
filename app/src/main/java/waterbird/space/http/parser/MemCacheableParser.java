package waterbird.space.http.parser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import waterbird.space.http.log.HttpLog;
import waterbird.space.http.utils.StringCodingUtils;

/**
 * Created by 高文文 on 2016/12/26.
 *
 * parse inputstream to data ,save to mem and sdcard.
 */

public abstract class MemCacheableParser<T> extends DataParser<T> {
    private static final String TAG = "MemCacheableParser";

    @Override
    public T readFromNetworkStream(InputStream inputStream, long len, String charset) throws IOException {
        this.data = super.readFromNetworkStream(inputStream, len, charset);
        if(this.data != null && request.isCacheMode()) {
            tryKeepToCache(this.data);
        }
        return this.data;
    }
    /**  store data to cache */
    protected abstract boolean tryKeepToCache(T data) throws IOException;

    @Override
    public T readFromDiskCache(File file) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            data = parseDiskCache(fis, file.length());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(fis != null) {
                fis.close();
            }
        }
        return this.data;
    }
    /** parse disk cached data */
    protected abstract T parseDiskCache(InputStream stream, long length) throws IOException;

    @Override
    protected boolean isMemCacheSupport() {
        return true;
    }

    /**
     * translate original string to custom string; if this string is encrypted, you can
     *  override this method to decrypt it to transparent string
     *
     * @param string  data from server
     * @return  decrypt data
     */
    protected String translateString(String string) {
        return string;
    }


    /**
     * translate stream to byte array
     * @param is
     * @param len
     * @return
     */
    protected final byte[] streamToByteArray(InputStream is, long len) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[bufferSize];
        int count = 0;
        try {
            while (!request.isCancelledOrInterrupted() && (count = is.read(buffer)) != -1) {
                baos.write(buffer, 0, count);
                readLength += count;
                notifyReadingStatus(len, readLength);
            }
            return baos.toByteArray();
        }  finally {
                try {
                    if(baos != null) {
                        baos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * parse input stream to string
     * @param is
     * @param len
     * @return
     */
    protected final String streamToString(InputStream is, long len, String charset) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[bufferSize];
        int count = 0;
        try {
            while (!request.isCancelledOrInterrupted() && (count = is.read(buffer)) != -1) {
                baos.write(buffer, 0, count);
                readLength += count;
                notifyReadingStatus(len, readLength);
            }
            return translateString(baos.toString(charset));
        }  finally {
            try {
                if(baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected final boolean keepToCache(String data) throws IOException {
        if(data != null) {
            return keepToCache(StringCodingUtils.getBytes(data, Charset.forName(charset)));
        }
        return false;
    }

    /**  save data to cache file, location is request.getCachedFile */
    protected final boolean keepToCache(byte[] data) throws IOException {
        if(data != null) {
            FileOutputStream fos = null;

            try {
                File file = request.getCachedFile();
                // 防止父目录不存在时，创建文件失败！
                File parentFile = file.getParentFile();
                if(!parentFile.exists()) {
                    boolean doMakeDir = parentFile.mkdir();
                    if(HttpLog.isPrint) {
                        HttpLog.d(TAG, "Keep Cache mkdirs result[doMakeDir=" + doMakeDir + "] path: " + parentFile.getAbsolutePath());
                    }
                }
                // write data to disk
                fos = new FileOutputStream(file);
                fos.write(data);
                fos.flush();

                if(HttpLog.isPrint) {
                    HttpLog.d(TAG,
                            "waterbird-http keep disk cache success, "
                                    + "   tag: " + request.getTag()
                                    + "   url: " + request.getUri()
                                    + "   key: " + request.getCacheKey()
                                    + "   path: " + file.getAbsolutePath());
                }
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }  finally {
                try {
                    if(fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
