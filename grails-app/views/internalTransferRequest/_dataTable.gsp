<el:dataTable id="internalTransferRequestTable" 
              searchFormName="internalTransferRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" 
              widthClass="col-sm-12" 
              controller="internalTransferRequest" 
              spaceBefore="true"
              hasRow="true" 
              action="filter" 
              serviceName="internalTransferRequest" 
              domainColumns="${domainColumns}">

    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'internalTransferRequest', action: 'show')}"
                            actionParams="encodedId"  functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show internalTransferRequest')}"/>
    </g:if>

    <g:else>
        <el:dataTableAction controller="internalTransferRequest" action="show"
                            actionParams="encodedId" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show internalTransferRequest')}"/>

        <el:dataTableAction controller="internalTransferRequest" action="edit"
                            actionParams="encodedId" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label',
                                    args: [entity], default: 'edit internalTransferRequest')}"/>

        <el:dataTableAction controller="internalTransferRequest" action="delete"
                            actionParams="encodedId" class="red icon-trash" type="confirm-delete"
                            message="${message(code: 'default.delete.label', args: [entity],
                                    default: 'delete internalTransferRequest')}"/>
    </g:else>
</el:dataTable>