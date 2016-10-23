package tw.edu.au.csie.hellodatabase;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    final String DB_NAME = "db";
    final String SQL_CREATE = "CREATE TABLE schedule (" +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "event TEXT," +
                                "datetime TEXT NOT NULL)";

    TextView vTvDate;
    TextView vTvTime;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        db.execSQL(SQL_CREATE);
        db.close();

        vTvDate = (TextView)findViewById(R.id.tv_date);
        vTvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        vTvTime = (TextView)findViewById(R.id.tv_time);
        vTvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
    }

    private void resetInputView(){
        DecimalFormat mFormat = new DecimalFormat("00");
        Calendar calendar = Calendar.getInstance();
        //Reset Date
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DATE);
        vTvDate.setText(mYear+"-"+mFormat.format(mMonth+1).toString()+"-"+mDay);
        //Reset Time
        int mHour = calendar.get(Calendar.HOUR);
        int mMinute = calendar.get(Calendar.MINUTE);
        vTvTime.setText(mFormat.format(mHour).toString()+":"+mFormat.format(mMinute).toString()+":00");
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetInputView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.db_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.item_add:
                String SQL_INSERT = "INSERT INTO schedule (datetime) VALUES('" +
                                        vTvDate.getText().toString() + " " + vTvTime.getText().toString() +
                                    "')";
                db = openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
                db.execSQL(SQL_INSERT);
                db.close();
                resetInputView();
                break;

            case R.id.item_show:
                String SQL_SELECT = "SELECT datetime FROM schedule";
                db = openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
                Cursor cursor = db.rawQuery(SQL_SELECT, null);
                int rows_num = cursor.getCount();

                String mMessage = "";
                cursor.moveToFirst();
                while(rows_num != 0){
                    mMessage += ("|" + cursor.getString(0) + "|\n");
                    cursor.moveToNext();
                    rows_num--;
                }
                db.close();
                Toast.makeText(this, mMessage, Toast.LENGTH_LONG).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            DecimalFormat mFormat = new DecimalFormat("00");
            ((TextView)getActivity().findViewById(R.id.tv_date)).setText(year+"-"+mFormat.format(month+1).toString()+"-"+mFormat.format(dayOfMonth).toString());
        }
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(),this,hour,minute,true);
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            DecimalFormat mFormat = new DecimalFormat("00");
            ((TextView)getActivity().findViewById(R.id.tv_time)).setText(mFormat.format(hourOfDay).toString()+":"+mFormat.format(minute).toString()+":00");
        }
    }
}
