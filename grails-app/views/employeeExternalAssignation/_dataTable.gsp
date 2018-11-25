

    <el:dataTable id="employeeExternalAssignationTable" searchFormName="employeeExternalAssignationSearchForm"
                  dataTableTitle="${title}"
                  hasCheckbox="true" widthClass="col-sm-12" controller="employeeExternalAssignation"
                  spaceBefore="true" hasRow="true" action="filter"
                  serviceName="employeeExternalAssignation"
              domainColumns="${domainColumns}">


        <g:if test="${isInLineActions}">

            <el:dataTableAction accessUrl="${createLink(controller: 'employeeExternalAssignation',
                    action: 'show')}" functionName="renderInLineShow" type="function"
                                actionParams="encodedId"  class="green icon-eye"
                                message="${message(code: 'default.show.label', args: [entity],
                                        default: 'show employeeExternalAssignation')}" />


            <el:dataTableAction  showFunction="showData" accessUrl="${createLink(controller: 'employeeExternalAssignation',
                    action: 'edit')}" functionName="renderInLineEdit" type="function"
                                 actionParams="encodedId"  class="blue icon-pencil"
                                message="${message(code: 'default.edit.label',
                                        args: [entity], default: 'edit employeeExternalAssignation')}"/>

        </g:if>
        <g:else>
            <el:dataTableAction controller="employeeExternalAssignation" action="show"
                                actionParams="encodedId" class="green icon-eye"
                                message="${message(code:'default.show.label',args:[entity],
                                        default:'show employeeExternalAssignation')}" />


            <el:dataTableAction  showFunction="showData" controller="employeeExternalAssignation" action="edit"
                                actionParams="encodedId" class="blue icon-pencil"
                                message="${message(code:'default.edit.label',
                                        args:[entity],default:'edit employeeExternalAssignation')}" />
        </g:else>

        <el:dataTableAction  showFunction="showData" controller="employeeExternalAssignation" action="delete"
                            actionParams="encodedId" class="red icon-trash"
                            type="confirm-delete" message="${message(code:'default.delete.label',
                args:[entity],default:'delete employeeExternalAssignation')}" />




    </el:dataTable>

    <script type="text/javascript">
        function showData(row) {
            return row.canEdit
        }
    </script>