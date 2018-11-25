<el:dataTable id="suspensionRequestTable"
              searchFormName="suspensionRequestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="suspensionRequest"
              spaceBefore="true" hasRow="true"
              action="filter"
              serviceName="suspensionRequest"
              domainColumns="${domainColumns}">


    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'personLiveStatus',action: 'show')}"
                            actionParams="encodedId" functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show personLiveStatus')}"/>
    </g:if>
    <g:else>



    <el:dataTableAction controller="suspensionRequest" action="show"
                        actionParams="encodedId" class="green icon-eye"
                        message="${message(code:'default.show.label',args:[entity],
                                default:'show suspensionRequest')}" />

    <el:dataTableAction controller="suspensionRequest" action="edit"
                        actionParams="encodedId" class="blue icon-pencil"
                        message="${message(code:'default.edit.label',
                                args:[entity],default:'edit suspensionRequest')}" />

    <el:dataTableAction controller="suspensionRequest" action="delete"
                        actionParams="encodedId" class="red icon-trash" type="confirm-delete"
                        message="${message(code:'default.delete.label',args:[entity],
                                default:'delete suspensionRequest')}" />
    </g:else>

</el:dataTable>