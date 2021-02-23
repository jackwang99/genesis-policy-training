/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.eisgroup.genesis.infra.reports.input;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * DTO for reading from activity aggregate cassandra table
 *
 * @author mguzelis
 */
public class ActivityAggregateRow implements Serializable {
    private static final long serialVersionUID = -2617700027583512947L;

    @SuppressWarnings("squid:S2386") // GENESIS-14408
    public static final String[] COLUMN_NAMES = new String[]{
            "timestamp", "id", "entityId", "group", "messageId", "entityNumber",
            "userKey", "status", "startedTime", "endedTime"
    };

    private Timestamp timestamp;
    private String id;
    private String entityId;
    private String group;
    private String messageId;
    private String entityNumber;
    private String userKey;
    private String status;
    private Timestamp startedTime;
    private Timestamp endedTime;

    public ActivityAggregateRow() {
    }

    @SuppressWarnings("squid:S00107")
    public ActivityAggregateRow(Timestamp timestamp, String id, String entityId, String group, String messageId,
                                String entityNumber, String userKey, String status, Timestamp startedTime, Timestamp endedTime) {
        this.timestamp = timestamp;
        this.id = id;
        this.entityId = entityId;
        this.group = group;
        this.messageId = messageId;
        this.entityNumber = entityNumber;
        this.userKey = userKey;
        this.status = status;
        this.startedTime = (startedTime != null) ? startedTime : endedTime;
        this.endedTime = (endedTime != null) ? endedTime : startedTime;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getEntityNumber() {
        return entityNumber;
    }

    public void setEntityNumber(String entityNumber) {
        this.entityNumber = entityNumber;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(Timestamp startedTime) {
        this.startedTime = startedTime;
    }

    public Timestamp getEndedTime() {
        return endedTime;
    }

    public void setEndedTime(Timestamp endedTime) {
        this.endedTime = endedTime;
    }
}
