package site.devsim.air.presenter;

import android.widget.Toast;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;

import java.util.ArrayList;
import java.util.HashMap;

import site.devsim.air.contract.EditContract;

public class EditPresenter implements EditContract.UserActions {
    private EditContract.View editView;

    public EditPresenter(EditContract.View editView){
        this.editView = editView;
    }

    @Override
    public void prepare(boolean isEditMode, ArrayList<Schedule> schedules) {
        if(isEditMode){
            editView.setActivityTitle("수업 편집");
            editView.showDeleteBtn();
            editView.restoreViews(schedules);
        }
        else{
            editView.hideDeleteBtn();
            editView.createTimeView(null);
        }
    }

    @Override
    public void clickAddTimeBtn() {
        editView.createTimeView(null);
    }

    @Override
    public void clickDeleteBtn() {
        editView.setResult(null);
    }

    @Override
    public void submit(ArrayList<Schedule> allSchedules, HashMap<Integer, Schedule> schedules) {
        if (isValidSchedule(allSchedules, schedules)) {
            editView.setResult(createResultSchedule(schedules));
        }
        else{
            editView.showToastMessage("다른 수업과 시간이 겹칩니다.");
        }

    }

    private boolean isValidSchedule(ArrayList<Schedule> allSchedules, HashMap<Integer, Schedule> thisSchedule){
        char[] checker = new char[60 * 24 * 5];
        for (int i = 0; i < 60 * 24 * 5; i++) checker[i] = 0;

        //자신과의 검증
        for (int key : thisSchedule.keySet()) {
            Schedule schedule = thisSchedule.get(key);
            Time thisStartTime = schedule.getStartTime();
            Time thisEndTime = schedule.getEndTime();
            int day = schedule.getDay();
            for (int i = thisStartTime.getHour(), k = thisStartTime.getMinute() + 1; ; k++) {
                int aa = k / 60;
                int pp = k % 60;
                if (i + aa == thisEndTime.getHour() && pp == thisEndTime.getMinute()) {
                    break;
                }
                checker[(60 * 24) * day + (i + aa) * 60 + pp] += 1;
                if (checker[(60 * 24) * day + (i + aa) * 60 + pp] > 1) return false;
            }
        }
        for (int i = 0; i < 60 * 24 * 5; i++) checker[i] = 0;

        //모든 스케줄 검증
        for (Schedule inner : allSchedules) {
            Time innerStartTime = inner.getStartTime();
            Time innerEndTime = inner.getEndTime();
            int day = inner.getDay();
            for (int i = innerStartTime.getHour(), k = innerStartTime.getMinute() + 1; ; k++) {
                int aa = k / 60;
                int pp = k % 60;
                if (i + aa == innerEndTime.getHour() && pp == innerEndTime.getMinute()) {
                    break;
                }
                checker[(60 * 24) * day + (i + aa) * 60 + pp] = 1;
            }
        }
        for (int key : thisSchedule.keySet()) {
            Schedule schedule = thisSchedule.get(key);
            Time thisStartTime = schedule.getStartTime();
            Time thisEndTime = schedule.getEndTime();
            int day = schedule.getDay();
            for (int i = thisStartTime.getHour(), k = thisStartTime.getMinute(); ; k++) {
                int aa = k / 60;
                int pp = k % 60;
                if (checker[(60 * 24) * day + (i + aa) * 60 + pp] > 0) return false;
                if (i + aa == thisEndTime.getHour() && pp == thisEndTime.getMinute()) {
                    break;
                }
            }
        }

        return true;
    }
    private ArrayList<Schedule> createResultSchedule(HashMap<Integer, Schedule> schedules){
        ArrayList<Schedule> resultSchedule = new ArrayList<Schedule>();
        for (int key : schedules.keySet()) {
            resultSchedule.add(schedules.get(key));
        }
        return resultSchedule;
    }
}
