<el:dataTable id="dispatchRequestTable"
              searchFormName="dispatchRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="dispatchRequest"
              spaceBefore="true"
              hasRow="true"
              action="filter"
              serviceName="dispatchRequest"
              domainColumns="${domainColumns}">

    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'dispatchRequest', action: 'showThread')}"
                            actionParams="threadId" functionName="renderInLineShowThread" type="function" class="green icon-list"
                            message="${message(code: 'request.showThread.label', default: 'show dispatchRequest Thread')}"/>
    </g:if>

    <g:else>
        <el:dataTableAction controller="dispatchRequest" action="show"
                            actionParams="encodedId" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show dispatchRequest')}"/>

        <el:dataTableAction controller="dispatchRequest" action="edit"
                            actionParams="encodedId" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label',
                                    args: [entity], default: 'edit dispatchRequest')}"/>

        <el:dataTableAction controller="dispatchRequest" action="delete"
                            actionParams="encodedId" class="red icon-trash" type="confirm-delete"
                            message="${message(code: 'default.delete.label', args: [entity],
                                    default: 'delete dispatchRequest')}"/>
    </g:else>
</el:dataTable>