package ust.tad.ansiblempsplugin.analysis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SerializeXMLTest {

  @Value("${mps.inputModel.path}")
  private String mpsInputPath;

  // TODO FIX TESTS
  /*
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
    Argument argument = new Argument("key", "val");
    Argument argument2 = new Argument("key2", "val2");
    Argument argument3 = new Argument("key3", "val3");
    Argument argumentFromVariable = new Argument("keyVar", "var.key");
    Block block = new Block("newBlockType", Set.of(argument2, argument3));

    Resource resource =
        new Resource("newResourceType", "newResource", Set.of(argument), Set.of(block));
    Resource resource2 =
        new Resource("newResource2Type", "newResource2", Set.of(argumentFromVariable), Set.of());

    Variable variable = new Variable("var.key", "variableValue");

    return new AnsibleDeploymentModel(Set.of(resource, resource2), Set.of(variable));
  }*/
}
