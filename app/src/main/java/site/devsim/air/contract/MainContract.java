package site.devsim.air.contract;

import com.github.tlaabs.timetableview.Schedule;

import java.util.ArrayList;

import site.devsim.air.model.PrefManager;


public interface MainContract {
    interface View{
        void startEditActivityForAdd();
        void startEditActivityForEdit(int idx, ArrayList<Schedule> schedules);
        void restoreTimetable(String data);
        void setDayHighlight(int day);
    }

    interface UserActions{
        void addMenuClick();
        void selectSticker(int idx, ArrayList<Schedule> schedules);
        void prepare();
        void save(String data);
        void setPrefManager(PrefManager prefManager);
    }
}
