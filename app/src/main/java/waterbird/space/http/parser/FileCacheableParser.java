package waterbird.space.http.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import waterbird.space.http.log.HttpLog;

/**
 * Created by 高文文 on 2016/12/26.
 */

public abstract class FileCacheableParser<T> extends DataParser<T> {
    private static final String TAG = "FileCacheableParser";
    /**
     * file to store parsed data
     */
    protected File file;

    public FileCacheableParser() {
    }

    public FileCacheableParser(File file) {
        this.file = file;
    }

    @Override
    public final T readFromDiskCache(File file) throws IOException {
        data = parseDiskCache(file);
        return data;
    }

    /**
     * parse local file to data in format<T>
     */
    protected abstract T parseDiskCache(File file) throws IOException;

    @Override
    protected boolean isMemCacheSupport() {
        return false;
    }

    protected final File streamToFile(InputStream inputStream, long totalLen) {
        File file = request.getCachedFile();
        FileOutputStream fos = null;

        try {
            File parentFile = file.getParentFile();
            if(!parentFile.exists()) {
                boolean doMakeDir = parentFile.mkdir();
                if(HttpLog.isPrint) {
                    HttpLog.d(TAG, "Keep Cache mkdirs result[doMakeDir=" + doMakeDir + "] path: " + parentFile.getAbsolutePath());
                }
            }
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[bufferSize];
            int count = 0;
            while( !request.isCancelledOrInterrupted() && (count = inputStream.read(buffer)) != -1) {
                //if read bytes from server is encrypted, call translateToBytes to decrypt it
                buffer = translateToBytes(buffer);
                fos.write(buffer, 0, count);
                readLength += count;
                notifyReadingStatus(totalLen, readLength);
            }
            if(HttpLog.isPrint) {
                HttpLog.d(TAG, "parsed file len: " + file.length());
            }
            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
                try {
                    if(fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }
}
