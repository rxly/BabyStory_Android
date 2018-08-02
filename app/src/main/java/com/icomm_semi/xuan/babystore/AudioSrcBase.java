package com.icomm_semi.xuan.babystore;

public interface AudioSrcBase {
    void getCategoryList(final HttpGetListen listen);
    void getSubCategoryList(final int catId,final HttpGetListen listen);
    void getAudioList(final int catId, final HttpGetListen listen);
}
