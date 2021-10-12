package com.oc.paymybuddy.service;

import com.oc.paymybuddy.domain.BankTransaction;
import com.oc.paymybuddy.domain.User;
import com.oc.paymybuddy.repositories.BankTransactionRepository;
import com.oc.paymybuddy.service.impl.BankTransactionServiceImpl;
import com.oc.paymybuddy.service.impl.LocalDateTimeServiceImpl;
import com.oc.paymybuddy.service.interfaces.PagingService;
import com.oc.paymybuddy.service.interfaces.UserService;
import com.oc.paymybuddy.utils.page.Paged;
import com.oc.paymybuddy.utils.page.Paging;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BankTransactionServiceImplTest {

    @InjectMocks
    BankTransactionServiceImpl bankTransactionServiceImpl;

    @Mock
    UserService userService;
    @Mock
    BankTransactionRepository bankTransactionRepository;
    @Mock
    LocalDateTimeServiceImpl localDateTimeServiceImpl;
    @Mock
    PagingService pagingService;

    LocalDateTime now;
    User user1;
    BankTransaction bankTransaction;

    @BeforeEach
    void initialize() {
        now = LocalDateTime.of(2019, Month.MARCH, 28, 14, 33, 48);
        user1 = new User(1L,"John","Doe","johndoe@mail.com",now,"password",true,"1234",
                null, Currency.getInstance("USD"),new HashSet<>(),new HashSet<>(),new HashSet<>(),new HashSet<>());

        bankTransaction = new BankTransaction(50L, user1, "1234", now, new BigDecimal("1000"), Currency.getInstance("USD"));
    }

    @Test
    void testCreate() {
        //Arrange

        when(userService.getCurrentUser()).thenReturn(user1);
        when(localDateTimeServiceImpl.now()).thenReturn(now);

        BankTransaction bankTransactionExpected = new BankTransaction(null, user1, "1234", now, new BigDecimal("1000"), Currency.getInstance("USD"));

        BankTransaction bankTransactionToCreate = new BankTransaction();
        bankTransactionToCreate.setAmount(new BigDecimal("1000"));
        bankTransactionToCreate.setCurrency(Currency.getInstance("USD"));;

        //Act
        bankTransactionServiceImpl.create(bankTransactionToCreate);

        // Assert
        verify(bankTransactionRepository, times(1)).save(bankTransactionToCreate);
        assertNull(bankTransactionToCreate.getId());
        assertEquals(bankTransactionExpected.getAmount(),bankTransactionToCreate.getAmount());
        assertEquals(bankTransactionExpected.getBankaccountnumber(),bankTransactionToCreate.getBankaccountnumber());
        assertEquals(bankTransactionExpected.getCurrency(),bankTransactionToCreate.getCurrency());
        assertEquals(bankTransactionExpected.getDatetime(),bankTransactionToCreate.getDatetime());
        assertEquals(bankTransactionExpected.getId(),bankTransactionToCreate.getId());
        assertEquals(bankTransactionExpected.getUser(),bankTransactionToCreate.getUser());

    }

    @Test
    void test_getCurrentUserBankTransactionPage() throws Exception {
        // Arrange

        //Page:
        when(userService.getCurrentUser()).thenReturn(user1);
        List<BankTransaction> bankTransactions = new ArrayList<>();
        bankTransactions.add(bankTransaction);
        Page<BankTransaction> expectedPage = new PageImpl<>(bankTransactions);
        when(bankTransactionRepository.findBankTransactionByUserId(any(Long.class),any(Pageable.class))).thenReturn(expectedPage);
        //Paging:
        Paging expectedPaging = new Paging(false, false, 1, new ArrayList<>());
        when(pagingService.of(any(Integer.class), any(Integer.class))).thenReturn(expectedPaging);

        // Act
        Paged<BankTransaction> pagedBankTransaction =  bankTransactionServiceImpl.getCurrentUserBankTransactionPage(1, 5);

        // Assert
        assertEquals(expectedPage,pagedBankTransaction.getPage());
        assertEquals(expectedPaging,pagedBankTransaction.getPaging());

    }
}
