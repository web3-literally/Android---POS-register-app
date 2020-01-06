package com.refresh.pos.ui.sale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.refresh.pos.R;
import com.refresh.pos.domain.DateTimeStrategy;
import com.refresh.pos.domain.inventory.LineItem;
import com.refresh.pos.domain.inventory.ReceiptProduct;
import com.refresh.pos.domain.sale.Register;
import com.refresh.pos.techicalservices.NoDaoSetException;
import com.refresh.pos.ui.MainActivity;
import com.refresh.pos.ui.component.UpdatableFragment;
import com.refresh.printerhelper.BaseApp;
import com.refresh.printerhelper.DemoActivity;
import com.refresh.printerhelper.utils.AidlUtil;
import com.refresh.printerhelper.utils.BluetoothUtil;
import com.refresh.printerhelper.utils.ESCUtil;
import com.refresh.printerhelper.utils.EnglishNumberToWords;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.refresh.printerhelper.DemoActivity.getBitmapFromAsset;

/**
 * A dialog shows the total change and confirmation for Sale.
 *
 * @author Refresh Team
 */
@SuppressLint("ValidFragment")
public class EndPaymentFragmentDialog extends DialogFragment {

    private Button doneButton;
    private TextView chg;
    private Long input;
    private Double discount;
    private Register regis;
    private UpdatableFragment saleFragment;
    private UpdatableFragment reportFragment;
    private int record;
    private String[] mStrings = new String[]{"CP437", "CP850", "CP860", "CP863", "CP865", "CP857", "CP737", "CP928", "Windows-1252", "CP866", "CP852", "CP858", "CP874", "Windows-775", "CP855", "CP862", "CP864", "GB18030", "BIG5", "KSC5601", "utf-8"};
    public BaseApp baseApp;

    public EndPaymentFragmentDialog(UpdatableFragment saleFragment, UpdatableFragment reportFragment) {
        super();
        this.saleFragment = saleFragment;
        this.reportFragment = reportFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            regis = Register.getInstance();
        } catch (NoDaoSetException e) {
            e.printStackTrace();
        }

        View v = inflater.inflate(R.layout.dialog_paymentsuccession, container, false);
        String strtext = getArguments().getString("edttext");
        input = Long.parseLong(getArguments().getString("input"));
        discount = Double.parseDouble(getArguments().getString("discount"));
        //input = (EditText) v.findViewById(R.id.dialog_mobile);
        chg = (TextView) v.findViewById(R.id.changeTxt);
        chg.setText(strtext);
        doneButton = (Button) v.findViewById(R.id.doneButton);

        doneButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                //  Toast.makeText(getActivity(),"phone:"+discount,Toast.LENGTH_SHORT).show();
                //   tryLogin();

                new LoginAsyncTask().execute();

                setPrintDataStore();
            }
        });
//        String content = edtEditText.getText().toString(); //gets you the contents of edit text
        //      tvTextView.setText(content);

        record = 17;
        baseApp = (BaseApp) getActivity().getApplication();
        AidlUtil.getInstance().initPrinter();

        return v;
    }


    private void end() {
        regis.endSale(DateTimeStrategy.getCurrentTime(), input, discount);
        saleFragment.update();
        reportFragment.update();
        this.dismiss();
    }


    protected void tryLogin() {

        Toast.makeText(getActivity(), "phone:" + input, Toast.LENGTH_SHORT).show();
    }


    private void setPrintDataStore() {
        String dt = DateTimeStrategy.getCurrentTime();
        String user = "Admin";
        DecimalFormat df = new DecimalFormat("#.00");

        double totalAmt = Math.round(regis.getTotal());
        double totalAmt1 = totalAmt;
        double tax = 0.09;
        if (totalAmt < 500) {
            tax = 0.025;
        }
        double sgst = Double.parseDouble(String.format("%.2f", totalAmt * tax));
        double cgst = Double.parseDouble(String.format("%.2f", totalAmt * tax));
        if (discount > 0) {
            totalAmt = (totalAmt + sgst + cgst) - discount;
        } else {
            totalAmt = (totalAmt + sgst + cgst);
        }
        String mode = "Cash";
        int totalQuantity = 0;


        ArrayList<ReceiptProduct> receiptProductArrayList = new ArrayList<>();

        for (LineItem lineItem : regis.getCurrentSale().getAllLineItem()) {
            receiptProductArrayList.add(new ReceiptProduct(lineItem.getProduct().getName(),
                    lineItem.getQuantity(),
                    lineItem.getProduct().getUnitPrice(),
                    lineItem.getQuantity() * lineItem.getProduct().getUnitPrice()));

            totalQuantity = totalQuantity + lineItem.getQuantity();
        }

        String c1 = "Retail Invoice";
        String c2 = "-----------------------------";
        String c3 = "Date : " + dt;
        String c4 = "User Name:" + user + blankSpace(user.length() + 10);
        String c6 = "Sr Product   Qty   Rate  Amount";


        String c9 = "Total   Qty:  " + totalQuantity + "  Amount: " + String.format("%.2f", totalAmt1);
        String c10 = "(Rupees " + EnglishNumberToWords.convert((long) totalAmt) + ")";
        String c14 = "SGST : " + (tax * 100) + "%       " + sgst;
        String c15 = "CGST : " + (tax * 100) + "%       " + cgst;
        String c16 = "Discount:               " + discount;
        String c11 = "Tender:                   " + String.format("%.2f", totalAmt);
        String c12 = "Pay Mode:Cash:            " + String.format("%.2f", totalAmt);
        String c13 = "                               ";


        float size = 20;

        if (baseApp.isAidl()) {
            Bitmap bmp = getBitmapFromAsset(getActivity(), "hevvylogo.jpg");
            AidlUtil.getInstance().printBitmap(bmp);
            AidlUtil.getInstance().printText(c1, 30, true, false);
            AidlUtil.getInstance().printText(c2, size, false, false);
            AidlUtil.getInstance().printText(c3, size, false, false);
            AidlUtil.getInstance().printText(c4, size, false, false);
            AidlUtil.getInstance().printText(c2, size, false, false);
            AidlUtil.getInstance().printText(c2, size, false, false);
            AidlUtil.getInstance().printText(c6, size, true, false);
            AidlUtil.getInstance().printText(c2, size, false, false);

            for (int i = 0; i < receiptProductArrayList.size(); i++) {


                String index, product, qty, rate, amt;

                ReceiptProduct product1 = receiptProductArrayList.get(i);

                index = "" + i;
                int s1 = 2 - (index.length());
                int s2 = 1;
                int s3 = 1;

                product = product1.getName();

                if (product.length() > 11) {

                    product = product.substring(0, 9) + ".";
                    s3 = 1;

                } else {
                    s3 = 12 - (product.length());

                }

                qty = String.valueOf(product1.getQty());
                rate = String.valueOf(String.format("%.2f", product1.getRate()));
                amt = String.valueOf(String.format("%.2f", product1.getAmt()));

                int s4 = 3 - (qty.length());

                int s5 = 1;
                int s6 = 1;


                String temp = blankSpace(s1) + index + blankSpace(s2) + product + blankSpace(s3) + blankSpace(s4) + qty + blankSpace(s5) + rate + blankSpace(s6) + amt;
                AidlUtil.getInstance().printText(temp, size, false, false);

            }

            AidlUtil.getInstance().printText(c2, size, false, false);
            AidlUtil.getInstance().printText(c9, size, true, false);

            AidlUtil.getInstance().printText(c14, size, false, false);
            AidlUtil.getInstance().printText(c15, size, false, false);
            AidlUtil.getInstance().printText(c16, size, false, false);
            AidlUtil.getInstance().printText(c11, size, true, false);
            AidlUtil.getInstance().printText(c10, size, false, false);
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


            printByBluTooth(c1, true);
            printByBluTooth(c2, false);
            printByBluTooth(c3, false);
            printByBluTooth(c4, false);
            printByBluTooth(c2, false);
            printByBluTooth(c2, false);
            printByBluTooth(c6, true);
            printByBluTooth(c2, false);

            for (int i = 0; i < receiptProductArrayList.size(); i++) {


                String index, product, qty, rate, amt;

                ReceiptProduct product1 = receiptProductArrayList.get(i);

                index = "" + i;
                int s1 = 2 - (index.length());
                int s2 = 1;
                int s3 = 1;

                product = product1.getName();

                if (product.length() > 11) {

                    product = product.substring(0, 9) + ".";
                    s3 = 1;

                } else {
                    s3 = 12 - (product.length());

                }

                qty = String.valueOf(product1.getQty());
                rate = String.valueOf(product1.getRate());
                amt = String.valueOf(product1.getAmt());

                int s4 = 3 - (qty.length());

                int s5 = 1;
                int s6 = 1;


                String temp = blankSpace(s1) + index + blankSpace(s2) + product + blankSpace(s3) + blankSpace(s4) + qty + blankSpace(s5) + rate + blankSpace(s6) + amt;
                printByBluTooth(temp, false);

            }


            printByBluTooth(c2, false);
            printByBluTooth(c9, true);
            printByBluTooth(c14, false);
            printByBluTooth(c15, false);
            printByBluTooth(c16, false);
            printByBluTooth(c11, true);
            printByBluTooth(c10, false);
            printByBluTooth(c12, false);
            printByBluTooth(c2, false);
            printByBluTooth(c13, false);
            printByBluTooth(c13, false);
            printByBluTooth(c13, false);


        }

        end();


    }

    private void printByBluTooth(String content, boolean isBold1) {
        try {

            BluetoothUtil.sendData(ESCUtil.alignCenter());

            BluetoothUtil.sendData(ESCUtil.boldOff());


            BluetoothUtil.sendData(ESCUtil.underlineOff());

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

    private Bitmap scaleImage(Bitmap bitmap1) {
        int width = bitmap1.getWidth();
        int height = bitmap1.getHeight();
        // 设置想要的大小
        int newWidth = (width / 8 + 1) * 8;
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


    private class LoginAsyncTask extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... voids) {
//            Toast.makeText(this,"discount:+")
            String mUsername;
            String mPassword;
            String db;
//            mUsername = "nishammpm@gmail.com";
            //          mPassword = "nisham";
            //        db = "cre8bill_db19";
//String phone="";
            String par = "";
            double[] price = new double[20];
            String[] name = new String[20];
            int[] quantity = new int[20];
            int[] number = new int[20];
            int i = 0;
            int totalQuantity = 0;
            ArrayList<ReceiptProduct> receiptProductArrayList = new ArrayList<>();
            for (LineItem lineItem : regis.getCurrentSale().getAllLineItem()) {

                receiptProductArrayList.add(new ReceiptProduct(lineItem.getProduct().getName(),
                        lineItem.getQuantity(),
                        lineItem.getProduct().getUnitPrice(),
                        lineItem.getQuantity() * lineItem.getProduct().getUnitPrice()));

                totalQuantity = totalQuantity + lineItem.getQuantity();
                par += "&name[]=" + lineItem.getProduct().getName() + "&qty[]=" + lineItem.getQuantity() + "&price[]=" + lineItem.getProduct().getUnitPrice() + "&number[]=" + lineItem.getProduct().getBarcode() + "&phone=" + input + "&discount=" + discount;
                //  name[i]=lineItem.getProduct().getName();
                // price[i]= lineItem.getProduct().getUnitPrice();
                // quantity[i]=lineItem.getQuantity();
                // number[i]=lineItem.getId();
                i++;

            }

            HttpURLConnection connection;
            OutputStreamWriter request = null;

            URL url = null;
            String response = null;
/*
        JSONObject obj=new JSONObject();
		obj.put("username",mUsername);
		obj.put("password",mPassword);
		obj.put("db",db);

		//System.out.print(obj);
*/
//        String parameters = "session={'username':'nishammpm@gmail.com','password':'nisham','db':'cre8bill_db1'}"+"&number[]="+number+"&name[]="+name+"&qty[]="+quantity+"&price[]="+price;
            String parameters = par + "&phone=" + input + "&discount=" + discount;
            //String parameters="";
            try {
                url = new URL("http://gohevvy.com/phone/index.php?_route=app/app-post");
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestMethod("POST");

                request = new OutputStreamWriter(connection.getOutputStream());
                request.write(par);
                request.flush();
                request.close();
                String line = "";
                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                // Response from server after login process will be stored in response variable.
                response = sb.toString();
                // You can perform UI operations here
                //   Toast.makeText(getActivity(),"Message from Server: \n"+ discount,Toast.LENGTH_SHORT).show();
                isr.close();
                reader.close();

            } catch (IOException e) {
                //          return new String("Exception: " + e.getMessage());

                // Error
            }


            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            Log.e("Response", s);

//            Toast.makeText(getContext(), "Message from Server: \n" + s, Toast.LENGTH_SHORT).show();

        }
    }

}
