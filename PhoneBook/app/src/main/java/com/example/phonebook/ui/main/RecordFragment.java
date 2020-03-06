package com.example.phonebook.ui.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.phonebook.DataBaseOperation;
import com.example.phonebook.MainActivity;
import com.example.phonebook.R;

import java.util.List;

public class RecordFragment extends Fragment {
    private DataBaseOperation operation;
    private Context context;
    public RecordListAdapter recordAdapter;
    public RecordFragment(Context context) {
        super();
        this.context = context;
        this.operation = new DataBaseOperation(this.context,"Record");
        recordAdapter = new RecordListAdapter(
                operation.fetchPhoneGroup(), context, R.layout.record_group, R.layout.record_item,
                new String[]{"phone","date","location"}, new int[]{R.id.recordGroupNumber,R.id.recordGroupDate,R.id.recordGroupLocation}, new String[]{"phone","date","location"}, new int[] {R.id.recordNumber,R.id.recordDate,R.id.recordLocation});
    }

    @Override
    public void onStart(){
        super.onStart();
        this.operation.UpdateDataFromSystemRecord((MainActivity)getActivity(), this.context);
        refreshListView(false,"");
    }

    public void refresh(){
        this.operation.UpdateDataFromSystemRecord((MainActivity)getActivity(), this.context);
        refreshListView(false,"");
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.record_fragment, container, false);
        ExpandableListView ListView = root.findViewById(R.id.recordList);
        ListView.setAdapter(recordAdapter);
        setSearchView(root);
        return root;
    }

    private void setSearchView(View root){
        SearchView searchView = root.findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                refreshListView(true, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                refreshListView(true, newText);
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                refreshListView(false,"");
                return false;
            }
        });
    }

    private void refreshListView(boolean search, String searchString) {
        Cursor cursor;
        if (search) cursor = operation.helper.getReadableDatabase().query("PhoneRecord", null, "top = ? and (phone LIKE ? or location LIKE ?)", new String[]{1+"","%"+searchString+"%","%"+searchString+"%"}, null, null, "date DESC");
        else cursor = operation.helper.getReadableDatabase().query("PhoneRecord", null, "top=?", new String[]{1+""}, null, null, "date DESC");
        recordAdapter.changeCursor(cursor);
    }
}
