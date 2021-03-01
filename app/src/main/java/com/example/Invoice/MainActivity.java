package com.example.Invoice;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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



public class MainActivity extends AppCompatActivity {
    Button btnCreate;
    EditText buyer_name;
    EditText date;
    EditText amount;
    EditText pdf_name;
    EditText remarks;
    private static final int STORAGE_PERMISSION_CODE = 101;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        btnCreate =  findViewById(R.id.create);
        buyer_name = findViewById(R.id.buyer);
        date = findViewById(R.id.date);
        amount = findViewById(R.id.Amount);
        pdf_name = findViewById(R.id.PDF_name);
        remarks = findViewById(R.id.Remarks);
        btnCreate.setOnClickListener(view -> createPdf());
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
        canvas.drawText("1",page_width/2 + 50,430,title);
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

        String amount_converted= convert_to_words(amount_s);

        canvas.drawText("Indian Rupees "+ amount_converted,150,2210,title);
        canvas.drawText("Remarks:",150,2760,title);

        canvas.drawRect(100,900,page_width-100,1100,rect);
        canvas.drawRect(100,900,300,2000,rect);//s no
        canvas.drawRect(300,900,1800,2000,rect);//parti
        canvas.drawRect(1800,900,page_width-100,2000,rect);//amount
        canvas.drawRect(100,1850,page_width-100,2000,rect);//total


        title.setTypeface(Typeface.DEFAULT);
        canvas.drawText("Amount Chargeable(in words)",150,2150,title);
        canvas.drawText(remak,150,2820,title);

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
    static String convert_to_words(String num)
    {
        // Get number of digits
        // in given number
        String ans = "";
        int len = num.length();

        // Base cases
        if (len == 0) {
            ans = "empty string";
            return ans;
        }
        if (len > 4) {
            ans = "Length more than 4 is not supported";
            return ans;
        }

        /* The first string is not used, it is to make
            array indexing simple */
        String[] single_digits = new String[] {
                "zero", "one", "two",   "three", "four",
                "five", "six", "seven", "eight", "nine"
        };

        /* The first string is not used, it is to make
            array indexing simple */
        String[] two_digits = new String[] {
                "",          "ten",      "eleven",  "twelve",
                "thirteen",  "fourteen", "fifteen", "sixteen",
                "seventeen", "eighteen", "nineteen"
        };

        /* The first two string are not used, they are to
         * make array indexing simple*/
        String[] tens_multiple = new String[] {
                "",      "",      "twenty",  "thirty", "forty",
                "fifty", "sixty", "seventy", "eighty", "ninety"
        };

        String[] tens_power
                = new String[] { "hundred", "thousand" };


        /* For single digit number */
        if (len == 1) {
            ans= ans + (single_digits[num.charAt(0) - '0']);
            return ans;
        }

        /* Iterate while num
            is not '\0' */
        int x = 0;
        while (x < num.length()) {

            /* Code path for first 2 digits */
            if (len >= 3) {
                if (num.charAt(x) - '0' != 0){
                    ans = ans + (single_digits[num.charAt(x) - '0'] + " ") ;
                    ans = ans + (tens_power[len - 3]) + (" ");
                    // here len can be 3 or 4
                }
                --len;
            }

            /* Code path for last 2 digits */
            else {
                /* Need to explicitly handle
                10-19. Sum of the two digits
                is used as index of "two_digits"
                array of strings */
                if (num.charAt(x) - '0' == 1) {
                    int sum
                            = num.charAt(x) - '0' + num.charAt(x+1) - '0';
                    ans = ans + (two_digits[sum]);
                    return ans;
                }

                /* Need to explicitely handle 20 */
                else if (num.charAt(x) - '0' == 2
                        && num.charAt(x+1) - '0' == 0) {
                    ans = ans + ("twenty");
                    return ans;
                }

                /* Rest of the two digit
                numbers i.e., 21 to 99 */
                else {
                    int i = (num.charAt(x) - '0');
                    if (i > 0)
                        ans= ans +(tens_multiple[i])+(" ");
                    else
                        ans= ans+("");
                    ++x;
                    if (num.charAt(x) - '0' != 0)
                        ans= ans + (single_digits[num.charAt(x) - '0']);
                }
            }
            ++x;
        }
        return ans;
    }
}