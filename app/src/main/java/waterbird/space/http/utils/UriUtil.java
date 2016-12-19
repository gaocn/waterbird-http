package waterbird.space.http.utils;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author gonvind
 * @date 2016-12-16
 */
public class UriUtil {
    /**
     * Returns a set of the unique names of all query parameters. Iterating
     * over the set will return the names in order of their first occurrence.
     *
     * @return a set of decoded names
     * @throws UnsupportedOperationException if this isn't a hierarchical URI
     *
     */
    public static Set<String> getQueryParameterNames(Uri uri) {
        if (uri.isOpaque()) {
            return Collections.emptySet();
        }
        /*
            http://www.baidu.com?name=wxz&id=123#weixinzhang
            getEncodedQuery返回值：name=wxz&id=123#weixinzhang
         */
        String query = uri.getEncodedQuery();
        if (query == null) {
            return Collections.emptySet();
        }

        Set<String> names = new LinkedHashSet<String>();
        int start = 0;
        do {
            int next = query.indexOf('&', start);
            int end = (next == -1) ? query.length() : next;

            int separator = query.indexOf('=', start);
            if (separator > end || separator == -1) {
                separator = end;
            }

            String name = query.substring(start, separator);
            names.add(name);
            // Move start to end of name.
            start = end + 1;
        } while (start < query.length());

        return Collections.unmodifiableSet(names);
    }

    /**
     * Searches the query string for parameter values with the given key.
     *
     * @param key which will be encoded
     * @return a list of decoded values
     * @throws UnsupportedOperationException if this isn't a hierarchical URI
     * @throws NullPointerException          if key is null
     */
    public static List<String> getQueryParameters(Uri uri, String key) {
        if (uri.isOpaque()) {
            return Collections.emptyList();
        }
        if (key == null) {
            throw new NullPointerException("key");
        }

        String query = uri.getEncodedQuery();
        if (query == null) {
            return Collections.emptyList();
        }

        ArrayList<String> values = new ArrayList<String>();

        int start = 0;
        do {
            int nextAmpersand = query.indexOf('&', start);
            int end = nextAmpersand != -1 ? nextAmpersand : query.length();

            int separator = query.indexOf('=', start);
            if (separator > end || separator == -1) {
                separator = end;
            }

            if (separator - start == key.length()
                    && query.regionMatches(start, key, 0, key.length())) {
                if (separator == end) {
                    values.add("");
                } else {
                    values.add(query.substring(separator + 1, end));
                }
            }

            // Move start to end of name.
            if (nextAmpersand != -1) {
                start = nextAmpersand + 1;
            } else {
                break;
            }
        } while (true);

        return Collections.unmodifiableList(values);
    }

    /**
     * Return a map of argument->value from a query in a URI
     * @param uri The URI
     */
    private Map<String,String> getQueryParameter(Uri uri) {
        if (uri.isOpaque()) {
            return Collections.emptyMap();
        }

        String query = uri.getEncodedQuery();
        if (query == null) {
            return Collections.emptyMap();
        }

        Map<String,String> parameters = new LinkedHashMap<String,String>();
        int start = 0;
        do {
            int next = query.indexOf('&', start);
            int end = (next == -1) ? query.length() : next;

            int separator = query.indexOf('=', start);
            if (separator > end || separator == -1) {
                separator = end;
            }

            String name = query.substring(start, separator);
            String value;
            if (separator < end)
                value = query.substring(separator + 1, end);
            else
                value = "";

            parameters.put(Uri.decode(name), Uri.decode(value));

            // Move start to end of name.
            start = end + 1;
        } while (start < query.length());

        return Collections.unmodifiableMap(parameters);
    }
}
