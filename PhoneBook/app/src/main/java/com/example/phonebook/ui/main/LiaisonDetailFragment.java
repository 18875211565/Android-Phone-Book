package com.example.phonebook.ui.main;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.phonebook.LiaisonMan;
import com.example.phonebook.R;

public class LiaisonDetailFragment extends Fragment {
    private LiaisonMan liaisonMan;
    public LiaisonDetailFragment(LiaisonMan liaisonMan){
        this.liaisonMan = liaisonMan;
    }
    private View root;
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.liaisonman_detail, container, false);
        TextView textView = root.findViewById(R.id.phone_name);
        textView.setText("电话: " + liaisonMan.number);
        textView = root.findViewById(R.id.location);
        textView.setText("地点: " + liaisonMan.location);
        textView = root.findViewById(R.id.group);
        textView.setText("群组: " + liaisonMan.group);
        textView = root.findViewById(R.id.weather);
        textView.setText("天气: " + LiaisonShowActivity.weather);
        textView = root.findViewById(R.id.temp);
        textView.setText("温度: "+ LiaisonShowActivity.temp);
        Button messageButton = root.findViewById(R.id.messageButton);
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowEditDialog();
            }
        });
        Button callButton =  root.findViewById(R.id.callButton);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                Uri data = Uri.parse("tel:" + liaisonMan.number);
                intent.setData(data);
                startActivity(intent);
            }
        });
        return root;
    }

    public void ShowEditDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(getActivity(), R.layout.message_edit, null);
        dialog.setView(dialogView);
        dialog.show();
        final EditText editText = dialogView.findViewById(R.id.groupEditName);
        final Button EnsureButton = dialogView.findViewById(R.id.EnsureButton);
        String message = liaisonMan.name + "你好，最近过得怎么样？ " + "你那现在是" + LiaisonShowActivity.weather + "天气对吧？ 记得多注意身体健康。";
        editText.setText(message);
        EnsureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().length() > 0) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(liaisonMan.number,null,editText.getText().toString(),null,null);
                }
                dialog.dismiss();
            }
        });
    }
}
