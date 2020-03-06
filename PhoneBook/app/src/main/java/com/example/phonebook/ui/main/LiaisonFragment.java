package com.example.phonebook.ui.main;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.TestLooperManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.phonebook.DataBaseOperation;
import com.example.phonebook.IncomingCallReceiver;
import com.example.phonebook.MainActivity;
import com.example.phonebook.R;

public class LiaisonFragment extends Fragment {
    public DataBaseOperation operation;
    private Context context;
    public LiaisonAdapter liaisonAdapter;
    public LiaisonFragment(Context context) {
        super();
        this.context = context;
        this.operation = new DataBaseOperation(this.context,"Record");
        liaisonAdapter = new LiaisonAdapter(
                operation.fetchGroup(), context, R.layout.liaison_group, R.layout.liaison_item,
                new String[]{"name"}, new int[]{R.id.groupName}, new String[]{"phone","name","location","file"}, new int[] {R.id.liaisonNumber,R.id.liaisonName,R.id.liaisonLocation,R.id.file}
        );
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.liaison_fragment, container, false);
        final MainActivity activity = (MainActivity)getActivity();
        this.operation.UpdateDataFromSystemLiaison((MainActivity)getActivity(), this.context);
        UpdateBlackList();
        ExpandableListView listView = root.findViewById(R.id.liaisonList);
        listView.setAdapter(liaisonAdapter);

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (liaisonAdapter.CheckBoxVisible) {
                    TextView text = v.findViewById(R.id.liaisonNumber);
                    if (text != null) {
                        String number = text.getText().toString();
                        if (liaisonAdapter.Selected.contains(number)) liaisonAdapter.Selected.remove(number); else liaisonAdapter.Selected.add(number);
                        liaisonAdapter.notifyDataSetChanged();
                    }
                }
                else {
                    TextView text = v.findViewById(R.id.liaisonNumber);
                    String phone = text.getText().toString();
                    Intent intent = new Intent(getActivity(), LiaisonShowActivity.class);
                    intent.putExtra("phone", phone);
                    startActivity(intent);
                }
                return false;
            }
        });
        final Button EditButton = root.findViewById(R.id.EditButton);
        final Button SetGroupButton = root.findViewById(R.id.SetGroupButton);
        final Button DeleteLiaisonButton = root.findViewById(R.id.DeleteLiaisonButton);
        final Button DeleteGroupButton = root.findViewById(R.id.DeleteGroupButton);
        final Button AddGroupButton = root.findViewById(R.id.AddGroupButton);
        final Button AddLiaisonButton = root.findViewById(R.id.addLiaison);
        AddLiaisonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditActivity.class);
                intent.putExtra("edit",0);
                startActivityForResult(intent,1);
            }
        });
        EditButton.setText("编辑");
        EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liaisonAdapter.CheckBoxVisible = ! liaisonAdapter.CheckBoxVisible;
                liaisonAdapter.notifyDataSetChanged();
                liaisonAdapter.Selected.clear();
                if (liaisonAdapter.CheckBoxVisible) {
                    EditButton.setText("返回"); SetGroupButton.setVisibility(View.VISIBLE); DeleteLiaisonButton.setVisibility(View.VISIBLE);
                    DeleteGroupButton.setVisibility(View.GONE); AddGroupButton.setVisibility(View.GONE);
                }
                else {
                    EditButton.setText("编辑"); SetGroupButton.setVisibility(View.GONE); DeleteLiaisonButton.setVisibility(View.GONE);
                    DeleteGroupButton.setVisibility(View.VISIBLE); AddGroupButton.setVisibility(View.VISIBLE);
                }
            }
        });
        SetGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (liaisonAdapter.Selected.size() > 0) ShowChooseDialogSetGroup();
            }
        });
        DeleteGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDeleteDialog();
            }
        });
        DeleteLiaisonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String number : liaisonAdapter.Selected) operation.DeleteLiaison(number);
                activity.refreshRecord();
                liaisonAdapter.notifyDataSetChanged();
            }
        });
        AddGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowEditDialog();
            }
        });
        final SearchView searchView = root.findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                liaisonAdapter.searchString = query;
                liaisonAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                liaisonAdapter.searchString = newText;
                liaisonAdapter.notifyDataSetChanged();
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                liaisonAdapter.searchString = "";
                liaisonAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return root;
    }

    private void refreshListView() {
        this.operation.UpdateDataFromSystemLiaison((MainActivity)getActivity(), this.context);
        Cursor cursor = operation.fetchGroup();
        liaisonAdapter.setGroupCursor(cursor);
    }

    public void ShowEditDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(context, R.layout.group_add, null);
        dialog.setView(dialogView);
        dialog.show();
        final EditText editText = dialogView.findViewById(R.id.groupEditName);
        final Button EnsureButton = dialogView.findViewById(R.id.EnsureButton);
        EnsureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().length() > 0) operation.Insert(editText.getText().toString());
                liaisonAdapter.setGroupCursor(operation.fetchGroup());
                liaisonAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
    }

    public void ShowDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(context, R.layout.move_group, null);
        dialog.setView(dialogView);
        dialog.show();
        final ListView listView = dialogView.findViewById(R.id.SelectGroup);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        final SingleChoiceAdapter singleChoiceAdapter = new SingleChoiceAdapter(
                context, R.layout.single_choice, operation.fetchGroup(), new String[]{"name"}, new int[]{R.id.singleGroupName}, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, listView
        );
        final Button EnsureButton = dialogView.findViewById(R.id.EnsureButton);
        EnsureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operation.DeleteGroup(singleChoiceAdapter.SelectedGroup);
                liaisonAdapter.setGroupCursor(operation.fetchGroup());
                liaisonAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
    }

    public void ShowChooseDialogSetGroup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(context, R.layout.move_group, null);
        dialog.setView(dialogView);
        dialog.show();
        final ListView listView = dialogView.findViewById(R.id.SelectGroup);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        final SingleChoiceAdapter singleChoiceAdapter = new SingleChoiceAdapter(
                context, R.layout.single_choice, operation.fetchGroup(), new String[]{"name"}, new int[]{R.id.singleGroupName}, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, listView
        );
        final Button EnsureButton = dialogView.findViewById(R.id.EnsureButton);
        EnsureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String number : liaisonAdapter.Selected)
                    operation.UpdateLiaisonGroup(number, singleChoiceAdapter.SelectedGroup);
                liaisonAdapter.Selected.clear();
                liaisonAdapter.notifyDataSetChanged();
                UpdateBlackList();
                dialog.dismiss();
            }
        });
    }

    public void UpdateBlackList(){
        Cursor cursor = operation.helper.getReadableDatabase().query("LiaisonMan",null,"groupID = ?",new String[]{"黑名单"},null,null,null);
        IncomingCallReceiver.UpdateHashSet(cursor);
    }
}
