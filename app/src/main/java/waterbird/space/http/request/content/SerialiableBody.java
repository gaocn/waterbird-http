package waterbird.space.http.request.content;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by 高文文 on 2016/12/26.
 */

public class SerialiableBody extends ByteArrayBody {
    public SerialiableBody(Serializable serializer) {
        // TODO  这里为什么传入null而不是调用super{@link getBytes(serializer)}
        super(getBytes(serializer), null);
    }

    public static byte[] getBytes(Serializable serializer) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(serializer);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
