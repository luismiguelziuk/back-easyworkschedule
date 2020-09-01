package tfg.luismiguel.ews.service.impl;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tfg.luismiguel.ews.dto.DayDTO;
import tfg.luismiguel.ews.dto.ShiftDTO;
import tfg.luismiguel.ews.dto.WeekDTO;
import tfg.luismiguel.ews.dto.algorithm.CleanWeekDTO;
import tfg.luismiguel.ews.dto.algorithm.FillWeekDTO;
import tfg.luismiguel.ews.dto.algorithm.WeekKnappSackDTO;
import tfg.luismiguel.ews.dto.algorithm.cex.TouristPointDayProblemDTO;
import tfg.luismiguel.ews.dto.cex.TeamDTO;
import tfg.luismiguel.ews.dto.cex.TouristInformerDTO;
import tfg.luismiguel.ews.dto.cex.TouristPointDTO;
import tfg.luismiguel.ews.entity.AccumulatedHours;
import tfg.luismiguel.ews.entity.Day;
import tfg.luismiguel.ews.entity.Shift;
import tfg.luismiguel.ews.entity.Week;
import tfg.luismiguel.ews.entity.cex.Team;
import tfg.luismiguel.ews.entity.cex.TouristInformer;
import tfg.luismiguel.ews.exception.EwsException;
import tfg.luismiguel.ews.repository.AccumulatedRepository;
import tfg.luismiguel.ews.repository.DayRepository;
import tfg.luismiguel.ews.repository.PointRepository;
import tfg.luismiguel.ews.repository.ShiftRepository;
import tfg.luismiguel.ews.repository.WeekRepository;
import tfg.luismiguel.ews.repository.WorkerRepository;
import tfg.luismiguel.ews.repository.cex.TeamRepository;
import tfg.luismiguel.ews.repository.cex.TouristInformerRepository;
import tfg.luismiguel.ews.repository.cex.TouristPointRepository;
import tfg.luismiguel.ews.service.AlgorithmService;
import tfg.luismiguel.ews.service.CexService;
import tfg.luismiguel.ews.service.TemporalService;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    AccumulatedRepository accumulatedRepository;
    @Autowired
    TouristPointRepository touristPointRepository;
    @Autowired
    TouristInformerRepository touristInformerRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    TemporalService temporalService;
    @Autowired
    CexService cexService;

    private Week lastWeekDatabase;
    private WeekDTO week;
    private Map<TouristInformerDTO, Double> availableWorkersHours;
    private List<TouristPointDTO> touristPoints;
    private List<TouristPointDayProblemDTO> errorPoints;
    public static WeekKnappSackDTO solution;

    @Override
    public void saveAll(WeekKnappSackDTO weekKnappSackDTO, FillWeekDTO fillWeekDTO) throws EwsException {
        Week weekDatabase = temporalService.findWeekByNumberAndYear(fillWeekDTO.getNumberOfWeek(), fillWeekDTO.getYear());
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
        List<AccumulatedHours> accumulatedHoursList = new ArrayList<>();
        for (TouristInformerDTO touristInformerDTO : weekKnappSackDTO.getAvailableWorkersHours().keySet()) {
            AccumulatedHours accumulatedHour =  new AccumulatedHours();
            TouristInformer touristInformer = touristInformerRepository.findById(touristInformerDTO.getId()).get();
            accumulatedHour.setWorker(touristInformer);
            accumulatedHour.setAccumulatedHour(weekKnappSackDTO.getAvailableWorkersHours().get(touristInformerDTO));
            accumulatedHoursList.add(accumulatedHour);
            accumulatedRepository.save(accumulatedHour);
        }
        weekDatabase.setAccumulatedHours(accumulatedHoursList);
        weekRepository.save(weekDatabase);
    }

    @Override
    public WeekKnappSackDTO fillCompleteWeek(FillWeekDTO fillWeekDTO) throws EwsException {
        // Inicializamos las variables
        initializeVariables(fillWeekDTO);
        // En una primera instancia rellenamos la semana cumpliendo los requisitos, y tratando de no repetir puntos
        fillWeek(false);
        // Metodo para mutar un punto de los puntos de informacion para el dia que ha dado error.
        // Tratando de rellenarlo con un trabajador que ya este ocupado esa semana, metiendo uno libre en el lugar que ocupaba,
        // para ver si conseguimos la solucion parcial. Esto se intentara n veces tambien y si no se consigue se dara como irresoluble.
        mutateIfErrorFilling(fillWeekDTO);
        // Para la primera iteracion inicializamos la mejor solucion como la primera dada.
        if (solution == null) {
            solution = generateSolution();
            //Si la solucion que propone esta iteracion es mejor que las anteriores la guardamos como mejor solucion
        } else if (getHealthFromAvailableWorkers(availableWorkersHours, errorPoints.size()) < solution.getHealth()) {
            solution = generateSolution();
        }
        // Solucion final al problema
        if (solution.getHealth() == 0) {
            fillWeekDTO.setNumberIteration(0);
        } else if (fillWeekDTO.getNumberIteration() >= 0) {
            fillWeekDTO.setNumberIteration(fillWeekDTO.getNumberIteration() - 1);
            this.fillCompleteWeek(fillWeekDTO);
        }
        // No hemos optenido solucion parcial.
        if (!solution.getErrors().isEmpty()) {
            StringBuilder message = new StringBuilder();
            solution.getErrors().forEach(problem -> message.append(problem.getDay().getDayOfWeek().toString()).append(" ")
                    .append(problem.getTouristPoint().getName()).append(", "));
            throw new EwsException("No tiene solucion, no hay trabajadores suficientes para los puntos { " + message.substring(0, message.length() - 2) + " }");
        }
        // Devolvemos el mejor resultado que hemos optenido
        return solution;
    }

    @Override
    public void cleanWeek(CleanWeekDTO cleanWeekDTO) throws EwsException {
        Week week = temporalService.findWeekByNumberAndYear(cleanWeekDTO.getNumberOfWeek(), cleanWeekDTO.getYear());
        for (Day day : week.getDays()) {
            List<Shift> shiftsToRemove = new ArrayList<>();
            for (Shift shift : day.getShifts()) {
                shiftsToRemove.add(shift);
                shiftRepository.delete(shift);
            }
            day.getShifts().removeAll(shiftsToRemove);
            dayRepository.save(day);
        }
        List<AccumulatedHours> accumulatedHoursToRemove = new ArrayList<>();
        for (AccumulatedHours accumulatedHours : week.getAccumulatedHours()) {
            accumulatedHoursToRemove.add(accumulatedHours);
            accumulatedRepository.delete(accumulatedHours);
        }
        week.getAccumulatedHours().removeAll(accumulatedHoursToRemove);
        weekRepository.save(week);
    }

    private void fillWeek(Boolean canRepeatPoint) {
        // Generamos un mapa con los id de los worker y los id de los puntos en los que ha trabajado en la semana,
        // ya que no puede repetir punto.
        Map<Long, List<Long>> workerPointMap = new LinkedHashMap<>();
        for (DayDTO day : week.getDays()) {
            // Generamos una lista con los trabajadores que ya han sido ocupados para este dia.
            List<Long> busyWorkersToday = checkBusyBreaks(day);
            for (TouristPointDTO touristPoint : touristPoints) {
                // Comprobamos que no existen turnos para este dia en este punto establecido ya.
                if (checkPointNotAssignedDay(day, touristPoint)) {
                    // Comprobamos que existena algun trabajador con horas disponibles del equipo que necesitamos.
                    if (haveAvailableHours(touristPoint)) {
                        Iterator<Map.Entry<TouristInformerDTO, Double>> iterator = availableWorkersHours.entrySet().iterator();
                        Map.Entry<TouristInformerDTO, Double> entry = iterator.next();
                        // Si el trabajador no es correcto, busca el siguiente trabajador (si hay mas).
                        while (iterator.hasNext()
                                && !isCorrectWorker(workerPointMap, busyWorkersToday, touristPoint, entry, day,
                                canRepeatPoint)) {
                            entry = iterator.next();
                        }
                        // Si el trabajador es correcto lo asocia al punto ese dia.
                        if (isCorrectWorker(workerPointMap, busyWorkersToday, touristPoint, entry, day,
                                canRepeatPoint)) {
                            associateShift(day, touristPoint, entry.getKey(), workerPointMap);
                            busyWorkersToday.add(entry.getKey().getId());
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

    private void mutateIfErrorFilling(FillWeekDTO fillWeekDTO) {
        int n = 0;
        if (!errorPoints.isEmpty()) {
            while (n < fillWeekDTO.getNumberOfMutation()) {
                TouristPointDayProblemDTO touristPointDayProblemDTO = errorPoints.stream()
                        .skip((int) (errorPoints.size() * Math.random())).findFirst().orElse(null);
                if (touristPointDayProblemDTO != null) {
                    DayDTO dayDTO = week.getDays().get(touristPointDayProblemDTO.getDay().getDayOfWeek().getValue() - 1);
                    TouristPointDTO touristPoint = touristPointDayProblemDTO.getTouristPoint();
                    List<ShiftDTO> filteredShifs = dayDTO.getShifts().stream()
                            .filter(shiftDTO -> !shiftDTO.getPoint().getName().equals("Descanso")
                                    && !shiftDTO.getPoint().getName().equals("DescansoAleatorio")
                                    && touristPoint.getTrainedTeams().stream()
                                    .map(TeamDTO::getId).collect(Collectors.toList())
                                    .contains(shiftDTO.getWorker().getTeam().getId())
                                    && touristPoint.getTime() <= shiftDTO.getPoint().getTime())
                            .collect(Collectors.toList());
                    if (!filteredShifs.isEmpty()) {
                        ShiftDTO shiftToRemove = filteredShifs.stream()
                                .skip((int) (filteredShifs.size() * Math.random()))
                                .findFirst().orElse(null);
                        if (shiftToRemove != null) {
                            ShiftDTO shiftToAdd = new ShiftDTO();
                            shiftToAdd.setPoint(touristPoint);
                            shiftToAdd.setWorker(shiftToRemove.getWorker());
                            dayDTO.getShifts().remove(shiftToRemove);
                            availableWorkersHours.computeIfPresent(shiftToRemove.getWorker(), (touristInformerDTO, value) -> value + shiftToRemove.getPoint().getTime());
                            dayDTO.getShifts().add(shiftToAdd);
                            availableWorkersHours.computeIfPresent(shiftToAdd.getWorker(), (touristInformerDTO, value) -> value - shiftToAdd.getPoint().getTime());
                            orderByAvailableHours();
                            errorPoints.clear();
                            // En este momento es cuando podemos permitir el repetir punto en la semana
                            fillWeek(true);
                        }
                    }
                }
                if (errorPoints.isEmpty()) {
                    n = fillWeekDTO.getNumberOfMutation();
                }
                n++;
            }
        }
    }

    private boolean isCorrectWorker(Map<Long, List<Long>> workerPointMap, List<Long> busyWorkersToday,
                                    TouristPointDTO touristPoint, Map.Entry<TouristInformerDTO, Double> entry,
                                    DayDTO day, Boolean canRepeatPoint) {
        TouristInformerDTO touristInformer = entry.getKey();
        Long touristInformerId = touristInformer.getId();
        // Comprobamos que el trabajador no esta ocupado hoy, no repite punto en la semana,
        // le quedan horas disponibles esta semana y es del equipo correcto. Tambien comprobamos que ese dia la
        // semana anterior no trabajo en el mismo punto.
        return !busyWorkersToday.contains(touristInformer.getId())
                && !repeatPointAtWeek(canRepeatPoint, workerPointMap, touristPoint, touristInformerId)
                && haveCorrectTeamInformerAvailableHours(touristPoint, entry)
                && !repeatPointSameDayLastWeek(canRepeatPoint, touristPoint, entry.getKey(), day);
    }

    private boolean repeatPointSameDayLastWeek(Boolean canRepeatPoint, TouristPointDTO touristPoint, TouristInformerDTO touristInformer, DayDTO day) {
        if (canRepeatPoint) {
            return false;
        } else {
            return lastWeekDatabase != null && lastWeekDatabase.getDays()
                    .get(day.getDayOfWeek().getValue() - 1).getShifts()
                    .stream()
                    .anyMatch(shift ->
                            shift.getPoint().getId().equals(touristPoint.getId())
                                    && shift.getWorker().getId().equals(touristInformer.getId()));
        }
    }

    private boolean repeatPointAtWeek(Boolean canRepeatPoint, Map<Long, List<Long>> workerPointMap, TouristPointDTO touristPoint, Long touristInformerId) {
        if (canRepeatPoint) {
            return false;
        } else {
            //Solo puede repetirse en una semana punto Visitor 3, ya que solo puede hacerlo el equipo visitor
            return !touristPoint.getName().equals("Visitor 3")
                    && workerPointMap.get(touristInformerId) != null
                    && workerPointMap.get(touristInformerId).contains(touristPoint.getId());
        }
    }

    private boolean checkPointNotAssignedDay(DayDTO day, TouristPointDTO touristPoint) {
        return day.getShifts().stream().noneMatch(shift -> shift.getPoint().getId().equals(touristPoint.getId()));
    }

    private boolean haveAvailableHours(TouristPointDTO touristPoint) {
        return availableWorkersHours.entrySet().stream().anyMatch(entry ->
                haveCorrectTeamInformerAvailableHours(touristPoint, entry));
    }

    private boolean haveCorrectTeamInformerAvailableHours(TouristPointDTO touristPoint, Map.Entry<TouristInformerDTO, Double> entry) {
        double MAX_LAG_HOUR = -1 * 0.35;
        return touristPoint.getTrainedTeams().stream().map(TeamDTO::getId).anyMatch(id -> id.equals(entry.getKey().getTeam().getId()))
                && entry.getValue() - touristPoint.getTime()
                >= MAX_LAG_HOUR * entry.getKey().getWorkHours();
    }

    private void initializeVariables(FillWeekDTO fillWeekDTO) throws EwsException {
        availableWorkersHours = new LinkedHashMap<>();
        errorPoints = new ArrayList<>();
        touristPoints = new ArrayList<>();
        Week weekDatabase = temporalService.findWeekByNumberAndYear(fillWeekDTO.getNumberOfWeek(), fillWeekDTO.getYear());
        try {
            lastWeekDatabase = temporalService.findWeekByNumberAndYear(weekDatabase.getNumberOfWeek() - 1, weekDatabase.getYear());
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

    private void findAvailableWorkersHours() {
        Double accumulated = 0.0;
        for (TouristInformer touristInformer : touristInformerRepository.findAll().stream()
                .filter(worker -> worker.getDismissDate() == null).collect(Collectors.toList())) {
            if(lastWeekDatabase!=null) {
                accumulated = lastWeekDatabase.getAccumulatedHours().stream()
                        .filter(accumulatedHours ->  accumulatedHours.getWorker().getId().equals(touristInformer.getId()))
                        .findFirst().get().getAccumulatedHour();
            }
            availableWorkersHours.put(new TouristInformerDTO(touristInformer), touristInformer.getWorkHours() + accumulated);
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
        //Comprueba que existen los puntos Descanso y Descanso aleatorio, si no existen los crea.
        cexService.checkOrCreateBreakPoint();
        //Todos los dias menos el miercoles que es con el que va a compartir semana.
        List<DayOfWeek> dayRandomList = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
        TouristPointDTO randomBreakShift = new TouristPointDTO(touristPointRepository.findAll().stream()
                .filter(touristPoint -> touristPoint.getName().equals("Descanso Aleatorio")).findFirst().get());
        //No deberiamos dar mas de x dias de bonus, porque podemos crear tantos descansos que no haya nadie para
        // trabajar en algun momento dado.
        if (lastWeekDatabase != null) {
            for (Day dayLastWeek : lastWeekDatabase.getDays()) {
                for (ShiftDTO shiftLastWeek : dayLastWeek.getShifts()
                        .stream()
                        .filter(shift -> shift.getPoint().getName().equals("Descanso"))
                        .map(ShiftDTO::new)
                        .collect(Collectors.toList())) {
                    ShiftDTO breakShift = new ShiftDTO();
                    breakShift.setWorker(shiftLastWeek.getWorker());
                    breakShift.setPoint(shiftLastWeek.getPoint());
                    ShiftDTO breakShift2 = new ShiftDTO();
                    breakShift2.setWorker(shiftLastWeek.getWorker());
                    breakShift2.setPoint(shiftLastWeek.getPoint());
                    ShiftDTO breakShift3 = new ShiftDTO();
                    breakShift3.setWorker(shiftLastWeek.getWorker());
                    breakShift3.setPoint(shiftLastWeek.getPoint());
                    switch (dayLastWeek.getDayOfWeek()) {
                        case MONDAY:
                            addBreakDay(breakShift, DayOfWeek.WEDNESDAY);
                            break;
                        case TUESDAY:
                            breakShift.setPoint(randomBreakShift);
                            addBreakDay(breakShift, dayRandomList.stream()
                                    .skip((int) (dayRandomList.size() * Math.random()))
                                    .findFirst().get());
                            break;
                        case WEDNESDAY:
                            addBreakDay(breakShift, DayOfWeek.THURSDAY);
                            addBreakDay(breakShift3, DayOfWeek.FRIDAY);
                            break;
                        case THURSDAY:
                            addBreakDay(breakShift, DayOfWeek.SATURDAY);
                            break;
                        case FRIDAY:
                            addBreakDay(breakShift, DayOfWeek.SUNDAY);
                            break;
                        case SATURDAY:
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
                    .filter(touristInformer -> touristInformer.getDismissDate() == null).collect(Collectors.toList());
            List<Team> teams = teamRepository.findAll();
            TouristPointDTO breakPoint = new TouristPointDTO(touristPointRepository.findAll().stream()
                    .filter(touristPoint -> touristPoint.getName().equals("Descanso")).findFirst().get());
            for (Team team : teams) {
                DayOfWeek lastStartBreak = DayOfWeek.MONDAY;
                for (TouristInformerDTO touristInformer : touristInformers.stream()
                        .filter(touristInformer -> touristInformer.getTeam().equals(team))
                        .map(TouristInformerDTO::new)
                        .collect(Collectors.toList())) {
                    ShiftDTO shift1 = new ShiftDTO();
                    shift1.setWorker(touristInformer);
                    shift1.setPoint(breakPoint);
                    ShiftDTO shift2 = new ShiftDTO();
                    shift2.setWorker(touristInformer);
                    shift2.setPoint(breakPoint);
                    switch (lastStartBreak) {
                        case MONDAY:
                            addBreakDay(shift1, DayOfWeek.WEDNESDAY );
                            shift2.setPoint(randomBreakShift);
                            addBreakDay(shift2, dayRandomList.stream()
                                    .skip((int) (dayRandomList.size() * Math.random()))
                                    .findFirst().get());
                            lastStartBreak = DayOfWeek.WEDNESDAY;
                            break;
                        case WEDNESDAY:
                            addBreakDay(shift1, DayOfWeek.THURSDAY );
                            addBreakDay(shift2, DayOfWeek.FRIDAY);
                            lastStartBreak = DayOfWeek.THURSDAY;
                            break;
                        case THURSDAY:
                            addBreakDay(shift1, DayOfWeek.SATURDAY);
                            addBreakDay(shift2, DayOfWeek.SUNDAY );
                            lastStartBreak = DayOfWeek.SATURDAY;
                            break;
                        case SATURDAY:
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
        if (week.getDays().get(dayOfWeek.getValue() - 1).getShifts().stream()
                .noneMatch(shiftDTO -> (shiftDTO.getPoint().getName().equals(breakShift.getPoint().getName()))
                        && shiftDTO.getWorker().getId().equals(breakShift.getWorker().getId()))) {
            week.getDays().get(dayOfWeek.getValue() - 1).getShifts().add(breakShift);
        }
    }

    private void getOrderedTouristPoints() {
        touristPoints.addAll(touristPointRepository.findAll().stream()
                .filter(touristPoint -> !touristPoint.getName().equals("Descanso")
                        && !touristPoint.getName().equals("Descanso Aleatorio")
                        && touristPoint.getDismissDate() == null)
                .map(TouristPointDTO::new)
                .sorted(Comparator.comparingDouble(o -> (2 * o.getTrainedTeams().size()) - o.getPriority()))
                .collect(Collectors.toList()));
    }

    private List<Long> checkBusyBreaks(DayDTO day) {
        List<Long> busyWorkers = new ArrayList<>();
        for (ShiftDTO shift : day.getShifts()) {
            busyWorkers.add(shift.getWorker().getId());
        }
        return busyWorkers;
    }

    private void associateShift(DayDTO day, TouristPointDTO touristPoint, TouristInformerDTO touristInformer,
                                Map<Long, List<Long>> workerPointMap) {
        ShiftDTO shift = new ShiftDTO();
        shift.setPoint(touristPoint);
        shift.setWorker(touristInformer);
        day.getShifts().add(shift);
        if (workerPointMap.containsKey(touristInformer.getId())) {
            workerPointMap.get(touristInformer.getId()).add(touristPoint.getId());
        } else {
            List<Long> idPoints = new ArrayList<>();
            idPoints.add(touristPoint.getId());
            workerPointMap.put(touristInformer.getId(), idPoints);
        }
    }

    private WeekKnappSackDTO generateSolution() {
        Map<TouristInformerDTO, Double> availableWorkersHoursToWeekKnappSackDTO = new LinkedHashMap<>(availableWorkersHours);
        return new WeekKnappSackDTO(week.toBuilder().build(), availableWorkersHoursToWeekKnappSackDTO,
                getHealthFromAvailableWorkers(availableWorkersHours, errorPoints.size()), errorPoints);
    }

    private Double getHealthFromAvailableWorkers(Map<TouristInformerDTO, Double> availableWorkersHours, int errorPoints) {
        //Media
        return ((errorPoints * errorPoints) + 0.1) * Math.abs(availableWorkersHours.values().stream().mapToDouble(a -> a).average().getAsDouble())
                * availableWorkersHours.values().stream().map(value -> value = Math.abs(value)).max(Double::compareTo).get();
    }

    private <K, V extends Comparable<? super V>> Comparator<Map.Entry<K, V>> comparingByValue() {
        return (Comparator<Map.Entry<K, V>> & Serializable)
                (c1, c2) -> c2.getValue().compareTo(c1.getValue());
    }
}
