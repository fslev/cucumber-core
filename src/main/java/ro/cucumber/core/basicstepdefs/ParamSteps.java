package ro.cucumber.core.basicstepdefs;

import cucumber.api.java.en.Given;
import cucumber.runtime.java.guice.ScenarioScoped;
import ro.cucumber.core.context.config.CustomDataTable;
import ro.cucumber.core.context.props.ScenarioProps;
import com.google.inject.Inject;

@ScenarioScoped
public class ParamSteps {

    @Inject
    ScenarioProps props;

    @Given("param {cstring}=")
    public void setParamDocString(String name, String value) {
        props.put(name, value);
    }

    @Given("param {cstring}={cstring}")
    public void setParamString(String name, String value) {
        props.put(name, value);
    }

    @Given("table {cstring}=")
    public void setDatatable(String paramName, CustomDataTable value) {
        props.put(paramName, value);
    }
}
