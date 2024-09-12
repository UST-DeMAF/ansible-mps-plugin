# Open TODOs in the Ansible MPS Plugin

### 1. Enhance Variable replacement  
Vars can be used by vars in outer scopes, this is not covered yet in variable replacement.

### 2. Finalize the implementation of the ansible metamodel
Fully implement the metamodels and all keywords for Ansible in all stages meaning: Update the models and update the
transformation rules that every key word is respected. The current state is represented in the following tables.   

Please note that missing things in the metamodel are _also missing in the different parsing and transformation steps_.
The three following steps all need adjustments for the given type transformations once their models have been implemented: 
1. Ansible to Java Parsing
2. MPS XML Read-In 
3. MPS Model-To-Model-Transformation


| Linguistic Type in Ansible                   | Metamodel |                         Java Class                         |         MPS Class |   
|----------------------------------------------|:----------|:----------------------------------------------------------:|------------------:|
| Play                                         | complete  | incomplete (several properties from the metamodel missing) |          complete |
| Role                                         | complete  | incomplete (several properties from the metamodel missing) |          complete |
| Host                                         | complete  | incomplete (several properties from the metamodel missing) |          complete |
| Task                                         | complete  | incomplete (several properties from the metamodel missing) |          complete |
| Handler                                      | complete  |                          missing                           |           missing |
| Variable                                     | complete  |                          complete                          |          complete |
| Module                                       | complete  |                          complete                          |          complete |
| AnsibleComponent</br>(abstract helper class) | complete  |                          missing                           |          complete |
| NonPlayComponent</br>(abstract helper class) | complete  |                          missing                           |          complete |
| File                                         | complete  |                     complete (unused)                      | complete (unused) |
| Template                                     | complete  |                          missing                           |           missing |


| Implemented Ontological Types in Ansible | Metamodel                                                                                                                  |                         Java Class                         |                                                 MPS Class |   
|------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------|:----------------------------------------------------------:|----------------------------------------------------------:|
| Apt                                      | missing [official docs](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/apt_module.html)               | incomplete (several properties from the metamodel missing) | incomplete (several properties from the metamodel missing |
| DockerContainer                          | missing [official docs](https://docs.ansible.com/ansible/latest/collections/community/docker/docker_container_module.html) | incomplete (several properties from the metamodel missing) | incomplete (several properties from the metamodel missing |
| DockerImage                              | missing [official docs](https://docs.ansible.com/ansible/latest/collections/community/docker/docker_image_module.html)     | incomplete (several properties from the metamodel missing  | incomplete (several properties from the metamodel missing |
| DockerNetwork                            | missing [official docs](https://docs.ansible.com/ansible/latest/collections/community/docker/docker_network_module.html)   | incomplete (several properties from the metamodel missing  | incomplete (several properties from the metamodel missing |
| LaunchD                                  | missing [official docs](https://docs.ansible.com/ansible/latest/collections/community/general/launchd_module.html)         | incomplete (several properties from the metamodel missing  | incomplete (several properties from the metamodel missing |


### 3. Future work
Implement more ontological types overall to cover more Ansible projects.