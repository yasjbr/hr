<el:dataTable id="employmentServiceRequestTable"
              searchFormName="employmentServiceRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="employmentServiceRequest"
              spaceBefore="true"
              hasRow="true"
              action="filter"
              serviceName="employmentServiceRequest"
              domainColumns="${domainColumns}">

    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'employmentServiceRequest',action: 'show')}"
                            actionParams="encodedId"  functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show employmentServiceRequest')}"/>
    </g:if>
    <g:else>
    <el:dataTableAction controller="employmentServiceRequest" action="show"
                        actionParams="encodedId" class="green icon-eye"
                        message="${message(code:'default.show.label',args:[entity],
                                default:'show employmentServiceRequest')}" />

    <el:dataTableAction controller="employmentServiceRequest" action="edit"
                        actionParams="encodedId" class="blue icon-pencil"
                        message="${message(code:'default.edit.label',
                                args:[entity],default:'edit employmentServiceRequest')}" />

    <el:dataTableAction controller="employmentServiceRequest" action="delete"
                        actionParams="encodedId" class="red icon-trash" type="confirm-delete"
                        message="${message(code:'default.delete.label',args:[entity],
                                default:'delete employmentServiceRequest')}" />
    </g:else>

</el:dataTable>