/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.connector.mysql;

import io.debezium.pipeline.ErrorHandler;
import io.debezium.pipeline.EventDispatcher;
import io.debezium.pipeline.source.spi.ChangeEventSourceFactory;
import io.debezium.pipeline.source.spi.SnapshotChangeEventSource;
import io.debezium.pipeline.source.spi.SnapshotProgressListener;
import io.debezium.pipeline.source.spi.StreamingChangeEventSource;
import io.debezium.pipeline.spi.OffsetContext;
import io.debezium.relational.TableId;
import io.debezium.util.Clock;

public class MySqlChangeEventSourceFactory implements ChangeEventSourceFactory {

    private final MySqlConnectorConfig configuration;
    private final MySqlConnection connection;
    private final ErrorHandler errorHandler;
    private final EventDispatcher<TableId> dispatcher;
    private final Clock clock;
    private final MySqlTaskContext taskContext;
    private final MySqlStreamingChangeEventSourceMetrics streamingMetrics;

    public MySqlChangeEventSourceFactory(MySqlConnectorConfig configuration, MySqlConnection connection,
                                         ErrorHandler errorHandler, EventDispatcher<TableId> dispatcher, Clock clock, MySqlDatabaseSchema schema,
                                         MySqlTaskContext taskContext, MySqlStreamingChangeEventSourceMetrics streamingMetrics) {
        this.configuration = configuration;
        this.connection = connection;
        this.errorHandler = errorHandler;
        this.dispatcher = dispatcher;
        this.clock = clock;
        this.taskContext = taskContext;
        this.streamingMetrics = streamingMetrics;
    }

    @Override
    public SnapshotChangeEventSource getSnapshotChangeEventSource(OffsetContext offsetContext, SnapshotProgressListener snapshotProgressListener) {
        return new MySqlSnapshotChangeEventSource(configuration, (MySqlOffsetContext) offsetContext, connection, taskContext.getSchema(), dispatcher, clock,
                (MySqlSnapshotChangeEventSourceMetrics) snapshotProgressListener);
    }

    @Override
    public StreamingChangeEventSource getStreamingChangeEventSource(OffsetContext offsetContext) {
        return new MySqlStreamingChangeEventSource(
                configuration,
                (MySqlOffsetContext) offsetContext,
                connection,
                dispatcher,
                errorHandler,
                clock,
                taskContext,
                streamingMetrics);
    }
}
