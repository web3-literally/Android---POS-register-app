package com.refresh.printerhelper;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.refresh.pos.R;
import com.refresh.printerhelper.utils.AidlUtil;
import com.refresh.printerhelper.utils.BluetoothUtil;
import com.refresh.printerhelper.utils.ESCUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


/**
 * Created by Administrator on 2017/5/2.
 */

public class DemoActivity extends BaseActivity {
    private ImageView mImageView;
    private int encode, position;
    TextView mTextView1;
    private String[] mStrings = new String[]{"CP437", "CP850", "CP860", "CP863", "CP865", "CP857", "CP737", "CP928", "Windows-1252", "CP866", "CP852", "CP858", "CP874", "Windows-775", "CP855", "CP862", "CP864", "GB18030", "BIG5", "KSC5601", "utf-8"};
    private int record;
    private boolean isBold , isUnderLine ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        setMyTitle(R.string.barcode_title);
       // setBack();

        encode = 8;
        position = 1;

        record = 17;
        isBold = false;
        isUnderLine = false;


        AidlUtil.getInstance().initPrinter();





    }

    public void onClick(View view) {


        String no = "10/10";
        String dt = "01:33PM 21-Dec-2010";
        String user = "Adm";
        String name = "Mr.Soham Banerjee(100001)";
        String totalAmt = "600.00";
        String totalQty = "2";
        String mode = "Cash";


        ArrayList<Product> p = new ArrayList<>();
        p.add(new Product("John Player","1","300.00","300.00"));
        p.add(new Product("Jockey Mens","1","300.00","300.00"));

        String c1 = "Retail Invoice";
        String c2 = "-----------------------------";
        String c3 = "Memo #"+no+" "+dt;
        String c4 = "User Name:"+user+blankSpace(user.length()+10);
        String c5 = name;
        String c6 = "Sr Product   Qty   Rate  Amount";

        String c9 = "Total   Qty:  "+totalQty+"  Amount: "+totalAmt;
        String c10= "(Rupees Six Hundred Only)";
        String c11 ="Tender:                   "+totalAmt;
        String c12 ="Pay Mode:Cash:            "+totalAmt;
        String c13 ="                               ";


        float size = 20;
        if (baseApp.isAidl()) {
            Bitmap bmp = getBitmapFromAsset(DemoActivity.this,"smart.jpg");
            AidlUtil.getInstance().printBitmap(bmp);
            AidlUtil.getInstance().printText(c1, 30, true, false);
            AidlUtil.getInstance().printText(c2, size, false, false);
            AidlUtil.getInstance().printText(c3, size, false, false);
            AidlUtil.getInstance().printText(c4, size, false, false);
            AidlUtil.getInstance().printText(c2, size, false, false);
            AidlUtil.getInstance().printText(c5, size, false, false);
            AidlUtil.getInstance().printText(c2, size, false, false);
            AidlUtil.getInstance().printText(c6, size, true, false);
            AidlUtil.getInstance().printText(c2, size, false, false);

            for (int i=0;i<p.size();i++){


                String index,product,qty,rate,amt;

                Product product1 = p.get(i);

                index = ""+i;
                int s1 = 2-(index.length());
                int s2 = 1;
                int s3=1;

                product = product1.getName();

                if(product.length()>11){

                    product = product.substring(0,9)+".";
                    s3 = 1;

                }else {
                    s3 = 12-(product.length());

                }

                qty = product1.getQty();
                rate = product1.getRate();
                amt = product1.getAmt();

                int s4= 3-(qty.length());

                int s5=1;
                int s6=1;


                String temp = blankSpace(s1)+index+blankSpace(s2)+product+blankSpace(s3)+blankSpace(s4)+qty+blankSpace(s5)+rate+blankSpace(s6)+amt;
                AidlUtil.getInstance().printText(temp, size, false, false);

            }

            AidlUtil.getInstance().printText(c2, size, false, false);
            AidlUtil.getInstance().printText(c9, size, true, false);
            AidlUtil.getInstance().printText(c10, size, false, false);
            AidlUtil.getInstance().printText(c11, size, true, false);
            AidlUtil.getInstance().printText(c12, size, false, false);
            AidlUtil.getInstance().printText(c2, size, false, false);
            AidlUtil.getInstance().printText(c13, size, false, false);
            AidlUtil.getInstance().printText(c13, size, false, false);
            AidlUtil.getInstance().printText(c13, size, false, false);

        } else {





            Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.smart);
            bitmap1 = scaleImage(bitmap1);


            BluetoothUtil.sendData(ESCUtil.printBitmap(bitmap1));
            BluetoothUtil.sendData(ESCUtil.nextLine(1));


            printByBluTooth(c1,true);
            printByBluTooth(c2,false);
            printByBluTooth(c3,false);
            printByBluTooth(c4,false);
            printByBluTooth(c2,false);
            printByBluTooth(c5,false);
            printByBluTooth(c2,false);
            printByBluTooth(c6,true);
            printByBluTooth(c2,false);

            for (int i=0;i<p.size();i++){


                String index,product,qty,rate,amt;

                Product product1 = p.get(i);

                index = ""+i;
                int s1 = 2-(index.length());
                int s2 = 1;
                int s3=1;

                product = product1.getName();

                if(product.length()>11){

                    product = product.substring(0,9)+".";
                    s3 = 1;

                }else {
                    s3 = 12-(product.length());

                }

                qty = product1.getQty();
                rate = product1.getRate();
                amt = product1.getAmt();

                int s4= 3-(qty.length());

                int s5=1;
                int s6=1;



                String temp = blankSpace(s1)+index+blankSpace(s2)+product+blankSpace(s3)+blankSpace(s4)+qty+blankSpace(s5)+rate+blankSpace(s6)+amt;
                printByBluTooth(temp, false);

            }


            printByBluTooth(c2,false);
            printByBluTooth(c9,true);
            printByBluTooth(c10,false);
            printByBluTooth(c11,true);
            printByBluTooth(c12,false);
            printByBluTooth(c2,false);
            printByBluTooth(c13,false);
            printByBluTooth(c13,false);
            printByBluTooth(c13,false);


        }
    }

    private Bitmap scaleImage(Bitmap bitmap1) {
        int width = bitmap1.getWidth();
        int height = bitmap1.getHeight();
        // 设置想要的大小
        int newWidth = (width/8+1)*8;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, 1);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bitmap1, 0, 0, width, height, matrix,
                true);
        return newbm;
    }

    private void printByBluTooth(String content, boolean isBold1) {
        try {

            BluetoothUtil.sendData(ESCUtil.alignCenter());

            if (isBold1) {
                BluetoothUtil.sendData(ESCUtil.boldOn());
            } else {
                BluetoothUtil.sendData(ESCUtil.boldOff());
            }



            if (isUnderLine) {
                BluetoothUtil.sendData(ESCUtil.underlineWithOneDotWidthOn());
            } else {
                BluetoothUtil.sendData(ESCUtil.underlineOff());
            }

            if (record < 17) {
                BluetoothUtil.sendData(ESCUtil.singleByte());
                BluetoothUtil.sendData(ESCUtil.setCodeSystemSingle(codeParse(record)));
            } else {
                BluetoothUtil.sendData(ESCUtil.singleByteOff());
                BluetoothUtil.sendData(ESCUtil.setCodeSystem(codeParse(record)));
            }

            BluetoothUtil.sendData(content.getBytes(mStrings[record]));
            BluetoothUtil.sendData(ESCUtil.nextLine(1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String blankSpace(int l) {


        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < l; i++) {
            sb.append(" ");

        }

        return sb.toString();

    }


    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            // handle exception
        }

        return bitmap;
    }




        private void printByBluTooth(String content,boolean isBold,boolean isCenter,int no) {
            try {
                if (isBold) {
                    BluetoothUtil.sendData(ESCUtil.boldOn());
                } else {
                    BluetoothUtil.sendData(ESCUtil.boldOff());
                }


                    BluetoothUtil.sendData(ESCUtil.alignCenter());




                if (false) {
                    BluetoothUtil.sendData(ESCUtil.underlineWithOneDotWidthOn());
                } else {
                    BluetoothUtil.sendData(ESCUtil.underlineOff());
                }

                if (no < 17) {
                    BluetoothUtil.sendData(ESCUtil.singleByte());
                    BluetoothUtil.sendData(ESCUtil.setCodeSystemSingle(codeParse(no)));
                } else {
                    BluetoothUtil.sendData(ESCUtil.singleByteOff());
                    BluetoothUtil.sendData(ESCUtil.setCodeSystem(codeParse(no)));
                }



                BluetoothUtil.sendData(content.getBytes(mStrings[no]));
                BluetoothUtil.sendData(ESCUtil.nextLine(1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }







    private byte codeParse(int value) {
        byte res = 0x00;
        switch (value) {
            case 0:
                res = 0x00;
                break;
            case 1:
            case 2:
            case 3:
            case 4:
                res = (byte) (value + 1);
                break;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
                res = (byte) (value + 8);
                break;
            case 12:
                res = 21;
                break;
            case 13:
                res = 33;
                break;
            case 14:
                res = 34;
                break;
            case 15:
                res = 36;
                break;
            case 16:
                res = 37;
                break;
            case 17:
            case 18:
            case 19:
                res = (byte) (value - 17);
                break;
            case 20:
                res = (byte) 0xff;
                break;
        }
        return (byte) res;
    }




     class Product{


         public String getName() {
             return name;
         }

         public void setName(String name) {
             this.name = name;
         }

         public String getQty() {
             return qty;
         }

         public void setQty(String qty) {
             this.qty = qty;
         }

         public String getRate() {
             return rate;
         }

         public void setRate(String rate) {
             this.rate = rate;
         }

         public String getAmt() {
             return amt;
         }

         public void setAmt(String amt) {
             this.amt = amt;
         }

         private String name;
      private String qty;
      private String rate;
         private String amt;

         public Product(String name, String qty, String rate, String amt) {
             this.name = name;
             this.qty = qty;
             this.rate = rate;
             this.amt = amt;
         }




    }
}
