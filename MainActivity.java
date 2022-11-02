package com.example.appforstaff;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private AdaptStaff sAdapter;
    private List<Staff> staffList = new ArrayList<>();

    ListView list;
    Intent add_new_staff;
    Intent item_gridd;
    AdaptStaff adapter;
    Spinner sort;



    String ConnectionResult = "";
    ArrayList<Staff> stafflist = new ArrayList<>();
    ArrayList<Staff> staffList_s  ;

    int[] imagestaff = {R.drawable.ic_launcher_foreground};
    ArrayAdapter<String> arrayAdapter;

    Connection cnt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView ivProducts = findViewById(R.id.gridviewlist);//Находим лист в который будем класть наши объекты
        sAdapter = new AdaptStaff(MainActivity.this, (ArrayList<Staff>) staffList); //Создаем объект нашего адаптера
        ivProducts.setAdapter(sAdapter); //Cвязывает подготовленный список с адаптером

        new GetProducts().execute(); //Подключение к нашей API в отдельном потоке


        String[] sort_name = { "ФИО", "Почте", "Телефону"};
        sort = findViewById(R.id.sort);
        ArrayAdapter<String> spinner_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, sort_name);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sort.setAdapter(spinner_adapter);

        list = findViewById(R.id.gridviewlist);

        item_gridd = new Intent(this,item_grid.class);
        add_new_staff = new Intent(this, com.example.appforstaff.add_new_staff.class);

       // GetStaffList();


        //arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1 );


        sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        Collections.sort(stafflist, new Comparator<Staff>() {
                            @Override
                            public int compare(Staff staff, Staff t1) {
                                return staff.name.compareTo(t1.name);
                            }
                        });
                        adapter = new AdaptStaff(MainActivity.this, stafflist);
                        list.setAdapter(adapter);
                        break;
                    case 1:
                        Collections.sort(stafflist, new Comparator<Staff>() {
                            @Override
                            public int compare(Staff staff, Staff t1) {
                                return staff.email.compareTo(t1.email);
                            }
                        });
                        adapter = new AdaptStaff(MainActivity.this, stafflist);
                        list.setAdapter(adapter);
                        break;
                    case 2:
                        Collections.sort(stafflist, new Comparator<Staff>() {
                            @Override
                            public int compare(Staff staff, Staff t1) {
                                return staff.phone.compareTo(t1.phone);
                            }
                        });
                        adapter = new AdaptStaff(MainActivity.this, stafflist);
                        list.setAdapter(adapter);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Staff item = (Staff) list.getItemAtPosition(i);

                item_gridd.putExtra("ФИО",item.name);
                item_gridd.putExtra("Телефон",item.phone);
                item_gridd.putExtra("Почта",item.email);
                item_gridd.putExtra("id", item.id);

                startActivity(item_gridd);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        MenuItem menuItem2 = menu.findItem(R.id.action_add);
        SearchView  searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                arrayAdapter.getFilter().filter(newText);

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    public void onclick_view_add_staff(View view){startActivity(add_new_staff);}
 //   public void UpdateList(View view){GetStaffList();}

/*    public void GetStaffList(){
        try{
            stafflist.clear();
            cnt = SQLConnectHelper.connect();
            if(cnt != null){
                String qu = "select * from Staff";
                Statement statement = cnt.createStatement();
                ResultSet resultSet = statement.executeQuery(qu);
                while (resultSet.next()){
                    Log.d(ConnectionResult, resultSet.getString("name"));
                    stafflist.add(new Staff(resultSet.getString("name"),resultSet.getString("phone"),resultSet.getString("email"),resultSet.getString("id")));
                }
                ConnectionResult = "Success";
                AdaptStaff adapter = new AdaptStaff(this,stafflist);
                list.setAdapter(adapter);
            }
            else {
                ConnectionResult = "Failed";
            }
            Log.d(ConnectionResult,"");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            Log.d(ConnectionResult, throwables.getMessage());
        }
    }*/



    private class GetProducts extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("https://ngknn.ru:5101/NGKNN/НаумовСА/api/Staffs");//Строка подключения к нашей API
                HttpURLConnection connection = (HttpURLConnection) url.openConnection(); //вызываем нашу API

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                /*
                BufferedReader успрощает чтение текста из потока символов
                InputStreamReader преводит поток байтов в поток символов
                connection.getInputStream() получает поток байтов
                */
                StringBuilder result = new StringBuilder();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    result.append(line);//кладет строковое значение в потоке
                }
                return result.toString();

            } catch (Exception exception) {
                return null;
            }
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try
            {
                JSONArray tempArray = new JSONArray(s);//преоброзование строки в json массив
                for (int i = 0;i<tempArray.length();i++)
                {

                    JSONObject productJson = tempArray.getJSONObject(i);//Преобразование json объекта в нашу структуру
                    Staff tempProduct = new Staff(
                            productJson.getString("id"),
                            productJson.getString("name"),
                            productJson.getString("email"),
                            productJson.getString("phone"),
                            getImageBitmap( productJson.getString("image")));
                    staffList.add(tempProduct);
                    sAdapter.notifyDataSetInvalidated();
                }
            } catch (Exception ignored) {


            }
        }
        private Bitmap getImageBitmap(String encodedImg) {
            if (encodedImg != null) {
                byte[] bytes = new byte[0];
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    bytes = Base64.getDecoder().decode(encodedImg);
                }
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
            return BitmapFactory.decodeResource(getResources(),
                    R.drawable.icon);
        }
    }
}