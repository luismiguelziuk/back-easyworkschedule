package tfg.luismiguel.ews.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tfg.luismiguel.ews.dto.cex.TouristInformerDTO;
import tfg.luismiguel.ews.dto.cex.TouristPointDTO;
import tfg.luismiguel.ews.entity.cex.TouristInformer;
import tfg.luismiguel.ews.entity.cex.TouristPoint;
import tfg.luismiguel.ews.repository.cex.TouristInformerRepository;
import tfg.luismiguel.ews.repository.cex.TouristPointRepository;
import tfg.luismiguel.ews.service.impl.CexServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Clase para test de {@link CexService}
 */
@Tag("cexServiceTest")
public class CexServiceTest {
    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Mock
    private TouristPointRepository touristPointRepository;
    @Mock
    private TouristInformerRepository touristInformerRepository;
    @InjectMocks
    private CexServiceImpl cexService;

    @Test
    public void createTouristPointTest() {
        when(touristPointRepository.save(any())).thenReturn(new TouristPoint());
        cexService.createTouristPoint(new TouristPointDTO());
        verify(touristPointRepository, atLeast(1)).save(any());
    }

    @Test
    public void createTouristInformerTest() {
        when(touristPointRepository.save(any())).thenReturn(new TouristInformer());
        cexService.createTouristInformer(new TouristInformerDTO());
        verify(touristInformerRepository, atLeast(1)).save(any());
    }
}
