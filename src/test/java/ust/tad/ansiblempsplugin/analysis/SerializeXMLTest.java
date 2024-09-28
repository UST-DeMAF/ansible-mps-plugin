package ust.tad.ansiblempsplugin.analysis;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import ust.tad.ansiblempsplugin.ansiblemodel.*;
import ust.tad.ansiblempsplugin.ansiblemodel.actions.DockerImage;

@SpringBootTest
public class SerializeXMLTest {

  @Value("${mps.inputModel.path}")
  private String mpsInputPath;

  @Test
  public void serializeAnsibleToXML() throws JsonProcessingException {
    AnsibleDeploymentModel modelToSerialize = createDummyModel();

    XmlMapper xmlMapper = new XmlMapper();
    String xml = xmlMapper.writeValueAsString(modelToSerialize);
    assertNotNull(xml);
    System.out.print(xml);
  }

  @Test
  public void serializeAnsibleToXMLFile() throws IOException {
    AnsibleDeploymentModel modelToSerialize = createDummyModel();

    XmlMapper xmlMapper = new XmlMapper();
    xmlMapper.writeValue(new File("dummyAnsibleDM.xml"), modelToSerialize);
    File file = new File("dummyAnsibleDM.xml");
    assertNotNull(file);
  }

  private AnsibleDeploymentModel createDummyModel() {

    HashSet<Variable> vars = new HashSet<>();
    vars.add(new Variable("test-key", "test-value"));

    HashSet<Host> hosts = new HashSet<>();
    hosts.add(new Host("test-host", vars, "test-group"));

    HashSet<Task> tasks = new HashSet<>();
    tasks.add(
        new Task(
            "test-task",
            vars,
            false,
            new DockerImage("test-name", "test-source"),
            new HashSet<>()));

    HashSet<Role> roles = new HashSet<>();
    roles.add(new Role("test-role", tasks, tasks, vars, vars, new HashSet<>(), new HashSet<>()));

    Play play = new Play("test-play", hosts, tasks, tasks, tasks, roles, vars, true);
    return new AnsibleDeploymentModel(Set.of(play));
  }
}
