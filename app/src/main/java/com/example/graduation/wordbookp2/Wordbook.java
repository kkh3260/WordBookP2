package com.example.graduation.wordbookp2;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Wordbook extends AppCompatActivity implements View.OnClickListener{


    int Wordresult = 1;
    int Excelload = 2;
    int Excelsave = 3;
    private WBadapter wordadapter;
    public static ArrayList<WBlayout> words;
    private ListView wordlistview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordbook);

        words = new ArrayList<>();
        wordadapter = new WBadapter(words);

        wordlistview = (ListView) findViewById(R.id.wordlist);
        wordlistview.setAdapter(wordadapter);
        final EditText wordexceled = (EditText)findViewById(R.id.wordexcel_ed);


        // 엑셀 저장 버튼
        Button wordexcel_save_button = (Button) findViewById(R.id.wordexcel_but);
        wordexcel_save_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                final EditText exceledit = (EditText) findViewById(R.id.wordexcel_ed);
                String excel_name = exceledit.getText().toString();
                String xls_name = ".xls";
                String excel = excel_name+xls_name;
                saveExcel(excel);

                /*Intent intent = new Intent();
                intent.setType("application/vnd.ms-excel");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, Excelsave);*/
            }
        });

        Button wordexcel_load_button = (Button) findViewById(R.id.wordexcel_but2);
        wordexcel_load_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setType("application/vnd.ms-excel");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, Excelload);
                /*Intent intent = new Intent(MainActivity.this, array.class);
                startActivity(intent);*/
            }
        });

        Button wordmanagebutton = (Button) findViewById(R.id.wordmanage_but);
        wordmanagebutton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent wordmanageintent = new Intent(Wordbook.this, WBmanage.class);
                startActivityForResult(wordmanageintent,Wordresult);
            }
        });

        wordlistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                wordadapter.removeword(position);
                wordadapter.notifyDataSetChanged();
                return false;
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Wordresult)
        {
            if(resultCode == RESULT_OK)
            {
                String wordnameres = data.getStringExtra("Wordname");
                String wordmeanres = data.getStringExtra("Wordmean");
                String wordexplainres = data.getStringExtra("Wordexplain");

                Toast.makeText(getApplicationContext(),wordnameres + "(이)가 추가되었습니다.",Toast.LENGTH_SHORT).show();
                wordadapter.addword(wordnameres, wordmeanres, wordexplainres);
                wordadapter.notifyDataSetChanged();
            }
        }
        else if (requestCode == Excelload)
        {
            Uri excelload_fileuri = data.getData();
            String excelload_filepath = excelload_fileuri.getPath();
            String excelload_filename = excelload_filepath;
            String excelload_filenamearr[] = excelload_filename.split("/");
            Toast.makeText(getApplicationContext(),excelload_filenamearr[excelload_filenamearr.length - 1]+"에서 로드되었습니다",Toast.LENGTH_LONG).show();
            readExcel(this,excelload_filenamearr[excelload_filenamearr.length-1]);
        }

        else if (requestCode == Excelsave)
        {
            Uri excelsave_fileuri = data.getData();
            String excelsave_filepath = excelsave_fileuri.getPath();
            String excelsave_filename = excelsave_filepath;
            String excelsave_filenamearr[] = excelsave_filename.split("/");
            saveExcel(excelsave_filenamearr[excelsave_filenamearr.length-1]);

        }
    }


    private void saveExcel(String excel)
    {
        HSSFWorkbook wordbook = new HSSFWorkbook();

        Sheet sheet = wordbook.createSheet("sheet1"); // 새로운 시트 생성

        Row row = sheet.createRow(0); // 새로운 행 생성
        Cell cell;

        cell = row.createCell(0); // 1번 셀 생성
        cell.setCellValue("이름"); // 1번 셀 값 입력

        cell = row.createCell(1); // 2번 셀 생성
        cell.setCellValue("나이"); // 2번 셀 값 입

        cell = row.createCell(2);
        cell.setCellValue("뜻");

        for(int i = 0; i < words.size() ; i++){ // 데이터 엑셀에 입력
            row = sheet.createRow(i+1);
            cell = row.createCell(0);
            cell.setCellValue(words.get(i).getwordname());
            cell = row.createCell(1);
            cell.setCellValue(words.get(i).getwordmean());
            cell = row.createCell(2);
            cell.setCellValue(words.get(i).getwordexplain());
        }


        File xlsFile = new File(getExternalFilesDir(null),excel);
        try{
            FileOutputStream os = new FileOutputStream(xlsFile);
            wordbook.write(os); // 외부 저장소에 엑셀 파일 생성
        }catch (IOException e){
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(),xlsFile.getAbsolutePath()+"에 저장되었습니다",Toast.LENGTH_SHORT).show();
    }

    private void readExcel(View.OnClickListener context, String excel_name)
    {
        try {
            File file = new File(getExternalFilesDir(null),excel_name); // 입력된 파일 이름과 경로를 합쳐서 새로운 경로 생성
            FileInputStream fis = new FileInputStream(file); // 새로운 경로로 탐색

            POIFSFileSystem fileSystem = new POIFSFileSystem(fis); // 탐색한거 뽑아내고
            HSSFWorkbook wordbook = new HSSFWorkbook(fileSystem); // 뽑아낸거 xls인지 확인하는 뭐 그런거

            int rowindex=0; // 행 인덱스
            int columnindex=0; // 열 인덱스
            HSSFSheet sheet = wordbook.getSheetAt(0); // sheet1에 내용 불러옴
            int rows = sheet.getPhysicalNumberOfRows(); // 행은 그 시트에 있는 행의 수를 대입
            for(rowindex = 1 ; rowindex < rows ; rowindex++) // 행 숫자만큼 반복
            {
                //행을 읽는다
                HSSFRow row = sheet.getRow(rowindex); // 1열부터 시작!
                if(row != null){
                    //셀의 수
                    int cells = row.getPhysicalNumberOfCells(); // 셀의 수는 그 행의 셀의 수를 대입
                    String wordname = null;
                    String wordmean = null;
                    String wordexplain = null;
                    for(columnindex = 0 ; columnindex < cells ; columnindex++) // 0번째 셀부터 있는 셀까지 반복
                    {
                        //셀값을 읽는다
                        HSSFCell cell = row.getCell(columnindex);
                        String word = cell.getStringCellValue(); // 셀에있는 문자 값을 받아옴

                        //String value="";
                        //셀이 빈값일경우를 위한 널체크
                        if(cell==null){
                            continue;
                        }
                        switch (columnindex){ // 0이면 wordname , 1이면 wordmean , 2면 wordexplain 이렇게 대입
                            case 0:
                                wordname = word;
                                break;
                            case 1:
                                wordmean = word;
                                break;
                            case 2:
                                wordexplain = word;
                                break;
                        }
                    }
                    words.add(new WBlayout(wordname,wordmean,wordexplain)); // 이제 한개의 열이 끝났으니 리스트뷰에 대입하고
                    wordadapter.notifyDataSetChanged(); // 대입이 끝났으니 저장
                }
            }
            Toast.makeText(getApplicationContext(), file.getAbsolutePath()+"에서 로드되었습니다", Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) { // 예외 처리
            e.printStackTrace();
        } catch (IOException e) { // 예외처리
            e.printStackTrace();
        }

    }


    @Override
    public void onClick(View view) {

    }
}
