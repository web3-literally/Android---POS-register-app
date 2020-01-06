package com.refresh.pos.ui.inventory;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentIntegratorSupportV4;
import com.google.zxing.integration.android.IntentResult;
import com.refresh.pos.R;
import com.refresh.pos.domain.inventory.Inventory;
import com.refresh.pos.domain.inventory.ProductCatalog;
import com.refresh.pos.techicalservices.NoDaoSetException;
import com.refresh.pos.ui.component.UpdatableFragment;

//import org.json.JSONException;
//import org.json.JSONObject;
import android.net.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A dialog of adding a Product.
 *
 * @author Refresh Team
 */
@SuppressLint("ValidFragment")
public class AddProductDialogFragment extends DialogFragment {

    private EditText barcodeBox;
    private ProductCatalog productCatalog;
    private Button scanButton;
    private EditText priceBox;
    private EditText nameBox;
    private EditText Quantity;
    private Button confirmButton;
    private Button clearButton;
    private UpdatableFragment fragment;
    private Resources res;

    public AddProductDialogFragment(UpdatableFragment fragment) {

        super();
        this.fragment = fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            productCatalog = Inventory.getInstance().getProductCatalog();
        } catch (NoDaoSetException e) {
            e.printStackTrace();
        }

        View v = inflater.inflate(R.layout.layout_addproduct, container,
                false);

        res = getResources();

        barcodeBox = (EditText) v.findViewById(R.id.barcodeBox);
        scanButton = (Button) v.findViewById(R.id.scanButton);
        priceBox = (EditText) v.findViewById(R.id.priceBox);
        nameBox = (EditText) v.findViewById(R.id.nameBox);
        Quantity = (EditText) v.findViewById(R.id.Quantity);
        confirmButton = (Button) v.findViewById(R.id.confirmButton);
        clearButton = (Button) v.findViewById(R.id.clearButton);

        initUI();
        return v;
    }

    protected void tryLogin() {


    }

    /*
    public void postData(String toPost) {
// Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://www.URL.com/yourpage.php");

//This is the data to send
        String MyName = 'adil'; //any data to send

        try {
// Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("action", MyName));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

// Execute HTTP Post Request

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String response = httpclient.execute(httppost, responseHandler);

//This is the response from a php application
            String reverseString = response;
            Toast.makeText(this, "response" + reverseString, Toast.LENGTH_LONG).show();

        } catch (ClientProtocolException e) {
            Toast.makeText(this, "CPE response " + e.toString(), Toast.LENGTH_LONG).show();
// TODO Auto-generated catch block
        } catch (IOException e) {
            Toast.makeText(this, "IOE response " + e.toString(), Toast.LENGTH_LONG).show();
// TODO Auto-generated catch block
        }

    }//end postData()
*/
    private void initUI() {
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegratorSupportV4 scanIntegrator = new IntentIntegratorSupportV4(AddProductDialogFragment.this);
                scanIntegrator.initiateScan();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (nameBox.getText().toString().equals("")
                        || barcodeBox.getText().toString().equals("")
                        || priceBox.getText().toString().equals("")) {

                    Toast.makeText(getActivity().getBaseContext(),
                            res.getString(R.string.please_input_all), Toast.LENGTH_SHORT)
                            .show();

                } else {
                    boolean success = productCatalog.addProduct(nameBox
                            .getText().toString(), barcodeBox.getText()
                            .toString(), Double.parseDouble(priceBox.getText()
                            .toString()));

                    if (success) {

                        Toast.makeText(getActivity().getBaseContext(),
                                res.getString(R.string.success) + ", "
                                        + nameBox.getText().toString(),
                                Toast.LENGTH_SHORT).show();
                        Editable number = barcodeBox.getText();
                        Editable names = nameBox.getText();
                        Editable qty = Quantity.getText();
                        Editable price = priceBox.getText();
                        new LoginAsyncTask(number, names, qty, price).execute();

                        fragment.update();
                        clearAllBox();
                        AddProductDialogFragment.this.dismiss();

                    } else {
                        Toast.makeText(getActivity().getBaseContext(),
                                res.getString(R.string.fail),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (barcodeBox.getText().toString().equals("") && nameBox.getText().toString().equals("") && priceBox.getText().toString().equals("")) {
                    AddProductDialogFragment.this.dismiss();
                } else {
                    clearAllBox();
                }
            }
        });
    }

    private void clearAllBox() {
        barcodeBox.setText("");
        nameBox.setText("");
        priceBox.setText("");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, intent);

        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            barcodeBox.setText(scanContent);
        } else {
            Toast.makeText(getActivity().getBaseContext(),
                    res.getString(R.string.fail),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private class LoginAsyncTask extends AsyncTask<Void, Void, String> {

        private Editable number;
        private Editable name;
        private Editable quantity;
        private Editable price;


        public LoginAsyncTask(Editable number, Editable name, Editable quantity, Editable price) {
            this.number = number;
            this.name = name;
            this.quantity = quantity;
            this.price = price;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String mUsername;
            String mPassword;
            String db;
            mUsername = "nishammpm@gmail.com";
            mPassword = "nisham";
            db = "cre8bill_db19";
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
            String parameters = "session={'username':'nishammpm@gmail.com','password':'nisham','db':'cre8bill_db1'}" + "&number=" + number + "&name=" + name + "&qty=" + quantity + "&price=" + price;
            //String parameters="";
            try {
                url = new URL("http://gohevvy.com/phone/index.php?_route=app/add-product");
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestMethod("POST");

                request = new OutputStreamWriter(connection.getOutputStream());
                request.write(parameters);
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

                isr.close();
                reader.close();

            } catch (IOException e) {
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

