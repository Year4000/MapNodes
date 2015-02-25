package net.year4000.mapnodes.backend;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.year4000.mapnodes.MapNodesPlugin;
import net.year4000.mapnodes.utils.GsonUtil;
import net.year4000.mapnodes.utils.SchedulerUtil;
import net.year4000.utilities.Callback;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class APIFetcher {
    private enum Methods {GET}

    /** Normal data request method that only return data */
    private static Reader request(Methods method, String uri) throws IOException {
        URL url = new URL(uri);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod(method.name());
        connection.setRequestProperty("User-Agent", "Year4000 MapNodes API Interface");

        // Get Response
        if (connection.getResponseCode() != 200) {
            throw new IOException(connection.getResponseMessage());
        }
        else {
            return new InputStreamReader(connection.getInputStream());
        }
    }

    /** HTTP get method with async request */
    public static <T> void get(String url, Class<T> clazz, Callback<T> callback) {
        SchedulerUtil.runAsync(() -> {
            T data = null;
            Throwable error = null;

            try {
                data = get(url, clazz);
            }
            catch (Exception e) {
                MapNodesPlugin.debug(e, false);
                error = e;
            }
            finally {
                callback.callback(data, error);
            }
        });
    }

    /** HTTP get method with sync request */
    public static <T> T get(String url, Class<T> clazz) throws Exception {
        try (Reader reader = request(Methods.GET, url)) {
            return GsonUtil.GSON.fromJson(reader, clazz);
        }
    }
}
