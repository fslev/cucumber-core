package com.cucumber.utils.context.stepdefs.sql;

import com.cucumber.utils.context.ScenarioUtils;
import com.cucumber.utils.context.props.ScenarioProps;
import io.jtest.utils.clients.database.SqlClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doNothing;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*", "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.dom.*", "com.sun.org.apache.xalan.*"})
public class SqlStepsTest {

    @Mock
    private ResultSet rs;
    @Mock
    private PreparedStatement pst;
    @Mock
    private Connection connection;
    @Mock
    private ResultSetMetaData rsMetaData;
    @Spy
    private final SqlClient sqlClient = new SqlClient("", "", "", "com.mysql.cj.jdbc.Driver");

    @Mock
    private ScenarioUtils scenarioUtils;
    @Spy
    private final ScenarioProps scenarioProps = new ScenarioProps();

    private final SqlSteps sqlSteps = new SqlSteps();


    @Before
    public void mockSqlClient() throws Exception {
        Whitebox.setInternalState(sqlClient, connection);
        doNothing().when(sqlClient).connect();
        doNothing().when(sqlClient).close();
        when(connection.prepareStatement(anyString())).thenReturn(pst);
        when(pst.executeQuery()).thenReturn(rs);
        when(rs.getMetaData()).thenReturn(rsMetaData);

        when(rsMetaData.getColumnCount()).thenReturn(3);
        when(rsMetaData.getColumnLabel(1)).thenReturn("first_name");
        when(rsMetaData.getColumnLabel(2)).thenReturn("last_name");
        when(rsMetaData.getColumnLabel(3)).thenReturn("address");
        when(rs.next()).thenReturn(true, true, true, false);
        when(rs.getObject(1)).thenReturn("David", "Andrew", "Lara");
        when(rs.getObject(2)).thenReturn("Jones", "Sputnik", "Croft");
        when(rs.getObject(3)).thenReturn("Hamilton 16", "Liberty 1", "Liberty 2");
    }

    @Before
    public void injectMocksIntoSqlSteps() {
        Whitebox.setInternalState(sqlSteps, scenarioProps);
        Whitebox.setInternalState(sqlSteps, scenarioUtils);
        Whitebox.setInternalState(sqlSteps, sqlClient);
    }

    @Test
    public void testExecuteAndMatchWithObject() throws SQLException {
        List<Map<String, Object>> expectedResult = new ArrayList<>();
        expectedResult.add(Map.of("first_name", "David", "last_name", "Jones", "address", "Hamilton 16"));
        expectedResult.add(Map.of("first_name", "~[firstName2]", "last_name", ".*", "address", "Liberty 1"));
        expectedResult.add(Map.of("first_name", "Lara", "last_name", "Croft", "address", "~[address3]"));
        sqlSteps.executeQueryAndMatch("does not matter", expectedResult);
        assertEquals(scenarioProps.getAsString("address3"), "Liberty 2");
        assertEquals(scenarioProps.getAsString("firstName2"), "Andrew");
    }

    @Test
    public void testExecuteAndMatchWithJson() throws SQLException {
        List<Map<String, Object>> expectedResult = new ArrayList<>();
        expectedResult.add(Map.of("first_name", "David", "last_name", "Jones", "address", "Hamilton 16"));
        expectedResult.add(Map.of("first_name", "~[firstName2]", "last_name", ".*", "address", "Liberty 1"));
        expectedResult.add(Map.of("first_name", "Lara", "last_name", "Croft", "address", "~[address3]"));
        sqlSteps.executeQueryAndMatchWithJson("does not matter", expectedResult);
        assertEquals(scenarioProps.getAsString("address3"), "Liberty 2");
        assertEquals(scenarioProps.getAsString("firstName2"), "Andrew");
    }

    @Test
    public void testExecuteAndMatchWithTable() throws SQLException {
        List<Map<String, Object>> expectedResult = new ArrayList<>();
        expectedResult.add(Map.of("first_name", "David", "last_name", "Jones", "address", "Hamilton 16"));
        expectedResult.add(Map.of("first_name", "~[firstName2]", "last_name", ".*", "address", "Liberty 1"));
        expectedResult.add(Map.of("first_name", "Lara", "last_name", "Croft", "address", "~[address3]"));
        sqlSteps.executeQueryAndMatchWithTable("does not matter", expectedResult);
        assertEquals(scenarioProps.getAsString("address3"), "Liberty 2");
        assertEquals(scenarioProps.getAsString("firstName2"), "Andrew");
    }

    @Test
    public void testExecutePollAndMatch() throws SQLException {
        List<Map<String, Object>> expectedResult = new ArrayList<>();
        expectedResult.add(Map.of("first_name", "David", "last_name", "Jones", "address", "Hamilton 16"));
        expectedResult.add(Map.of("first_name", "~[firstName2]", "last_name", ".*", "address", "Liberty 1"));
        expectedResult.add(Map.of("first_name", "Lara", "last_name", "Croft", "address", "~[address3]"));
        sqlSteps.executeQueryAndPollAndMatch("does not matter", 10, expectedResult);
        assertEquals(scenarioProps.getAsString("address3"), "Liberty 2");
        assertEquals(scenarioProps.getAsString("firstName2"), "Andrew");
    }
}
