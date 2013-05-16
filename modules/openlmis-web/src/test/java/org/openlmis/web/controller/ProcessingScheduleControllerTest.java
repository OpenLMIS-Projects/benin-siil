/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.web.controller;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.core.domain.ProcessingSchedule;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.ProcessingScheduleService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.authentication.web.UserAuthenticationSuccessHandler.USER_ID;
import static org.openlmis.web.controller.ProcessingScheduleController.SCHEDULE;
import static org.openlmis.web.controller.ProcessingScheduleController.SCHEDULES;

@Category(UnitTests.class)
public class ProcessingScheduleControllerTest {
  @Rule
  public ExpectedException expectedEx = org.junit.rules.ExpectedException.none();

  String scheduleName = "Test schedule name";

  private static final Long userId = 1L;
  private MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

  @Mock
  ProcessingScheduleService processingScheduleService;

  ProcessingScheduleController processingScheduleController;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    processingScheduleController = new ProcessingScheduleController(processingScheduleService);
    MockHttpSession mockHttpSession = new MockHttpSession();
    httpServletRequest.setSession(mockHttpSession);
    mockHttpSession.setAttribute(USER_ID, userId);
  }

  @Test
  public void shouldGetAll() throws Exception {
    List<ProcessingSchedule> processingSchedules = new ArrayList<>();
    when(processingScheduleService.getAll()).thenReturn(processingSchedules);

    ResponseEntity<OpenLmisResponse> responseEntity = processingScheduleController.getAll();

    Map<String, Object> responseEntityData = responseEntity.getBody().getData();
    assertThat((List<ProcessingSchedule>) responseEntityData.get(SCHEDULES), is(processingSchedules));
  }

  @Test
  public void shouldGetASchedule() throws Exception {

    ProcessingSchedule processingSchedule = new ProcessingSchedule();
    when(processingScheduleService.get(1L)).thenReturn(processingSchedule);

    ResponseEntity<OpenLmisResponse> responseEntity = processingScheduleController.get(1L);

    Map<String, Object> responseEntityData = responseEntity.getBody().getData();
    assertThat((ProcessingSchedule) responseEntityData.get(SCHEDULE), is(processingSchedule));
  }

  @Test
  public void shouldReturnErrorMessageWhileGettingAScheduleForIdThatDoesNotExist() {
    doThrow(new DataException("Schedule not found")).when(processingScheduleService).get(1L);

    ResponseEntity<OpenLmisResponse> response = processingScheduleController.get(1L);

    verify(processingScheduleService).get(1L);

    assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    assertThat(response.getBody().getErrorMsg(), is("Schedule not found"));
  }

  @Test
  public void shouldCreateAndReturnANewSchedule() {
    ProcessingSchedule processingSchedule = new ProcessingSchedule("testCode", "testName");
    ProcessingSchedule mockedSchedule = mock(ProcessingSchedule.class);
    when(processingScheduleService.save(processingSchedule)).thenReturn(mockedSchedule);
    when(mockedSchedule.getName()).thenReturn(scheduleName);

    ResponseEntity<OpenLmisResponse> response = processingScheduleController.create(processingSchedule, httpServletRequest);

    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(response.getBody().getSuccessMsg(), is("'" + scheduleName + "' created successfully"));
    ProcessingSchedule savedSchedule = (ProcessingSchedule) response.getBody().getData().get(SCHEDULE);
    assertThat(savedSchedule, is(mockedSchedule));
  }

  @Test
  public void shouldReturnErrorResponseWhenTryingToSaveAScheduleWithNoCodeSet() throws Exception {
    ProcessingSchedule processingSchedule = new ProcessingSchedule();
    doThrow(new DataException("Schedule can not be saved without its code.")).when(processingScheduleService).save(processingSchedule);
    ResponseEntity<OpenLmisResponse> response = processingScheduleController.create(processingSchedule, httpServletRequest);

    verify(processingScheduleService).save(processingSchedule);

    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(response.getBody().getErrorMsg(), is("Schedule can not be saved without its code."));
  }

  @Test
  public void shouldReturnErrorResponseWhenTryingToSaveAScheduleWithNoNameSet() {
    ProcessingSchedule processingSchedule = new ProcessingSchedule();
    processingSchedule.setCode("testCode");
    doThrow(new DataException("Schedule can not be saved without its name.")).when(processingScheduleService).save(processingSchedule);
    ResponseEntity<OpenLmisResponse> response = processingScheduleController.create(processingSchedule, httpServletRequest);

    verify(processingScheduleService).save(processingSchedule);
    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(response.getBody().getErrorMsg(), is("Schedule can not be saved without its name."));
  }



  @Test
  public void shouldUpdateAndReturnTheSchedule() {
    ProcessingSchedule processingSchedule = new ProcessingSchedule("testCode", "testName");
    ProcessingSchedule mockedSchedule = mock(ProcessingSchedule.class);
    when(processingScheduleService.save(processingSchedule)).thenReturn(mockedSchedule);

    when(mockedSchedule.getName()).thenReturn(scheduleName);

    ResponseEntity<OpenLmisResponse> response = processingScheduleController.update(processingSchedule, 1L, httpServletRequest);
    assertThat(processingSchedule.getModifiedBy(), is(userId));

    assertThat(response.getStatusCode(), is(HttpStatus.OK));
    assertThat(response.getBody().getSuccessMsg(), is("'" + scheduleName + "' updated successfully"));
    ProcessingSchedule savedSchedule = (ProcessingSchedule) response.getBody().getData().get(SCHEDULE);
    assertThat(savedSchedule, is(mockedSchedule));
  }

  @Test
  public void shouldReturnErrorResponseWhenTryingToUpdateAScheduleWithNoCodeSet() throws Exception {
    ProcessingSchedule processingSchedule = new ProcessingSchedule();
    processingSchedule.setId(1L);
    doThrow(new DataException("Schedule can not be saved without its code.")).when(processingScheduleService).save(processingSchedule);
    ResponseEntity<OpenLmisResponse> response = processingScheduleController.create(processingSchedule, httpServletRequest);

    verify(processingScheduleService).save(processingSchedule);

    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(response.getBody().getErrorMsg(), is("Schedule can not be saved without its code."));
  }

  @Test
  public void shouldReturnErrorResponseWhenTryingToUpdateAScheduleWithNoNameSet() {
    ProcessingSchedule processingSchedule = new ProcessingSchedule();
    processingSchedule.setId(1L);
    processingSchedule.setCode("testCode");
    doThrow(new DataException("Schedule can not be saved without its name.")).when(processingScheduleService).save(processingSchedule);
    ResponseEntity<OpenLmisResponse> response = processingScheduleController.create(processingSchedule, httpServletRequest);

    verify(processingScheduleService).save(processingSchedule);
    assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    assertThat(response.getBody().getErrorMsg(), is("Schedule can not be saved without its name."));
  }


}
