--
-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements.  See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership.  The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License.  You may obtain a copy of the License at
--
--   http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied.  See the License for the
-- specific language governing permissions and limitations
-- under the License.
--

--
-- Table of arbitrary connection history attributes. Each attribute is simply a
-- name/value pair associated with a connection history record. Arbitrary
-- attributes can be defined by either this extension or by other extensions
-- that decorate this one, delegating storage of the attributes to this
-- extension.
--

CREATE TABLE [guacamole_connection_history_attribute] (

    [history_id]      [int]            NOT NULL,
    [attribute_name]  [nvarchar](128)  NOT NULL,
    [attribute_value] [nvarchar](4000) NOT NULL,

    CONSTRAINT [PK_guacamole_connection_history_attribute]
        PRIMARY KEY CLUSTERED ([history_id], [attribute_name]),

    CONSTRAINT [FK_guacamole_connection_history_attribute_history_id]
        FOREIGN KEY ([history_id])
        REFERENCES [guacamole_connection_history] ([history_id])
        ON DELETE CASCADE

);

GO


--
-- Table of arbitrary user history attributes. Each attribute is simply a
-- name/value pair associated with a user history record. Arbitrary attributes
-- can be defined by either this extension or by other extensions that
-- decorate this one, delegating storage of the attributes to this extension.
--

CREATE TABLE [guacamole_user_history_attribute] (

    [history_id]      [int]            NOT NULL,
    [attribute_name]  [nvarchar](128)  NOT NULL,
    [attribute_value] [nvarchar](4000) NOT NULL,

    CONSTRAINT [PK_guacamole_user_history_attribute]
        PRIMARY KEY CLUSTERED ([history_id], [attribute_name]),

    CONSTRAINT [FK_guacamole_user_history_attribute_history_id]
        FOREIGN KEY ([history_id])
        REFERENCES [guacamole_user_history] ([history_id])
        ON DELETE CASCADE

);

GO