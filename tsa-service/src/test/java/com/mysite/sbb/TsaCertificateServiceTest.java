package com.mysite.sbb;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.BeforeEach;

import com.goono.tsaService.tsaCertificate.TsaCertificate;
import com.goono.tsaService.tsaCertificate.TsaCertificateRepository;
import com.goono.tsaService.tsaCertificate.TsaCertificateService;
import com.goono.tsaService.tsaCertificate.TsaRunner;
import com.goono.tsaService.tsaCertificate.TsaTokenParser;

class TsaCertificateServiceTest {

    @Mock
    private TsaRunner tsaRunner;

    @Mock
    private TsaCertificateRepository tsaCertificateRepository;

    @Mock
    private TsaTokenParser tsaTokenParser;

    @InjectMocks
    private TsaCertificateService tsaCertificateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreate_shouldCallDependenciesAndSaveCertificate() throws Exception {
        String hash = "dummyHash";
        String dummyToken = "dummyToken";
        TsaCertificate dummyCertificate = new TsaCertificate();

        when(tsaRunner.run(hash)).thenReturn(dummyToken);
        when(tsaTokenParser.parse(dummyToken)).thenReturn(dummyCertificate);

        tsaCertificateService.create(hash);

        verify(tsaRunner).ready();
        verify(tsaRunner).run(hash);
        verify(tsaTokenParser).parse(dummyToken);
        verify(tsaCertificateRepository).save(dummyCertificate);
    }

    @Test
    void testGetList_shouldReturnAllCertificates() {
        List<TsaCertificate> dummyList = Arrays.asList(new TsaCertificate(), new TsaCertificate());
        when(tsaCertificateRepository.findAll()).thenReturn(dummyList);

        List<TsaCertificate> result = tsaCertificateService.getList();

        assertEquals(2, result.size());
        verify(tsaCertificateRepository).findAll();
    }
}
