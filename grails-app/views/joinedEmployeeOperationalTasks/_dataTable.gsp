<el:dataTable id="joinedEmployeeOperationalTasksTable" searchFormName="joinedEmployeeOperationalTasksSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12"
              controller="joinedEmployeeOperationalTasks" spaceBefore="true" hasRow="true"
              action="filter" serviceName="joinedEmployeeOperationalTasks" domainColumns="${domainColumns}" >



    <g:if test="${isInLineActions}">

        <el:dataTableAction accessUrl="${createLink(controller: 'joinedEmployeeOperationalTasks',
                action: 'show')}" functionName="renderInLineShow" type="function"
                            actionParams="['encodedId']" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show joinedEmployeeOperationalTasks')}" />


        <el:dataTableAction accessUrl="${createLink(controller: 'joinedEmployeeOperationalTasks',
                action: 'edit')}" functionName="renderInLineEdit" type="function"
                            actionParams="['encodedId']" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label',
                                    args: [entity], default: 'edit joinedEmployeeOperationalTasks')}"/>

    </g:if>
    <g:else>
    <el:dataTableAction controller="joinedEmployeeOperationalTasks" action="show"
                        actionParams="encodedId" class="green icon-eye"
                        message="${message(code:'default.show.label',args:[entity],
                                default:'show joinedEmployeeOperationalTasks')}" />


    <el:dataTableAction controller="joinedEmployeeOperationalTasks" action="edit"
                        actionParams="encodedId" class="blue icon-pencil"
                        message="${message(code:'default.edit.label',
                                args:[entity],default:'edit joinedEmployeeOperationalTasks')}" />
    </g:else>

    <el:dataTableAction controller="joinedEmployeeOperationalTasks" action="delete"
                        actionParams="encodedId" class="red icon-trash"
                        type="confirm-delete" message="${message(code:'default.delete.label',
            args:[entity],default:'delete joinedEmployeeOperationalTasks')}" />




</el:dataTable>