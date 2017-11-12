package com.example.jianming.Tasks;


import com.example.jianming.Utils.Daos;
import com.example.jianming.Utils.TimeUtil;
import com.example.jianming.beans.PicAlbumBean;
import com.example.jianming.beans.PicAlbumBeanDao;
import com.example.jianming.beans.UpdateStamp;
import com.example.jianming.myapplication.PicAlbumListActivityMD;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.ref.SoftReference;

public class DownloadAlbumsTask extends DownloadWebpageTask {
    private PicAlbumBeanDao picAlbumBeanDao;
    private final UpdateStamp albumStamp = UpdateStamp.getUpdateStampByTableName("PIC_ALBUM_BEAN");
    private SoftReference<PicAlbumListActivityMD> activityMD;

    public DownloadAlbumsTask(PicAlbumBeanDao picAlbumBeanDao, PicAlbumListActivityMD activityMD) {
        this.picAlbumBeanDao = picAlbumBeanDao;
        this.activityMD = new SoftReference<>(activityMD);
    }

    @Override
    protected void onPostExecute(String s) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            Daos.db.beginTransaction();
            albumStamp.setUpdateStamp(TimeUtil.getGmtInFormatyyyyMMddHHmmss());
            albumStamp.update();
            PicAlbumBean[] picAlbumBeanList = mapper.readValue(s, PicAlbumBean[].class);
            for (PicAlbumBean picAlbumBean : picAlbumBeanList) {
                picAlbumBeanDao.insert(picAlbumBean);
            }
            Daos.db.setTransactionSuccessful();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            Daos.db.endTransaction();
        }
        activityMD.get().refreshFrontPage();
    }
}
