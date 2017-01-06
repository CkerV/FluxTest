package com.ckev.fluxtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ckev.fluxtest.actions.ActionCreator;
import com.ckev.fluxtest.dispatcher.Dispatcher;
import com.ckev.fluxtest.stores.LoginStore;
import com.ckev.fluxtest.stores.Store;

import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText mEtUsername;
    private EditText mEtPassword;
    private Button mBtnLogin;

    private ActionCreator mActionCreator;
    private Dispatcher mDispatcher;
    private LoginStore mStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initVariables();
        initViews();

    }

    private void initVariables() {
        mStore = new LoginStore();
        mDispatcher = Dispatcher.getInstance();
        mActionCreator = ActionCreator.getInstance(mDispatcher);
        mDispatcher.register(mStore);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mStore.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStore.unRegister(this);
    }

    private void initViews() {

        mEtUsername = (EditText) findViewById(R.id.main_et_username);
        mBtnLogin = (Button) findViewById(R.id.main_btn_login);
        mEtPassword = (EditText) findViewById(R.id.main_et_password);
        mBtnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_btn_login :
                mActionCreator.login(mEtUsername.getText().toString(), mEtPassword.getText().toString());
                break;
            default : break;
        }
    }

    @Subscribe
    public void onChange(Store.StoreChangeEvent changeEvent) {
       if(mStore.getLoginResponseBean().isSuccess()) {
           Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_LONG).show();
       } else {
           Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_LONG).show();
       }
    }
}
