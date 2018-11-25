<el:dataTable id="externalTransferRequestTable" 
              searchFormName="externalTransferRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" 
              widthClass="col-sm-12" 
              controller="externalTransferRequest" 
              spaceBefore="true" hasRow="true" 
              action="filter" 
              serviceName="externalTransferRequest"
              domainColumns="${domainColumns}">

    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'externalTransferRequest', action: 'show')}"
                            actionParams="encodedId" functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show externalTransferRequest')}"/>
    </g:if>

    <g:else>
        <el:dataTableAction controller="externalTransferRequest" action="show"
                            actionParams="encodedId" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show externalTransferRequest')}"/>

        <el:dataTableAction controller="externalTransferRequest" action="edit"
                            actionParams="encodedId" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label',
                                    args: [entity], default: 'edit externalTransferRequest')}"/>

        <el:dataTableAction controller="externalTransferRequest" action="delete"
                            actionParams="encodedId" class="red icon-trash" type="confirm-delete"
                            message="${message(code: 'default.delete.label', args: [entity],
                                    default: 'delete externalTransferRequest')}"/>
    </g:else>
</el:dataTable>