/**
 * Copyright 2014 www.codereligion.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codereligion.bugsnag.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.codereligion.bugsnag.logback.model.EventVO;
import com.codereligion.bugsnag.logback.model.MetaDataVO;
import com.codereligion.bugsnag.logback.model.NotificationVO;
import com.codereligion.bugsnag.logback.model.TabVO;
import org.junit.Test;
import static com.codereligion.bugsnag.logback.matcher.TabKeyValueMatcher.hasKeyValuePair;
import static com.codereligion.bugsnag.logback.mock.logging.MockLoggingEvent.createLoggingEvent;
import static com.codereligion.bugsnag.logback.mock.logging.MockThrowableProxy.createThrowableProxy;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConverterTest {

    private Configuration configuration = new Configuration();
    private Converter converter = new Converter(configuration);

    @Test
    public void addsApiKeyFromConfigurationToNotification() {

        // given
        configuration.setApiKey("some api key");
        final ILoggingEvent loggingEvent = createLoggingEvent().with(createThrowableProxy());

        // when
        final NotificationVO notification = converter.convertToNotification(loggingEvent);

        // then
        assertThat(notification.getApiKey(), is("some api key"));
    }

    @Test
    public void addsReleaseStageFromConfigurationToEvent() {

        // given
        configuration.setReleaseStage("someReleaseStage");
        final ILoggingEvent loggingEvent = createLoggingEvent().with(createThrowableProxy());

        // when
        final NotificationVO notification = converter.convertToNotification(loggingEvent);
        final EventVO event = notification.getEvents().get(0);

        // then
        assertThat(event.getReleaseStage(), is("someReleaseStage"));
    }

    @Test
    public void addsUserId() {

        // given
        final ILoggingEvent loggingEvent = createLoggingEvent()
                .with(createThrowableProxy())
                .withMdcProperty("userId", "someId");

        // when
        final NotificationVO notification = converter.convertToNotification(loggingEvent);
        final EventVO event = notification.getEvents().get(0);

        // then
        assertThat(event.getUserId(), is("someId"));
    }

    @Test
    public void addsAppVersion() {

        // given
        final ILoggingEvent loggingEvent = createLoggingEvent()
                .with(createThrowableProxy())
                .withMdcProperty("appVersion", "someAppVersion");

        // when
        final NotificationVO notification = converter.convertToNotification(loggingEvent);
        final EventVO event = notification.getEvents().get(0);

        // then
        assertThat(event.getAppVersion(), is("someAppVersion"));
    }

    @Test
    public void addsOsVersion() {

        // given
        final ILoggingEvent loggingEvent = createLoggingEvent()
                .with(createThrowableProxy())
                .withMdcProperty("osVersion", "someOsVersion");

        // when
        final NotificationVO notification = converter.convertToNotification(loggingEvent);
        final EventVO event = notification.getEvents().get(0);

        // then
        assertThat(event.getOsVersion(), is("someOsVersion"));
    }

    @Test
    public void addsContext() {

        // given
        final ILoggingEvent loggingEvent = createLoggingEvent()
                .with(createThrowableProxy())
                .withMdcProperty("context", "someContext");

        // when
        final NotificationVO notification = converter.convertToNotification(loggingEvent);
        final EventVO event = notification.getEvents().get(0);

        // then
        assertThat(event.getContext(), is("someContext"));
    }

    @Test
    public void addsGroupingHash() {

        // given
        final ILoggingEvent loggingEvent = createLoggingEvent()
                .with(createThrowableProxy())
                .withMdcProperty("groupingHash", "someGroupingHash");

        // when
        final NotificationVO notification = converter.convertToNotification(loggingEvent);
        final EventVO event = notification.getEvents().get(0);


        // then
        assertThat(event.getGroupingHash(), is("someGroupingHash"));
    }

    @Test
    public void addsDataFromMetaDataProvider() {

        // given
        configuration.setMetaDataProviderClassName(TestMetaDataProvider.class.getName());
        final ILoggingEvent loggingEvent = createLoggingEvent()
                .with(createThrowableProxy())
                .withMdcProperty("someProp", "someValue");

        // when
        final NotificationVO notification = converter.convertToNotification(loggingEvent);
        final EventVO event = notification.getEvents().get(0);

        // then
        final TabVO tab = event.getMetaData().getTabsByName().get("someTab");
        assertThat(tab, hasKeyValuePair("someProp", "someValue"));
    }

    public static class TestMetaDataProvider implements MetaDataProvider {

        @Override
        public MetaDataVO provide(ILoggingEvent loggingEvent) {
            return new MetaDataVO().addToTab("someTab", "someProp", loggingEvent.getMDCPropertyMap().get("someProp"));
        }
    }
}
