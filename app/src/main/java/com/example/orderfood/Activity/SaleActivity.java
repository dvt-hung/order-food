package com.example.orderfood.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.orderfood.Adapter.FoodDetailOrderAdapter;
import com.example.orderfood.Adapter.OrderManagerAdapter;
import com.example.orderfood.Model.Order;
import com.example.orderfood.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SaleActivity extends AppCompatActivity {

    private ImageView img_Back_Sale, img_Menu_Sale;
    private TextView txt_Title_Sale, txt_Total_Sale, txt_Start_Date, txt_End_Date, txt_Month;
    private RecyclerView recycler_Sale;
    private OrderManagerAdapter adapter_Sale;
    private final List<Order> listSale = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);

        // init View
        initView();
        // set Start,End Date Gone
        txt_Start_Date.setVisibility(View.GONE);
        txt_End_Date.setVisibility(View.GONE);
        txt_Month.setVisibility(View.GONE);

        // Recycler Sale
        adapter_Sale = new OrderManagerAdapter(new OrderManagerAdapter.IOrderManagerListener() {
            @Override
            public void itemOrderManagerClick(Order order) {
                showDialogDetailsSale(order);
            }
        });
        recycler_Sale.setAdapter(adapter_Sale);

        // Layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recycler_Sale.setLayoutManager(layoutManager);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Menu
        img_Menu_Sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
            }
        });
        // Back
        img_Back_Sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    // =============== SHOW DIALOG DETAIL ===============

    @SuppressLint("SetTextI18n")
    private void showDialogDetailsSale(Order order) {
        Dialog dialogDetailSale = new Dialog(SaleActivity.this);
        dialogDetailSale.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDetailSale.setContentView(R.layout.dialog_details_sale);

        Window windowDetailsOrder = dialogDetailSale.getWindow();
        windowDetailsOrder.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        windowDetailsOrder.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        // init view in dialog
        TextView txt_Name_Detail_Sale = dialogDetailSale.findViewById(R.id.txt_Name_Detail_Sale);
        TextView txt_Phone_Detail_Sale = dialogDetailSale.findViewById(R.id.txt_Phone_Detail_Sale);
        TextView txt_Address_Detail_Sale = dialogDetailSale.findViewById(R.id.txt_Address_Detail_Sale);
        TextView txt_Total_Sale = dialogDetailSale.findViewById(R.id.txt_Total_Detail_Sale);
        RecyclerView recycle_Detail_Sale = dialogDetailSale.findViewById(R.id.recycle_Detail_Sale);

        // origin value order
        Locale locale = new Locale("vn");
        NumberFormat numberFormat = NumberFormat.getInstance(locale);

        txt_Name_Detail_Sale.setText(order.getUser().getName());
        txt_Phone_Detail_Sale.setText(order.getPhoneOrder());
        txt_Address_Detail_Sale.setText(order.getAddressOder());
        txt_Total_Sale.setText(numberFormat.format(order.getTotalOrder()) + " đ");

        // Recycler detail order
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycle_Detail_Sale.setLayoutManager(layoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recycle_Detail_Sale.addItemDecoration(itemDecoration);

        FoodDetailOrderAdapter adapterFoodDetailOrder = new FoodDetailOrderAdapter(order.getItemCartList());
        recycle_Detail_Sale.setAdapter(adapterFoodDetailOrder);


        dialogDetailSale.show();
    }

    // =============== SHOW MENU ===============
    private void showMenu(View v) {
        PopupMenu popupMenuSale = new PopupMenu(this, v);
        getMenuInflater().inflate(R.menu.menu_sale, popupMenuSale.getMenu());

        popupMenuSale.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                // Today
                if (item.getItemId() == R.id.sale_today) {
                    listSale.clear();
                    txt_Title_Sale.setText(item.getTitle());
                    txt_Start_Date.setVisibility(View.GONE);
                    txt_End_Date.setVisibility(View.GONE);
                    txt_Month.setVisibility(View.GONE);
                    txt_Total_Sale.setText("");
                    getOrderToDay();
                }
                // Days
                else if (item.getItemId() == R.id.sale_date) {
                    listSale.clear();
                    txt_Title_Sale.setText(item.getTitle());
                    // set Start,End Date VISIBLE
                    txt_Start_Date.setVisibility(View.VISIBLE);
                    txt_End_Date.setVisibility(View.VISIBLE);
                    txt_Month.setVisibility(View.GONE);
                    txt_Total_Sale.setText("");
                    getDays();
                }
                // Month
                else {
                    listSale.clear();
                    txt_Month.setVisibility(View.VISIBLE);
                    txt_Title_Sale.setText(item.getTitle());
                    txt_Start_Date.setVisibility(View.GONE);
                    txt_End_Date.setVisibility(View.GONE);
                    txt_Total_Sale.setText("");
                    showMonth();
                }
                return true;
            }
        });
        popupMenuSale.show();
    }

    // =============== SHOW MONTH PICKER ===============

    private void showMonth() {
        Calendar today = Calendar.getInstance();
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(SaleActivity.this,
                new MonthPickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) {
                        txt_Month.setText("Tháng " + (selectedMonth + 1));
                        Calendar monthSale = Calendar.getInstance();
                        monthSale.set(Calendar.MONTH, selectedMonth);
                        monthSale.set(Calendar.YEAR, selectedYear);
                        getOrderByMonth(monthSale);

                    }
                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

        builder.setActivatedMonth(today.get(Calendar.MONTH))
                .setMinYear(1990)
                .setActivatedYear(today.get(Calendar.YEAR))
                .setMaxYear(2030)
                .setTitle("Select trading month")
                .build().show();
    }

    // =============== GET ORDER: MONTH ===============

    private void getOrderByMonth(Calendar monthSale) {
        listSale.clear();
        DatabaseReference referenceOrder = FirebaseDatabase.getInstance().getReference("Order");
        @SuppressLint("SimpleDateFormat")
        Calendar calendarOrder = Calendar.getInstance();

        referenceOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listSale.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Order order = data.getValue(Order.class);

                    if (order != null) {
                        if (order.getStatusOrder() == 2) {
                            order.setIdOrder(data.getKey());
                            Date dateOrder = new Date();
                            dateOrder.setTime(order.getDateOrder());
                            calendarOrder.setTime(dateOrder);

                            // compare order with month vs year
                            if (monthSale.get(Calendar.MONTH) == calendarOrder.get(Calendar.MONTH)
                                    && monthSale.get(Calendar.YEAR) == calendarOrder.get(Calendar.YEAR)) {
                                listSale.add(order);
                                adapter_Sale.setListOrder(listSale);
                                Locale locale = new Locale("vn");
                                NumberFormat numberFormat = NumberFormat.getInstance(locale);
                                txt_Total_Sale.setText(numberFormat.format(getTotalPrice(listSale)));
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // =============== GET TWO DAYS ===============

    private void getDays() {
        Calendar calendar = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Select start date
        txt_Start_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog startDateDialog = new DatePickerDialog(SaleActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        startDate.set(year, month, dayOfMonth);
                        txt_Start_Date.setText(dateFormat.format(startDate.getTime()));
                    }
                }, year, month, day);
                startDateDialog.show();
            }
        });
        // Select end date
        txt_End_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog endDateDialog = new DatePickerDialog(SaleActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        endDate.set(year, month, dayOfMonth);
                        txt_End_Date.setText(dateFormat.format(endDate.getTime()));

                        if (startDate.compareTo(endDate) <= 0) {
                            listSale.clear();
                            txt_Total_Sale.setText("0");
                            getListOrderByDays(startDate, endDate);
                        } else {
                            startDate.clear();
                            endDate.clear();
                            listSale.clear();
                            txt_Total_Sale.setText("0");
                            Toast.makeText(getApplicationContext(), "Chọn ngày không hợp lệ", Toast.LENGTH_SHORT).show();
                        }

                    }

                }, year, month, day);
                endDateDialog.show();
            }
        });

    }

    // =============== GET ORDER: TWO DAT ===============
    private void getListOrderByDays(Calendar startDate, Calendar endDate) {
        DatabaseReference referenceOrder = FirebaseDatabase.getInstance().getReference("Order");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        referenceOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listSale.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Order order = data.getValue(Order.class);
                    if (order != null) {
                        if (order.getStatusOrder() == 2) {
                            order.setIdOrder(data.getKey());
                            String stringDate = dateFormat.format(order.getDateOrder());
                            try {
                                Date date = dateFormat.parse(stringDate);
                                assert date != null;
                                if (date.compareTo(startDate.getTime()) >= 0 && date.compareTo(endDate.getTime()) <= 0) {
                                    listSale.add(order);
                                    adapter_Sale.setListOrder(listSale);
                                    Locale locale = new Locale("vn");
                                    NumberFormat numberFormat = NumberFormat.getInstance(locale);
                                    txt_Total_Sale.setText(numberFormat.format(getTotalPrice(listSale)));
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    // =============== GET ORDER: TODAY ===============

    private void getOrderToDay() {
        DatabaseReference referenceOrder = FirebaseDatabase.getInstance().getReference("Order");
        // today
        Date today = new Date();
        // format
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        referenceOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listSale.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Order order = data.getValue(Order.class);

                    if (order != null) {
                        if (order.getStatusOrder() == 2) {
                            if (dateFormat.format(today).equals(dateFormat.format(order.getDateOrder()))) {
                                listSale.add(order);
                                adapter_Sale.setListOrder(listSale);

                            }
                            Locale locale = new Locale("vn");
                            NumberFormat numberFormat = NumberFormat.getInstance(locale);
                            txt_Total_Sale.setText(numberFormat.format(getTotalPrice(listSale)));
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // =============== GET TOTAL PRICE ===============

    private long getTotalPrice(List<Order> listSale) {
        long total = 0;

        if (listSale.size() != 0) {
            for (int i = 0; i < listSale.size(); i++) {
                Order order = listSale.get(i);
                total += order.getTotalOrder();
            }
        }
        return total;
    }

    // =============== INIT VIEW ===============

    private void initView() {
        img_Back_Sale = findViewById(R.id.img_Back_Sale);
        img_Menu_Sale = findViewById(R.id.img_Menu_Sale);
        txt_Title_Sale = findViewById(R.id.txt_Title_Sale);
        txt_Total_Sale = findViewById(R.id.txt_Total_Sale);
        txt_Start_Date = findViewById(R.id.txt_Start_Date);
        txt_Month = findViewById(R.id.txt_Month);
        txt_End_Date = findViewById(R.id.txt_End_Date);
        recycler_Sale = findViewById(R.id.recycler_Sale);
    }


}