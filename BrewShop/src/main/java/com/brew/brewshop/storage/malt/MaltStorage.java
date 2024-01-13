package com.brew.brewshop.storage.malt;

import android.content.Context;

import com.brew.brewshop.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MaltStorage {
    private final static String TAG = MaltStorage.class.getName();
    private static MaltInfoList sMaltCache;

    private Context mContext;

    public MaltStorage(Context context) {
        mContext = context;
    }

    @SuppressWarnings("unchecked")
    public MaltInfoList getMalts() {
        if (sMaltCache != null) {
            return sMaltCache;
        }
        try {
            sMaltCache = new MaltInfoList();
            // From BeerSmith - http://www.beersmith.com/Grains/Grains/GrainList.htm
            InputStream is = mContext.getResources().openRawResource(R.raw.fermentables);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] rowData = line.split("\\|");
                String name = rowData[0];
                double srm = Double.parseDouble(rowData[1].split("\\s+")[0]);
                double gravity = Double.parseDouble(rowData[2].split("\\s+")[0]);
                boolean mashed = Integer.parseInt(rowData[3]) == 1;
                MaltInfo info = new MaltInfo(name, srm, gravity, mashed);
                sMaltCache.add(info);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sMaltCache;
    }
}
