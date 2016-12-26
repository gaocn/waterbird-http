package waterbird.space.http.request.content;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import waterbird.space.http.data.Constants;
import waterbird.space.http.data.NameValuePair;

/**
 * Created by 高文文 on 2016/12/26.
 */

public class UrlEncodedFormBody extends StringBody {
    public UrlEncodedFormBody(String string) {
        super(string, Constants.DEFAULT_CHARSET, Constants.MIME_TYPE_FORM_URLENCODE);
    }

    public UrlEncodedFormBody(String string, String charset, String mimeType) {
        super(string, charset, mimeType);
    }

    public UrlEncodedFormBody(List<NameValuePair> list) {
        super(handleListValue(list, Constants.DEFAULT_CHARSET), Constants.DEFAULT_CHARSET, Constants.MIME_TYPE_FORM_URLENCODE);
    }

    public UrlEncodedFormBody(List<NameValuePair> list, String charset) {
        super(handleListValue(list, charset), charset, Constants.MIME_TYPE_FORM_URLENCODE);
    }

    public UrlEncodedFormBody(Map<String, String> map) {
        super(handleMapValue(map, Constants.DEFAULT_CHARSET), Constants.DEFAULT_CHARSET, Constants.MIME_TYPE_FORM_URLENCODE);
    }

    public UrlEncodedFormBody(Map<String, String> map, String charset) {
        super(handleMapValue(map, charset), charset, Constants.MIME_TYPE_FORM_URLENCODE);
    }

    /**
     * return encoded key-value pair
     * ex: map={k1=v1, k2=v2}
     * result: "k1=v1&k2=v2"
     */
    public static String handleMapValue(Map<String, String> map, String charset) {
        if(map == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        boolean appendAndOperator = false;

        for(Map.Entry<String, String> entry : map.entrySet()) {
            if(appendAndOperator) {
                stringBuilder.append(Constants.AND);
            } else {
                appendAndOperator = true;
            }

            try {
                stringBuilder.append(URLEncoder.encode(entry.getKey(),charset))
                        .append(Constants.EQUALS)
                        .append(URLEncoder.encode(entry.getValue(), charset));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return stringBuilder.toString();
    }
    /**
     * return encoded key-value pair
     * ex: list={{k1, v1}, {k2, v2}}
     * result: "k1=v1&k2=v2"
     */
    public static String handleListValue(List<NameValuePair> list, String charset) {
        if(list == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        boolean appendAndOperator = false;

        for(NameValuePair pair : list) {
            if(appendAndOperator) {
                stringBuilder.append(Constants.AND);
            } else {
                appendAndOperator = true;
            }

            try {
                stringBuilder.append(URLEncoder.encode(pair.getName(),charset))
                        .append(Constants.EQUALS)
                        .append(URLEncoder.encode(pair.getValue(), charset));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return "StringEntity{" +
                "string='" + string + '\'' +
                ", charset='" + charset + '\'' +
                ", contentType='" + contentType + '\'' +
                '}';
    }
}
