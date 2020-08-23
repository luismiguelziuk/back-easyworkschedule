package tfg.luismiguel.ews.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import tfg.luismiguel.ews.dto.WeekCreationDTO;
import tfg.luismiguel.ews.dto.cex.TouristInformerCreationDTO;
import tfg.luismiguel.ews.dto.cex.TouristPointCreationDTO;
import tfg.luismiguel.ews.entity.Week;
import tfg.luismiguel.ews.entity.cex.TouristInformer;
import tfg.luismiguel.ews.entity.cex.TouristPoint;
import tfg.luismiguel.ews.repository.DayRepository;
import tfg.luismiguel.ews.repository.WeekRepository;
import tfg.luismiguel.ews.repository.cex.TouristInformerRepository;
import tfg.luismiguel.ews.repository.cex.TouristPointRepository;
import tfg.luismiguel.ews.service.impl.CexServiceImpl;
import tfg.luismiguel.ews.service.impl.TemporalServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Clase para test de {@link CexService}
 */
@Tag("temporalServiceTest")
public class TemporalServiceTest {
    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Mock
    private WeekRepository weekRepository;
    @Mock
    private DayRepository dayRepository;
    @InjectMocks
    private TemporalServiceImpl temporalService;

    @Test
    public void createTouristPointTest() {
        temporalService.createWeek(new WeekCreationDTO());
        verify(weekRepository, atLeast(1)).save(any());
        verify(dayRepository, atLeast(7)).save(any());
    }
}
