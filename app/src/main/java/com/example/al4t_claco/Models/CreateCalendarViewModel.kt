package com.example.al4t_claco.Models

import android.provider.CalendarContract
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalDate
import java.time.LocalDateTime

import kotlin.collections.HashMap

data class CalendarUIState(
    var startTimeHour: Int? = null,
    var startTimeMinute: Int? = null,
    var endTimeHour : Int? =null,
    var endTimeMinute : Int? =null
)

data class UpdateCalendarUIState(
    var events:List<Event> = mutableListOf<Event>()
    //var eventsPerDay : MutableMap<LocalDate, MutableList<Event>>? = HashMap(),
)


class CalendarViewModel : ViewModel() {

    private val activities = mutableListOf<Activity>();
    private val courses = mutableMapOf<Int,Course>();
    private val nevents = mutableListOf<Event>(
        Event("SA4L-L1-4MIN", Classroom("1E06"), LocalDateTime.of(2021, 12, 13, 8, 30), LocalDateTime.of(2021, 12, 13, 12, 0), "Programmation parallèle  OpenGL\nGroupe : 4MIN\nEns : LUR"),
        Event("DD4L-L1-4MIN", Classroom("1G01"), LocalDateTime.of(2021, 12, 17, 8, 30), LocalDateTime.of(2021, 12, 17, 12, 0), "Labo architecture et qualité logicielle\nGroupe : 4MIN\nEns : J3L"),
        Event("AL4T-T1-4MIN", Classroom("1G01"), LocalDateTime.of(2021, 12, 17, 12, 45), LocalDateTime.of(2021, 12, 17, 14, 0), "architecture et qualité logicielle\nGroupe : 4MIN\nEns : J3L")
    );
    private val events = mutableListOf<Event>();
    //private val eventsPerDay :MutableMap<LocalDate, MutableList<Event>> = HashMap();
    //ui flows
    val _uiState = MutableStateFlow(CalendarUIState())
    val _CalendarUIState = MutableStateFlow(UpdateCalendarUIState())//this.events

    fun fetch_events(){
        val date1 = LocalDateTime.of(2021, 12, 13, 8, 30)
        val date2 = LocalDateTime.of(2021, 12, 13, 12, 0)

        val date3 = LocalDateTime.of(2021, 12, 17, 8, 30)
        val date4 = LocalDateTime.of(2021, 12, 17, 12, 0)

        val date5 = LocalDateTime.of(2021, 12, 17, 12, 45)
        val date6 = LocalDateTime.of(2021, 12, 17, 14, 0)

        val date9 = LocalDateTime.of(2021, 12, 15, 12, 45)
        val date10 = LocalDateTime.of(2021, 12, 15, 16, 0)

        val date7 = LocalDateTime.of(2021, 12, 15, 8, 30)
        val date8 = LocalDateTime.of(2021, 12, 15, 12, 0)

        val event1 = Event("SA4L-L1-4MIN", Classroom("1E06"), date1, date2, "Programmation parallèle  OpenGL\nGroupe : 4MIN\nEns : LUR")
        val event2 = Event("DD4L-L1-4MIN", Classroom("1G01"), date3, date4, "Labo architecture et qualité logicielle\nGroupe : 4MIN\nEns : J3L")
        val event3 = Event("AL4T-T1-4MIN", Classroom("1G01"), date5, date6, "architecture et qualité logicielle\nGroupe : 4MIN\nEns : J3L")
        val event4 = Event("SI4C-L1-4MIN", Classroom("1F04"), date7, date8, "Labo instrumentation\nGroupe : 4MIN\nEns : MCH, MDM")
        val event5 = Event("OS4T-T1-4MEO-4MIN", Classroom("1G01"), date9, date10, "Systèmes d'exploitation\nGroupe: 4MIN\nEns : HSL, XEI")
        events.add(event1);
        events.add(event2);
        events.add(event3);
        events.add(event4);
        events.add(event5);
        _CalendarUIState.value = _CalendarUIState.value.copy(events=events)

    }
    fun get_events():List<Event>{
        if (events.size==0){
            fetch_events()
        }
        return events;
    }
    fun create_event(name:String,local:String,desc:String,year:Int,month:Int,dayOfMonth:Int){
        fetch_events()
        val startTimeHour = _uiState.value.startTimeHour
        val startTimeMinute = _uiState.value.startTimeMinute
        val endTimeHour = _uiState.value.endTimeHour
        val endTimeMinute = _uiState.value.endTimeMinute
        val oldEventsSize = this.events.size
        // need to check if event already exists before adding it
        if (startTimeHour !=null && startTimeMinute !=null && endTimeHour !=null && endTimeMinute !=null){
            val start = LocalDateTime.of(year,month,dayOfMonth, startTimeHour, startTimeMinute)
            val end = LocalDateTime.of(year,month,dayOfMonth, endTimeHour, endTimeMinute)
            //val oldEvents =_CalendarUIState.value.events
            val latest_events =_CalendarUIState.value.events
            val new_event =Event(name,Classroom(local),start,end,desc)
            events.add(new_event)
            _CalendarUIState.value = _CalendarUIState.value.copy(events= latest_events+ mutableListOf<Event>(new_event) )
            Log.i("Create Calendar","New event added")
            Log.i("Create Calendar",events.get(events.size -1).name.toString())
            Log.i("Create Calendar",events.get(events.size -1).description.toString())
            Log.i("Create Calendar",events.get(events.size -1).startDate.dayOfMonth.toString())

            //val upEvents = this.events.subList(0,oldEventsSize) +this.events.subList(oldEventsSize-1,oldEventsSize+1)

            //_CalendarUIState.value = _CalendarUIState.value.copy(events=oldEvents +events)


        }

    }
    fun get_calendar(events:List<Event>) : MutableMap<LocalDate, MutableList<Event>> {
        val eventsPerDay :MutableMap<LocalDate, MutableList<Event>> = HashMap()
        for(event in events){
            //val date = event.dateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
            val date = event.startDate.toLocalDate()
            if(eventsPerDay.containsKey(date)){
                eventsPerDay[date]!!.add(event)
            }
            else{
                eventsPerDay[date] = mutableListOf<Event>(event)
            }
        }

        return eventsPerDay;
    }

}