package com.parentalert.in.schoolapp.Fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.mukesh.tinydb.TinyDB;
import com.parentalert.in.schoolapp.Config.AppConfig;
import com.parentalert.in.schoolapp.Config.AppController;
import com.parentalert.in.schoolapp.Model.Classes_model;
import com.parentalert.in.schoolapp.Model.Message_model;
import com.parentalert.in.schoolapp.Personalize_message;
import com.parentalert.in.schoolapp.R;
import com.parentalert.in.schoolapp.SQliteDB.DBHelper;
import com.parentalert.in.schoolapp.TestActivity;
import com.parentalert.in.schoolapp.ViewHolder.Recycleviewholder_create;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by EinNel3 on 15-06-2016.
 */
public class Create extends Fragment {
    public AppConfig appconfig = new AppConfig();
    public static ArrayList<Message_model> arrayListt = new ArrayList<>();
    ArrayListAnySize<String> classnames = new ArrayListAnySize<>();
    ArrayListAnySize<String> msg_id = new ArrayListAnySize<>();
    //    List<String> classnames_getchecked = new ArrayList<>();
    EditText alert_edt_getmessage;
    Button alert_buttonsend;
    public static RecyclerView recyclerView;
    Button button_selectall, type_message_button, button_unselectall;
    EditText getedittext_message;
    ImageView imageView_send;
    LinearLayout linearLayout_selectall, linearLayout_unselectall;
    static RecyclerView_Adapter adapter;
    Recycleviewholder_create mainHolder;
    DBHelper dbHelper;
    private Cursor cursor;
    TinyDB tinyDB;
    ImageView imageView_date;
    TextView textView_date, textView_time;
    int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_layout, container, false);
        classnames.clear();
        arrayListt.clear();
        msg_id.clear();
        initViews(view);
        onclicklisetener();
        checkInternerPermission();
        return view;
    }

    private void initViews(View view) {
        tinyDB = new TinyDB(TestActivity.context);
        dbHelper = new DBHelper(TestActivity.context);
        button_selectall = (Button) view.findViewById(R.id.selectall);
        button_unselectall = (Button) view.findViewById(R.id.Unselectall);
        imageView_send = (ImageView) view.findViewById(R.id.imageview_send);
        imageView_date = (ImageView) view.findViewById(R.id.imageview_date);
        textView_date = (TextView) view.findViewById(R.id.textviewdate);
        textView_time = (TextView) view.findViewById(R.id.textviewtime);
//        type_message_button = (Button) view.findViewById(R.id.type_message_button);

        getedittext_message = (EditText) view.findViewById(R.id.editext_message);
        linearLayout_selectall = (LinearLayout) view.findViewById(R.id.linear_select_all);
        linearLayout_unselectall = (LinearLayout) view.findViewById(R.id.linear_unselect_all);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));

    }

    private void onclicklisetener() {
        imageView_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(TestActivity.context,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                textView_date.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                settime();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }

        });

        imageView_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (classnames.size() > 0) {
                    sendJson(getedittext_message.getText().toString().trim().replaceAll(System.getProperty("line.separator"), ""));
                    classnames.clear();
                    populatRecyclerView();
                    getedittext_message.setText("");
                    try {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    }catch (Exception e){

                    }
                    TestActivity.mTabHost.setCurrentTab(1);
                    Log.d("Textvalue", getedittext_message.getText().toString().trim().replaceAll(System.getProperty("line.separator"), ""));
                } else {
                    Toast.makeText(TestActivity.context, "Please click anyone class", Toast.LENGTH_SHORT).show();
                }

            }
        });


        linearLayout_selectall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                classnames.clear();

                Classes_model names = (Classes_model) mainHolder.checkbox.getTag();

                List<Classes_model> ClassList = ((RecyclerView_Adapter) adapter)
                        .getClasslist();

                Log.d("selectall_size", String.valueOf(ClassList.size()));


                for (int i = 0; i <= ClassList.size() - 1; i++) {

                    classnames.add(ClassList.get(i).getTitle().toString());

                    Classes_model singleStudent = ClassList.get(i);

                    ClassList.get(i).setSelected(true);

                }
                adapter.notifyDataSetChanged();
            }
        });
        linearLayout_unselectall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Classes_model names = (Classes_model) mainHolder.checkbox.getTag();

                List<Classes_model> ClassList = ((RecyclerView_Adapter) adapter)
                        .getClasslist();

                Log.d("Unselectall_size", String.valueOf(ClassList.size()));

                for (int i = 0; i <= ClassList.size() - 1; i++) {

                    Classes_model singleStudent = ClassList.get(i);

                    ClassList.get(i).setSelected(false);

                }

                classnames.clear();
                adapter.notifyDataSetChanged();
            }
        });

    }

    private void settime() {

        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(TestActivity.context,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        textView_time.setText(hourOfDay + ":" + minute + ":" + "00");
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();

    }

    private void deselect_all() {
        Classes_model names = (Classes_model) mainHolder.checkbox.getTag();

        List<Classes_model> ClassList = ((RecyclerView_Adapter) adapter)
                .getClasslist();

        Log.d("Unselectall_size", String.valueOf(ClassList.size()));

        for (int i = 0; i <= ClassList.size() - 1; i++) {

            Classes_model singleStudent = ClassList.get(i);

            ClassList.get(i).setSelected(false);

        }

        classnames.clear();
        adapter.notifyDataSetChanged();
    }

    private void checkInternerPermission() {
        if (isNetworkConnected()) {
            JSON_GETSECTIONS();
        } else {
            populatRecyclerView();
        }
    }

    private void populatRecyclerView() {
        ArrayList<Classes_model> arrayList = new ArrayList<>();
        cursor = dbHelper.selectRecords_create();
        if (cursor.moveToFirst()) {

            do {

                String section = cursor.getString(cursor.getColumnIndex(DBHelper.CLASSSEC));

                arrayList.add(new Classes_model(section, false));

            } while (cursor.moveToNext());

        }
        cursor.close();
        adapter = new RecyclerView_Adapter(TestActivity.context, arrayList);
        recyclerView.setAdapter(adapter);// set adapter on recyclerview
        adapter.notifyDataSetChanged();// Notify the adapter
    }

    private void JSON_GETSECTIONS() {
        dbHelper.deleteRecord_create("SectionList");
        final ProgressDialog pDialog = new ProgressDialog(TestActivity.context);
        pDialog.show();
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        final AppConfig config = new AppConfig();
        // prepare the Request
        String tag_string_req = "req_register";
        StringRequest stringRequest = new StringRequest(config.GET_PARENTALERT_SECTION_LIST + "/" + tinyDB.getString("DBNAME"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("get_selection_list", response);
                        pDialog.cancel();
                        pDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String json_error = jsonObject.getString("error").toString();
                            if (json_error.equals("false")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("sections");
                                for (int i = 0; i <= jsonArray.length() - 1; i++) {
                                    String classname_individual = jsonArray.getJSONObject(i).getString(config.PARENTALERT_KEYNAME);
                                    dbHelper.inserRecord_create(classname_individual);
                                }

                            } else {

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        populatRecyclerView();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.cancel();
                        pDialog.dismiss();

                        Toast.makeText(TestActivity.context,"oops Server Problem Try sometime !!",Toast.LENGTH_SHORT).show();
                    }
                });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 2, 2));
        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);
    }

    private void sendJson(String getalert_editext) {

        JSONObject selected_classes_json = new JSONObject();
        JSONObject editext_message_json = new JSONObject();

        if (classnames.size() > 0) {

            for (int i = 0; i <= classnames.size() - 1; i++) {
                try {
                    selected_classes_json.put(classnames.get(i).toString(), classnames.get(i).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            try {
                editext_message_json.put("MSG", getalert_editext.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONObject combine = new JSONObject();
            try {
                combine.put("selectedclasses", selected_classes_json);
                if (textView_date.getText().toString().equals("") && textView_time.getText().toString().equals("")) {
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);

                    // Get Current Time
                    final Calendar cc = Calendar.getInstance();
                    int mHour = cc.get(Calendar.HOUR_OF_DAY);
                    int mMinute = cc.get(Calendar.MINUTE);

                    combine.put("dateTime", String.valueOf(mYear + "-" + (mMonth + 1) + "-" + mDay) + " " + String.valueOf(mHour + ":" + mMinute + ":" + "00"));
                } else {
                    combine.put("dateTime", textView_date.getText().toString().trim() + " " + textView_time.getText().toString().trim());
                }
                combine.put("message", editext_message_json);
                Log.d("send", combine.toString());

                SAVEJSON_CREATE(appconfig.SECTION_WISE_SAVE_MESSAGE, combine);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(TestActivity.context, "Please click anyone class", Toast.LENGTH_SHORT).show();
        }
    }


    public class RecyclerView_Adapter extends RecyclerView.Adapter<Recycleviewholder_create> {
        private boolean onBind;
        private ArrayList<Classes_model> arrayList;
        private Context context;

        public RecyclerView_Adapter(Context context,
                                    ArrayList<Classes_model> arrayList) {
            this.context = context;
            this.arrayList = arrayList;

        }

        @Override
        public int getItemCount() {
            return (null != arrayList ? arrayList.size() : 0);

        }

        @Override
        public void onBindViewHolder(Recycleviewholder_create holder, final int position) {

            final Classes_model model = arrayList.get(position);

            mainHolder = (Recycleviewholder_create) holder;
            mainHolder.checkbox.setOnCheckedChangeListener(null);
            mainHolder.checkbox.setText(model.getTitle());
            mainHolder.checkbox.setTextOn(model.getTitle());
            mainHolder.checkbox.setTextOff(model.getTitle());
            mainHolder.checkbox.setChecked(model.isSelected());
            mainHolder.checkbox.setTag(model);

            mainHolder.checkbox.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            if (isChecked) {
                                classnames.add(model.getTitle());
                                arrayList.set(position, new Classes_model(model.getTitle(), true));
                                Log.d("classcheckedif", String.valueOf(classnames));
                            } else if (!isChecked) {
                                classnames.remove(model.getTitle());
                                arrayList.set(position, new Classes_model(model.getTitle(), false));
                                Log.d("classcheckedelse", String.valueOf(classnames));
                            }
                        }
                    });

        }


        @Override
        public Recycleviewholder_create onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

            ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
                    R.layout.fragment_create_recycleview, viewGroup, false);
            Recycleviewholder_create listHolder = new Recycleviewholder_create(mainGroup);
            return listHolder;

        }

        public List<Classes_model> getClasslist() {
            return arrayList;
        }


    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) TestActivity.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    public class ArrayListAnySize<E> extends ArrayList<E> {
        @Override
        public void add(int index, E element) {
            if (index >= 0 && index <= size()) {
                super.add(index, element);
                return;
            }
            int insertNulls = index - size();
            for (int i = 0; i < insertNulls; i++) {
                super.add(null);
            }
            super.add(element);
        }

    }


    private void JSON_APPROVE() {
        final ProgressDialog pDialog = new ProgressDialog(TestActivity.context);
        pDialog.show();
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        final AppConfig config = new AppConfig();
        // prepare the Request
        String tag_string_req = "req_register";
        StringRequest stringRequest = new StringRequest(config.GET_SECTION_MESSAGE_WISE + "/" + tinyDB.getString("DBNAME"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("JSON_APPROVE", response);
                        pDialog.cancel();
                        pDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String json_error = jsonObject.getString("error").toString();
                            if (json_error.equals("false")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("messages");
                                for (int i = 0; i <= jsonArray.length() - 1; i++) {

                                    String msg_id = jsonArray.getJSONObject(i).getString(config.SECTION_WISE_MESG_ID_KEYNAME);

                                    String section = jsonArray.getJSONObject(i).getString(config.SECTION_WISE_SECTION_KEYNAME);

                                    String message = jsonArray.getJSONObject(i).getString(config.SECTION_WISE_MESSAGE_KEYNAME);

                                    arrayListt.add(new Message_model(msg_id, section, message,""));
                                }


                            } else {

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.cancel();
                        pDialog.dismiss();
                        Toast.makeText(TestActivity.context,"oops Server Problem Try sometime !!",Toast.LENGTH_SHORT).show();

                    }
                });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 2, 2));
        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);
    }

    private void SAVEJSON_CREATE(String requestURL, final JSONObject postDataParams) {
        final ProgressDialog pDialog = new ProgressDialog(TestActivity.context);
        pDialog.show();
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        // Request a string response from the provided URL.
        String tag_string_req = "req_register";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestURL + "/" + tinyDB.getString("DBNAME"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // your response
                        pDialog.dismiss();
                        pDialog.cancel();
                        Toast.makeText(TestActivity.context, "Message created successfully", Toast.LENGTH_SHORT).show();
                        getedittext_message.setText("");
                        Log.d("Save_message", response.toString());
                        JSON_APPROVE();
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                pDialog.cancel();
                Toast.makeText(TestActivity.context,"oops Server Problem Try sometime !!",Toast.LENGTH_SHORT).show();

                // error
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                String your_string_json = postDataParams.toString(); // put your json
                return your_string_json.getBytes();
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 2, 2));
        //Adding request to request queue
        AppController.getInstance().addToRequestQueue(stringRequest, tag_string_req);
    }
}
