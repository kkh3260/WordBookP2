package com.example.graduation.wordbookp2;

/**
 * Created by hkk32 on 2018-08-22.
 */

public class WBlayout
{
    private String mwordname;
    private String mwordmean;
    private String mwordexplain;

    public void setwordname(String wordname)
    {
        mwordname = wordname;
    }

    public void setwordmean(String wordmean)
    {
        mwordmean = wordmean;
    }

    public void setwordexplain(String wordexplain)
    {
        mwordexplain = wordexplain;
    }

    String getwordname()
    {
        return mwordname;
    }

    String getwordmean()
    {
        return mwordmean;
    }

    String getwordexplain()
    {
        return mwordexplain;
    }

    WBlayout(String wordname, String wordmean, String wordexplain)
    {
        mwordname = wordname;
        mwordmean = wordmean;
        mwordexplain = wordexplain;
    }
}

