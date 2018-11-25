<el:dataTable id="employeeStatusHistoryTable"
              searchFormName="employeeStatusHistorySearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="employeeStatusHistory"
              spaceBefore="true" hasRow="true"
              action="filter"
              serviceName="employeeStatusHistory"
              domainColumns="${domainColumns}">


    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'personLiveStatus',action: 'show')}"
                            actionParams="encodedId"  functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show employeeStatusHistory')}"/>
        <sec:ifAnyGranted roles="${ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_SUPER_ADMIN.value}">
            <el:dataTableAction accessUrl="${createLink(controller: 'personLiveStatus',action: 'show')}"
                                actionParams="encodedId"  functionName="renderInLineEdit" type="function" class="blue icon-pencil"
                                message="${message(code: 'default.edit.label', args: [entity],
                                        default: 'edit employeeStatusHistory')}"/>
        </sec:ifAnyGranted>
    </g:if>
    <g:else>


    <el:dataTableAction controller="employeeStatusHistory" action="show"
                        actionParams="encodedId" class="green icon-eye"
                        message="${message(code:'default.show.label',args:[entity],
                                default:'show employeeStatusHistory')}" />


        <sec:ifAnyGranted roles="${ps.gov.epsilon.hr.enums.v1.EnumApplicationRole.ROLE_SUPER_ADMIN.value}">

            <el:dataTableAction controller="employeeStatusHistory" action="edit"
                                actionParams="encodedId" class="blue icon-pencil"
                                message="${message(code:'default.edit.label',
                                        args:[entity],default:'edit employeeStatusHistory')}" />
        </sec:ifAnyGranted>


    </g:else>

</el:dataTable>


<script>

</script>