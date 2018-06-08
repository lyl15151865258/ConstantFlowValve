package cn.njmeter.constantflowvalve.aboutapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.njmeter.constantflowvalve.R;
import cn.njmeter.constantflowvalve.activity.BaseActivity;
import cn.njmeter.constantflowvalve.aboutapp.adapter.ContactsAdapter;
import cn.njmeter.constantflowvalve.aboutapp.bean.Contacts;
import cn.njmeter.constantflowvalve.constant.PhoneNumber;
import cn.njmeter.constantflowvalve.utils.ActivityController;
import cn.njmeter.constantflowvalve.widget.MyToolbar;
import cn.njmeter.constantflowvalve.widget.RecyclerViewDivider;

/**
 * 联系我们
 * Created by Li Yuliang on 2018/01/17.
 *
 * @author LiYuliang
 * @version 2018/01/17
 */

public class ContactUsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        MyToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.initToolBar(this, toolbar, getString(R.string.ContactUs), R.drawable.back_white, onClickListener);
        RecyclerView rvContacts = findViewById(R.id.rv_contacts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvContacts.setLayoutManager(linearLayoutManager);
        rvContacts.addItemDecoration(new RecyclerViewDivider(this, LinearLayoutManager.HORIZONTAL, 2, ContextCompat.getColor(this, R.color.gray_slight)));
        List<Contacts> contactsList = new ArrayList<>();
        contactsList.add(new Contacts("售后服务", PhoneNumber.CUSTOMER_SERVICE));
        contactsList.add(new Contacts("平台软件", PhoneNumber.SYSTEM_TECHNICAL_SUPPORT));
        contactsList.add(new Contacts("手机软件", PhoneNumber.APP_TECHNICAL_SUPPORT));
        contactsList.add(new Contacts("实名认证", PhoneNumber.APP_TECHNICAL_SUPPORT));
        ContactsAdapter contactsAdapter = new ContactsAdapter(contactsList);
        contactsAdapter.setOnItemClickListener((view, position) -> {
            Intent intentCall = new Intent(Intent.ACTION_DIAL);
            intentCall.setData(Uri.parse("tel:" + contactsList.get(position).getPhoneNumber()));
            startActivity(intentCall);
        });
        rvContacts.setAdapter(contactsAdapter);
    }

    private View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()) {
            case R.id.iv_left:
                ActivityController.finishActivity(this);
                break;
            default:
                break;
        }
    };

}
