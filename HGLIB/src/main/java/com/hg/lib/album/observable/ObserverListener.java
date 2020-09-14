package com.hg.lib.album.observable;





import com.hg.lib.album.bean.LocalMediaFolder;
import com.hg.lib.base.LocalMedia;

import java.util.List;


public interface ObserverListener {
    void observerUpFoldersData(List<LocalMediaFolder> folders);

    void observerUpSelectsData(List<LocalMedia> selectMedias);
}
