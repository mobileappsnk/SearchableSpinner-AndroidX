package nk.mobileapps.spinnerex;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import nk.mobileapps.spinnerlib.SearchableMultiSpinner;
import nk.mobileapps.spinnerlib.SearchableSpinner;
import nk.mobileapps.spinnerlib.SpinnerData;

/*
 * @author mobileapps.nk@gmail.com
 */

public class MainActivity extends AppCompatActivity implements SearchableSpinner.SpinnerListener, SearchableMultiSpinner.SpinnerListener {

    SearchableSpinner sp_dist, sp_mandal, sp_without_search;
    SearchableMultiSpinner smp, smp_fixed, smp_limit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
    }

    private void findViews() {
        sp_dist = findViewById(R.id.sp_dist);
        sp_mandal = findViewById(R.id.sp_mandal);
        sp_without_search = findViewById(R.id.sp_without_search);
        smp = findViewById(R.id.smp);
        smp_fixed = findViewById(R.id.smp_fixed);
        smp_limit = findViewById(R.id.smp_limit);
        loadDist();
        loadSpinner_WithoutSearch();
        loadMultiSpinner();
        loadMultiSpinner_Fixed();
        loadMultiSpinner_Limit();
        smp_fixed.setFixedItemIDForRemainingUnCheck("12");
        smp_limit.setLimit(3, new SearchableMultiSpinner.LimitExceedListener() {
            @Override
            public void onLimitExceed(int limitexceed) {
                Toast.makeText(MainActivity.this, "Limit Exceed! " + limitexceed, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadMultiSpinner_Fixed() {
        String dist = Helper.readTextFile(getApplicationContext(), R.raw.dist);
        String[] distString = dist.split("\\\n");

        List<SpinnerData> ll_dist = new ArrayList<>();

        for (int i = 0; i < distString.length; i++) {
            String[] splitD = distString[i].split("\\^");

            SpinnerData spinnerData = new SpinnerData();

            spinnerData.setId(splitD[0]);
            spinnerData.setName(splitD[1]);

            ll_dist.add(spinnerData);
        }
        smp_fixed.setItems(ll_dist, this);
    }

    private void loadMultiSpinner_Limit() {
        String dist = Helper.readTextFile(getApplicationContext(), R.raw.dist);
        String[] distString = dist.split("\\\n");

        List<SpinnerData> ll_dist = new ArrayList<>();

        for (int i = 0; i < distString.length; i++) {
            String[] splitD = distString[i].split("\\^");

            SpinnerData spinnerData = new SpinnerData();

            spinnerData.setId(splitD[0]);
            spinnerData.setName(splitD[1]);

            ll_dist.add(spinnerData);
        }
        smp_limit.setItems(ll_dist, this);
    }

    private void loadMultiSpinner() {
        String dist = Helper.readTextFile(getApplicationContext(), R.raw.dist);
        String[] distString = dist.split("\\\n");

        List<SpinnerData> ll_dist = new ArrayList<>();

        for (int i = 0; i < distString.length; i++) {
            String[] splitD = distString[i].split("\\^");

            SpinnerData spinnerData = new SpinnerData();

            spinnerData.setId(splitD[0]);
            spinnerData.setName(splitD[1]);

            ll_dist.add(spinnerData);
        }
        smp.setItems(ll_dist, this);
    }

    public void loadSpinner_WithoutSearch() {
        String dist = Helper.readTextFile(getApplicationContext(), R.raw.dist);
        String[] distString = dist.split("\\\n");

        List<SpinnerData> ll_dist = new ArrayList<>();

        for (int i = 0; i < distString.length; i++) {
            String[] splitD = distString[i].split("\\^");

            SpinnerData spinnerData = new SpinnerData();

            spinnerData.setId(splitD[0]);
            spinnerData.setName(splitD[1]);

            ll_dist.add(spinnerData);
        }
        sp_without_search.setItems(ll_dist);


    }

    public void loadDist() {


        String dist = Helper.readTextFile(getApplicationContext(), R.raw.dist);
        String[] distString = dist.split("\\\n");

        List<SpinnerData> ll_dist = new ArrayList<>();

        for (int i = 0; i < distString.length; i++) {
            String[] splitD = distString[i].split("\\^");

            SpinnerData spinnerData = new SpinnerData();

            spinnerData.setId(splitD[0]);
            spinnerData.setName(splitD[1]);

            ll_dist.add(spinnerData);
        }


        sp_dist.setItems(ll_dist, this);
        sp_mandal.setItems(new ArrayList<SpinnerData>(), this);

    }

    public void loadMandal(int distPos, String selectMandalID) {

        List<SpinnerData> ll_mandal = new ArrayList<>();
        String mandal = Helper.readTextFile(getApplicationContext(), R.raw.mandals);
        String[] mandalString = mandal.split("\\\n")[distPos].split("\\,");

        for (int i = 0; i < mandalString.length; i++) {
            String[] splitD = mandalString[i].split("\\^");
            SpinnerData spinnerData = new SpinnerData();
            spinnerData.setId(splitD[0]);
            spinnerData.setName(splitD[1]);
            ll_mandal.add(spinnerData);
        }

        sp_mandal.setItems(ll_mandal, this);

        if (!selectMandalID.trim().equals("")) {
            sp_mandal.setItemID(selectMandalID);
            sp_mandal.setEnabled(false);
        }


    }

    @Override
    public void onItemsSelected(View v, List<SpinnerData> items, int position) {

        if (v == sp_dist) {
            if (sp_dist.isSelected()) {
                loadMandal(sp_dist.getSelectedItemPosition(), "");
            }
        }

    }

    public void onClick_ValidateSpinner(View view) {
        if (isValidate()) {
            Toast.makeText(MainActivity.this, "Validation Done!", Toast.LENGTH_LONG).show();
        }
    }

    public void setViewFocus(View v) {
        v.setFocusableInTouchMode(true);
        v.setFocusable(true);
        v.requestFocus();
    }

    private boolean isValidate() {
        boolean flag = true;
        if (!sp_dist.isSelected()) {
            Toast.makeText(MainActivity.this, "Please Select Spinner sp_dist...", Toast.LENGTH_LONG).show();
            setViewFocus(sp_dist);
            flag = false;
        } else if (!sp_mandal.isSelected()) {
            Toast.makeText(MainActivity.this, "Please Select Spinner sp_mandal...", Toast.LENGTH_LONG).show();
            setViewFocus(sp_mandal);
            flag = false;
        } else if (!sp_without_search.isSelected()) {
            Toast.makeText(MainActivity.this, "Please Select Spinner sp_without_search...", Toast.LENGTH_LONG).show();
            setViewFocus(sp_without_search);
            flag = false;
        } else if (!smp.isSelected()) {
            Toast.makeText(MainActivity.this, "Please Select Spinner smp...", Toast.LENGTH_LONG).show();
            setViewFocus(smp);
            flag = false;
        }
        return flag;
    }

    @Override
    public void onItemsSelected(View v, List<SpinnerData> items, List<SpinnerData> selectedItems) {

    }
}
