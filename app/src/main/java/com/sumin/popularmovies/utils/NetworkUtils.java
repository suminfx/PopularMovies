package com.sumin.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class NetworkUtils {
    //Test internet connection
    private static final String URL_FOR_TEST_CONNECTION = "http://www.google.com/";
    public static boolean isInternetConnection = false;

    //Reading from network
    private static final int CONNECT_TIMEOUT = 5000; //milliseconds
    private static final int READ_TIMEOUT = 10000; //milliseconds

    //Movie db API
    private static final String API_KEY = ""; //TODO: Add your API_KEY here
    private static final String MOVIES_DB_URL = "https://api.themoviedb.org/3/movie";
    private static final String SORT_BY_POPULARITY = "/popular";
    private static final String SORT_BY_TOP_RATED = "/top_rated";
    private static final String LANGUAGE_PARAMS = "language";
    private static final String PAGE_PARAMS = "page";
    private static final String VOTE_COUNT_PARAMS = "vote_count.gte";
    private static final String MIN_VOTE_COUNT = "0";
    private static final String API_KEY_PARAMS = "api_key";

    public static JSONObject getJSONObjectFromURL(URL url, AsyncProcess asyncProcess) {
        JSONObject resultJSONObject = null;
        try {
            resultJSONObject = new GetJSONFromURLTask(asyncProcess).execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return resultJSONObject;
    }

    public interface AsyncProcess {
        void onProcessStart();
        void onProcessFinish();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    private static boolean isInternetConnection() {
        boolean result;
        try {
            URL url = new URL(URL_FOR_TEST_CONNECTION);
            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
            urlc.setRequestProperty("User-Agent", "test");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(CONNECT_TIMEOUT); // Timeout is in milliseconds
            urlc.connect();
            result = urlc.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            result = false;
        }
        isInternetConnection = result;
        return isInternetConnection;
    }

    public static URL createURLByMethodOfSort(@NonNull SortBy sortBy, int pageNumber, String language) {
        String methodOfSorting = "";
        switch (sortBy) {
            case TOP_RATED_DESC:
                methodOfSorting = SORT_BY_TOP_RATED;
                break;
            case MOST_POPULAR_DESC:
                methodOfSorting = SORT_BY_POPULARITY;
                break;
        }

        Uri.Builder builder = Uri.parse(MOVIES_DB_URL + methodOfSorting).buildUpon()
                .appendQueryParameter(API_KEY_PARAMS, API_KEY)
                .appendQueryParameter(PAGE_PARAMS, String.valueOf(pageNumber))
                .appendQueryParameter(LANGUAGE_PARAMS, language)
                .appendQueryParameter(VOTE_COUNT_PARAMS, MIN_VOTE_COUNT);
        Uri uri = builder.build();
        URL resultURL = null;
        try {
            resultURL = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return resultURL;
    }

    public enum SortBy {
        MOST_POPULAR_DESC,
        TOP_RATED_DESC
    }

    private static class GetJSONFromURLTask extends AsyncTask<URL, Void, JSONObject> {

        private AsyncProcess asyncProcess;

        GetJSONFromURLTask(AsyncProcess asyncProcess) {
            this.asyncProcess = asyncProcess;
        }

        @Override
        protected void onPreExecute() {
            asyncProcess.onProcessStart();
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(URL... urls) {
            if (!isInternetConnection()) {
                return null;
            }
            JSONObject resultJSONObject = null;
            if (urls != null && urls.length > 0) {
                URL url = urls[0];
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(CONNECT_TIMEOUT);
                    connection.setReadTimeout(READ_TIMEOUT);
                    InputStream inputStream = connection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader reader = new BufferedReader(inputStreamReader);
                    String line = reader.readLine();
                    StringBuilder builderResultString = new StringBuilder();
                    while (line != null) {
                        builderResultString.append(line);
                        line = reader.readLine();
                    }
                    if (builderResultString.length() > 0) {
                        String jsonAsString = builderResultString.toString();
                        resultJSONObject = new JSONObject(jsonAsString);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
            return resultJSONObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            asyncProcess.onProcessFinish();
            super.onPostExecute(jsonObject);
        }
    }
}
