# ansible-mps-plugin
The ansible-mps-plugin is one of many plugins of the [DeMAF](https://github.com/UST-DeMAF) project.
It is designed to transform [Ansible deployment models](https://docs.ansible.com) into an [EDMM](https://github.com/UST-EDMM) representation.
The transformation rules are described in the [TRANSFORMATION_RULES.md](TRANSFORMATION_RULES.md).

The plugin only works (without adaptions) in the context of the entire DeMAF application using the [deployment-config](https://github.com/UST-DeMAF/deployment-config).
The documentation for setting up the entire DeMAF application locally is [here](https://github.com/UST-DeMAF/EnPro-Documentation).

## Build and Run Application

You can run the application without the [deployment-config](https://github.com/UST-DeMAF/deployment-config), but it will not run as it needs to register itself at the [analysis-manager](https://github.com/UST-DeMAF/analysis-manager).

If you want to boot it locally nevertheless use the following commands.

```shell
./mvnw spring-boot:run
```
or to use the built package:
```shell
./mvnw package
java -jar target/ansible-plugin-0.0.1-SNAPSHOT.jar
```

## Init and Update Submodule
This plugin uses [JetBrains MPS](https://www.jetbrains.com/mps/) to facilitate the model-to-model transformation from Ansible to EDMM.
The [matching MPS project](https://github.com/UST-DeMAF/mps-transformation-ansible) is located in another git repository and must be added as a submodule (you can also clone via https):

```shell
git submodule add git@github.com:UST-DeMAF/mps-transformation-ansible.git mps-transformation-ansible
```

To update the MPS application to a new version, execute:
```shell
git submodule update --remote
```

## Ansible-Specific Configurations
This plugin has some minor specialities compared to other DeMAF transformation plugins:
1. No directories: Ansible projects are created in a folder structure, where it is possible to have multiple overlapping main files.
Therefore, it is not possible to scan directories, but always only one main file.
Of course, matching subdirectories, e.g. for roles are possible to read.
2. Host-file: If available one host.yaml can be read, but it must be in the same folder as the main.yaml and be named host.y(a)ml.
3. Variable replacement: Ansible uses variables to create re-usable templates.
Those variables are replaced with their values after parsing the files by the ansible-mps-plugin before handing it over to MPS.

## Debugging
When running the project locally using e.g. IntelliJ IDEA or from the command-line, make sure that the plugin is not also running
in a Docker container, launched by the deployment-config, otherwise the port is blocked.
