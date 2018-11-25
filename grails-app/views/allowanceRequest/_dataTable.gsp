<el:dataTable id="allowanceRequestTable"
              searchFormName="allowanceRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="allowanceRequest"
              spaceBefore="true"
              hasRow="true"
              action="filter"
              serviceName="allowanceRequest"
              domainColumns="${domainColumns}">

    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'allowanceRequest', action: 'showThread')}"
                            actionParams="threadId" functionName="renderInLineShowThread" type="function" class="green icon-list"
                            message="${message(code: 'request.showThread.label', default: 'show allowanceRequest Thread')}"/>
    </g:if>

    <g:else>
        <el:dataTableAction controller="allowanceRequest" action="show"
                            actionParams="encodedId" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show allowanceRequest')}"/>

        <el:dataTableAction controller="allowanceRequest" action="edit"
                            actionParams="encodedId" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label',
                                    args: [entity], default: 'edit allowanceRequest')}"/>

        <el:dataTableAction controller="allowanceRequest" action="delete"
                            actionParams="encodedId" class="red icon-trash" type="confirm-delete"
                            message="${message(code: 'default.delete.label', args: [entity],
                                    default: 'delete allowanceRequest')}"/>
    </g:else>
</el:dataTable>