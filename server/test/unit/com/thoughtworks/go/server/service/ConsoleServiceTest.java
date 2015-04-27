/**
 * **********************GO-LICENSE-START*********************************
 * Copyright 2015 ThoughtWorks, Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */

package com.thoughtworks.go.server.service;

import com.thoughtworks.go.domain.ConsoleOut;
import com.thoughtworks.go.domain.JobIdentifier;
import com.thoughtworks.go.domain.LocatableEntity;
import com.thoughtworks.go.helper.JobIdentifierMother;
import com.thoughtworks.go.server.view.artifacts.ArtifactDirectoryChooser;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import static com.thoughtworks.go.util.ArtifactLogUtil.getConsoleOutputFolderAndFileName;
import static java.lang.System.getProperty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class ConsoleServiceTest {

    private ArtifactDirectoryChooser chooser;
    private ConsoleService service;

    @Before
    public void setUp() throws Exception {
        chooser = mock(ArtifactDirectoryChooser.class);
        service = new ConsoleService(chooser);
    }

    @Test
    public void shouldReturnConsoleUpdates() throws IOException {
        String separator = getProperty("line.separator");
        String output = "line1" + separator + "line2" + separator + "line3";
        ByteArrayInputStream stream = new ByteArrayInputStream(output.getBytes());

        ConsoleOut consoleOut = service.getConsoleOut(0, stream);
        assertThat(consoleOut.output(), is(output + separator));
        assertThat(consoleOut.calculateNextStart(), is(3));

        output += separator + "line4" + separator + "line5";

        stream = new ByteArrayInputStream(output.getBytes());
        consoleOut = service.getConsoleOut(3, stream);
        assertThat(consoleOut.output(), is("line4" + separator + "line5" + separator));
        assertThat(consoleOut.calculateNextStart(), is(5));
    }

    @Test
    public void shouldReturnTemporaryArtifactFileIfItExists() throws Exception {
        JobIdentifier jobIdentifier = JobIdentifierMother.anyBuildIdentifier();

        File consoleFile = mock(File.class);

        when(chooser.temporaryConsoleFile(jobIdentifier)).thenReturn(consoleFile);
        when(consoleFile.exists()).thenReturn(true);

        File file = service.consoleLogFile(jobIdentifier);

        assertThat(file, is(consoleFile));
        verify(chooser).temporaryConsoleFile(jobIdentifier);
        verify(chooser, never()).findArtifact(any(LocatableEntity.class), anyString());
    }

    @Test
    public void shouldReturnFinalArtifactFileIfItExists() throws Exception {
        JobIdentifier jobIdentifier = JobIdentifierMother.anyBuildIdentifier();

        File consoleFile = mock(File.class);

        when(chooser.temporaryConsoleFile(jobIdentifier)).thenReturn(consoleFile);
        when(consoleFile.exists()).thenReturn(false);

        File finalConsoleFile = mock(File.class);

        when(chooser.findArtifact(jobIdentifier, getConsoleOutputFolderAndFileName())).thenReturn(finalConsoleFile);
        when(finalConsoleFile.exists()).thenReturn(true);

        File file = service.consoleLogFile(jobIdentifier);

        assertThat(file, is(finalConsoleFile));

        verify(chooser).temporaryConsoleFile(jobIdentifier);
        verify(chooser).findArtifact(jobIdentifier, getConsoleOutputFolderAndFileName());
    }

    @Test
    public void shouldReturnTemporaryFileIfBothTemporaryAndFinalFilesDoNotExist() throws Exception {
        JobIdentifier jobIdentifier = JobIdentifierMother.anyBuildIdentifier();

        File consoleFile = mock(File.class);

        when(chooser.temporaryConsoleFile(jobIdentifier)).thenReturn(consoleFile);
        when(consoleFile.exists()).thenReturn(false);

        File finalConsoleFile = mock(File.class);

        when(chooser.findArtifact(jobIdentifier, getConsoleOutputFolderAndFileName())).thenReturn(finalConsoleFile);
        when(finalConsoleFile.exists()).thenReturn(false);

        File file = service.consoleLogFile(jobIdentifier);

        assertThat(file, is(consoleFile));

        verify(chooser).temporaryConsoleFile(jobIdentifier);
        verify(chooser).findArtifact(jobIdentifier, getConsoleOutputFolderAndFileName());
    }

}