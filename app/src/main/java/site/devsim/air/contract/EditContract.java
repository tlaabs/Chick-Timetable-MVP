package site.devsim.air.contract;

import com.github.tlaabs.timetableview.Schedule;

import java.util.ArrayList;
import java.util.HashMap;

public interface EditContract{
    interface View{
        void hideDeleteBtn();
        void showDeleteBtn();
        void setActivityTitle(String title);
        void createTimeView(Schedule schedule);
        void showToastMessage(String msg);
        void setResult(ArrayList<Schedule> schedules);
        void restoreViews(ArrayList<Schedule> schedules);
    }

    interface UserActions{
        void prepare(boolean isEditMode, ArrayList<Schedule> schedules);
        void clickAddTimeBtn();
        void clickDeleteBtn();
        void submit(ArrayList<Schedule> allSchedules, HashMap<Integer, Schedule> schedules);
    }
}
