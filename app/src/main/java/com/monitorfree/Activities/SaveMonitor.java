package com.monitorfree.Activities;


import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.monitorfree.MyApp;
import com.monitorfree.R;
import com.monitorfree.RequestModel.UserRequests;
import com.monitorfree.UserModel.AddMonitor;
import com.monitorfree.databinding.ActivitySaveMonitorBinding;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import javax.inject.Inject;

import static com.monitorfree.Util.GlobalKeys.USER_HASH;
import static com.monitorfree.Util.GlobalKeys.USER_ID;

public class SaveMonitor extends AppCompatActivity implements CallBackSuccess, View.OnFocusChangeListener, DatePickerDialog.OnDateSetListener {

    ActivitySaveMonitorBinding binding;

    @Inject
    MyApp myApp;

//    @Inject
    UserRequests userRequests = new UserRequests();

//    @Inject
    AddMonitor addMonitor = new AddMonitor();

    CallBackSuccess callBackSuccess;

    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_save_monitor);
//        MyApp.component().inject(this);

        callBackSuccess = this;
        type = getIntent().getStringExtra("type");

        if (type.equals("3")) {
            binding.txtMonitor.setText("Creating keyword monitor");
            binding.edtMeta.setVisibility(View.VISIBLE);
            binding.edtMeta.setHint("Enter Search Keyword");

        } else if (type.equals("2")) {
            binding.edtWebAddress.setHint("e.g 8.8.8.8");
            binding.txtMonitor.setText("Creating ping monitor");
        } else if (type.equals("4")){
            binding.txtMonitor.setText("Creating port monitor");
            binding.edtPort.setVisibility(View.VISIBLE);
            binding.edtPort.setHint("Enter Port");
        }

        binding.include.imgVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.include.txtVw.setText("Add Monitor");
        binding.edTxtDate.setOnFocusChangeListener(this);

        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                binding.txtVwInterval.setText("" + progress + "/100");
                addMonitor.setInterval(String.valueOf(progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void clickSave(View view) {

        switch (view.getId()) {
            case R.id.edTxtDate:
                funShowDOBDialog();
                break;

            case R.id.btnSaveMonitor:

                addMonitor.setName(binding.edtFriendlyName.getText().toString());
                addMonitor.setAddress(binding.edtWebAddress.getText().toString());
                addMonitor.setType(type);
                addMonitor.setStartDate(binding.edTxtDate.getText().toString());
                addMonitor.setUserId(MyApp.instance.getKey(USER_ID));
                addMonitor.setKeywords("");
                addMonitor.setPort("");

                if (addMonitor.getName() == null || addMonitor.getName().equals("")) {
                    binding.edtFriendlyName.setError("Please Enter Name");
                } else if (addMonitor.getAddress() == null || addMonitor.getAddress().equals("")) {
                    binding.edtWebAddress.setError("Please Enter Address");
                } else if (checkWebAddress(addMonitor.getAddress()) == false) {
                    binding.edtWebAddress.setError("Web Address Invalid");
                } else if (addMonitor.getStartDate() == null || addMonitor.getStartDate().equals("")) {
                    binding.edTxtDate.setError("Please Select Date");
                } else {
                    if(type.equals("1") || type.equals("3") || type.equals("4")) {
                        if (!checkPortocal(addMonitor.getAddress())) {
                            binding.edtWebAddress.setError("Please enter web address corretly");
                            break;
                        }
                    }

                    if (type.equals("3")) {
                        addMonitor.setKeywords(binding.edtMeta.getText().toString());
                    } else if (type.equals("4")) {
                        addMonitor.setPort(binding.edtPort.getText().toString());
                    }

                    userRequests.funAddMonitor(MyApp.instance, addMonitor, binding, SaveMonitor.this, callBackSuccess);

                }
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if (hasFocus) {
            funShowDOBDialog();
        }
    }


    void funShowDOBDialog() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                SaveMonitor.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)

        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.show(getFragmentManager(), "Datepickerdialog");

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        monthOfYear+=1;
        String month = (monthOfYear < 10) ? String.format("0%s", monthOfYear) : String.valueOf(monthOfYear);

        binding.edTxtDate.setError(null);
        binding.edTxtDate.setText(String.format("%d-%s-%d", year, month, dayOfMonth));
        binding.edTxtDate.setSelection(binding.edTxtDate.getText().length());
    }

    @Override
    public void success(Object object) {

      //  myApp.getDao(myApp.getContext()).insertChat(addMonitor);
        onBackPressed();
    }


    boolean checkWebAddress(String address) {
        return Patterns.WEB_URL.matcher(address).matches();
    }

    boolean checkPortocal(String address) {

        if (address.toLowerCase().contains("http".toLowerCase()) || address.toLowerCase().contains("https".toLowerCase())){
            return true;
        }

        return false;
    }


}
