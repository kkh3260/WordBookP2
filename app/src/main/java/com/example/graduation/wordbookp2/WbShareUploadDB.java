package com.example.graduation.wordbookp2;

import com.google.firebase.database.Exclude;

/**
 * Created by hkk32 on 2018-08-09.
 */

public class WbShareUploadDB { // 업로드한 파일을 파이어 베이스Db에 업로드 하는 클래스
    private String mName;
    private String mFileUrl;
    private String mtextK;// 단어장 종류
    private String mtextEx;//단어장 설명
    private String mKey;

    public WbShareUploadDB() {
        //empty constructor needed
    }

    public WbShareUploadDB(String name, String fileUrl ,String textk,String textex) {
        if (name.trim().equals("")) {
            name = "No Name";
        }
        if(textex.trim().equals("")){
            textex = "empty text";
        }

        mName = name;
        mtextK =textk;
        mtextEx = textex;
        mFileUrl = fileUrl;
    }

    //단어장 이름
    public String getName() {
        return mName;
    }
    public void setName(String name) {
        mName = name;
    }

    //단어장 종류
    public String getTextKind() {return mtextK;}
    public void setTextKind(String textk){mtextK=textk;}

    //단어장 설명
    public String getTextExplain() {return mtextEx;}
    public void setTextExplain(String textex){mtextEx=textex;}

    //파일 Url
    public String getmFileUrl() {return mFileUrl;}
    public void setmFileUrl(String fileUrl) {
        mFileUrl = fileUrl;
    }

    // 키 설정
    @Exclude
    public String getKey() {
        return mKey;
    }
    @Exclude
    public void setKey(String key) {
        mKey = key;
    }
}
