<el:dataTable id="maritalStatusRequestTable"
              searchFormName="maritalStatusRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="maritalStatusRequest"
              spaceBefore="true"
              hasRow="true"
              action="filter"
              serviceName="maritalStatusRequest"
              domainColumns="${domainColumns}">

    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'maritalStatusRequest', action: 'showThread')}"
                            actionParams="threadId" functionName="renderInLineShowThread" type="function" class="green icon-list"
                            message="${message(code: 'request.showThread.label', default: 'show maritalStatusRequest Thread')}"/>
    </g:if>

    <g:else>
        <el:dataTableAction controller="maritalStatusRequest" action="show"
                            actionParams="encodedId" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show maritalStatusRequest')}"/>

        <el:dataTableAction controller="maritalStatusRequest" action="edit"
                            actionParams="encodedId" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label',
                                    args: [entity], default: 'edit maritalStatusRequest')}"/>

        <el:dataTableAction controller="maritalStatusRequest" action="delete"
                            actionParams="encodedId" class="red icon-trash" type="confirm-delete"
                            message="${message(code: 'default.delete.label', args: [entity],
                                    default: 'delete maritalStatusRequest')}"/>
    </g:else>
</el:dataTable>