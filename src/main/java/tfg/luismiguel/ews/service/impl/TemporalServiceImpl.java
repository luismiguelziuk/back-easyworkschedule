package tfg.luismiguel.ews.service.impl;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tfg.luismiguel.ews.dto.creation.WeekCreationDTO;
import tfg.luismiguel.ews.entity.Day;
import tfg.luismiguel.ews.entity.Week;
import tfg.luismiguel.ews.exception.EwsException;
import tfg.luismiguel.ews.repository.DayRepository;
import tfg.luismiguel.ews.repository.WeekRepository;
import tfg.luismiguel.ews.service.TemporalService;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Service
@Transactional
public class TemporalServiceImpl implements TemporalService {

    @Autowired
    private WeekRepository weekRepository;
    @Autowired
    private DayRepository dayRepository;

    @Override
    public Week createWeek(WeekCreationDTO weekDTO) {
        Week week = new Week();
        week.setNumberOfWeek(weekDTO.getNumberOfWeek());
        week.setYear(weekDTO.getYear());
        List<Day> days = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            days.add(createDay(i));
        }
        week.setDays(days);
        weekRepository.save(week);
        return week;
    }

    public Week findWeekByNumberAndYear(Long numberOfWeek, Long year) throws EwsException {
        try {
            return weekRepository.findAll().stream().filter(week ->
                    week.getNumberOfWeek().equals(numberOfWeek)
                            && week.getYear().equals(year)).findFirst().get();
        } catch (Exception e) {
            throw new EwsException("No existe esta semana, creela primero");
        }
    }

    private Day createDay(int dayOfWeek) {
        Day day = new Day();
        day.setDayOfWeek(DayOfWeek.of(dayOfWeek));
        day.setShifts(new ArrayList<>());
        dayRepository.save(day);
        return day;
    }
}
