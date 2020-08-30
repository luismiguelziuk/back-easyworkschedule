package tfg.luismiguel.ews.service.impl;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tfg.luismiguel.ews.dto.DayDTO;
import tfg.luismiguel.ews.dto.FillWeekDTO;
import tfg.luismiguel.ews.dto.ShiftDTO;
import tfg.luismiguel.ews.dto.WeekDTO;
import tfg.luismiguel.ews.dto.WeekKnappSackDTO;
import tfg.luismiguel.ews.dto.cex.TeamDTO;
import tfg.luismiguel.ews.dto.cex.TouristInformerDTO;
import tfg.luismiguel.ews.dto.cex.TouristPointDTO;
import tfg.luismiguel.ews.dto.cex.TouristPointDayProblemDTO;
import tfg.luismiguel.ews.entity.Day;
import tfg.luismiguel.ews.entity.Shift;
import tfg.luismiguel.ews.entity.Week;
import tfg.luismiguel.ews.entity.cex.Team;
import tfg.luismiguel.ews.entity.cex.TouristInformer;
import tfg.luismiguel.ews.exception.EwsException;
import tfg.luismiguel.ews.repository.DayRepository;
import tfg.luismiguel.ews.repository.PointRepository;
import tfg.luismiguel.ews.repository.ShiftRepository;
import tfg.luismiguel.ews.repository.WeekRepository;
import tfg.luismiguel.ews.repository.WorkerRepository;
import tfg.luismiguel.ews.repository.cex.TeamRepository;
import tfg.luismiguel.ews.repository.cex.TouristInformerRepository;
import tfg.luismiguel.ews.repository.cex.TouristPointRepository;
import tfg.luismiguel.ews.service.AlgorithmService;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@NoArgsConstructor
@Service
@Transactional
public class AlgorithmServiceImpl implements AlgorithmService {

    @Autowired
    WeekRepository weekRepository;
    @Autowired
    DayRepository dayRepository;
    @Autowired
    PointRepository pointRepository;
    @Autowired
    WorkerRepository workerRepository;
    @Autowired
    ShiftRepository shiftRepository;
    @Autowired
    TouristPointRepository touristPointRepository;
    @Autowired
    TouristInformerRepository touristInformerRepository;
    @Autowired
    TeamRepository teamRepository;

    private Week weekDatabase;
    private Week lastWeekDatabase;
    private WeekDTO week;
    private Map<TouristInformerDTO, Double> availableWorkersHours = new LinkedHashMap<>();
    private final List<TouristPointDTO> touristPoints = new ArrayList<>();
    private final List<TouristPointDayProblemDTO> errorPoints = new ArrayList<>();

    @Override
    public void fillCompleteWeek(FillWeekDTO fillWeekDTO) throws EwsException {
        // Inicializamos las variables
        initializeVariables(fillWeekDTO);
        // En una primera instancia rellenamos la semana cumpliendo los requisitos
        fillWeek();
        // Metodo para mutar un punto de los puntos de informacion para el dia que ha dado error.
        // Tratando de rellenarlo con un trabajador que ya este ocupado esa semana, metiendo uno libre en el lugar que ocupaba,
        // para ver si conseguimos la solucion parcial. Esto se intentara n veces tambien y si no se consigue se dara como irresoluble.
        mutateIfErrorFilling(fillWeekDTO);
        if (!errorPoints.isEmpty()) {
            StringBuilder message = new StringBuilder();
            errorPoints.forEach(problem -> message.append(problem.getTouristPoint().getName()).append(", "));
            throw new EwsException("No tiene solucion, no hay trabajadores suficientes para los puntos { " + message.substring(0, message.length() - 2) + " }");
        }
        // Generamos un weekKnappSackDTO con una copia de la semana actual y su salud.
        WeekKnappSackDTO weekKnappSackDTO = generateWeekKnappSackDTO();
        // Si obtenemos solucion parcial, hacer metodo para mutar el week escogido produciendo algun otro week con mejor salud (media de horas
        // acumuladas de un trabajador, o que la empresa debe a un trabajador (valor absoluto de las horas acumuladas)
        // sea menor). Si las horas son 0, la solucion es solucion final. Aunque es poco probable llegar a ella
        // teniendo en cuenta la problematica. Esta operacion se ejecutara un numero n de veces.
        // Quedandonos con el mejor de los resultados al final de ese ciclo
        randomMutate(fillWeekDTO, weekKnappSackDTO);
        // Guardamos el mejor resultado que hemos optenido
        saveAll(weekKnappSackDTO);
    }

    private void fillWeek() {
        // Generamos un mapa con los id de los worker y los id de los puntos en los que ha trabajado en la semana,
        // ya que no puede repetir punto.
        Map<Long, List<Long>> workerPointMap = new LinkedHashMap<>();
        for (DayDTO day : week.getDays()) {
            // Generamos una lista con los trabajadores que ya han sido ocupados para este dia.
            List<Long> busyWorkersToday = new ArrayList<>();
            // Añadimos a la lista de ocupados diarios los que esten de descanso.
            checkBusyBreaks(busyWorkersToday, day);
            for (TouristPointDTO touristPoint : touristPoints) {
                // Comprobamos que no existen turnos para este dia en este punto establecido ya.
                if (checkPointNotAssignedDay(day, touristPoint)) {
                    // Comprobamos que existena algun trabajador con horas disponibles del equipo que necesitamos.
                    if (haveAvailableHours(touristPoint)) {
                        Iterator<Map.Entry<TouristInformerDTO, Double>> iterator = availableWorkersHours.entrySet().iterator();
                        Map.Entry<TouristInformerDTO, Double> entry = iterator.next();
                        // Si el trabajador no es correcto, busca el siguiente trabajador (si hay mas).
                        while (iterator.hasNext()
                                && !isCorrectWorker(workerPointMap, busyWorkersToday, touristPoint, entry, day)) {
                            entry = iterator.next();
                        }
                        // Si el trabajador es correcto lo asocia al punto ese dia.
                        if (isCorrectWorker(workerPointMap, busyWorkersToday, touristPoint, entry, day)) {
                            associateShift(day, touristPoint, entry.getKey(), busyWorkersToday, workerPointMap);
                            // Actualizamos las horas disponibles de esta semana para el trabajador
                            entry.setValue(entry.getValue() - touristPoint.getTime());
                        } else {
                            errorPoints.add(new TouristPointDayProblemDTO(touristPoint, day));
                        }
                    } else {
                        errorPoints.add(new TouristPointDayProblemDTO(touristPoint, day));
                    }
                }
            }
            orderByAvailableHours();
        }
    }

    private void randomMutate(FillWeekDTO fillWeekDTO, WeekKnappSackDTO weekKnappSackDTO) {
        int n = 0;
        while (n < fillWeekDTO.getNumberIteration()) {
            DayDTO randomDay = week.getDays().stream()
                    .skip((int) (week.getDays().size() * Math.random()))
                    .findFirst().get();
            List<ShiftDTO> filteredShifts = randomDay.getShifts()
                    .stream()
                    .filter(shiftDTO -> !shiftDTO.getPoint().getName().equals("Descanso"))
                    .collect(Collectors.toList());
            ShiftDTO randomShift = filteredShifts.stream()
                    .skip((int) (filteredShifts.size() * Math.random()))
                    .findFirst().get();
            randomDay.getShifts().remove(randomShift);
            availableWorkersHours.computeIfPresent(randomShift.getWorker(), (touristInformerDTO, value) -> value + randomShift.getPoint().getTime());
            orderByAvailableHours();
            fillWeek();
            if (getHealthFromAvailableWorkers(availableWorkersHours) < weekKnappSackDTO.getHealth()) {
                weekKnappSackDTO = generateWeekKnappSackDTO();
            }
            if (weekKnappSackDTO.getHealth() == 0) {
                n = fillWeekDTO.getNumberIteration();
            }
            n++;
        }
    }

    private void mutateIfErrorFilling(FillWeekDTO fillWeekDTO) {
        int n = 0;
        while (n < fillWeekDTO.getNumberIteration()) {
            for (TouristPointDayProblemDTO touristPointDayProblemDTO : errorPoints) {
                DayDTO dayDTO = week.getDays().get(touristPointDayProblemDTO.getDay().getDayOfWeek().getValue() - 1);
                TouristPointDTO touristPoint = touristPointDayProblemDTO.getTouristPoint();
                List<ShiftDTO> filteredShifs = dayDTO.getShifts().stream()
                        .filter(shiftDTO -> !shiftDTO.getPoint().getName().equals("Descanso")
                                && touristPoint.getTrainedTeams().contains(shiftDTO.getWorker().getTeam())
                                && touristPoint.getTime() <= shiftDTO.getPoint().getTime())
                        .collect(Collectors.toList());
                ShiftDTO shiftToRemove = filteredShifs.stream()
                        .skip((int) (filteredShifs.size() * Math.random()))
                        .findFirst().get();
                ShiftDTO shiftToAdd = new ShiftDTO();
                shiftToAdd.setPoint(touristPoint);
                shiftToAdd.setWorker(shiftToRemove.getWorker());
                dayDTO.getShifts().remove(shiftToRemove);
                availableWorkersHours.computeIfPresent(shiftToRemove.getWorker(), (touristInformerDTO, value) -> value + shiftToRemove.getPoint().getTime());
                dayDTO.getShifts().add(shiftToAdd);
                availableWorkersHours.computeIfPresent(shiftToAdd.getWorker(), (touristInformerDTO, value) -> value - shiftToAdd.getPoint().getTime());
                orderByAvailableHours();
                errorPoints.remove(touristPointDayProblemDTO);
            }
            fillWeek();
            if (errorPoints.isEmpty()) {
                n = fillWeekDTO.getNumberIteration();
            }
            n++;
        }
    }

    private boolean isCorrectWorker(Map<Long, List<Long>> workerPointMap, List<Long> busyWorkersToday,
                                    TouristPointDTO touristPoint, Map.Entry<TouristInformerDTO, Double> entry, DayDTO day) {
        TouristInformerDTO touristInformer = entry.getKey();
        Long touristInformerId = touristInformer.getId();
        // Comprobamos que el trabajador no esta ocupado hoy, no repite punto en la semana,
        // le quedan horas disponibles esta semana y es del equipo correcto. Tambien comprobamos que ese dia la
        // semana anterior no trabajo en el mismo punto.
        return !busyWorkersToday.contains(touristInformer.getId())
                && !repeatPointAtWeek(workerPointMap, touristPoint, touristInformerId)
                && haveCorrectTeamInformerAvailableHours(touristPoint, entry)
                && !repeatPointSameDayLastWeek(touristPoint, entry.getKey(), day);
    }

    private boolean repeatPointSameDayLastWeek(TouristPointDTO touristPoint, TouristInformerDTO touristInformer, DayDTO day) {
        return lastWeekDatabase != null && lastWeekDatabase.getDays()
                .get(day.getDayOfWeek().getValue() - 1).getShifts()
                .stream()
                .anyMatch(shift ->
                        shift.getPoint().getId().equals(touristPoint.getId())
                                && shift.getWorker().getId().equals(touristInformer.getId()));
    }

    private boolean repeatPointAtWeek(Map<Long, List<Long>> workerPointMap, TouristPointDTO touristPoint, Long touristInformerId) {
        return workerPointMap.get(touristInformerId) != null
                && workerPointMap.get(touristInformerId).contains(touristPoint.getId());
    }

    private boolean checkPointNotAssignedDay(DayDTO day, TouristPointDTO touristPoint) {
        return day.getShifts().stream().noneMatch(shift -> shift.getPoint().getId().equals(touristPoint.getId()));
    }

    private boolean haveAvailableHours(TouristPointDTO touristPoint) {
        return availableWorkersHours.entrySet().stream().anyMatch(entry ->
                haveCorrectTeamInformerAvailableHours(touristPoint, entry));
    }

    private boolean haveCorrectTeamInformerAvailableHours(TouristPointDTO touristPoint, Map.Entry<TouristInformerDTO, Double> entry) {
        double MAX_LAG_HOUR = -1 * 0.25;
        return touristPoint.getTrainedTeams().stream().map(TeamDTO::getId).anyMatch(id -> id.equals(entry.getKey().getTeam().getId()))
                && entry.getValue() - touristPoint.getTime()
                >= MAX_LAG_HOUR * entry.getKey().getWorkHours();
    }

    private void initializeVariables(FillWeekDTO fillWeekDTO) throws EwsException {
        weekDatabase = findWeekByNumberAndYear(fillWeekDTO.getNumberOfWeek(), fillWeekDTO.getYear());
        try {
            lastWeekDatabase = findWeekByNumberAndYear(weekDatabase.getNumberOfWeek() - 1, weekDatabase.getYear());
        } catch (EwsException ewsException) {
            lastWeekDatabase = null;
        }
        week = new WeekDTO(weekDatabase);
        findAvailableWorkersHours();
        // Generamos los descansos de la semana a partir de la anterior o de la manera establecidad para la primera vez
        // teniendo en cuenta los descansos para los trabajadores que tengan las suficientes horas acumuladas negativas.
        generateBreaks();
        // Cargamos la lista de puntos disponibles ordenados por prioridad, obviando el punto descanso
        // que se rellenara de forma independiente.
        getOrderedTouristPoints();
    }

    private Week findWeekByNumberAndYear(Long numberOfWeek, Long year) throws EwsException {
        try {
            return weekRepository.findAll().stream().filter(week ->
                    week.getNumberOfWeek().equals(numberOfWeek)
                            && week.getYear().equals(year)).findFirst().get();
        } catch (Exception e) {
            throw new EwsException("No existe esta semana, creela primero");
        }
    }

    private void findAvailableWorkersHours() {
        for (TouristInformer touristInformer : touristInformerRepository.findAll().stream()
                .filter(worker -> worker.getDismissDate() == null).collect(Collectors.toList())) {
            availableWorkersHours.put(new TouristInformerDTO(touristInformer), touristInformer.getWorkHours() + touristInformer.getAccumulatedHours());
        }
        orderByAvailableHours();
    }

    private void orderByAvailableHours() {
        availableWorkersHours = availableWorkersHours.entrySet()
                .stream()
                .sorted(comparingByValue())
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,
                                LinkedHashMap::new));
    }

    private void generateBreaks() {
        if (lastWeekDatabase != null) {
            for (Day dayLastWeek : lastWeekDatabase.getDays()) {
                for (ShiftDTO shiftLastWeek : dayLastWeek.getShifts()
                        .stream()
                        .filter(shift -> shift.getPoint().getName().equals("Descanso"))
                        .map(ShiftDTO::new)
                        .collect(Collectors.toList())) {
                    boolean bonusDay = shiftLastWeek.getWorker().getAccumulatedHours() <= (-1 * shiftLastWeek.getWorker().getWorkHours() / 5);
                    ShiftDTO breakShift = new ShiftDTO();
                    breakShift.setWorker(shiftLastWeek.getWorker());
                    breakShift.setPoint(shiftLastWeek.getPoint());
                    ShiftDTO breakShift2 = new ShiftDTO();
                    breakShift2.setWorker(shiftLastWeek.getWorker());
                    breakShift2.setPoint(shiftLastWeek.getPoint());
                    switch (dayLastWeek.getDayOfWeek()) {
                        case MONDAY:
                            if (bonusDay) {
                                addBreakDay(breakShift2, DayOfWeek.TUESDAY);
                            }
                            addBreakDay(breakShift, DayOfWeek.WEDNESDAY);
                            break;
                        case TUESDAY:
                        case WEDNESDAY:
                            if (bonusDay) {
                                addBreakDay(breakShift2, DayOfWeek.WEDNESDAY);
                            }
                            addBreakDay(breakShift, DayOfWeek.THURSDAY);
                            break;
                        case THURSDAY:
                            //Si tambien tuvo descanso el miercoles pasado, estamos en el caso en que esta semana tambien descansa el jueves
                            if (lastWeekDatabase.getDays().get(2).getShifts().stream().anyMatch(shift -> shift.getPoint().getName().equals("Descanso")
                                    && Objects.equals(shift.getWorker().getId(), shiftLastWeek.getWorker().getId()))) {
                                addBreakDay(breakShift, DayOfWeek.FRIDAY);
                            } else {
                                if (bonusDay) {
                                    addBreakDay(breakShift2, DayOfWeek.FRIDAY);
                                }
                                addBreakDay(breakShift, DayOfWeek.SATURDAY);
                            }
                            break;
                        case FRIDAY:
                            addBreakDay(breakShift, DayOfWeek.SUNDAY);
                            break;
                        case SATURDAY:
                            if (bonusDay) {
                                addBreakDay(breakShift2, DayOfWeek.WEDNESDAY);
                            }
                            addBreakDay(breakShift, DayOfWeek.MONDAY);
                            break;
                        case SUNDAY:
                            addBreakDay(breakShift, DayOfWeek.TUESDAY);
                            break;
                    }
                }
            }
        } else {
            List<TouristInformer> touristInformers = touristInformerRepository.findAll().stream()
                    .filter(touristInformer -> touristInformer.getDismissDate()==null).collect(Collectors.toList());
            List<Team> teams = teamRepository.findAll();
            TouristPointDTO breakPoint = new TouristPointDTO(touristPointRepository.findAll().stream()
                    .filter(touristPoint -> touristPoint.getName().equals("Descanso")).findFirst().get());
            for (Team team : teams) {
                DayOfWeek lastStartBreak = DayOfWeek.MONDAY;
                for (TouristInformerDTO touristInformer : touristInformers.stream()
                        .filter(touristInformer -> touristInformer.getTeam().equals(team))
                        .map(TouristInformerDTO::new)
                        .collect(Collectors.toList())) {
                    boolean bonusDay = touristInformer.getAccumulatedHours() <= (-1 * touristInformer.getWorkHours() / 5);
                    ShiftDTO shift1 = new ShiftDTO();
                    shift1.setWorker(touristInformer);
                    shift1.setPoint(breakPoint);
                    ShiftDTO shift2 = new ShiftDTO();
                    shift2.setWorker(touristInformer);
                    shift2.setPoint(breakPoint);
                    switch (lastStartBreak) {
                        case MONDAY:
                            if (bonusDay) {
                                addBreakDay(shift1, DayOfWeek.TUESDAY);
                            }
                            addBreakDay(shift1, DayOfWeek.WEDNESDAY);
                            addBreakDay(shift2, DayOfWeek.THURSDAY);
                            lastStartBreak = DayOfWeek.WEDNESDAY;
                            break;
                        case WEDNESDAY:
                            if (bonusDay) {
                                addBreakDay(shift1, DayOfWeek.WEDNESDAY);
                            }
                            addBreakDay(shift1, DayOfWeek.THURSDAY);
                            addBreakDay(shift2, DayOfWeek.FRIDAY);
                            lastStartBreak = DayOfWeek.THURSDAY;
                            break;
                        case THURSDAY:
                            if (bonusDay) {
                                addBreakDay(shift1, DayOfWeek.FRIDAY);
                            }
                            addBreakDay(shift1, DayOfWeek.SATURDAY);
                            addBreakDay(shift2, DayOfWeek.SUNDAY);
                            lastStartBreak = DayOfWeek.SATURDAY;
                            break;
                        case SATURDAY:
                            if (bonusDay) {
                                addBreakDay(shift1, DayOfWeek.WEDNESDAY);
                            }
                            addBreakDay(shift1, DayOfWeek.MONDAY);
                            addBreakDay(shift2, DayOfWeek.TUESDAY);
                            lastStartBreak = DayOfWeek.MONDAY;
                            break;
                    }
                }
            }
        }
    }

    private void addBreakDay(ShiftDTO breakShift, DayOfWeek dayOfWeek) {
        // Comprobamos que no tiene asignado ya los descanso para el dia que le corresponde.
        if (week.getDays().get(dayOfWeek.getValue()-1).getShifts().stream()
                .noneMatch(shiftDTO -> shiftDTO.getPoint().getName().equals("Descanso")
                        && shiftDTO.getWorker().getId().equals(breakShift.getWorker().getId()))) {
            week.getDays().get(dayOfWeek.getValue()-1).getShifts().add(breakShift);
        }
    }

    private void getOrderedTouristPoints() {
        touristPoints.addAll(touristPointRepository.findAll().stream()
                .filter(touristPoint -> !touristPoint.getName().equals("Descanso")
                && touristPoint.getDismissDate() == null)
                .map(TouristPointDTO::new)
                .sorted(Comparator.comparing(TouristPointDTO::getPriority)
                        .reversed())
                .collect(Collectors.toList()));
    }

    private void checkBusyBreaks(List<Long> busyWorkers, DayDTO day) {
        for (ShiftDTO shift : day.getShifts()) {
            busyWorkers.add(shift.getWorker().getId());
        }
    }

    private void associateShift(DayDTO day, TouristPointDTO touristPoint, TouristInformerDTO touristInformer,
                                List<Long> busyWorkersToday, Map<Long, List<Long>> workerPointMap) {
        ShiftDTO shift = new ShiftDTO();
        shift.setPoint(touristPoint);
        shift.setWorker(touristInformer);
        day.getShifts().add(shift);
        busyWorkersToday.add(touristInformer.getId());
        if (workerPointMap.containsKey(touristInformer.getId())) {
            workerPointMap.get(touristInformer.getId()).add(touristPoint.getId());
        } else {
            List<Long> idPoints = new ArrayList<>();
            idPoints.add(touristPoint.getId());
            workerPointMap.put(touristInformer.getId(), idPoints);
        }
    }

    private WeekKnappSackDTO generateWeekKnappSackDTO() {
        Map<TouristInformerDTO, Double> availableWorkersHoursToWeekKnappSackDTO = new LinkedHashMap<>(availableWorkersHours);
        return new WeekKnappSackDTO(week.toBuilder().build(), availableWorkersHoursToWeekKnappSackDTO,
                getHealthFromAvailableWorkers(availableWorkersHours));
    }

    private Double getHealthFromAvailableWorkers(Map<TouristInformerDTO, Double> availableWorkersHours) {
        return availableWorkersHours.values().stream().map(value -> value = Math.abs(value)).max(Double::compareTo).get();
    }

    private void saveAll(WeekKnappSackDTO weekKnappSackDTO) {
        for (DayDTO day : weekKnappSackDTO.getWeek().getDays()) {
            Day dayDatabase = weekDatabase.getDays().get(day.getDayOfWeek().getValue() - 1);
            List<Shift> shifts = new ArrayList<>();
            for (ShiftDTO shiftDTO : day.getShifts()) {
                Shift shift = new Shift();
                shift.setPoint(pointRepository.findById(shiftDTO.getPoint().getId()).get());
                shift.setWorker(workerRepository.findById(shiftDTO.getWorker().getId()).get());
                shifts.add(shift);
                shiftRepository.save(shift);
            }
            dayDatabase.setShifts(shifts);
            dayRepository.save(dayDatabase);
        }
        //Actualizamos las horas acumuladas
        for (TouristInformerDTO touristInformerDTO : weekKnappSackDTO.getAvailableWorkersHours().keySet()) {
            TouristInformer touristInformer = touristInformerRepository.findById(touristInformerDTO.getId()).get();
            touristInformer.setAccumulatedHours(weekKnappSackDTO.getAvailableWorkersHours().get(touristInformerDTO));
            touristInformerRepository.save(touristInformer);
        }
        weekRepository.save(weekDatabase);
    }

    private <K, V extends Comparable<? super V>> Comparator<Map.Entry<K, V>> comparingByValue() {
        return (Comparator<Map.Entry<K, V>> & Serializable)
                (c1, c2) -> c2.getValue().compareTo(c1.getValue());
    }
}
