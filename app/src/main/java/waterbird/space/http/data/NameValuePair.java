package waterbird.space.http.data;

import java.io.Serializable;

/**
 * Created by 高文文 on 2016/12/19.
 */

public class NameValuePair implements Serializable {
    private static final long serialVersionUID = -1938103227501760393L;
    private final String name;
    private final String value;

    public NameValuePair(String name, String value) {
        this.name = name;
        this.value = value;
    }


    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%-20s", this.name) + "=  " + this.value;
    }
}
