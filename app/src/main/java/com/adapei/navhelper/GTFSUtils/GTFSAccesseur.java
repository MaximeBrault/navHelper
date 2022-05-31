package com.adapei.navhelper.GTFSUtils;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class GTFSAccesseur {

    private Context context;

    public GTFSAccesseur(Context context){
        if (context != null){
            this.context = context;
        }
        else {
            System.err.println("err param errorr in GTFSAccesseur constructor");
        }
        fetch();
    }




    public void fetch() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String ret = null;
        RequestQueue mQueue = Volley.newRequestQueue(this.context);
        Log.d("mQueue",mQueue.toString());
        JsonObjectRequest request = new JsonObjectRequest(
                "https://data.opendatasoft.com/api/v2/catalog/datasets/all-datasets%40navitia/records?select=*&where=distance(geo%2C%20geom%27POINT(-3.4037432%2047.7430224)%27%2C%2010km)&limit=10&offset=0",
                null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            //Récupération de l'adresse du Zip depuis l'api
                            //********************************************************************************************************************************************************************
                            String urlNeedS = jsonObject.getJSONArray("records").getJSONObject(0).getJSONObject("record").getJSONObject("fields").getString("data_source_file");
                            //json['records'][0]['record']['fields']['data_source_file']);
                            String urlToAdd = urlNeedS.substring(0,4)+"s"+urlNeedS.substring(4);
                            Log.d("url avec un s",urlToAdd);
                            Log.d("responseTest", urlToAdd);



                            //Traitement du Zip
                            //********************************************************************************************************************************************************************
                            String destDirectory = context.getFilesDir().getAbsolutePath();
                            Log.d("directory", context.getFilesDir().getPath());
                            byte[] buffer = new byte[2048];
                            URL url = new URL(urlToAdd);
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");
                            InputStream in = connection.getInputStream();
                            ZipInputStream zis = new ZipInputStream(in);
                            ZipEntry zipEntry = zis.getNextEntry();
                            while (zipEntry!=null) {
                                String filePath = destDirectory + File.separator + zipEntry.getName();
                                System.out.println("Unzipping "+filePath);
                                if (!zipEntry.isDirectory()) {
                                    FileOutputStream fos = new FileOutputStream(filePath);
                                    int len;
                                    while((len = zis.read(buffer)) > 0) {
                                        fos.write(buffer,0,len);
                                    }
                                    fos.close();
                                }
                                else {
                                    File dir = new File(filePath);
                                    dir.mkdir();
                                }
                                zis.closeEntry();
                                zipEntry = zis.getNextEntry();
                            }
                            zis.closeEntry();
                            zis.close();
                            System.out.println("unzipping complete");



                        } catch (JSONException | MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //***********************************************************************************************************************************

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("ResponseError",volleyError.toString());
                    }
                });
        mQueue.add(request);
    }

    public Context getContext() {
        return context;
    }
}

