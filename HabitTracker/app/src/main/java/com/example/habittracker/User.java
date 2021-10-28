package com.example.habittracker;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private ArrayList<Habit> userHabits;

    public User(ArrayList<Habit> userHabits) {
        this.userHabits = userHabits;
    }


    public ArrayList<Habit> getAllHabits() {
        return userHabits;
    }
    public ArrayList<Habit> getTodayHabits() {

        ArrayList<Habit> todayHabitList = new ArrayList<>();

        // iterate through all habits
        for (int i = 0; i < userHabits.size(); i++) {
            // get first char of name of current habit
            Habit habit = userHabits.get(i);
            String habitName = habit.getHabitName();
            char habitChar = habitName.charAt(0);
            // Check if habitLetter == 'E'
            char E = 'E';
            int comparisonValue = Character.compare(habitChar, E);
            // add 'E' cities to eCityDataList
            if (comparisonValue == 0) {
                todayHabitList.add(habit);
            }
        }
        return todayHabitList;
    }

    public void setHabits(ArrayList<Habit> userHabits) {
        this.userHabits = userHabits;
    }
    public void addHabit(Habit habit) {
        userHabits.add(habit);
    }

}
