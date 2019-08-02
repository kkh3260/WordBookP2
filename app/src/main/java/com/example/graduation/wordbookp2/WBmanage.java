package com.example.graduation.wordbookp2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by hkk32 on 2018-08-22.
 */

public class WBmanage extends Activity
{
    //private ArrayList<Word> mwords = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceBundle)
    {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_wbmanage);

        final EditText wordnameed = (EditText)findViewById(R.id.wordname_ed);
        final EditText wordmeaned = (EditText)findViewById(R.id.wordmean_ed);
        final EditText wordexplained = (EditText)findViewById(R.id.wordexplain_ed);


        Button addButton = (Button) findViewById(R.id.wordadd_but);
        addButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String str1 = wordnameed.getText().toString();
                String str2 = wordmeaned.getText().toString();
                String str3 = wordexplained.getText().toString();

                Intent wordintent = new Intent();
                wordintent.putExtra("Wordname", str1);
                wordintent.putExtra("Wordmean", str2);
                wordintent.putExtra("Wordexplain", str3);
                setResult(RESULT_OK, wordintent);
                finish();
            }
        });

        Button returnButton = (Button) findViewById(R.id.wordreturn_but);
        returnButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });


    }

}
