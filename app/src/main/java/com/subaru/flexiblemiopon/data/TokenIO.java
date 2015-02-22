package com.subaru.flexiblemiopon.data;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by shiny_000 on 2015/02/22.
 */
public class TokenIO {

    private Context mContext;
    private final String LOG_TAG = "TokenIO";

    public TokenIO(Context context) {
        mContext = context;
    }

    public void writeAccessToken(String accessToken, AccessToken token) {
        try {
            String fileName = accessToken + ".tkn";
            FileOutputStream fos = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(token);
            Log.d(LOG_TAG, "Write token : " + fos.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AccessToken readAccessToken(String accessToken) {
        try {
            ObjectInputStream ois = new ObjectInputStream((mContext.openFileInput(accessToken)));
            AccessToken token = (AccessToken) ois.readObject();
            return token;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Read existing access token set.
     * If someone uses multiple SIM, then it may occur.
     * @return Set of existing AccessToken string in this device.
     */
    public Set<String> readAccessTokenSet() {
        File dir =mContext.getFilesDir();
        File[] tokenFiles = dir.listFiles(getExtensionFilter(".tkn"));
        Set<String> tokenSet = new HashSet<>();
        for (File tokenFile : tokenFiles) {
            tokenSet.add(tokenFile.getName());
        }
        return tokenSet;
    }

    private FilenameFilter getExtensionFilter(final String ext) {
        return new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                Log.d(LOG_TAG, "file : " + file.getAbsolutePath() + file.separatorChar + s);
                return (s.endsWith(ext)) ? true : false;
            }
        };
    }
}
