package nk.mobileapps.spinnerlib;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSpinner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mobileapps.nk@gmail.com
 * @created on 20-Dec-2017 at 12:15:41 PM
 */

public class SearchableMultiSpinner extends AppCompatSpinner implements OnCancelListener {
    private static final String TAG = SearchableMultiSpinner.class.getSimpleName();
    public static AlertDialog.Builder builder;
    public static AlertDialog ad;
    MyAdapter adapter;
    Context context;
    private List<SpinnerData> items;
    private String defaultText = "Select";
    private String spinnerTitle = "Select";
    private SpinnerListener listener;
    private int limit = -1;
    private int selected = 0;
    private String fixedItemID = "";

    private boolean searchable = true;
    private boolean singleLine = false;
    private ColorStateList textColorStateList = ColorStateList.valueOf(getResources().getColor(android.R.color.black));
    private LimitExceedListener limitListener;
    private boolean textAppear = true;
    private ColorStateList dialogTextColor = ColorStateList.valueOf(getResources().getColor(android.R.color.black));
    private boolean dialogTitleVisible = true;
    private String searchHintText = "Search Here...";

    public SearchableMultiSpinner(Context context) {
        super(context);
        this.context = context;
    }

    public SearchableMultiSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SearchableSpinner);


        for (int i = 0; i < a.getIndexCount(); ++i) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.SearchableSpinner_hintText) {
                spinnerTitle = a.getString(attr);
                defaultText = spinnerTitle;
            } else if (attr == R.styleable.SearchableSpinner_searchable) {
                searchable = a.getBoolean(attr, true);
            } else if (attr == R.styleable.SearchableSpinner_singleLine) {
                singleLine = a.getBoolean(attr, false);
            } else if (attr == R.styleable.SearchableSpinner_textColor) {
                textColorStateList = a.getColorStateList(R.styleable.SearchableSpinner_textColor);
            } else if (attr == R.styleable.SearchableSpinner_textAppear) {
                textAppear = a.getBoolean(attr, true);
            } else if (attr == R.styleable.SearchableSpinner_dialogTextColor) {
                dialogTextColor = a.getColorStateList(R.styleable.SearchableSpinner_dialogTextColor);
            } else if (attr == R.styleable.SearchableSpinner_dialogTitleVisible) {
                dialogTitleVisible = a.getBoolean(attr, true);
            } else if (attr == R.styleable.SearchableSpinner_searchHintText) {
                searchHintText = a.getString(attr);
            }
        }
        Log.i(TAG, "spinnerTitle: " + spinnerTitle);
        a.recycle();
    }

    public SearchableMultiSpinner(Context context, AttributeSet arg1, int arg2) {
        super(context, arg1, arg2);
        this.context = context;
    }

    public void setLimit(int limit, LimitExceedListener listener) {
        this.limit = limit;
        this.limitListener = listener;
    }

    public void setListener(SpinnerListener listener) {
        this.listener = listener;
    }

    public void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }


    public List<SpinnerData> getSelectedItems() {
        List<SpinnerData> selectedItems = new ArrayList<>();
        for (SpinnerData item : items) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }

    public boolean isSelected() {
        boolean isSelected = false;
        if (items != null)
            for (SpinnerData item : items) {
                if (item.isSelected()) {
                    isSelected = true;
                    break;
                }
            }
        return isSelected;
    }

    public List<String> getSelectedIds() {
        List<String> selectedItemsIds = new ArrayList<>();
        for (SpinnerData item : items) {
            if (item.isSelected()) {
                selectedItemsIds.add(item.getId());
            }
        }
        return selectedItemsIds;
    }

    public List<String> getSelectedNames() {
        List<String> selectedItemsNames = new ArrayList<>();
        for (SpinnerData item : items) {
            if (item.isSelected()) {
                selectedItemsNames.add(item.getName());
            }
        }
        return selectedItemsNames;
    }

    public List<Integer> getSelectedPositions() {
        List<Integer> selectedItemsPositions = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isSelected()) {
                selectedItemsPositions.add(i);
            }
        }
        return selectedItemsPositions;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        // refresh text on spinner
        List<SpinnerData> selectedItems = new ArrayList<>();

        StringBuilder spinnerBuffer = new StringBuilder();

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isSelected()) {
                selectedItems.add(items.get(i));
                spinnerBuffer.append(items.get(i).getName());
                spinnerBuffer.append(", ");
            }
        }

        String spinnerText = spinnerBuffer.toString();
        if (spinnerText.length() > 2)
            spinnerText = spinnerText.substring(0, spinnerText.length() - 2);
        else
            spinnerText = defaultText;


        setAdapter(textAppear == true ? spinnerText : "");


        if (adapter != null)
            adapter.notifyDataSetChanged();

        if (listener != null)
            listener.onItemsSelected(this, items, selectedItems);
    }

    private void setAdapter(String value) {
        if (singleLine) {
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, new String[]{value}) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = super.getView(position, convertView, parent);
                    ((TextView) v).setTextColor(textColorStateList);
                    return v;
                }
            };
            setAdapter(adapterSpinner);
        } else {
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, new String[]{value}) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = super.getView(position, convertView, parent);
                    ((TextView) v).setTextColor(textColorStateList);
                    return v;
                }
            };
            setAdapter(adapterSpinner);
        }
    }

    @Override
    public boolean performClick() {

        builder = new AlertDialog.Builder(getContext());
        if (dialogTitleVisible)
            builder.setTitle(spinnerTitle);


        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View view = inflater.inflate(R.layout.view_searchable_list_dialog, null);
        builder.setView(view);

        final ListView listView = view.findViewById(R.id.alertSearchListView);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        listView.setFastScrollEnabled(false);
        adapter = new MyAdapter(getContext(), items);

        listView.setAdapter(adapter);


        final TextView emptyText = view.findViewById(R.id.empty);
        listView.setEmptyView(emptyText);

        final EditText editText = view.findViewById(R.id.alertSearchEditText);
        editText.setHint(searchHintText);
        if (searchable) {
            editText.setVisibility(VISIBLE);
            editText.setEnabled(true);

            editText.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        } else {
            editText.setEnabled(false);
            editText.setVisibility(GONE);
            editText.addTextChangedListener(null);
        }

        builder.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Log.i(TAG, " ITEMS : " + items.size());
                dialog.cancel();
            }
        });


        builder.setOnCancelListener(this);
        ad = builder.show();
        return true;
    }


    public void setItems(List<SpinnerData> items, int[] positions, SpinnerListener listener) {

        this.items = items;
        this.listener = listener;

        StringBuilder spinnerBuffer = new StringBuilder();

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isSelected()) {
                spinnerBuffer.append(items.get(i).getName());
                spinnerBuffer.append(", ");
            }
        }
        if (spinnerBuffer.length() > 2)
            defaultText = spinnerBuffer.toString().substring(0, spinnerBuffer.toString().length() - 2);

        setAdapter(defaultText);

        setItemPositions(positions);

    }

    public void setItems(List<SpinnerData> items, String[] ids, SpinnerListener listener) {

        this.items = items;
        this.listener = listener;

        StringBuilder spinnerBuffer = new StringBuilder();

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isSelected()) {
                spinnerBuffer.append(items.get(i).getName());
                spinnerBuffer.append(", ");
            }
        }
        if (spinnerBuffer.length() > 2)
            defaultText = spinnerBuffer.toString().substring(0, spinnerBuffer.toString().length() - 2);


        setAdapter(defaultText);

        setItemIDs(ids);

    }

    public void setItems(List<SpinnerData> items, SpinnerListener listener) {

        this.items = items;
        this.listener = listener;

        StringBuilder spinnerBuffer = new StringBuilder();

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isSelected()) {
                spinnerBuffer.append(items.get(i).getName());
                spinnerBuffer.append(", ");
            }
        }
        if (spinnerBuffer.length() > 2)
            defaultText = spinnerBuffer.toString().substring(0, spinnerBuffer.toString().length() - 2);


        setAdapter(defaultText);


    }

    public void setItems(List<SpinnerData> items) {

        this.items = items;


        StringBuilder spinnerBuffer = new StringBuilder();

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isSelected()) {
                spinnerBuffer.append(items.get(i).getName());
                spinnerBuffer.append(", ");
            }
        }
        if (spinnerBuffer.length() > 2)
            defaultText = spinnerBuffer.toString().substring(0, spinnerBuffer.toString().length() - 2);


        setAdapter(defaultText);


    }

    private List<SpinnerData> getItemsFromArrayList(List<String> items) {

        List<SpinnerData> spinnerDataList = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            SpinnerData spinnerData = new SpinnerData();
            spinnerData.setId(i + "");
            spinnerData.setName(items.get(i).trim());
            spinnerDataList.add(spinnerData);
        }

        return spinnerDataList;
    }

    public void setArrayListItems(List<String> l_items, SpinnerListener listener) {

        this.items = getItemsFromArrayList(l_items);
        this.listener = listener;

        StringBuilder spinnerBuffer = new StringBuilder();

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isSelected()) {
                spinnerBuffer.append(items.get(i).getName());
                spinnerBuffer.append(", ");
            }
        }
        if (spinnerBuffer.length() > 2)
            defaultText = spinnerBuffer.toString().substring(0, spinnerBuffer.toString().length() - 2);


        setAdapter(defaultText);


    }

    private List<SpinnerData> getItemsFromStringArray(String[] l_items) {

        List<SpinnerData> spinnerDataList = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            SpinnerData spinnerData = new SpinnerData();
            spinnerData.setId(i + "");
            spinnerData.setName(items.get(i).toString().trim());
            spinnerDataList.add(spinnerData);
        }

        return spinnerDataList;
    }

    public void setStringArrayItems(String[] l_items, SpinnerListener listener) {

        this.items = getItemsFromStringArray(l_items);
        this.listener = listener;

        StringBuilder spinnerBuffer = new StringBuilder();

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isSelected()) {
                spinnerBuffer.append(items.get(i).getName());
                spinnerBuffer.append(", ");
            }
        }
        if (spinnerBuffer.length() > 2)
            defaultText = spinnerBuffer.toString().substring(0, spinnerBuffer.toString().length() - 2);


        setAdapter(defaultText);


    }

    public void clearSelections() {
        for (SpinnerData spinnerData : items) {
            spinnerData.setSelected(false);
        }
        setAdapter(defaultText);
    }

    //Based on POSITION item will select
    public void setItemPositions(int[] positions) {
        if (positions.length > 0 && items.size() > 0) {
            clearSelections();
            for (int pos = 0; pos < positions.length; pos++) {
                items.get(pos).setSelected(true);
            }
            onCancel(null);
        }
    }

    public void setFixedItemIDForRemainingUnCheck(String fixedItemID) {
        this.fixedItemID = fixedItemID;
    }


    //Based on ID item will select
    public void setItemIDs(String[] ids) {
        if (ids.length > 0 && items.size() > 0) {
            clearSelections();
            for (String id : ids) {
                for (int item = 0; item < items.size(); item++) {
                    if (items.get(item).getId().trim().equals(id)) {
                        items.get(item).setSelected(true);
                        break;
                    }
                }
            }
            onCancel(null);
        }
    }

    // public void setError(String error_msg) {


/*
        // Focus
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        // Error icon
        ((TextView)getSelectedView()).setError("");
        // Initialize a pop up window type
        PopupWindow popupWindow = new PopupWindow(this);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        // Initialize Text View & set error msg
        View popupView = getLayoutInflater().inflate(
                com.blue.camlib.R.layout.textview_hint, null);
        TextView txtView = (TextView) popupView.findViewById(com.blue.camlib.R.id.tv_errormsg);
        txtView.setTextColor(Color.WHITE);
        txtView.setText(error_msg);
        // Add popupview to popupWindow & set width & heigth to popupwindow
        popupWindow.setContentView(popupView);
        popupWindow.setWidth(260);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        // Show popupwindow
        popupWindow.showAsDropDown(spinner,
                spinner.getWidth() - popupWindow.getWidth(),
                0 - spinner.getHeight() / 2);
        // Set Background image to popupView
        if (popupWindow.isAboveAnchor()) {
            popupView
                    .setBackgroundResource(com.blue.camlib.R.drawable.popup_inline_error_above_holo_light);
        } else {
            popupView
                    .setBackgroundResource(com.blue.camlib.R.drawable.popup_inline_error_holo_light);
        }*/

    // }

    @Override
    public Object getSelectedItem() {

        StringBuilder spinnerBuffer = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isSelected()) {

                spinnerBuffer.append(items.get(i).getName());
                spinnerBuffer.append(", ");
            }
        }

        String spinnerText = spinnerBuffer.toString();
        if (spinnerText.length() > 2)
            spinnerText = spinnerText.substring(0, spinnerText.length() - 2);
        else
            spinnerText = defaultText;

        return spinnerText;
    }

    public interface LimitExceedListener {
        void onLimitExceed(int limitexceed);
    }

    public interface SpinnerListener {
        void onItemsSelected(View v, List<SpinnerData> items, List<SpinnerData> selectedItems);
    }

    //Adapter Class
    public class MyAdapter extends BaseAdapter implements Filterable {

        List<SpinnerData> arrayList;
        List<SpinnerData> mOriginalValues; // Original Values
        LayoutInflater inflater;

        public MyAdapter(Context context, List<SpinnerData> arrayList) {
            this.arrayList = arrayList;
            this.mOriginalValues = arrayList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            Log.i(TAG, "getView() enter");
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.view_item_listview_multiple, parent, false);
                holder.textView = convertView.findViewById(R.id.alertTextView);
                holder.checkBox = convertView.findViewById(R.id.alertCheckbox);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            final SpinnerData data = arrayList.get(position);

            holder.textView.setText(data.getName());
            holder.textView.setTypeface(null, Typeface.NORMAL);
            holder.textView.setTextColor(dialogTextColor);

            if (true) {
                holder.checkBox.setVisibility(VISIBLE);
                holder.checkBox.setChecked(data.isSelected());
                convertView.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {

                        if (data.isSelected()) { // deselect
                            selected--;
                        } else if (selected == limit) { // select with limit

                            if (data.getId().trim().equals(fixedItemID)) {
                                selected = 0;
                                selected++;
                                for (int i = 0; i < arrayList.size(); i++) {
                                    if (!arrayList.get(i).getId().equals(fixedItemID)) {
                                        View uncheckView = getView(position, null, parent);
                                        CheckBox checkBox = uncheckView.findViewById(R.id.alertCheckbox);
                                        checkBox.setChecked(false);
                                        arrayList.get(i).setSelected(false);
                                    }
                                }
                            } else {
                                if (limitListener != null)
                                    limitListener.onLimitExceed(limit);
                                return;
                            }


                        } else { // selected

                            if (data.getId().trim().equals(fixedItemID)) {
                                selected = 0;
                                selected++;
                                for (int i = 0; i < arrayList.size(); i++) {
                                    if (!arrayList.get(i).getId().equals(fixedItemID)) {
                                        View uncheckView = getView(position, null, parent);
                                        CheckBox checkBox = uncheckView.findViewById(R.id.alertCheckbox);
                                        checkBox.setChecked(false);
                                        arrayList.get(i).setSelected(false);
                                    }
                                }
                            } else {
                                selected++;
                                if (!fixedItemID.trim().equals(""))
                                    for (int i = 0; i < arrayList.size(); i++) {
                                        if (arrayList.get(i).getId().equals(fixedItemID) && arrayList.get(i).isSelected()) {
                                            View uncheckView = getView(position, null, parent);
                                            CheckBox checkBox = uncheckView.findViewById(R.id.alertCheckbox);
                                            checkBox.setChecked(false);
                                            arrayList.get(i).setSelected(false);
                                            selected--;
                                            break;
                                        }
                                    }
                            }
                        }

                        final ViewHolder temp = (ViewHolder) v.getTag();
                        temp.checkBox.setChecked(!temp.checkBox.isChecked());

                        data.setSelected(!data.isSelected());
                        Log.i(TAG, "On Click Selected Item : " + data.getName() + " : " + data.isSelected());
                        notifyDataSetChanged();


                    }
                });
                if (data.isSelected()) {
                    holder.textView.setTypeface(null, Typeface.BOLD);
                    holder.textView.setTextColor(dialogTextColor);

                    // convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.list_selected));
                } else {
                    holder.textView.setTypeface(null, Typeface.NORMAL);
                    holder.textView.setTextColor(dialogTextColor);
                }
                holder.checkBox.setTag(holder);
            }
            return convertView;
        }

        @SuppressLint("DefaultLocale")
        @Override
        public Filter getFilter() {
            return new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    arrayList = (List<SpinnerData>) results.values; // has the filtered values
                    notifyDataSetChanged();  // notifies the data with new filtered values
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                    List<SpinnerData> FilteredArrList = new ArrayList<>();

                    if (mOriginalValues == null) {
                        mOriginalValues = new ArrayList<>(arrayList); // saves the original data in mOriginalValues
                    }

                    /********
                     *
                     *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                     *  else does the Filtering and returns FilteredArrList(Filtered)
                     *
                     ********/
                    if (constraint == null || constraint.length() == 0) {

                        // set the Original result to return
                        results.count = mOriginalValues.size();
                        results.values = mOriginalValues;
                    } else {
                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < mOriginalValues.size(); i++) {
                            Log.i(TAG, "Filter : " + mOriginalValues.get(i).getName() + " -> " + mOriginalValues.get(i).isSelected());
                            String data = mOriginalValues.get(i).getName();
                            if (data.toLowerCase().contains(constraint.toString())) {
                                FilteredArrList.add(mOriginalValues.get(i));
                            }
                        }
                        // set the Filtered result to return
                        results.count = FilteredArrList.size();
                        results.values = FilteredArrList;
                    }
                    return results;
                }
            };
        }

        private class ViewHolder {
            TextView textView;
            CheckBox checkBox;
        }
    }
}