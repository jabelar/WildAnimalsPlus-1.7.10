package net.minecraft.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpUtil
{
    /** The number of download threads that we have started so far. */
    private static final AtomicInteger downloadThreadsStarted = new AtomicInteger(0);
    private static final Logger logger = LogManager.getLogger();
    private static final String __OBFID = "CL_00001485";

    /**
     * Builds an encoded HTTP POST content string from a string map
     */
    public static String buildPostString(Map data)
    {
        StringBuilder stringbuilder = new StringBuilder();
        Iterator iterator = data.entrySet().iterator();

        while (iterator.hasNext())
        {
            Entry entry = (Entry)iterator.next();

            if (stringbuilder.length() > 0)
            {
                stringbuilder.append('&');
            }

            try
            {
                stringbuilder.append(URLEncoder.encode((String)entry.getKey(), "UTF-8"));
            }
            catch (UnsupportedEncodingException unsupportedencodingexception1)
            {
                unsupportedencodingexception1.printStackTrace();
            }

            if (entry.getValue() != null)
            {
                stringbuilder.append('=');

                try
                {
                    stringbuilder.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                }
                catch (UnsupportedEncodingException unsupportedencodingexception)
                {
                    unsupportedencodingexception.printStackTrace();
                }
            }
        }

        return stringbuilder.toString();
    }

    /**
     * Sends a POST to the given URL using the map as the POST args
     */
    public static String postMap(URL url, Map data, boolean skipLoggingErrors)
    {
        /**
         * Sends a POST to the given URL
         *  
         * @param content Formatted POST args (key=value&key2=value2&etc=etc) Should be encoded properly or an error may
         * occur.
         */
        return post(url, buildPostString(data), skipLoggingErrors);
    }

    /**
     * Sends a POST to the given URL
     *  
     * @param content Formatted POST args (key=value&key2=value2&etc=etc) Should be encoded properly or an error may
     * occur.
     */
    private static String post(URL url, String content, boolean skipLoggingErrors)
    {
        try
        {
            Proxy proxy = MinecraftServer.getServer() == null ? null : MinecraftServer.getServer().getServerProxy();

            if (proxy == null)
            {
                proxy = Proxy.NO_PROXY;
            }

            HttpURLConnection httpurlconnection = (HttpURLConnection)url.openConnection(proxy);
            httpurlconnection.setRequestMethod("POST");
            httpurlconnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpurlconnection.setRequestProperty("Content-Length", "" + content.getBytes().length);
            httpurlconnection.setRequestProperty("Content-Language", "en-US");
            httpurlconnection.setUseCaches(false);
            httpurlconnection.setDoInput(true);
            httpurlconnection.setDoOutput(true);
            DataOutputStream dataoutputstream = new DataOutputStream(httpurlconnection.getOutputStream());
            dataoutputstream.writeBytes(content);
            dataoutputstream.flush();
            dataoutputstream.close();
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream()));
            StringBuffer stringbuffer = new StringBuffer();
            String s1;

            while ((s1 = bufferedreader.readLine()) != null)
            {
                stringbuffer.append(s1);
                stringbuffer.append('\r');
            }

            bufferedreader.close();
            return stringbuffer.toString();
        }
        catch (Exception exception)
        {
            if (!skipLoggingErrors)
            {
                logger.error("Could not post to " + url, exception);
            }

            return "";
        }
    }

    @SideOnly(Side.CLIENT)
    public static void downloadResourcePack(final File packFile, final String packName, final HttpUtil.DownloadListener completionListener, final Map requestData, final int maxSize, final IProgressUpdate loadingScreen, final Proxy proxy)
    {
        Thread thread = new Thread(new Runnable()
        {
            private static final String __OBFID = "CL_00001486";
            public void run()
            {
                URLConnection urlconnection = null;
                InputStream inputstream = null;
                DataOutputStream dataoutputstream = null;

                if (loadingScreen != null)
                {
                    loadingScreen.resetProgressAndMessage("Downloading Texture Pack");
                    loadingScreen.displayLoadingString("Making Request...");
                }

                try
                {
                    try
                    {
                        byte[] abyte = new byte[4096];
                        URL url = new URL(packName);
                        urlconnection = url.openConnection(proxy);
                        float f = 0.0F;
                        float f1 = (float)requestData.entrySet().size();
                        Iterator iterator = requestData.entrySet().iterator();

                        while (iterator.hasNext())
                        {
                            Entry entry = (Entry)iterator.next();
                            urlconnection.setRequestProperty((String)entry.getKey(), (String)entry.getValue());

                            if (loadingScreen != null)
                            {
                                loadingScreen.setLoadingProgress((int)(++f / f1 * 100.0F));
                            }
                        }

                        inputstream = urlconnection.getInputStream();
                        f1 = (float)urlconnection.getContentLength();
                        int i = urlconnection.getContentLength();

                        if (loadingScreen != null)
                        {
                            loadingScreen.displayLoadingString(String.format("Downloading file (%.2f MB)...", new Object[] {Float.valueOf(f1 / 1000.0F / 1000.0F)}));
                        }

                        if (packFile.exists())
                        {
                            long j = packFile.length();

                            if (j == (long)i)
                            {
                                completionListener.onDownloadComplete(packFile);

                                if (loadingScreen != null)
                                {
                                    loadingScreen.setDoneWorking();
                                }

                                return;
                            }

                            HttpUtil.logger.warn("Deleting " + packFile + " as it does not match what we currently have (" + i + " vs our " + j + ").");
                            packFile.delete();
                        }
                        else if (packFile.getParentFile() != null)
                        {
                            packFile.getParentFile().mkdirs();
                        }

                        dataoutputstream = new DataOutputStream(new FileOutputStream(packFile));

                        if (maxSize > 0 && f1 > (float)maxSize)
                        {
                            if (loadingScreen != null)
                            {
                                loadingScreen.setDoneWorking();
                            }

                            throw new IOException("Filesize is bigger than maximum allowed (file is " + f + ", limit is " + maxSize + ")");
                        }

                        boolean flag = false;
                        int k;

                        while ((k = inputstream.read(abyte)) >= 0)
                        {
                            f += (float)k;

                            if (loadingScreen != null)
                            {
                                loadingScreen.setLoadingProgress((int)(f / f1 * 100.0F));
                            }

                            if (maxSize > 0 && f > (float)maxSize)
                            {
                                if (loadingScreen != null)
                                {
                                    loadingScreen.setDoneWorking();
                                }

                                throw new IOException("Filesize was bigger than maximum allowed (got >= " + f + ", limit was " + maxSize + ")");
                            }

                            dataoutputstream.write(abyte, 0, k);
                        }

                        completionListener.onDownloadComplete(packFile);

                        if (loadingScreen != null)
                        {
                            loadingScreen.setDoneWorking();
                            return;
                        }
                    }
                    catch (Throwable throwable)
                    {
                        throwable.printStackTrace();
                    }
                }
                finally
                {
                    try
                    {
                        if (inputstream != null)
                        {
                            inputstream.close();
                        }
                    }
                    catch (IOException ioexception1)
                    {
                        ;
                    }

                    try
                    {
                        if (dataoutputstream != null)
                        {
                            dataoutputstream.close();
                        }
                    }
                    catch (IOException ioexception)
                    {
                        ;
                    }
                }
            }
        }, "File Downloader #" + downloadThreadsStarted.incrementAndGet());
        thread.setDaemon(true);
        thread.start();
    }

    @SideOnly(Side.CLIENT)
    public static int getSuitableLanPort() throws IOException
    {
        ServerSocket serversocket = null;
        boolean flag = true;
        int i;

        try
        {
            serversocket = new ServerSocket(0);
            i = serversocket.getLocalPort();
        }
        finally
        {
            try
            {
                if (serversocket != null)
                {
                    serversocket.close();
                }
            }
            catch (IOException ioexception)
            {
                ;
            }
        }

        return i;
    }

    /**
     * Send a GET request to the given URL.
     */
    @SideOnly(Side.CLIENT)
    public static String get(URL url) throws IOException
    {
        HttpURLConnection httpurlconnection = (HttpURLConnection)url.openConnection();
        httpurlconnection.setRequestMethod("GET");
        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream()));
        StringBuilder stringbuilder = new StringBuilder();
        String s;

        while ((s = bufferedreader.readLine()) != null)
        {
            stringbuilder.append(s);
            stringbuilder.append('\r');
        }

        bufferedreader.close();
        return stringbuilder.toString();
    }

    @SideOnly(Side.CLIENT)
    public interface DownloadListener
    {
        void onDownloadComplete(File p_148522_1_);
    }
}