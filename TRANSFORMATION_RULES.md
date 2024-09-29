# Transformation Rules for the Ansible MPS Plugin


The transformations take place in the submodule [mps-transformation-ansible](https://github.com/UST-DeMAF/mps-transformation-ansible) using JetBrains MPS.

| Ansible Type                                      | EDMM TYPE                                                                                                       |
|---------------------------------------------------|:----------------------------------------------------------------------------------------------------------------|
| Play                                              | -                                                                                                               |
| Role                                              | Component                                                                                                       |
| Host                                              | Component                                                                                                       |
| Task                                              | Operation <br/> (if play-wide task, attached to all host-components, if role-wide, attached to role-component)  |
| Handler                                           | _TODO_ (should behave the same way as Task as it is the same type)                                              |
| Variable                                          | Property (attached to the given context)                                                                        |
| Module (i.e. concrete subtypes)                   | Artifact (as part of the operation of the component transformed from the task where the module was attached to) |
| File                                              | Artifact (as part of the component transformed from the role)                                                   |
| Template                                          | _TODO_                                                                                                          |
|                                                   |                                                                                                                 |
| Dependency (between two roles, denoted as `meta`) | Connects-To-Relation (between both role-components)                                                             |
| Roles and Hosts in the same Play                  | Hosted-On-Relation (between a role-component and all host-components)                                           |

