package com.refresh.printerhelper;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.refresh.pos.R;
import com.refresh.printerhelper.utils.AidlUtil;
import com.refresh.printerhelper.utils.BytesUtil;

import sunmi.sunmiui.dialog.DialogCreater;
import sunmi.sunmiui.dialog.EditTextDialog;

/**
 * Created by Alpesh Makwana on 22/1/18.
 */

public class BaseActivity extends FragmentActivity {

    public BaseApp baseApp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseApp = (BaseApp) getApplication();
    }

    /**
     * 设置标题
     * @param title
     */
    public void setMyTitle(String title){
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(title);
    }

    public void setMyTitle(@StringRes int title){
        ActionBar actionBar = getActionBar();;
        actionBar.setTitle(title);
        if(!baseApp.isAidl()){
            actionBar.setSubtitle("bluetooth®");
        }else{
            actionBar.setSubtitle("");
        }
    }

    public void setBack(){
        ActionBar actionBar = getActionBar();;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hexprint, menu);
        return true;
    }

    EditTextDialog mEditTextDialog;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_print:
                //Toast.makeText(this, "将实现十六进制指令发送", Toast.LENGTH_SHORT).show();
//                mEditTextDialog = DialogCreater.createEditTextDialog(this, "取消", "确定", "请输入指令：", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mEditTextDialog.cancel();
//                    }
//                }, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String text = mEditTextDialog.getEditText().getText().toString();
//                        AidlUtil.getInstance().sendRawData(BytesUtil.getBytesFromHexString(text));
//                        mEditTextDialog.cancel();
//                    }
//                }, null);
//                mEditTextDialog.show();


                mEditTextDialog = DialogCreater.createEditTextDialog(this, "Cancel", "\n" +
                        "determine", "Please enter the instruction：", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mEditTextDialog.cancel();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = mEditTextDialog.getEditText().getText().toString();
                        AidlUtil.getInstance().sendRawData(BytesUtil.getBytesFromHexString(text));
                        mEditTextDialog.cancel();
                    }
                }, null);
                mEditTextDialog.show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
