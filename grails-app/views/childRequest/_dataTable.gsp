<el:dataTable id="childRequestTable"
              searchFormName="childRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="childRequest"
              spaceBefore="true"
              hasRow="true"
              action="filter"
              serviceName="childRequest"
              domainColumns="${domainColumns}">

    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'childRequest', action: 'showThread')}"
                            actionParams="threadId" functionName="renderInLineShowThread" type="function" class="green icon-list"
                            message="${message(code: 'request.showThread.label', default: 'show childRequest Thread')}"/>
    </g:if>

    <g:else>
        <el:dataTableAction controller="childRequest" action="show"
                            actionParams="encodedId" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show childRequest')}"/>

        <el:dataTableAction controller="childRequest" action="edit"
                            actionParams="encodedId" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label',
                                    args: [entity], default: 'edit childRequest')}"/>

        <el:dataTableAction controller="childRequest" action="delete"
                            actionParams="encodedId" class="red icon-trash" type="confirm-delete"
                            message="${message(code: 'default.delete.label', args: [entity],
                                    default: 'delete childRequest')}"/>
    </g:else>
</el:dataTable>