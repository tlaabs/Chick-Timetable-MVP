package site.devsim.air;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;

import java.util.ArrayList;
import java.util.HashMap;

import site.devsim.air.contract.EditContract;
import site.devsim.air.presenter.EditPresenter;
import site.devsim.air.view.TimeBoxView;

public class EditActivity extends AppCompatActivity implements EditContract.View {
    public static final int RESULT_OK_ADD = 1;
    public static final int RESULT_OK_EDIT = 2;
    public static final int RESULT_OK_DELETE = 3;

    private EditPresenter editPresenter;

    ArrayList<Schedule> allSchedules = new ArrayList<Schedule>();

    TextView titleView;

    EditText classTitle, classPlace, professorName;
    TimeBoxView timeBox;
    Button addTimeBtn;

    LinearLayout backBtn;
    LinearLayout addBtn;
    LinearLayout deleteBtn;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);
        context = this;

        editPresenter = new EditPresenter(this);

        titleView = findViewById(R.id.title);
        classTitle = findViewById(R.id.class_title);
        classPlace = findViewById(R.id.class_place);
        professorName = findViewById(R.id.professor_name);
        timeBox = findViewById(R.id.time_box);

        addTimeBtn = findViewById(R.id.add_time);
        backBtn = findViewById(R.id.back);
        addBtn = findViewById(R.id.add);
        deleteBtn = findViewById(R.id.delete);

        addTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPresenter.clickAddTimeBtn();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPresenter.submit(allSchedules,timeBox.getSchedules());
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPresenter.clickDeleteBtn();
            }
        });

        init();
    }

    private void init() {
        allSchedules = (ArrayList<Schedule>) getIntent().getSerializableExtra("allSchedules");
        if (isEditMode()) {
            ArrayList<Schedule> itemSchedules = (ArrayList<Schedule>) getIntent().getSerializableExtra("schedules");
            editPresenter.prepare(true, itemSchedules);
        } else {
            editPresenter.prepare(false, null);
        }
    }

    private boolean isEditMode() {
        int mode = getIntent().getIntExtra("mode", -1);
        if (mode == MainActivity.REQUEST_EDIT) return true;
        return false;
    }

    @Override
    public void restoreViews(ArrayList<Schedule> schedules) {
        for(Schedule schedule : schedules) {
            classTitle.setText(schedule.getClassTitle());
            classPlace.setText(schedule.getClassPlace());
            professorName.setText(schedule.getProfessorName());
            timeBox.add(schedule);
        }
    }

    @Override
    public void createTimeView(Schedule schedule) {
        timeBox.add(schedule);
    }

    private ArrayList<Schedule> addMetaData(ArrayList<Schedule> resultSchedule){
        for(Schedule schedule : resultSchedule){
            schedule.setClassTitle(classTitle.getText().toString());
            schedule.setClassPlace(classPlace.getText().toString());
            schedule.setProfessorName(professorName.getText().toString());
        }
        return resultSchedule;
    }

    @Override
    public void setResult(ArrayList<Schedule> resultSchedule) {
        Intent i = new Intent();
        if(resultSchedule == null){
            i.putExtra("idx",getIntent().getIntExtra("idx",-1));
            setResult(RESULT_OK_DELETE,i);
            finish();
            return;
        }
        resultSchedule = addMetaData(resultSchedule);
        i.putExtra("schedules", resultSchedule);
        if(isEditMode()){
            i.putExtra("idx",getIntent().getIntExtra("idx",-1));
            setResult(RESULT_OK_EDIT, i);
        }
        else{
            setResult(RESULT_OK_ADD, i);
        }
        finish();
    }

    @Override
    public void hideDeleteBtn() {
        deleteBtn.setVisibility(View.GONE);
    }

    @Override
    public void showDeleteBtn() {
        deleteBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void setActivityTitle(String title) {
        titleView.setText(title);
    }

    @Override
    public void showToastMessage(String msg) {
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }


}
