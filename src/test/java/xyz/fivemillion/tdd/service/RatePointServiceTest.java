package xyz.fivemillion.tdd.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RatePointServiceTest {

    @InjectMocks
    RatePointService ratePointService;

    @Test
    public void _10000원의적립금은100원() {
        //given
        final int price = 10000;

        //when
        final int result = ratePointService.calculateAmount(price);

        //then
        assertEquals(100, result);
    }

    @Test
    public void _20000원의적립금은200원() {
        //given
        final int price = 20000;

        //when
        final int result = ratePointService.calculateAmount(price);

        //then
        assertEquals(200, result);
    }

    @Test
    public void _30000원의적립금은300원() {
        //given
        final int price = 30000;

        //when
        final int result = ratePointService.calculateAmount(price);

        //then
        assertEquals(300, result);
    }
}
