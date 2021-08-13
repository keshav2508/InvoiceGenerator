package com.example.Invoice;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    Button btnCreate;
    EditText buyer_name;
    EditText date;
    EditText amount;
    EditText pdf_name;
    EditText remarks;
    EditText InvoiceNo;
    View view;


    private int year, month, day;

    private static final int STORAGE_PERMISSION_CODE = 101;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        view = findViewById(R.id.view);
        btnCreate =  findViewById(R.id.create);
        buyer_name = findViewById(R.id.buyer);
        date = findViewById(R.id.date);
        amount = findViewById(R.id.Amount);
        pdf_name = findViewById(R.id.PDF_name);
        remarks = findViewById(R.id.Remarks);
        InvoiceNo = findViewById(R.id.invoice_no);
        btnCreate.setOnClickListener(v -> {
            final Animation myAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce);

            MyBounceInterpolator interpolator = new MyBounceInterpolator(0.1, 25);
            myAnim.setInterpolator(interpolator);

            btnCreate.startAnimation(myAnim);

            createPdf();
        });
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createPdf() {
        String buyer = buyer_name.getText().toString();
        String date_s = date.getText().toString();
        String amount_s = amount.getText().toString();
        String pdf_name_s = pdf_name.getText().toString();
        String remak = remarks.getText().toString();
        float page_width = 2480;
        // create a new document
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(2480,3508, 1).create();

        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint title = new Paint();
        title.setTextAlign(Paint.Align.CENTER);
        title.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.ITALIC));
        title.setTextSize(70);
        canvas.drawText("INVOICE",page_width/2,200,title);


        Paint rect= new Paint();
        rect.setStyle(Paint.Style.STROKE);
        rect.setStrokeWidth(4);

        title.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD_ITALIC));
        title.setTextSize(50);
        title.setTextAlign(Paint.Align.LEFT);

        canvas.drawText("Preksha Jain",150,370,title);

        title.setTypeface(Typeface.DEFAULT);
        canvas.drawText("Invoice No.",page_width/2 + 50,370,title);
        String I_N= InvoiceNo.getText().toString();
        canvas.drawText(I_N,page_width/2 + 50,430,title);
        canvas.drawText("Dated",1850,370,title);
        canvas.drawText(date_s,1850,430,title);

        canvas.drawText("34 N.S.Road,Rishra-Hooghly",150,430,title);
        canvas.drawText("E-Mail : Prekshajain11@yahoo.com",150,490,title);

        canvas.drawRect(100,300,page_width/2,600,rect);//add
        canvas.drawRect(page_width/2,300,1800,600,rect);//inv no.
        canvas.drawRect(1800,300,page_width-100,600,rect);//date

        title.setTextSize(40);
        canvas.drawText("Buyer",110,640,title);
        title.setTextSize(50);
        title.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD_ITALIC));
        canvas.drawText(buyer,150,720,title);
        canvas.drawRect(100,600,page_width/2,900,rect);//buyer

        title.setTextSize(30);
        title.setTypeface(Typeface.DEFAULT);
        canvas.drawText("Terms Of Delivery",page_width/2 + 50,640,title);
        canvas.drawRect(page_width/2,600,page_width-100,900,rect);//term of delivery



        title.setTextSize(50);
        canvas.drawText("S. No.",110,1000,title);
        canvas.drawText("Particulars",350,1000,title);
        canvas.drawText("Amount",1850,1000,title);
        canvas.drawText("1.",150,1300,title);

        canvas.drawText("Total",1600,1950,title);

        title.setTextSize(50);
        title.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD_ITALIC));
        canvas.drawText("Professional Fees",400,1300,title);

        canvas.drawText("₹ "+amount_s,1900,1300,title);
        canvas.drawText("₹ "+amount_s,1900,1950,title);

        String amount_converted= convertToWord(Integer.parseInt(amount_s));

        canvas.drawText("Indian Rupees "+ amount_converted,150,2210,title);
        canvas.drawText("Remarks:",150,2400,title);

        canvas.drawRect(100,900,page_width-100,1100,rect);
        canvas.drawRect(100,900,300,2000,rect);//s no
        canvas.drawRect(300,900,1800,2000,rect);//parti
        canvas.drawRect(1800,900,page_width-100,2000,rect);//amount
        canvas.drawRect(100,1850,page_width-100,2000,rect);//total


        title.setTypeface(Typeface.DEFAULT);
        canvas.drawText("Amount Chargeable(in words)",150,2150,title);
        //canvas.drawText(remak,150,2820,title);


        canvas.drawText("Company's Bank Details",page_width/2,2500,title);
        canvas.drawText("Bank Name : ",page_width/2,2560,title);
        canvas.drawText("A/c No. : ",page_width/2,2620,title);
        canvas.drawText("Branch & IFS Code: ",page_width/2,2680,title);

        title.setTextAlign(Paint.Align.RIGHT);
        title.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD_ITALIC));
        canvas.drawText("Indusind Bank",page_width-110,2560,title);
        canvas.drawText("158961320183",page_width-110,2620,title);
        canvas.drawText("WOOD STREET & INDB0000015",page_width-110,2680,title);
        canvas.drawText("For PrekshaJain",page_width-110,2760,title);
        title.setTypeface(Typeface.DEFAULT);
        canvas.drawText("Authorised Signatory",page_width-110,2990,title);
        title.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("This is a Computer Generated Invoice",page_width/2,3060,title);




        canvas.drawRect(100,2000,page_width-100,3000,rect);

        canvas.drawRect(page_width/2,2700,page_width-100,3000,rect);



        TextPaint tp = new TextPaint();
        tp.setColor(Color.BLACK);
        tp.setTextSize(50);
        tp.setAntiAlias(true);

// split line
        StaticLayout staticLayout = new StaticLayout(remak, tp, ((int)page_width/2)-200, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
        //int yPos = (height / 2) - (staticLayout.getHeight() / 2);
        canvas.translate(150, 2400);
        staticLayout.draw(canvas);
        //canvas.restore();








        document.finishPage(page);

        String directory_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mypdf/";
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        String targetPdf = directory_path + pdf_name_s + ".pdf";
        File filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("main", "error " + e.toString());
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }
        // close the document
        document.close();
    }

    public void checkPermission(String permission, int requestCode)
    {

        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(
                MainActivity.this,
                permission)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat
                    .requestPermissions(
                            MainActivity.this,
                            new String[] { permission },
                            requestCode);
        }
        else {
            Toast
                    .makeText(MainActivity.this,
                            "Permission already granted",
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }
    private static final String[] units = {
            "",
            " one",
            " two",
            " three",
            " four",
            " five",
            " six",
            " seven",
            " eight",
            " nine"
    };
    private static final String[] doubles = {
            " ten",
            " eleven",
            " twelve",
            " thirteen",
            " fourteen",
            " fifteen",
            " sixteen",
            " seventeen",
            " eighteen",
            " nineteen"
    };
    private static final String[] tens = {
            "",
            "",
            " twenty",
            " thirty",
            " forty",
            " fifty",
            " sixty",
            " seventy",
            " eighty",
            " ninety"
    };

    private static final String[] hundreds = {
            "",
            " thousand",
            " lakh",
            " crore",
            " arab",
            " kharab"
    };

    private static String convertToWord(int number) {
        String num = "";
        int index = 0;
        int n;
        int digits;
        boolean firstIteration = true;
        do {
            if(firstIteration){
                digits = 1000;
            }else{
                digits = 100;
            }
            n = number % digits;
            if (n != 0){
                String s = convertThreeOrLessThanThreeDigitNum(n);
                num = s + hundreds[index] + num;
            }
            index++;
            number = number/digits;
            firstIteration = false;
        } while (number > 0);
        return num;
    }
    private static String convertThreeOrLessThanThreeDigitNum(int number) {
        String word = "";
        int num = number % 100;
        // Logic to take word from appropriate array
        if(num < 10){
            word = word + units[num];
        }
        else if(num < 20){
            word = word + doubles[num%10];
        }else{
            word = tens[num/10] + units[num%10];
        }
        word = (number/100 > 0)? units[number/100] + " hundred" + word : word;
        return word;
    }
    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "Set Date",
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,myDateListener, year, month, day);
        }
        return null;
    }

    private final DatePickerDialog.OnDateSetListener myDateListener = (arg0, arg1, arg2, arg3) -> {
        showDate(arg1, arg2+1, arg3);
    };

    private void showDate(int year, int month, int day) {
        date.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }
}