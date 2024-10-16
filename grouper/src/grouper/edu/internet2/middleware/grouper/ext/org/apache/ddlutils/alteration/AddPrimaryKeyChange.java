package edu.internet2.middleware.grouper.ext.org.apache.ddlutils.alteration;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import edu.internet2.middleware.grouper.ext.org.apache.ddlutils.model.Column;
import edu.internet2.middleware.grouper.ext.org.apache.ddlutils.model.Database;
import edu.internet2.middleware.grouper.ext.org.apache.ddlutils.model.Table;

/**
 * Represents the addition of a primary key to a table which does not have one.
 * 
 * @version $Revision: $
 */
public class AddPrimaryKeyChange extends TableChangeImplBase
{
    /** The columns making up the primary key. */
    private Column[] _primaryKeyColumns;

    /**
     * Creates a new change object.
     * 
     * @param table             The table to add the primary key to
     * @param primaryKeyColumns The columns making up the primary key
     */
    public AddPrimaryKeyChange(Table table, Column[] primaryKeyColumns)
    {
        super(table);
        _primaryKeyColumns = primaryKeyColumns;
    }

    /**
     * Returns the primary key columns making up the new primary key.
     *
     * @return The primary key columns
     */
    public Column[] getPrimaryKeyColumns()
    {
        return _primaryKeyColumns;
    }

    /**
     * {@inheritDoc}
     */
    public void apply(Database database, boolean caseSensitive)
    {
        Table table = database.findTable(getChangedTable().getName(), caseSensitive);

        for (int idx = 0; idx < _primaryKeyColumns.length; idx++)
        {
            Column column = table.findColumn(_primaryKeyColumns[idx].getName(), caseSensitive);

            column.setPrimaryKey(true);
        }
    }
}
