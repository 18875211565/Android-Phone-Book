package com.example.phonebook.ui.main;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.phonebook.DataBaseOperation;
import com.example.phonebook.LiaisonMan;
import com.example.phonebook.R;
public class LiaisonPhoneRecordFragment extends Fragment {
    public Context context;
    private DataBaseOperation operation;
    private LiaisonMan liaisonMan;
    private LiaisonPhoneRecordAdapter liaisonPhoneRecordAdapter;
    public LiaisonPhoneRecordFragment(Context context, LiaisonMan liaisonMan){
        this.context = context;
        this.liaisonMan = liaisonMan;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        operation = new DataBaseOperation(this.context,"Record");
        View root = inflater.inflate(R.layout.liaison_record, container, false);
        LiaisonMan liaisonMan = this.liaisonMan;
        Cursor cursor = operation.fetchChildrenPhone(liaisonMan.number);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            liaisonPhoneRecordAdapter = new LiaisonPhoneRecordAdapter(context, R.layout.liaison_phone_item, cursor, new String[]{}, new int[]{}, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            ListView listView = root.findViewById(R.id.liaisonRecordList);
            listView.setAdapter(liaisonPhoneRecordAdapter);
        }
        return root;
    }
}
