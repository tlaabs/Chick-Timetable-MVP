package site.devsim.air.view;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;

import java.util.ArrayList;
import java.util.HashMap;

import site.devsim.air.R;

import static site.devsim.air.util.DisplayUtil.dpToPx;

public class TimeBoxView extends LinearLayout{

    public static final int TIME_EDIT_TEXT_SIZE_SP = 17;

    HashMap<Integer, LinearLayout> innerTimeBox;
    HashMap<Integer, Spinner> dayViews;
    HashMap<Integer, TextView> startViews;
    HashMap<Integer, TextView> endViews;

    HashMap<Integer, Schedule> schedules;

    int timeCount = 0;

    Context context;

    public TimeBoxView(Context context){
        this(context,null);
    }

    public TimeBoxView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TimeBoxView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        innerTimeBox = new HashMap<Integer, LinearLayout>();
        dayViews = new HashMap<Integer, Spinner>();
        startViews = new HashMap<Integer, TextView>();
        endViews = new HashMap<Integer, TextView>();
        schedules = new HashMap<Integer, Schedule>();
    }

    public void add(Schedule schedule){
        if(schedule == null) createTimeView(null);
        else createTimeView(schedule);
    }
    public void remove(int idx){
        LinearLayout box = innerTimeBox.get(idx);
        this.removeView(box);

        schedules.remove(idx);
        innerTimeBox.remove(idx);
        dayViews.remove(idx);
        startViews.remove(idx);
        endViews.remove(idx);
    }

    public HashMap<Integer, Schedule> getSchedules(){
        return this.schedules;
    }

    private Schedule createDefaultSchedule(){
        Schedule schedule = new Schedule();
        schedule.setDay(0);
        schedule.getStartTime().setHour(9);
        schedule.getStartTime().setMinute(0);
        schedule.getEndTime().setHour(10);
        schedule.getEndTime().setMinute(0);
        return schedule;
    }
    private LinearLayout createInnerTimeBox(){
        LinearLayout box = new LinearLayout(context);
        LinearLayout.LayoutParams linParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        box.setOrientation(LinearLayout.HORIZONTAL);
        linParam.bottomMargin = dpToPx(20);
        box.setLayoutParams(linParam);
        return box;
    }
    private Spinner createDaySpinner(final int count){
        LinearLayout.LayoutParams viewParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        Spinner daySpinner = new Spinner(context, Spinner.MODE_DIALOG);
        daySpinner.setLayoutParams(viewParam);

        String[] day_array = getResources().getStringArray(R.array.days);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, day_array) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                TextView tv = ((TextView) v);
                tv.setTextColor(getResources().getColor(R.color.colorWeakBlack));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, TIME_EDIT_TEXT_SIZE_SP);
                return v;
            }
        };
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(spinnerAdapter);
        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                schedules.get(count).setDay(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        daySpinner.setSelection(schedules.get(count).getDay());
        return daySpinner;
    }
    private TextView createStartView(final int count){
        LinearLayout.LayoutParams viewParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView startView = new TextView(context);
        startView.setLayoutParams(viewParam);
        startView.setText(getTimeString(schedules.get(count).getStartTime().getHour(), schedules.get(count).getStartTime().getMinute()));

        startView.setTextColor(getResources().getColor(R.color.colorWeakBlack));
        startView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TIME_EDIT_TEXT_SIZE_SP);
        //event
        startView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(context, startListener, schedules.get(count).getStartTime().getHour(), schedules.get(count).getStartTime().getMinute(), false);
                dialog.show();
            }

            private TimePickerDialog.OnTimeSetListener startListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    startViews.get(count).setText(hourOfDay + " : " + minute);
                    schedules.get(count).getStartTime().setHour(hourOfDay);
                    schedules.get(count).getStartTime().setMinute(minute);
                    transInvalidateTime(schedules.get(count).getStartTime(), schedules.get(count).getEndTime());
                    startViews.get(count).setText(getTimeString(schedules.get(count).getStartTime().getHour(), schedules.get(count).getStartTime().getMinute()));
                    endViews.get(count).setText(getTimeString(schedules.get(count).getEndTime().getHour(), schedules.get(count).getEndTime().getMinute()));

                }
            };
        });
        return startView;
    }
    private TextView createHyphenText(){
        LinearLayout.LayoutParams viewParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView hyphenView = new TextView(context);
        hyphenView.setLayoutParams(viewParam);
        hyphenView.setText(" ~ ");
        hyphenView.setTextColor(getResources().getColor(R.color.colorWeakBlack));
        hyphenView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TIME_EDIT_TEXT_SIZE_SP);
        return hyphenView;
    }
    private TextView createEndView(final int count){
        LinearLayout.LayoutParams viewParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView endView = new TextView(context);
        endView.setLayoutParams(viewParam);
        endView.setText(getTimeString(schedules.get(count).getEndTime().getHour(), schedules.get(count).getEndTime().getMinute()));
        endView.setTextColor(getResources().getColor(R.color.colorWeakBlack));
        endView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TIME_EDIT_TEXT_SIZE_SP);
        endView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog dialog = new TimePickerDialog(context, endListener, schedules.get(count).getStartTime().getHour(), schedules.get(count).getStartTime().getMinute(), false);
                dialog.show();
            }

            private TimePickerDialog.OnTimeSetListener endListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    endViews.get(count).setText(hourOfDay + " : " + minute);
                    schedules.get(count).getEndTime().setHour(hourOfDay);
                    schedules.get(count).getEndTime().setMinute(minute);
                    transInvalidateTime(schedules.get(count).getStartTime(), schedules.get(count).getEndTime());
                    startViews.get(count).setText(getTimeString(schedules.get(count).getStartTime().getHour(), schedules.get(count).getStartTime().getMinute()));
                    endViews.get(count).setText(getTimeString(schedules.get(count).getEndTime().getHour(), schedules.get(count).getEndTime().getMinute()));
                }
            };
        });
        return endView;
    }
    private ImageView createDeleteTimeView(final int count){
        final ImageView deleteTime = new ImageView(context);
        LinearLayout.LayoutParams marginParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        marginParam.leftMargin = dpToPx(15);
        deleteTime.setLayoutParams(marginParam);

        deleteTime.setBackground(getResources().getDrawable(R.drawable.baseline_clear_black_24));
        deleteTime.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWeakBlack)));

        deleteTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(count);
            }
        });
        return deleteTime;
    }

    public void createTimeView(Schedule schedule) {
        final int count = timeCount++;
        if (schedule == null) schedule = createDefaultSchedule();
        schedules.put(count, schedule);

        LinearLayout box = createInnerTimeBox();
        innerTimeBox.put(count, box);

        Spinner daySpinner = createDaySpinner(count);
        dayViews.put(count, daySpinner);

        TextView startView = createStartView(count);
        startViews.put(count, startView);

        //~
        TextView hyperView = createHyphenText();

        TextView endView = createEndView(count);
        endViews.put(count, endView);

        ImageView deleteTime = createDeleteTimeView(count);

        box.addView(daySpinner);
        box.addView(startView);
        box.addView(hyperView);
        box.addView(endView);
        box.addView(deleteTime);

        this.addView(box);

    }

    private void transInvalidateTime(Time startTime, Time endTime) {
        //시간 범위 : 9~20
        int startTimeHour = startTime.getHour();
        int endTimeHour = endTime.getHour();

        if (startTimeHour < 9) startTime.setHour(9);
        if (startTimeHour > 20) startTime.setHour(20);
        if (endTimeHour < 9) endTime.setHour(9);
        if (endTimeHour > 20) endTime.setHour(20);

        if (startTime.getHour() > endTime.getHour()) { //시작시간이 더 느리면
            endTime.setHour(startTime.getHour() + 1);
            endTime.setMinute(0);
        } else if (startTime.getHour() == endTime.getHour()) {
            if (startTime.getMinute() >= endTime.getMinute()) {
                endTime.setHour(startTime.getHour() + 1);
                endTime.setMinute(0);
            }
        }
    }

    private String getTimeString(int hourOfDay, int minute) {
        String ampm;
        String hourStr = hourOfDay + "";
        String minuteStr = minute + "";
        ampm = (hourOfDay < 12) ? "오전" : "오후";
        if (hourOfDay < 10) hourStr = "0" + hourStr;
        if (hourOfDay > 12) hourStr = "0" + (hourOfDay - 12);
        if (minute < 10) minuteStr = "0" + minuteStr;
        return ampm + " " + hourStr + ":" + minuteStr;
    }
}
